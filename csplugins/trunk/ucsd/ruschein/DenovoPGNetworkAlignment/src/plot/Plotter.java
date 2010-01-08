package plot;

import java.util.*;
import java.util.List;

import data.*;
import java.awt.*;

import org.jfree.chart.annotations.XYAnnotation;

public class Plotter {
	
	private javax.swing.JFrame frame;
	private XYPlotControl xypc;
	private PlotType type;
	
	public Plotter(PlotType type)
	{
		frame = new javax.swing.JFrame();
		frame.setSize(800, 600);
        frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
       
        this.type = type;
        if(type==PlotType.XYLinePlot) xypc = new XYLinePlot();
        else if(type==PlotType.XYScatterPlot) xypc = new XYScatterPlot();
        
        frame.getContentPane().add(xypc);
	}
	
	public Plotter(PlotType type, DoubleVector dv)
	{
		frame = new javax.swing.JFrame(dv.getListName());
		frame.setSize(800, 600);
        frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
    
        this.type = type;
        if(type==PlotType.XYLinePlot) xypc = new XYLinePlot(dv);
        else if(type==PlotType.XYScatterPlot) xypc = new XYScatterPlot(dv);
        
        frame.getContentPane().add(xypc);
	}
	
	public Plotter(PlotType type, IntVector dv)
	{
		frame = new javax.swing.JFrame(dv.getListName());
		frame.setSize(800, 600);
        frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
    
        this.type = type;
        if(type==PlotType.XYLinePlot) xypc = new XYLinePlot(dv);
        else if(type==PlotType.XYScatterPlot) xypc = new XYScatterPlot(dv);
        
        frame.getContentPane().add(xypc);
	}
	
	public Plotter(PlotType type, double[] x, double[] y)
	{
		DoubleVector a = new DoubleVector(x);
		DoubleVector b = new DoubleVector(y);
		frame = new javax.swing.JFrame(b.getListName());
		frame.setSize(800, 600);
        frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
    
        this.type = type;
        if(type==PlotType.XYLinePlot) xypc = new XYLinePlot(a,b);
        else if(type==PlotType.XYScatterPlot) xypc = new XYScatterPlot(a,b);
        
        frame.getContentPane().add(xypc);
	}
	
	public Plotter(PlotType type, DoubleVector x, DoubleVector y)
	{
		frame = new javax.swing.JFrame(y.getListName());
		frame.setSize(800, 600);
        frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
    
        this.type = type;
        if(type==PlotType.XYLinePlot) xypc = new XYLinePlot(x,y);
        else if(type==PlotType.XYScatterPlot) xypc = new XYScatterPlot(x,y);
        
        frame.getContentPane().add(xypc);
	}
	
	public Plotter(PlotType type, List<DoubleVector> x, List<DoubleVector> y, StringVector categories)
	{
		frame = new javax.swing.JFrame("");
		frame.setSize(800, 600);
        frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
    
        this.type = type;
        if(type==PlotType.XYLinePlot) xypc = new XYLinePlot(x,y,categories);
        else if(type==PlotType.XYScatterPlot) xypc = new XYScatterPlot(x,y,categories);
        
        frame.getContentPane().add(xypc);
	}
	
	public Plotter(PlotType type, DoubleVector x, DoubleVector y, StringVector categories)
	{
		StringVector catList = categories.unique();
		Map<String,Integer> catIndex = catList.hashIndex();
		
		List<DoubleVector> X = new ArrayList<DoubleVector>(catIndex.size());
		List<DoubleVector> Y = new ArrayList<DoubleVector>(catIndex.size());
		
		for (int i=0;i<catIndex.size();i++)
		{
			X.add(new DoubleVector(x.size()));
			Y.add(new DoubleVector(y.size()));
		}
		
		for (int i=0;i<categories.size();i++)
		{
			int cati = catIndex.get(categories.get(i));
			X.get(cati).add(x.get(i));
			Y.get(cati).add(y.get(i));
		}
		
		frame = new javax.swing.JFrame("");
		frame.setSize(800, 600);
        frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
    
        this.type = type;
        if(type==PlotType.XYLinePlot) xypc = new XYLinePlot(X,Y,catList);
        else if(type==PlotType.XYScatterPlot) xypc = new XYScatterPlot(X,Y,catList);
        
        frame.getContentPane().add(xypc);
	}
	
	/*
	public Plotter(PlotType type, DoubleVector x, DoubleVector y, List<Color> colorData)
	{
		frame = new javax.swing.JFrame(y.getListName());
		frame.setSize(800, 600);
        frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
    
        this.type = type;
        if(type==PlotType.XYLinePlot) xypc = new XYLinePlot(x,y);
        else if(type==PlotType.XYScatterPlot) xypc = new XYScatterPlot(x,y);
        
        xypc.setColorData(colorData);
        
        frame.getContentPane().add(xypc);
	}*/
	
	public Plotter(Histogram h)
	{
		frame = new javax.swing.JFrame();
		frame.setSize(800, 600);
        frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
    
        xypc = new XYLinePlot(h);
        frame.getContentPane().add(xypc);
   	}
	
	public Plotter(PlotType type, Histogram h)
	{
		frame = new javax.swing.JFrame();
		frame.setSize(800, 600);
        frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
    
        if(type==PlotType.XYLinePlot) xypc = new XYLinePlot(h);
        else if(type==PlotType.XYBarPlot) xypc = new XYBarPlot(h);
        
        frame.getContentPane().add(xypc);
   	}
	
	public void updateData(Histogram h)
	{
		frame = new javax.swing.JFrame();
		frame.setSize(800, 600);
        frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
    
        xypc = new XYLinePlot(h);
        frame.getContentPane().add(xypc);
	}
	
	public PlotType type()
	{
		return this.type;
	}
	
	public void show()
	{
		frame.setVisible(true);
	}
	
	public void savePNG(String file)
	{
		xypc.savePNG(file);
	}
	
	public void setTitle(String title)
	{
		xypc.setTitle(title);
	}
	
	public void setXlabel(String text)
	{
		xypc.setXlabel(text);
	}
	
	public void setYlabel(String text)
	{
		xypc.setYlabel(text);
	}
	
	public void addLegend(List<String> text)
	{
		xypc.addLegend();
	}
	
	public void addSeries(Histogram h, String name)
	{
		xypc.addSeries(h, name);
	}
	
	public void addSeries(DoubleVector x, DoubleVector y, String name)
	{
		xypc.addSeries(x,y, name);
	}
	
	public void addSeries(DoubleVector Y, String name)
	{
		xypc.addSeries(Y, name);
	}
	
	/**
	 * Sets the series' names.
	 * Note: This is much slower than setting the names during construction.
	 * @param names
	 */
	public void setSeriesNames(List<String> names)
	{
		xypc.setSeriesNames(names);
	}
	
	public static Plotter getPlots(PlotType type, List<DoubleVector> dvl)
	{
		Plotter out = new Plotter(type);
		
		for (int i=0;i<dvl.size();i++)
			out.addSeries(dvl.get(i), "Series"+i);
		
		return out;
	}
	
	public static Plotter getPlots(PlotType type, List<DoubleVector> dvl, List<String> names)
	{
		Plotter out = new Plotter(type);
		
		for (int i=0;i<dvl.size();i++)
			out.addSeries(dvl.get(i), names.get(i));
		
		return out;
	}
	
	public static Plotter getHistPlots(List<DoubleVector> dvl, int numbins)
	{
		Plotter out = new Plotter(PlotType.XYLinePlot);
		
		for (int i=0;i<dvl.size();i++)
			out.addSeries(new Histogram(dvl.get(i),numbins), "Series"+i);
		
		return out;
	}
	
	public static Plotter getHistPlots(List<DoubleVector> dvl, int numbins, List<String> names)
	{
		Plotter out = new Plotter(PlotType.XYLinePlot);
		
		for (int i=0;i<dvl.size();i++)
			out.addSeries(new Histogram(dvl.get(i),numbins), names.get(i));
		
		return out;
	}
	
	public static Plotter getDensityPlots(List<DoubleVector> dvl, int numbins)
	{
		Plotter out = new Plotter(PlotType.XYLinePlot);
		
		for (int i=0;i<dvl.size();i++)
			out.addSeries(new Histogram(dvl.get(i),numbins).density(), "Series"+i);
		
		return out;
	}
	
	public static Plotter getDensityPlots(List<DoubleVector> dvl, int numbins, List<String> names)
	{
		Plotter out = new Plotter(PlotType.XYLinePlot);
		
		for (int i=0;i<dvl.size();i++)
			out.addSeries(new Histogram(dvl.get(i),numbins).density(), names.get(i));
		
		return out;
	}
	
	public void setXLog10(boolean val)
	{
		xypc.setXLog10(val);
	}
	
	public void setYLog10(boolean val)
	{
		xypc.setYLog10(val);
	}
	
	public void addAnnotation(XYAnnotation annot)
	{
		xypc.addAnnotation(annot);
	}
	
	public void setXRange(double low, double high)
	{
		xypc.setXRange(low,high);
	}
	
	public void setYRange(double low, double high)
	{
		xypc.setYRange(low,high);
	}
}
