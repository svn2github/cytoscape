package Factory;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.*;


import java.awt.*;

import GuiInterception.AbstractGuiHandler;
import Tunable.*;
import Utils.*;

public class ListMultipleHandler<T> extends AbstractGuiHandler{

	ListMultipleSelection<T> lms;
	JList jlist;
	private List<T> selected;
	CheckListManager<T> checkListManager;
	Map<Integer,T> map;

	
	@SuppressWarnings("unchecked")
	public ListMultipleHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
		try {
            lms = (ListMultipleSelection<T>) f.get(o);
        } catch(Exception e) {e.printStackTrace();}
	
        panel = new JPanel(new BorderLayout());
        JTextArea jta = new JTextArea(t.description());
        jta.setLineWrap(true);
        jta.setWrapStyleWord(true);
        panel.add(jta,BorderLayout.BEFORE_LINE_BEGINS);
        jta.setBackground(null);
        jta.setEditable(false);
        jlist = new JList(lms.getPossibleValues().toArray());
        jlist.setFont(new Font("sansserif",Font.PLAIN,11));
        jlist.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jlist.addListSelectionListener(this);
        //checkListManager = new CheckListManager<T>(jlist,lms);
        JScrollPane scrollpane = new JScrollPane(jlist);
        panel.add(scrollpane,BorderLayout.EAST);
	}

	

	public void handle(){

//		map = checkListManager.getMap();
//		for(int i=0;i<lms.getPossibleValues().size();i++){
//			if(map.containsKey(i)){
//			selected.add((T)map.get(i));}
//		}
		
//		selected = checkListManager.getArray2();
//		selected = new ArrayList<T>();
//		System.out.println(selected);		
//		selected.add(checkListManager.getValue());
//		jlist.setSelectionModel(checkListManager.getSelectionModel());
		//selected.set(i, (T)jlist.getSelectedValue());// = jlist.get

		selected = convertToArray(jlist.getSelectedValues());
		if (selected!=null) {
			lms.setSelectedValues(selected);
            try{
                f.set(o,lms);
            }catch(Exception e){e.printStackTrace();}
        }
	}
	
	
	@SuppressWarnings("unchecked")
	public List<T> convertToArray(Object[] in){
		List<T> list = new ArrayList<T>();
		if(in.length!=0){
			for(int i=0;i<in.length;i++){
				list.add(i, (T)in[i]);
			}
			return (List<T>) list;
		}
		else return null;
	}


	public String getState() {
		java.util.List<T> sel = lms.getSelectedValues();
		if ( sel == null )
			return "";
		else
			return sel.toString();
	}

	@Override
	public void returnPanel() {
		panel.removeAll();
		panel.add(new JLabel(t.description()));
		panel.add(new JScrollPane(new JList(lms.getSelectedValues().toArray())));
		
	}
}
