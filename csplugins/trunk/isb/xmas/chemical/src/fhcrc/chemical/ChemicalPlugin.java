package fhcrc.chemical;

import cytoscape.plugin.*;
import cytoscape.*;

import java.util.*;
import javax.swing.*;
import java.awt.event.*;

public class ChemicalPlugin extends CytoscapePlugin {


  static Set drugs;
  static JFrame drugFrame;

  public ChemicalPlugin () {
    ChemicalAction chemical_action = new ChemicalAction();
    Cytoscape.getDesktop().getCyMenus().addCytoscapeAction( chemical_action );
  
    JMenuItem drug = new JMenuItem( new AbstractAction( "Drugs" ) {
        public void actionPerformed ( ActionEvent e ) {
          // Do this in the GUI Event Dispatch thread...
          SwingUtilities.invokeLater( new Runnable() {
              public void run() {
                getDrugFrame().setVisible( true );
              } } ); } } );
    Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu( "Chemical" ).add( drug );



  }  

  public static JFrame getDrugFrame () {
    if ( drugFrame == null ) {
      drugFrame = new JFrame( "Drugs" );
      drugFrame.getContentPane().add( new DrugScorePanel() );
      drugFrame.pack();
    }
    return drugFrame;
  }

  public static void addDrug ( String drug_name ) {
    getDrugs().add( drug_name );
    // update
  }

  public static Set getDrugs () {
    if ( drugs == null )
      drugs = new HashSet();

    return drugs;
  }

  

}
