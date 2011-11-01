package pingo;

/**
 * * Copyright (c) 2010 Flanders Interuniversitary Institute for Biotechnology (VIB)
 * *
 * * Authors : Steven Maere
 * *
 * * This program is free software; you can redistribute it and/or modify
 * * it under the terms of the GNU General Public License as published by
 * * the Free Software Foundation; either version 2 of the License, or
 * * (at your option) any later version.
 * *
 * * This program is distributed in the hope that it will be useful,
 * * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * * The software and documentation provided hereunder is on an "as is" basis,
 * * and the Flanders Interuniversitary Institute for Biotechnology
 * * has no obligations to provide maintenance, support,
 * * updates, enhancements or modifications.  In no event shall the
 * * Flanders Interuniversitary Institute for Biotechnology
 * * be liable to any party for direct, indirect, special,
 * * incidental or consequential damages, including lost profits, arising
 * * out of the use of this software and its documentation, even if
 * * the Flanders Interuniversitary Institute for Biotechnology
 * * has been advised of the possibility of such damage. See the
 * * GNU General Public License for more details.
 * *
 * * You should have received a copy of the GNU General Public License
 * * along with this program; if not, write to the Free Software
 * * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * *
 * * Authors: Steven Maere
 * * Date: Jul.27.2010
 * * Description: PiNGO is a Cytoscape plugin that leverages functional enrichment
 * * analysis to discover lead genes from biological networks.          
 **/


import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Properties;


public class SaveSettingsButtonActionListener implements ActionListener {


    /*--------------------------------------------------------------
    FIELD.
    --------------------------------------------------------------*/
    private SettingsPanel settingsPanel;

    /*--------------------------------------------------------------
       CONSTRUCTOR.
    --------------------------------------------------------------*/

    public SaveSettingsButtonActionListener(SettingsPanel settingsPanel) {
        this.settingsPanel = settingsPanel;

    }

    /*--------------------------------------------------------------
    LISTENER-PART.
    --------------------------------------------------------------*/

    public void actionPerformed(ActionEvent e) {
        try {
            //adapt settings in properties file
            Properties pingoProps = settingsPanel.getPingoProps();
            if(settingsPanel.getStartGoCatInputField().getText() != null){
                pingoProps.setProperty("start_go_def", (String) settingsPanel.getStartGoCatInputField().getText());
            }
            else{
                pingoProps.setProperty("start_go_def","---");
            }
            if(settingsPanel.getFilterGoCatInputField().getText() != null){
                pingoProps.setProperty("filter_go_def", (String) settingsPanel.getFilterGoCatInputField().getText());
            }
            else{
                pingoProps.setProperty("filter_go_def","---");
            }
            if(settingsPanel.getTargetGoCatInputField().getText() != null){
                pingoProps.setProperty("target_go_def", (String) settingsPanel.getTargetGoCatInputField().getText());
            }
            else{
                pingoProps.setProperty("target_go_def","---");
            }
            pingoProps.setProperty("tests_def", (String) settingsPanel.getTestBox().getSelectedItem());
            pingoProps.setProperty("correction_def", (String) settingsPanel.getCorrectionBox().getSelectedItem());       
            pingoProps.setProperty("signif_def", settingsPanel.getAlphaField().getText());
            pingoProps.setProperty("refset_def", (String) settingsPanel.getClusterVsPanel().getSelection());
            pingoProps.setProperty("species_def", (String) settingsPanel.getAnnotationPanel().getSelection());
            pingoProps.setProperty("ontology_file_def", (String) settingsPanel.getOntologyPanel().getSelection());
            pingoProps.setProperty("namespace_def", (String) settingsPanel.getNamespacePanel().getSelection());
            pingoProps.setProperty("graph_def", (String) settingsPanel.getGraphPanel().getSelection());
            pingoProps.setProperty("genedescription_def", (String) settingsPanel.getGeneDescriptionPanel().getSelection());
            pingoProps.setProperty("file_output", settingsPanel.getDataPanel().getEnabled().toString());
            if(settingsPanel.getDataPanel().getFileDir() != null){
                pingoProps.setProperty("outputdir_def", (String) settingsPanel.getDataPanel().getFileDir());
            }
            else{
                pingoProps.setProperty("outputdir_def","");
            }
            if(settingsPanel.getVizPanel().getVizMode().equals(VizPanel.VIZSTRING)){
                pingoProps.setProperty("visual_def", VizPanel.VIZSTRING);
            }
            else{
                pingoProps.setProperty("visual_def", VizPanel.NOVIZSTRING);
            }
            if(settingsPanel.getVizPanel().getTabMode().equals(VizPanel.TABSTRING)){
                pingoProps.setProperty("tab_def", VizPanel.TABSTRING);
            }
            else{
                pingoProps.setProperty("tab_def", VizPanel.NOTABSTRING);
            }
            if(settingsPanel.getVizPanel().getStarMode().equals(VizPanel.STARSTRING)){
                pingoProps.setProperty("star_def", VizPanel.STARSTRING);
            }
            else{
                pingoProps.setProperty("star_def", VizPanel.NOSTARSTRING);
            }
            
            if (settingsPanel.getAnnotationPanel().getDefault()==true){
                pingoProps.setProperty("annotation_default", "true");
            }
            else{
                pingoProps.setProperty("annotation_default", "false");
            }
            if(settingsPanel.getOntologyPanel().getDefault()==true){
                pingoProps.setProperty("ontology_default", "true");
            }
            else{
                pingoProps.setProperty("ontology_default", "false");
            }
            settingsPanel.getParams().storeParameterSettings();
        }
        catch (IOException ee) {
            JOptionPane.showMessageDialog(settingsPanel, "Could not save settings :" + ee);
        }
    }
}
