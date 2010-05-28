package org.idekerlab.PanGIAPlugin.data;

import java.io.*;
import java.util.*;
import org.idekerlab.PanGIAPlugin.utilities.files.*;

public abstract class DataVector {

	protected List<String> elementnames;
	protected String listname;
	
	public DataVector()
	{
	}
	
	public DataVector(int size, List<String> elementnames, String listname)
	{
		this.elementnames = elementnames;
		this.listname = listname;
	}
	
	public abstract boolean getAsBoolean(int i);
	public abstract byte getAsByte(int i);
	public abstract float getAsFloat(int i);
	public abstract int getAsInteger(int i);
	public abstract double getAsDouble(int i);
	public abstract String getAsString(int i);
	public abstract Object getAsObject(int i);
	protected abstract Object getDataAsObject();


	public void setElementNames(List<String> elementnames)
	{
		this.elementnames = new ArrayList<String>(elementnames);
	}
	
	public void setElementNames(StringVector elementnames)
	{
		this.elementnames = elementnames.asStringList();
	}
	
	public void setListName(String listname)
	{
		this.listname = listname;
	}
	
	public String getListName()
	{
		return(listname);
	}
	
	public List<String> getElementNames()
	{
		return(new ArrayList<String>(elementnames));
	}
	
	public List<String> getElementNames(IntVector indexes)
	{
		List<String> out = new ArrayList<String>(indexes.size());
		
		for (int i=0;i<indexes.size();i++)
			out.add(elementnames.get(indexes.get(i)));
		
		return(out);
	}
	
	public List<String> getElementNames(BooleanVector bv)
	{
		List<String> out = new ArrayList<String>(bv.sum());
		
		for (int i=0;i<this.elementnames.size();i++)
			if (bv.get(i)) out.add(elementnames.get(i));
		
		return(out);
	}
	
	public void removeElementNames()
	{
		elementnames=null;
	}
	
	public boolean hasElementNames()
	{
		return elementnames!=null;
	}
	
	public boolean hasListName()
	{
		return listname!=null;
	}
	
	public String getElementName(int i)
	{
		return(elementnames.get(i));
	}
	
	public void setElementName(int index, String name)
	{
		elementnames.set(index, name);
	}
	
	public void addElementName(String name)
	{
		elementnames.add(name);
	}
	
	protected abstract void add(String val);
	
	public abstract void Initialize(int size);
	
	protected abstract void set(int i, String val);
	
	public abstract int size();
	
	public void LoadLine(String file)
	{
		Initialize(0);
		FileIterator in = new FileIterator(file);
		for(String line : in)
			this.add(line);
	}
	
	public void LoadColumn(String file, boolean arerownames, boolean islistname, int col)
	{
		LoadColumn(file, arerownames, islistname, col, "\t");
	
	}
	
	public void LoadColumn(String file, boolean arerownames, boolean islistname, int col, String delimiter)
	{
		Initialize(0);
		
		Iterator<String> fi = new FileIterator(file);
		
		if (islistname) this.setListName(fi.next().split(delimiter)[col]);
		
		List<String> newrownames = new ArrayList<String>();
		
		while (fi.hasNext())
		{
			String[] cols = fi.next().split(delimiter);
			
			if (arerownames)
			{
				newrownames.add(cols[0]);
				this.add(cols[col]);
			}else this.add(cols[col]);
		}
			
			
		if (arerownames) this.setElementNames(newrownames);
	}
	
	public void appendFile(String file)
	{
		writeOut(file,true, false);
	}
	
	public void appendFile(String file, boolean vertical)
	{
		writeOut(file,true, vertical);
	}
	
	public void save(String file)
	{
		writeOut(file, false, true);
	}
	
	private void writeOut(String file, boolean append, boolean vertical)
	{
		//Check if file exists... create a new one if necessary
		File outfile = new File(file);
		if (!outfile.exists())
		{
			append = false;
			
			try
			{
				outfile.createNewFile();
			}catch (IOException e)
			{
				System.out.println("Error DataVector.writeOut(String,boolean,boolean): IOException.");
				System.out.println(e.getMessage());
				System.exit(0);
			}
		}
		
		if (size()==0) return;
		
		//Initialize the writers
		FileWriter fw = null;
		try
		{
			fw = new FileWriter(file, append);
		}catch (FileNotFoundException e)
		{
			System.out.println("Error DataVector.writeOut(String,boolean,boolean): File not found.");
			System.out.println(file);
			System.out.println(e.getMessage());
			System.exit(0);
		}catch (IOException e)
		{
			System.out.println("Error DataVector.writeOut(String,boolean,boolean): IOException.");
			System.out.println(e.getMessage());
			System.exit(0);
		}
		
		BufferedWriter bw = new BufferedWriter(fw);
		
		try
		{
			if (vertical)
			{
				
				if (this.hasListName() && !append && this.hasElementNames()) bw.write("\t"+this.getListName()+"\n");
				if (this.hasListName() && !append && !this.hasElementNames()) bw.write(this.getListName()+"\n");
				
				for (int i=0;i<this.size();i++)
				{
					if (this.hasElementNames()) bw.write(this.getElementName(i)+"\t");
					bw.write(this.getAsString(i)+"\n");
				}
			}else
			{
				if (this.hasElementNames() && !append)
				{
					if (this.hasListName()) bw.write("\t");
					
					bw.write(elementnames.get(0));
					for (int i=1;i<size();i++)
						bw.write("\t" + elementnames.get(i));
								
					bw.write("\n");	
				}
				
				if (this.hasListName()) bw.write(listname+"\t");
					
				bw.write(getAsString(0));
				for (int i=1;i<size();i++)
					bw.write("\t" + getAsString(i));
				
				bw.write("\n");
			}
		}
		catch(IOException e)
		{
			System.out.println(e.getMessage());
			System.exit(0);
		}
		
		//Close writer
		try {bw.close();}
		catch(IOException e)
		{
			System.out.println(e.getMessage());
			System.exit(0);
		}
	}
	
	public void saveToFile_oneElementPerLine (String outputFileName)
	{
		try
		{
			PrintWriter out = new PrintWriter(new FileOutputStream(outputFileName));
			for(int j=0; j<size(); j++)
				out.println(getAsString(j));
			out.close();
		}
		catch(FileNotFoundException e)
		{
			System.out.println(e.getMessage());
			System.out.println("Error! Aborting program!");
			System.exit(0);
		}
	}
	
	public void saveToFile_oneLineDelimited (String outputFileName, String delimiter)
	{
		try
		{
			PrintWriter out = new PrintWriter(new FileOutputStream(outputFileName));
			for(int j=0; j<size(); j++)
			{
				if(j<size()-1)
					out.print(getAsString(j)+delimiter);
				else
					out.println(getAsString(j));
			}
			out.close();
		}
		catch(FileNotFoundException e)
		{
			System.out.println(e.getMessage());
			System.out.println("Error! Aborting program!");
			System.exit(0);
		}
	}
	
	public boolean contains(Object o)
	{
		for (int i=0;i<this.size();i++)
			if (this.getAsObject(i).equals(o)) return true;
		
		return false;
	}
	
	public String printAsRow()
	{
		if (this.size()==0) return "";
		
		String out = this.getAsString(0);
		
		for (int i=1;i<this.size();i++)
			out += "\t"+this.getAsString(i);
		
		return out+"\n";
	}
	
	public void print()
	{
		if (this.size()==0)
		{
			System.out.println("");
			return;
		}
		
		if (this.hasListName())
			System.out.println("\t"+this.listname);
		
		for (int i=0;i<this.size();i++)
			if (this.hasElementNames()) System.out.println(this.getElementName(i)+"\t"+this.getAsString(i));
			else System.out.println(this.getAsString(i));
	}
	
	public BooleanVector equalTo(String s)
	{
		BooleanVector out = new BooleanVector(this.size());
		
		for (int i=0;i<this.size();i++)
			out.add(this.getAsString(i).equals(s));
		
		return out;
	}

	public BooleanVector equalToAny(Collection<String> entries)
	{
		BooleanVector out = new BooleanVector(this.size());
		
		for (int i=0;i<this.size();i++)
			out.add(entries.contains(this.getAsString(i)));
		
		return out;
	}


	public BooleanVector asBooleanVector()
	{
		BooleanVector ail = new BooleanVector(this.size());
		for (int i=0;i<this.size();i++)
			ail.add(this.getAsBoolean(i));
		
		return ail;
	}  
	
	public ByteVector asByteVector()
	{
		ByteVector ail = new ByteVector(this.size());
		for (int i=0;i<this.size();i++)
			ail.add(this.getAsByte(i));
		
		return ail;
	}
	
	public IntVector asIntVector()
	{
		IntVector ail = new IntVector(this.size());
		for (int i=0;i<this.size();i++)
			ail.add(this.getAsInteger(i));
		
		return ail;
	}
	
	public DoubleVector asDoubleVector()
	{
		DoubleVector ail = new DoubleVector(this.size());
		for (int i=0;i<this.size();i++)
			ail.add(this.getAsDouble(i));
		
		return ail;
	}
	
	public StringVector asStringVector()
	{
		StringVector ail = new StringVector(this.size());
		for (int i=0;i<this.size();i++)
			ail.add(this.getAsString(i));
		
		return ail;
	}

	public boolean[] asBooleanArray()
	{
		boolean[] da = new boolean[size()];
		
		for (int i=0;i<this.size();i++)
			da[i] = getAsBoolean(i);
		
		return da;
	}
	
	public byte[] asByteArray()
	{
		byte[] da = new byte[size()];
		
		for (int i=0;i<this.size();i++)
			da[i] = getAsByte(i);
		
		return da;
	}
	
	public int[] asIntArray()
	{
		int[] da = new int[size()];
		
		for (int i=0;i<this.size();i++)
			da[i] = getAsInteger(i);
		
		return da;
	}
	
	public float[] asFloatArray()
	{
		float[] da = new float[size()];
		
		for (int i=0;i<this.size();i++)
			da[i] = getAsFloat(i);
		
		return da;
	}
	
	public double[] asDoubleArray()
	{
		double[] da = new double[size()];
		
		for (int i=0;i<this.size();i++)
			da[i] = getAsDouble(i);
		
		return da;
	}
	
	public String[] asStringArray()
	{
		String[] da = new String[size()];
		
		for (int i=0;i<this.size();i++)
			da[i] = getAsString(i);
		
		return da;
	}

	public boolean equals(Object other)
	{
		if (other==null) return false;
		else if (! (other instanceof DataVector)) return false;
		else
		{
			DataVector dv = (DataVector)other;
			
			if (dv.size()!=size()) return false;
			if (dv.listname!=this.listname) return false;
			
			if (this.hasElementNames() && this.elementnames.size()==dv.getElementNames().size())
				for (int i=0;i<size();i++)
					if (!this.getElementName(i).equals(dv.getElementName(i))) return false;
			
			if (!this.getDataAsObject().equals(dv.getDataAsObject())) return false;
		}
		
		return true;
	}

	public int hashCode()
	{
		return getDataAsObject().hashCode();
	}

	public String toString()
	{
		if (size()==0) return "[]";
		
		String out = "["+getAsString(0);
		for (int i=1;i<size();i++)
			out += ","+getAsString(i);
		
		return out+"]";
	}

	public DoubleVector exp()
	{
		DoubleVector out = new DoubleVector(this.size());
		
		for (int i=0;i<size();i++)
			out.set(i, Math.exp(out.getAsDouble(i)));
		
		return out;
	}

	public List<Double> asDoubleList()
	{
		List<Double> out = new ArrayList<Double>(size());
		
		for (int i=0;i<size();i++)
			out.add(getAsDouble(i));
		
		return out;
	}
	
	public List<Float> asFloatList()
	{
		List<Float> out = new ArrayList<Float>(size());
		
		for (int i=0;i<size();i++)
			out.add(getAsFloat(i));
		
		return out;
	}
	
	public List<Integer> asIntegerList()
	{
		List<Integer> out = new ArrayList<Integer>(size());
		
		for (int i=0;i<size();i++)
			out.add(getAsInteger(i));
		
		return out;
	}
	
	public List<Byte> asByteList()
	{
		List<Byte> out = new ArrayList<Byte>(size());
		
		for (int i=0;i<size();i++)
			out.add(getAsByte(i));
		
		return out;
	}
	
	public List<Boolean> asBooleanList()
	{
		List<Boolean> out = new ArrayList<Boolean>(size());
		
		for (int i=0;i<size();i++)
			out.add(getAsBoolean(i));
		
		return out;
	}
	
	public List<String> asStringList()
	{
		List<String> out = new ArrayList<String>(size());
		
		for (int i=0;i<size();i++)
			out.add(getAsString(i));
		
		return out;
	}
	
	public BooleanVector notEqualTo(String val)
	{
		BooleanVector out = new BooleanVector(size());
		
		for (int i=0;i<size();i++)
			out.add(!getAsString(i).equals(val));
		
		return out;
	}
	
	
	

}
