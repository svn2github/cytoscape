package org.jmathplot.io.files;

import java.io.*;
import java.util.*;

/**
 * <p>Copyright : BSD License</p>
 * @author Yann RICHET
 * @version 3.0
 */

public class ASCIIFile
	extends DataFile {

	public ASCIIFile(File f) {
		super(f);
	}

	public static String read(File f) {
		ASCIIFile af = new ASCIIFile(f);
		return af.read();
	}

	public static String[] readLines(File f) {
		ASCIIFile af = new ASCIIFile(f);
		return af.readLines();
	}

	public static String readLine(File f, int i) {
		ASCIIFile af = new ASCIIFile(f);
		return af.readLine(i);
	}

	public static void write(File f, String t) {
		ASCIIFile af = new ASCIIFile(f);
		af.write(t, false);
	}

	public static void append(File f, String t) {
		ASCIIFile af = new ASCIIFile(f);
		af.write(t, true);
	}

	/**Read an ASCII File
	 * @return String
	 */
	public String read() {
		StringBuffer text = new StringBuffer((int)file.length());
		try {
			FileReader fr = new FileReader(file);
			BufferedReader b = new BufferedReader(fr);
			boolean eof = false;
			String line;
			String ret = "\n";
			while (!eof) {
				line = b.readLine();
				if (line == null) {
					eof = true;
				} else {
					text.append(line);
					text.append(ret);
				}
			}
			b.close();
		} catch (IOException e) {
			throw new IllegalArgumentException("File " + file.getName() +
				" is unreadable : " + e.toString());
		}
		return text.toString();
	}

	/**Read lines of an ASCII File
	 * @return an Array of String
	 */
	public String[] readLines() {
		Vector linesVector = new Vector(); ;
		try {
			FileReader fr = new FileReader(file);
			BufferedReader b = new BufferedReader(fr);
			boolean eof = false;
			while (!eof) {
				String line = b.readLine();
				if (line == null) {
					eof = true;
				} else {
					linesVector.add(line);
				}
			}
			b.close();
		} catch (IOException e) {
			throw new IllegalArgumentException("File " + file.getName() +
				" is unreadable : " + e.toString());
		}
		String[] lines = new String[linesVector.size()];
		for (int i = 0; i < lines.length; i++) {
			lines[i] = (String) (linesVector.get(i));
		}
		return lines;
	}

	/**Read only one line in an ASCII File
	 * @param i line index
	 * @return String
	 */
	public String readLine(int i) {
		String line = new String("");
		try {
			FileReader fr = new FileReader(file);
			BufferedReader b = new BufferedReader(fr);
			boolean eof = false;
			for (int j = 0; j < i; j++) {
				if (eof) {
					throw new IllegalArgumentException("Line " + i +
						" is not found in the file " +
						file.getName() + ".");
				}
				b.readLine();
				if (line == null) {
					eof = true;
				}
			}
			line = b.readLine();
			b.close();
		} catch (IOException e) {
			throw new IllegalArgumentException("File " + file.getName() +
				" is unreadable : " + e.toString());
		}
		return line;
	}

	/**Write a text in an ASCII File
	 * @param text String
	 * @param append boolean
	 */
	public void write(String text, boolean append) {
		if (file.exists()) {
			System.out.println("Warning : the file " + file.getName() +
				" already exists !");
		}
		try {
			FileWriter fw = new FileWriter(file, append);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(text);
			bw.close();
		} catch (IOException e) {
			throw new IllegalArgumentException("File " + file.getName() +
				" is unwritable : " + e.toString());
		}
	}

	public static void main(String[] args) {
		File f = new File("read.txt");
		System.out.println(ASCIIFile.read(f));

		String[] lines = ASCIIFile.readLines(f);
		for (int i = 0; i < lines.length; i++) {
			System.out.println("line " + i + " : " + lines[i]);
		}

		System.out.println(ASCIIFile.readLine(f, 0));
		System.out.println(ASCIIFile.readLine(f, lines.length - 1));

		ASCIIFile.append(new File("write.txt"),
			Calendar.getInstance().getTime().toString());
	}
}