/*
 * Created on May 29, 2005
 *
 */
package cytoscape.editor.impl;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import cytoscape.Cytoscape;
import cytoscape.editor.CytoscapeEditorManager;
import cytoscape.editor.actions.DeleteAction;
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
	
	protected JScrollPane scrollPane;
	
	// AJK: 12/06/05 put the help text in a scroll pane, so that it doesn't truncate
	protected JScrollPane helpScrollPane;
	
	private static final String ICONS_REL_LOC = "images/";

    private JButton deleteButton, undoButton, redoButton;
    private ImageIcon deleteIcon, undoIcon, redoIcon;
    private JPanel _controlPane ;
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
    	
    	// AJK: 10/04/05 do via a control pane with scroll pane
//		this.add(dataList);
    	
        JTextArea instructionsArea = new JTextArea();

        instructionsArea.setLineWrap(true);
        instructionsArea.setWrapStyleWord(true);
        instructionsArea.setText("To add a node to a network, drag and drop a shape from the palette onto the canvas." +
				"\n\nTo connect two nodes with an edge, " +  
				"drag and drop the arrow onto a node on the canvas, " + 
				"then move the cursor over a second node and click the mouse.");
//        instructionsArea.setBorder(BorderFactory.createEtchedBorder());
        Border       blackline = BorderFactory.createLineBorder (Color.black);
        TitledBorder title = BorderFactory.createTitledBorder (blackline,
                                                               "Cytoscape Editor");
        instructionsArea.setBorder (title);
        instructionsArea.setBackground(Cytoscape.getDesktop().getBackground());
        instructionsArea.setPreferredSize(new Dimension(
        		((JPanel) Cytoscape.getDesktop().getCytoPanel( SwingConstants.WEST )).getSize().width - 5, 125));
        
        
    	
        // AJK: 11/15/05 try to do this directly dragging and dropping Cytoshapes, so don't have to do a select on JList
//        JScrollPane scrollPane = new JScrollPane(dataList);
        _shapePane = new JPanel();
        _shapePane.setLayout(new BoxLayout (_shapePane, BoxLayout.Y_AXIS));

        scrollPane = new JScrollPane(_shapePane);
        
        
        // AJK: 12/05/05 BEGIN
        //     put the help text in a scrollPane so that it doesn't truncate        
//        _controlPane.add (instructionsArea);
        helpScrollPane = new JScrollPane (instructionsArea);
        _controlPane.add(helpScrollPane);
        
        
        // AJK: 10/24/05 comment this out, moving undo controls into Cytoscape
//        _controlPane.add(buildEditControlsPanel());
        
        
        scrollPane.setBorder(BorderFactory.createEtchedBorder());
//        scrollPane.setBorder (BorderFactory.createEmptyBorder (0, 0,
//                                                               10, 0));
        dataList.setBackground(Cytoscape.getDesktop().getBackground());
        scrollPane.setBackground(Cytoscape.getDesktop().getBackground());
        scrollPane.setPreferredSize (new Dimension(
        		((JPanel) Cytoscape.getDesktop().getCytoPanel( SwingConstants.WEST )).getSize().width - 5,
        		((JPanel) Cytoscape.getDesktop().getCytoPanel( SwingConstants.WEST )).getSize().height
				    - instructionsArea.getPreferredSize().height - 5));
        
       _controlPane.add (scrollPane);
	      
        CytoscapeEditorManager.setCurrentShapePalette(this);
        CyNetworkView view = Cytoscape.getCurrentNetworkView();
        CytoscapeEditorManager.setShapePaletteForView(view, this);
       

		this.add(_controlPane);
		this.setBackground(Cytoscape.getDesktop().getBackground());
		this.setVisible(true);	
		
	
	}
  
	
	   private JPanel buildEditControlsPanel ()
	    {
	        JPanel editControlsPanel;
	        editControlsPanel = new JPanel();
	        editControlsPanel.setLayout (new BoxLayout(editControlsPanel, BoxLayout.X_AXIS));

	        Border       blackline = BorderFactory.createLineBorder (Color.black);
	        TitledBorder title = BorderFactory.createTitledBorder (blackline,
	                                                               "Delete/Undo/Redo");
	        editControlsPanel.setBorder (title);

			deleteButton = new JButton ();
			deleteButton.setAction(new DeleteAction());
			deleteIcon = new ImageIcon(getClass().getResource(
					ICONS_REL_LOC + "deleteIcon.gif"));
			deleteButton.setIcon(deleteIcon);
			deleteButton.setToolTipText("Delete Selected Nodes and Edges");
			editControlsPanel.add(deleteButton);

//			deleteButton.setDisabledIcon(new ImageIcon(getClass()
//					.getResource(ICONS_REL_LOC + "DisabledUpLeftWhite.gif")));

			undoButton = new JButton();
			// AJK: 10/21/05 BEGIN
			//   comment out, couple actions to buttons in showPalette().
//			undoButton.setAction(new UndoAction
//					(CytoscapeEditorManager.getUndoManagerForView(Cytoscape.getCurrentNetworkView())));
            // AJK: 10/21/05 END
			undoIcon = 
			    new ImageIcon(getClass().getResource(
					ICONS_REL_LOC + "undo.gif"));
			undoButton.setIcon(undoIcon);
			undoButton.setToolTipText("Undo");
			editControlsPanel.add(undoButton);
			
			redoButton = new JButton();
			// AJK: 10/21/05 BEGIN
			//   comment out, couple actions to buttons in showPalette().
//			redoButton.setAction(new RedoAction
//					(CytoscapeEditorManager.getUndoManagerForView(Cytoscape.getCurrentNetworkView())));	
			// AJK: 10/21/05
			editControlsPanel.add(redoButton);
						redoIcon = 
			new ImageIcon(getClass().getResource(
					ICONS_REL_LOC + "redo.gif"));
			redoButton.setIcon(redoIcon);
			redoButton.setToolTipText("redo");
	        return editControlsPanel;   
	    }

	/**
	 * add a shape to the palette
	 * @param attributeName attribute name for the shape, should be one of "NodeType" or "EdgeType"
	 * @param attributeValue value for the attribute assigned to the shape, for example a "NodeType" of "protein"
	 * @param img the icon for the shape
	 * @param name the title of the shape
	 */
//	public void addShape(String attributeName, String attributeValue, ImageIcon img, String name)
	public void addShape(String attributeName, String attributeValue, Icon img, String name)
		{
		BasicCytoShapeEntity cytoShape = new BasicCytoShapeEntity(attributeName, 
				attributeValue, img, name);
		
		cytoShape.setTransferHandler(new BasicCytoShapeTransferHandler(cytoShape,
				null));

		
		_shapeMap.put(cytoShape.getTitle(), cytoShape);

		
		// AJK: 11/12/05 BEGIN
		//    add mouseAdaptor so that we can initiate DnD without having to first select shape
//		MouseListener ml = new MouseAdapter() {
//		    public void mousePressed(MouseEvent e) {
//		    	System.out.println("Mouse pressed on: " + e);
//		        JComponent c = (JComponent)e.getSource();
//                BasicCytoShapeTransferHandler handler = 
//                	(BasicCytoShapeTransferHandler) ((BasicCytoShapeEntity) c).getTransferHandler();//            
////                handler.exportAsDrag(c, e, TransferHandler.COPY);   
//                handler.exportString(c);		        
//		    }
//		};
//	      cytoShape.addMouseListener(ml);
//	      
	
	      // don't add to list, add directly to _shapePane
//		listModel.addElement(cytoShape);
	      _shapePane.add(cytoShape);
		
		// AJK: 11/12/05 END
//		Cytoscape.getCurrentNetworkView().redrawGraph(false, true);

	}
	
	/**
	 * setup the palette in the Navigator Panel of Cytoscape
	 * TODO: use CytoPanels?
	 *
	 */
	public void showPalette() {
		
		// AJK: 05/25/06 BEGIN
		//     store and restore current tab in WEST Cytopanel
		int currentCytoPanelIdx = 
			Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST).getSelectedIndex();
		
		// AJK: 05/25/06 END
		
		
		// remove old existing editor palette from Cytopanel and replace with new one
        int idx = Cytoscape.getDesktop().getCytoPanel( SwingConstants.WEST ).indexOfComponent("Editor");
        System.out.println ("index of current palette = " + idx);
        if (idx >= 0)
        {
        	Cytoscape.getDesktop().getCytoPanel( SwingConstants.WEST ).remove(idx);
        	System.out.println ("removing palette at Cytopanel indes: " + idx);
        }
		
		Cytoscape.getDesktop().getCytoPanel( SwingConstants.WEST ).add( "Editor", this);
		Cytoscape.getDesktop().getCytoPanel( SwingConstants.WEST ).setSelectedIndex(
				Cytoscape.getDesktop().getCytoPanel( SwingConstants.WEST ).indexOfComponent(this));
		System.out.println ("Set new selected component on Cytopanel: " +
				Cytoscape.getDesktop().getCytoPanel( SwingConstants.WEST ).getSelectedComponent());
		
		CyNetworkView view = Cytoscape.getCurrentNetworkView();
		CytoscapeEditorManager.setShapePaletteForView(view, this);
		
		// AJK: 10/24/05 BEGIN
		//    comment out, undo functionality is moving to Cytoscape
		/*
		this.getUndoButton().setAction(
//				new UndoAction
//				(CytoscapeEditorManager.getUndoManagerForView(
				CytoscapeEditorManager.getUndoActionForView(
						Cytoscape.getCurrentNetworkView()));

		this.getUndoButton().setIcon(undoIcon);
//		this.getUndoButton().setEnabled(true);
		
		this.getRedoButton().setAction(
//				new RedoAction
//				(CytoscapeEditorManager.getUndoManagerForView(
//						Cytoscape.getCurrentNetworkView())));
				CytoscapeEditorManager.getRedoActionForView(
						Cytoscape.getCurrentNetworkView()));
		this.getRedoButton().setIcon(redoIcon);
//		this.getRedoButton().setEnabled(true);
		*/
		
		this.setVisible(true);
		
		// AJK: 05/25/06 BEGIN
		//     store and restore current tab in WEST Cytopanel
		 
		Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST).setSelectedIndex(currentCytoPanelIdx);
		
		// AJK: 05/25/06 END	

//		Cytoscape.getDesktop().getNetworkPanel().setNavigator(this);
//
//		Rectangle bounds = Cytoscape.getDesktop().getNetworkPanel()
//				.getNavigatorPanel().getBounds();
//		Cytoscape.getDesktop().getNetworkPanel().getNavigatorPanel()
//				.setPreferredSize(
//						new Dimension(bounds.width, 3 * bounds.height));
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

    private class DragMouseAdapter extends MouseAdapter {
        public void mousePressed(MouseEvent e) {
        	
            JComponent c = (JComponent)e.getSource();
//            JComponent p = dataList;
//            dataList.setSelectedValue(c, true);
            System.out.println ("Mouse pressed on: " + c);
////            TransferHandler handler = c.getTransferHandler();
//            TransferHandler handler = p.getTransferHandler();
            if (c instanceof BasicCytoShapeEntity)
            {
                BasicCytoShapeTransferHandler handler = 
                	(BasicCytoShapeTransferHandler) ((BasicCytoShapeEntity) c).getTransferHandler();//            
//                handler.exportAsDrag(c, e, TransferHandler.COPY);   
                handler.exportString(c);
            }
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
	    
	    // AJK: 12/11/05 BEGIN
	    //     try to handle with 
	     
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

	 
	 
	 /**
	 * @return Returns the deleteButton.
	 */
	public JButton getDeleteButton() {
		return deleteButton;
	}
	/**
	 * @return Returns the redoButton.
	 */
	public JButton getRedoButton() {
		return redoButton;
	}
	/**
	 * @return Returns the undoButton.
	 */
	public JButton getUndoButton() {
		return undoButton;
	}
}

