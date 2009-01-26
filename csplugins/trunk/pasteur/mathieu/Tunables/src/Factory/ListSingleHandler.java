package Factory;

import java.awt.BorderLayout;
import java.awt.GridLayout;
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
	String title;
	

	@SuppressWarnings("unchecked")
	public ListSingleHandler(Field f, Object o, Tunable t){
		this.f=f;
		this.o=o;
		this.t=t;
		try{
			LSS = (ListSingleSelection<T>) f.get(o);
		}catch(Exception e){e.printStackTrace();}
		this.title=t.description();
		this.days=f.getName();
	}

	public JPanel getInputPanel(){
		
//		JPanel inpane = new JPanel(new BorderLayout());
		JPanel inpane = new JPanel(new GridLayout());
		JPanel test1 = new JPanel(new BorderLayout());
		JPanel test2 = new JPanel();
		inpane.add(test1);
		inpane.add(test2);
		selected = null;
		JTextArea jta = new JTextArea(title);
		jta.setLineWrap(true);
		jta.setWrapStyleWord(true);
//		inpane.add(jta);
		test1.add(jta,BorderLayout.CENTER);
		jta.setBackground(null);
		jta.setEditable(false);		combobox = new JComboBox(LSS.getPossibleValues().toArray());
		combobox.insertItemAt(days,0);
		combobox.setSelectedIndex(0);
		combobox.addActionListener(new myActionListener1());
		test2.add(combobox,BorderLayout.EAST);
//		inpane.add(combobox,BorderLayout.EAST);
		return inpane;
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
		


	public JPanel getOutputPanel() {
		JPanel outpane = new JPanel();
		JTextArea jta = new JTextArea(title);
		jta.setBackground(null);
		outpane.add(jta,BorderLayout.WEST);
		if(selected!=null){
			LSS.setSelectedValue(selected);	
			try{
				f.set(o,LSS);
				JTextField jtf2 = new JTextField(LSS.getSelectedValue().toString());
				jtf2.setEditable(false);
				outpane.add(jtf2,BorderLayout.EAST);
			}catch(Exception e){e.printStackTrace();}
		}
		return outpane;
	}


	@SuppressWarnings("unchecked")
	public void valueChanged(ListSelectionEvent e) {
		selected = (T) jlist.getSelectedValue();
		System.out.println(selected);
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