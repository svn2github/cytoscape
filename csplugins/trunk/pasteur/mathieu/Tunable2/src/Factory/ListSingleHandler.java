package Factory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import GuiInterception.Guihandler;
import Tunable.Tunable;
import Utils.ListSingleSelection;


public class ListSingleHandler<T>implements Guihandler,ListSelectionListener{
	Field f;
	Object o;
	Tunable t;
	
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
			LSS =  (ListSingleSelection<T>) f.get(o);
		}catch(Exception e){e.printStackTrace();}
	}



	@Override
	public JPanel getInputPanel() {
		JPanel returnpane = new JPanel();
		selected=null;
		jlist=new JList(LSS.getPossibleValues().toArray());
		jlist.addListSelectionListener(this);
		jlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);		
		JScrollPane scrollpane = new JScrollPane(jlist);
		returnpane.add(scrollpane);
		return returnpane;
	}



	public void handle() {
		if(selected!=null){
			LSS.setSelectedValue(selected);
			try{
				f.set(o,LSS);
			}catch(Exception e){e.printStackTrace();}
		}
	}
		


	public JPanel update() {
		JPanel result = new JPanel();
		if(selected!=null){
			LSS.setSelectedValue(selected);	
			try{
				f.set(o,LSS);
				result.add(new JTextField((String) LSS.getSelectedValue()));
			}catch(Exception e){e.printStackTrace();}
		}
		return result;
	}


	@SuppressWarnings("unchecked")
	public void valueChanged(ListSelectionEvent e) {
		selected = (T) jlist.getSelectedValue();
	}

	public Object getObject() {
		return o;
	}
	public Tunable getTunable() {
		return t;
	}
	public Field getField() {
		return f;
	}
	public Class<?> getclass() {
		return null;
	}
}