//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.io.File;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileSystemView;

//-------------------------------------------------------------------------
/** extends JFileChooser by adding a JCheckBox at the bottom of the chooser
 */
public class CheckBoxFileChooser extends JFileChooser {
    JCheckBox jcb;
    boolean jcbFlag = false;
    String jcbLabel;

    public CheckBoxFileChooser(File currentDirectory, String checkBoxLabel) {
	super(currentDirectory, (FileSystemView) null);
	jcbLabel = checkBoxLabel;
    }
    protected JDialog createDialog(Component parent) throws HeadlessException {
	JDialog jd = super.createDialog(parent);
	jcb = new JCheckBox(jcbLabel);
	jcb.setSelected(jcbFlag);
	jcb.addItemListener(new CheckBoxListener());
        Container contentPane = jd.getContentPane();
        contentPane.add(jcb, BorderLayout.SOUTH);
	jd.pack();
	return jd;
    }
    /** inner class for listening to the JCheckBox jcb
     *  and updating the boolean jcbFlag when appropriate
     */
    private class CheckBoxListener implements ItemListener {
	public void itemStateChanged(ItemEvent e) {
	    if (e.getStateChange() == ItemEvent.SELECTED)
		jcbFlag = true;	    
	    else if (e.getStateChange() == ItemEvent.DESELECTED)
		jcbFlag = false;
	}
    }
    /** method for accessing last state of JCheckBox jcb */
    public boolean getCheckBoxState() {
	return jcbFlag;
    }
}

