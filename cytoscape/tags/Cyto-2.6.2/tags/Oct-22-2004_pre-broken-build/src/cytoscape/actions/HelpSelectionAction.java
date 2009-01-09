//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//------------------------------------------------------------------------------
package cytoscape.actions;
//------------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import cytoscape.view.CyWindow;
import cytoscape.CytoscapeVersion;
//------------------------------------------------------------------------------
public class HelpSelectionAction extends AbstractAction   {
    CyWindow cyWindow;
    /** The constructor that takes no arguments shows the
     *  label "About Cytoscape" - this makes it appropriate
     *  for the pulldown menu system, and inappropriate for an icon. */
    public HelpSelectionAction(CyWindow cyWindow) {
        super("About Cytoscape");
        this.cyWindow = cyWindow;
    }
    /** The constructor that takes a boolean shows no label,
     *  no matter what the value of the boolean actually is.
     *  This makes is appropriate for an icon, but inappropriate
     *  for the pulldown menu system. */
    public HelpSelectionAction(CyWindow cyWindow,
                               boolean showLabel) {
	super();
        this.cyWindow = cyWindow;
    }
    public void actionPerformed(ActionEvent e) {
        CytoscapeVersion tmp = new CytoscapeVersion();
        String blerb = new String("Cytoscape is a joint project between:\n" +
                                   "- Institute for Systems Biology (ISB),\n" +
                                   "- University of California San Diego,\n" +
                                   "- Memorial Sloan-Kettering Cancer Center (MSKCC)"
                                  );
     
	JOptionPane.showMessageDialog(null, tmp.getVersion() + "\n\n"
                                      // + "Build date: " + tmp.getBuildDate() + "\n\n"
                                      + blerb
                                     );
    }
}

