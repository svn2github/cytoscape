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
	java.util.List<T> list;
	ListMultipleSelection<T> LMS;
	private Object[] x;
	private List<T> selected = new ArrayList<T>();
	JList jlist;
	
	
	public ListMultipleHandler(Field f, Object o, Tunable t){
		this.f=f;
		this.o=o;
		this.t=t;
		try{
			list=(List<T>) f.get(o);
		}catch(Exception e){e.printStackTrace();}
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		
	}

	public Field getField() {
		return f;
	}

	public JPanel getInputPanel() {
		JPanel result = new JPanel();
		LMS= new ListMultipleSelection<T>(list);
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

	@Override
	public Class<?> getclass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JPanel getresultpanel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void handle() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public JPanel update() {
		JPanel resultpane = new JPanel();
		System.out.println(x.length);
		selected = castObject(x);
		LMS.setSelectedValues(selected);

		JList listout=new JList(selected.toArray());
		
		JScrollPane scroll = new JScrollPane(listout);
		resultpane.add(scroll);
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