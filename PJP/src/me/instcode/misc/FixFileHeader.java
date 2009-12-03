package me.instcode.misc;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class FixFileHeader {
	public static void fix(File inputFile, File outputFile) throws IOException {
		OutputStream outputStream = new FileOutputStream(outputFile);
		InputStream inputStream = new FileInputStream(inputFile);
		try {
			inputStream.read(new byte[656]);
			// Writing to output file
			byte[] buffer = new byte[40960];
			int size = -1;
			while ((size = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, size);
			}
		}
		finally {
			outputStream.close();
			inputStream.close();
		}
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		for (int i = 1; i <= 8; i++) {
			fix(new File("District.9.2009.R5.XviD-ViSiON.part" + i + ".rar"),
				new File("District.9.2009.R5.XviD-ViSiON.part" + i + ".rar.out")
			);
		}
	}
}
