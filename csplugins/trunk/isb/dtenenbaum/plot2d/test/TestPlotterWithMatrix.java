// TestPlotterWithMatrix

package csplugins.isb.dtenenbaum.plot2d.test;

import java.awt.event.*; // need this for the dismiss button listener
import java.io.*; // need for reading matrix from file
import java.net.*;


//import csplugins.expressionData.Plot2D; // the old way to do it. 
//import csplugins.expressionData.ptpwrapper.*; // the newer, better way.

import csplugins.isb.dtenenbaum.plot2d.*; // the newest, best way

import csplugins.isb.pshannon.dataMatrix.*; // needed for DataMatrix
import org.jfree.chart.*; // need this for the event listener
import org.jfree.chart.entity.*; // this too

   
/**
 * A class to test the capabilities of the extended, JFreeChart-based
 * version of Plot2D and give examples of its usage.
 * 
 * @author Dan Tenenbaum 
 */

public class TestPlotterWithMatrix {

	Plot2D plotter = null;
	private static final String title = "expression profiles";
	private static final String xLabel = "Condition";
	private static final String yLabel = "Expression";
	
	DataMatrixReader reader;
	DataMatrix[] matrices;
	DataMatrix matrix;
	DataMatrixLens lens = null;
	

	/**
	 * Need a constructor in order to create a non-static context in which to 
	 * set up an event listener.
	 *
	 */
	public TestPlotterWithMatrix(String[] args) {
		ClassLoader cl = this.getClass().getClassLoader();
		String pkg = this.getClass().getPackage().getName();
		pkg = pkg.replaceAll("\\.","\\/");
		
		//URL url = cl.getResource(pkg+"/simpleMatrix.txt");
		System.out.println("args[0] = " + args[0]);
		URL url = cl.getResource(pkg + "/" + args[0]);
		
		
		// try it with (the first few lines of) Nitin's data
		//URL url = cl.getResource(pkg+"/nitin.data");
		
		
		// TODO -  there is an issue with this matrix for some
		// reason, but it only happens with this test program,
		// so I am not worrying about it yet. BTW, this is just
		// the first few lines of the mflory data.
		//URL url = cl.getResource(pkg+"/matrix.expression");
		
		
		String fileName = url.getFile().replaceAll("^\\/","");
		
		
		reader = new DataMatrixFileReader  ("file://", fileName);
		try {
			reader.read ();
			matrices = reader.get ();
			matrix = matrices [0];
			lens = new DataMatrixLens(matrix);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		plotter = new Plot2D(title, xLabel, yLabel, true, lens);
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

		if (args.length < 1) {
			System.out.println("Supply a filename.");
			System.exit(0);
		}
		
		TestPlotterWithMatrix tp = new TestPlotterWithMatrix(args);
		
		Plot2D plotter = tp.getPlotter();

		

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
	
} //TestPlotterWithMatrix
