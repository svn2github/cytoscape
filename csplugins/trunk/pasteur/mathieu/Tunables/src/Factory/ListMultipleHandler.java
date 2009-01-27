package Factory;

import Tunable.*;
import java.awt.*;
import java.lang.reflect.*;
import java.util.List;
import javax.swing.*;
import Utils.*;
import GuiInterception.*;

public class ListMultipleHandler<T> implements Guihandler{
	Field f;
	Object o;
	Tunable t;
	ListMultipleSelection<T> LMS;
	private List<T> selected = null;
	static JList jlist;
	Boolean available;
	String title;
	CheckListManager<T> checkListManager;
	
	
	
	/*-------------------------------Constructor-----------------------------------*/	
	public ListMultipleHandler(Field f, Object o, Tunable t){
		this.f=f;
		this.o=o;
		this.t=t;
		this.title=t.description();
	}
	
	/*-------------------------------Get the Panel with the INITIAL items that are in the input List-----------------------------------*/	
	@SuppressWarnings("unchecked")
	public JPanel getPanel() {
		JPanel inpane = new JPanel(new GridLayout());
		JPanel test1 = new JPanel(new BorderLayout());
		JPanel test2 = new JPanel();
		inpane.add(test1);
		inpane.add(test2);
		JTextArea jta = new JTextArea(title);
		jta.setLineWrap(true);
		jta.setWrapStyleWord(true);
		test1.add(jta,BorderLayout.CENTER);
		jta.setBackground(null);
		jta.setEditable(false);
		//Set the items from the input list into the MultipleSelection list
		try{
			LMS=(ListMultipleSelection<T>) f.get(o);
		}catch(Exception e){e.printStackTrace();}
		//Set the JList with the available items(from MultipleSelectionList)
		jlist = new JList(LMS.getPossibleValues().toArray());
		jlist.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		//Set the MultipleSelection with checkBoxes to make the multiple selection
		checkListManager = new CheckListManager<T>(jlist,LMS); 
		JScrollPane scrollpane = new JScrollPane(jlist);
		test2.add(scrollpane,BorderLayout.EAST);
		return inpane;
	}
	
	
	/*-------------------------------Get the JScrollPane which displays the item(s) that have been selected from the list-----------------------------------*/			
	public JPanel getOutputPanel() {
		JPanel outpane = new JPanel();
		JTextArea jta = new JTextArea(title);
		jta.setBackground(null);
		outpane.add(jta,BorderLayout.WEST);
		handle();
		JScrollPane scrollpane = new JScrollPane(new JList(LMS.getSelectedValues().toArray()));
		scrollpane.setEnabled(false);
		outpane.add(scrollpane,BorderLayout.EAST);
		return outpane;
	}	
	
	
	/*-------------------------------Set the MultipleSelectionList Object with the item(s) that has been checked(selected)-----------------------------------*/	
	@SuppressWarnings("unchecked")
	public void handle() {
			selected = checkListManager.getArray();
			if(selected!=null){
				LMS.setSelectedValues(selected);
				try{
					f.set(o, LMS);
				}catch(Exception e){e.printStackTrace();}
			}
	}
	
	
	
	public Object getObject() {
		return o;
	}
	public Field getField() {
		return f;
	}
	public Tunable getTunable() {
		return t;
	}
}