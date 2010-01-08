package plot;

import java.awt.*;
import java.util.*;
import java.util.List;

import javax.swing.JComponent;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYAnnotation;

import data.DoubleVector;
import data.Histogram;

public abstract class XYPlotControl extends JComponent
{
	protected String title="";
	protected String xlabel="";
	protected String ylabel="";
	protected JFreeChart chart;
	protected java.awt.geom.Rectangle2D chartArea;
	protected MyXYDataset dataset=new MyXYDataset();
	//protected List<Color> colorData;
	protected boolean initialized=false;
	protected boolean xlog10 = false;
	protected boolean ylog10 = false;
	protected List<XYAnnotation> annotations = new ArrayList<XYAnnotation>();
	
	protected boolean hasXRange = false;
	protected double xlow;
	protected double xhigh;
	
	protected double ylow;
	protected double yhigh;
	protected boolean hasYRange = false;

	public void addSeries(Histogram h, String name)
	{
		DoubleVector x = h.getBinMids();
		DoubleVector y = h.getBinCounts();
		this.addSeries(x, y, name);
	}
	
	public void addSeries(DoubleVector x, DoubleVector y, String name)
	{
		dataset.addSeries(x, y, name);
		
		initialized = false;
	}
	
	public void addSeries(DoubleVector Y, String name)
	{
		DoubleVector y = Y.clone().real();
		
		DoubleVector x = DoubleVector.getScale(0, y.size()-1, 1.0);
		
		dataset.addSeries(x, y, name);
		
		initialized = false;
	}
	
	/*
	public void setColorData(List<Color> colorData)
	{
		this.colorData = colorData;
	}*/
	
	/**
	 * Sets the series's names.
	 * @param names
	 */
	public void setSeriesNames(List<String> names)
	{
		if (dataset.size()!=names.size())
		{
			System.err.println("Error: XYPlotControl.setSeriesNames(List<String> names): names list length does not match series count.");
			System.err.println("Names: "+names.size());
			System.err.println("Series: "+dataset.size());
			System.exit(0);
		}
		
		for (int i=0;i<names.size();i++)
			dataset.setSeriesName(i, names.get(i));
				
		initialized = false;
	}
	
	protected abstract void Initialize();
	
	public void paint(Graphics g)
	{
		if (!initialized) Initialize();
		
		chart.draw((Graphics2D)g, chartArea);
    }
	
	public java.awt.image.BufferedImage getBufferedImage()
	{
		return chart.createBufferedImage(new Double(chartArea.getWidth()).intValue()+1, new Double(chartArea.getHeight()).intValue()+1);
	}
	
	public void setTitle(String text)
	{
		this.title = text;
		initialized = false;
	}
	
	public void setXlabel(String text)
	{
		xlabel = text;
		initialized = false;
	}
	
	public void setYlabel(String text)
	{
		ylabel = text;
		initialized = false;
	}
	
	public void setXLog10(boolean val)
	{
		xlog10 = val;
		initialized = false;
	}
	
	public void setYLog10(boolean val)
	{
		ylog10 = val;
		initialized = false;
	}
	
	public void addAnnotation(XYAnnotation annot)
	{
		annotations.add(annot);
	}
	
	public void addLegend()
	{
		//LegendItemCollection lic = new LegendItemCollection();
		
		//lic.add(new LegendItem("Series1", "-", null, null, Plot.DEFAULT_LEGEND_ITEM_BOX, Color.blue));
		
		/*
		CategoryPlot pl = new CategoryPlot();
		pl.setFixedLegendItems(lic);
		
		chart.getPlot().se
		*/
		
		//LegendTitle lt = new LegendTitle(chart.getCategoryPlot());
		
		//chart.addLegend(lt);
		
		//chart.getXYPlot().setFixedLegendItems(lic);
		
		//Plot().getLegendItems().addAll(lic);
		
		//chart.getPlot().setOutlineStroke(java.awt.new org.jfree.chart.StrokeMap.);
		//org.jfree.chart.StrokeMap
		
	}
	
	public void savePNG(String file)
	{
		if (!initialized) Initialize();
		
		try
		{
			javax.imageio.ImageIO.write(this.getBufferedImage(), "png", new java.io.File(file));
		}catch (Exception e)
		{
			System.out.println(e.getMessage());
			System.out.println("Error XYPlotControl.savePNG(String): Couldn't save plot to png.");
			System.exit(0);
		}
	}
	
	public synchronized void setXRange(double low, double high)
	{
		this.xlow = low;
		this.xhigh = high;
		this.hasXRange = true;
	}
	
	public synchronized void setYRange(double low, double high)
	{
		this.ylow = low;
		this.yhigh = high;
		this.hasYRange = true;
	}
}


