package csplugins.data.colt;


import cern.colt.matrix.ObjectMatrix2D;
import cern.colt.matrix.ObjectMatrix1D;
import cern.colt.matrix.ObjectFactory2D;
import cern.colt.matrix.ObjectFactory1D;
import cern.colt.map.PrimeFinder;
import cern.colt.list.IntArrayList;
import cern.colt.list.ObjectArrayList;

import com.sosnoski.util.hashmap.StringIntHashMap;

import java.util.Arrays;
import java.util.ArrayList;

//Data Cube Import ////////////////////
import csplugins.data.*;



/**
 * The DataPropertyMap holds all of the data for  
 * SharedIdentifiable objects. 
 *
 * The Attributes are stored as Columns, and the 
 * SharedIdentifiable as Rows.
 *
 * It is best to access the DataPropertyMap by using 
 * the default map: DataPropertyMap.defaultMap()
 */
public abstract class DataPropertyMap 
  implements DataMatrixLens, DataMatrix {

 
}
