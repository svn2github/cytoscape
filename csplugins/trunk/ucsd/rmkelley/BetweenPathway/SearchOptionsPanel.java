package ucsd.rmkelley.BetweenPathway;

import javax.swing.JPanel;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.ButtonGroup;


public class SearchOptionsPanel extends JPanel{

  JCheckBox selectedSearch, newScore, specifyCutoff, generateCutoff;
  JTextField cutoffField, alphaField,iterationsField;

  public SearchOptionsPanel(){
    
    //create the display objects
    selectedSearch = new JCheckBox("Search from selected interactions",false);
    newScore = new JCheckBox("Use new scoring model?",true);
    specifyCutoff = new JCheckBox("Specify score cutoff?",true);
    generateCutoff = new JCheckBox("Generate score cutoff?",false);
    cutoffField = new JTextField("0.0",10);
    alphaField = new JTextField("0.05",10);
    alphaField.setEnabled(false);
    iterationsField = new JTextField("100",10);
    iterationsField.setEnabled(false);

    ButtonGroup group = new ButtonGroup();
    group.add(specifyCutoff);
    group.add(generateCutoff);

    //set up the behaviors of the objects
    specifyCutoff.addChangeListener(new ChangeListener(){
	public void stateChanged(ChangeEvent e){	
	  cutoffField.setEnabled(specifyCutoff.isSelected());
	}});
    generateCutoff.addChangeListener(new ChangeListener(){
	public void stateChanged(ChangeEvent e){	
	  alphaField.setEnabled(generateCutoff.isSelected());
	  iterationsField.setEnabled(generateCutoff.isSelected());
	}});
    
    //set up the verification agents for the particular data types
    DoubleVerification verifyCutoff = new DoubleVerification(cutoffField,0.0,Double.POSITIVE_INFINITY,0.0);
    DoubleVerification verifyAlpha = new DoubleVerification(alphaField,0.0,1.0,0.05);
    IntegerVerification verifyIterations = new IntegerVerification(iterationsField,1,Integer.MAX_VALUE,100);
    
    //add into the layout
    add(selectedSearch);
    add(newScore);
    add(specifyCutoff);
    add(generateCutoff);
    add(cutoffField);
    add(alphaField);
    add(iterationsField);

  }


  public boolean selectedSearch(){
    return selectedSearch.isSelected();
  }

  public boolean newScore(){
    return newScore.isSelected();
  }

  public boolean generateCutoff(){
    return generateCutoff.isSelected();
  }

  public double getCutoff(){
    return (new Double(cutoffField.getText()).doubleValue());
  }

  public double getAlpha(){
    return (new Double(alphaField.getText())).doubleValue();
  }

  public int getIterations(){
    return (new Integer(iterationsField.getText())).intValue();
  }

}




class IntegerVerification implements ActionListener, FocusListener {
  JTextField targetField;
  int min,max,def;
  public IntegerVerification(JTextField targetField, int min, int max, int def){
    this.targetField = targetField;
    this.min = min;
    this.max = max;
    this.def = def;
    targetField.addActionListener(this);
    targetField.addFocusListener(this);
  }

  public void handleEvent(){
    String text = targetField.getText();
    int value = def;
    try{
      value = Integer.parseInt(text);
    }catch(NumberFormatException nfe){

    }
    if(value < min){
      value = min;
    }
    if(value > max){
      value = max;
    }
    targetField.setText(Integer.toString(value));
  }
  public void actionPerformed(ActionEvent ae){
    handleEvent();
  }
  public void focusGained(FocusEvent fe){}
  public void focusLost(FocusEvent fe){
    handleEvent();
  }
}

class DoubleVerification implements ActionListener, FocusListener {
  JTextField targetField;
  double min,max,def;
  public DoubleVerification(JTextField targetField, double min, double max, double def){
    this.targetField = targetField;
    this.min = min;
    this.max = max;
    this.def = def;
    targetField.addActionListener(this);
    targetField.addFocusListener(this);
  }

  public void handleEvent(){
    String text = targetField.getText();
    double value = def;
    try{
      value = Double.parseDouble(text);
    }catch(NumberFormatException nfe){

    }
    if(value < min){
      value = min;
    }
    if(value > max){
      value = max;
    }
    targetField.setText(Double.toString(value));
  }
  public void actionPerformed(ActionEvent ae){
    handleEvent();
  }
  public void focusGained(FocusEvent fe){}
  public void focusLost(FocusEvent fe){
    handleEvent();
  }
}
