// Series.java: An named Array of floating point values
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
 * An named Array of floating point values, and their 
 * maximum. The maximum is assumed to be positive.
 */
public class Series {
  String name;
  float [] vals;
  float max;

  public Series(String name, float [] vals, float max) {
    this.name = name;
    this.vals = vals;
    this.max = max;
  }
  public Series(String name, float [] vals) {
    this.name = name;
    this.vals = vals;
    float max = 0;
    for(int i=0; i < this.vals.length; i++) {
      if (vals[i] > max) {
        max = vals[i];
      }
    }
    this.max = max + (max*.10f);
  }
  public float [] getVals () {
    return(vals);
  }
  public String getName () {
    return(name);
  }
  public float getMax () {
    return(max);
  }
}
