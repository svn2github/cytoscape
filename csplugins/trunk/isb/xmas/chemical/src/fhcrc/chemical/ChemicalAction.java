package fhcrc.chemical;

import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;
import java.awt.event.ActionEvent;

public class ChemicalAction extends CytoscapeAction {

  private ChemicalView chemicalview;

  public ChemicalAction () {
    super( "Chemical Genetics" );
    setPreferredMenu( "Chemical" );
  }


  public void actionPerformed ( ActionEvent e ) {
    getChemicalView().setVisible( true );    
  }

  protected ChemicalView getChemicalView () {
    if ( chemicalview == null )
      chemicalview = new ChemicalView();
    return chemicalview;
  }

}
