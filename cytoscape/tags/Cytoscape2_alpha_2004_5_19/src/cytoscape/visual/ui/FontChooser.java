//--------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------
package cytoscape.visual.ui;
//--------------------------------------------------------------------------
import java.awt.*;
import java.awt.font.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;
//--------------------------------------------------------------------------
/**
 * Defines a generalized font chooser class. FontChooser contains three
 * components to display font face selection.
 */
public class FontChooser extends JPanel {

    private Font selectedFont;

    protected Font[] displayFonts;
    protected DefaultComboBoxModel fontFaceModel;
    protected JComboBox face;

    /**
     * Create a FontChooser to choose between all fonts available on the system.
     */
    public FontChooser() {
	this(null);
    }
    
    public FontChooser(Font def) {
	this(GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts(),
	     def);
    }
    
    /**
     * Create a FontChooser to choose between the given array of fonts.
     */
    public FontChooser(Font[] srcFonts, Font def) {
	this.displayFonts = srcFonts;
	this.fontFaceModel = new DefaultComboBoxModel(displayFonts);
	
	this.face = new JComboBox(fontFaceModel);
	face.setRenderer(new FontRenderer());
	
	// set the prototype display for the combo box
	//face.setPrototypeDisplayValue("Really really long font name Bold Italic");
	face.addItemListener(new FontFaceSelectionListener());

	// set the currently selected face, default if null
	if (def == null)
	    this.selectedFont = new Font("dialog", Font.PLAIN, 1);
	else
	    this.selectedFont = def.deriveFont(1F);
	face.setSelectedItem(this.selectedFont);

	add(face);
    }

    public JComboBox getFaceComboBox() {
	return face;
    }

    public Font getSelectedFont() {
	return selectedFont;
    }

    private class FontFaceSelectionListener implements ItemListener {
	public void itemStateChanged(ItemEvent e) {
	    if (e.getStateChange() == ItemEvent.SELECTED) {
		JComboBox source = (JComboBox) e.getItemSelectable();
		selectedFont = (Font) source.getSelectedItem();
	    }
	}
    }
}
    /*
      Only display MSWord-type toolbar for now

    public JPanel getFontToolbar() {
	JPanel panel = new JPanel(false);
	JComboBox face = getFontFaceJComboBox();
	panel.add(face);
	panel.add(getFontSizeJComboBox(face));
	return panel;
    }

    public JList getFontFaceJList() {
	JList face = new JList(fontFaceModel);
	face.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	face.setCellRenderer(new FontRenderer(FontRenderer.FONT_FACE));
	return face;
    }

    public JComboBox getFontFaceJComboBox() {
	JComboBox face = new JComboBox(fontFaceModel);
	face.setRenderer(new FontRenderer(FontRenderer.FONT_FACE));
	return face;
    }

    public JList getFontSizeJList(JList fontFaceList) {
	JList list = new JList(fontSizeModel);
	list.setRenderer(new FontRenderer(FontRenderer.FONT_SIZE));
	return list;
    }

    public JComboBox getFontSizeJComboBox(JList fontFaceList) {
	JComboBox box = new JComboBox(fontSizeModel);
	box.setCellRenderer(new FontRenderer(FontRenderer.FONT_SIZE));
	return box;
    }

    public JList getFontSizeJList(JComboBox fontFaceList) {
	JList list = new JList(fontSizeModel);
	list.setCellRenderer(new FontRenderer(FontRenderer.FONT_SIZE));
	return list;
    }

    public JComboBox getFontSizeJComboBox(JComboBox fontFaceList) {
	JComboBox box = new JComboBox(fontSizeModel);
	box.setRenderer(new FontRenderer(FontRenderer.FONT_SIZE));
	return box;
    }
    */
			    
