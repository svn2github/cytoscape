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
 * Listens to a ValueDisplayer and updates the underlying DiscreteMapping class.
 */
public class ValueChangeListener implements ItemListener {
    private Object key;
    private DiscreteMapping dm;

    /**
     * Constructs a ValueChangeListener.
     */
    public ValueChangeListener(DiscreteMapping dm, Object key) {
        this.dm = dm;
        this.key = key;
    }

    /**
     *  The ValueDisplayer being reflected by this listener was changed.
     *  Make the appropriate changes to the underlying data in the mapper.
     */
    public void itemStateChanged(ItemEvent e) {
        //  Update Discrete Mapper with new Value
        ValueDisplayer v = (ValueDisplayer) e.getItemSelectable();
        dm.putMapValue(key, v.getValue());
    }
}