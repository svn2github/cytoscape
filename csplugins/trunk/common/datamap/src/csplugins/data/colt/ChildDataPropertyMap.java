package csplugins.data.colt;

import csplugins.data.*;

import cern.colt.matrix.*;
import cern.colt.map.*;
import cern.colt.list.*;

import com.sosnoski.util.hashmap.*;

import java.util.*;

public class ChildDataPropertyMap
  extends DataPropertyMap {

  private ParentDataPropertyMap parent;


  protected String identifier;
  private static int childCount = 0;

  /**
   * @param parent the ParentDataPropertyMap tht this Child gets 
   *               its DataStructuresFrom
   * @param uids the set of uids
   * @param attributes the set of attributes
   */
  protected ChildDataPropertyMap ( ParentDataPropertyMap parent,
                                   int[] uids,
                                   int[] attributes ) {
    this.parent = parent;
    
    // create the shared DataStructures from the factory methods
    dataMatrix = createDataMatrix( uids, attributes );
    identifierVector = createIdentifierVector( uids );
    attributeVector = createAttributeVector( attributes );

    // reference everything else
    attributeIntMap = parent.getAttributeIntMap();
    identifierIntMap = parent.getIdentifierIntMap();
    nodeUIDMap = parent.getNodeUIDMap();
    edgeUIDMap = parent.getEdgeUIDMap();
    identifier = "Child: "+childCount;
    childCount++;

  }
 
  public ObjectMatrix2D createDataMatrix ( int[] uids, int[] atts ) {
    return parent.getDataMatrix().viewSelection( uids, atts );
  }

  public ObjectMatrix1D createIdentifierVector ( int[] uids ) {
    return parent.getIdentifierVector().viewSelection( uids );
  }

  public ObjectMatrix1D createAttributeVector ( int[] atts ) {
    return parent.getAttributeVector().viewSelection( atts );
  }
                                                            



}
