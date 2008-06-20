/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package csplugins.network.merge.util;

/**
 *
 * @author gjj
 */

import javax.swing.AbstractListModel;

import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map;
import java.util.Iterator;

public class SortedListModel extends AbstractListModel {

  // Define a SortedSet
  SortedMap model;

  public SortedListModel() {
    // Create a TreeSet
    // Store it in SortedSet variable
    model = new TreeMap();
  }

  // ListModel methods
  public int getSize() {
    // Return the model size
    return model.size();
  }

  public Object getElementAt(int index) {
    // Return the appropriate element
    return model.values().toArray()[index];
  }

  public Object getKeyAt(int index) {
    // Return the appropriate element
    return model.keySet().toArray()[index];
  }
  
  // Other methods
  public void add(Object key, Object value) {
    model.put(key,value);
    fireContentsChanged(this, 0, getSize());
  }

  public void addAll(Map m) {
    model.putAll(m);
    fireContentsChanged(this, 0, getSize());
  }

  public void clear() {
    model.clear();
    fireContentsChanged(this, 0, getSize());
  }

  public boolean contains(Object key) {
    return model.containsKey(key);
  }

  public Iterator iterator() {
    return model.values().iterator();
  }

  public Object removeElement(Object key) {
    Object removed = model.remove(key);
    if (removed!=null) {
      fireContentsChanged(this, 0, getSize());
    }
    return removed;   
  }
}
