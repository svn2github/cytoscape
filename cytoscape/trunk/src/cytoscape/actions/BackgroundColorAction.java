//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;

//-------------------------------------------------------------------------

import cytoscape.view.NetworkView;
import cytoscape.visual.GlobalAppearanceCalculator;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualStyle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

//-------------------------------------------------------------------------
/**
 * Prompts User for New Background Color.
 */
public class BackgroundColorAction extends AbstractAction {

    /**
     * Current Network View Object.
     */
    NetworkView networkView;

    /**
     * Constructor.
     * @param networkView Network View Object.
     */
    public BackgroundColorAction(NetworkView networkView) {
        super("Change Background Color");
        this.networkView = networkView;
    }

    /**
     * Captures User Menu Selection.
     * @param ev Action Event.
     */
    public void actionPerformed(ActionEvent ev) {
        // Do this in the GUI Event Dispatch thread...
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JColorChooser color = new JColorChooser();
                Color newPaint = color.showDialog
                        (networkView.getView().getComponent(),
                                "Choose a Background Color",
                                (java.awt.Color) networkView.getView().
                                getBackgroundPaint());

                //  Update the Current Background Color
                //  and Synchronize with current Visual Style
                networkView.getView().setBackgroundPaint(newPaint);
                synchronizeVisualStyle(newPaint);
            }
        });
    }//action performed

    /**
     * Synchronizes the New Background Color with the Current Visual Style.
     * @param newColor New Color
     */
    private void synchronizeVisualStyle(Color newColor) {
        VisualMappingManager vmm = networkView.getVizMapManager();
        VisualStyle style = vmm.getVisualStyle();
        GlobalAppearanceCalculator gCalc =
                style.getGlobalAppearanceCalculator();
        gCalc.setDefaultBackgroundColor(newColor);
    }
}