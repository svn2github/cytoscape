package clusterExplorerPlugin;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.Vector;

import net.sourceforge.chart2d.Chart2DProperties;
import net.sourceforge.chart2d.Dataset;
import net.sourceforge.chart2d.GraphChart2DProperties;
import net.sourceforge.chart2d.GraphProperties;
import net.sourceforge.chart2d.LBChart2D;
import net.sourceforge.chart2d.LegendProperties;
import net.sourceforge.chart2d.MultiColorsProperties;
import net.sourceforge.chart2d.Object2DProperties;


public class HistogramPlot extends LBChart2D {
	
	private Vector<Vector<Float>> rawDatas;
	
	private int numClasses;
	
	private float min = Float.MAX_VALUE;
	private float max = Float.MIN_VALUE;
	private float span = 0;
	
	private String xLabel, yLabel, title;
	private String[] labels;
	
	private boolean showTo = false;
	
	private int valuesNr = 0;
	private int numSteps = 0;
	private int currentStep = 0;
	
	private boolean progressGui = false;
	
	private float minUser;
	private float maxUser;
	private boolean logY = false;
	
	public HistogramPlot(Vector<Vector<Float>> datas, int numClasses, String title, String xLabel, String yLabel, String[] labels, boolean showTo, boolean progressGui, boolean logY, float minUser, float maxUser) {
		super();
		
		this.rawDatas = datas;
		this.labels = labels;
		
		this.numClasses = numClasses;
		this.xLabel = xLabel;
		this.yLabel = yLabel;
		this.title = title;
		this.progressGui = progressGui;
		this.minUser = minUser;
		this.maxUser = maxUser;
		
		this.showTo = showTo;
		this.logY = logY;
		
		for (int i = 0; i < datas.size(); i++) {
			this.valuesNr = this.valuesNr + datas.get(i).size();
		}
		this.numSteps = this.valuesNr + 2*datas.size();
		
		if (this.progressGui) { Console.startNewConsoleWindow(0,this.numSteps,"Creating histogram '" + title + "'..."); }
		
		makeHistogram();
		
		if (this.progressGui) { Console.closeWindow(); }
	}
	
	private void makeHistogram() {
		
		FloatComparator fc = new FloatComparator(true);
		for (int k = 0; k < this.rawDatas.size(); k++) {
			Vector<Float> data = this.rawDatas.get(k);
			Collections.sort(data, fc);
			if (this.progressGui) { Console.setBarValue(this.currentStep++); }
		}
		
		assignMinMaxSpan();
		
		String[] labelsAxisLabels = assignAxisLabels();
		
		Dataset dataset = new Dataset (this.rawDatas.size(), this.numClasses, 1);
		
		for (int k = 0; k < this.rawDatas.size(); k++) {
			Vector<Float> data = this.rawDatas.get(k);
			assignHistogram(data, dataset, k);
		}
		
		
		//Configure object properties
	    Object2DProperties object2DProps = new Object2DProperties();
	    object2DProps.setObjectTitleText (this.title);

	    //Configure chart properties
	    Chart2DProperties chart2DProps = new Chart2DProperties();

	    //Configure legend properties
	    LegendProperties legendProps = new LegendProperties();
	    String[] legendLabels = this.labels;
	    legendProps.setLegendLabelsTexts (legendLabels);
	    
	    //Configure graph chart properties
	    GraphChart2DProperties graphChart2DProps = new GraphChart2DProperties();
	    graphChart2DProps.setLabelsAxisLabelsTexts (labelsAxisLabels);
	    graphChart2DProps.setLabelsAxisTitleText (this.xLabel);
	    graphChart2DProps.setNumbersAxisTitleText (this.yLabel);
	    graphChart2DProps.setLabelsAxisTicksAlignment (GraphChart2DProperties.CENTERED);
	    
	    //Configure graph properties
	    GraphProperties graphProps = new GraphProperties();
	    graphProps.setGraphBarsExistence (false);
	    graphProps.setGraphLinesExistence (true);
	    graphProps.setGraphOutlineComponentsExistence (true);
	    graphProps.setGraphAllowComponentAlignment (true);
	    
	    //Configure graph component colors
	    MultiColorsProperties multiColorsProps = new MultiColorsProperties();

	    //Configure chart
	    this.setObject2DProperties (object2DProps);
	    this.setChart2DProperties (chart2DProps);
	    this.setLegendProperties (legendProps);
	    this.setGraphChart2DProperties (graphChart2DProps);
	    this.addGraphProperties (graphProps);
	    this.addDataset (dataset);
	    this.addMultiColorsProperties (multiColorsProps);
		
	    //Optional validation:  Prints debug messages if invalid only.
	    if (!this.validate (false)) this.validate (true);
	    
	}
	
	private void assignHistogram(Vector<Float> data, Dataset dataset, int setNr) {
		
		int[] h  = new int[this.numClasses];
		
		for (int i = 0; i < data.size(); i++) {
			
			if ((data.get(i) >= this.min) && (data.get(i) <= this.max)) {
				double c = ((data.get(i) - this.min) / this.span * this.numClasses);
				int cl = (int) c;
				if (cl == this.numClasses) {
					cl--;
				}
				h[cl] = h[cl] + 1;
			}
			
			if (this.progressGui) { Console.setBarValue(this.currentStep++); }
		}
		for (int i = 0; i < h.length; i++) {
			if (!this.logY) {
				dataset.set (setNr,  i, 0, h[i]);
			} else {
				float dummy = (float) (Math.rint(Math.log10(h[i]+1) * 1000)/1000);
				dataset.set (setNr,  i, 0, dummy);
			}
			
		}
		
	}
	
	
	private String[] assignAxisLabels() {
		
		String[] labelsAxisLabels = new String[numClasses];
		
		float step = this.span / numClasses;
		
		int j = 0;
		float i = this.min;
		for (j=0; j<numClasses; j++) {
			if (this.showTo) {
				labelsAxisLabels[j] = Math.rint(i*1000)/1000 + " to " + (i+step);
			} else {
				labelsAxisLabels[j] = "" + Math.rint(i*1000)/1000;
			}
			i=i+step;
		}
		
		return labelsAxisLabels;
	}
	
	public static Point2D.Float getMinMax(Vector<Vector<Float>> rawDatas) {
		
		FloatComparator fc = new FloatComparator(true);
		for (int k = 0; k < rawDatas.size(); k++) {
			Vector<Float> data = rawDatas.get(k);
			Collections.sort(data, fc);
		}
		
		float min = Float.MAX_VALUE;
		float max = Float.MIN_VALUE;
		for (int k = 0; k < rawDatas.size(); k++) {
			Vector<Float> data = rawDatas.get(k);
			if (data.get(0) < min) {
				min = data.get(0);
			}
			if (data.get(data.size()-1) > max) {
				max = data.get(data.size()-1);
			}
		}
		return new Point2D.Float(min, max);
	}
	
	private void assignMinMaxSpan() {
		
		for (int k = 0; k < this.rawDatas.size(); k++) {
			Vector<Float> data = this.rawDatas.get(k);
			if (data.get(0) < this.min) {
				this.min = data.get(0);
			}
			if (data.get(data.size()-1) > this.max) {
				this.max = data.get(data.size()-1);
			}
			if (this.progressGui) { Console.setBarValue(this.currentStep++); }
		}
		
		if (this.min < this.minUser) {
			this.min = this.minUser;
		}
		if (this.max > this.maxUser) {
			this.max = this.maxUser;
		}
		
		if (this.min > this.minUser) {
			this.minUser = this.min;
		}
		if (this.max < this.maxUser) {
			this.maxUser = this.max;
		}
		
		this.span = this.max - this.min;
		
	}
	
	
}

































