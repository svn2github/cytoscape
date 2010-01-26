package data;

import java.util.*;

import com.google.common.collect.*;

public class StringVector extends DataVector implements java.lang.Iterable<String>{

	private List<String> data;
	
	public StringVector()
	{
		data = new ArrayList<String>();
	}
	
	public StringVector(Collection<String> vals)
	{
		Initialize(vals.size());
		
		for (String s: vals)
			data.add(s);
	}
	
	public StringVector(String[] vals)
	{
		Initialize(vals.length);
		for(int i=0; i<vals.length; i++)
			data.add(vals[i]);
	}
	
	public StringVector(double[] vals)
	{
		Initialize(vals.length);
		
		for (int i=0;i<vals.length;i++)
			data.add(i,String.valueOf(vals[i]));
	}
	
	public StringVector(DoubleVector vals)
	{
		Initialize(vals.size());
		
		for (int i=0;i<vals.size();i++)
			data.add(i,String.valueOf(vals.get(i)));
	}
	
	public StringVector(int size, List<String> elementnames, String listname)
	{
		Initialize(size);
					
		setElementNames(elementnames);
		
		setListName(listname);
	}
	
	public StringVector(int size)
	{
		Initialize(size);
	}
	
	public StringVector(int size, String value)
	{
		Initialize(size, value);
	}
	
	public StringVector(String file, boolean arerownames, boolean arecolname)
	{
		LoadColumn(file,arerownames,arecolname,0);
	}
	
	public StringVector(String file, boolean arerownames, boolean arecolname, int column)
	{
		LoadColumn(file,arerownames,arecolname,column);
	}
	
	public StringVector(String file, boolean arerownames, boolean arecolname, int column, String delimiter)
	{
		LoadColumn(file,arerownames,arecolname,column,delimiter);
	}
	
	public StringVector(String file)
	{
		LoadLine(file);
	}
	
	public void Initialize(int size)
	{
		data = new ArrayList<String>(size);
	}
	
	public void Initialize(int size, String value)
	{
		for (int i=0;i<size;i++)
			data.add(value);
	}
	
	public String[] asStringArray()
	{
		String[] da = new String[size()];
		
		for (int i=0;i<data.size();i++)
			da[i] = get(i);
		
		return da;
	}
	
	public Set<String> asStringSet()
	{
		Set<String> out = new HashSet<String>(this.size());
		
		for (int i=0;i<data.size();i++)
			out.add(this.get(i));
		
		return out;
	}
	
	public Set<Integer> asIntegerSet()
	{
		Set<Integer> out = new HashSet<Integer>(this.size());
		
		for (int i=0;i<data.size();i++)
			out.add(Integer.valueOf(this.get(i)));
		
		return out;
	}
	
	public Object getAsObject(int i)
	{
		return data.get(i);
	}
	
	public boolean getAsBoolean(int i)
	{
		return Boolean.valueOf(data.get(i));
	}
	
	public String getAsString(int i)
	{
		return data.get(i);
	}
	
	public double getAsDouble(int i)
	{
		return Double.valueOf(get(i));
	}
	
	public byte getAsByte(int i)
	{
		return Byte.valueOf(get(i));
	}
	
	public int getAsInteger(int i)
	{
		return Integer.valueOf(get(i));
	}
	
	public float getAsFloat(int i)
	{
		return Float.valueOf(get(i));
	}
	
	protected Object getDataAsObject()
	{
		return data;
	}
	
	public String get(int i)
	{
		return(data.get(i));
	}
	
	public StringVector get(IntVector iv)
	{
		StringVector out = new StringVector(iv.size());
		
		for (int i=0;i<iv.size();i++)
			out.add(this.get(iv.get(i)));
		
		return out;
	}
	
	public String get(String element)
	{
		return(data.get(getElementNames().indexOf(element)));
	}
	
	public void set(String[] newData)
	{
		this.data = new ArrayList<String>(newData.length);
		
		for (int i=0;i<newData.length;i++)
			this.data.add(newData[i]);
	}
	
	public void set(int i, String val)
	{
		data.set(i, val);
	}
	
	public synchronized void add(String toAdd)
	{
		data.add(toAdd);
	}
	
	public void add(int val)
	{
		data.add(""+val);
	}
	
	public void add(double val)
	{
		data.add(""+val);
	}
	
	public void set(String element, String val)
	{
		data.set(getElementNames().indexOf(element),val);
	}
	
	protected void clearData()
	{
		data.clear();
	}
	
	protected void RemoveData(int i)
	{
		data.remove(i);
	}
	
	public StringVector sample(int samplesize, boolean replace)
	{
		StringVector mysample = new StringVector(samplesize);
		
		StringVector cp = this.clone();
		
		java.util.Random randgen = new java.util.Random();
		
		randgen.setSeed(System.nanoTime());
		
		if (!replace)
		{
			int lsizem1 = cp.size()-1;
			
			for (int i=0;i<samplesize;i++)
			{
				int swapi = lsizem1-randgen.nextInt(cp.size()-i);
				String temp = cp.get(i);
				cp.set(i, cp.get(swapi));
				cp.set(swapi, temp);
			}
			
			
			return cp.subVector(0, samplesize);
		}else
		{
			for (int r=0;r<samplesize;r++)
			{
				int rand = randgen.nextInt(cp.size());
				mysample.add(cp.get(rand));
			}
		}
		
		return mysample;
	}
	
	public StringVector subVector(int i1, int size)
	{
		StringVector out = new StringVector(size);
		
		int i1s = i1+size;
		
		for (int i=i1;i<i1s;i++)
			out.add(this.get(i));
		
		return out;
	}
	
	public StringVector clone()
	{
		StringVector copy = new StringVector();
		
		if (this.hasListName()) copy.listname = this.listname;
		if (this.hasElementNames()) copy.setElementNames(new ArrayList<String>(this.getElementNames()));
		
		int size = data.size();
			
		copy.data = new ArrayList<String>(size);
		
		for (int i=0;i<size;i++)
			copy.data.add(get(i));
		
		return(copy);
	}
	
	public int size()
	{
		return data.size();
	}
	
	public static StringVector join(StringVector dt1, StringVector dt2)
	{
		int newsize = dt1.size() + dt2.size();
				
		List<String> rownames = dt1.getElementNames();
		rownames.addAll(dt2.getElementNames());
		
		StringVector dtout = new StringVector(newsize, rownames, dt1.getListName());
		
		for (int i=0;i<dt1.size();i++)
			dtout.set(i, dt1.get(i));
		
		for (int i=0;i<dt2.size();i++)
			dtout.set(i+dt1.size(), dt2.get(i));
		
		return dtout;
	}
	
	public static StringVector joinAll(List<StringVector> dtlist)
	{
		if (dtlist.size()==0) return null;
		if (dtlist.size()==1) return dtlist.get(0);
		
		StringVector dtout = join(dtlist.get(0), dtlist.get(1));
		
		for (int i=2;i<dtlist.size();i++)
			dtout = join(dtout, dtlist.get(i));
		
		return dtout;
	}
	
	public Iterator<String> iterator()
	{
		return data.iterator();
	}
	
	public Map<String,Integer> tabulate()
	{
		Multiset<String> ms = new HashMultiset<String>(this.size());
		
		for (String s : this)
			ms.add(s);
		
		Map<String,Integer> out = new HashMap<String,Integer>(ms.size());
		
		for (String s : ms.elementSet())
			out.put(s, ms.count(s));
		
		return out;
	}
	
	public StringVector sort()
	{
		StringVector out;
		
		if (!this.hasElementNames())
		{
			out = this.clone();
			String[] mydata = out.asStringArray();
			Arrays.sort(mydata);
			out.set(mydata);
		}else
		{
			out = new StringVector(this.size());
			if (this.hasListName()) out.setListName(this.listname);
			
			IntVector sorti = this.sort_I();
			List<String> rownames = new ArrayList<String>(this.size());
			for (int i=0;i<sorti.size();i++)
			{
				out.add(this.get(sorti.get(i)));
				rownames.add(this.getElementName(sorti.get(i)));
			}
			out.elementnames = rownames;
		}
		
		return out;
	}
	
	public IntVector sort_I()
	{
		return Sorter.Sort_I(this.clone());
	}
	
	public BooleanVector notContainedWithin(Set<String> s)
	{
		BooleanVector out = new BooleanVector(this.size());
		
		for (int i=0;i<this.size();i++)
			out.add(!s.contains(this.get(i)));
		
		return out;
	}
	
	public BooleanVector containedWithin(Set<String> s)
	{
		BooleanVector out = new BooleanVector(this.size());
		
		for (int i=0;i<this.size();i++)
			out.add(s.contains(this.get(i)));
		
		return out;
	}
	
	public void remove(int index)
	{
		this.data.remove(index);
	}
	
	public void addAll(StringVector sv)
	{
		for (String s : sv)
			this.add(s);
	}
	
	public void retainAll(Collection<String> c)
	{
		this.data.retainAll(c);
	}
	
	public void removeAll(Collection<String> c)
	{
		this.data.removeAll(c);
	}
	
	public Map<String,Integer> hashIndex()
	{
		Map<String,Integer> out = new HashMap<String,Integer>(this.size());
		
		for (int i=0;i<this.size();i++)
			out.put(this.get(i), i);
		
		return out;
	}
	
	public StringVector get(BooleanVector bv)
	{
		StringVector out = new StringVector(bv.sum());
		
		for (int i=0;i<bv.size();i++)
			if (bv.get(i)) out.add(this.get(i));
		
		return out;
	}
	
	public boolean allEqualTo(String val)
	{
		for (int i=0;i<size();i++)
			if(!get(i).equals(val)) return false;
		
		return true;
	}
	
	public StringVector unique()
	{
		return new StringVector(new HashSet<String>(this.data));
	}
	
	public StringVector reverseOrder()
	{
		StringVector out = new StringVector(this.size());
		
		for (int i=this.size()-1;i>=0;i--)
			out.add(this.get(i));
		
		return out;
	}
	
	public int indexOf(String s)
	{
		return data.indexOf(s);
	}
	
	public static double orderDistance(StringVector s1, StringVector s2)
	{
		double dist = 0;
		
		for (int i=0;i<s1.size();i++)
		{
			int index = s2.indexOf(s1.get(i));
			if (index==-1) throw new java.lang.IllegalArgumentException("Entry \""+s1.get(i)+"\" could not be found in s2.");
			
			int diff = i-index;
			dist += diff*diff;
		}
		
		return Math.sqrt(dist);
	}
}
