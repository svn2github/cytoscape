package ucsd.rmkelley.Util;

import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;


public class DoubleVerification implements ActionListener, FocusListener {
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
