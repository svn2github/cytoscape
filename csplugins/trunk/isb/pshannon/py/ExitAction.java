package csplugins.isb.pshannon.py;

import java.awt.event.ActionEvent;
import javax.swing.*;

/**
 * Quits the SPyConsole application
 * @author Jeff Davies
 * @version 1.0
 */

public class ExitAction extends AbstractAction {

   public ExitAction() {
      super("Exit");
   }

   public void actionPerformed(ActionEvent parm1) {
      System.exit(0);
   }
}
