package Utils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public class CheckListManager<T> extends MouseAdapter implements ListSelectionListener, ActionListener{
	
    private ListSelectionModel selectionModel = new DefaultListSelectionModel(); 
    private JList list = new JList();
    ArrayList<T> arrayOut=null;
    ArrayList arrayTest=null;
    ListMultipleSelection<T> LMS;
    int hotspot = new JCheckBox().getPreferredSize().width; 
 
    public CheckListManager(JList list,ListMultipleSelection<T> LMS){
    	this.LMS=LMS;
        this.list = list; 
        list.setCellRenderer(new CheckListCellRenderer(list.getCellRenderer(), selectionModel)); 
        list.registerKeyboardAction(this, KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), JComponent.WHEN_FOCUSED); 
        list.addMouseListener(this); 
        selectionModel.addListSelectionListener(this);
    } 
 
    public ListSelectionModel getSelectionModel(){ 
        return selectionModel; 
    } 
 
    private void toggleSelection(int index){ 
        if(index<0) 
            return; 
 
        if(selectionModel.isSelectedIndex(index)){
            selectionModel.removeSelectionInterval(index, index);}
        else {
            selectionModel.addSelectionInterval(index, index);}
    }
 
    public ArrayList getArray(){
    	
        arrayOut = new ArrayList<T>();
        arrayTest = new ArrayList();
    	for(int i=0;i<LMS.getPossibleValues().size();i++){
    		if(selectionModel.isSelectedIndex(i))arrayTest.add(i);
    	}
		if(arrayTest.size()!=0){
			for(int i=0;i<arrayTest.size();i++){
				list.setSelectedIndex((Integer)arrayTest.get(i));
				arrayOut.add(i, (T) list.getSelectedValue());
			}
    	return arrayOut;
		}
		else {
			arrayOut=null;
			return arrayOut;
		}
    }
    
    
    
    public void mouseClicked(MouseEvent me){ 
        int index = list.locationToIndex(me.getPoint()); 
        if(index<0) 
            return; 
        if(me.getX()>list.getCellBounds(index, index).x+hotspot) 
            return; 
        toggleSelection(index); 
    } 

 
    public void valueChanged(ListSelectionEvent e){ 
        list.repaint(list.getCellBounds(e.getFirstIndex(), e.getLastIndex())); 
    } 
 

    public void actionPerformed(ActionEvent e){ 
        toggleSelection(list.getSelectedIndex());
    }
} 