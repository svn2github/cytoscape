
package Factory;

import TunableDefinition.*;

import java.awt.List;
import java.lang.reflect.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import GuiInterception.*;


public class ListHandler implements Guihandler,ListSelectionListener{
	
	
	Field f;
	Object o;
	Tunable t;

	JList listIn;
	List listOut=new List();
	String[] data;
	Object[] values;
	Boolean multiselect;
	String title;
	
	public ListHandler(Field f, Object o, Tunable t){
		this.f = f;
		this.o = o;
		this.t = t;
	}
	
	public JPanel getInputPanel(){
		JPanel pane = new JPanel();
		try{
			listIn = (JList) f.get(o);
		}catch(Exception e){e.printStackTrace();}
		values = listIn.getSelectedValues();
		DefaultListModel model = new DefaultListModel();
		int pos=0;
		for(Object ii : values){
			model.add(pos,ii);
			pos++;
		}
		
		for(int i=0;i<3;i++){
			listIn.setSelectedIndex(i);
			System.out.println("listesortir:"+listIn.getSelectedValue());
			listOut.add(listIn.getSelectedValue().toString());
		}
		//System.out.println("listesortir:"+listOut.getItem(0));

		
		//list.setListData(data);
		//list.addListSelectionListener(this);
		//list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);	
		//if(multiselect)list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		pane.add(new JLabel(title));
		//pane.add(new JScrollPane(list));
		return pane;
	}
	
	public void handle(){
		//list.setListData(values);	
		try{
		//	f.set(o, list);
		}catch(Exception e){e.printStackTrace();}	
		
	}
	
	
	public JPanel getresultpanel(){
		JPanel resultpane = new JPanel();
		if(values!=null){
			try{
		//		list = (JList) f.get(o);
			}catch(Exception e){e.printStackTrace();}
			//resultpane.add(new JScrollPane(list));
		}
		return resultpane;
	}

	
	@Override
	public void valueChanged(ListSelectionEvent event) {
	//	values = list.getSelectedValues();
		}

	@Override
	public Tunable getTunable() {
		return t;
	}


	public JPanel update() {

		return null;
	}

	public void cancel() {
		
	}


	public Field getField() {
		return f;
	}

	public Object getObject() {
		return o;
	}
}
