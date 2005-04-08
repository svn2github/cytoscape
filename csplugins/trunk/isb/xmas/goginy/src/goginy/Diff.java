package goginy;

import giny.model.*;
import java.util.*;
import cern.colt.list.*;
import cern.colt.map.*;

public abstract class Diff {

  /**
   * @return diff between ints
   */
  public static IntArrayList diff ( IntArrayList node_list_1, 
                                    IntArrayList node_list_2,
                                    IntArrayList in1_not2,
                                    IntArrayList in2_not1 ) {
   
    OpenIntIntHashMap map = new OpenIntIntHashMap();
    for ( int i = 0; i < node_list_1.size(); ++i ) 
      map.put( node_list_1.get(i), 1 );
    
    for ( int i = 0; i < node_list_2.size(); ++i ) 
      map.put( node_list_2.get(i), 1 );
 
    IntArrayList both = new IntArrayList();
    map.keys( both );

    IntArrayList diff = new IntArrayList();

    node_list_1.sort();
    node_list_2.sort();

    for ( int i = 0; i < both.size(); ++i ) {
      if ( node_list_1.binarySearch( both.get(i) ) >= 0 &&
           node_list_2.binarySearch( both.get(i) ) >= 0 ) {
        // thy both conain the node
      } else {
        diff.add( both.get(i) );
        if ( node_list_1.binarySearch( both.get(i) ) >= 0 &&
             node_list_2.binarySearch( both.get(i) ) < 0 ) {
          in1_not2.add( both.get(i) );
        } else if ( node_list_1.binarySearch( both.get(i) ) >= 0  &&
                    node_list_2.binarySearch( both.get(i) ) < 0 ) {
          in2_not1.add( both.get(i) );
        }
      }
    }
    return diff;
  }
  
}
