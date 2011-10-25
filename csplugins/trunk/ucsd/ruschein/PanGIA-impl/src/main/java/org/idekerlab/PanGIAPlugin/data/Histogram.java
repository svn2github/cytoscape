package org.idekerlab.PanGIAPlugin.data;

public class Histogram {

	private DoubleVector bincount;
	private DoubleVector leftbin;
	private double binsize;
	private double low;
	private double high;
	
	public Histogram()
	{
		
	}
	
	public Histogram(Histogram h)
	{
		this.bincount = h.bincount.clone();
		this.leftbin = h.leftbin.clone();
		this.binsize = h.binsize;
		this.low = h.low;
		this.high = h.high;
	}
	
	public Histogram(ByteVector data, int numbins)
	{
		bincount = new DoubleVector(numbins,0);
		
		low = data.min();
		high = data.max();
		binsize = (high-low)/numbins;
		
		setBins(data.asDoubleVector(),numbins);
	}
	
	public Histogram(IntVector data, int numbins)
	{
		bincount = new DoubleVector(numbins,0);
					
		low = data.min();
		high = data.max();
		binsize = (high-low)/numbins;
		
		setBins(data.asDoubleVector(),numbins);
	}
	
	public Histogram(DoubleVector data, int numbins)
	{
		bincount = new DoubleVector(numbins,0);
					
		low = data.min(false);
		high = data.max(false);
		
		if (low==high)
		{
			System.err.println("Error Histogram(DoubleVector,int): Cannot infer bounds on a single peak.");
			System.exit(0);
		}
		
		binsize = (high-low)/numbins;
				
		setBins(data,numbins);
	}
	
	public Histogram(DoubleVector data, DoubleVector bounds)
	{
		int numbins = bounds.size()-1;
		bincount = new DoubleVector(numbins,0);
					
		low = bounds.get(0);
		high = bounds.get(numbins);
		binsize = (high-low)/numbins;
		
		setBins(data,numbins);
	}
	
	public Histogram(DoubleVector data, int numbins, double low, double high)
	{
		if (data.size()==0) return;
		
		this.low = low;
		this.high = high;
		binsize = (high-low)/numbins;
		
		setBins(data,numbins);
	}
	
	private void setBins(DoubleVector data, int numbins)
	{
		double eta = (numbins+1)*Double.MIN_VALUE;
		leftbin = DoubleVector.getScale(low, low+numbins*binsize, binsize);
		if (leftbin.size()>numbins) leftbin = new DoubleVector(DoubleVector.resize(leftbin.getData(), leftbin.size()-1));
		
		bincount = new DoubleVector(numbins);
		for (int i=0;i<numbins;i++)
			bincount.add(data.greaterThan(low+i*binsize).and(data.lessThanOrEqual(low+(i+1)*binsize+eta)).sum());
			
		//Correct for get(0) !> min?
		bincount.set(0, bincount.get(0)+data.equalTo(leftbin.get(0)).sum());
	}
	
	public DoubleVector getBinCounts()
	{
		return bincount.clone();
	}
	
	public DoubleVector getBinMids()
	{
		return leftbin.clone().plus(binsize/2);
	}
	
	public void setBinCounts(DoubleVector binCounts)
	{
		if (this.bincount.size()!=binCounts.size())
		{
			System.err.println("Error setBinCounts(DoubleVector): new binCounts is not the same size as the existing binCounts.");
			System.exit(0);
		}
		
		this.bincount = binCounts.clone();
	}
	
	public Histogram clone()
	{
		return new Histogram(this);
	}
	
	public double sum()
	{
		return bincount.sum();
	}
	
	public Histogram density()
	{
		Histogram out = this.clone();
		
		double total = out.sum();
		out.bincount = out.bincount.divideBy(total);
		
		return out;
	}
}
