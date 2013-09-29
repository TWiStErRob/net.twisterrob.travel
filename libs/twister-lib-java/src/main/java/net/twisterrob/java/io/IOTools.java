package net.twisterrob.java.io;

import java.io.*;
import java.nio.charset.Charset;

public final class IOTools {
	// TODO check if UTF-8 is used by cineworld
	public static final String ENCODING = Charset.forName("UTF-8").name();
	public static final String LINE_SEPARATOR = System.getProperty("line.separator");

	private IOTools() {
		// prevent instantiation
	}

	public static String readAll(Reader reader) throws IOException {
		StringBuilder sb = new StringBuilder();
		for (int c = reader.read(); c != -1; c = reader.read()) {
			sb.append((char)c);
		}
		return sb.toString();
	}
	public static String readAll(InputStream stream) throws IOException {
		return readAll(new InputStreamReader(stream, ENCODING));
	}
	public static String readAll(InputStream stream, String charsetName) throws IOException {
		return readAll(new InputStreamReader(stream, charsetName));
	}

	public static void ignorantClose(Closeable closeMe) {
		if (closeMe != null) {
			try {
				closeMe.close();
			} catch (IOException e) {
				// ignore
			}
		}
	}
}