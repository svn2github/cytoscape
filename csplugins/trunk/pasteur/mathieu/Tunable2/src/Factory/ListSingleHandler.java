package Factory;

import GuiInterception.Guihandler;
import Tunable.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

import Utils.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ListSingleHandler<T> implements Guihandler,ListSelectionListener{
	
	Field f;
	Object o;
	Tunable t;
	
	java.util.List<T> list;
	ListSingleSelection<T> LSS;
	JList jlist;
	private T selected;
	
	
	
	public ListSingleHandler(Field f, Object o, Tunable t){
		this.f=f;
		this.o=o;
		this.t=t;	
	}

	
	
	public JPanel getInputPanel(){
		JPanel returnpane = new JPanel();
		try{
			list =  (List<T>) f.get(o);
		}catch(Exception e){e.printStackTrace();}
		LSS= new ListSingleSelection<T>(list);
		//LSS.getPossibleValues();
		
		jlist=new JList(LSS.getPossibleValues().toArray());
		jlist.addListSelectionListener(this);
		jlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		
		JScrollPane scrollpane = new JScrollPane(jlist);
		returnpane.add(scrollpane);
		return returnpane;
		
		
	}



	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		
	}



	public Field getField() {
		return f;
	}

	public Object getObject() {
		return o;
	}



	public Tunable getTunable() {
		return t;
	}



	@Override
	public Class<?> getclass() {
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
		JPanel result = new JPanel();
		LSS.setSelectedValue(selected);
		ArrayList<T> array = new ArrayList<T>();
		array.add(selected);

		JList listout=new JList(array.toArray());
		
		JScrollPane scrollpane = new JScrollPane(listout);
		result.add(scrollpane);
		return result;
	}

	public void valueChanged(ListSelectionEvent evt) {
		//selected=LSS.getSelectedValue();
		selected = (T)jlist.getSelectedValue();
		
	}


	
	
}
