/*
 * ZChooseBioDataServerAnnotation.java
 *
 * Created on May 17, 2006, 6:59 PM by Olivier Garcia.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * The software and documentation provided hereunder is on an "as is" basis,
 * and the Pasteur Institut
 * has no obligations to provide maintenance, support,
 * updates, enhancements or modifications.  In no event shall the
 * Pasteur Institut
 * be liable to any party for direct, indirect, special,
 * incidental or consequential damages, including lost profits, arising
 * out of the use of this software and its documentation, even if
 * the Pasteur Institut
 * has been advised of the possibility of such damage. See the
 * GNU General Public License for more details: 
 *                http://www.gnu.org/licenses/gpl.txt.
 *
 * Authors: Olivier Garcia

 */

package BiNGO;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import cytoscape.data.annotation.*;
/**
 *
 * @author ogarcia
 */
public class ZChooseBioDataServerAnnotation extends JPanel {
    /**ComboBox to choose the Annotation data structure via its Annotation description
     * , if Annotations have been loaded in BioDataServer and if user activated 
     * useInMemoryAnnot. */
    JComboBox inMemoryChoice;
    /**user choise : use annotation datas directly loaded via cytoscape or not*/
    JCheckBox useInMemoryAnnot;
    
    boolean bioDataServerInMemory;
    cytoscape.data.servers.BioDataServer bds;
    /** 
     * selectedItemToAnnotationDescription : to retrieve the Annotation Data structure choosen by user
     * from the String selected in inMemoryChoice
     */
    HashMap selectedItemToAnnotationDescription;
    SettingsPanel settingsPanel ;
    
    
    /** Creates a new instance of ZChooseBioDataServerAnnotation */
    public ZChooseBioDataServerAnnotation(SettingsPanel stPanel) {
                
        
                selectedItemToAnnotationDescription = new HashMap();
                GridBagLayout gridbag = new GridBagLayout() ;	
                GridBagConstraints c = new GridBagConstraints();
                inMemoryChoice = new JComboBox();
                inMemoryChoice.setEnabled(false);
                useInMemoryAnnot = new JCheckBox();
                useInMemoryAnnot.setSelected(false);
                bioDataServerInMemory =false;
                bds = cytoscape.Cytoscape.getBioDataServer();
                settingsPanel = stPanel;
                
                if (bds !=null){
                    cytoscape.data.annotation.AnnotationDescription[] ad =bds.getAnnotationDescriptions();

                    //System.out.println(ad[0].getCurator());

                    for (int i=0;i<ad.length;i++){
                        //if (ad[i].getCurator().matches("*GO*") ){    //.equals("GO")){
                        
                            bioDataServerInMemory =true;
                            String item = bds.getAnnotation(ad[i]).getCurator()+" "+bds.getAnnotation(ad[i]).getType()+" "+bds.getAnnotation(ad[i]).getSpecies();
                            inMemoryChoice.addItem(item);
                            selectedItemToAnnotationDescription.put(item,ad[i]);
                        //}
                    }
                }

                if (bioDataServerInMemory){
                    useInMemoryAnnot.setSelected(false);
                    c.gridx=1;
                    //c.gridy=1;
                    c.weightx = 0 ;
                    c.weighty = 1 ;
                    c.fill = GridBagConstraints.HORIZONTAL;
                    gridbag.setConstraints(useInMemoryAnnot, c);
                    add(useInMemoryAnnot);
                    
                    
                    useInMemoryAnnot.addActionListener(new java.awt.event.ActionListener(){
                                                public void actionPerformed(java.awt.event.ActionEvent e){
                                                    if (useInMemoryAnnot.isSelected()){
                                                        inMemoryChoice.setEnabled(true);
                                                        settingsPanel.getAnnotationPanel().choiceBox.setEnabled(false) ;
      //                                                  settingsPanel.getAnnotationPanel().getTypeOfIdentifierPanel().disableButtons() ;
                                                        settingsPanel.getOntologyPanel().choiceBox.setEnabled(false) ;
                                                    }
                                                    else {
                                                        inMemoryChoice.setEnabled(false);
                                                        settingsPanel.getAnnotationPanel().choiceBox.setEnabled(true) ;
     //                                                   settingsPanel.getAnnotationPanel().getTypeOfIdentifierPanel().enableButtons() ;
                                                        settingsPanel.getOntologyPanel().choiceBox.setEnabled(true) ;
                                                    }
                                                }
                                                });
                    c.gridx=2;
                    c.weightx = 1 ;
                    c.weighty = 1 ;
                     c.fill = GridBagConstraints.HORIZONTAL;
                    gridbag.setConstraints(inMemoryChoice, c);
                    add(inMemoryChoice);
                }
               
                
                        
    }
    /** answer the question
     */
    public boolean isThereAnnotInMemorie(){
        return bioDataServerInMemory;
    }
    /** answer the question
     */
    public boolean isMemoryChoiceEnabled(){
        if (useInMemoryAnnot!=null)
            return useInMemoryAnnot.isSelected();
        else return false;
    }
    /** get the annotation chosen by user
     */
    public cytoscape.data.annotation.Annotation getAnnotation (){
        return bds.getAnnotation((AnnotationDescription)selectedItemToAnnotationDescription.get(inMemoryChoice.getSelectedItem()));
    }
    /** get the ontology chosen by the user
     */
    public cytoscape.data.annotation.Ontology getOntology (){
        return getAnnotation().getOntology();
    }
    
}
