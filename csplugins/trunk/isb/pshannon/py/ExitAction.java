package csplugins.isb.pshannon.py;

import java.awt.event.ActionEvent;
import javax.swing.*;

/**
 * Quits the SPyConsole application
 * @author Jeff Davies
 * @version 1.0
 */

public class ExitAction extends AbstractAction {

   private JFrame parent;
   
   public ExitAction(JFrame parent) {
      super("Exit");
   	  this.parent = parent;
   }

   public void actionPerformed(ActionEvent parm1) {

    ThreadGroup currentGroup = Thread.currentThread().getThreadGroup();
	int numThreads = currentGroup.activeCount();
	Thread[] listOfThreads = new Thread[numThreads];
	currentGroup.enumerate(listOfThreads);
    for (int i = 0; i < listOfThreads.length; i++) {
		if (null != listOfThreads[i]) {
			if ("jythonConsoleThread".equals(listOfThreads[i].getName())) {
				listOfThreads[i].stop(); // yes, i know.
			}
		}
	}
   	if (null != parent)parent.dispose();
   }
}
