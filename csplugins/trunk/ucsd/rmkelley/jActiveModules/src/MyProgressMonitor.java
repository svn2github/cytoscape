package csplugins.jActiveModules;
import javax.swing.*;
public class MyProgressMonitor extends ProgressMonitor{
  int current;
  int steps;
  public MyProgressMonitor(java.awt.Component parentComponent, Object message, String note, int min, int max){
    super(parentComponent,message,note,min,max);
    current = min;
    steps = max-min;
  }
    
  public synchronized void update(){
    super.setProgress(current++);
  }

  public int getSteps(){
    return steps;
  }
}
