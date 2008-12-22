package Factory;

import GuiInterception.Guihandler;
import Tunable.*;
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
	
	List<T> listIn;
	ListSingleSelection<T> LSS;
	JList jlist;
	private T selected;
	Boolean available;
	ArrayList<T> array;
	
	
	@SuppressWarnings("unchecked")
	public ListSingleHandler(Field f, Object o, Tunable t){
		this.f=f;
		this.o=o;
		this.t=t;
		this.available=t.available();
		try{
			listIn =  (List<T>) f.get(o);
		}catch(Exception e){e.printStackTrace();}
		LSS= new ListSingleSelection<T>(listIn);
	}

	
	
	public JPanel getInputPanel(){
		JPanel returnpane = new JPanel();		
		jlist=new JList(LSS.getPossibleValues().toArray());
		jlist.addListSelectionListener(this);
		jlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);		
		JScrollPane scrollpane = new JScrollPane(jlist);
		returnpane.add(scrollpane);
		return returnpane;
	}



	public void cancel() {
		try{
			f.set(o, listIn);
		}catch(Exception e){e.printStackTrace();}
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


	public Class<?> getclass() {
		return null;
	}



	public JPanel getresultpanel() {
		return null;
	}



	public void handle() {
		List<T> listOut = new ArrayList<T>();
		listOut.add(selected);
		if(available==true){
			try{
				f.set(o,listOut);
			}catch(Exception e){e.printStackTrace();}
		}
		else{
			try{
				f.set(o,listIn);
			}catch(Exception e){e.printStackTrace();}
		}
	}
	
		

	public JPanel update() {
		JPanel result = new JPanel();
		array = new ArrayList<T>();
		if(selected!=null){
			LSS.setSelectedValue(selected);
			array.add(selected);
		}
		result.add(new JScrollPane(new JList(array.toArray())));
		return result;
	}

	@SuppressWarnings("unchecked")
	public void valueChanged(ListSelectionEvent evt) {
		selected = (T)jlist.getSelectedValue();
	}



	@Override
	public Object getValue() {
		// TODO Auto-generated method stub
		return null;
	}	

}