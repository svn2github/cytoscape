package exesto;

import cytoscape.data.CyAttributesImpl;
import cytoscape.data.CyAttributes;
import cytoscape.CyNetwork;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;
import java.util.Iterator;

/**
 * Attribute Tags are a very important method of keeping groups of attributes organized. Why the Cytoscape Core does not have this in right now? No clue.

This Class will take a CyAttributes class and provide Tag support for it.
*/

public abstract class AttributeTags {
  
  static Map dataToTags = new HashMap();
  static Vector listeners = new Vector();

  private static Map getTagMap ( CyAttributes data ) {
    if ( dataToTags.get( data ) == null ) {
      Map map = new HashMap();
      dataToTags.put( data, map );
      return map;
    } else {
      return (Map)dataToTags.get(data);
    }
    
  }
  public static void applyTag ( CyAttributes data, 
                                  String attributeName, 
                                  String tagName ) {
    Map tagMap = getTagMap( data );
    if ( tagMap.get( tagName ) == null ) {
      Set atts = new HashSet();
      tagMap.put( tagName, atts );
    }

    Set atts = ( Set )tagMap.get( tagName );

    atts.add( attributeName );
    notifyListeners();
  }

  public static void removeTag ( CyAttributes data,
                                 String attributeName, 
                                 String tagName ) {
    Map tagMap = getTagMap( data );
    if ( tagMap.get( tagName ) == null ) 
      return;

    Set atts = ( Set )tagMap.get( tagName );

    atts.remove( attributeName );
    notifyListeners();
  }

  public static Set getAttributesByTag ( CyAttributes data,
                                         String tagName ) {
    Map tagMap = getTagMap( data );
    if ( tagMap.get( tagName ) == null ) {
       return null;
     }

     return ( Set )tagMap.get( tagName );
  }

  public static Set getTagNames  (CyAttributes data) {
    Map tagMap = getTagMap( data );
    return tagMap.keySet();
  }

  /**
   * SLOW
   */
  public static Set getTagsOfAttribute ( CyAttributes data,
                                         String attributeName ) {
    Map tagMap = getTagMap( data );
    Set new_set = new HashSet();
    for ( Iterator i = tagMap.keySet().iterator(); i.hasNext(); ) {
      String tag = (String)i.next();
      if ( tagMap.get( tag ) == null ) 
        continue;
      Set atts = ( Set )tagMap.get( tag );
      if ( atts.contains( attributeName ) )
        new_set.add( tag );
    }
    return new_set;
  }

  public static void addTagListener ( TagListener listener ) {
    listeners.add( listener );
  }

  public static void removeTagListener ( TagListener listener ) {
    listeners.remove( listener );
  }

  public static void notifyListeners (){
    for(Iterator listenIt = listeners.iterator();listenIt.hasNext();){
      ( (TagListener)listenIt.next()).tagStateChange();
    }
  }

  
}