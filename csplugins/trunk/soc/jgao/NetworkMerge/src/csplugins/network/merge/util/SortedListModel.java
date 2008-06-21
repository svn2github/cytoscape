/* File: SortedListModel.java

 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */


package csplugins.network.merge.util;

/**
 *
 * @author JGao
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
