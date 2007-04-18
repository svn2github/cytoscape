package cytoscape.visual.ui;

import com.l2fprod.common.propertysheet.PropertySheetTable;

import java.awt.event.MouseListener;

import javax.swing.Action;


/**
 * DOCUMENT ME!
 *
 * @author $author$
  */
public class CyPropertySheetTable extends PropertySheetTable {
    /**
     * Creates a new CyPropertySheetTable object.
     */
    public CyPropertySheetTable() {
        super();

        Action ac = getActionMap()
                        .get("toggle");
        System.out.println("****AC = " + ac.toString());

        for (MouseListener l : this.getMouseListeners())
            System.out.println("******************* lis = " + l.toString());
    }

    protected void expand() {
        this.setRowSelectionInterval(0, 2);
        getActionMap()
            .get("toggle")
            .actionPerformed(null);
    }
}
