
package org.example.tunable.internal.gui;

import java.lang.reflect.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;
import org.example.tunable.*;
import org.example.tunable.util.*;

public class ListSingleSelectionHandler<T> extends AbstractGuiHandler {

	ListSingleSelection<T> lss;
	JComboBox combobox;
	T selected;

	public ListSingleSelectionHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
		try {
            lss = (ListSingleSelection<T>) f.get(o);
        } catch(Exception e) {e.printStackTrace();}
	
		panel = new JPanel(new GridLayout());
		JPanel test1 = new JPanel(new BorderLayout());
		JPanel test2 = new JPanel();
		panel.add(test1);
		panel.add(test2);
		JTextArea jta = new JTextArea(t.description());
		jta.setLineWrap(true);
		jta.setWrapStyleWord(true);
		test1.add(jta,BorderLayout.CENTER);
		jta.setBackground(null);
		jta.setEditable(false);     
		combobox = new JComboBox(lss.getPossibleValues().toArray());
		combobox.setSelectedIndex(0);
		combobox.addActionListener(this);
		test2.add(combobox,BorderLayout.EAST);
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
	}

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
}
