package csplugins.isb.pshannon.py;

import java.awt.event.ActionEvent;
import javax.swing.*;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class AboutBoxAction extends AbstractAction {

   public AboutBoxAction() {
      super("About SPyConsole...");
   }

   public void actionPerformed(ActionEvent parm1) {
      AboutBox abtBox = new AboutBox();
      abtBox.setModal(true);
      abtBox.show();
   }
}
