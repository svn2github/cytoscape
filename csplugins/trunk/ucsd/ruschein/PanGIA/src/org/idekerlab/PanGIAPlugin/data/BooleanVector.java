package org.idekerlab.PanGIAPlugin.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class BooleanVector extends DataVector {

	private boolean[] data;
	private int size;
	
	public BooleanVector()
	{
		Initialize(0);
	}
	
	public BooleanVector(boolean[] data)
	{
		this.data = data;
		this.size = data.length;
	}
	
	public BooleanVector(List<Boolean> vals)
	{
		data = new boolean[vals.size()];
		size = vals.size();
		
		for (int i=0;i<vals.size();i++)
			data[i] = vals.get(i);
	}
	
	public BooleanVector(int size, List<String> elementnames, String listname)
	{
		Initialize(size);
					
		setElementNames(elementnames);
		
		setListName(listname);
	}
	
	public BooleanVector(int size, boolean value, List<String> elementnames, String listname)
	{
		Initialize(size, value);
					
		setElementNames(elementnames);
		
		setListName(listname);
	}
	
	public BooleanVector(int size)
	{
		Initialize(size);
	}
	
	public BooleanVector(int size, boolean value)
	{
		Initialize(size,value);
	}
	
	public BooleanVector(int size, Set<Integer> trues)
	{
		data = new boolean[size];
		this.size = size;
		
		for (int i=0;i<size;i++)
			data[i] = trues.contains(i);
	}
	
	public BooleanVector(String file, boolean arerownames, boolean arecolname)
	{
		LoadColumn(file,arerownames,arecolname,0);
	}
	
	public void Initialize(int size)
	{
		data = new boolean[size];
		size = 0;
	}
	
	public void Initialize(int count, boolean val)
	{
		data = new boolean[count];
		size = count;
		
		for (int i=0;i<count;i++)
			data[i] = val;
	}
	
	protected Object getDataAsObject()
	{
		return data;
	}
	
	public boolean[] getData()
	{
		if (size==data.length) return data;
		else return BooleanVector.resize(data, this.size);
	}
	
	public static boolean[] resize(boolean[] vec, int size)
	{
		boolean[] out = new boolean[size];
		
		int n = Math.min(vec.length, size);
		for (int i=0;i<n;i++)
			out[i] = vec[i];
		
		return out;
	}
	
	public void add(boolean val)
	{
		if (data.length==0) data = new boolean[10];
		else if (this.size==data.length)	data = BooleanVector.resize(data,data.length*2);
		
		data[size] = val;
		size++;
	}
	
	public void add(String val)
	{
		this.add(Boolean.valueOf(val));
	}
	
	public void addAll(boolean[] vals)
	{
		if (data.length<this.size+vals.length) data = BooleanVector.resize(data,data.length+vals.length);
		
		for (Boolean d : vals)
			this.add(d);
	}
	
	public void addAll(Collection<Boolean> vals)
	{
		if (data.length<this.size+vals.size()) data = BooleanVector.resize(data,data.length+vals.size());
		
		for (Boolean d : vals)
			this.add(d);
	}
	
	public void addAll(BooleanVector vals)
	{
		if (data.length<this.size+vals.size()) data = BooleanVector.resize(data,data.length+vals.size());
		
		for (int i=0;i<vals.size();i++)
			this.add(vals.get(i));
	}
	
	public Object getAsObject(int i)
	{
		return data[i];
	}
	
	public String getAsString(int i)
	{
		return Boolean.toString(data[i]);
	}
	
	public double getAsDouble(int i)
	{
		if (get(i)) return 1;
		else return 0;
	}
	
	public boolean getAsBoolean(int i)
	{
		return get(i);
	}
	
	public byte getAsByte(int i)
	{
		if (get(i)) return 1;
		else return 0;
	}
	
	public float getAsFloat(int i)
	{
		if (get(i)) return 1;
		else return 0;
	}
	
	public int getAsInteger(int i)
	{
		if (get(i)) return 1;
		else return 0;
	}
	
	public Boolean get(int i)
	{
		return data[i];
	}
	
	public Boolean get(String element)
	{
		return data[getElementNames().indexOf(element)];
	}
	
	public Boolean set(int i, Boolean val)
	{
		return data[i] = val;
	}
	
	public void set(int i, Integer val)
	{
		if (val.intValue()==0) data[i] = false;
		else if (val.intValue()==1) data[i] = true;
		else throw new java.lang.IllegalArgumentException("Value must be 0 or 1");
	}
	
	public void set(String element, Boolean val)
	{
		data[getElementNames().indexOf(element)] = val;
	}
	
	public void set(int i, String val)
	{
		data[i] = Boolean.valueOf(val);
	}
	
	public BooleanVector clone()
	{
		BooleanVector copy = new BooleanVector(BooleanVector.copy(this.data));
		
		if (this.hasListName()) copy.setListName(this.getListName());
		if (this.hasElementNames()) copy.setElementNames(new ArrayList<String>(this.getElementNames()));
		
		return copy;
	}
	
	public static boolean[] copy(boolean[] data)
	{
		boolean[] out = new boolean[data.length];
		for (int i=0;i<data.length;i++)
			out[i] = data[i];
		
		return out;
	}
	
	public int size()
	{
		return size;
	}
		
	public BooleanVector not()
	{
		BooleanVector out = this.clone();
		
		for (int i=0;i<size();i++)
			out.set(i,!out.get(i));
		
		return out;
	}
	
	public BooleanVector and(BooleanVector v2)
	{
		if (v2.size()!=size())
		{
			System.out.println("Error and(): Vectors must be the same size.");
			System.exit(0);
		}
		
		BooleanVector out = new BooleanVector(size());
		
		for (int i=0;i<size();i++)
			out.add(get(i) && v2.get(i));
		
		return out;
	}
	
	public BooleanVector or(BooleanVector v2)
	{
		if (v2.size()!=size())
		{
			System.out.println("Error or(): Vectors must be the same size.");
			System.exit(0);
		}
		
		BooleanVector out = new BooleanVector(size());
		
		for (int i=0;i<size();i++)
			out.add(get(i) || v2.get(i));
		
		return out;
	}
	
	public Double mean()
	{
		return sum() / (double)size();
	}
	
	public int sum()
	{
		return BooleanVector.sum(getData());
	}
	
	public static int sum(boolean[] data)
	{
		int mysum = 0;
		
		for (int i=0;i<data.length;i++)
			if (data[i]) 
				mysum++;
		
		return mysum;
	}
	
	public int sumFalse()
	{
		int mySumFalse = 0;
		for(int i=0; i<this.size(); i++)
			if(!data[i]) 
				mySumFalse++;
	
		return mySumFalse;
	}

	public static BooleanVector join(BooleanVector dt1, BooleanVector dt2)
	{
		int newsize = dt1.size() + dt2.size();
				
		List<String> rownames = dt1.getElementNames();
		rownames.addAll(dt2.getElementNames());
		
		BooleanVector dtout = new BooleanVector(newsize, rownames, dt1.getListName());
		
		for (int i=0;i<dt1.size();i++)
			dtout.set(i, dt1.get(i));
		
		for (int i=0;i<dt2.size();i++)
			dtout.set(i+dt1.size(), dt2.get(i));
		
		return dtout;
	}
	
	public static BooleanVector joinAll(List<BooleanVector> dtlist)
	{
		if (dtlist.size()==0) return null;
		if (dtlist.size()==1) return dtlist.get(0);
		
		BooleanVector dtout = join(dtlist.get(0), dtlist.get(1));
		
		for (int i=2;i<dtlist.size();i++)
			dtout = join(dtout, dtlist.get(i));
		
		return dtout;
	}
	
	public static int[] asIndexes(boolean[] data)
	{
		int[] out = new int[BooleanVector.sum(data)];
		
		int j = 0;
		for (int i=0;i<data.length;i++)
			if (data[i])
			{
				out[j] = i;
				j++;
			}
		
		return out;
	}
	
	
	public IntVector asIndexes()
	{
		return new IntVector(asIndexes(getData()));
	}
	
	public BooleanVector get(List<?> indexes)
	{
		BooleanVector sub = new BooleanVector(indexes.size());
		
		for (int i=0;i<indexes.size();i++)
			sub.add(data[((Double)(indexes.get(i))).intValue()]);
		
		return sub;
	}
	
	public BooleanVector reverse()
	{
		BooleanVector out = new BooleanVector(this.size());
		
		for (int i=this.size()-1;i>=0;i--)
			out.add(get(i));
		
		return out;
	}
	
	public boolean allEqualTo(boolean val)
	{
		for (int i=0;i<size();i++)
			if(get(i)!=val) return false;
		
		return true;
	}
	
	public boolean anyEqualTo(boolean val)
	{
		for (int i=0;i<size();i++)
			if(get(i)==val) return true;
		
		return false;
	}
	
	public BooleanVector equalTo(boolean val)
	{
		BooleanVector out = new BooleanVector(size());
		
		for (int i=0;i<size();i++)
			out.add(get(i)==val);
		
		return out;
	}
	
	public BooleanVector notEqualTo(boolean val)
	{
		BooleanVector out = new BooleanVector(size());
		
		for (int i=0;i<size();i++)
			out.add(get(i)!=val);
		
		return out;
	}
	

}
