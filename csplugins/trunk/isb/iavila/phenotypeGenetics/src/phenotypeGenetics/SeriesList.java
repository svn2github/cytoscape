// SeriesList.java: An extension of an ArrayList
//-----------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//------------------------------------------------------------------------------
package csplugins.phenotypeGenetics;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.font.*;
import java.util.*;
import cytoscape.*;

/**
 * An extension of ArrayList,with a maximal floating point value, 
 * and string array. Stores Series objects. 
 * The maximum is the geatest of all the Series maxima. 
 * 
 * @see Series
 */


public class SeriesList extends java.util.ArrayList {
  String [] labels;
  float max;
  
  public SeriesList (String [] labels) {
    super();
    this.labels = labels;
    this.max = 0.0f;
  }

/**    
 * Add another Series, adjust maximum if needed.
 */
  public boolean add (Series s) {
    boolean r = super.add(s);
    if (s.getMax() > this.max) {
      this.max = s.getMax();
    }
    return(r);
  }

/**    
 * Remove a Series, adjust maximum if needed.
 */
  public Object remove (int i) {
    Object o = super.remove(i);
    this.max = 0;
    for (i=0; i<this.size(); i++) {
      Series s = (Series)super.get(i);
      if (s.getMax() > this.max) {
        this.max = s.getMax();
      }
    }
    return(o);
  }

  public float getMax () {
    return(max);
  }

  public String [] getLabels () {
    return(labels);
  }
}
