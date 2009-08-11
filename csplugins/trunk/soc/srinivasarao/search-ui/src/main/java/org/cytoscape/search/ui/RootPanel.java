package org.cytoscape.search.ui;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.JPanel;

import org.cytoscape.session.CyNetworkManager;

public class RootPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	ArrayList<BasicDraggablePanel> list;
	CyNetworkManager netmgr = null;

	/**
	 * This is the default constructor
	 */
	public RootPanel(CyNetworkManager nm) {
		super();
		this.setLayout(new GridBagLayout());
		this.setTransferHandler(new DragAndDropTransferHandler());
		this.setDropTarget(new DropTarget(RootPanel.this,
				new BasicDraggablePanelDropTargetListener(RootPanel.this)));
		list = new ArrayList<BasicDraggablePanel>();
		this.netmgr = nm;
	}

	public void addPanel(Component c) {
		if (c instanceof BasicDraggablePanel) {
			BasicDraggablePanel bb = (BasicDraggablePanel) c;
			list.add(bb);
		}
		relayout();
	}

	public List<BasicDraggablePanel> getPanelList() {
		return list;
	}

	public CyNetworkManager getNetworkManager() {
		return netmgr;
	}
	
	public void clearAll(){
		list.clear();
		this.removeAll();
	}
	
	public void relayout() {
		GridBagConstraints gc = new GridBagConstraints();
		gc.gridx = 0;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.weightx = 1.0;
		gc.anchor = GridBagConstraints.PAGE_START;
		this.removeAll();
		int row = 0;
		for (int i = 0; i < list.size(); i++) {
			if (i == 0)
				gc.insets = new Insets(8, 0, 10, 0);
			else
				gc.insets = new Insets(0, 0, 10, 0);
			gc.gridy = row;
			BasicDraggablePanel b = list.get(i);
			super.add(b, gc);
			row++;
		}
		gc.fill = GridBagConstraints.BOTH;
		gc.weighty = 1.0;
		this.add(Box.createRigidArea(null), gc);
		this.validate();
		// this.repaint();
	}
}

/**
 * <p>
 * Listens for drops and performs the updates.
 * </p>
 * <p>
 * The real magic behind the drop!
 * </p>
 */
class BasicDraggablePanelDropTargetListener implements DropTargetListener {

	private final RootPanel searchPanel;

	/**
	 * <p>
	 * Two cursors with which we are primarily interested while dragging:
	 * </p>
	 * <ul>
	 * <li>Cursor for droppable condition</li>
	 * <li>Cursor for non-droppable condition</li>
	 * </ul>
	 * <p>
	 * After drop, we manually change the cursor back to default, though does
	 * this anyhow -- just to be complete.
	 * </p>
	 */
	private final Cursor droppableCursor = Cursor
			.getPredefinedCursor(Cursor.HAND_CURSOR),
			notDroppableCursor = Cursor
					.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);

	public BasicDraggablePanelDropTargetListener(RootPanel sheet) {
		this.searchPanel = sheet;
	}

	// Could easily find uses for these, like cursor changes, etc.
	public void dragEnter(DropTargetDragEvent dtde) {
		
	}

	public void dragOver(DropTargetDragEvent dtde) {
		if (!this.searchPanel.getCursor().equals(droppableCursor)) {
			this.searchPanel.setCursor(droppableCursor);
		}
	}

	public void dropActionChanged(DropTargetDragEvent dtde) {
	}

	public void dragExit(DropTargetEvent dte) {
		this.searchPanel.setCursor(notDroppableCursor);
	}

	@Override
	public void drop(DropTargetDropEvent dtde) {
		System.out
				.println("Step 5 of 7: The user dropped the panel. The drop(...) method will compare the drops location with other panels and reorder the panels accordingly.");
		this.searchPanel.setCursor(Cursor.getDefaultCursor());
		DataFlavor flavor = null;
		Object transferableObj = null;
		Transferable transferable = null;
		try {
			flavor = BasicDraggablePanel.getDragAndDropPanelDataFlavor();
			transferable = dtde.getTransferable();
			// What does the Transferable support
			if (transferable.isDataFlavorSupported(flavor)) {
				transferableObj = dtde.getTransferable()
						.getTransferData(flavor);

			}

		} catch (Exception e) {
			System.out.println(e);
			if (transferableObj == null)
				return;
		}

		BasicDraggablePanel droppedPanel = (BasicDraggablePanel) transferableObj;

		final int dropYLoc = dtde.getLocation().y;

		Map<Integer, BasicDraggablePanel> map = new HashMap<Integer, BasicDraggablePanel>();
		map.put(dropYLoc, droppedPanel);
		for (BasicDraggablePanel b : searchPanel.getPanelList()) {
			int y = b.getY();
			if (!b.equals(droppedPanel)) {
				map.put(y, b);
			}
		}
		List<Integer> yvalues = new ArrayList<Integer>();
		yvalues.addAll(map.keySet());
		Collections.sort(yvalues);

		List<BasicDraggablePanel> orderedPanels = new ArrayList<BasicDraggablePanel>();
		for (Integer i : yvalues) {
			orderedPanels.add(map.get(i));
		}

		List<BasicDraggablePanel> present = searchPanel.getPanelList();
		present.clear();
		present.addAll(orderedPanels);
		searchPanel.relayout();
		SearchPanelFactory.getGlobalInstance(searchPanel.getNetworkManager())
				.updateSearchField();
	}
}
