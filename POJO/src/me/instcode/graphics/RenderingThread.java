package me.instcode.graphics;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class RenderingThread {
	private final Queue<Runnable> tasks = new LinkedBlockingQueue<Runnable>();
	private final Executor executor = Executors.newSingleThreadExecutor();
	private Runnable active;
	
	/**
	 * Use for checking if the cache is expired or not
	 */
	private Map<File, Long> cacheModifiedDate = new ConcurrentHashMap<File, Long>();
	
	/**
	 * Cache in external memory
	 */
	private Map<File, File> externalCache = new ConcurrentHashMap<File, File>();
	
	/**
	 * Cache in primary memory
	 */
	private Map<File, Image> memoryCache = new ConcurrentHashMap<File, Image>();
	
	/**
	 * To be swapped out list (FIFO)
	 */
	private Queue<File> beSwappedOut = new ConcurrentLinkedQueue<File>();
	
	private Map<File, Boolean> renderingQueue = new ConcurrentHashMap<File, Boolean>();
	
	private static RenderingThread instance = new RenderingThread();
	
	private RenderingThread() {
		// Singleton
	}
	
	public static RenderingThread getInstance() {
		return instance;
	}
	
    /**
     * This is non blocking call. It queues the given renderer
     * and schedules for rendering. If the rendering result has
     * been existed (cached in memory or on disk), the method will
     * returns that rendered image immediately without queuing
     * the renderer.
     * 
     * Developer have to use {@link #check(File)} to
     * retrieve the image when ready.
     */
    public synchronized void render(final Renderer renderer) {
		final File file = renderer.getFile();
		if (renderingQueue.containsKey(file) || !file.canWrite()) {
			return;
		}
		renderingQueue.put(file, Boolean.TRUE);
		tasks.offer(new Runnable() {
			public void run() {
				try {
					while (!isEnoughMemory()) {
						if (beSwappedOut.size() == 0) {
							break;
						}
						swapOut();
					}
					if (renderer.render()) {
						// Cache the results...
						Image renderedFrame = renderer.renderedFrame();
						cacheRendering(file, renderedFrame);
					}
					renderingQueue.remove(file);
				}
				finally {
					scheduleNext();
				}
			}
		});
		if (active == null) {
			scheduleNext();
		}
	}

    /**
     * Lookup and return rendered image..
     * 
     * @param file
     * 		The file for retrieving its rendered image.
     * @return
     * 		An image not null if cache hits, 'null' if cache misses.
     */
    public Image getRenderedImage(File file) {
   		Image image = check(file);
   		if (!memoryCache.containsKey(file)) {
	    	if (!isEnoughMemory()) {
				swapOut();
			}
    		swapIn(file, image);
    	}
   		return image;
    }
	/**
	 * Check if the image is cached in memory,
	 * if not, try to load it from external file.
	 */
	public Image check(File file) {
		Image image = memoryCache.get(file);
    	if (image == null) {
    		File imageFile = externalCache.get(file);
    		if (imageFile != null && imageFile.exists()) {
				try {
					image = loadImage(new FileInputStream(imageFile));
				}
				catch (IOException e) {
					// The file exists but its content is now
        			// obsolete, means we have to render it again.
        			// The external cache thus doesn't have to
        			// link to it anymore.
					e.printStackTrace();
    				externalCache.remove(file);
				}
    		}
    	}
    	// Check if the cache is expired?
    	if (image != null) {
			Long modifiedDate = new Long(file.lastModified());
			Long lastModifiedDate = cacheModifiedDate.get(file);
			if (modifiedDate.compareTo(lastModifiedDate) != 0) {
				// Out of date, removing...
				memoryCache.remove(file);
				externalCache.remove(file);
				image = null;
			}
    	}
   		return image;
	}
	
	public void purge() {
		renderingQueue.clear();
		tasks.clear();
	}
	
	/**
	 * Check if the remaining memory is enough for storing
	 * the given image, if not, swap the oldest image out,
	 * to a file on external memory, then replace it with
	 * the image in memory.
	 */
	private void cacheRendering(File file, Image renderedFrame) {
		if (renderedFrame != null) {
			beSwappedOut.add(file);
			cacheModifiedDate.put(file, new Long(file.lastModified()));
			memoryCache.put(file, renderedFrame);
		}
	}

	private void swapIn(File file, Image image) {
    	if (image != null) {
			beSwappedOut.add(file);
			memoryCache.put(file, image);
    	}
	}
	
	private void swapOut() {
		File cachedFile = beSwappedOut.poll();
		if (cachedFile == null) {
			return;
		}
		try {
			if (!externalCache.containsKey(cachedFile) && memoryCache.containsKey(cachedFile)) {
				BufferedImage image = (BufferedImage) memoryCache.remove(cachedFile);
				File imageFile = File.createTempFile("tsui-", ".gif");
				imageFile.deleteOnExit();
				ImageIO.write(image, "gif", imageFile);
				externalCache.put(cachedFile, imageFile);
			}
		}
		catch (IOException e) {
		}
		System.gc();
	}
	
	private boolean isEnoughMemory() {
		long free = Runtime.getRuntime().freeMemory();
		return (free > 40000000);
	}
    
	private synchronized void scheduleNext() {
		if ((active = tasks.poll()) != null) {
			executor.execute(active);
		}
	}
	
	private static Image loadImage(InputStream inputStream) throws IOException {
		byte[] imageData = readStream(inputStream);
		ImageIcon icon = new ImageIcon(imageData);
		Image image = icon.getImage();
		return image;
	}
	
	private static byte[] readStream(InputStream inputStream) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		int tempSize = 1024;
		byte[] temp = new byte[tempSize];
		int length = inputStream.read(temp);
		while (length != -1) {
			outputStream.write(temp, 0, length);
			length = inputStream.read(temp);
		}
		return outputStream.toByteArray();
	}
}
