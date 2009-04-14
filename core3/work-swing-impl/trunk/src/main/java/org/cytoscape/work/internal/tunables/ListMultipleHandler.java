package org.cytoscape.work.internal.tunables;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.*;

import org.cytoscape.work.Tunable;
import org.cytoscape.work.internal.tunables.utils.CheckListManager;
import org.cytoscape.work.util.ListMultipleSelection;

import java.awt.*;

public class ListMultipleHandler<T> extends AbstractGuiHandler{

	ListMultipleSelection<T> lms;
	JList jlist;
	private List<T> selected;
	CheckListManager<T> checkListManager;
	Map<Integer,T> map;

	
	@SuppressWarnings("unchecked")
	protected ListMultipleHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
		try {
            lms = (ListMultipleSelection<T>) f.get(o);
        } catch(Exception e) {e.printStackTrace();}
        panel = new JPanel(new BorderLayout());
        JTextArea jta = new JTextArea(t.description());
        jta.setLineWrap(true);
        jta.setWrapStyleWord(true);
        panel.add(jta,BorderLayout.BEFORE_LINE_BEGINS);
        jta.setBackground(null);
        jta.setEditable(false);
        jlist = new JList(lms.getPossibleValues().toArray());
        jlist.setFont(new Font("sansserif",Font.PLAIN,11));
        jlist.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jlist.addListSelectionListener(this);
        //checkListManager = new CheckListManager<T>(jlist,lms);
        JScrollPane scrollpane = new JScrollPane(jlist);
        panel.add(scrollpane,BorderLayout.EAST);
	}

	

	public void handle(){
		selected = convertToArray(jlist.getSelectedValues());
		if (selected!=null) {
			lms.setSelectedValues(selected);
            try{
                f.set(o,lms);
            }catch(Exception e){e.printStackTrace();}
        }
	}
	
	
	public void resetValue() {
		lms.setSelectedValues(null);
		try{
			f.set(o,lms);
		}catch(Exception e){e.printStackTrace();}
		System.out.println("#########Value will be reset to initial value = " + lms.getSelectedValues() + "#########");
	}
	
	
	private List<T> convertToArray(Object[] in){
		List<T> list = new ArrayList<T>();
		if(in.length!=0){
			for(int i=0;i<in.length;i++){
				list.add(i, (T)in[i]);
			}
			return (List<T>) list;
		}
		else return null;
	}


	public String getState() {
		java.util.List<T> sel = lms.getSelectedValues();
		if ( sel == null )
			return "";
		else
			return sel.toString();
	}
}
