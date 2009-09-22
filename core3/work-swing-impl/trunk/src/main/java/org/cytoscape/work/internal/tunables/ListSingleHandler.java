package org.cytoscape.work.internal.tunables;

import java.lang.reflect.*;
import javax.swing.*;

import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

import java.awt.*;


/**
 * Handler for the type <i>ListSingleSelection</i> of <code>Tunable</code>
 * 
 * @author pasteur
 *
 * @param <T> type of items the List contains
 */
public class ListSingleHandler<T> extends AbstractGuiHandler {

	private ListSingleSelection<T> listSingleSelection;
	private ListSingleSelection<T> initList;
	private JComboBox combobox;
	private T selectedItem;

	/**
	 * Constructs the <code>Guihandler</code> for the <code>ListSingleSelection</code> type
	 * 
	 * creates a ComboBox to collect all the <code>T</code> items and displays it in the GUI
	 * Informations about the list and its contents are also displayed
	 * 
	 * @param f field that has been annotated
	 * @param o object contained in <code>f</code>
	 * @param t tunable associated to <code>f</code>
	 */
	@SuppressWarnings("unchecked")
	protected ListSingleHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
		try {
            listSingleSelection = (ListSingleSelection<T>) f.get(o);
            initList = (ListSingleSelection<T>) f.get(o);
        } catch(Exception e) {e.printStackTrace();}
	
        //set Gui
		panel = new JPanel(new BorderLayout());
		JTextArea textArea = new JTextArea(t.description());
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		panel.add(textArea,BorderLayout.WEST);
		textArea.setBackground(null);
		textArea.setEditable(false);
		
		//add list's items to the combobox
		combobox = new JComboBox(listSingleSelection.getPossibleValues().toArray());
		combobox.setFont(new Font("sansserif",Font.PLAIN,11));
		combobox.addActionListener(this);
		panel.add(combobox,BorderLayout.EAST);
	}

	
	
	/**
	 * set the item that is currently selected in the ComboBox as the only possible item selected in <code>listSingleSelection</code>
	 */
	@SuppressWarnings("unchecked")
	public void handle() {
		selectedItem = (T) combobox.getSelectedItem();
        if(selectedItem!=null){
            listSingleSelection.setSelectedValue(selectedItem);
            try{
                f.set(o,listSingleSelection);
            }catch(Exception e){e.printStackTrace();}
        }
	}

	
	/**
	 * To remove the item that has been selected, and set the <code>listSingleSelection</code> in its initial state
	 */
	public void resetValue() {
		try{
			f.set(o,initList);
		}catch(Exception e){e.printStackTrace();}
	}
	
	
	
	/**
	 * To get the item that is currently selected
	 */
	public String getState() {
		selectedItem = (T) combobox.getSelectedItem();	
        if(selectedItem != null)
            listSingleSelection.setSelectedValue(selectedItem);
		T selection = listSingleSelection.getSelectedValue();
		if ( selection == null )
			return "";
		else
			return selection.toString();
	}
}
