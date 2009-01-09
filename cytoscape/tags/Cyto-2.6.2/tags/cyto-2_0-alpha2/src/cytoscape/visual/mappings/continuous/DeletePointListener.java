//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual.mappings.continuous;

import cytoscape.visual.mappings.ContinuousMapping;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Listens for User Request to Delete Existing Point.
 */
public class DeletePointListener implements ActionListener {
    private ContinuousUI ui;
    private ContinuousMapping cm;
    private int index = -1;     //  Index Value in Point List.

    /**
     * Constructor.
     * @param ui ContinuousUI Object.
     * @param cm ContinuousMapping Object.
     */
    public DeletePointListener(ContinuousUI ui, ContinuousMapping cm, int i) {
        this.ui = ui;
        this.cm = cm;
        index = i;
    }

    /**
     * User Initiated Action.
     * @param e Action Event.
     */
    public void actionPerformed(ActionEvent e) {
        if ((index < 0) || (index >= cm.getPointCount())) return;
        cm.removePoint(index);
        ui.resetUI();
    }
}