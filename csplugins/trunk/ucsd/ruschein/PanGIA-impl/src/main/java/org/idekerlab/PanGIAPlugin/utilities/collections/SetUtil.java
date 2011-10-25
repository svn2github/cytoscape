package org.idekerlab.PanGIAPlugin.utilities.collections;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.idekerlab.PanGIAPlugin.utilities.files.FileIterator;

public class SetUtil {
	public static <X> void saveToFile(Set<X> toOutput, String fout) {
		try {
			PrintWriter out = new PrintWriter(new FileOutputStream(fout));
			for (X eachElement : toOutput)
				out.println(eachElement.toString());
			out.close();
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
			System.out.println("Error! Aborting program!");
			System.exit(0);
		}
	}

	public static <X> String convertToSingleLine(Set<X> a, String delimiter) {
		String output = "";
		int length = a.size();
		int i = 1;
		for (X eachElement : a) {
			if (i < length) {
				output += (eachElement.toString() + delimiter);
				i += 1;
			} else {
				output += (eachElement.toString());
				break;
			}
		}
		return (output);
	}

	public static int intersectionSize(Collection<?> a, Collection<?> b) {
		Set<?> intersection = new HashSet<Object>(a);
		intersection.retainAll(b);
		return intersection.size();
	}

	public static <T> Set<T> intersect(Collection<T> a, Collection<T> b) {
		Set<T> out = new HashSet<T>(a);
		out.retainAll(b);

		return out;
	}

	public static int intersectionSize(Set<?> a, List<?> b) {
		Set<?> intersection = new HashSet<Object>(a);
		intersection.retainAll(b);
		return intersection.size();
	}

	public static Set<String> setFromArray(String[] a) {
		Set<String> blah = new HashSet<String>(a.length);
		for (int i = 0; i < a.length; i++)
			blah.add(a[i]);

		return blah;
	}

	public static int max(Set<Integer> a) {
		int max = Integer.MIN_VALUE;

		for (Integer i : a)
			if (i > max)
				max = i;

		return max;
	}

	public static int min(Set<Integer> a) {
		int min = Integer.MAX_VALUE;

		for (Integer i : a)
			if (i < min)
				min = i;

		return min;
	}

	public static double jaccard(Set<String> set1, Set<String> set2) {
		Set<String> intersect = new HashSet<String>(set1);
		intersect.retainAll(set2);

		Set<String> union = new HashSet<String>(set1);
		union.addAll(set2);

		return intersect.size() / (double) union.size();
	}

	public static Set<String> loadString(String file) {
		Set<String> s = new HashSet<String>();
		for (String line : new FileIterator(file))
			s.add(line);

		return s;
	}

	public static Set<String> hashSetFromArray(String[] arr) {
		Set<String> out = new HashSet<String>(arr.length, 1);

		for (String s : arr)
			out.add(s);

		return out;
	}

	public static Set<Integer> hashSetFromArray(int[] arr) {
		Set<Integer> out = new HashSet<Integer>(arr.length, 1);

		for (Integer i : arr)
			out.add(i);

		return out;
	}

	public static boolean containsAny(Set<String> a, Set<String> b) {
		if (a.size() < b.size()) {
			for (String s : a)
				if (b.contains(s))
					return true;
		} else {
			for (String s : b)
				if (a.contains(s))
					return true;
		}

		return false;
	}

}
