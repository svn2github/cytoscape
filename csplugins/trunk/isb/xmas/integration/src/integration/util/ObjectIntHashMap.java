package integration.util;

import java.util.*;
import cern.colt.map.PrimeFinder;

/**
 * @author Rowan Christmas
 * This class provides an easy to use Map that allows for 
 * ints to be stored as keys of Objects.
 */
public class ObjectIntHashMap extends HashMap {

  /**
   * Creates a new ObjectIntHashMap, that is empty
   */
  public ObjectIntHashMap () {
    super();
  }

  /**
   * Create a new Empty ObjectIntHashMap, with at least the given
   * initial capacity.
   * @param initial_capacity the initial capacity
   */
  public ObjectIntHashMap ( int initial_capacity ) {
    super( PrimeFinder.nextPrime( initial_capacity ) );
  }

  /**
   * Return the int that is mapped to the given Object
   * @param key key   
   */
  public int getInt ( Object key ) {
    return ( ( Integer )super.get( key ) ).intValue();
  }

  /**
   * Set the given value to the given key
   */
  public int putInt ( Object key, int value ) {
    super.put( key, new Integer( value ) );
    return value;
  }

  /**
   * Remove the key value pair for the given key
   */
  public int removeInt ( Object key ) {
    return ( ( ( Integer )super.remove( key ) ).intValue() );
  }
  
  /**
   * Return all of the values in this map
   */
  public int[] intValues () {
    Iterator i = super.values().iterator();
    int[] array = new int[ super.values().size() ];
    int counter = 0;
    while ( i.hasNext() ) {
      array[ counter ] = ( ( Integer ) i.next() ).intValue();
      counter++;
    }
    return array;
  }
  



} //class ObjectIntHashMap
