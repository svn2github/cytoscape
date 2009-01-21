package Factory;

import Tunable.*;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import Utils.*;
import GuiInterception.*;

public class ListMultipleHandler<T> implements Guihandler,ListSelectionListener{

	
	Field f;
	Object o;
	Tunable t;

	ListMultipleSelection<T> LMS;
	private List<T> selected=null;
	JList jlist;
	Boolean available;
	Object[] array=null;
	String title;
	
	
	
	@SuppressWarnings("unchecked")
	public ListMultipleHandler(Field f, Object o, Tunable t){
		this.f=f;
		this.o=o;
		this.t=t;
		try{
			LMS=(ListMultipleSelection<T>) f.get(o);
		}catch(Exception e){e.printStackTrace();}
		this.title=t.description();
	}

	public JPanel getInputPanel() {
//		JPanel inpane = new JPanel(new BorderLayout());inpane
		JPanel inpane = new JPanel(new GridLayout());
		JPanel test1 = new JPanel(new BorderLayout());
		JPanel test2 = new JPanel();
		inpane.add(test1);
		inpane.add(test2);
		JTextArea jta = new JTextArea(title);
		jta.setLineWrap(true);
		jta.setWrapStyleWord(true);
//		inpane.add(jta);
		test1.add(jta,BorderLayout.CENTER);
		jta.setBackground(null);
		jta.setEditable(false);
		jlist = new JList(LMS.getPossibleValues().toArray());
		jlist.addListSelectionListener(this);
		jlist.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		JScrollPane scrollpane = new JScrollPane(jlist);
		test2.add(scrollpane,BorderLayout.EAST);
//		inpane.add(scrollpane,BorderLayout.EAST);
		return inpane;
	}
	
	public void handle() {
		if(array!=null){
			selected = castObject(array);
			LMS.setSelectedValues(selected);
			try{
				f.set(o, LMS);
			}catch(Exception e){e.printStackTrace();}
		}
	}
	
	
	
	public JPanel getOutputPanel() {
		JPanel outpane = new JPanel(new BorderLayout());
		JTextArea jta = new JTextArea(title);
		jta.setBackground(null);
		outpane.add(jta,BorderLayout.WEST);
		if(array!=null){
			selected = castObject(array);
			LMS.setSelectedValues(selected);
			try{
				f.set(o, LMS);
				outpane.add(new JScrollPane(new JList(LMS.getSelectedValues().toArray())),BorderLayout.EAST);
			}catch(Exception e){e.printStackTrace();}
		}
		return outpane;
	}

	
	
	
	public void valueChanged(ListSelectionEvent le) {
		array = jlist.getSelectedValues();		
	}
	
	
	@SuppressWarnings("unchecked")
	public ArrayList<T> castObject(Object[] in){
		ArrayList<T> array = new ArrayList<T>();
		T value;
		for(int i=0;i<in.length;i++){
			value=(T)in[i];
			array.add(i, value);
		}
	return array;
	}	
	
	
	public Object getObject() {
		return o;
	}
	public Field getField() {
		return f;
	}
	public Tunable getTunable() {
		return t;
	}
}