package datamap;

import cern.colt.map.*;

/**
 * The MetaData class holds all of the MetaData for 
 * a DataPropertyMap.  It tries to hold the minumum 
 * number of heavyweight objects, and instead holds
 * references to those uniqueIDs for whom there is 
 * multi-dimensional data.
 *
 * There exists one MetaData Object for every Top-Level 
 * data property map.
 *
 * Due to the somewhat unique nature of MetaData, mainly 
 * the fact that there should only be a limited number of
 * attributes, and a limited number of values for each 
 * attribute.  It is important to note that only a limited
 * number of elements should be put into the meta data. If
 * each object requires a unique attribute, then that is
 * a an attribute and should be stored as such in 
 * DataPropertyMap.
 *
 * This class will be optimized for data look-up more so 
 * than data-loading.  And it is specifically designed to
 * facilitate a fast lookup using the ints that are stored 
 * with each Object in the DataPropertyMap.
 *
 */
public class MetaData { 

  /**
   * The look ups for attributes will be primarily done
   * by using the int that is stored with the data.
   *
   * All of the Objects stored are arrays, the first
   * element is always the name of the Attribute,
   * the subsequent elements are the possible values
   * for the attrbibute.
   *
   * The integers stored are the same that correspond
   * to the ones stored in the DataPropertyMap.
   */
  protected OpenIntObjectHashMap attributeMap;
  
 
  public String getAttributeName ( int att_id ) {
    // this is the first element in the array for this attribute
    return ( String )( ( Object[] ) attributeMap.get( att_id ) )[0];
  }


}
