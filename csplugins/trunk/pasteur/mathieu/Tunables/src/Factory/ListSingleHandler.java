package Factory;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.util.ArrayList;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
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
	JComboBox combobox;
	String days;
	

	@SuppressWarnings("unchecked")
	public ListSingleHandler(Field f, Object o, Tunable t){
		this.f=f;
		this.o=o;
		this.t=t;
		try{
			LSS =  (ListSingleSelection<T>) f.get(o);
		}catch(Exception e){e.printStackTrace();}
	}



	@Override
	public JPanel getInputPanel() {
		JPanel returnpane = new JPanel(new BorderLayout());
		selected = null;
		JTextArea jta = new JTextArea(t.description());
		jta.setBackground(null);
		combobox = new JComboBox(LSS.getPossibleValues().toArray());
		combobox.insertItemAt(f.getName(),0);
		combobox.setSelectedIndex(0);
		combobox.addActionListener(new myActionListener1());
		returnpane.add(combobox,BorderLayout.EAST);
		returnpane.add(jta,BorderLayout.WEST);
		return returnpane;
	}

	public class myActionListener1 implements ActionListener{
		@SuppressWarnings("unchecked")
		public void actionPerformed(ActionEvent event){
			selected = (T) combobox.getSelectedItem();
		}
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
		JPanel result = new JPanel(new BorderLayout());
		JTextArea jta = new JTextArea(f.getName());
		jta.setBackground(null);
		result.add(jta,BorderLayout.WEST);
		if(selected!=null){
			LSS.setSelectedValue(selected);	
			try{
				f.set(o,LSS);
				result.add(new JTextField((String) LSS.getSelectedValue()),BorderLayout.EAST);
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
}