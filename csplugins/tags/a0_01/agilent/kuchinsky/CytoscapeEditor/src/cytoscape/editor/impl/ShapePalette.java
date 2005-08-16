/*
 * Created on May 29, 2005
 *
 */
package cytoscape.editor.impl;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.HashMap;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;

import cytoscape.Cytoscape;
import cytoscape.editor.event.BasicCytoShapeTransferHandler;



/**
 * 
 * The <b>ShapePalette</b> class implements a palette from which the user drags and drops shapes onto the canvas
 * The dropping of shapes onto the canvas results in the addition of nodes and edges to the current Cytoscape 
 * network, as defined by the behavior of the event handler that responds to the drop events.  For example, in the
 * simple "BioPAX-like" editor, there are node types for proteins, catalysis, small molecules, and biochemical 
 * reactions, as well as a directed edge type.
 * <p>
 * The user interface for the ShapePalette is built upon the JList class.
 * <p>
 * This functionality is not available in Cytoscape 2.2
 * @author Allan Kuchinsky
 * @version 1.0
 * 
 */
public class ShapePalette extends JPanel 
{
	/**
	 * mapping of shapes to their titles
	 */
	static HashMap _shapeMap = new HashMap ();

	/**
	 * the user interface for the ShapePalette
	 */
	protected JList dataList;
	protected DefaultListModel listModel;
	 
	  
	public ShapePalette() {
		super();
		listModel = new DefaultListModel();
		dataList = new JList (listModel);
		dataList.setCellRenderer(new MyCellRenderer());
		dataList.setDragEnabled(true);
    	dataList.setTransferHandler(new PaletteListTransferHandler());
		this.add(dataList);
		dataList.setBackground(Cytoscape.getDesktop().getBackground());
		this.setBackground(Cytoscape.getDesktop().getBackground());
		this.setVisible(true);		
	}
	
	/**
	 * add a shape to the palette
	 * @param attributeName attribute name for the shape, should be one of "NodeType" or "EdgeType"
	 * @param attributeValue value for the attribute assigned to the shape, for example a "NodeType" of "protein"
	 * @param img the icon for the shape
	 * @param name the title of the shape
	 */
	public void addShape(String attributeName, String attributeValue, ImageIcon img, String name)
	{
		BasicCytoShapeEntity cytoShape = new BasicCytoShapeEntity(attributeName, 
				attributeValue, img, name);
		cytoShape.setTransferHandler(new BasicCytoShapeTransferHandler(cytoShape,
				null));
		_shapeMap.put(cytoShape.getTitle(), cytoShape);
		listModel.addElement(cytoShape);
		Cytoscape.getCurrentNetworkView().redrawGraph(false, true);
	}
	
	/**
	 * setup the palette in the Navigator Panel of Cytoscape
	 * TODO: use CytoPanels?
	 *
	 */
	public void showPalette() {
		Cytoscape.getDesktop().getNetworkPanel().setNavigator(this);

		Rectangle bounds = Cytoscape.getDesktop().getNetworkPanel()
				.getNavigatorPanel().getBounds();
		Cytoscape.getDesktop().getNetworkPanel().getNavigatorPanel()
				.setPreferredSize(
						new Dimension(bounds.width, 3 * bounds.height));
	}	
	

	/**
	 * 
	 * @param key the name of the shape to be returned
	 * @return return the BasicCytoShapeEntity associated with the input shape name
	 */
	public static BasicCytoShapeEntity getBasicCytoShapeEntity(String key)
	{
		Object val = _shapeMap.get(key);
		if (val instanceof BasicCytoShapeEntity)
		{
			return ((BasicCytoShapeEntity) val);
		}
		else
		{
			return null;
		}
	}

	/**
	 * renders each cell of the ShapePalette
	 * @author Allan Kuchinsky
	 * @version 1.0
	 *
	 */
	class MyCellRenderer extends JLabel implements ListCellRenderer {

	     // This is the only method defined by ListCellRenderer.
	     // We just reconfigure the JLabel each time we're called.

	     public Component getListCellRendererComponent(
	       JList list,
	       Object value,            // value to display
	       int index,               // cell index
	       boolean isSelected,      // is the cell selected
	       boolean cellHasFocus)    // the list and the cell have the focus
	     {
	     	if (value instanceof BasicCytoShapeEntity)
	     	{
	     		BasicCytoShapeEntity cytoShape = (BasicCytoShapeEntity) value;
	     		setText(cytoShape.getTitle());
	     		setIcon(cytoShape.getIcon());
	     		setToolTipText(cytoShape.getToolTipText());
	     	    
	     	}

	   	   if (isSelected) {
	             setBackground(list.getSelectionBackground());
		       setForeground(list.getSelectionForeground());
		   }
	         else {
		       setBackground(list.getBackground());
		       setForeground(list.getForeground());
		   }
		   setEnabled(list.isEnabled());
		   setFont(list.getFont());
	         setOpaque(true);
	         return this;
	     }
	 }	
	
	
	/**
	 * bundles up the name of the BasicCytoShapeEntity for export via drag/drop from the palette
	 * @author Allan Kuchinsky
	 * @version 1.0
	 * 
	 */
	 class PaletteListTransferHandler extends StringTransferHandler {
	    private int[] indices = null;
	    private int addIndex = -1; //Location where items were added
	    private int addCount = 0;  //Number of items added.
	     
	    protected  void cleanup(JComponent c, boolean remove) {};
	    protected  void importString(JComponent c, String str) {};
	    //Bundle up the selected items in the list
	    //as a single string, for export.
	    protected String exportString(JComponent c) {
	        JList list = (JList)c;
	        Object val = list.getSelectedValue();
	        
	        if (val instanceof BasicCytoShapeEntity)
	        {
	        	return ((BasicCytoShapeEntity) val).getTitle();
	        }
	        else
	        {
	        	return null;
	        }
	    }
	 }	
}

