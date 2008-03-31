/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: rqluk $
 * $RCSfile: FontSettingsPanel.java,v $
 * $Revision: 1.1 $B
 * $Date: 2006/08/16 19:13:46 $
 * $Name:  $
 *
 * This file is part of Java TreeView
 * Copyright (C) 2001-2003 Alok Saldanha, All Rights Reserved. Modified by Alex Segal 2004/08/13. Modifications Copyright (C) Lawrence Berkeley Lab.
 *
 * This software is provided under the GNU GPL Version 2. In particular, 
 *
 * 1) If you modify a source file, make a comment in it containing your name and the date.
 * 2) If you distribute a modified version, you must do it under the GPL 2.
 * 3) Developers are encouraged but not required to notify the Java TreeView maintainers at alok@genome.stanford.edu when they make a useful addition. It would be nice if significant contributions could be merged into the main distribution.
 *
 * A full copy of the license can be found in gpl.txt or online at
 * http://www.gnu.org/licenses/gpl.txt
 *
 * END_HEADER 
 */
package edu.stanford.genetics.treeview.plugin.dendroview;

import edu.stanford.genetics.treeview.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
* This class allows selection of Fonts for a FontSelectable.
*/
public class FontSettingsPanel extends JPanel implements SettingsPanel {
	private FontSelectable client;

	public FontSettingsPanel(FontSelectable fs) {
		client = fs;
		setupWidgets();
		updateExample();
	}
	
	public static void main(String [] argv) {
		HeaderInfo hi = new DummyHeaderInfo();
		UrlExtractor ue = new UrlExtractor(hi);
		
		FontSelectable fs = new TextView(hi, ue);
		fs.setPoints(10);
		FontSettingsPanel e  = new FontSettingsPanel(fs);
		JFrame f = new JFrame("Font Settings Test");
		f.add(e);
		f.addWindowListener(new WindowAdapter (){
			public void windowClosing(WindowEvent we) 
			{System.exit(0);}
		});
		f.pack();
		f.show();
    }

	public void synchronizeFrom() {
	  setupWidgets();
	}

	public void synchronizeTo() {
	  //nothing to do...
	}
	


	//
// the allowed font styles
//
	/**
	 *  Description of the Field
	 */
	public final static String[] styles  = {
			"Plain",
			"Italic",
			"Bold",
			"Bold Italic"
			};

	/**
	* turn a style number from class java.awt.Font into a string
	 *
	 * @param  style  style index
	 * @return        string description
	 */
	public final static String decode_style(int style) {
		switch (style) {
						case Font.PLAIN:
							return styles[0];
						case Font.ITALIC:
							return styles[1];
						case Font.BOLD:
							return styles[2];
						default:
							return styles[3];
		}
	}

	/**
	* turn a string into a style number
	 *
	 * @param  style  string description
	 * @return        integer encoded representation
	 */
	public final static int encode_style(String style) {
		return
				style == styles[0] ? Font.PLAIN :
				style == styles[1] ? Font.ITALIC :
				style == styles[2] ? Font.BOLD :
				Font.BOLD + Font.ITALIC;
	}

	/**
	 *  Create a blocking dialog containing this component
	 *
	 * @param  f  frame to block
	 */
	public void showDialog(Frame f, String title) {
		JDialog d = new JDialog(f, title);
		d.setLayout(new BorderLayout());
		d.add(this, BorderLayout.CENTER);
		d.add(new ButtonPanel(d), BorderLayout.SOUTH);
		d.addWindowListener(
			new WindowAdapter() {
				public void windowClosing(WindowEvent we) {
					we.getWindow().dispose();
				}
			});
		d.pack();
		d.show();
	}


	  
	 private String title;
	private JComboBox font_choice;
	private JComboBox style_choice;
	private NatField size_field;
	private JButton display_button;
	private JLabel exampleField;
	private JButton updateButton;

	String size_prop, face_prop, style_prop;

	
	
	private void setupFontChoice() {
		font_choice = new JComboBox(FontSelector.fonts);
		font_choice.setSelectedItem(client.getFace());
	}
	private void setupStyleChoice() {
		style_choice = new JComboBox(styles);
		style_choice.setSelectedItem(decode_style(client.getStyle()));
	}
	
	private void synchronizeClient() {
		String string  = (String) font_choice.getSelectedItem();
		int i          = encode_style((String) style_choice.getSelectedItem());
		int size       = size_field.getNat();
		client.setFace(string);
		client.setStyle(i);
		client.setPoints(size);
	}
	/**
	*  Sets up widgets
	*/
	private void setupWidgets() {
		removeAll();
		GridBagLayout gbl  = new GridBagLayout();
		setLayout(gbl);
		GridBagConstraints gbc  = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		
		setupFontChoice();
		add(font_choice, gbc);
		
		setupStyleChoice();
		gbc.gridx = 1;
		add(style_choice, gbc);
		
		size_field = new NatField(client.getPoints(), 3);
		gbc.gridx = 2;
		add(size_field, gbc);
		
		display_button = new JButton("Set");
		display_button.addActionListener(
		new ActionListener() {
			
			public void actionPerformed(ActionEvent actionEvent) {
				updateExample();
				synchronizeClient();
			}
		});
		gbc.gridx = 3;
		add(display_button, gbc);
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 3;
		gbc.fill = GridBagConstraints.BOTH;
		exampleField = new JLabel("Font Example Text", JLabel.CENTER);
		add(exampleField, gbc);
	}
	
	private void updateExample() {
		String string  = (String)font_choice.getSelectedItem();
		int i          = encode_style((String)style_choice.getSelectedItem());
		int size       = size_field.getNat();
		//				System.out.println("Setting size to " + size);
		exampleField.setFont(new Font(string, i, size) );
		exampleField.revalidate();
		exampleField.repaint();
	}
	private class ButtonPanel extends JPanel {
	ButtonPanel(Window w) {
		final Window window = w;
	    JButton save_button = new JButton("Close");
	    save_button.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			window.hide();
		    }
		});
	    add(save_button);

    }    
	}
}

