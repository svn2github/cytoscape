/* -*-Java-*-
********************************************************************************
*
* File:         ShapePalette.java
* RCS:          $Header: $
* Description:
* Author:       Allan Kuchinsky
* Created:      Sun May 29 11:18:17 2005
* Modified:     Sun Dec 17 05:33:30 2006 (Michael L. Creech) creech@w235krbza760
* Language:     Java
* Package:
* Status:       Experimental (Do Not Distribute)
*
* (c) Copyright 2006, Agilent Technologies, all rights reserved.
*
********************************************************************************
*
* Revisions:
*
* Sun Dec 17 05:30:11 2006 (Michael L. Creech) creech@w235krbza760
*  Added DragSourceContextCursorSetter parameter to addShape().
* Mon Dec 04 11:57:11 2006 (Michael L. Creech) creech@w235krbza760
*  Changed the JList to no longer use
*  setFixedCellHeight() since BasicCytoShapeEntitys can now have
*  different sizes.
* Sun Aug 06 11:19:38 2006 (Michael L. Creech) creech@w235krbza760
*  Added generated serial version UUID for serializable classes.
********************************************************************************
*/
package cytoscape.editor.impl;

import cytoscape.Cytoscape;
import cytoscape.editor.CytoscapeEditorManager;
import cytoscape.editor.DragSourceContextCursorSetter;
import cytoscape.editor.event.BasicCytoShapeTransferHandler;
import org.cytoscape.view.GraphView;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;


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
public class ShapePalette extends JPanel {
	// MLC 08/06/06:
	private static final long serialVersionUID = -4018789452330887392L;

	/**
	 * mapping of shapes to their titles
	 */
	static Map<String, BasicCytoShapeEntity> _shapeMap = new HashMap<String, BasicCytoShapeEntity>();

	/**
	 * the user interface for the ShapePalette
	 */
	protected JList dataList;
	protected DefaultListModel listModel;
	private JPanel _controlPane;
	protected JScrollPane scrollPane;
	protected JPanel _shapePane;

	/**
	 * Creates a new ShapePalette object.
	 */
	public ShapePalette() {
		super();

		_controlPane = new JPanel();
		_controlPane.setLayout(new BoxLayout(_controlPane, BoxLayout.Y_AXIS));

		listModel = new DefaultListModel();
		dataList = new JList(listModel);
		dataList.setCellRenderer(new MyCellRenderer());
		dataList.setDragEnabled(true);

		dataList.setTransferHandler(new PaletteListTransferHandler());
		_shapePane = new JPanel();
		_shapePane.setLayout(new BoxLayout(_shapePane, BoxLayout.Y_AXIS));

		scrollPane = new JScrollPane(_shapePane);

		scrollPane.setBorder(BorderFactory.createEtchedBorder());
		dataList.setBackground(Cytoscape.getDesktop().getBackground());
		scrollPane.setBackground(Cytoscape.getDesktop().getBackground());

		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.setLayout(new BorderLayout());
		this.add(scrollPane, BorderLayout.CENTER);
		this.setPreferredSize(new Dimension(300, 300));
		this.setMaximumSize(new Dimension(300, 300));
		// AJK: 12/10/06 END
		CytoscapeEditorManager.setCurrentShapePalette(this);

		GraphView view = Cytoscape.getCurrentNetworkView();
		CytoscapeEditorManager.setShapePaletteForView(view, this);

		this.setBackground(Cytoscape.getDesktop().getBackground());
		this.setVisible(true);
	}

	/**
	 * clear the ShapePalette by removing all its shape components
	 *
	 */
	public void clear() {
		_shapePane.removeAll();
	}

	/**
	* add a shape to the palette
	* @param attributeName attribute name for the shape, should be one of "NodeType" or "EdgeType"
	* @param attributeValue value for the attribute assigned to the shape, for example a "NodeType" of "protein"
	* @param img the icon for the shape
	* @param name the title of the shape
	* @param cursorSetter a possibly null DragSourceContextCursorSetter used to specify
	*                     the cursor so show when dragging over the current network view.
	*                     
	* @param directed_edge if true, the edge type declared with this call is directed
	* 		(i.e. edges of this type will be created as directed), if false, undirected.
	* 		
	*  		used only for when attributeName is EDGE_TYPE, ignored for nodes 
	*/

	// MLC 12/16/06 BEGIN:
	public void addShape(String attributeName, String attributeValue, Icon img, String name,
	                     DragSourceContextCursorSetter cursorSetter, boolean directed_edge) {
		BasicCytoShapeEntity cytoShape = new BasicCytoShapeEntity(attributeName, attributeValue,
		                                                          img, name, cursorSetter);
		cytoShape.setTransferHandler(new BasicCytoShapeTransferHandler(cytoShape, null));
		_shapeMap.put(cytoShape.getTitle(), cytoShape);

		if (attributeName.equals(CytoscapeEditorManager.EDGE_TYPE)) {
			CytoscapeEditorManager.addEdgeTypeForVisualStyle(Cytoscape.getVisualMappingManager().getVisualStyleForView(Cytoscape.getCurrentNetworkView()),attributeValue, directed_edge);
			
		}

		_shapePane.add(cytoShape);
	}
	public void addShape(String attributeName, String attributeValue, Icon img, String name,
            DragSourceContextCursorSetter cursorSetter){
				addShape(attributeName, attributeValue, img, name, cursorSetter, true);
	}
	/**
	 * show the palette in the WEST cytopanel
	 *
	 */
	public void showPalette() {
		GraphView view = Cytoscape.getCurrentNetworkView();
		CytoscapeEditorManager.setShapePaletteForView(view, this);
		this.setVisible(true);
	}

	/**
	 *
	 * @param key the name of the shape to be returned
	 * @return return the BasicCytoShapeEntity associated with the input shape name
	 */
	public static BasicCytoShapeEntity getBasicCytoShapeEntity(String key) {
		Object val = _shapeMap.get(key);

		if (val instanceof BasicCytoShapeEntity) {
			return ((BasicCytoShapeEntity) val);
		} else {
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
		// MLC 08/06/06:
		private static final long serialVersionUID = -4704405703871398609L;

		public Component getListCellRendererComponent(JList list, Object value, // value to display
		                                              int index, // cell index
		                                              boolean isSelected, // is the cell selected
		                                              boolean cellHasFocus) // the list and the cell have the focus
		 {
			if (value instanceof BasicCytoShapeEntity) {
				BasicCytoShapeEntity cytoShape = (BasicCytoShapeEntity) value;
				setText(cytoShape.getTitle());
				setIcon(cytoShape.getIcon());
				setToolTipText(cytoShape.getToolTipText());
			}

			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
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
		// MLC 08/06/06:
		private static final long serialVersionUID = -3858539899491771525L;

		protected void cleanup(JComponent c, boolean remove) {
		}

		protected void importString(JComponent c, String str) {
		}

		//Bundle up the selected items in the list
		//as a single string, for export.
		protected String exportString(JComponent c) {
			JList list = (JList) c;
			Object val = list.getSelectedValue();

			if (val instanceof BasicCytoShapeEntity) {
				return ((BasicCytoShapeEntity) val).getTitle();
			} else {
				return null;
			}
		}
	}
}
