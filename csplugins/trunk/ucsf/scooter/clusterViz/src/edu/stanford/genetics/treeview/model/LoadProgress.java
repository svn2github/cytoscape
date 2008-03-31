/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: alokito $
 * $RCSfile: LoadProgress.java,v $
 * $Revision: 1.6 $
 * $Date: 2004/12/21 03:28:12 $
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
package edu.stanford.genetics.treeview.model;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

/**
 * A simple progress dialog. It has a progress bar, a text area which lines can be added to, 
 * and a cancel button with customizable text.
 * 
 * @author aloksaldanha
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class LoadProgress extends JDialog {
    private JProgressBar progressBar;
    private JTextArea taskOutput;
    private String newline = "\n";
	private JButton closeButton;
	public void clear(){
		taskOutput.setText("");
	}
    public void println(String s) {
	taskOutput.append(s + newline);
	taskOutput.setCaretPosition
	    (taskOutput.getDocument().getLength());
    }
	
	public void setButtonText(String text) {
	  closeButton.setText(text);
	}
    public void setLength(int i) {
		setIndeterminate(false);
		if (progressBar.getMaximum() != i) {
			progressBar.setMinimum(0);
			progressBar.setMaximum(i);
		}
    }
    public void setValue(int i) {
	progressBar.setValue(i);
    }
    public void setIndeterminate(boolean flag) {
	// actually, this only works in jdk 1.4 and up...
		// progressBar.setIndeterminate(flag);
    }

    public LoadProgress(String title, Frame f) {
	super(f, title, true);
	progressBar = new JProgressBar();
	progressBar.setValue(0);
	progressBar.setStringPainted(true);
	
	taskOutput = new JTextArea(10, 40);
	taskOutput.setMargin(new Insets(5,5,5,5));
	taskOutput.setEditable(false);
	

	JPanel panel = new JPanel();
	panel.add(progressBar);
    
	JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(panel, BorderLayout.NORTH);
        contentPane.add(new JScrollPane(taskOutput), BorderLayout.CENTER);
	closeButton = new JButton("Cancel");
	closeButton.addActionListener( new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			setCanceled(true);
		    LoadProgress.this.dispose();
		}
	    });
	panel = new JPanel();
	panel.add(closeButton);
	contentPane.add(panel, BorderLayout.SOUTH);

        contentPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setContentPane(contentPane);
    }
	boolean canceled;
	/** Setter for canceled */
	public void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}
	/** Getter for canceled */
	public boolean getCanceled() {
		return canceled;
	}
}
