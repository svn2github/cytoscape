package Factory;

import TunableDefinition.*;

import java.lang.reflect.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import GuiInterception.*;


public class ListHandler implements Guihandler,ListSelectionListener{
	
	
	Field f;
	Object o;
	Tunable t;

	JList list;
	String[] data;
	Object[] values;
	Boolean multiselect;
	String title;
	
	public ListHandler(Field f, Object o, Tunable t){
		this.f = f;
		this.o = o;
		this.t = t;
		this.title=t.description();
		if(t.flag()==4)this.multiselect=true;
		this.data=t.data();
	}
	
	public JPanel getInputPanel(){
		JPanel pane = new JPanel();
		try{
			list = (JList) f.get(o);
		}catch(Exception e){e.printStackTrace();}
		list.setListData(data);
		list.addListSelectionListener(this);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);	
		if(multiselect)list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		pane.add(new JLabel(title));
		pane.add(new JScrollPane(list));
		return pane;
	}
	
	public void handle(){
		list.setListData(values);	
		try{
			f.set(o, list);
		}catch(Exception e){e.printStackTrace();}	
		
	}
	
	
	public JPanel getresultpanel(){
		JPanel resultpane = new JPanel();
		if(values!=null){
			try{
				list = (JList) f.get(o);
			}catch(Exception e){e.printStackTrace();}
			resultpane.add(new JScrollPane(list));
		}
		return resultpane;
	}

	
	@Override
	public void valueChanged(ListSelectionEvent event) {
		values = list.getSelectedValues();
		}

	@Override
	public Tunable getTunable() {
		return t;
	}

	@Override
	public JPanel update() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Field getField() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getObject() {
		// TODO Auto-generated method stub
		return null;
	}
}