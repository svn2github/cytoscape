
package Factory;

import java.lang.reflect.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.*;

import java.awt.event.*;
import java.awt.*;

import GuiInterception.AbstractGuiHandler;
import Tunable.*;
import Utils.*;

public class ListMultipleHandler<T> extends AbstractGuiHandler implements ListSelectionListener {

	ListMultipleSelection<T> lms;
	JList jlist;
//	T selected;
	private List<T> selected;
	CheckListManager<T> checkListManager;

	public ListMultipleHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
		try {
            lms = (ListMultipleSelection<T>) f.get(o);
        } catch(Exception e) {e.printStackTrace();}
	
        panel = new JPanel();
        JTextArea jta = new JTextArea(t.description());
        jta.setLineWrap(true);
        jta.setWrapStyleWord(true);
        panel.add(jta);
        jta.setBackground(null);
        jta.setEditable(false);
        jlist = new JList(lms.getPossibleValues().toArray());
        jlist.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        checkListManager = new CheckListManager<T>(jlist,lms); 
        JScrollPane scrollpane = new JScrollPane(jlist);
        panel.add(scrollpane);
	}

	@SuppressWarnings("unchecked")
	public void handle() {
		//T[] selected = (T[]) jlist.getSelectedValues();
		selected = checkListManager.getArray();
		if (selected!=null) {
            lms.setSelectedValues(selected);
            try{
                f.set(o,lms);
            }catch(Exception e){e.printStackTrace();}
        }
	}

    public void valueChanged(ListSelectionEvent le) {
		handle();
    }

	public String getState() {
		java.util.List<T> sel = lms.getSelectedValues();
		if ( sel == null )
			return "";
		else
			return sel.toString();
	}

	@Override
	public void returnPanel() {
		panel.removeAll();
		panel.add(new JLabel(t.description()));
		panel.add(new JScrollPane(new JList(lms.getSelectedValues().toArray())));
		
	}
}
