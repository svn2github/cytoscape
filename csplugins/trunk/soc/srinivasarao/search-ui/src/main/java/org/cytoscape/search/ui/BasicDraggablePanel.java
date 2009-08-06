package org.cytoscape.search.internal;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceMotionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.TransferHandler;

public class BasicDraggablePanel extends JPanel implements Transferable {

	private static final long serialVersionUID = 1L;
	private static DataFlavor dataflavor = null; // @jve:decl-index=0:

	/**
	 * This is the default constructor
	 */
	public BasicDraggablePanel() {
		super();
		// Add the listener which will export this panel for dragging
		this.addMouseListener(new DraggableMouseListener());
		// Add the handler, which negotiates between drop target and this
		// draggable panel
		this.setTransferHandler(new DragAndDropTransferHandler());

	}

	@Override
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		// TODO Auto-generated method stub
		System.out
				.println("Step 7 of 7: Returning the data from the Transferable object. In this case, the actual panel is now transfered!");
		DataFlavor thisFlavor = null;
		try {
			thisFlavor = BasicDraggablePanel.getDragAndDropPanelDataFlavor();
		} catch (Exception ex) {
			System.err.println("Problem lazy loading: " + ex.getMessage());
			ex.printStackTrace(System.err);
			return null;
		}

		// For now, assume wants this class... see loadDnD
		if (thisFlavor != null && flavor.equals(thisFlavor)) {
			return BasicDraggablePanel.this;
		}

		return null;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		// TODO Auto-generated method stub
		System.out
				.println("Step 4 of 7: Querying for acceptable DataFlavors to determine what is available. Our example only supports our custom RandomDragAndDropPanel DataFlavor.");

		DataFlavor[] flavors = { null };
		try {
			flavors[0] = BasicDraggablePanel.getDragAndDropPanelDataFlavor();
		} catch (Exception e) {
			System.out.println(e);
		}

		return null;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		// TODO Auto-generated method stub
		System.out
				.println("Step 6 of 7: Verifying that DataFlavor is supported.  Our example only supports our custom RandomDragAndDropPanel DataFlavor.");
		DataFlavor[] flavors = { null };
		try {
			flavors[0] = BasicDraggablePanel.getDragAndDropPanelDataFlavor();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		for (DataFlavor f : flavors) {
			if (f.equals(flavor)) {
				return true;
			}
		}

		return false;
	}

	public static DataFlavor getDragAndDropPanelDataFlavor() throws Exception {
		// TODO Auto-generated method stub
		if (dataflavor == null) {
			dataflavor = new DataFlavor(
					DataFlavor.javaJVMLocalObjectMimeType
							+ ";class=org.cytoscape.search.internal.BasicDraggablePanel");
		}
		return dataflavor;
	}

}

class DraggableMouseListener extends MouseAdapter {
	public void mousePressed(MouseEvent e) {
		System.out
				.println("Step 1 of 7: Mouse pressed. Going to export our RandomDragAndDropPanel so that it is draggable.");

		JComponent c = (JComponent) e.getSource();
		TransferHandler handler = c.getTransferHandler();
		handler.exportAsDrag(c, e, TransferHandler.COPY);
	}
}

class DragAndDropTransferHandler extends TransferHandler implements
		DragSourceMotionListener {

	/**
		 * 
		 */
	private static final long serialVersionUID = 1L;

	public DragAndDropTransferHandler() {
		super();
	}

	public Transferable createTransferable(JComponent c) {
		System.out
				.println("Step 3 of 7: Casting the RandomDragAndDropPanel as Transferable. The Transferable RandomDragAndDropPanel will be queried for acceptable DataFlavors as it enters drop targets, as well as eventually present the target with the Object it transfers.");

		if (c instanceof BasicDraggablePanel) {
			Transferable tip = (BasicDraggablePanel) c;
			return tip;
		}
		return null;
	}

	@Override
	public void dragMouseMoved(DragSourceDragEvent dsde) {
		// TODO Auto-generated method stub

	}

	/**
	 * <p>
	 * This is queried to see whether the component can be copied, moved, both
	 * or neither. We are only concerned with copying.
	 * </p>
	 * 
	 * @param c
	 * @return
	 */
	@Override()
	public int getSourceActions(JComponent c) {

		System.out
				.println("Step 2 of 7: Returning the acceptable TransferHandler action. Our RandomDragAndDropPanel accepts Copy only.");

		if (c instanceof BasicDraggablePanel) {
			return TransferHandler.COPY;
		}

		return TransferHandler.NONE;
	}

}
