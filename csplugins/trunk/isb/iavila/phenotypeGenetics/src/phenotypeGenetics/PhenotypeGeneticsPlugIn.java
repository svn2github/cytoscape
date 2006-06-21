/**
 * PlugIn to determine and analyze biological networks from the observation of phenotypes 
 * under defined environmental and/or genetic perturbations.
 *
 * @author Vesteinn Thorsson
 * @author Greg Carter
 * @author Alex Rives
 * @author Paul Shannon
 * @author Iliana Avila-Campillo
 * @version 2.0
 */
package phenotypeGenetics;
import phenotypeGenetics.view.*;
import phenotypeGenetics.ui.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import junit.framework.*;
import java.util.*;

import cytoscape.*;
import cytoscape.plugin.*;
import cytoscape.data.servers.*;
import cytoscape.util.*;
import cytoscape.data.Semantics;
import cytoscape.view.*;
import cytoscape.visual.*;
import cytoscape.visual.calculators.*;
import cytoscape.visual.mappings.*;
import cytoscape.visual.ui.*;

public class PhenotypeGeneticsPlugIn extends CytoscapePlugin{
  
  /**
   * The dialog to set plug-in parameters like xml files, modes, visualization
   * parameters, etc. There is one dialog only for the whole session.
   */
  protected static PGDialog PG_DIALOG;
  
  /**
   * The paths of the input project XML files 
   */
  public static String [] xmlProjectFiles;

  /**
   * The path of the input Mode XML file
   */
  public static String modeXmlFile;

  
  /**
   * Sole constructor
   */
  public PhenotypeGeneticsPlugIn (){

    PhenotypeGeneticsPlugIn.xmlProjectFiles = readProjectFileArgs();
    PhenotypeGeneticsPlugIn.modeXmlFile = readModeXmlArg();
    
    if(PhenotypeGeneticsPlugIn.PG_DIALOG == null){
      PhenotypeGeneticsPlugIn.PG_DIALOG = new PGDialog();
    }
    
    PhenotypeGeneticsPlugIn.PG_DIALOG.setResizable(false);
    
    Cytoscape.getDesktop().getCyMenus().getOperationsMenu().add(
      new AbstractAction ("Phenotype Genetics..."){
        public void actionPerformed (ActionEvent action){
          PhenotypeGeneticsPlugIn.PG_DIALOG.pack();
          PhenotypeGeneticsPlugIn.PG_DIALOG.setLocationRelativeTo(Cytoscape.getDesktop());
          PhenotypeGeneticsPlugIn.PG_DIALOG.setVisible(true);
        }//actionPerformed
      });
    
    JMenuItem aboutItem = new JMenuItem("About Phenotype Genetics...");
    aboutItem.addActionListener(new AboutListener());
    Cytoscape.getDesktop().getCyMenus().getOperationsMenu().add(aboutItem);
    // set up whatever options we want for Cytoscape (like visual style, etc).
    setUpCyOptions();
  }//PhenotypeGeneticsPlugIn
  
   /**
    * @return a String describing the plugin.
    */
  public String toString (){
    return "PlugIn to determine and analyze biological networks from the observation of phenotypes under defined environmental and/or genetic perturbations.";
  }//toString

  /**
   * Sets up Cytoscape options that are best for this plug-in, like a visual-style
   */
  public static void setUpCyOptions (){
    VisualStyle vs = PGVisualStyle.createVisualStyle();
    VisualMappingManager vmManager = Cytoscape.getVisualMappingManager();
    vmManager.setVisualStyle(vs);
  }//setUpCyOptions

  /**
   * When actionPerformed is called, it pops-up an information dialog.
   */
  protected class AboutListener extends AbstractAction {
    
    public AboutListener (){}
    
    public void actionPerformed (ActionEvent event){
      final String nl = System.getProperty("line.separator");
      final String info =
        "Version: 2.0"+ nl +
        "Authors (alphabetical order):"+nl+
        "  Iliana Avila-Campillo" + nl +
        "  Greg Carter" + nl +
        "  Alex Rives" + nl +
        "  Paul Shannon"+ nl +
        "  Vesteinn Thorsson"+ nl +
        "Organization: Institute for Systems Biology" + nl +
        "Contact: thorsson@systemsbiology.org"+nl+
        "License: GNU Lesser General Public License";
      JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
                                    info,
                                    "About Phenotype Genetics",
                                    JOptionPane.PLAIN_MESSAGE);
    }//actionPerformed
    
  }//AboutListener

  /**
   * @return an array of String with file paths for XML input files<br>
   * To input arguments to the plugIn, enter this line into cytoscape.props:<br>
   * PGproject = file1.xml:file2.xml:file3.xml<br>
   * In the command line:<br>
   * PGproject file1.xml:file2.xml:file3.xml
   * 
   */
  protected static String[] readProjectFileArgs () {
    // C2.0 and C2.x have different ways of getting command line arguments:
    // C2.0:
    //String [] args = Cytoscape.getCytoscapeObj().getConfiguration().getArgs();
    // C2.x:
    //String [] args = CytoscapeInit.getArgs();
    // C2.3:
	Properties props = CytoscapeInit.getProperties();
	if(props.containsKey("PGproject")){
		String value = props.getProperty("PGproject");
		String [] files = value.split(":");
		return files;
	}
	return new String[0];
	
	//	  ArrayList strs = new ArrayList();
	//    for(int i = 0; i < args.length; i++){
	//      if(args[i].equals ("--PGproject")){
	//        if(i+1 <= args.length){
	//          strs.add(args[i+1]);
	//        }
	//      }// if we find --PGproject
	//    }// for i
	//    return (String[])strs.toArray(new String[strs.size()]);
  }//readProjectFileArgs
  
  /**
   * @return the name of the XML modes file loaded from the command line, or null
   * if there wasn't any<br>
   * To input arguments to the plugIn, enter this line into cytoscape.props:<br>
   * PGmodes = file.xml<br>
   * or in the command line:<br>
   * PGmodes file.xml
   */
  public String readModeXmlArg (){
    Properties props = CytoscapeInit.getProperties();
    if(props.containsKey("PGmodes")){
    		String file = props.getProperty("PGmodes");
    		return file;
    }
	
    //OLD:
	//String [] args = cytoscapeWindow.getConfiguration().getArgs();
    // C2.0 and C2.x have different ways of getting command line arguments:
    // C2.0:
    //String [] args = Cytoscape.getCytoscapeObj().getConfiguration().getArgs();
    // C2.x:
    //String [] args = CytoscapeInit.getArgs();
    //for(int i = 0; i < args.length; i++){
    //  if(args[i].equals("--PGmodes")){
    //    if(i+1 <= args.length){
    //      return args[i+1];
    //    }
    //  }
    //}//for i
    return null;
  }//readModeXmlArg

  /**
   * @return the dialog that represents this plugin
   */
  public static PGDialog getDialog (){
    return PG_DIALOG;
  }//getDialog

  /**
   * Adds the given XML file full-path to the static list of XML 
   * files available in this plug-in.
   */
  public static void addXmlFile (String full_path){
    if(PhenotypeGeneticsPlugIn.xmlProjectFiles == null){
      PhenotypeGeneticsPlugIn.xmlProjectFiles = new String[1];
      PhenotypeGeneticsPlugIn.xmlProjectFiles[0] = full_path;
      return;
    }
    String [] tempArray = new String[PhenotypeGeneticsPlugIn.xmlProjectFiles.length + 1];
    System.arraycopy(PhenotypeGeneticsPlugIn.xmlProjectFiles, 
                     0, 
                     tempArray, 
                     0, 
                     PhenotypeGeneticsPlugIn.xmlProjectFiles.length);
    tempArray[tempArray.length - 1] = full_path;
    PhenotypeGeneticsPlugIn.xmlProjectFiles = tempArray;
  }//addXmlFile

}//class PhenotypeGeneticsPlugIn
