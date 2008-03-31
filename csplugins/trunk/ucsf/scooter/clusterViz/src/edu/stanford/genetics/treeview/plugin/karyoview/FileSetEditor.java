/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: rqluk $
 * $RCSfile: FileSetEditor.java,v $
 * $Revision: 1.1 $
 * $Date: 2006/08/16 19:13:49 $
 * $Name:  $
 *
 * This file is part of Java TreeView
 * Copyright (C) 2001-2003 Alok Saldanha, All Rights Reserved. Modified by Alex Segal 2004/08/13. Modifications Copyright (C) Lawrence Berkeley Lab.
 *
 * This software is provided under the GNU GPL Version 2. In particular, 
 *
 * 1) If you modify a source file, make a comment in it containing your name and the date.
 * 2) If you distribute a modified version, you must do it under the GPL 2.
 * 3) Developers are encouraged but not required to notify the Java TreeView maintainers at alok@genome.stanford.edu when they make a useful addition. It would be nice if significant contributions could be merged into the main distribution.
 *
 * A full copy of the license can be found in gpl.txt or online at
 * http://www.gnu.org/licenses/gpl.txt
 *
 * END_HEADER 
 */
package edu.stanford.genetics.treeview.plugin.karyoview;

import edu.stanford.genetics.treeview.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
/**
* This class allows editing of a file set...
*/

public class FileSetEditor extends JPanel {
  private FileSet fileSet;
  /** Setter for fileSet */
  public void setFileSet(FileSet fileSet) {
	  this.fileSet = fileSet;
  }
  /** Getter for fileSet */
  public FileSet getFileSet() {
	  return fileSet;
  }
 public FileSetEditor(FileSet fileSet, Window jFrame) {
	 setFileSet(fileSet);
	 final JLabel desc = new JLabel(fileSet.toString());
	 add(desc);
	 final Window frame = jFrame;
	  JButton pushButton = new JButton("Find...");
	  pushButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {			
			JFileChooser fileDialog = new JFileChooser();
			CdtFilter ff = new CdtFilter();
			fileDialog.setFileFilter(ff);
			
			String string = getFileSet().getDir();
			if (string != null) {
				fileDialog.setCurrentDirectory(new File(string));
			}
			int retVal = fileDialog.showOpenDialog(frame);
			if (retVal == JFileChooser.APPROVE_OPTION) {
				File chosen = fileDialog.getSelectedFile();
				
				FileSet fileSet1 = new FileSet(chosen.getName(), chosen.getParent()+File.separator);
				fileSet1.setName(getFileSet().getName());
				getFileSet().copyState(fileSet1);
			}
			desc.setText(getFileSet().toString());
			desc.revalidate();
			desc.repaint();
		}
	  });
	  add(pushButton);
  }
  
  public static final void main(String [] argv) {
	FileSet temp = new FileSet(new DummyConfigNode("DummyFileSet"));
	JFrame frame = new JFrame("FileSetEditor Test");
	FileSetEditor cse = new FileSetEditor(temp, frame);
	frame.getContentPane().add(cse);
	frame.pack();
	frame.show();
  }
  
}
