package csplugins.jActiveModules;
import javax.swing.ProgressMonitor;
public class MyProgressMonitor extends ProgressMonitor{
  int current;
  int steps;
  int onePercent;
  public MyProgressMonitor(java.awt.Component parentComponent, Object message, String note, int min, int max){
    super(parentComponent,message,note,min,max);
    current = min;
    steps = max-min;
    onePercent = (int)(0.01 * (float)steps) + 1;
    super.setMillisToDecideToPopup(1);
    super.setMillisToPopup(1);
  }
    
  public synchronized void update(){
    current++;
    // This check forces the progress bar to appear
    // when the number of steps is large and the time
    // a step takes is long.  Otherwise, you'd have
    // to wait for the 1% threshold, which could be
    // a while.
    if ( ((float)current)/((float)steps) < 0.01 ) {
    	super.setProgress(onePercent);
    } else {
    	super.setProgress(current);
    }
  }

  public int getSteps(){
    return steps;
  }
}
