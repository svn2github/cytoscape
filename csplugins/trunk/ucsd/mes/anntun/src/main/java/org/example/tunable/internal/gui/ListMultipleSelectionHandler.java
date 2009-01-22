
package org.example.tunable.internal.gui;

import java.lang.reflect.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;
import org.example.tunable.*;
import org.example.tunable.util.*;

public class ListMultipleSelectionHandler<T> extends AbstractGuiHandler implements ListSelectionListener {

	ListMultipleSelection<T> lss;
	JList jlist;
	T selected;

	public ListMultipleSelectionHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
		try {
            lss = (ListMultipleSelection<T>) f.get(o);
        } catch(Exception e) {e.printStackTrace();}
	}
	
	public JPanel getJPanel() {
        JPanel inpane = new JPanel(new GridLayout());
        JPanel test1 = new JPanel(new BorderLayout());
        JPanel test2 = new JPanel();
        inpane.add(test1);
        inpane.add(test2);
        JTextArea jta = new JTextArea(t.description());
        jta.setLineWrap(true);
        jta.setWrapStyleWord(true);
        test1.add(jta,BorderLayout.CENTER);
        jta.setBackground(null);
        jta.setEditable(false);
        jlist = new JList(lss.getPossibleValues().toArray());
        jlist.addListSelectionListener(this);
        jlist.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollpane = new JScrollPane(jlist);
        test2.add(scrollpane,BorderLayout.EAST);
        return inpane;			
	}

	@SuppressWarnings("unchecked")
	public void handle() {
		T[] selected = (T[]) jlist.getSelectedValues();
		if ( selected.length > 0 ) {
            lss.setSelectedValues(Arrays.asList(selected));
            try{
                f.set(o,lss);
            }catch(Exception e){e.printStackTrace();}
        }
	}

    public void valueChanged(ListSelectionEvent le) {
		handle();
    }

}
