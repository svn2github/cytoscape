package filter.cytoscape;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import filter.model.*;
import javax.swing.border.*;
import java.beans.*;
import javax.swing.event.SwingPropertyChangeSupport;

import cytoscape.*;
import cytoscape.data.*;

import giny.model.*;

import ViolinStrings.Strings;

/**
 * This filter will do any sort of search on any attribute for all
 * objects.  It will support String wild card searches, numerical
 * inequalities, and numerical comparisons.
 */
public class CsAttributeValueFilter 
  implements Filter {

  //----------------------------------------//
  // Filter specific properties 
  //----------------------------------------//
  
  // we can search over one or more attributes
  protected String[] selectedAttributes;

  protected String firstSearch;
  protected Double secondSearch;

  protected String firstCompare;
  protected String secondCompare;

  protected boolean between;


  public static String EQ = "==";
  public static String NE = "!=";
  public static String GT = ">";
  public static String GE = ">=";
  public static String LT = "<";
  public static String LE = "<=";

  public static String BETWEEN_EVENT = "BETWEEN_EVENT";
  public static String FIRST_SEARCH_EVENT = "FIRST_SEARCH_EVENT";
  public static String SECOND_SEARCH_EVENT = "SECOND_SEARCH_EVENT";
  public static String FIRST_COMPARE_EVENT = "FIRST_COMPARE_EVENT";
  public static String SECOND_COMPARE_EVENT = "SECOND_COMPARE_EVENT";
  public static String SELECTED_ATTRIBUTES_EVENT = "SELECTED_ATTRIBUTES_EVENT";
  public static String FILTER_NAME_EVENT = "FILTER_NAME_EVENT";

  public static String FILTER_ID = "CsAttributeValueFilter";

  //----------------------------------------//
  // Cytoscape specific Variables
  //----------------------------------------//
  protected CyNetwork network;
  protected GraphObjAttributes nodeAttributes;
  protected GraphObjAttributes edgeAttributes;

  //----------------------------------------//
  // Needed Variables
  //----------------------------------------//
  protected String identifier = "default";
  protected SwingPropertyChangeSupport pcs = new SwingPropertyChangeSupport(this);

  //---------------------------------------//
  // Constructor
  //----------------------------------------//
  
   /**
   * Creates a new CsAttributeValueFilter
   */  
  public CsAttributeValueFilter ( CyNetwork network,  
                                  String identifier,
                                  String[] selectedAttributes,
                                  String firstSearch,
                                  Double secondSearch,
                                  String firstCompare,
                                  String secondCompare,
                                  boolean between ) {
    this.network = network;
    this.nodeAttributes = network.getNodeAttributes();
    this.edgeAttributes = network.getEdgeAttributes();
    this.identifier = identifier;
    this.selectedAttributes = selectedAttributes;  
    this.firstSearch = firstSearch;
    this.secondSearch = secondSearch;
    this.firstCompare = firstCompare;
    this.secondCompare = secondCompare;
    this.between = between;
  }

  //----------------------------------------//
  // Implements Filter
  //----------------------------------------//

  /**
   * Returns the name for this Filter
   */
  public String toString () {
    return identifier;
  }

  /**
   * sets a new name for this filter
   */
  public void setIdentifier ( String new_id ) {
    FilterManager.defaultManager().renameFilter( identifier, new_id );
    this.identifier = new_id;
  }

  /**
   * This is usually the same as the class name
   */
  public String getFilterID () {
    return FILTER_ID;
  }

  /**
   * Any object that has an entry in a GraphObjAttributes will be able
   * to pass this filter. This is essentially an "or" filter....
   */
  public boolean passesFilter ( Object object ) {
    
    // run the filter for each Attribute
    for ( int i = 0; i < selectedAttributes.length; ++i ) {
      if ( passesSpecificAttribute ( object, selectedAttributes[i] ) ) {
        return true;
      }
    }
    return false;
  }

  /**
   * We can really only check one attribute at a time, so we do that 
   * here.
   */
  protected boolean passesSpecificAttribute ( Object object, String attribute ) {

    Object value;

    // right now, we only work on nodes and edges....

   
    if ( object instanceof Node ) {
      value = nodeAttributes.getValue( attribute, (( Node )object).getIdentifier() );
    } else if ( object instanceof Edge ) {
      value = edgeAttributes.getValue( attribute, (( Edge )object).getIdentifier() );
    } else { 
      return false;
    }

    
    // first try to cast the search to a Number, if that works, swell
    // otherwise we will do string matching
    
    double first_val = 0;
    try {
      Double n = new Double( firstSearch );
      first_val = n.doubleValue();
    } catch ( Exception e ) {
      
      
      if ( value instanceof String ) {
      String[] pattern = firstSearch.split("\\s");
      for ( int p = 0; p < pattern.length; ++p ) {
        
        if ( Strings.isLike( ( String )value, pattern[p], 0, true ) ) {
          if ( firstCompare.equals( EQ ) ) {
            return true;
          } 
        } else {
          if ( firstCompare.equals( NE ) ) {
            return true;
          }
        }
      }
      return false;
    }


    }

    // all number will be cast to doubles
    if ( value instanceof Number ) {
      
      double val = ( ( Double )value).doubleValue();

      // now we do the appropriate comparison
      

      // First do the non-between stuff
      if ( !between ) {  
        if ( firstCompare.equals( EQ ) && val == first_val ) {
          return true;
        } else if ( firstCompare.equals( NE ) && val != first_val ) {
          return true;
        } else if ( firstCompare.equals( GT ) && val > first_val ) {
          return true;
        } else if ( firstCompare.equals( GE ) && val >= first_val ) {
          return true;
        } else if ( firstCompare.equals( LT ) && val < first_val ) {
          return true;
        } else if ( firstCompare.equals( LE ) && val <= first_val ) {
          return true;
        } else {
          return false;
        }
      }

      //now do the between stuff
      double second_val = 0;
      try {
        second_val = secondSearch.doubleValue();
      } catch ( Exception e ) {
        return false;
      }

      // we now have two numbers, first_val and second_val
      // these are the ones we will do the comparison between

    
      if ( firstCompare.equals( LT ) && secondCompare.equals( LT ) 
           && first_val < val && val < second_val ) {
        return true;
      } else if ( firstCompare.equals( LE ) && secondCompare.equals( LT ) 
           && first_val <= val && val < second_val ) {
        return true;
      } else if ( firstCompare.equals( LE ) && secondCompare.equals( LE ) 
           && first_val <= val && val <= second_val ) {
        return true;
      } else if ( firstCompare.equals( LT ) && secondCompare.equals( LE ) 
           && first_val < val && val <= second_val ) {
        return true;
      } else {
        return false;
      }

    }
    return false;
  }

  public Class[] getPassingTypes () {
    //    return new Class[] { Class.forName( "giny.model.Node" ), Class.forName( "giny.model.Edge" ) };  
    return null;
  }
  
  public boolean equals ( Object other_object ) {
    // currently unimplemented due to laziness and time
    return false;
  }
  
  public Object clone () {
    return new CsAttributeValueFilter(  network,  
                                        identifier,
                                        selectedAttributes,
                                        firstSearch,
                                        secondSearch,
                                        firstCompare,
                                        secondCompare,
                                        between );
  }
  
  public SwingPropertyChangeSupport getSwingPropertyChangeSupport() {
   return pcs;
  }
  
  //----------------------------------------//
  // CsAttributeValueFilter
  //----------------------------------------//

  public void propertyChange ( PropertyChangeEvent e ) {
    
    if ( e.getPropertyName() == BETWEEN_EVENT ) {
      setBetween( ( Boolean )e.getNewValue() );
    } else if ( e.getPropertyName() == FIRST_SEARCH_EVENT ) {
      setFirstSearch( ( String )e.getNewValue() );
    } else if ( e.getPropertyName() == SECOND_SEARCH_EVENT ) {
      setSecondSearch( ( Double )e.getNewValue() );
    } else if ( e.getPropertyName() == FIRST_COMPARE_EVENT ) {
      setFirstCompare( ( String )e.getNewValue() );
    } else if ( e.getPropertyName() == SECOND_COMPARE_EVENT ) {
      setSecondCompare( ( String )e.getNewValue() );
    } else if ( e.getPropertyName() == SELECTED_ATTRIBUTES_EVENT ) {
      setSelectedAttributes( ( String[] )e.getNewValue() );
    } else if ( e.getPropertyName() == FILTER_NAME_EVENT ) {
      setIdentifier( ( String )e.getNewValue() );
    }

  }

  // Between //////////////////////////////
  
  public boolean getBetween () {
    return between;
  }

  public void setBetween ( Boolean bool ) {
    this.between = bool.booleanValue();
    fireBetweenChanged();
  }

  public void setBetween ( boolean bool ) {
    this.between = bool;
    fireBetweenChanged();
  }

  public void fireBetweenChanged () {
    pcs.firePropertyChange( BETWEEN_EVENT, null, new Boolean( between ) );
  }

  // FirstSearch //////////////////////////

  public String getFirstSearch () {
    return firstSearch;
  }

  public void setFirstSearch ( String s ) {
    this.firstSearch = s;
    fireFirstSearchChanged();
  }

  public void fireFirstSearchChanged () {
    pcs.firePropertyChange( FIRST_SEARCH_EVENT, null, firstSearch );
  }

  // SecondSearch //////////////////////////

  public Double getSecondSearch () {
    return secondSearch;
  }

  public void setSecondSearch ( Double s ) {
    this.secondSearch = s;
    fireSecondSearchChanged();
  }

  public void fireSecondSearchChanged () {
    pcs.firePropertyChange( SECOND_SEARCH_EVENT, null, secondSearch );
  }

  // FirstCompare //////////////////////////

  public String getFirstCompare () {
    return firstCompare;
  }

  public void setFirstCompare ( String s ) {
    this.firstCompare = s;
    fireFirstCompareChanged();
  }

  public void fireFirstCompareChanged () {
    pcs.firePropertyChange( FIRST_COMPARE_EVENT, null, firstCompare );
  }

  // SecondCompare //////////////////////////

  public String getSecondCompare () {
    return secondCompare;
  }

  public void setSecondCompare ( String s ) {
    this.secondCompare = s;
    fireSecondCompareChanged();
  }

  public void fireSecondCompareChanged () {
    pcs.firePropertyChange( SECOND_COMPARE_EVENT, null, secondCompare );
  }

   // Selected_Attributes ///////////////////////////

  public String[] getSelectedAttributes () {
    return selectedAttributes;
  }

  public void setSelectedAttributes ( String[] new_attr ) {
    this.selectedAttributes = new_attr;
    fireSelectedAttributesModified();
  }

  public void fireSelectedAttributesModified () {
    pcs.firePropertyChange( SELECTED_ATTRIBUTES_EVENT, null, selectedAttributes );
  } 
  

  //----------------------------------------//
  // IO
  //----------------------------------------//

  public String output () {
    return null;
  }
  
  public Filter input ( String desc ) {
    return null;
  }
}
