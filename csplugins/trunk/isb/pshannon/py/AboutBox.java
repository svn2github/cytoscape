package csplugins.isb.pshannon.py;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

/**
 * A little dialog to give credit where credit is due
 *
 * @author Jeff Davies
 * @version 1.0
 */

public class AboutBox extends JDialog {
   JPanel panel1 = new JPanel();
   GridBagLayout gridBagLayout1 = new GridBagLayout();
   JLabel jlblAppName = new JLabel();
   JLabel jlblAuthorLabel = new JLabel();
   JLabel jlblAuthorName = new JLabel();
   JLabel jlblAuthor2 = new JLabel();
   JLabel jlblCopyrightLabel = new JLabel();
   JTextArea jtxtCopyright = new JTextArea();
   JButton jbtnOK = new JButton();

   public AboutBox(Frame frame, String title, boolean modal) {
      super(frame, title, modal);
      try {
         jbInit();
         pack();
      }
      catch(Exception ex) {
         ex.printStackTrace();
      }

      //this.setSize(500, 400);
		//pack();

		// Center the dialog
		Toolkit tk = this.getToolkit();
		Dimension d = this.getSize();
		int x = (tk.getScreenSize().width - d.width) / 2;
		int y = (tk.getScreenSize().height - d.height) / 2;
		this.setLocation(x, y);
   }

   public AboutBox() {
      this(null, "", false);
   }
   void jbInit() throws Exception {
      panel1.setLayout(gridBagLayout1);
      jlblAppName.setFont(new java.awt.Font("Serif", 1, 24));
      jlblAppName.setText("SPyConsole");
      jlblAuthorLabel.setText("Authors:");
      jlblAuthorName.setText("Tom Maxwell (maxwell@cbl.umces.edu)");
      jlblAuthor2.setText("GUI enhancements by Jeff Davies (xaan@xaan.com) - Modified By dtenenbaum & pshannon @isb");
      jlblCopyrightLabel.setText("Copyright:");
      jtxtCopyright.setText("GNU - General Public License");
      jtxtCopyright.setBackground(Color.lightGray);
      jtxtCopyright.setEditable(false);
      jtxtCopyright.setFont(new java.awt.Font("SansSerif", 0, 12));
      jbtnOK.setText("OK");
      jbtnOK.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(ActionEvent e) {
            jbtnOK_actionPerformed(e);
         }
      });
      this.setModal(true);
      this.setTitle("About SPyConsole");
      getContentPane().add(panel1);
      panel1.add(jlblAppName, new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10, 10, 10, 10), 0, 0));
      panel1.add(jlblAuthorLabel, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 10, 4), 0, 0));
      panel1.add(jlblAuthorName, new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 10, 0), 0, 0));
      panel1.add(jlblAuthor2, new GridBagConstraints(1, 2, 1, 1, 1.0, 1.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 6, 0), 0, 0));
      panel1.add(jlblCopyrightLabel, new GridBagConstraints(0, 3, 1, 1, 1.0, 1.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 10, 6), 0, 0));
      panel1.add(jtxtCopyright, new GridBagConstraints(1, 3, 1, 1, 1.0, 1.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 10, 10), 0, 0));
      panel1.add(jbtnOK, new GridBagConstraints(0, 4, 2, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 20, 10, 20), 10, 0));
   }

   void jbtnOK_actionPerformed(ActionEvent e) {
      this.dispose();
   }
}
