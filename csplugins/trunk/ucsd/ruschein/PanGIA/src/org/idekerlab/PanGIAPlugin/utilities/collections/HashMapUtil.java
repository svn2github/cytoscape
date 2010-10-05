package org.idekerlab.PanGIAPlugin.utilities.collections;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.idekerlab.PanGIAPlugin.structures.IntPair;
import org.idekerlab.PanGIAPlugin.utilities.files.FileIterator;
import org.idekerlab.PanGIAPlugin.data.DoubleVector;

public class HashMapUtil {

	public static <T1, T2> void saveMap(Map<T1, T2> hm, String filename) {
		Set<T1> keys = hm.keySet();
		try {
			FileOutputStream fsout = new FileOutputStream(filename);
			PrintWriter out1 = new PrintWriter(fsout);

			Iterator<T1> eachKey = keys.iterator();
			while (eachKey.hasNext()) {
				T1 nextKey = eachKey.next();
				T2 val = hm.get(nextKey);

				out1.print(nextKey + "\t" + val + "\n");
			}

			out1.close();
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
			System.out.println("Error! Aborting program!");
			System.exit(0);
		}
	}

	public static void saveMapSS(Map<String, Set<String>> hm, String filename) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
			
			for (String key : hm.keySet()) {
				bw.write(key + "\t");

				Set<String> vals = hm.get(key);

				boolean first = true;
				for (String val : vals)
					if (!first)
						bw.write("|" + val);
					else {
						first = false;
						bw.write(val);
					}

				bw.write("\n");
			}

			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	public static void saveMapSSetI(Map<String, Set<Integer>> hm,
			String filename) {
		Set<String> keys = hm.keySet();
		try {
			FileOutputStream fsout = new FileOutputStream(filename);
			PrintWriter out1 = new PrintWriter(fsout);

			for (String key : keys) {
				out1.print(key + "\t");

				Set<Integer> vals = hm.get(key);

				boolean first = true;
				for (Integer val : vals)
					if (!first)
						out1.print("|" + val);
					else {
						first = false;
						out1.print(val);
					}

				out1.print("\n");
			}

			out1.close();
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
			System.out.println("Error! Aborting program!");
			System.exit(0);
		}
	}

	public static void saveMapII(Map<Integer, Set<Integer>> hm, String filename) {
		Set<Integer> keys = hm.keySet();
		try {
			FileOutputStream fsout = new FileOutputStream(filename);
			PrintWriter out1 = new PrintWriter(fsout);

			for (Integer key : keys) {
				out1.print(key + "\t");

				Set<Integer> vals = hm.get(key);

				boolean first = true;
				for (Integer val : vals)
					if (!first)
						out1.print("|" + val);
					else {
						first = false;
						out1.print(val);
					}

				out1.print("\n");
			}

			out1.close();
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
			System.out.println("Error! Aborting program!");
			System.exit(0);
		}
	}

	public static void saveMapSISS(Map<Set<Integer>, Set<String>> hm,
			String filename, String delim1, String delim2) {
		Set<Set<Integer>> keys = hm.keySet();
		try {
			FileOutputStream fsout = new FileOutputStream(filename);
			PrintWriter out1 = new PrintWriter(fsout);

			for (Set<Integer> key : keys) {
				Iterator<Integer> ii = key.iterator();
				out1.print(ii.next());
				while (ii.hasNext())
					out1.print(delim1 + ii.next());

				out1.print("\t");

				Set<String> vals = hm.get(key);

				Iterator<String> si = vals.iterator();
				out1.print(si.next());
				while (si.hasNext())
					out1.print(delim2 + si.next());

				out1.print("\n");
			}

			out1.close();
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
			System.out.println("Error! Aborting program!");
			System.exit(0);
		}
	}

	public static void saveMapSISS(Map<Set<Integer>, Set<String>> hm,
			String filename) {
		saveMapSISS(hm, filename, "|", "|");
	}

	public static void saveMapSSIS(Map<Set<String>, Set<Integer>> hm,
			String filename) {
		Set<Set<String>> keys = hm.keySet();
		try {
			FileOutputStream fsout = new FileOutputStream(filename);
			PrintWriter out1 = new PrintWriter(fsout);

			for (Set<String> key : keys) {
				Iterator<String> ii = key.iterator();
				out1.print(ii.next());
				while (ii.hasNext())
					out1.print("|" + ii.next());

				out1.print("\t");

				Set<Integer> vals = hm.get(key);

				Iterator<Integer> si = vals.iterator();
				out1.print(si.next());
				while (si.hasNext())
					out1.print("|" + si.next());

				out1.print("\n");
			}

			out1.close();
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
			System.out.println("Error! Aborting program!");
			System.exit(0);
		}
	}

	public static void saveMapSL(Map<String, List<String>> hm, String filename) {
		Set<String> keys = hm.keySet();
		try {
			FileOutputStream fsout = new FileOutputStream(filename);
			PrintWriter out1 = new PrintWriter(fsout);

			Iterator<String> eachKey = keys.iterator();
			while (eachKey.hasNext()) {
				String nextKey = eachKey.next();
				Collection<String> val = hm.get(nextKey);

				String valString = val.toString();
				valString = valString.replace(" ", "");
				valString = valString.substring(1, valString.length() - 1);

				out1.print(nextKey + "\t" + valString + "\n");
			}

			out1.close();
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
			System.out.println("Error! Aborting program!");
			System.exit(0);
		}
	}

	public static <X> void saveMapXL(Map<X, List<String>> hm, String filename) {
		Set<X> keys = hm.keySet();
		try {
			FileOutputStream fsout = new FileOutputStream(filename);
			PrintWriter out1 = new PrintWriter(fsout);

			Iterator<X> eachKey = keys.iterator();
			while (eachKey.hasNext()) {
				X nextKey = eachKey.next();
				Collection<String> val = hm.get(nextKey);

				String valString = val.toString();
				valString = valString.replace(" ", "");
				valString = valString.substring(1, valString.length() - 1);

				out1.print(nextKey + "\t" + valString + "\n");
			}

			out1.close();
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
			System.out.println("Error! Aborting program!");
			System.exit(0);
		}
	}

	public static <X> void saveMapXS(Map<X, Set<String>> hm, String filename,
			String setDelimiter) {
		try {
			FileOutputStream fsout = new FileOutputStream(filename);
			PrintWriter out1 = new PrintWriter(fsout);

			for (X eachKey : hm.keySet())
				out1.println(eachKey.toString()
						+ "\t"
						+ SetUtil.convertToSingleLine(hm.get(eachKey),
								setDelimiter));

			out1.close();
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
			System.out.println("Error! Aborting program!");
			System.exit(0);
		}
	}

	public static <X, Y> void saveMapXY(Map<X, Set<Y>> hm, String filename,
			String setDelimiter) {
		try {
			FileOutputStream fsout = new FileOutputStream(filename);
			PrintWriter out1 = new PrintWriter(fsout);

			for (X eachKey : hm.keySet())
				out1.println(eachKey.toString()
						+ "\t"
						+ SetUtil.convertToSingleLine(hm.get(eachKey),
								setDelimiter));

			out1.close();
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
			System.out.println("Error! Aborting program!");
			System.exit(0);
		}
	}

	public static <X> void saveMapSI(Map<String, Integer> hm, String filename) {
		try {
			FileOutputStream fsout = new FileOutputStream(filename);
			PrintWriter out1 = new PrintWriter(fsout);

			for (String eachKey : hm.keySet())
				out1.println(eachKey.toString() + "\t" + hm.get(eachKey));

			out1.close();
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
			System.out.println("Error! Aborting program!");
			System.exit(0);
		}
	}

	public static Map<String, List<String>> loadSS(String filename) {
		return loadSSfun(filename, 0, 1);
	}

	public static Map<String, List<String>> loadSS(String filename, int keycol,
			int valcol) {
		return loadSSfun(filename, keycol, valcol);
	}

	private static Map<String, List<String>> loadSSfun(String filename,
			int keycol, int valcol) {
		Map<String, List<String>> map = new HashMap<String, List<String>>();

		FileReader fr = null;
		try {
			fr = new FileReader(filename);
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
			System.exit(0);
		}

		BufferedReader br = new BufferedReader(fr);

		String line = "";
		do {
			try {
				line = br.readLine();
			} catch (IOException e) {
				System.out.println(e.getMessage());
				System.exit(0);
			}

			if (line != null) {
				String[] cols = line.split("\t");

				String[] keys = cols[keycol].split("\\|");
				String[] entries = cols[valcol].split("\\|");

				List<String> vals = new ArrayList<String>(entries.length);
				for (int a = 0; a < entries.length; a++)
					vals.add(entries[a]);

				for (int a = 0; a < keys.length; a++) {
					if (map.containsKey(keys[a]))
						map.get(keys[a]).addAll(vals);
					else
						map.put(keys[a], vals);
				}
			}
		} while (line != null);

		try {
			br.close();
			fr.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			System.exit(0);
		}

		return map;
	}

	public static Map<String, String> loadSS1(String filename, int keyCol,
			int valCol) {
		Map<String, String> map = new HashMap<String, String>();

		for (String line : new FileIterator(filename)) {
			String[] cols = line.split("\t");

			map.put(cols[keyCol], cols[valCol]);
		}

		return map;
	}

	public static Map<Integer, String> loadIS1(String filename, int keyCol,
			int valCol) {
		Map<Integer, String> map = new HashMap<Integer, String>();

		for (String line : new FileIterator(filename)) {
			String[] cols = line.split("\t");

			map.put(Integer.valueOf(cols[keyCol]), cols[valCol]);
		}

		return map;
	}

	public static Map<String, Integer> loadSI1(String filename, int keyCol,
			int valCol) {
		Map<String, Integer> map = new HashMap<String, Integer>();

		for (String line : new FileIterator(filename)) {
			String[] cols = line.split("\t");

			map.put(cols[keyCol], Integer.valueOf(cols[valCol]));
		}

		return map;
	}

	public static Map<Integer, Integer> loadII1(String filename, int keyCol,
			int valCol) {
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();

		for (String line : new FileIterator(filename)) {
			String[] cols = line.split("\t");

			map.put(Integer.valueOf(cols[keyCol]), Integer
					.valueOf(cols[valCol]));
		}

		return map;
	}

	public static Map<String, Double> loadSD1(String filename, int keyCol,
			int valCol) {
		Map<String, Double> map = new HashMap<String, Double>();

		for (String line : new FileIterator(filename)) {
			String[] cols = line.split("\t");

			map.put(cols[keyCol], Double.valueOf(cols[valCol]));
		}

		return map;
	}

	public static Map<String, IntPair> loadSIP1(String filename, int keyCol,
			int valCol1, int valCol2) {
		Map<String, IntPair> map = new HashMap<String, IntPair>();

		for (String line : new FileIterator(filename)) {
			String[] cols = line.split("\t");

			map.put(cols[keyCol], new IntPair(Integer.valueOf(cols[valCol1]),
					Integer.valueOf(cols[valCol2])));
		}

		return map;
	}

	public static Map<String, Set<String>> loadSS_Set(String filename) {
		return loadSSfun_Set(filename, "|", 0, 1);
	}

	public static Map<String, Set<String>> loadSS_Set(String filename,
			String delim) {
		return loadSSfun_Set(filename, delim, 0, 1);
	}

	public static Map<String, Set<String>> loadSS_Set(String filename,
			int keycol, int valcol) {
		return loadSSfun_Set(filename, "|", keycol, valcol);
	}

	private static Map<String, Set<String>> loadSSfun_Set(String filename,
			String delim, int keycol, int valcol) {
		if (delim.equals("|"))
			delim = "\\|";

		Map<String, Set<String>> map = new HashMap<String, Set<String>>();

		FileReader fr = null;
		try {
			fr = new FileReader(filename);
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
			System.exit(0);
		}

		BufferedReader br = new BufferedReader(fr);

		String line = "";
		do {
			try {
				line = br.readLine();
			} catch (IOException e) {
				System.out.println(e.getMessage());
				System.exit(0);
			}

			if (line != null) {
				String[] cols = line.split("\t");

				if (cols.length < 2)
					continue;

				String[] keys = cols[keycol].split(delim);
				String[] entries = cols[valcol].split(delim);

				Set<String> vals = new HashSet<String>(entries.length);
				for (int a = 0; a < entries.length; a++)
					vals.add(entries[a]);

				for (int a = 0; a < keys.length; a++) {
					if (map.containsKey(keys[a])) {
						Set<String> oldVal = new HashSet<String>(map
								.get(keys[a]));
						oldVal.addAll(vals);
						map.put(keys[a], oldVal);
					} else
						map.put(keys[a], vals);
				}
			}
		} while (line != null);

		return map;
	}

	public static Map<String, Set<Integer>> loadSI_Set(String filename) {
		return loadSIfun_Set(filename, "|", 0, 1);
	}

	public static Map<String, Set<Integer>> loadSI_Set(String filename,
			String delim) {
		return loadSIfun_Set(filename, delim, 0, 1);
	}

	public static Map<String, Set<Integer>> loadSI_Set(String filename,
			int keycol, int valcol) {
		return loadSIfun_Set(filename, "|", keycol, valcol);
	}

	private static Map<String, Set<Integer>> loadSIfun_Set(String filename,
			String delim, int keycol, int valcol) {
		if (delim.equals("|"))
			delim = "\\|";

		Map<String, Set<Integer>> map = new HashMap<String, Set<Integer>>();

		for (String line : new FileIterator(filename)) {
			String[] cols = line.split("\t");

			if (cols.length < 2)
				continue;

			String[] keys = cols[keycol].split(delim);
			String[] entries = cols[valcol].split(delim);

			Set<Integer> vals = new HashSet<Integer>(entries.length);
			for (int a = 0; a < entries.length; a++)
				vals.add(Integer.valueOf(entries[a]));

			for (int a = 0; a < keys.length; a++) {
				if (map.containsKey(keys[a])) {
					Set<Integer> oldVal = new HashSet<Integer>(map.get(keys[a]));
					oldVal.addAll(vals);
					map.put(keys[a], oldVal);
				} else
					map.put(keys[a], vals);
			}
		}

		return map;
	}

	public static Map<Integer, Set<String>> loadIS_Set(String filename) {
		return loadISfun_Set(filename, "|", 0, 1);
	}

	public static Map<Integer, Set<String>> loadIS_Set(String filename,
			int keycol, int valcol) {
		return loadISfun_Set(filename, "|", keycol, valcol);
	}

	private static Map<Integer, Set<String>> loadISfun_Set(String filename,
			String delim, int keycol, int valcol) {
		if (delim.equals("|"))
			delim = "\\|";

		Map<Integer, Set<String>> map = new HashMap<Integer, Set<String>>();

		for (String line : new FileIterator(filename)) {
			String[] cols = line.split("\t");

			if (cols.length < 2)
				continue;

			String[] keys = cols[keycol].split(delim);
			String[] entries = cols[valcol].split(delim);

			Set<String> vals = new HashSet<String>(entries.length);
			for (int a = 0; a < entries.length; a++)
				vals.add(entries[a]);

			for (int a = 0; a < keys.length; a++) {
				Integer key = Integer.valueOf(keys[a]);

				Set<String> ss = map.get(key);
				if (ss == null)
					map.put(key, vals);
				else
					ss.addAll(vals);
			}
		}

		return map;
	}

	public static Map<Integer, Set<Integer>> loadII_Set(String filename) {
		return loadIIfun_Set(filename, "|", 0, 1);
	}

	public static Map<Integer, Set<Integer>> loadII_Set(String filename,
			int keycol, int valcol) {
		return loadIIfun_Set(filename, "|", keycol, valcol);
	}

	private static Map<Integer, Set<Integer>> loadIIfun_Set(String filename,
			String delim, int keycol, int valcol) {
		if (delim.equals("|"))
			delim = "\\|";

		Map<Integer, Set<Integer>> map = new HashMap<Integer, Set<Integer>>();

		for (String line : new FileIterator(filename)) {
			String[] cols = line.split("\t");

			if (cols.length < 2)
				continue;

			String[] keys = cols[keycol].split(delim);
			String[] entries = cols[valcol].split(delim);

			Set<Integer> vals = new HashSet<Integer>(entries.length);
			for (int a = 0; a < entries.length; a++)
				vals.add(Integer.valueOf(entries[a]));

			for (int a = 0; a < keys.length; a++) {
				Integer key = Integer.valueOf(keys[a]);

				Set<Integer> ss = map.get(key);
				if (ss == null)
					map.put(key, vals);
				else
					ss.addAll(vals);
			}
		}

		return map;
	}

	public static Map<String, String> reverseMapSS(Map<String, String> hm) {
		Map<String, String> outMap = new HashMap<String, String>(hm.size());

		for (String key : hm.keySet())
			outMap.put(hm.get(key), key);

		return outMap;
	}

	public static Map<String, Set<String>> reverseMapSS_Set(
			Map<String, Set<String>> hm) {
		Map<String, Set<String>> outMap = new HashMap<String, Set<String>>(hm
				.size());

		for (String key : hm.keySet())
			for (String val : hm.get(key)) {
				if (!outMap.containsKey(val)) {
					Set<String> newset = new HashSet<String>(1);
					newset.add(key);
					outMap.put(val, newset);
				} else {
					Set<String> newset = outMap.get(val);
					hm.remove(val);
					newset.add(key);
					outMap.put(val, newset);
				}
			}

		return outMap;
	}

	public static Map<String, List<String>> reverseMapSS_List(
			Map<String, List<String>> hm) {
		Map<String, List<String>> outMap = new HashMap<String, List<String>>(hm
				.size());

		for (String key : hm.keySet())
			for (String val : hm.get(key)) {
				if (!outMap.containsKey(val)) {
					List<String> newlist = new ArrayList<String>(1);
					newlist.add(key);
					outMap.put(val, newlist);
				} else {
					List<String> newlist = outMap.get(val);
					hm.remove(val);
					newlist.add(key);
					outMap.put(val, newlist);
				}
			}

		return outMap;
	}

	public static DoubleVector getMapDistributionSL(Map<String, List<String>> hm) {
		Set<String> keySet = hm.keySet();

		DoubleVector dist = new DoubleVector(keySet.size());

		for (String s : keySet)
			dist.add(hm.get(s).size());

		return dist;
	}

	public static DoubleVector getMapDistributionSS(Map<String, Set<String>> hm) {
		Set<String> keySet = hm.keySet();

		DoubleVector dist = new DoubleVector(keySet.size());

		for (String s : keySet)
			dist.add(hm.get(s).size());

		return dist;
	}

	public static <X, T> List<X> queryMultipleKeys(Map<T, X> myMap, List<T> keys) {
		List<X> values = new ArrayList<X>();
		for (T eachKey : keys) {
			if (!myMap.containsKey(eachKey)) {
				System.out
						.println("WARNING (HashUti.queryMultipleKeys): Cannot find "
								+ eachKey + " in hashmap!");
			}
			values.add(myMap.get(eachKey));
		}
		return values;
	}

	public static <T1, T2> Map<T2, T1> invertMap(Map<T1, T2> map) {
		Map<T2, T1> out = new HashMap<T2, T1>(map.size());

		for (T1 s : map.keySet())
			out.put(map.get(s), s);

		return out;
	}

	public static void printMapSS(Map<String, Set<String>> hm) {
		Set<String> keys = hm.keySet();
		for (String key : keys) {
			System.out.print(key + "\t");

			Set<String> vals = hm.get(key);

			boolean first = true;
			for (String val : vals)
				if (!first)
					System.out.print("|" + val);
				else {
					first = false;
					System.out.print(val);
				}

			System.out.print("\n");
		}
	}

	/*
	 * public static Map<String,List<ArrayIntList>> readMap (String filename) {
	 * try { FileReader fsin = new FileReader(filename); BufferedReader in1 =
	 * new BufferedReader (fsin); String line; while((line = in1.readLine()) !=
	 * null) { StringTokenizer fields = new StringTokenizer(line,"\t");
	 * while(fields.hasMoreTokens()) { } } } catch(FileNotFoundException e) {
	 * System.out.println(e.getMessage());
	 * System.out.println("Error! Aborting program!"); System.exit(0); }
	 * catch(IOException e) { System.out.println(e.getMessage());
	 * System.out.println("Error! Aborting program!"); System.exit(0); } }
	 */

	public static <T1, T2, T3> Map<T1, Set<T3>> reMapSet(Map<T1, Set<T2>> m1,
			Map<T2, Set<T3>> m2) {
		Map<T1, Set<T3>> out = new HashMap<T1, Set<T3>>(m1.size(), 1);

		for (T1 s : m1.keySet()) {
			Set<T3> newSet = new HashSet<T3>();

			for (T2 s2 : m1.get(s)) {
				Set<T3> aset = m2.get(s2);
				if (aset != null)
					newSet.addAll(aset);
			}

			if (newSet.size() > 0)
				out.put(s, newSet);
		}

		return out;
	}

	public static <T1, T2, T3> Map<T1, Set<T3>> reMapSet1(Map<T1, Set<T2>> m1,
			Map<T2, T3> m2) {
		Map<T1, Set<T3>> out = new HashMap<T1, Set<T3>>(m1.size(), 1);

		for (T1 s : m1.keySet()) {
			Set<T3> newSet = new HashSet<T3>();

			for (T2 s2 : m1.get(s)) {
				T3 m2t3 = m2.get(s2);
				if (m2t3 != null)
					newSet.add(m2t3);
			}

			if (newSet.size() > 0)
				out.put(s, newSet);
		}

		return out;
	}

	public static <T1, T2, T3> Map<T1, T3> reMap(Map<T1, T2> m1, Map<T2, T3> m2) {
		Map<T1, T3> out = new HashMap<T1, T3>(m1.size(), 1);

		for (T1 s : m1.keySet()) {
			T3 newT3 = m2.get(m1.get(s));

			if (newT3 != null)
				out.put(s, newT3);
		}

		return out;
	}

	public static <T1, T2, T3> Map<T3, T2> reMap2(Map<T1, T2> m1, Map<T1, T3> m2) {
		Map<T3, T2> out = new HashMap<T3, T2>(m1.size(), 1);

		for (T1 s : m1.keySet()) {
			T3 newT3 = m2.get(s);

			if (newT3 != null)
				out.put(newT3, m1.get(s));
		}

		return out;
	}
	
	public static <T1,T2> void updateMapSet(Map<T1,Set<T2>> map, T1 key, T2 addition)
	{
		Set<T2> tset = map.get(key);
		if (tset==null)
		{
			tset= new HashSet<T2>();
			tset.add(addition);
			map.put(key, tset);
		}else tset.add(addition);
	}
	
	public static <T1,T2> void updateMapSet(Map<T1,Set<T2>> map, T1 key, Set<T2> additions)
	{
		Set<T2> tset = map.get(key);
		if (tset==null)
		{
			tset= new HashSet<T2>(additions);
			map.put(key, tset);
		}else tset.addAll(additions);
	}

}
