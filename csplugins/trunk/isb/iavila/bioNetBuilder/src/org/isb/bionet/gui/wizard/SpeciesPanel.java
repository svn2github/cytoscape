
/**
 * SpeciesPanle.java
 */
package org.isb.bionet.gui.wizard;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;
import java.awt.*;

public class SpeciesPanel extends JPanel {
    
    protected JList sList;
    protected Map sourceToSpecies;
    protected Map sourceToName;
    
    /**
     * 
     * @param source_to_species a Map from a data source fully specified class to its supported species
     * @param source_to_name a Map from a data source fully specified class to its human readable name
     */
    public SpeciesPanel (Map source_to_species, Map source_to_name){
        this.sourceToSpecies = source_to_species;
        this.sourceToName = source_to_name;
        create();
    }//SpeciesPanel
    
    /**
     * @return a Map from data source's fully described class to Lists of the species that
     * are selected for those sources
     */
    public Map getSourcesSelectedSpecies (){
        Hashtable table = new Hashtable();
        Object [] selected = (Object[])sList.getSelectedValues();
        for(int i = 0; i < selected.length; i++){
            ListItem item = (ListItem)selected[i];
            ArrayList list = (ArrayList)table.get(item.dataSource);
            if(list == null){
                list = new ArrayList();
                table.put(item.dataSource,list);
            }
            list.add(item.species);
        }//for i
        return table;
    }
    
    /**
     * @return a Map from a data source fully specified class to its human readable name
     */
    public Map getSourcesNames (){
        return this.sourceToName;
    }
    
    /**
     * 
     */
    protected void create (){
        
        
        Iterator it = this.sourceToSpecies.keySet().iterator();
        Vector listItems = new Vector();
        while(it.hasNext()){
            String sourceClass = (String)it.next();
            String sourceName = (String)this.sourceToName.get(sourceClass);
            Vector sp = (Vector)this.sourceToSpecies.get(sourceClass);
            Iterator it2 = sp.iterator();
            while(it2.hasNext()){
                String species = (String)it2.next();
                ListItem item = new ListItem( species + " (" + sourceName + ")",
                                              species, sourceClass);
                listItems.add(item);
                
            }//while it2
        }// while it
        
        
        sList = new JList(listItems.toArray());
        sList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        sList.setLayoutOrientation(JList.VERTICAL);
        sList.setVisibleRowCount(-1);
       
        JScrollPane listScroller = new JScrollPane(sList);
        listScroller.setPreferredSize(new Dimension(250, 80));
        
        JLabel sLabel = new JLabel("Available Species:");
        JPanel labelPanel = new JPanel();
        labelPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        labelPanel.add(sLabel);
        
        setLayout(new BorderLayout());
        
        add(labelPanel, BorderLayout.NORTH);
        add(listScroller, BorderLayout.CENTER);
        
    }//create
    
    // Internal class
    
    protected class ListItem {
        public String text, species, dataSource;
        
        public ListItem (String text, String species, String data_source){
            this.text = text;
            this.species = species;
            this.dataSource = data_source;
        }
        
        public String toString(){
            return this.text;
        }
    }//ListItem
    
    
}//SpeciesPanel