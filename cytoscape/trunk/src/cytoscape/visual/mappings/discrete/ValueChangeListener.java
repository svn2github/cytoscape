//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual.mappings.discrete;

import cytoscape.visual.mappings.DiscreteMapping;
import cytoscape.visual.ui.ValueDisplayer;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Listens to a ValueDisplayer and sets the underlying TreeMap when a new
 * selection is made. Construct with the key whose mapping the ValueDisplayer
 * is displaying.
 */
public class ValueChangeListener implements ItemListener {
    private Object key;
    private DiscreteMapping dm;
    private JDialog parentDialog;
    private DiscreteUI ui;
    private int position;

    /**
     * Constructs a ValueChangeListener.
     */
    public ValueChangeListener(DiscreteMapping dm, DiscreteUI ui,
            JDialog parentDialog, int position, Object key) {
        this.dm = dm;
        this.ui = ui;
        this.parentDialog = parentDialog;
        this.position = position;
        this.key = key;
    }

    /**
     *  The ValueDisplayer being reflected by this listener was changed.
     *  Make the appropriate changes to the underlying data in the mapper
     *  and notify interested listeners that state has changed.
     */
    public void itemStateChanged(ItemEvent e) {
        String usrMsg = "Define Discrete Mapping";
        ValueDisplayer v = (ValueDisplayer) e.getItemSelectable();

        //  Update Discrete Mapper with new Value
        dm.putMapValue(key, v.getValue());

        //  Swap in New Value Displayer
        ValueDisplayer newValueDisplayer = ValueDisplayer.getDisplayFor
                (parentDialog, usrMsg, v.getValue());
        ui.swapValueDisplayer(newValueDisplayer, position);
    }
}