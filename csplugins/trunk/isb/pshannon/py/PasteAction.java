package csplugins.isb.pshannon.py;

import java.awt.event.ActionEvent;
import javax.swing.*;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.StringReader;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

/**
 * This action is executed whenever the user wants to paste text into the
 * console
 *
 * @author Jeff Davies
 * @version 1.0
 */

public class PasteAction extends AbstractAction {

   SPyConsole console = null;

   public PasteAction(SPyConsole con) {
      super("Paste");
      console = con;
   }

   public void actionPerformed( ActionEvent e ) {
      try {
         Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
         Transferable trans = clip.getContents(this);
         String data = (String)trans.getTransferData(DataFlavor.stringFlavor);
         if (data.charAt(data.length()-1)==0) {
         	data = data.substring(0,data.length()-1);
         }
         console.executeCommandSet(data);
      } catch (IOException ex) {
         ex.printStackTrace();
      } catch (UnsupportedFlavorException ex) {
         ex.printStackTrace();
      }
   }
}
