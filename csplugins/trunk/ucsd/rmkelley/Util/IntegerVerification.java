package ucsd.rmkelley.Util;

import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class IntegerVerification implements ActionListener, FocusListener {
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
