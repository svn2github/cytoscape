package csplugins.isb.pshannon.py;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.event.*;

/**
 * Menubar for the SPyConsole app.
 * @author Jeff Davies
 * @version 1.0
 */
public class ConsoleMenubar extends JMenuBar {

   JMenu mnuFile = new JMenu("File");
   JMenu mnuEdit = new JMenu("Edit");
   JMenu mnuHelp = new JMenu("Help");
   SPyConsole _console = null;
   JFrame parent = null;
   
   public ConsoleMenubar(SPyConsole con) {
   	   this(con, null);
   }
   
   public ConsoleMenubar(SPyConsole con, JFrame parent) {
      _console = con;
      this.parent = parent;
	  JMenuItem        menuItem;
	  
	  
      mnuFile.addSeparator();

      // Add the menu items for the File menu
      mnuFile.add(new LoadScriptAction(_console));
      //mnuFile.add(new SaveScriptAction(_console,false));
      mnuFile.add(new SaveScriptAction(_console,true));
      
      mnuFile.addSeparator();
      mnuFile.add(new ExitAction(parent));
      this.add(mnuFile);

	  menuItem = mnuEdit.add(new JMenuItem("Cut"));
	  menuItem.addActionListener( _console.getAction( DefaultEditorKit.cutAction ) );

	  menuItem = mnuEdit.add(new JMenuItem("Copy"));
	  menuItem.addActionListener( _console.getAction( DefaultEditorKit.copyAction ) );

      mnuEdit.add(new PasteAction(_console));
      this.add(mnuEdit);

      mnuHelp.add(new AboutBoxAction());
      this.add(mnuHelp);
   }
   
   public void addFileMenuItem( String label, ActionListener l ) {
	  JMenuItem menuItem = mnuFile.insert(new JMenuItem(label),0);
	  menuItem.addActionListener(l);
   }
   
   public void addHelpMenuItem( String label, ActionListener l ) {
	  JMenuItem menuItem = mnuHelp.insert(new JMenuItem(label),0);
	  menuItem.addActionListener(l);
   }
}
