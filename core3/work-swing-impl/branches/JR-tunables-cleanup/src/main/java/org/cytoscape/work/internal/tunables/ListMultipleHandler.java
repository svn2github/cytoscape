package org.cytoscape.work.internal.tunables;

import java.awt.BorderLayout;
import java.awt.Font;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;

import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListMultipleSelection;


/**
 * Handler for the type <i>ListMultipleSelection</i> of <code>Tunable</code>
 * 
 * @author pasteur
 *
 * @param <T> type of items the List contains
 */
public class ListMultipleHandler<T> extends AbstractGuiHandler{

	private ListMultipleSelection<T> listMultipleSelection;
	private ListMultipleSelection<T> initList;
	private JList itemsContainerList;
	private List<T> selectedItems;
	//private CheckListManager<T> checkListManager;
	//private Map<Integer,T> map;

	
	
	/**
	 * Constructs the <code>Guihandler</code> for the <code>ListMultipleSelection</code> type
	 * 
	 * creates a list to collect all the <code>T</code> items and displays it in the GUI through a JScrollPane
	 * Informations about the list and its contents are also displayed
	 * 
	 * @param f field that has been annotated
	 * @param o object contained in <code>f</code>
	 * @param t tunable associated to <code>f</code>
	 */
	@SuppressWarnings("unchecked")
	protected ListMultipleHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
		try {
			listMultipleSelection = (ListMultipleSelection<T>) f.get(o);
            initList = (ListMultipleSelection<T>) f.get(o);
        } catch(Exception e) {e.printStackTrace();}
        
        //create Gui
        panel = new JPanel(new BorderLayout());
        JTextArea jta = new JTextArea(t.description());
        jta.setLineWrap(true);
        jta.setWrapStyleWord(true);
        panel.add(jta,BorderLayout.BEFORE_LINE_BEGINS);
        jta.setBackground(null);
        jta.setEditable(false);
        
        //put the items in a list
        itemsContainerList = new JList(listMultipleSelection.getPossibleValues().toArray());
        itemsContainerList.setFont(new Font("sansserif",Font.PLAIN,11));
        itemsContainerList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        itemsContainerList.addListSelectionListener(this);
        //checkListManager = new CheckListManager<T>(jlist,lms);
        
        //use a JscrollPane to visualize the items
        JScrollPane scrollpane = new JScrollPane(itemsContainerList);
        panel.add(scrollpane,BorderLayout.EAST);
	}

	

	/**
	 * set the items that are currently selected in the <code>itemsContainerList</code> as the selected items in <code>listMultipleSelection</code>
	 */
	public void handle(){
		selectedItems = convertToArray(itemsContainerList.getSelectedValues());
		if (selectedItems!=null) {
			listMultipleSelection.setSelectedValues(selectedItems);
            try{
                f.set(o,listMultipleSelection);
            }catch(Exception e){e.printStackTrace();}
        }
	}
	
	
	/**
	 * To remove the items that have been selected, and set the <code>listMultipleSelection</code> in its initial state
	 */
	public void resetValue() {
		try{
			f.set(o,initList);
		}catch(Exception e){e.printStackTrace();}
	}
	
	
	//converts the array that contains the selected items into a List to be able to set them in ListMultipleSelection object
	@SuppressWarnings("unchecked")
	private List<T> convertToArray(Object[] array){
		List<T> list = new ArrayList<T>();
		
		if(array.length!=0){
			for(int i=0;i<array.length;i++){
				list.add(i, (T)array[i]);
			}
			return (List<T>) list;
		}
		else return null;
	}



	/**
	 * returns a string representation of all the selected items of <code>listMultipleSelection</code>
	 */
	public String getState() {
		List<T> selection = listMultipleSelection.getSelectedValues();
		if ( selection == null )
			return "";
		else
			return selection.toString();
	}
}
