// GridBagGroup.java:  more GridBagLayout utilities
//--------------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------------------
package cytoscape.dialogs;
//--------------------------------------------------------------------------------------
import java.io.*;
import java.util.*;

import javax.swing.*;
import java.awt.*;

//------------------------------------------------------------------------------
public class GridBagGroup {
    public JPanel panel;
    public GridBagLayout gridbag;
    public GridBagConstraints constraints;
    public GridBagGroup() {
	panel = new JPanel();
	gridbag = new GridBagLayout();
	constraints = new GridBagConstraints();
	panel.setLayout(gridbag);
    }
}

