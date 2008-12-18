package Factory;

import Tunable.*;
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
	List<T> listIn;
	ListMultipleSelection<T> LMS;
	private Object[] x;
	private List<T> selected = new ArrayList<T>();
	JList jlist;
	Boolean available;
	
	
	public ListMultipleHandler(Field f, Object o, Tunable t){
		this.f=f;
		this.o=o;
		this.t=t;
		this.available=t.available();
		try{
			listIn=(List<T>) f.get(o);
		}catch(Exception e){e.printStackTrace();}
		LMS= new ListMultipleSelection<T>(listIn);
	}


	
	public void cancel() {
		try{
			f.set(o, listIn);
		}catch(Exception e){e.printStackTrace();}
	}

	
	
	public Field getField() {
		return f;
	}

	public JPanel getInputPanel() {
		JPanel result = new JPanel();
		jlist = new JList(LMS.getPossibleValues().toArray());
		jlist.addListSelectionListener(this);
		jlist.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		JScrollPane scroll = new JScrollPane(jlist);
		result.add(scroll);
		return result;
	}

	public Object getObject() {
		return o;
	}

	public Tunable getTunable() {
		return t;
	}

	public Class<?> getclass() {
		return null;
	}


	public JPanel getresultpanel() {
		return null;
	}


	
	public void handle() {
		if(available==true){
			try{
				f.set(o, selected);
			}catch(Exception e){e.printStackTrace();}
		}
		else{
			try{
				f.set(o, listIn);
			}catch(Exception e){e.printStackTrace();}
		}	
	}

	
	public JPanel update() {
		JPanel resultpane = new JPanel();
		if(x!=null){
			selected = castObject(x);
			LMS.setSelectedValues(selected);
		}
		resultpane.add(new JScrollPane(new JList(selected.toArray())));
		return resultpane;
	}

	
	public void valueChanged(ListSelectionEvent le) {
		x = jlist.getSelectedValues();		
	}
	
	
	public ArrayList<T> castObject(Object[] in){
		ArrayList<T> array = new ArrayList<T>();
		T value;
		for(int i=0;i<in.length;i++){
			value=(T)in[i];
			array.add(i, value);
		}
	return array;
	}
	
}