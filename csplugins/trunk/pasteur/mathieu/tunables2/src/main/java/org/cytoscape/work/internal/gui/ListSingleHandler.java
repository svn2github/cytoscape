
package org.cytoscape.work.internal.gui;

import java.lang.reflect.*;
import javax.swing.*;

import org.cytoscape.work.AbstractGuiHandler;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

import java.awt.*;


public class ListSingleHandler<T> extends AbstractGuiHandler {

	ListSingleSelection<T> lss;
	JComboBox combobox;
	T selected;

	@SuppressWarnings("unchecked")
	public ListSingleHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
		try {
            lss = (ListSingleSelection<T>) f.get(o);
        } catch(Exception e) {e.printStackTrace();}
	
		panel = new JPanel(new BorderLayout());
		JTextArea jta = new JTextArea(t.description());
		jta.setLineWrap(true);
		jta.setWrapStyleWord(true);
		panel.add(jta,BorderLayout.WEST);
		jta.setBackground(null);
		jta.setEditable(false);     
		combobox = new JComboBox(lss.getPossibleValues().toArray());
		combobox.setFont(new Font("sansserif",Font.PLAIN,11));
//		combobox.setSelectedIndex(0);
		combobox.addActionListener(this);
		panel.add(combobox,BorderLayout.EAST);
	}

	
	@SuppressWarnings("unchecked")
	public void handle() {
		selected = (T) combobox.getSelectedItem();	
        if(selected!=null){
            lss.setSelectedValue(selected);
            try{
                f.set(o,lss);
            }catch(Exception e){e.printStackTrace();}
        }
        //System.out.println(lss.getSelectedValue());
	}

	@SuppressWarnings("unchecked")
	public String getState() {
		selected = (T) combobox.getSelectedItem();	
        if(selected!=null)
            lss.setSelectedValue(selected);
		T sel = lss.getSelectedValue();
		if ( sel == null )
			return "";
		else
			return sel.toString();
	}

	@Override
	public void returnPanel() {
		panel.removeAll();
		panel.add(new JLabel(t.description()));
		panel.add(new JTextField(lss.getSelectedValue().toString()));
		
	}
}
