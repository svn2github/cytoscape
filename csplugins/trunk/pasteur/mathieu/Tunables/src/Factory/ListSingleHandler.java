package Factory;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import GuiInterception.Guihandler;
import Tunable.Tunable;
import Utils.ListSingleSelection;


public class ListSingleHandler<T>implements Guihandler{
	Field f;
	Object o;
	Tunable t;
	ListSingleSelection<T> LSS;
	private T selected;
	Boolean available;
	JComboBox combobox;
	String days;
	String title;
	
	/*-------------------------------Constructor-----------------------------------*/	
	public ListSingleHandler(Field f, Object o, Tunable t){
		this.f=f;
		this.o=o;
		this.t=t;		
		this.title=t.description();
		this.days=f.getName();
	}

	
	@SuppressWarnings("unchecked")
	/*-------------------------------Get the Panel with the INITIAL items that are in the input List-----------------------------------*/	
	public JPanel getPanel(){
		JPanel inpane = new JPanel(new GridLayout());
		JPanel test1 = new JPanel(new BorderLayout());
		JPanel test2 = new JPanel();
		inpane.add(test1);
		inpane.add(test2);
		selected = null;
		JTextArea jta = new JTextArea(title);
		jta.setLineWrap(true);
		jta.setWrapStyleWord(true);
		test1.add(jta,BorderLayout.CENTER);
		jta.setBackground(null);
		jta.setEditable(false);
		//Set the values from the input into the SingleSelection list
		try{
			LSS = (ListSingleSelection<T>) f.get(o);
		}catch(Exception e){e.printStackTrace();}
		//Set the JComboBox with the values from the input list
		combobox = new JComboBox(LSS.getPossibleValues().toArray());
		combobox.insertItemAt(days,0);
		combobox.setSelectedIndex(0);
		combobox.addActionListener(new myActionListener1());
		test2.add(combobox,BorderLayout.EAST);
		return inpane;
	}

	
	/*-------------------------------Detect the item that are selected-----------------------------------*/	
	public class myActionListener1 implements ActionListener{
		@SuppressWarnings("unchecked")
		public void actionPerformed(ActionEvent event){
			selected = (T) combobox.getSelectedItem();
		}
	}

	
	/*-------------------------------Get the Panel which displays the Item that has been selected from the list-----------------------------------*/		
	public JPanel getOutputPanel() {
		JPanel outpane = new JPanel();
		JTextArea jta = new JTextArea(title);
		jta.setBackground(null);
		outpane.add(jta,BorderLayout.WEST);
		handle();
		JTextField jtf2 = new JTextField(LSS.getSelectedValue().toString());
		jtf2.setEditable(false);
		outpane.add(jtf2,BorderLayout.EAST);
		return outpane;
	}

	/*-------------------------------Set the SingleSelectionList Object with the item that has been selected-----------------------------------*/	
	public void handle() {
		if(selected!=null){
			LSS.setSelectedValue(selected);
			try{
				f.set(o,LSS);
			}catch(Exception e){e.printStackTrace();}
		}
	}

	
	
//	@SuppressWarnings("unchecked")
//	public void valueChanged(ListSelectionEvent e) {
//		selected = (T) jlist.getSelectedValue();
//	}

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