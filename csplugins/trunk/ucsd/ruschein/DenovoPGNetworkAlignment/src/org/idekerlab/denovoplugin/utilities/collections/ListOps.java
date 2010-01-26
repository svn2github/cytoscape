package utilities.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.ListUtils;

import data.IntVector;

/**
 * A utility class providing commonly-used operations on lists (vectors).
 * @author rsrivas
 *
 */
public class ListOps {

	/**
	 * Computes mean of numbers in list.
	 * @param vals A list of doubles.
	 * @return Mean of doubles in <i>vals</i>.
	 */
	public static double Mean(List<Double> vals)
	{
		while (vals.contains(Double.NaN))
			vals.remove(Double.NaN);
		
		if (vals.size()==0) return Double.NaN;
		
		double sum = 0;
		for (int i=0;i<vals.size();i++)
			sum += vals.get(i).doubleValue();
		
		return (sum / vals.size());
	}
	
	public static double Mean(List<Double> vals, List<Double> weights)
	{		
		if (vals.size()!=weights.size())
		{
			System.out.println("Weighted mean: Data length != weight length");
			return Double.NaN;
		}
		
		while (vals.contains(Double.NaN))
			weights.remove(vals.remove(Double.NaN));
				
		if (vals.size()==0) return Double.NaN;
		
		double sum = 0;
		for (int i=0;i<vals.size();i++)
			sum += vals.get(i).doubleValue()*weights.get(i);
		
		return (sum / ListOps.sum(weights));
	}
	
	public static double sum(List<Double> vals)
	{
		double total = 0;
		for (int i=0;i<vals.size();i++)
			total+=vals.get(i);
		
		return total;
	}
	
	public static int sum(List<Integer> vals)
	{
		int total = 0;
		for (int i=0;i<vals.size();i++)
			total+=vals.get(i);
		
		return total;
	}
	
	
	public static double Max(List<Double> vals)
	{
		while (vals.contains(Double.NaN))
			vals.remove(Double.NaN);
		
		if (vals.size()==0) return Double.NaN;
		
		double max = vals.get(0);
		for (int i=1;i<vals.size();i++)
			if (vals.get(i)>max) max = vals.get(i).doubleValue();
					
		return (max);
	}
	
	public static List<String> toList(String[] array)
	{
		List<String> newlist = new ArrayList<String>();
		
		if (array != null)
		{
			for (int i=0;i<array.length;i++)
				newlist.add(array[i]);
		}
		
		return(newlist);
	}
	
	public static <T> List<T> sample(List<T> mylist, int samplesize, boolean replace)
	{
		if (samplesize==0) return new ArrayList<T>(0);
		
		List<T> thelist = new ArrayList<T>(mylist);
		
		java.util.Random rand = new java.util.Random();
		rand.setSeed(System.nanoTime());
		
		
		if (!replace)  
		{
			//Collections.shuffle(thelist);
			int lsizem1 = thelist.size()-1;
			
			for (int i=0;i<samplesize;i++)
			{
				int swapi = lsizem1-rand.nextInt(thelist.size()-i);
				T temp = thelist.get(i);
				thelist.set(i, thelist.get(swapi));
				thelist.set(swapi, temp);
			}
			
			
			return thelist.subList(0, samplesize);
		}else
		{
			List<T> mysample = new ArrayList<T>(samplesize);
			
			rand.setSeed(System.nanoTime());
			
			for (int r=0;r<samplesize;r++)
			{
				int rnum = rand.nextInt(thelist.size());
				mysample.add(thelist.get(rnum));
			}
			
			return mysample;
		}
	}
	
	public static String Concatenate(String[] list, String sep)
	{
		String out = list[0];
		for (int i=1;i<list.length;i++)
			out+=sep+list[i];
		
		return out;
	}
	
	public static String[] Unique(String[] st)
	{
		HashSet<String> hs = new HashSet<String>();
		for (int i=0;i<st.length;i++)
			hs.add(st[i]);
		
		
		return hs.toArray(new String[hs.size()]);
	}
	
	@SuppressWarnings("unchecked") public static List<String> intersect(List<String> s1, List<String> s2)
	{
		return new ArrayList<String>(ListUtils.intersection(s1, s2));
	}
	
	@SuppressWarnings("unchecked") public static List<String> union(List<String> s1, List<String> s2)
	{
		return new ArrayList<String>(ListUtils.union(s1, s2));
	}
	
	/*
	public static List<String> subList (List<String> s1, int startIndex, int endIndex)
	{
		if(startIndex>endIndex)
		{
			System.out.println("Start index cannot be greater then end index!");
			System.exit(0);
		}
		
		if(startIndex>s1.size()|| endIndex>s1.size())
		{
			System.out.println("Indices provided are larger than the list!");
			System.exit(0);
		}

		List<String> mySubList = new ArrayList<String>();
		for(int i = startIndex; i<=endIndex; i++)
			mySubList.add(s1.get(i));
		
		return mySubList;
	}*/
	
	public static <T> List<T> reOrder(List<T> data, IntVector order)
	{
		List<T> newlist = new ArrayList<T>(data.size());
		
		for (int i=0;i<order.size();i++)
			newlist.add(data.get(order.get(i)));
		
		return newlist;
	}
	
	public static String collectionToString(Collection<?> st)
	{
		String out = "";
		
		Iterator<?> si = st.iterator();
		while (si.hasNext())
		{
			out+= si.next();
			if (si.hasNext()) out+="|";
		}
		
		return out;
	}
	
	public static String collectionToString(double[] st)
	{
		if (st.length==0) return "";
		
		String out = String.valueOf(st[0]);
		
		for (int i=1;i<st.length;i++)
			out += "|"+st[i];
		
		return out;
	}
	
	public static String collectionToString(Collection<String> st, String delim)
	{
		String out = "";
		
		Iterator<String> si = st.iterator();
		while (si.hasNext())
		{
			out+= si.next();
			if (si.hasNext()) out+=delim;
		}
		
		return out;
	}
}
