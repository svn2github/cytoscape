
package Factory;

import TunableDefinition.*;
import TunableDefinition.Tunable.Param;
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
	JList listOut;
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
		
		listIn.addListSelectionListener(this);
		listIn.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		if(t.flag()==Param.MultiSelect)listIn.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		pane.add(new JLabel(title));
		JScrollPane scrollpane = new JScrollPane(listIn);
		pane.add(scrollpane);
		return pane;
	}
	
	public void handle(){
		listOut = new JList(values);
		//listOut.setListData(values);	
		try{
			f.set(o, listOut);
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

	
	public void valueChanged(ListSelectionEvent event) {
		values = listIn.getSelectedValues();
		}




	public JPanel update() {
		JPanel result = new JPanel();
		if(values!=null){
			listOut = new JList(values);
			JScrollPane resultscroll = new JScrollPane(listOut);
			result.add(resultscroll);
		}
		return result;
	}

	
	public void cancel() {
		try{
			f.set(o, listIn);
		}catch(Exception e){e.printStackTrace();}
	}

	
	public Tunable getTunable() {
		return t;
	}

	public Field getField() {
		return f;
	}

	public Object getObject() {
		return o;
	}
}
