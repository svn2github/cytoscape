package rowan.browser;

import cytoscape.*;
import cytoscape.plugin.*;
import cytoscape.util.*;
import cern.colt.list.*;
import cern.colt.map.*;
 

import java.util.*;

import javax.swing.*;
import javax.swing.undo.*;

public class DataEditAction extends AbstractUndoableEdit {


  final ArrayList changes;

  public DataEditAction () {
    changes = new ArrayList();
  }


  public void addChange ( String object, String attribute, Object old_value, Object new_value ) {
    changes.add( new Object[] { object, attribute, old_value, new_value } );
  }

  public String	getPresentationName () {
    return ""+changes.size()+" Data Modifications";
  }
          
  public String getRedoPresentationName () {
    return "Redo Data Modifications";
  }
        
  public String getUndoPresentationName () {
    return "Undo Data Modifications";
  }
        
  public void	redo () {
    // this puts the new values back in
    for ( Iterator i = changes.iterator(); i.hasNext(); ) {

    }
            
  }
        
  public void undo () {
    // this puts the old values back in
    for ( Iterator i = changes.iterator(); i.hasNext(); ) {

    }
  }





}
