// NewStringPopupDialog.java:  the name says it all.
//--------------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------------------

package cytoscape.dialogs;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

/**
 *  NewStringPopupDialog: a dialog that is used for creating a
 *  new string.  Use the getString() method to retrieve the string.
 *
 */

public class NewStringPopupDialog extends JDialog {
    
    private String theString; 
    JTextField textField;
    public NewStringPopupDialog(Frame parent, String title) {
	super(parent,true);
	setTitle(title);
	textField = new JTextField();
	textField.setPreferredSize(new Dimension(200,25));
	theString=null;
	JButton okButton = new JButton("OK");
	JButton cancelButton = new JButton("Cancel");
	okButton.addActionListener(new OkAction());
	cancelButton.addActionListener(new CancelAction());
	JPanel panel = new JPanel();
	GridBagLayout gridbag = new GridBagLayout(); 
	GridBagConstraints c = new GridBagConstraints();
	panel.setLayout (gridbag);
	c.gridx=0;
	c.gridy=0;
	c.gridwidth=2;
	gridbag.setConstraints(textField,c);
	panel.add (textField);
	c.gridx=0;
	c.gridy=1;
	c.gridwidth=1;
	gridbag.setConstraints(okButton,c);
	panel.add (okButton);
	c.gridx=1;
	c.gridy=1;
	gridbag.setConstraints(cancelButton,c);
	panel.add (cancelButton);
	setContentPane(panel);
	pack ();
	setLocationRelativeTo (parent);
	setVisible (true);
    }
    public String getString() {
	if(theString!=null)
	    return theString.trim();
	else
	    return null;
    }
    
    public class OkAction extends AbstractAction {
	OkAction () {
	    super ("");
	}
	public void actionPerformed (ActionEvent e) {
	    theString = textField.getText();
	    NewStringPopupDialog.this.dispose();
	}
    }

    public class CancelAction extends AbstractAction {
	CancelAction () {
	    super ("");
	}
	public void actionPerformed (ActionEvent e) {
	    NewStringPopupDialog.this.dispose();
	}
    }
}

