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
import javax.swing.border.*;
import java.awt.*;

//------------------------------------------------------------------------------
public class GridBagGroup {
    public JPanel panel;
    public GridBagLayout gridbag;
    public GridBagConstraints constraints;
    public GridBagGroup() {
	init();
    }
    private void init() {
	panel = new JPanel();
	gridbag = new GridBagLayout();
	constraints = new GridBagConstraints();
	panel.setLayout(gridbag);
    }
    public GridBagGroup(String title) {
	init();
	Border border = BorderFactory.createLineBorder (Color.black);
	Border titledBorder = 
	    BorderFactory.createTitledBorder (border,
					      title,
					      TitledBorder.CENTER, 
					      TitledBorder.DEFAULT_POSITION);
	panel.setBorder (titledBorder);
    }
}

