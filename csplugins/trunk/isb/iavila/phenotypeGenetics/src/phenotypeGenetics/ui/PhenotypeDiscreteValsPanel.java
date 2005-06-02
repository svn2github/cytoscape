/**
 * A JPanel that contains sliders for each discrete phenotype value so that
 * the user can order them with respect to each other.
 *
 * @author Iliana Avila
 * @version 2.0
 */

package phenotypeGenetics.ui;

import phenotypeGenetics.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.Hashtable;
import java.awt.GridLayout;
import cern.colt.list.ObjectArrayList;


public class PhenotypeDiscreteValsPanel{
  
  protected static final int MAX_SLIDER_SIZE = 100;
  protected String phenotypeName;
  protected String [] phenotypeVals;
  protected JPanel panel;
  protected ArrayList sliders;
  protected List phenotypeBins;

  /**
   * Constructor, takes a Phenotype name and an array of its possible unranked values.
   */
  public PhenotypeDiscreteValsPanel (String phenotype_name, 
                                     String [] phenotype_values){
    this.phenotypeBins = new ArrayList();
    this.phenotypeName = phenotype_name;
    this.phenotypeVals = phenotype_values;
    create();
  }//PhenotypeDiscreteValsPanel

  /**
   * Constructor
   *
   * @param phenotype_name the name of the phenotype that this panel represents
   * @param values_bins a List of Lists of Strings of ranked phenotype values
   * @see DiscretePhenotypeRanking
   */
  public PhenotypeDiscreteValsPanel (String phenotype_name, List values_bins){
    this.phenotypeBins = values_bins;
    this.phenotypeName = phenotype_name;
    
    List values = new ArrayList();
    Iterator it = values_bins.iterator();
    while(it.hasNext()){
      List aBin = (List)it.next();
      Iterator it2 = aBin.iterator();
      while(it2.hasNext()){
        String value = (String)it2.next();
        values.add(value);
      }//it2
    }//it
    
    this.phenotypeVals = (String[])values.toArray(new String[0]);
    create();
  }//PhenotypeDiscreteValsPanel
  
  /**
   * @return the created panel
   */
  public JPanel getPanel (){
    return this.panel;
  }//getPanel

  /**
   * @return the name of the phenotype that this panel represents
   */
  public String getPhenotypeName (){
    return this.phenotypeName;
  }//getPhenotypeName

  /**
   * @return the values that this phenotype can take
   */
  public String [] getPhenotypeValues (){
    return this.phenotypeVals;
  }//getPhenotypeValues

  /**
   * @return
   */
  public List getPhenotypeBins (){
    
    // Sort the phenotype-values according to their slider value:
    ObjectArrayList phenosToSort = new ObjectArrayList();
    
    for(int i = 0; i < this.sliders.size(); i++){
      PhenoValue phenoValue = new PhenoValue();
      phenoValue.name = this.phenotypeVals[i];
      phenoValue.value = ((JSlider)this.sliders.get(i)).getValue();
      phenosToSort.add(phenoValue);
    }//for i
    
    // sorts in ascending order
    phenosToSort.sort();
    
    // Bin phenotype-values if they have the same slider value
    ArrayList bins = new ArrayList();
    PhenoValue [] sortedVals = (PhenoValue[])phenosToSort.toArray(new PhenoValue[0]);
    int index = 0;
    while(index < sortedVals.length){
      ArrayList aBin = new ArrayList();
      aBin.add(sortedVals[index].name);
      int j = index + 1;
      if(j >= sortedVals.length){
        index = j;
      }
      while(j < sortedVals.length){
        if(sortedVals[j].value == sortedVals[index].value){
          aBin.add(sortedVals[j].name);
        }else{
          index = j;
          break;
        }
        j++;
      }//while j
      bins.add(aBin);
    }//while
    
    this.phenotypeBins = bins;
        
    return this.phenotypeBins;
  }//getPhenotypeRelations

  /**
   * Creates the panel
   */
  protected void create (){
    this.panel = new JPanel();
    this.panel.setLayout(new BoxLayout(this.panel, BoxLayout.Y_AXIS));
    
    JLabel label = new JLabel("Possible values for phenotype " + this.phenotypeName + ":");
    JPanel labelPanel = new JPanel();
    labelPanel.add(label);
    this.panel.add(labelPanel);
    
    this.sliders = new ArrayList();
    JPanel sPanel = new JPanel();
    sPanel.setLayout(new GridLayout(this.phenotypeVals.length, 1)); //rows,cols
    
    if(this.phenotypeBins.size() == 0){
      
      for(int i = 0; i < this.phenotypeVals.length; i++){
        
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
        
        JLabel l = new JLabel(this.phenotypeVals[i]);
        p.add(l);
        
        JSlider slider = new JSlider(0,MAX_SLIDER_SIZE,50); //min, max, value
        this.sliders.add(slider);
        Hashtable labelTable = new Hashtable();
        labelTable.put(new Integer(0), new JLabel("Less"));
        labelTable.put(new Integer(MAX_SLIDER_SIZE), new JLabel("Greater"));
        slider.setLabelTable(labelTable);
        slider.setPaintLabels(true);
        slider.setMinorTickSpacing(MAX_SLIDER_SIZE/10);
        slider.setPaintTicks(true);
        slider.setSnapToTicks(true);
        slider.setPaintTrack(true);
        p.add(slider);
        
        sPanel.add(p);
      
      }//for i
    }else{
      // the bins are already set, so make the phenotype notches represent
      // the ranking
      int numBins = this.phenotypeBins.size();
      int notchInterval = MAX_SLIDER_SIZE/numBins;
      Iterator it = this.phenotypeBins.iterator();
      int rank = -1;
      while(it.hasNext()){
        List aBin = (List)it.next();
        Iterator it2 = aBin.iterator();
        rank++;
        while(it2.hasNext()){
          String phenoValue = (String)it2.next();
          JPanel p = new JPanel();
          p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
        
          JLabel l = new JLabel(phenoValue);
          p.add(l);
                  
          int notchValue = rank*notchInterval;
          JSlider slider = new JSlider(0,MAX_SLIDER_SIZE,notchValue); //min, max, value
          this.sliders.add(slider);
          Hashtable labelTable = new Hashtable();
          labelTable.put(new Integer(0), new JLabel("Less"));
          labelTable.put(new Integer(MAX_SLIDER_SIZE), new JLabel("Greater"));
          slider.setLabelTable(labelTable);
          slider.setPaintLabels(true);
          slider.setMinorTickSpacing(MAX_SLIDER_SIZE/10);
          slider.setPaintTicks(true);
          slider.setSnapToTicks(true);
          slider.setPaintTrack(true);
          p.add(slider);
          
          sPanel.add(p);
        }//it2
      }//it
    }//else
    
    this.panel.add(sPanel);
    
  }//create
  
  //--------------------- Internal classes ------------------------//

  protected class PhenoValue implements Comparable{
    public String name;
    public int value;
    
    /**
     * @return a negative integer, zero, or a positive integer as this object 
     * is less than, equal to, or greater than the specified object.
     */
    public int compareTo (Object other){
      PhenoValue otherPV = (PhenoValue)other;
      if(this.value < otherPV.value){
        return -1;
      }
      
      if(this.value > otherPV.value){
        return 1;
      }
      return 0;
    }//compareTo
  
  }//PhenoValue


}//PhenotypeDiscreteValsPanel
