// GeneralColorDialogListener.java:  listener for mutable colors
//--------------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------------------

package cytoscape.dialogs;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import cytoscape.util.MutableColor;
//import javax.swing.event.*;
//import javax.swing.colorchooser.*;
//import java.awt.Color;

/**
 *  GeneralColorDialogListener: a listener that pops up and
 *  makes useful a JColorChooser.
 *  
 *  The GeneralColorDialogListener is a listener that might be
 *  added, for example, to a button.  The result of an ActionEvent
 *  is that the current color in the MutableColor (argument to the
 *  constructor) will be used as the starting point for a
 *  JColorChooser, and if the user selects a new color with the
 *  JColorChooser, that new color is written to the color of the
 *  MutableColor.  The parent Component is also passed to the
 *  constructor so that it may be passed on to the JColorChooser's
 *  constructor.
 *
 */

public class GeneralColorDialogListener implements ActionListener {
    private MutableColor returnColor;
    private String title;
    private Component component;
    private JLabel label;
    public GeneralColorDialogListener(Component component, MutableColor writeToThisColor, String title) {
	super ();
	returnColor = writeToThisColor;
	this.component = component;
	this.title = title;
	this.label = null;
    }
    public GeneralColorDialogListener(Component component, MutableColor writeToThisColor, JLabel label, String title) {
	super ();
	returnColor = writeToThisColor;
	this.component = component;
	this.title = title;
	this.label = label;
    }
    
    public void actionPerformed(ActionEvent e) {
	popup();
    }

    public void popup() {
	Color tempColor = JColorChooser.showDialog(component,
						   title,
						   returnColor.getColor());
	if (tempColor != null) {
	    returnColor.setColor(tempColor);
	    if (label != null) {
		label.setBackground(tempColor);
	    }
	}
    }
}

