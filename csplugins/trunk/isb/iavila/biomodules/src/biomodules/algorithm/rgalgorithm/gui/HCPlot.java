/**  Copyright (c) 2003 Institute for Systems Biology
 **  This program is free software; you can redistribute it and/or modify
 **  it under the terms of the GNU General Public License as published by
 **  the Free Software Foundation; either version 2 of the License, or
 **  any later version.
 **
 **  This program is distributed in the hope that it will be useful,
 **  but WITHOUT ANY WARRANTY; without even the implied warranty of
 **  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  The software and
 **  documentation provided hereunder is on an "as is" basis, and the
 **  Institute for Systems Biology has no obligations to provide maintenance, 
 **  support, updates, enhancements or modifications.  In no event shall the
 **  Institute for Systems Biology be liable to any party for direct, 
 **  indirect, special,incidental or consequential damages, including 
 **  lost profits, arising out of the use of this software and its 
 **  documentation, even if the Institute for Systems Biology 
 **  has been advised of the possibility of such damage. See the
 **  GNU General Public License for more details.
 **   
 **  You should have received a copy of the GNU General Public License
 **  along with this program; if not, write to the Free Software
 **  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 **/

/**
 * This class creates a JFrame that contains a plot created using the VisAD package.
 * The plot has a vertical line that can be dragged right or left by the user. The plot
 * visually presents hierarchical clustering data. On the x-axis, it has "join number", 
 * and on the y-axis it has "join distance". 
 * This plot helps the user to pick a join at which to cut the hierarchical clustering tree 
 * to form biomodules (<code>RGAlgorithm</code>).
 *
 * @author Iliana Avila-Campillo iavila@systemsbiology.org
 * @version %I%, %G%
 * @since 1.0
 */

package biomodules.algorithm.rgalgorithm.gui;

import visad.*;
import visad.java2d.DisplayImplJ2D;
import visad.util.VisADSlider;
import visad.java2d.DirectManipulationRendererJ2D;

import javax.swing.JFrame;
import java.rmi.RemoteException;
import java.awt.BorderLayout;
import java.util.HashSet;
import java.util.Iterator;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class HCPlot extends JFrame{
  
  private double [] hcDistances;
  private double minDistance;
  private double maxDistance;

  private Set whiteLine;

  private RealType distanceType,joinNumberType,index;

  private RealTupleType tuple;
  private RealTupleType rTuple;
  private FunctionType hcFunction;
   
  private FlatField flatField;
  private FunctionType djFunction;
  
  private FlatField lineFlatField;
  
  private DisplayImplJ2D display;
  private ScalarMap distanceMap,joinNumMap,joinNumRangeMap;
  
  private DataReferenceImpl cursorDataRef;
  private DataReferenceImpl wLineDataRef;

  private HashSet joinNumBarListeners;
  private int joinNumber;
  private String xLabel;
  private String yLabel;
  
  
  /**
   * Constructor.
   *
   * @param hc_distances an array with the join distances in order of join number
   * @param min_distance the minimum distance in the array
   * @param max_distance the maximum distance in the array
   */
  public HCPlot (double [] hc_distances,
                 double min_distance,
                 double max_distance,
                 int init_bar_position,
                 String x_label,
                 String y_label){
    joinNumBarListeners = new HashSet();
    this.xLabel = x_label;
    this.yLabel = y_label;
    // Set the distances, and create the plot:
    setDistances(hc_distances,
                 min_distance,
                 max_distance,
                 init_bar_position);
  }//HCPlot

  /**
   * Adds a listener that will be notified when the vertical line moves.
   */
  public void addJoinBarListener (ActionListener listener){
    joinNumBarListeners.add(listener);
  }//addDistanceBarListener

  /**
   * Removes the given listener.
   *
   * @return true if the listener is contained in the list of listeners for this
   * <code>HCPlot</code> and is successfully removed, false otherwise
   */
  public boolean removeJoinBarListener (ActionListener listener){
    return joinNumBarListeners.remove(listener);
  }//removeDistanceBarListener

  /**
   * Creates the plot.
   */
  protected void createPlot () {
    
    try{
      
      // Points props:
      ConstantMap [] pointsCMap =  { new ConstantMap( 1.0f, Display.Red ),
                                     new ConstantMap( 0.0f, Display.Green ),
                                     new ConstantMap( 0.0f, Display.Blue ),
                                     new ConstantMap( 3.50f, Display.PointSize )  };
      
      // Line over points props:
      ConstantMap [] lineCMap = {new ConstantMap( 0.0f, Display.Red),
                                 new ConstantMap( 0.8f, Display.Green),
                                 new ConstantMap( 0.0f, Display.Blue),
                                 new ConstantMap( 1.50f, Display.LineWidth)};
      // Cursor props:
      ConstantMap [] cMaps = { new ConstantMap( 0.0f, Display.Red ),
                               new ConstantMap( 1.0f, Display.Green ),
                               new ConstantMap( 0.0f, Display.Blue ),
                               new ConstantMap( 1.0f, Display.XAxis ),
                               new ConstantMap( 4f, Display.PointSize )  };
      
      distanceType = RealType.getRealType(this.yLabel);
      joinNumberType = RealType.getRealType(this.xLabel);
      index = RealType.getRealType("index");
      
      tuple = new RealTupleType(joinNumberType, distanceType);
      rTuple = new RealTupleType(distanceType,joinNumberType);

      hcFunction = new FunctionType(index, tuple);
      Integer1DSet indexSet = new Integer1DSet(index, hcDistances.length);
        
      double [][] matrix = new double [2][];
      matrix[0] = new double[hcDistances.length];
      matrix[1] = new double[hcDistances.length];
      for(int i = 0; i < hcDistances.length; i++){
        matrix[0][i] = i; //i+1
      }
      for(int i = 0; i < hcDistances.length; i++){
        matrix[1][i] = hcDistances[i];
      }
      
      flatField = new FlatField(hcFunction, indexSet);
      flatField.setSamples(matrix);

      //Line over dots code
      djFunction = new FunctionType(joinNumberType,distanceType);
      Linear1DSet joinNumSet = new Linear1DSet(joinNumberType, 0, 
                                               hcDistances.length-1, hcDistances.length);
      double [][] lineVals = new double[1][hcDistances.length];
      for(int i = 0; i < hcDistances.length; i++){
        lineVals[0][i] = hcDistances[i];
      }
      lineFlatField = new FlatField(djFunction,joinNumSet);
      lineFlatField.setSamples(lineVals);
        
      //----
      display = new DisplayImplJ2D("Hierarchical Distances");
      distanceMap = new ScalarMap(distanceType, Display.YAxis);
      joinNumMap = new ScalarMap(joinNumberType,Display.XAxis);
      joinNumRangeMap = new ScalarMap(joinNumberType,Display.SelectRange);
      display.addMap(distanceMap);
      display.addMap(joinNumMap);
      display.addMap(joinNumRangeMap);
      
      DataReferenceImpl dataRef = new DataReferenceImpl("data_ref");
      dataRef.setData(flatField);
      display.addReference(dataRef, pointsCMap);
      
      DataReferenceImpl lineRef = new DataReferenceImpl("line_ref");
      lineRef.setData(lineFlatField);
      display.addReference(lineRef, lineCMap);
      
      // The following displays axis names and scales
      GraphicsModeControl dispGMC = (GraphicsModeControl) display.getGraphicsModeControl();
      dispGMC.setScaleEnable(true);

      // Cursor code, necessary for line code

      Real cursorCoords = new Real(joinNumberType, joinNumber);
      cursorDataRef = new DataReferenceImpl("cursorDataRef");
      cursorDataRef.setData( cursorCoords );
        
      // Line code
      int numberOfPoints = 100; 
      whiteLine = (Set) makeLineSet(joinNumber, 
                                    numberOfPoints,
                                    (int)Math.floor(this.minDistance),
                                    (int)Math.ceil(this.maxDistance)); 
      wLineDataRef = new DataReferenceImpl("wLineDataRef"); 
      wLineDataRef.setData(whiteLine); 

      CellImpl cell = new CellImpl() {
          public void doAction() throws RemoteException, VisADException {

            // get the data object from the reference. We know it's a RealTuple
            Real position = (Real) cursorDataRef.getData();
            
            double positionValue = position.getValue();
            
            if(positionValue <  0){
              positionValue = 0;
            }else if(positionValue >  (hcDistances.length-1) ){
              positionValue = hcDistances.length - 1;
            }
            
            //Snap bar to closest join number:
            joinNumber = (int)Math.round(positionValue);
                        
            //System.out.println(positionValue);
            //System.out.println(joinNumber);
            
            // make a new line
            int nOfPoints = 100;
            whiteLine = (Set) makeLineSet(joinNumber, 
                                          nOfPoints,
                                          (int)Math.floor(minDistance),
                                          (int)Math.ceil(maxDistance) 
                                          );
            
            // Re-set Data, will update display
            wLineDataRef.setData(whiteLine);

            // Tell all the listeners that there was a bar event
            dispatchBarEvents();
          }
        };
      cell.addReference(cursorDataRef);
     
      display.addReferences( new DirectManipulationRendererJ2D(), cursorDataRef, cMaps );
      display.addReference(wLineDataRef);
      //-----
      getContentPane().removeAll();
      getContentPane().setLayout( new BorderLayout());
      getContentPane().add(display.getComponent(),BorderLayout.CENTER);
    }catch(Exception e){
      System.out.println("Oops! We have an exception in HCPlot.createPlot(): ");
      e.printStackTrace();
    }//catch
    
  }//createPlot

  /**
   * Moves the vertical bar to the given x-value.
   */
  public void moveVerticalBarTo (int xValue){
    
    try{
      this.joinNumber = xValue;
      Real cursorCoords = new Real(this.joinNumberType, this.joinNumber);
      this.cursorDataRef.setData(cursorCoords);
      
      // Line code
      int numberOfPoints = 100; 
      this.whiteLine = (Set) makeLineSet(this.joinNumber, 
                                         numberOfPoints,
                                         (int)Math.floor(this.minDistance),
                                         (int)Math.ceil(this.maxDistance)); 
      this.wLineDataRef.setData(whiteLine); 
    }catch(Exception e){
      e.printStackTrace();
    }
  }// moveVerticalBarTo

  /**
   * Dispatches events to the registered listeners with the command "Moved Join Bar".
   */
  private void dispatchBarEvents () {
    ActionEvent event = new ActionEvent(
                                        this,                             //source
                                        ActionEvent.ACTION_PERFORMED,    //id
                                        "Moved Join Bar"                 //command
                                        );
    
    Iterator it = joinNumBarListeners.iterator();
    ActionListener listener;
    while(it.hasNext()){
      listener = (ActionListener)it.next();
      listener.actionPerformed(event);
    }
  }//dispatchBarEvents

  /**
   * Returns the selected join number. This number is determined by the vertical line.
   */
  public int getSelectedJoinNumber () {
    return this.joinNumber;
  }//getBarDistance
  
  private Set makeLineSet( double joinVal, int pointsPerLine,
                           int lowVal, int hiVal) 
    throws VisADException, RemoteException { 
    double[][] domainSamples = new double[2][pointsPerLine]; 
    double lonVal = lowVal; 
    double increment = ( hiVal - lowVal )/ (double) pointsPerLine ; 
    for(int i=0;i < pointsPerLine;i++){ 
      domainSamples[0][i] = lonVal; 
      domainSamples[1][i] = joinVal; 
      lonVal+=increment; 
    } 
    return new Gridded2DDoubleSet( rTuple, domainSamples, pointsPerLine); 
  }//makeLineSet 
  
  /**
   * Set the join distances and its extreme values.
   * The plot gets updated.
   */
  public void setDistances (double [] hc_distances,
                            double min_distance,
                            double max_distance,
                            int bar_position){
    this.hcDistances = hc_distances;
    this.minDistance = min_distance;
    this.maxDistance = max_distance;
    this.joinNumber = bar_position;
    // update plot
    createPlot();
  }//setDistances
         
}//HCPlot
