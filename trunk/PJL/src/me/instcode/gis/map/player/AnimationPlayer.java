package me.instcode.gis.map.player;

import java.awt.Image;

/**
 * A virtual player to play pictures.
 */
public class AnimationPlayer implements Runnable {
	public static final int PLAYER_STOPPED_EVENT = 0;
	public static final int PLAYER_START_PLAYING_EVENT = 1;
	public static final int PLAYER_PAUSED_EVENT = 3;
	
	private boolean isPlaying = false;
	private long interval = 1000;
	private int currentFrame = 0;
	
	private AnimationListener listener;
	private Source source;
	private Screen screen;
	private Thread animationThread; 
	private String name;

	public AnimationPlayer(String name, AnimationListener listener, Source source, Screen screen) {
		this.name = name;
		this.listener = listener;
		this.source = source;
		this.screen = screen;
	}
	
	/**
	 * Set player speed by change the delay between 2 frames
	 * 
	 * @param milliseconds	Delay time in milliseconds
	 */
	public void setDelay(long milliseconds) {
		interval = milliseconds;
	}

	/**
	 * Check whether the player is playing or not
	 * 
	 * @return	true if the play is playing.
	 */
	public boolean isPlaying() {
		return isPlaying;
	}

	/**
	 * Pause current playing
	 */
	public void pause() {
		if (!isPlaying) {
			listener.notifyEvent(PLAYER_STOPPED_EVENT);
			return;
		}
		isPlaying = false;
		if (animationThread != null) {
			if (animationThread.isAlive()) {
				animationThread.interrupt(); 
			}
			animationThread = null;
		}
	}
	
	/**
	 * Start playing.
	 */
	public void play() {
		if (isPlaying || animationThread != null) {
			return;
		}
		isPlaying = true;
		animationThread = new Thread(this, name);
		animationThread.start();
	}

	/**
	 * Stop playing.
	 */
	public void stop() {
		currentFrame = 0;
		pause();
	}
	
	/**
	 * Jump to specific position.
	 * @param position	Position to be moved to.
	 */
	public void seek(int position) {
		currentFrame = position;
	}
	
	/**
	 * Restart to the beginning
	 */
	public void rewind() {
		currentFrame = 0;
	}
	
	/**
	 * Move 1 step forward in frame chain.
	 */
	public void stepForward() {
		if (currentFrame < source.getLength() - 1) {
			currentFrame++;
		}
		renderFrame();
	}

	/**
	 * Move 1 step backward in frame chain 
	 */
	public void stepBackward() {
		if (currentFrame > 0) {
			currentFrame--;
		}
		renderFrame();
	}

	/**
	 * Draw the current frame onto the screen
	 *  
	 * @return
	 */
	private boolean renderFrame() {
		Image frame = source.getFrame(currentFrame);
		if (frame == null) {
			return false;
		}
		screen.setRenderedImage(frame);
		return true;
	}

	private void nextFrame() {
		currentFrame++;
		if (currentFrame == source.getLength()) {
			isPlaying = false;
			currentFrame = 0;
			animationThread = null;
			listener.notifyEvent(PLAYER_STOPPED_EVENT);
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		listener.notifyEvent(PLAYER_START_PLAYING_EVENT);
		while (isPlaying) {
			if (renderFrame()) {
				nextFrame();
			}
			try {
				Thread.sleep(interval);
			}
			catch (InterruptedException e) {
			}
		}
		if (currentFrame == 0) {
			listener.notifyEvent(PLAYER_STOPPED_EVENT);
		}
		else {
			listener.notifyEvent(PLAYER_PAUSED_EVENT);
		}
		isPlaying = false;
	}

	/**
	 * Get the length of the source in number of frames.
	 * 
	 * @return
	 */
	public int getLength() {
		return source.getLength();
	}
}
