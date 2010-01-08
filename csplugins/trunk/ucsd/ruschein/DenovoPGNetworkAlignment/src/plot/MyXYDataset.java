package plot;

import java.util.ArrayList;
import java.util.List;

import org.jfree.data.xy.DefaultIntervalXYDataset;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYSeries;

import data.DoubleVector;

public class MyXYDataset {

	private List<MyXYSeries> series = new ArrayList<MyXYSeries>();
	
	public MyXYDataset()
	{
		
	}
	
	public void addSeries(DoubleVector x, DoubleVector y, String name)
	{
		series.add(new MyXYSeries(x,y,name));
	}
	
	public DefaultXYDataset asJDataset()
	{
		DefaultXYDataset dataset = new DefaultXYDataset();
		
		for (int s=0;s<series.size();s++)
		{
			DoubleVector x2 = series.get(s).getX().real();
			DoubleVector y2 = series.get(s).getY().real();
			
			XYSeries s1 = new XYSeries(series.get(s).getName());
			
			for (int i=0;i<x2.size();i++)
	    	   	s1.add(x2.get(i), y2.get(i));
					
			dataset.addSeries(s1.getKey(), s1.toArray());
		}
		
		return dataset;
	}
	
	public DefaultIntervalXYDataset asJIntervalXYDataset()
	{
		DefaultIntervalXYDataset dataset = new DefaultIntervalXYDataset();
		
		for (int s=0;s<series.size();s++)
		{
			DoubleVector x2 = series.get(s).getX().real();
			DoubleVector y2 = series.get(s).getY().real();
			
			/*
			XYSeries s1 = new XYSeries(series.get(s).getName());
			
			for (int i=0;i<x2.size();i++)
	    	   	s1.add(x2.get(i), y2.get(i));
			
			System.out.println(s1.toArray().length);
			*/
			/*
			double[][] old = s1.toArray();
			
			double[][] tr = new double[old[0].length][old.length];
			
			for (int i=0;i<old.length;i++)
				for (int j=0;j<old[i].length;j++)
					tr[j][i] = old[i][j];
			*/
			
			//dataset.addSeries(s1.getKey(), s1.toArray());
			
			
			double[][] data = new double[6][x2.size()];
			
			for (int i=0;i<x2.size();i++)
			{
				data[0][i] = x2.get(i);
				if (i==0) data[1][i] = x2.get(i)-(x2.get(1)-x2.get(0))/2;
				else data[1][i] = x2.get(i)-(x2.get(i)-x2.get(i-1))/2;
				
				if (i==x2.size()-1) data[2][i] = x2.get(i)+(x2.get(x2.size()-1)-x2.get(x2.size()-2))/2;
				else data[2][i] = x2.get(i)+(x2.get(i+1)-x2.get(i))/2;
				
				data[3][i] = y2.get(i);
				data[4][i] = y2.get(i);
				data[5][i] = y2.get(i);
			}
			
			dataset.addSeries(series.get(s).getName(), data);
		}
		
		return dataset;
	}
	
	public int size()
	{
		return series.size();
	}
	
	public void setSeriesName(int index, String name)
	{
		series.get(index).setName(name);
	}
	
	public MyXYDataset clone()
	{
		MyXYDataset out = new MyXYDataset();
		
		for (int s=0;s<this.size();s++)
			out.addSeries(series.get(s).getX(), series.get(s).getY(), series.get(s).getName());
		
		return out;
	}
	
	public void removeXZeros()
	{
		for (int s=0;s<this.size();s++)
			series.get(s).removeXZeros();
	}
	
	public void removeYZeros()
	{
		for (int s=0;s<this.size();s++)
			series.get(s).removeYZeros();
	}
	
	public void removeXNonReal()
	{
		for (int s=0;s<this.size();s++)
			series.get(s).removeXNonReal();
	}
	
	public void removeYNonReal()
	{
		for (int s=0;s<this.size();s++)
			series.get(s).removeYNonReal();
	}
}
