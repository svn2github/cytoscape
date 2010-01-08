package data;

import java.util.*;

import org.apache.commons.collections.primitives.ArrayByteList;

import plot.Plotter;


public class ByteVector extends DataVector {

	private byte[] data;
	private int size;
	
	public ByteVector()
	{
		Initialize(0);
	}
	
	public ByteVector(double[] data)
	{
		this.data = new byte[data.length];
		this.size = data.length;
		
		for (int i=0;i<data.length;i++)
			this.data[i] = (byte)data[i];
	}
	
	public ByteVector(float[] data)
	{
		this.data = new byte[data.length];
		this.size = data.length;
		
		for (int i=0;i<data.length;i++)
			this.data[i] = (byte)data[i];
	}
	
	public ByteVector(short[] data)
	{
		this.data = new byte[data.length];
		this.size = data.length;
		
		for (int i=0;i<data.length;i++)
			this.data[i] = (byte)data[i];
	}
	
	public ByteVector(int[] data)
	{
		this.data = new byte[data.length];
		this.size = data.length;
		
		for (int i=0;i<data.length;i++)
			this.data[i] = (byte)data[i];
	}

	public ByteVector(byte[] data)
	{
		this.data = data;
		this.size = data.length;
	}
	
	public ByteVector(List<?> vals)
	{
		this.data = new byte[vals.size()];
		this.size = vals.size();
		
		if (vals.size()==0) return;
		
		if (vals.get(0) instanceof String)
		{
			for (int i=0;i<vals.size();i++)
				data[i] = Byte.valueOf((String)vals.get(i));
		}
		
		if (vals.get(0) instanceof Double)
		{
			for (int i=0;i<vals.size();i++)
				data[i] = (Byte)vals.get(i);
		}
		
		if (vals.get(0) instanceof Byte)
		{
			for (int i=0;i<vals.size();i++)
				data[i] = (Byte)vals.get(i);
		}
	}
	
	public ByteVector(Set<Byte> vals)
	{
		this.data = new byte[vals.size()];
		this.size = vals.size();
		
		int i = 0;
		for (Byte b : vals)
		{
			data[i] = b;
			i++;
		}
	}
	
	
	public ByteVector(int size, List<String> elementnames, String listname)
	{
		Initialize(size);
					
		setElementNames(elementnames);
		
		setListName(listname);
	}
	
	public ByteVector(int size)
	{
		Initialize(size);
	}
	
	public ByteVector(int size, byte vals)
	{
		Initialize(size,vals);
	}
	
	public ByteVector(int size, int vals)
	{
		Initialize(size,vals);
	}
	
	public ByteVector(StringVector sv)
	{
		Initialize(sv.size());
		
		if (sv.hasElementNames()) setElementNames(sv.getElementNames());
		if (sv.hasListName()) setListName(sv.getListName());
		
		for (int i=0;i<sv.size();i++)
			add(Byte.valueOf(sv.get(i)));
	}

	
	public ByteVector(String file, boolean arerownames, boolean arecolname)
	{
		LoadColumn(file,arerownames,arecolname,0);
	}
	
	public ByteVector(String file, boolean arerownames, boolean arecolname, int column)
	{
		LoadColumn(file,arerownames,arecolname,column);
	}
	
	public void Initialize(int size)
	{
		data = new byte[size];
		size = 0;
	}
	
	public void Initialize(int count, byte val)
	{
		data = new byte[count];
		size = count;
		
		for (int i=0;i<count;i++)
			data[i] = val;
	}
	
	public void Initialize(int count, int val)
	{
		Initialize(count,(byte)val);
	}
	
	public static byte[] resize(byte[] vec, int size)
	{
		byte[] out = new byte[size];
		
		int n = Math.min(vec.length, size);
		for (int i=0;i<n;i++)
			out[i] = vec[i];
		
		return out;
	}
	
	public void add(byte val)
	{
		if (data.length==0) data = new byte[10];
		else if (this.size==data.length)	data = ByteVector.resize(data,data.length*2);
		
		data[size] = val;
		size++;
	}
	
	public void add(int val)
	{
		this.add((byte)val);
	}
	
	public void add(String val)
	{
		this.add(Byte.valueOf(val));
	}
	
	public synchronized void addAll(byte[] vals)
	{
		if (data.length<this.size+vals.length) data = ByteVector.resize(data,data.length+vals.length);
		
		for (Byte d : vals)
			this.add(d);
	}
	
	public synchronized void addAll(Collection<Byte> vals)
	{
		if (data.length<this.size+vals.size()) data = ByteVector.resize(data,data.length+vals.size());
		
		for (Byte d : vals)
			this.add(d);
	}
	
	public synchronized void addAll(ByteVector vals)
	{
		if (data.length<this.size+vals.size()) data = ByteVector.resize(data,data.length+vals.size());
		
		for (int i=0;i<vals.size;i++)
			this.add(vals.get(i));
	}
	
	public void addFromFile(String fileName, boolean arerownames, boolean arecolumnnames)
	{
		ByteVector other = new ByteVector(fileName, arerownames, arecolumnnames);
		this.addAll(other);
	}
	
	public void add(byte Byte, String name)
	{
		this.add(Byte);
		addElementName(name);
	}
	
	protected Object getDataAsObject()
	{
		return data;
	}
	
	public Object getAsObject(int i)
	{
		return data[i];
	}
	
	public String getAsString(int i)
	{
		return Byte.toString(data[i]);
	}
	
	public double getAsDouble(int i)
	{
		return get(i);
	}
	
	public boolean getAsBoolean(int i)
	{
		return data[i]==1;
	}
	
	public byte getAsByte(int i)
	{
		return data[i];
	}
	
	public float getAsFloat(int i)
	{
		return data[i];
	}
	
	public int getAsInteger(int i)
	{
		return data[i];
	}
	
	public byte get(int i)
	{
		return data[i];
	}
	
	public byte get(String element)
	{
		return data[getElementNames().indexOf(element)];
	}
	
	public void set(int i, byte val)
	{
		data[i] = val;
	}
	
	public void set(int i, int val)
	{
		data[i] = (byte)val;
	}
	
	public void set(List<Integer> indices, byte val)
	{
		for(Integer index : indices)
			data[index] = val;
	}
	
	public void set(String element, byte val)
	{
		data[getElementNames().indexOf(element)] = val;
	}
	
	public void set(int i, String val)
	{
		data[i] = Byte.valueOf(val);
	}
		
	public double getEmpiricalPvalue(double score, boolean upperTail)
	{
		if (this.size()==0) return Double.NaN;
		
		int gtoe = -1;
		
		if (upperTail) gtoe = this.greaterThanOrEqual(score).sum();
		else gtoe = this.lessThanOrEqual(score).sum();
		
		double pval = (double)gtoe/this.size();
		
		double min = 1.0/this.size();
		if (pval<min) pval = min;
		
		return pval;
	}
	
	public double getEmpiricalValueFromSortedDist (double score)
	{
		int count;
		for(count=0; count<this.size(); count++)
		{
			if(score<this.get(count))
				break;
		}
		return (count==(this.size()) ? (1/(double)this.size()) : (1.0-((double)(count)/this.size())));
	}
	
	public ByteVector clone()
	{
		ByteVector copy = new ByteVector(ByteVector.copy(this.data));
		
		if (this.hasListName()) copy.setListName(this.getListName());
		if (this.hasElementNames())	copy.setElementNames(this.getElementNames());
				
		return copy;
	}
	
	public static byte[] copy(byte[] data)
	{
		byte[] out = new byte[data.length];
		for (int i=0;i<data.length;i++)
			out[i] = data[i];
		
		return out;
	}
	
	public byte[] getData()
	{
		return data;
	}
	
	public int size()
	{
		return size;
	}
	
	public ByteVector reZero(byte zero)
	{	
		ByteVector out = this.clone();
		
		for (int i=0;i<data.length;i++)
			if (out.get(i)==0) out.set(i, zero);
		
		return out;
	}
	
	public ByteVector reOne(byte one)
	{
		ByteVector out = this.clone();
		
		for (int i=0;i<data.length;i++)
			if (out.get(i)==1) out.set(i, one);
		
		return out;
	}
	
	public ByteVector subtract(ByteVector data2)
	{
		ByteVector out = this.clone();
		
		if (data2.size()!=size())
		{
			System.out.println("Error Subtract: Vectors must be the same size.");
			System.exit(0);
		}
		
		for (int i=0;i<data.length;i++)
			out.set(i, out.get(i)-data2.get(i));
		
		return out;
	}
	
	public ByteVector plus(ByteVector data2)
	{
		if (data2.size()!=size())
		{
			System.out.println("Error Subtract: Vectors must be the same size.");
			System.exit(0);
		}
		
		ByteVector out = this.clone();
		
		for (int i=0;i<data.length;i++)
			out.set(i, out.get(i)+data2.get(i));
		
		return out;
	}
	
	public ByteVector minus(ByteVector data2)
	{
		if (data2.size()!=size())
		{
			System.out.println("Error Subtract: Vectors must be the same size.");
			System.exit(0);
		}
		
		ByteVector out = this.clone();
		
		for (int i=0;i<data.length;i++)
			out.set(i, out.get(i)-data2.get(i));
		
		return out;
	}
	
	
	public int max()
	{
		if (size()==0) return -1;
		
		int max = data[0];
		for (int i=1;i<data.length;i++)
			if (data[i]>max) max = data[i];
		return max;
	}
	
	public int maxI()
	{
		int max = data[0];
		int index = 0;
		
		for (int i=1;i<data.length;i++)
		{
			if (data[i]>max)
			{
				max = data[i];
				index = i;
			}
		}
		
		return index;
	}
		
	public IntVector maxIs(int num)
	{
		IntVector maxes = new IntVector(num);
		
		while (maxes.size()<num)
		{
			double max = Double.NaN;
			int maxI = -1;
			
			for (int i=0;i<this.size();i++)
			{
				if (maxes.contains(i)) continue;
				
				if (this.get(i)>max || Double.isNaN(max))
				{
					maxI = i;
					max = this.get(i);
				}
			}
			
			maxes.add(maxI);
		}
			
		return maxes;
	}
	
	public IntVector minIs(int num)
	{
		IntVector mins = new IntVector(num);
		
		while (mins.size()<num)
		{
			double min = Double.NaN;
			int minI = -1;
			
			for (int i=0;i<this.size();i++)
			{
				if (mins.contains(i)) continue;
				
				if (this.get(i)<min || Double.isNaN(min))
				{
					minI = i;
					min = this.get(i);
				}
			}
			
			mins.add(minI);
		}
			
		return mins;
	}
	
	public int minI()
	{
		double min = data[0];
		int index = 0;
		
		for (int i=0;i<data.length;i++)
		{
			if (!Double.isNaN(data[i]) && data[i]<min)
			{
				min = data[i];
				index = i;
			}
		}
		
		return index;
	}
	
	public int min()
	{
		if (size()==0) return -1;
		
		int min = data[0];
		for (int i=0;i<data.length;i++)
			if (data[i]<min) min = data[i];
		return min;
	}

	public ByteVector abs()
	{
		ByteVector out = this.clone();
		
		for (int i=0;i<size();i++)
			if (out.get(i)<0) out.set(i, -out.get(i));
		
		return out;
	}
		
	public ByteVector negative()
	{
		ByteVector out = this.clone();
		
		for (int i=0;i<size();i++)
			out.set(i, -out.get(i));
		
		return out;
	}
	
	public ByteVector plus(int val)
	{
		ByteVector out = this.clone();
		
		for (int i=0;i<size();i++)
			out.set(i, out.get(i)+val);
		
		return out;
	}
	
	public ByteVector minus(int val)
	{
		ByteVector out = this.clone();
		
		for (int i=0;i<size();i++)
			out.set(i, out.get(i)-val);
		
		return out;
	}
	
	public ByteVector subtract(int val)
	{
		ByteVector s = this.clone();
		
		for (int i=0;i<size();i++)
			s.set(i, s.get(i)-val);
		
		return s;
	}
	
	public ByteVector divideBy(ByteVector val)
	{
		ByteVector out = this.clone();
		
		for (int i=0;i<size();i++)
			out.set(i, out.get(i)/val.get(i));
		
		return out;
	}
	
	public ByteVector divideBy(int val)
	{
		ByteVector out = this.clone();
		
		for (int i=0;i<size();i++)
			out.set(i, out.get(i)/val);
		
		return out;
	}
	
	public ByteVector times(ByteVector val)
	{
		ByteVector out = this.clone();
		
		for (int i=0;i<size();i++)
			out.set(i, out.get(i)*val.get(i));
		
		return out;
	}
	
	public ByteVector times(int val)
	{
		ByteVector out = this.clone();
		
		for (int i=0;i<size();i++)
			out.set(i, out.get(i)*val);
		
		return out;
	}
	
	public boolean isNaN()
	{
		for (int i=0;i<size();i++)
			if (!Double.isNaN(data[i])) return false;
		
		return true;
	}
	
	public static final double pearsonCorrelation(ByteVector v1, ByteVector v2)
	{
		if (v1.size()!=v2.size())
		{
			System.err.println("Error pearsonCorrelation: Vectors must be the same size.");
			System.exit(0);
		}
		
		org.apache.commons.math.stat.regression.SimpleRegression sr = new org.apache.commons.math.stat.regression.SimpleRegression();
		
		for (int i=0;i<v1.size();i++)
			sr.addData(v1.get(i), v2.get(i));
		
		return sr.getR();
	}
	
	public double mean()
	{
		double sum = 0.0;
		int valcount = 0;
		
		for (int i=0;i<size();i++)
			if (!Double.isNaN(data[i]))
					{
						sum += data[i];
						valcount++;
					}
		
		if (valcount==0) return Double.NaN;
		
		return sum / (valcount);
	}
	
	public double std()
	{
		double avg = mean();
		
		double sum = 0.0;
		int valcount = 0;
		
		for (int i=0;i<size();i++)
			if (!Double.isNaN(get(i)))
					{
						sum += java.lang.Math.pow((get(i) - avg),2);
						valcount++;
					}
		
		if (valcount==0) return Double.NaN;
		
		sum = sum / (valcount-1);
		
		return java.lang.Math.pow(sum,.5);
	}
	
	public ZStat zstat()
	{
		double avg = mean();
		
		double sum = 0.0;
		int valcount = 0;
		
		for (int i=0;i<size();i++)
			if (!Double.isNaN(get(i)))
					{
						sum += java.lang.Math.pow((get(i) - avg),2);
						valcount++;
					}
		
		if (valcount==0) return new ZStat(Double.NaN,Double.NaN);
		
		sum = sum / (valcount-1);
		
		return new ZStat(avg,java.lang.Math.pow(sum,.5));
	}
	
	public int sum()
	{
		int sum = 0;
		int valcount = 0;
		
		for (int i=0;i<size();i++)
			if (!Double.isNaN(data[i]))
					{
						sum += data[i];
						valcount++;
					}
		
		if (valcount==0) return 0;
		
		return sum;
	}

	public static ByteVector join(ByteVector dt1, ByteVector dt2)
	{
		int newsize = dt1.size() + dt2.size();
		
		ByteVector dtout = new ByteVector();
		dtout.Initialize(newsize, (byte)0);
		
		if (dt1.hasElementNames() && dt2.hasElementNames())
		{
			List<String> enames = dt1.getElementNames();
			enames.addAll(dt2.getElementNames());
			dtout.setElementNames(enames);
		}
		
		if (dt1.hasListName()) dtout.setListName(dt1.getListName());
		
		for (int i=0;i<dt1.size();i++)
			dtout.set(i, dt1.get(i));
		
		for (int i=0;i<dt2.size();i++)
			dtout.set(i+dt1.size(), dt2.get(i));
		
		return dtout;
	}
	
	public static ByteVector joinAll(List<ByteVector> dtlist)
	{
		if (dtlist.size()==0) return null;
		if (dtlist.size()==1) return dtlist.get(0);
		
		ByteVector dtout = join(dtlist.get(0), dtlist.get(1));
		
		for (int i=2;i<dtlist.size();i++)
			dtout = join(dtout, dtlist.get(i));
		
		return dtout;
	}
	
	public ByteVector Discretize(List<Double> breaks)
	{
		ByteVector out = this.clone();
		
		for (int i=0;i<size();i++)
			for (int b=0;b<breaks.size();b++)
					if (b==0 && get(i)<=breaks.get(0))
					{
						out.set(i,0);
						break;
					}else if (b==breaks.size()-1 && get(i)>=breaks.get(breaks.size()-1))
					{
						out.set(i,breaks.size());
						break;
					}else if (b!=0 && get(i)>=breaks.get(b-1) && get(i)<=breaks.get(b))
					{
						out.set(i,b);
						break;
					}
		
		return out;
	}
	
	public ByteVector pow(double power)
	{
		ByteVector pdt = this.clone();
		
		for (int i=0;i<data.length;i++)
			pdt.set(i,(int)java.lang.Math.pow(get(i),power));
		
		return pdt;
	}
	
	public List<Byte> asList()
	{
		List<Byte> out = new ArrayList<Byte>(size());
		
		for (int i=0;i<size();i++)
			out.add(get(i));
		
		return out;
	}
	
	public Set<Byte> asIntSet()
	{
		Set<Byte> intset = new HashSet<Byte>(this.size());
		
		for (int i=0;i<this.size();i++)
			intset.add(this.get(i));
		
		return intset;
	}
	
	public ByteVector sample(int samplesize, boolean replace)
	{
		ByteVector mysample = new ByteVector(samplesize);
		
		ByteVector cp = this.clone();
		
		java.util.Random randgen = new java.util.Random();
		
		randgen.setSeed(System.nanoTime());
		
		if (!replace)
		{
			int lsizem1 = cp.size()-1;
			
			for (int i=0;i<samplesize;i++)
			{
				int swapi = lsizem1-randgen.nextInt(cp.size()-i);
				int temp = cp.get(i);
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
	
	public ByteVector subVector(int i1, int size)
	{
		ByteVector out = new ByteVector(size);
		
		int i1s = i1+size;
		
		for (int i=i1;i<i1s;i++)
			out.add(this.get(i));
		
		return out;
	}
		
	public ByteVector get(List<?> indexes)
	{
		ByteVector sub = new ByteVector(indexes.size());
		
		for (int i=0;i<indexes.size();i++)
			sub.add(data[(((Double)(indexes.get(i))).intValue())]);
		
		return sub;
	}
	
	public ByteVector get(int[] indexes)
	{
		ByteVector sub = new ByteVector(indexes.length);
		
		for (int i=0;i<indexes.length;i++)
			sub.add(data[indexes[i]]);
		
		return sub;
	}
	
	public ByteVector get(ArrayByteList indexes)
	{
		ByteVector sub = new ByteVector(indexes.size());
		
		for (int i=0;i<indexes.size();i++)
			sub.add(data[indexes.get(i)]);
		
		return sub;
	}
	
	public ByteVector get(ByteVector indexes)
	{
		ByteVector sub = new ByteVector(indexes.size());
		
		for (int i=0;i<indexes.size();i++)
			sub.add(data[indexes.get(i)]);
		
		return sub;
	}
	
	public ByteVector get(BooleanVector bv)
	{
		if (bv.size()!=this.size())
		{
			System.err.println("Error DoubleVector.get(BooleanVector): The two vectors must be the same size.");
			System.err.println("this.size = "+this.size()+", bvsize = "+bv.size());
			System.exit(0);
		}
		
		ByteVector sub = new ByteVector();
		
		boolean found = false;
		if (this.elementnames!=null)
		{
			sub.setElementNames(new ArrayList<String>());
			
			for (int i=0;i<size();i++)
				if (bv.get(i))
				{
					sub.add(this.get(i),this.getElementName(i));
					found = true;
				}
		}else
		{
			for (int i=0;i<size();i++)
				if (bv.get(i))
				{
					sub.add(this.get(i));
					found = true;
				}
		}
		
		if (!found) sub.removeElementNames();
		
		return sub;
	}
	
	public static double diffSum(ByteVector v1, ByteVector v2)
	{
		if (v1.size()!=v2.size())
		{
			System.out.println("diffSum Error: v1, v2 are not the same size.");
			System.exit(0);
		}
		
		double ds = 0.0;
		
		for (int i=0;i<v1.size();i++)
			ds += java.lang.Math.abs(v1.get(i)-v2.get(i));
		
		return ds;
	}
	
	public static BooleanVector difference(ByteVector v1, ByteVector v2)
	{
		if(v1.size()!=v2.size())
		{
			System.out.println("The two double vectors must be of the same size to perform this operation");
			System.exit(0);
		}
		
		BooleanVector diff = new BooleanVector(v1.size());
		diff.Initialize(v1.size(), true);
		
		for(int i=0; i<v1.size(); i++)
			if(v1.get(i)!=v2.get(i))
				diff.set(i, true);
			else
				diff.set(i, false);
		
		return diff;
	}
	
	public static BooleanVector similarity(ByteVector v1, ByteVector v2)
	{
		if(v1.size()!=v2.size())
		{
			System.out.println("The two double vectors must be of the same size to perform this operation");
			System.exit(0);
		}
		
		BooleanVector similar = new BooleanVector(v1.size());
		
		for(int i=0; i<v1.size(); i++)
			if(v1.get(i)!=v2.get(i))
				similar.set(i, false);
			else
				similar.set(i, true);
		
		return similar;
	}
	
	public double squaredMean()
	{
		return this.pow(2.0).mean();
	}
	
	public ByteVector diff1()
	{
		ByteVector out = this.clone();
		
		out.set(0, get(1)-get(0));
		
		for (int i=1;i<size()-1;i++)
			out.set(i, (get(i+1)-get(i-1))/2);
		
		out.set(out.size()-1, get(out.size()-1)-get(out.size()-2));
		
		return out;
	}
	
		
	public BooleanVector isReal()
	{
		BooleanVector out = new BooleanVector(size());
		
		for (int i=0;i<size();i++)
			out.add(!(Double.isNaN(get(i)) || Double.isInfinite(get(i))));
		
		return out;
	}
	
	public ByteVector sort()
	{
		ByteVector out = this.clone();
		Sorter.Sort_I(out);
				
		return out;
	}
	
	public IntVector sort_I()
	{
		return Sorter.Sort_I(this.clone());
	}
	
	public void set(BooleanVector bv, int val)
	{
		for (int i=0;i<size();i++)
			if (bv.get(i)) set(i,val);
	}
	
	public void set(ArrayByteList indexes, ByteVector vals)
	{
		vals = vals.clone();
		
		for (int i=0;i<indexes.size();i++)
			this.set(indexes.get(i),vals.get(i));
	}
	
	public void replace(int oldval, int newval)
	{
		if (Double.isNaN(oldval))
		{
			for (int i=0;i<size();i++)
				if (Double.isNaN(get(i))) set(i,newval);
		}
		else
		{
			for (int i=0;i<size();i++)
				if (get(i)==oldval) set(i,newval);
		}
	}
	
	public ByteVector permutation()
	{
		ByteVector perm = this.clone();
		
		java.util.Random r = new java.util.Random();
			
		for (int i=0;i<perm.size();i++)
		{
			int other = r.nextInt(perm.size());
			
			int temp = perm.get(i);
			perm.set(i,perm.get(other));
			perm.set(other,temp);
		}
		
		return perm;
	}
	
	public ByteVector tabulate()
	{
		com.google.common.collect.Multiset<Byte> ms = new com.google.common.collect.HashMultiset<Byte>(this.asList());
		
		ByteVector vals = (new ByteVector(ms.elementSet())).sort();
		
		ByteVector outbin = new ByteVector(vals.size());
		
		for (int i=0;i<vals.size();i++)
			outbin.add((byte)ms.count(vals.get(i)));
		
		return outbin;
	}
	
	public ByteVector cumSum()
	{
		if (this.size()==0) return new ByteVector(0);
		
		ByteVector out = new ByteVector(this.size());
		
		out.add(this.get(0));
		
		for (int i=1;i<this.size();i++)
			out.add((byte)(this.get(i)+out.get(i-1)));
		
		return out;
	}
	
	public ByteVector cumSum(ArrayByteList order)
	{
		if (this.size()==0) return new ByteVector(0);
		
		ByteVector out = new ByteVector(this.size(),(byte)0);
		
		int sum = 0;
		
		for (int i=0;i<order.size();i++)
		{
			sum+=this.get(order.get(i));
			out.set(order.get(i),sum);
		}
		
		return out;
	}
	
	public BooleanVector isEqual(int val)
	{
		BooleanVector out = new BooleanVector(size());
		
		for (int i=0;i<size();i++)
			out.add(get(i)==val);
		
		return out;
	}
	
	public int indexOf(byte val)
	{
		for (int i=0;i<data.length;i++)
			if (data[i]==val) return i;
		return -1;
	}
	
	public ByteVector reverse()
	{
		ByteVector out = new ByteVector(this.size());
		
		for (int i=this.size()-1;i>=0;i--)
			out.add(this.get(i));
		
		return out;
	}
	
	public void plothist(int numbins)
	{
		Plotter pl = new Plotter(new Histogram(this,numbins));
		if (this.hasListName()) pl.setTitle("Histogram of "+this.listname);
		pl.show();
	}
	
	public boolean allEqualTo(double val)
	{
		for (int i=0;i<size();i++)
			if(get(i)!=val) return false;
		
		return true;
	}
	
	public boolean allEqualTo(float val)
	{
		for (int i=0;i<size();i++)
			if(get(i)!=val) return false;
		
		return true;
	}
	
	public boolean allEqualTo(int val)
	{
		for (int i=0;i<size();i++)
			if(get(i)!=val) return false;
		
		return true;
	}
	
	public boolean allEqualTo(byte val)
	{
		for (int i=0;i<size();i++)
			if(get(i)!=val) return false;
		
		return true;
	}
	
	public boolean noneEqualTo(double val)
	{
		for (int i=0;i<size();i++)
			if(get(i)==val) return false;
		
		return true;
	}
	
	public boolean noneEqualTo(float val)
	{
		for (int i=0;i<size();i++)
			if(get(i)==val) return false;
		
		return true;
	}
	
	public boolean noneEqualTo(int val)
	{
		for (int i=0;i<size();i++)
			if(get(i)==val) return false;
		
		return true;
	}
	
	public boolean noneEqualTo(byte val)
	{
		for (int i=0;i<size();i++)
			if(get(i)==val) return false;
		
		return true;
	}
	
	public boolean anyEqualTo(double val)
	{
		for (int i=0;i<size();i++)
			if(get(i)==val) return true;
		
		return false;
	}
	
	public boolean anyEqualTo(float val)
	{
		for (int i=0;i<size();i++)
			if(get(i)==val) return true;
		
		return false;
	}
	
	public boolean anyEqualTo(int val)
	{
		for (int i=0;i<size();i++)
			if(get(i)==val) return true;
		
		return false;
	}
	
	public boolean anyEqualTo(byte val)
	{
		for (int i=0;i<size();i++)
			if(get(i)==val) return true;
		
		return false;
	}
	
	public boolean allLessThan(double val)
	{
		for (int i=0;i<size();i++)
			if(get(i)>=val) return false;
		
		return true;
	}
	
	public boolean allLessThan(float val)
	{
		for (int i=0;i<size();i++)
			if(get(i)>=val) return false;
		
		return true;
	}
	
	public boolean allLessThan(int val)
	{
		for (int i=0;i<size();i++)
			if(get(i)>=val) return false;
		
		return true;
	}
	
	public boolean allLessThan(byte val)
	{
		for (int i=0;i<size();i++)
			if(get(i)>=val) return false;
		
		return true;
	}
	
	public boolean allGreaterThan(double val)
	{
		for (int i=0;i<size();i++)
			if(get(i)<=val) return false;
		
		return true;
	}
	
	public boolean allGreaterThan(float val)
	{
		for (int i=0;i<size();i++)
			if(get(i)<=val) return false;
		
		return true;
	}
	
	public boolean allGreaterThan(int val)
	{
		for (int i=0;i<size();i++)
			if(get(i)<=val) return false;
		
		return true;
	}
	
	public boolean allGreaterThan(byte val)
	{
		for (int i=0;i<size();i++)
			if(get(i)<=val) return false;
		
		return true;
	}
	
	public boolean anyLessThan(double val)
	{
		for (int i=0;i<size();i++)
			if(get(i)<val) return true;
		
		return false;
	}
	
	public boolean anyLessThan(float val)
	{
		for (int i=0;i<size();i++)
			if(get(i)<val) return true;
		
		return false;
	}
	
	public boolean anyLessThan(int val)
	{
		for (int i=0;i<size();i++)
			if(get(i)<val) return true;
		
		return false;
	}
	
	public boolean anyLessThan(byte val)
	{
		for (int i=0;i<size();i++)
			if(get(i)<val) return true;
		
		return false;
	}
	
	public boolean anyGreaterThan(double val)
	{
		for (int i=0;i<size();i++)
			if(get(i)>val) return true;
		
		return false;
	}
	
	public boolean anyGreaterThan(float val)
	{
		for (int i=0;i<size();i++)
			if(get(i)>val) return true;
		
		return false;
	}
	
	public boolean anyGreaterThan(int val)
	{
		for (int i=0;i<size();i++)
			if(get(i)>val) return true;
		
		return false;
	}
	
	public boolean anyGreaterThan(byte val)
	{
		for (int i=0;i<size();i++)
			if(get(i)>val) return true;
		
		return false;
	}
	
	public BooleanVector equalTo(double val)
	{
		BooleanVector out = new BooleanVector(size());
		
		for (int i=0;i<size();i++)
			out.add(get(i)==val);
		
		return out;
	}
	
	public BooleanVector equalTo(int val)
	{
		BooleanVector out = new BooleanVector(size());
		
		for (int i=0;i<size();i++)
			out.add(get(i)==val);
		
		return out;
	}
	
	public BooleanVector equalTo(float val)
	{
		BooleanVector out = new BooleanVector(size());
		
		for (int i=0;i<size();i++)
			out.add(get(i)==val);
		
		return out;
	}
	
	public BooleanVector equalTo(byte val)
	{
		BooleanVector out = new BooleanVector(size());
		
		for (int i=0;i<size();i++)
			out.add(get(i)==val);
		
		return out;
	}
	
	public BooleanVector notEqualTo(double val)
	{
		BooleanVector out = new BooleanVector(size());
		
		for (int i=0;i<size();i++)
			out.add(get(i)!=val);
		
		return out;
	}
	
	public BooleanVector notEqualTo(int val)
	{
		BooleanVector out = new BooleanVector(size());
		
		for (int i=0;i<size();i++)
			out.add(get(i)!=val);
		
		return out;
	}
	
	public BooleanVector notEqualTo(float val)
	{
		BooleanVector out = new BooleanVector(size());
		
		for (int i=0;i<size();i++)
			out.add(get(i)!=val);
		
		return out;
	}
	
	public BooleanVector notEqualTo(byte val)
	{
		BooleanVector out = new BooleanVector(size());
		
		for (int i=0;i<size();i++)
			out.add(get(i)!=val);
		
		return out;
	}
	
	public BooleanVector lessThanOrEqual(float val)
	{
		BooleanVector out = new BooleanVector(size());
		
		for (int i=0;i<size();i++)
			out.add(get(i)<=val);
		
		return out;
	}
	
	public BooleanVector lessThanOrEqual(int val)
	{
		BooleanVector out = new BooleanVector(size());
		
		for (int i=0;i<size();i++)
			out.add(get(i)<=val);
		
		return out;
	}
	
	public BooleanVector lessThanOrEqual(byte val)
	{
		BooleanVector out = new BooleanVector(size());
		
		for (int i=0;i<size();i++)
			out.add(get(i)<=val);
		
		return out;
	}
	
	public BooleanVector lessThanOrEqual(double val)
	{
		BooleanVector out = new BooleanVector(size());
		
		for (int i=0;i<size();i++)
			out.add(get(i)<=val);
		
		return out;
	}
	
	public BooleanVector greaterThan(double val)
	{
		BooleanVector out = new BooleanVector(size());
		
		for (int i=0;i<size();i++)
			out.add(get(i)>val);
		
		return out;
	}
	
	public BooleanVector greaterThan(int val)
	{
		BooleanVector out = new BooleanVector(size());
		
		for (int i=0;i<size();i++)
			out.add(get(i)>val);
		
		return out;
	}
	
	public BooleanVector greaterThan(float val)
	{
		BooleanVector out = new BooleanVector(size());
		
		for (int i=0;i<size();i++)
			out.add(get(i)>val);
		
		return out;
	}
	
	public BooleanVector greaterThan(byte val)
	{
		BooleanVector out = new BooleanVector(size());
		
		for (int i=0;i<size();i++)
			out.add(get(i)>val);
		
		return out;
	}
	
	public BooleanVector lessThan(double val)
	{
		BooleanVector out = new BooleanVector(size());
		
		for (int i=0;i<size();i++)
			out.add(get(i)<val);
		
		return out;
	}
	
	public BooleanVector lessThan(int val)
	{
		BooleanVector out = new BooleanVector(size());
		
		for (int i=0;i<size();i++)
			out.add(get(i)<val);
		
		return out;
	}
	
	public BooleanVector lessThan(float val)
	{
		BooleanVector out = new BooleanVector(size());
		
		for (int i=0;i<size();i++)
			out.add(get(i)<val);
		
		return out;
	}
	
	public BooleanVector lessThan(byte val)
	{
		BooleanVector out = new BooleanVector(size());
		
		for (int i=0;i<size();i++)
			out.add(get(i)<val);
		
		return out;
	}
	
	public BooleanVector greaterThanOrEqual(double val)
	{
		BooleanVector out = new BooleanVector(size());
		
		for (int i=0;i<size();i++)
			out.add(get(i)>=val);
		
		return out;
	}
	
	public BooleanVector greaterThanOrEqual(int val)
	{
		BooleanVector out = new BooleanVector(size());
		
		for (int i=0;i<size();i++)
			out.add(get(i)>=val);
		
		return out;
	}
	
	public BooleanVector greaterThanOrEqual(float val)
	{
		BooleanVector out = new BooleanVector(size());
		
		for (int i=0;i<size();i++)
			out.add(get(i)>=val);
		
		return out;
	}
	
	public BooleanVector greaterThanOrEqual(byte val)
	{
		BooleanVector out = new BooleanVector(size());
		
		for (int i=0;i<size();i++)
			out.add(get(i)>=val);
		
		return out;
	}

}
