// TestPlotter

package csplugins.isb.dtenenbaum.plot2d.test;

import java.awt.event.*; // need this for the dismiss button listener




//import csplugins.expressionData.Plot2D; // the old way to do it. 
//import csplugins.expressionData.ptpwrapper.*; // the newer, better way.

import csplugins.plot2d.*; // the newest, best way


import org.jfree.chart.*; // need this for the event listener
import org.jfree.chart.entity.*; // this too

  
/**
 * A class to test the capabilities of the extended, JFreeChart-based
 * version of Plot2D and give examples of its usage.
 * 
 * @author Dan Tenenbaum 
 */

public class TestPlotter {

	Plot2D plotter = null;
	private static final String title = "expression profiles";
	private static final String xLabel = "Condition";
	private static final String yLabel = "Expression";

	/**
	 * Need a constructor in order to create a non-static context in which to 
	 * set up an event listener.
	 *
	 */
	public TestPlotter() {
		plotter = new Plot2D(title, xLabel, yLabel, true);
		plotter.addChartMouseListener(new PlotListener());
		plotter.addDismissListener(new AL());
		plotter.setDefaultCloseOperation(plotter.EXIT_ON_CLOSE);
		
	} //ctor
	
	/**
	 * Get the plotter we will use in the sample program.
	 * 
	 * @return an instance of Plot2D for tests.
	 */
	public Plot2D getPlotter() {
		return plotter;
	} // getPlotter
	

	/**
	 * The bulk of the test code is here.
	 * 
	 * @param args Not used.
	 */
	public static void main(String[] args) {

		
		TestPlotter tp = new TestPlotter();
		
		Plot2D plotter = tp.getPlotter();

		
		try {
			
			// Add six rows of data (datasets). X values are implied (0..n)

			// Note that the rows can be of different lengths.
						
			plotter.addData("myGene", new double[] { 3.2232, -9.1, 0.4, 66.71 });
			plotter.addData("yrGene", new double[]{0.211, 5.5, -2.1, 32.767,4.567});
			plotter.addData("hisGene", new double[]{11,12,13,14}, false);
			plotter.addData("herGene", new double[]{5,4,3,2}, true);

			plotter.addData("ourGene", new double[]{19,20,21,22,23}, false);
			plotter.addData("theirGene", new double[]{7,8,9,10}, true);



			/*
			 * You can also add custom data where you specify the 
			 * X values as well as the Y. This can cause lines in 
			 * the plot that don't necessarily move from left to right.
			 * 
			 */
			
			double[] x = new double[] { 6, 3, 5, 4 };
			double[] y = new double[] { 16, 2.1, 6.7, 18.2 };
			plotter.addData("foo", x, y);
			
			/* uncomment these lines to see how
			 * the plotter and line legend handle large
			 * datasets.
			 */ 
			//for (int i = 0; i < 60; i++) {
			//	plotter.addData(""+i, new double[] {i, i+1, i+2, i+3, i+4, i+5});
			//}


		} catch (Exception e) {
			e.printStackTrace(); 
		}

		
		/*
		 * Sets custom labels for X axis ticks. If you set, say 5, 
		 * custom labels, your data can still  contain lines longer than 
		 * 5 points; ticks greater than 5 will be labelled "Unknown".
		 */
		
		plotter.setXLabels(new String[]{"Spellman_alphaT028_vs_async",
										"Spellman_alphaT119_vs_async",
										"Spellman_alphaT091_vs_async",
										"Spellman_alphaT063_vs_async"});

		plotter.placeInCenter();
		plotter.pack();
		//plotter.setSize(20,500); // the plotter will automatically increase this width
		plotter.show();


	} // main
	
	
	/**
	 * A standard mouse listener so we can tell when they clicked Dismiss
	 * and perform a system exit.
	 */
	class AL implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
	} // inner class AL
	
	
	
	/**
	 * Responds when the user clicks a rectangular point on a plotted line,
	 * and returns an event object from which you can get the name of the row
	 * clicked on.
	 */
	class PlotListener implements ChartMouseListener {

		public void chartMouseClicked(ChartMouseEvent e) {
			ChartEntity ent = e.getEntity();
			String shapeType = null;
		
			try { // see if the user clicked on a data point or not
				shapeType = ent.getShapeType();
			} catch (NullPointerException ex) {
				return; // they didn't, return.
			}

			// Parse the tooltip text into a meta data object
			XYMetaData md =  XYMetaData.parseToolTip(ent.getToolTipText());
			// Then print out the various values from that object
			
			// Here is where you could do other stuff, like tell a table or
			// graph what row or node to select.
			
			System.out.println("x="+md.getX());
			System.out.println("y="+md.getY());
			System.out.println("row="+md.getRow());
			System.out.println("xAxisName="+xLabel);
			try {
				System.out.println("xTickName="+md.getXTickName());
			} catch (NullPointerException ex) {;}
				
		
		} // chartMouseClicked
	
	
		/**
		 * Not implemented
		 */
		public void chartMouseMoved(ChartMouseEvent e) {
		} // chartMouseMoved
		
	} // inner class PlotListener
	
} //TestPlotter
