package me.instcode.gis.data;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

public class Etopo {

	public static interface EtopoPercentageListener {
		public void update(int number);
	}

	private static Etopo instance = new Etopo(new File("etopo2.bin"));
	
	private File etopo2;

	public static Etopo getInstance() {
		return instance;
	}
	
	private Etopo(File etopo2) {
		// Singleton
		this.etopo2 = etopo2;
	}
	
	public static Rectangle2D createDomain(double minLon, double maxLon, double minLat, double maxLat) {
		Rectangle domain = new Rectangle();
		domain.setRect(minLon, minLat, maxLon - minLon, maxLat - minLat);
		return domain;
	}
	
	/**
	 * Export highness data from the given domain to the output bathymetry file
	 * @param outputFile
	 * @param domain
	 * @return
	 * @throws IOException
	 */
	public Dimension export(File outputFile, Rectangle2D domain) throws IOException {
		return export(outputFile, domain, null);
	}
	
	/**
	 * Export all data in the given region in Etopo file
	 * to the given output file. 
	 * 
	 * @param outputFile
	 * @param domain
	 * 
	 * @return width & height of the grid.
	 * @throws IOException
	 */
	public Dimension export(File outputFile, Rectangle2D domain, EtopoPercentageListener listener) throws IOException {
		BufferedWriter writer = null;
		RandomAccessFile etopo = null;
		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;
		try {
			// Read data from etopo2
			etopo = new RandomAccessFile(etopo2, "r");
			etopo.read(new byte[8]);
			int dimensions[] = new int[2];
			dimensions[0] = etopo.readInt();
			dimensions[1] = etopo.readInt();
			double lons[] = new double[2];
			lons[0] = etopo.readDouble();
			lons[1] = etopo.readDouble();
			double lats[] = new double[2];
			lats[0] = etopo.readDouble();
			lats[1] = etopo.readDouble();
			int heights[] = new int [2];
			heights[0] = etopo.readInt();
			heights[1] = etopo.readInt();
			
			int dx = (int)Math.abs(domain.getWidth() * dimensions[0] / (lons[1] - lons[0])) + 1;
			int dy = (int)Math.abs(domain.getHeight() * dimensions[1] / (lats[1] - lats[0])) + 1;
			// Writing to output file
			writer = new BufferedWriter(new FileWriter(outputFile));
			writer.write("DSAA\n");
			writer.write(dx + " " + dy + "\n");
			writer.write(domain.getMinX() + " " + domain.getMaxX() + "\n");
			writer.write(domain.getMinY() + " " + domain.getMaxY() + "\n");
			// Prepare enough room for later writing the min-max values.
			writer.write("              \n");
			long x = (long)Math.abs((domain.getMinX() - lons[0]) * dimensions[0] / (lons[1] - lons[0]));
			long y = (long)Math.abs((domain.getMinY() - lats[0]) * dimensions[1] / (lats[1] - lats[0]));
			ByteBuffer buffer = ByteBuffer.allocate(dx * 4);
			etopo.seek(etopo.getFilePointer() + (y * dimensions[0] + x) * 4L);
			for (int i = 0; i < dy; i++) {
				// Read all *row* values in one trip...
				etopo.getChannel().read(buffer);
				buffer.position(0);
				for (int j = 1; j <= dx; j++) {
					int value = buffer.getInt();
					min = Math.min(min, value);
					max = Math.max(max, value);
					if (j % 10 == 0) {
						writer.write(value + "\n");
					}
					else {
						writer.write(value + " ");
					}
				}
				writer.write("\r\n");
				etopo.seek(etopo.getFilePointer() + (dimensions[0] - dx) * 4L);
				if (listener != null) {
					listener.update(i * 100 / dy);
				}
				buffer.rewind();
			}
			return new Dimension(dx, dy);
		}
		finally {
			if (writer != null) {
				writer.close();
				
				RandomAccessFile output = new RandomAccessFile(outputFile, "rw");
				// Jump over DSSA
				output.readLine();
				// Jump over row x col size
				output.readLine();
				// Jump over min-max longitudes
				output.readLine();
				// Jum over min-max latitudes
				output.readLine();
				// Stop. Put the min-max values
				output.write((min + " " + max).getBytes());
				output.close();
			}
		}
	}
	
	/**
	 * Convert the original Etopo2.grd in ASCII format
	 * to BINARY format.
	 * 
	 * @param output
	 * @throws IOException
	 */
	public void export(File input, File output) throws IOException {
		BufferedReader reader = null;
		DataOutputStream outputStream = null;
		try {
			reader = new BufferedReader(new FileReader(input));
			outputStream = new DataOutputStream(new FileOutputStream(output));
			// (1) string "DSAA" or "DSBB" (in ASC and BIN GRD files,
			// respectively)
			reader.readLine();
			outputStream.writeChars("DSBB");
			// (2) nx ny - array dimensions as "short"s (in ASC format,
			// separated by a white space)
			String[] dimensions = reader.readLine().split(" ");
			outputStream.writeInt(Integer.parseInt(dimensions[0]));
			outputStream.writeInt(Integer.parseInt(dimensions[1]));
			// (3) xlo xhi - lower and higher X boundaries as "double"s (in ASC
			// format, separated by a white space)
			String[] lons = reader.readLine().split(" ");
			outputStream.writeDouble(Double.parseDouble(lons[0]));
			outputStream.writeDouble(Double.parseDouble(lons[1]));
			// (4) ylo yhi - lower and higher Y boundaries as "double"s (in ASC
			// format, separated by a white space)
			String[] lats = reader.readLine().split(" ");
			outputStream.writeDouble(Double.parseDouble(lats[0]));
			outputStream.writeDouble(Double.parseDouble(lats[1]));
			// (5) zlo zhi - minimum and maximum array value as "double"s (in
			// ASC format, separated by a white space)
			String[] heights = reader.readLine().split(" ");
			outputStream.writeInt(Integer.parseInt(heights[0]));
			outputStream.writeInt(Integer.parseInt(heights[1]));
			// (6) u[i][j] - array values as "float"s (in ASC format, separated
			// by a white space), j index changes most rapidly
			String line;
			while ((line = reader.readLine()) != null) {
				String[] values = line.split(" ");
				for (String value : values) {
					if (value.trim().length() > 0) {
						outputStream.writeInt(Integer.parseInt(value));
					}
				}
			}
		}
		finally {
			if (reader != null) {
				reader.close();
			}
			if (outputStream != null) {
				outputStream.close();
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		Etopo etopo = new Etopo(new File("./data/etopo2.grd"));
		etopo.export(new File("./data/etopo2.grd"), new File("./data/etopo2.bin"));
		/*Etopo etopo = new Etopo(new File("./data/etopo2.bin"));
		Rectangle2D domain = createDomain(79.674043207089, -7.3680308607278, 107.7099722366, 23.037598761889);
		etopo.export(new File("./data/region.grd"), domain);
		System.out.println("Done.");*/
	}
}
