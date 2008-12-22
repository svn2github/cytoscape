package Factory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
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

public class ListSingleHandler2<T>implements Guihandler,ListSelectionListener{
	Field f;
	Object o;
	Tunable t;
	
	ListSingleSelection<T> listIn;
//	ListSingleSelection<T> LSS;
	JList jlist;
	private T selected;
	Boolean available;
	ArrayList<T> array;
	

	@SuppressWarnings("unchecked")
	public ListSingleHandler2(Field f, Object o, Tunable t){
		this.f=f;
		this.o=o;
		this.t=t;
		this.available=t.available();
		try{
			listIn =  (ListSingleSelection<T>) f.get(o);
		}catch(Exception e){e.printStackTrace();}
	}


	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public JPanel getInputPanel() {
		JPanel returnpane = new JPanel();		
		jlist=new JList(listIn.getPossibleValues().toArray());
		jlist.addListSelectionListener(this);
		jlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);		
		JScrollPane scrollpane = new JScrollPane(jlist);
		returnpane.add(scrollpane);
		return returnpane;
	}



	public void handle() {
		if(available==true){
			try{
				System.out.println(f.get(o).getClass());
				
			}catch(Exception e){e.printStackTrace();}
		}
//		else{
//			try{
//				f.set(o,listIn);
//			}catch(Exception e){e.printStackTrace();}
//		}
		
	}


	public JPanel update() {
		JPanel result = new JPanel();
		if(selected!=null){
			listIn.setSelectedValue(selected);
		}
		result.add(new JTextField((String) selected));
		return result;
	}


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


	@Override
	public T getValue() {
		return listIn.getSelectedValue();
	}

}