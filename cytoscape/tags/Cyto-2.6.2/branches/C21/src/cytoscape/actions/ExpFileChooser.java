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
/**  extends JFileChooser in the following way:
 *   adds a JCheckBox at the bottom of the chooser, asking
 *   whether to copy expression data to attributes.  The
 *   state of this box can be accessed using the method
 *   getWhetherToCopyExpToAttribs().
 *
 *   @see #getWhetherToCopyExpToAttribs
 */
public class ExpFileChooser extends JFileChooser {
    JCheckBox jcb;
    boolean copyToAttribs=true;
    public ExpFileChooser(File currentDirectory) {
	super(currentDirectory, (FileSystemView) null);
    }
    protected JDialog createDialog(Component parent) throws HeadlessException {
	JDialog jd = super.createDialog(parent);
	jcb = new JCheckBox("Copy Expression Data to Graph Attributes?");
	jcb.setSelected(copyToAttribs);
	jcb.addItemListener(new CopyExpListener());
        Container contentPane = jd.getContentPane();
        contentPane.add(jcb, BorderLayout.SOUTH);
	jd.pack();
	return jd;
    }
    /** inner class for listening to the JCheckBox jcb
     *  and updating the boolean copyToAttribs when appropriate.
     *  {@link cytoscape.view.NetworkView.ExpFileChooser.copyToAttribs
     *  copyToAttribs} when appropriate. 
     */
    private class CopyExpListener implements ItemListener {
	public void itemStateChanged(ItemEvent e) {
	    if (e.getStateChange() == ItemEvent.SELECTED) {
		copyToAttribs=true;
	    }
	    else if (e.getStateChange() == ItemEvent.DESELECTED) {
		copyToAttribs=false;
	    }
	}
    }
    /** method for accessing last state of JCheckBox jcb */
    public boolean getWhetherToCopyExpToAttribs() {
	return copyToAttribs;
    }
}

