
package Factory;

import java.lang.reflect.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;

import GuiInterception.AbstractGuiHandler;
import Tunable.*;
import Utils.*;

public class ListSingleHandler<T> extends AbstractGuiHandler {

	ListSingleSelection<T> lss;
	JComboBox combobox;
	T selected;

	public ListSingleHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
		try {
            lss = (ListSingleSelection<T>) f.get(o);
        } catch(Exception e) {e.printStackTrace();}
	
		panel = new JPanel();
		JTextArea jta = new JTextArea(t.description());
		jta.setLineWrap(true);
		jta.setWrapStyleWord(true);
		panel.add(jta);
		jta.setBackground(null);
		jta.setEditable(false);     
		combobox = new JComboBox(lss.getPossibleValues().toArray());
//		combobox.setSelectedIndex(0);
		combobox.addActionListener(this);
		panel.add(combobox);
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
