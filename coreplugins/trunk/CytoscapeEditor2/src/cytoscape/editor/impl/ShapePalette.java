/*
 * Created on May 29, 2005
 *
 */
package cytoscape.editor.impl;

import java.awt.Component;
import java.awt.Dimension;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import cytoscape.Cytoscape;
import cytoscape.editor.CytoscapeEditorManager;
import cytoscape.editor.event.BasicCytoShapeTransferHandler;
import cytoscape.view.CyNetworkView;



/**
 * 
 * The <b>ShapePalette</b> class implements a palette from which the user drags and drops shapes onto the canvas
 * The dropping of shapes onto the canvas results in the addition of nodes and edges to the current Cytoscape 
 * network, as defined by the behavior of the event handler that responds to the drop events.  For example, in the
 * simple "BioPAX-like" editor, there are node types for proteins, catalysis, small molecules, and biochemical 
 * reactions, as well as a directed edge type.
 * <p>
 * The user interface for the ShapePalette is built upon the JList class.
 * 
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
	
    private JPanel _controlPane ;

	protected JScrollPane scrollPane;
		
	protected JPanel _shapePane;


	public ShapePalette() {
		super();
		
	    _controlPane = new JPanel();
	    _controlPane.setLayout (new BoxLayout(_controlPane, BoxLayout.Y_AXIS));

		listModel = new DefaultListModel();
		dataList = new JList (listModel);
		dataList.setCellRenderer(new MyCellRenderer());
		dataList.setDragEnabled(true);
				
    	dataList.setTransferHandler(new PaletteListTransferHandler());
    	// AJK: 09/16/05 BEGIN
    	//     set internal spacing via fixed cell height and width
    	dataList.setFixedCellHeight(CytoShapeIcon.HEIGHT + 5);
    	// AJK: 09/16/05 END
    	
        _shapePane = new JPanel();
        _shapePane.setLayout(new BoxLayout (_shapePane, BoxLayout.Y_AXIS));

        scrollPane = new JScrollPane(_shapePane);
        
        
        scrollPane.setBorder(BorderFactory.createEtchedBorder());
        dataList.setBackground(Cytoscape.getDesktop().getBackground());
        scrollPane.setBackground(Cytoscape.getDesktop().getBackground());
        scrollPane.setPreferredSize (new Dimension(
        		((JPanel) Cytoscape.getDesktop().getCytoPanel( SwingConstants.WEST )).getSize().width - 5,
        		((JPanel) Cytoscape.getDesktop().getCytoPanel( SwingConstants.WEST )).getSize().height - 5));
        	
       
        CytoscapeEditorManager.setCurrentShapePalette(this);
        CyNetworkView view = Cytoscape.getCurrentNetworkView();
        CytoscapeEditorManager.setShapePaletteForView(view, this);
       
        _controlPane.add(scrollPane);
        this.add(_controlPane);
		this.setBackground(Cytoscape.getDesktop().getBackground());
		this.setVisible(true);	
					
	}
	
	/**
	 * clear the ShapePalette by removing all its shape components
	 *
	 */
	public void clear ()
	{
		_shapePane.removeAll();
	}
  
	
	   /**
	 * add a shape to the palette
	 * @param attributeName attribute name for the shape, should be one of "NodeType" or "EdgeType"
	 * @param attributeValue value for the attribute assigned to the shape, for example a "NodeType" of "protein"
	 * @param img the icon for the shape
	 * @param name the title of the shape
	 */
	public void addShape(String attributeName, String attributeValue, Icon img, String name)
		{
		BasicCytoShapeEntity cytoShape = new BasicCytoShapeEntity(attributeName, 
				attributeValue, img, name);
		
		cytoShape.setTransferHandler(new BasicCytoShapeTransferHandler(cytoShape,
				null));	
		_shapeMap.put(cytoShape.getTitle(), cytoShape);

		if (attributeName.equals(CytoscapeEditorManager.EDGE_TYPE))
		{
			CytoscapeEditorManager.addEdgeTypeForVisualStyle(
					Cytoscape.getCurrentNetworkView().getVisualStyle(), 
					attributeValue);		
		}
		
		_shapePane.add(cytoShape);
	}
	
	
	/**
	 * show the palette in the WEST cytopanel
	 * 	 
	 */
	public void showPalette() {		
		CyNetworkView view = Cytoscape.getCurrentNetworkView();
		CytoscapeEditorManager.setShapePaletteForView(view, this);
		this.setVisible(true);
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

