//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual.mappings.continuous;

import cytoscape.visual.mappings.BoundaryRangeValues;
import cytoscape.visual.mappings.ContinuousMapping;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Listens for User Request to Create New Point.
 */
public class AddPointListener implements ActionListener {
    private ContinuousUI ui;
    private ContinuousMapping cm;
    private Object defaultObject;

    /**
     * Constructor.
     * @param ui ContinuousUI Object.
     * @param cm ContinuousMapping Object.
     */
    public AddPointListener(ContinuousUI ui, ContinuousMapping cm,
            Object defaultObject) {
        this.ui = ui;
        this.cm = cm;
        this.defaultObject = defaultObject;
    }

    /**
     * User Initiated Action.
     * @param e Action Event.
     */
    public void actionPerformed(ActionEvent e) {
        double value;
        int i = cm.getPointCount();
        BoundaryRangeValues brv = new BoundaryRangeValues();

        if (i > 0) {
            //  If this is not the first point, new point
            //  is based on the previous point in the list.
            ContinuousMappingPoint previousPoint = cm.getPoint(i - 1);
            BoundaryRangeValues previousRange = previousPoint.getRange();
            brv.lesserValue = previousRange.lesserValue;
            brv.equalValue = previousRange.equalValue;
            brv.greaterValue = previousRange.greaterValue;
            value = previousPoint.getValue();
        } else {
            //  If this is the first point, use Default Object, and value = 0.0
            brv.lesserValue = defaultObject;
            brv.equalValue = defaultObject;
            brv.greaterValue = defaultObject;
            value = 0.0;
        }
        cm.addPoint(value, brv);
        ui.resetUI();
    }
}