//Plot2D.java

package csplugins.isb.dante.plot2d;


// TODO - get rid of legacy UI and LegendPanel code

// TODO - Can some public stuff be made private or protected?
// TODO - get tooltips over x ticks with condition names?
// TODO - enable zoom?
// TODO - possible to add to the number of colors used
// 		  so that line colors repeat less frequently? 
// TODO - statusPanel should never need a vertical scroll bar
//(accomplished, but in a kludgey way so leaving this comment here)

// TODO - no scroll bars should appear when frame is constructed w/minimum size
// at least for status panel; for legPanel we can't control this.

// TODO - should Y values appear in plot with same (lesser) precision 
// that they do in the DataCubeBrowser table? And if so how to achieve this?


// TODO - can we "perpetrate" the data so that rows with identical data points
// are offset by some small amount so the user knows there is more than one
// line there. Find out if this is supported in JFreeChart already; if not,
// write the code to do it. You just need to change the Y value by some small amount. 
// But don't let it affect the tooltip content.

// TODO - when live updating is enabled, try and make it so row names 
// always have the same color even if new rows are selected. 

// TODO - do something about long strings as window titles and Y axis names


import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.labels.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.*;
import org.jfree.chart.entity.*;
import org.jfree.data.*;
import org.jfree.ui.*;

import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.awt.geom.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;

import csplugins.trial.pshannon.dataCube.DataMatrixLens;

/**
 * A rewrite of the csplugins.expressionData.Plot2D class that uses 
 * the JFreeChart library instead of Visad and extends functionality.
 * Make sure that the latest jfreechart and jcommon jars are in
 * your classpath. They can be found 
 * <a href="http://www.jfree.org/jfreechart">
 * here</a>. They are also in cvs at $/csplugins/plot2d/lib.
 * 
 * @author Dan Tenenbaum
 */
public class Plot2D extends JFrame  {

  private String[] xLabels;
  private JComboBox cb;
  private String title;
  private String xAxisLabel;
  private String yAxisLabel;
  private ChartPanel chartPanel;
  private XYPlot plot;
  private int maxCols = 0;
  private XYSeriesCollection dataset;
  private JPanel legPanel;
  private JPanel statusPanel;
  private boolean showStatus = true;

  private JLabel lblX = new JLabel("   X:");
  private JLabel lblY = new JLabel("   Y:");
  private JLabel lblRow = new JLabel("   Row:");
  private JLabel lblXAxis = new JLabel();

  private Vector savedPaints;  
  private ItemListSelectionListener ilsl;

  private JScrollPane scrollPane;
  private JScrollPane statusScroll;
  private JSplitPane sPane;
  private JFreeChart chart;
  private JButton dismissButton;

  private static final int UNIT = 20;  
  private static final int MIN_WIDTH = 333;

  private int width;
  private int cbWidth;
  
  private JList itemList;
  
  private boolean allowLiveUpdate = false;
  private int populateCount = 0;
  
  private DataMatrixLens dm = null;
  
  
  
  

  /**
   * Constructs a new Plot2D object with data from a DataMatrix lens,
   * with flags determining whether to show the status panel and whether
   * to allow live updating.
   * 
   * @param title Title of the plot frame
   * @param xAxisLabel Title of the X axis
   * @param yAxisLabel Title of the Y axis
   * @param showStatus Whether to show the status panel - default is <code>true</code>.
   * @param allowLiveUpdate Whether to enable live updating of the panel based on user changes 
   * @param dm The DataMatrix containing the data to plot
   */

public Plot2D (String title, String xAxisLabel, String yAxisLabel,
 boolean showStatus, boolean allowLiveUpdate, DataMatrixLens dm) {
	super(title);
	
	this.title = title;
	this.xAxisLabel = xAxisLabel;
	this.yAxisLabel = yAxisLabel;
	this.showStatus = showStatus;
	this.allowLiveUpdate = allowLiveUpdate;
	this.dm = dm;

	initUI();
	
 }


 /**
  * Constructs a new Plot2D object with data from a DataMatrix lens,
  * with a flag determining whether to show the status panel. Live updating
  * is disabled by default but can be enabled by using a different 
  * constructor.
  * 
  * @param title Title of the plot frame
  * @param xAxisLabel Title of the X axis
  * @param yAxisLabel Title of the Y axis
  * @param showStatus Whether to show the status panel - default is <code>true</code>.
  * @param allowLiveUpdate Whether to enable live updating of the panel based on user changes 
  * @param dm The DataMatrix containing the data to plot
  */
 public Plot2D (String title, String xAxisLabel, String yAxisLabel,
  boolean showStatus, DataMatrixLens dm) {
    this(title, xAxisLabel, yAxisLabel, showStatus, false, dm);
 }



 /**
  * Constructs a new Plot2D object with data from a DataMatrixLens 
  * and the status panel visible by default.
  * 
  * @param title Title of the plot frame
  * @param xAxisLabel Title of the X axis
  * @param yAxisLabel Title of the Y axis
  * @param dm The DataMatrix containing the data to plot
  */

 public Plot2D (String title, String xAxisLabel, String yAxisLabel,
  DataMatrixLens dm) {
	this(title, xAxisLabel, yAxisLabel, true, dm);
 }


/**
 * Constructs a new Plot2D object, ready for data,
 * with a flag determining whether to show the status panel.
 * 
 * @param title Title of the plot frame
 * @param xAxisLabel Title of the X axis
 * @param yAxisLabel Title of the Y axis
 * @param showStatus Whether to show the status panel - default is <code>true</code>.
 */
public Plot2D (String title, String xAxisLabel, String yAxisLabel, 
  boolean showStatus) {
	this(title, xAxisLabel, yAxisLabel, showStatus, null);
} //ctor

/**
 * Constructs a new Plot2D ready for data, with the status panel visible
 * by default.
 *
 * @param title  The title to put in the window title bar
 * @param xAxisLabel  the label for the x-axis
 * @param yAxisLabel  the label for the y-axis
 */
public Plot2D (String title, String xAxisLabel, String yAxisLabel) {
  this(title, xAxisLabel, yAxisLabel, true);
} // ctor



/**
 * 
 * Does the initial UI setup.
 */

private void initUI() {
  
	setSize(300,400);  
	GridBagLayout gbl = new GridBagLayout();
	getContentPane().setLayout(gbl);
	GridBagConstraints c;
	//getContentPane().setLayout(new BorderLayout());
	
	
	itemList = new JList();
	ilsl = new ItemListSelectionListener();
	itemList.addListSelectionListener(ilsl);
	JScrollPane listScroll = new JScrollPane(itemList);
	
	
	
	c = new GridBagConstraints();
	c.gridx = 0;
	c. gridy = 0;
	c.weighty = 1;
	c.weightx = 0;
	c.gridwidth = 1;
	c.gridheight = 6;
	c.fill = c.BOTH;
	getContentPane().add(listScroll, c);

	JButton butClr = new JButton("Clear");
	class ClearAction extends AbstractAction {
	    public ClearAction() {
	    	super("Clear");
	    }
		public void actionPerformed(ActionEvent e) {
			itemList.clearSelection();
			resetColors();
		}
		
	}
	butClr.setAction(new ClearAction());
	

	
	c = new GridBagConstraints();
	c.gridx = 0;
	c.gridy = 7;
	getContentPane().add(butClr, c);
	
	
	
	dataset = new XYSeriesCollection();
	legPanel = new JPanel();
	chart = createChart(dataset);
		
	chartPanel = new ChartPanel(chart);
	chartPanel.setHorizontalZoom(true); // this doesn't enable zoom
	chartPanel.setVerticalZoom(true); // find out what does. or do we want zoom?
	
	Border b = new LineBorder(Color.BLACK,5);
	chartPanel.setBorder(b);
	chartPanel.addChartMouseListener(new ML());
  
	chartPanel.setHorizontalZoom(false);
	chartPanel.setVerticalZoom(false);
	chartPanel.setSize(200,200); 
	chartPanel.setPreferredSize(new Dimension(200, 200)); 
	
	c = new GridBagConstraints();
	c.gridx = 1;
	c.gridy = 0;
	c.gridheight = 4;
	c.gridwidth = 4;
	c.weightx = 1;
	c.weighty = 1;
	c.fill = c.BOTH;
	getContentPane().add(chartPanel, c);
		
	
	JPanel centerPanel = new JPanel();
	JPanel remainderPanel = new JPanel();
	remainderPanel.setLayout(new BorderLayout());
	
	statusPanel = new JPanel();
	Border sb = new LineBorder(Color.BLACK,2);
	centerPanel.setBorder(sb);


	scrollPane = new JScrollPane(legPanel);
	scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);


	// conditionally set up the status panel
	if (showStatus) {
		centerPanel.setLayout(new GridLayout(1,1));
		statusPanel.setLayout(new GridLayout(6,1));

		// make label fonts a little smaller than normal
		Font f = lblX.getFont();
		Font lblFont = new Font(f.getFontName(),f.getStyle(),9);
	
		
		lblX.setFont(lblFont);
		lblY.setFont(lblFont);
		lblRow.setFont(lblFont);
		lblXAxis.setFont(lblFont);				
		
		statusPanel.add(lblX);
		statusPanel.add(lblY);
		statusPanel.add(lblRow);
		lblXAxis.setText("   " + xAxisLabel + ": ");
		statusPanel.add(lblXAxis);
		
		// add blank space at the bottom so we never need a vertical scroll bar
		JLabel spacer1 = new JLabel("");
		statusPanel.add(spacer1);
		JLabel spacer2 = new JLabel("");
		statusPanel.add(spacer2);
		
		statusScroll = new JScrollPane(statusPanel);
		
		/*
		 * The vertical scroll bar *shouldn't* be needed, but on systems with
		 * different display setups, it might. If that happens, comment out the
		 * line below.
		 */
		statusScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

		// set up split pane
		sPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
		  statusScroll, scrollPane);
		sPane.setOneTouchExpandable(true);
		sPane.setContinuousLayout(false);

		centerPanel.add(sPane);
		scrollPane.setPreferredSize(new Dimension(1,30));
	} else {
		centerPanel.add(scrollPane);
		centerPanel.setLayout(new GridLayout(1,2));
		scrollPane.setPreferredSize(new Dimension(this.getWidth()+10,30));
	}

	remainderPanel.add(centerPanel,BorderLayout.CENTER);

	//getContentPane().add (chartPanel, BorderLayout.CENTER);
 
    c = new GridBagConstraints();
    c.gridx = 1;
    c.gridy = 4;
 	c.gridheight = 2;
 	c.gridwidth = 4;
 	c.weighty = 0.33;
 	
	c.fill = c.BOTH;
 
    getContentPane().add(statusScroll, c);
 
	JPanel dismissButtonPanel = new JPanel ();
	dismissButton = new JButton ("OK");
	dismissButton.setActionCommand("dismiss");
	dismissButton.addActionListener (new DismissAction (this));
	dismissButtonPanel.add (dismissButton);
	
	c = new GridBagConstraints();
	c.gridx = 3;
	c.gridy = 7;
	c.fill = c.BOTH;
	getContentPane().add(dismissButton, c);
	
	

	JPanel botPanel = new JPanel();
	botPanel.setLayout(new BorderLayout());
  
	JPanel legendPanel = new JPanel();
  
	cb = new JComboBox();
	c = new GridBagConstraints();
	c.gridx = 1;
	c.gridy = 7;

	getContentPane().add(cb, c);
	
  
	//legendPanel.add(cb);
	
	
	if (null != dm ) {
		JCheckBox cbox = new JCheckBox();
		
		cbox.setSelected(allowLiveUpdate);
		
		cbox.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				JCheckBox checkedBox = (JCheckBox)e.getSource();
				allowLiveUpdate = checkedBox.isSelected();
				/*// start updating right now. (do we want to do this?)
				if (allowLiveUpdate)
				   populateFromLens(dm);
			    */
			}
		});
		JLabel lblLiveUpdate = new JLabel("Enable Live Update");
		Font f = lblLiveUpdate.getFont();
		Font lblFont = new Font(f.getFontName(),f.getStyle(),9);
		lblLiveUpdate.setFont(lblFont);
		
		
		String toolTip = "Checking this box means that the plot will change in " +
		  "real time if the underlying data changes.";
    
		cbox.setToolTipText(toolTip);
		lblLiveUpdate.setToolTipText(toolTip);
		
		JPanel liveUpdatePanel = new JPanel();
		liveUpdatePanel.setLayout(new BorderLayout());
		liveUpdatePanel.add(cbox, BorderLayout.WEST);
		liveUpdatePanel.add(lblLiveUpdate, BorderLayout.CENTER);
		
		liveUpdatePanel.setBorder(new EtchedBorder());
		
		botPanel.add(liveUpdatePanel, BorderLayout.CENTER);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = 7;
		getContentPane().add(liveUpdatePanel, c);
		
	}
    
    
	botPanel.add(legendPanel,BorderLayout.WEST);
	botPanel.add(dismissButtonPanel,BorderLayout.EAST);

	
	remainderPanel.add(botPanel,BorderLayout.SOUTH);
	
	remainderPanel.setMaximumSize(remainderPanel.getSize());
	remainderPanel.setMinimumSize(remainderPanel.getSize());

	//getContentPane().add (remainderPanel, BorderLayout.SOUTH);
	
	/*
	 * Add a component listener to the frame so that the 
	 * line legend table can be properly redrawn if the frame
	 * is resized.
	 */
	addComponentListener(new ComponentAdapter() {
		public void componentResized(ComponentEvent ev) {
			if (width==0)return;
			JFrame fr = (JFrame)ev.getSource();
			
			
			if (fr.getWidth() == width)return; // only height was changed
			
			// return if they didn't resize enough to trigger a repaint of the table
			if (width / UNIT == fr.getWidth() / UNIT) {
				fr.setSize(getSize());
				return;
			}

			// don't let them make it narrower
			if (fr.getWidth() < MIN_WIDTH) {
				fr.setSize(MIN_WIDTH, fr.getHeight());
			}
			
			width = fr.getWidth();
	
			setupLegendPanel(legPanel.getWidth());
			repaint();
		}
	});


	/*
	 * Add a component listener to the legend scroll pane 
	 * so that we can recalculate the table if the split pane
	 * divider is moved.
	 */
	scrollPane.addComponentListener(new ComponentAdapter() {
		public void componentResized(ComponentEvent ev) {
			if (!showStatus)return;
			JScrollPane sp = (JScrollPane)ev.getSource();
			
			setupLegendPanel(sp.getWidth());
			repaint();
		}
	});


} //initUI

/**
 * Adds a listener to the dismiss button.
 * @param al The listener to add.
 */
public void addDismissListener(ActionListener al) {
	dismissButton.addActionListener(al);
} // addDismissListener




/**
 * Sets up the line legend table. Called when frame is first displayed
 * and also every time it is resized.
 *  
 * @param width The current width of the frame, or if it was resized, the new width.
 */
private void setupLegendPanel(int width) {
	
	LegendItemCollection lic = chart.getPlot().getLegendItems();
	if (lic.getItemCount() == 0)return;
	
	legPanel.removeAll();

	JTable tab = new JTable(new MyTableModel(width, lic));
	
	// (sort of) disable selection in the table
	tab.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting())return;
			ListSelectionModel lsm = (ListSelectionModel)e.getSource();
			lsm.clearSelection();
		}
	});
	
	
	
	tab.setDefaultRenderer(Vector.class, new LegendTableCellRenderer(true));
	
	for (int row = 0; row < tab.getColumnCount(); row++) {
		TableColumn column = tab.getColumnModel().getColumn(row);
		column.setPreferredWidth(UNIT);
	}	
	
	tab.setBackground(Color.WHITE);
	tab.setShowGrid(false);

	// Sometimes the table seems to be added twice. Here we make
	// sure that doesn't happen.
	if (legPanel.getComponentCount() > 0)
		legPanel.removeAll();

	legPanel.add(tab);
	legPanel.revalidate();
	repaint();
		
} //setupLegendPanel


/**
 * Simply a pass-through to the plot panel object.
 * @param l 
 */
public void addChartMouseListener(ChartMouseListener l) {
	chartPanel.addChartMouseListener(l);
} // addChartMouseListener


/**
 * The event handler class for the dismiss button. Code unaltered from
 * original Visad version.
 */
public class DismissAction extends AbstractAction {

  private JFrame frame;

  DismissAction (JFrame frame) {super (); this.frame = frame;}

  public void actionPerformed (ActionEvent e) {
	frame.dispose ();
	}

} // DismissAction



/**
 * Takes the name of a row of data and returns the appropriate series.
 * If no series has been created with this name, it creates it, adds it to
 * the dataset, and returns it. Otherwise it returns the existing series
 * with this name.
 * 
 * @param name Name of the series to look for.
 * @return The series with that name.
 */

private UnsortedXYSeries getSeries(String name) {
	
	
	if (dataset.getSeriesCount()==0) {
		UnsortedXYSeries s = new UnsortedXYSeries(name);
		dataset.addSeries(s);
		return s;
	}
	
	for (int i = 0; i < dataset.getSeriesCount(); i++) {
		if (dataset.getSeries(i).getName().equals(name))
			return (UnsortedXYSeries)dataset.getSeries(i);
	}
	UnsortedXYSeries s = new UnsortedXYSeries(name);
	dataset.addSeries(s);
	return s;
} //getSeries



/**
 * Adds x,y points to a series in the dataset. 
 * @param series The series to add to.
 * @param x The list of X points to add. Can be null.
 * @param y The list of Y points to add..
 */
private void addPoints(UnsortedXYSeries series,  double[] x, double[] y) {
	boolean xIsNull = false;
	
	if (null == x) {
		xIsNull= true;
		x = new double[y.length];
	}
	
	for (int i = 0; i < y.length; i++) {
		if (xIsNull) {
			x[i] = i;
		}
		series.add(x[i],y[i]);
	}
} // addPoints


/**
 * Adds a dataset to the plot, specifying only the Y values--the 
 * X values are automatically set to <code>0..y.length</code>. 
 *
 * @param name  the name of the line/row of data
 * @param y  a double array containing the y values
 */
public void addData (String name, double [] y)  {
  
  UnsortedXYSeries s = getSeries(name);
  addPoints(s, null, y);
  

  // Update the X axis combo box
  setupXLabels();

  
} // addData(String, double[])



/**
 * Sets the labels for the X axis. If the array passed to this method
 * has 3 elements and your data has 5 elements, the last two elements 
 * will be labelled "Unknown."
 * 
 * @param xLabels An array containing X axis labels
 */
public void setXLabels (String[] xLabels) {
	this.xLabels = xLabels;
	setupXLabels();
} // setXLabels



/**
 * Just a pass-through to the <code>addData(String, double[])</code> method
 * which strips off the <code>addColoredName</code> parameter no longer needed
 * in this version. Here for backwards compatibility.
 * 
 *
 * @param name  the name of the row/line of data
 * @param y  a double array containing the y values
 * @param addColoredName Does nothing - here for backwards compatibility.
 */
public void addData (String name, double [] y, boolean addColoredName) 
{
  addData(name, y);
  
} // addData


/**
 * Adds arbitrary 2D data to the plot. 
 *
 * @param name  the name associated with this data
 * @param x  a double array containing the x values
 * @param y  a double array containing the y values
 *
 * @throws IndexOutOfBoundsException  if x.length != y.length
 */
public void addData(String name, double[] x, double[] y)  
  throws IndexOutOfBoundsException {
	// This exception handling code unchanged from Visad version.
	if (x.length != y.length) {
		String eString = "Exception: x.length = " + x.length + ", y.length = " + y.length;
		throw new IndexOutOfBoundsException(eString);
	}
    
	UnsortedXYSeries s = getSeries(name);
	addPoints(s,x,y);
    
	
	// This must be called each time we are called.
	setupXLabels();

} // addData (String, double[], double[])


/**
 * Populates the combo box containing the X axis label legend. 
 * If the user has called <code>setXLabels(String[])</code>, those
 * values are used, otherwise the integer value of the X tick
 * and the word "unknown". If there are more X ticks than strings in
 * the label array, the remainder are populated with numeric 
 * (plus "unknown") values.
 * TODO - handle negative X values.
 *
 */
private void setupXLabels() {
	cb.removeAllItems();
	cb.addItem("X axis legend:");
	cbWidth = cb.getWidth();
	long uBound;

	uBound = (long)plot.getDomainAxis().getUpperBound();
	if (uBound > plot.getDomainAxis().getUpperBound()) 
	    uBound--;

	for (long i = 0; i <= uBound; i++) {
		String item = "" + (i);
		try {
			item +=  " - " + xLabels[(int)i];
		} catch (Exception e) {
			item += " - Unknown";
		}
		cb.addItem(item);
	}
} //setupXLabels



/**
 * Creates the JFreeChart object containing the plot. A JFreeChart 
 * example program was pillaged extensively here.
 * 
 * @param dataset The dataset from which the plot will get its data.
 * @return The chart object containing the plot.
 */
private JFreeChart createChart(XYDataset dataset) {
	JFreeChart chart = ChartFactory.createXYLineChart(
		null,
		xAxisLabel, yAxisLabel, dataset,
		PlotOrientation.VERTICAL,
		false,  // legend
		true,  // tooltips
		false  // urls
	);
	plot = chart.getXYPlot();
	
	plot.getDomainAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());

	plot.setRenderer(new CyclicXYItemRenderer(
	  StandardXYItemRenderer.SHAPES_AND_LINES));
	
	// why do i have to do this? CyclicXYItemRenderer inherits from
	// abstract renderer!
	AbstractRenderer ar = (AbstractRenderer)plot.getRenderer();
	
	
	// JFreeChart assumes that (0, 0) is at the center of the shape,
	// so offset the shape by 4 like this to get it to show up
	// in the center of the page. 
	Rectangle2D r = new Rectangle2D.Double(-4.0, -4.0, 10.0, 10.0);
	ar.setShape(r);
		
	// Set up an algorithm for generating tooltips
	plot.getRenderer().setToolTipGenerator(new XYToolTipGenerator(){
		public String generateToolTip(XYDataset data, int series, int item) {
			double  x = ((Double)data.getXValue(series, item)).doubleValue();
			double  y = ((Double)data.getYValue(series, item)).doubleValue();
			String name = data.getSeriesName(series);
			String xName = "";
			
			if ((x % 1) == 0) {
				try {
					xName = ", " + xAxisLabel + "=" + xLabels[(int)x];
				} catch (Exception e) {
					xName = ", " + xAxisLabel + "=Unknown";
				}
			}
			return "x=" +x+", y="+y + ", row=" + name + xName;				
		}
	});

	return chart;
} //createChart

private void resetStatusPanel() {
	lblX.setText("   X:");
	lblY.setText("   Y:");
	lblRow.setText("   Row:");
	lblXAxis.setText("   " + xAxisLabel + ":"); 
}



/**
 * The mouse listener for the Plot2D class. Clients should add their own 
 * listeners; this one is used to implement Plot2D-specific behavior.
 * 
 * @author Dan Tenenbaum 
 */
class ML implements ChartMouseListener {

	/**
	 * Responds to mouse clicks.
	 * 
	 * @param e The ChartMouseEvent generated by the user click.
	 */
	public void chartMouseClicked(ChartMouseEvent e) {
		ChartEntity ent = e.getEntity();
		String shapeType = null;
		
		
		try { // see if the user clicked on a data point or not
			shapeType = ent.getShapeType();
		} catch (NullPointerException ex) {
			// they didn't click on a data point, so reset status panel
			resetStatusPanel();
			return; // they didn't, return.
		}

		// they did click on a valid data point, so populate the status panel
		XYMetaData md =  XYMetaData.parseToolTip(ent.getToolTipText());
		lblX.setText("   X: "+md.getX());
		lblY.setText("   Y: "+md.getY());
		lblRow.setText("   Row: "+md.getRow());
		
		// If the column they clicked on has no name, we'll go to the
		// catch block.
		try {
			lblXAxis.setText("   " + xAxisLabel+": "+md.getXTickName());
		} catch (NullPointerException ex) {;}
	} // chartMouseMoved
		
		
	/**
	 * Responds to mouse motion. Not implemented here.
	 *
	 * @param e The event object generated by the user mouse move. 
	 */
	public void chartMouseMoved(ChartMouseEvent e) {
	} // chartMouseMoved

} // class ML


	
	/**
	 * Overriding setSize method to make sure nobody tries
	 * to set our size to below the minimum width.
	 */
	public void setSize(Dimension size) {
		int width;
		if (size.getWidth() < MIN_WIDTH) {
			width = MIN_WIDTH;
		} else {
			width = (int)size.getWidth();
		}
		super.setSize(width, (int)size.getHeight());
		
	} // setSize(Dimension)
	
	/**
	 * Just a pass through to the other setSize method we are overrriding.
	 */
	public void setSize(int width, int height) {
		setSize(new Dimension(width, height));
	} // setSize(int,int)
	
	
	private void resetColors() {
		XYItemRenderer ren = plot.getRenderer();
		for (int i = 0; i < dataset.getSeriesCount(); i++) {
			ren.setSeriesPaint(i, (Paint)savedPaints.get(i));
		}
	}
	
	
	/**
	 * 
	 *
	 */
	private void setupItemList() {
			itemList.setCellRenderer(new SelectorListRenderer());
			LegendItemCollection lic = plot.getLegendItems();
			Vector v  = new Vector();
			savedPaints = new Vector();
			for (int i = 0; i < lic.getItemCount(); i++) {
				LegendItem li = (LegendItem)lic.get(i);
				v.add(li);
				savedPaints.add(li.getPaint());
				
			}
			itemList.setListData(v);
			
	}
	
	/**
	 * Overriding show method to do some final adjusting to components
	 * before display. The most important thing that happens here is 
	 * the call to <code>setupLegendPanel</code>. 
	 */
	public void show() {
		if (null != dm)
			populateFromLens(dm);
		
		
		setupItemList();
		
		// make a call to the right greying method here (?)

		
		width = getWidth();
		setupLegendPanel(width);
		if (showStatus)
			sPane.setDividerLocation(0.68);
		super.show();
		
		if (showStatus) {
			setupLegendPanel(scrollPane.getWidth()); // need an extra repaint
		}
		
		setSize(getSize()); // causes a repaint?
	} // show
	
		
	
	/**
	 * Populates this Plot2D object with data from a DataMatrixLens object.
	 * @param dm
	 */
	public void populateFromLens(DataMatrixLens dm) {
		if (!allowLiveUpdate) {
			if (populateCount == 0) {
				populateCount++;
			} else {
				return;
			}
		}
		
		this.setTitle(dm.getMatrixName());
		resetStatusPanel();
		dataset.removeAllSeries();
		yAxisLabel = dm.getMatrixName();
		
		if (dm.getSelectedRowCount() == 0)return;
		
		String[] allColTitles = dm.getFilteredColumnTitles();
		
		if (allColTitles.length > dm.getEnabledColumnCount()) {
			String[] colTitles = new String[dm.getEnabledColumnCount()];
			for (int i = 0; i < allColTitles.length; i++) {
				if (i == 0)continue;
				colTitles[i-1] = allColTitles[i];
			}
			setXLabels(colTitles);				
		} else {
			setXLabels(allColTitles);
		}
		
		for (int i = 0; i < dm.getSelectedRowCount(); i++) {
			double[] d = dm.getFromSelected(i);
			addData(dm.getSelectedRowTitles()[i], d);
			// this way works too, but is more cumbersome:
			//addData(dm.getRowTitles()[i], dm.get(dm.getRowTitles()[i]));
		}
		
		chart = createChart(dataset);
		if (this.chartPanel != null) { 
			  this.chartPanel.setChart(this.chart); 
		}


		setupLegendPanel(legPanel.getWidth());

		setupItemList();
		resetColors();


		repaint();
	} //PopulateFromLens
	
	
	/**
	 * Places window in center of screen. This method unchanged from Visad version.
	 *
	 */

	public void placeInCenter () {
	  GraphicsConfiguration gc = getGraphicsConfiguration ();
	  int screenHeight = (int) gc.getBounds().getHeight ();
	  int screenWidth = (int) gc.getBounds().getWidth ();
	  int windowWidth = getWidth ();
	  int windowHeight = getHeight ();
	  setLocation ((screenWidth-windowWidth)/2, (screenHeight-windowHeight)/2);
	
	} // placeInCenter


	/**
	 * The table model for the line legend table. Based on the code example at
	 * <a href="http://java.sun.com/docs/books/tutorial/uiswing/components/example-1dot4/TableDialogEditDemo.java">
	 * http://java.sun.com/docs/books/tutorial/uiswing/components/example-1dot4/TableDialogEditDemo.java</a>.
	 * The constructor is the only method I changed, therefore it's the only one with
	 * my comments.
	 * 
	 * @author Dan Tenenbaum (sort of) 
	 */
	class MyTableModel extends AbstractTableModel {
		
		boolean DEBUG = false;
		private String[] columnNames;

		private Object[][] data;
		
		/**
		 * Construct the table model.
		 * @param frameWidth The width of the frame, or if it's being resized, its new width
		 * @param lic The legend information needed to populate the table
		 */
		public MyTableModel(int frameWidth, LegendItemCollection lic) {
			
			if (frameWidth == 0)return;	


			//System.out.println("in table ctor, w="+frameWidth);

			// calculate the number of columns in the table
			
			int numCols = frameWidth / UNIT;
	
			
			if ((frameWidth % UNIT != 0) && (numCols > 1)) numCols--;
	
			if (numCols > lic.getItemCount())
				numCols = lic.getItemCount();
	
			
			// calculate the number of rows in the table
			int numRows = lic.getItemCount() / numCols;
	
			if (lic.getItemCount() % numCols != 0)numRows++;

			if (lic.getItemCount() > (numRows * numCols))numRows++;

			
			// create an (unused) array of column names
			columnNames = new String[numCols];
			
			data = new Object[numRows][numCols];


			// pre-fill every cell of the table with essentially blank data.
			for (int i = 0; i < numRows; i++) {
				for (int j = 0; j < numCols; j++) {
					Vector v = new Vector();
					v.add(Color.WHITE);
					v.add("");
					data[i][j] = v;
				}
			}
			
			int row = 0;
			int col = 0;

			
			// loop through the legend data and populate the table
			for (int i = 0; i < lic.getItemCount(); i++) {
				
				LegendItem li = lic.get(i);
				/*
				 * The table cell renderer wants a vector containing two items:
				 * 0) The cell background color
				 * 1) The tooltip text for the cell
				 * Construct it here:
				 */
				Vector v = new Vector();
				v.add((Color)li.getPaint());
				v.add(li.getLabel());
				if (lic.getItemCount() == numCols) {
					data[0][i] = v;
				} else {
					data[row][col++] = v;
					if (col % numCols == 0) {
						col = 0;
						row++; 
					}
				}
			}
		}
		
		public int getColumnCount() {
			if (null == columnNames)return 0;
			return columnNames.length;
		}

		public int getRowCount() {
			if (null == data)return 0;
			return data.length;
		}

		public String getColumnName(int col) {
			return columnNames[col];
		}

		public Object getValueAt(int row, int col) {
			return data[row][col];
		}

		/*
		 * JTable uses this method to determine the default renderer/
		 * editor for each cell.  If we didn't implement this method,
		 * then the last column would contain text ("true"/"false"),
		 * rather than a check box.
		 */
		public Class getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}

		/*
		 * This method is commented out because it's not needed for tables
		 * in which no cells are editable. But we might want it later, I suppose,
		 * so I am leaving it in.
		 */
		
		/*
		public boolean isCellEditable(int row, int col) {
			//Note that the data/cell address is constant,
			//no matter where the cell appears onscreen.
			if (col < 1) {
				return false;
			} else {
				return true;
			}
		}
		*/

		public void setValueAt(Object value, int row, int col) {
			if (DEBUG) {
				System.out.println("Setting value at " + row + "," + col
								   + " to " + value
								   + " (an instance of "
								   + value.getClass() + ")");
			}

			data[row][col] = value;
			fireTableCellUpdated(row, col);

			if (DEBUG) {
				System.out.println("New value of data:");
				printDebugData();
			}
		}

		private void printDebugData() {
			int numRows = getRowCount();
			int numCols = getColumnCount();

			for (int i=0; i < numRows; i++) {
				System.out.print("    row " + i + ":");
				for (int j=0; j < numCols; j++) {
					System.out.print("  " + data[i][j]);
				}
				System.out.println();
			}
			System.out.println("--------------------------");
		}
	} // inner class MyTableModel


	class SelectorListRenderer implements ListCellRenderer {
			public Component getListCellRendererComponent(JList list,
			  Object value, int index, boolean isSelected, 
			  boolean cellHasFocus)
			    {
			    	LegendItem li = null;
			    	
			    	if (value instanceof LegendItem) li = (LegendItem) value;
			    	JLabel lbl = new JLabel();
			    	Font f = new Font(list.getFont().getName(),Font.PLAIN,
			    	  list.getFont().getSize()-2);
			    	lbl.setFont(f);
			    	lbl.setOpaque(true);
			    	lbl.setText(li.getLabel());
			    	lbl.setToolTipText(li.getLabel());
			    	
			    	int[] sel = itemList.getSelectedIndices();
			    	
			    	
			    	if (isSelected) {
			    		lbl.setBackground(Color.WHITE);
			    		lbl.setForeground((Color)li.getPaint());
			    	} else {
			    		lbl.setBackground((Color)li.getPaint());
			    	}
			    
			    	
			    	
			    	
			    	return lbl;
			    }
			    
	} // inner class SelectorListRenderer
	
	class ItemListSelectionListener implements ListSelectionListener {
		public void valueChanged (ListSelectionEvent e) {
			
			if (e.getValueIsAdjusting()) {
				return;
			}
			JList src = (JList)e.getSource();
			
			int[] selected = itemList.getSelectedIndices();
			Hashtable selHash = new Hashtable();
			for (int i = 0; i < selected.length; i++) {
				selHash.put(new Integer(selected[i]),"~");
			}
			
			
			
			LegendItemCollection lic = plot.getLegendItems();
			
			XYItemRenderer ren = plot.getRenderer();
			
			Color bgColor = new Color(230,230,230);
			for (int i = 0; i < dataset.getSeriesCount(); i++) {
				try {
					String s = (String)selHash.get(new Integer(i));
					if ("~".equals(s)) {
						ren.setSeriesPaint(i, (Paint)savedPaints.get(i));
					} else {
						ren.setSeriesPaint(i, bgColor);
					}
					
				}
				catch (NullPointerException ex) {
					System.out.println("caught ex");
					//ren.setSeriesPaint(i, (Paint)savedPaints.get(i));
				}
			}
			chartPanel.repaint();
			
			
		}
	} // inner class ItemListSelectionListener


} // class Plot2D

