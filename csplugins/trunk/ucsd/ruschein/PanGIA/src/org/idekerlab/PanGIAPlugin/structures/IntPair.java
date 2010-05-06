package org.idekerlab.PanGIAPlugin.structures;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.idekerlab.PanGIAPlugin.utilities.files.FileIterator;
import org.idekerlab.PanGIAPlugin.utilities.files.FileUtil;

public class IntPair {

	public final int n1;
	public final int n2;

	public IntPair(int n1, int n2) {
		this.n1 = n1;
		this.n2 = n2;
	}

	public int hashCode() {
		return n1 + n2;
	}

	public boolean equals(Object p) {
		if (p == null)
			return false;
		if (p instanceof IntPair) {
			IntPair other = (IntPair) p;
			if ((other.n1 == this.n1 && other.n2 == this.n2)
					|| (other.n1 == this.n2 && other.n2 == this.n1))
				return true;
			else
				return false;
		} else
			return false;
	}

	public String toString() {
		return n1 + "-" + n2;
	}

	public String toStringInOrder() {
		return (n1 < n2 ? this.toString() : n2 + "-" + n1);
	}

	public int size() {
		return n2 - n1 + 1;
	}

	public static List<IntPair> loadList(String file) {
		List<IntPair> out = new ArrayList<IntPair>(FileUtil.countLines(file));

		for (String line : new FileIterator(file)) {
			String[] cols = line.split("\t");
			out.add(new IntPair(Integer.valueOf(cols[0]), Integer
					.valueOf(cols[1])));
		}

		return out;
	}

	public static void saveList(Collection<IntPair> pairs, String file) {
		BufferedWriter bw = FileUtil.getBufferedWriter(file, false);

		try {
			for (IntPair ip : pairs)
				bw.write(ip.n1 + "\t" + ip.n2 + "\n");

			bw.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}
