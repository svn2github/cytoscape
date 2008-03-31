/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: alokito $
 * $RCSfile: LoadProgress2.java,v $
 * $Revision: 1.1 $
 * $Date: 2007/02/03 04:58:36 $
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
package edu.stanford.genetics.treeview;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

/**
 * This is like the original loadProgress, but it adds a setPhaseValue(int), 
 * setPhaseLength(int) and setPhaseText(String);
 * 
 * Typical use will involve three threads:
 * 
 * - a worker thread, that calls routines such as incrValue() asynchronously
 * - the Swing thread, which is the ever-present thread that services the 
 *    GUI components.
 * 
 * All routines that set values must be synchronized. This is to keep
 * the special "increment" routines behaving properly. In principle, I 
 * should also synchronize the get routines, but it's okay if things read stale
 * values now and then.
 * 
 */
public class LoadProgress2 extends JDialog {
	/** set when we encounter a problem in parsing? */
	private boolean hadProblem = false;
	/** set when loading has been cancelled */
	private boolean cancelled;
	/** We hold fatal exceptions, for access by a reporting thread */
	LoadException exception = null;
	/**this is set when thread is finished, either 
	 * - had problem
	 * - cancelled
	 * - task completed (default assumption)
	 */
	boolean finished = false;  
	
	private javax.swing.Timer loadTimer;
	private JProgressBar phaseBar;
	private JProgressBar progressBar;
	private JTextArea taskOutput;
	private String newline = "\n";
	private JButton closeButton;
	private boolean indeterminate;
	private String[] phases;
	public void println(String s) {
		taskOutput.append(s + newline);
		taskOutput.setCaretPosition
		(taskOutput.getDocument().getLength());
	}

	class TimerListener implements ActionListener { // manages the FileLoader
		// this method is invoked every few hundred ms
		public void actionPerformed(ActionEvent evt) {
			if (getCanceled() || getFinished()) {
				setFinished(true);
				loadTimer.stop();
				if (getHadProblem() == false) { 
					setVisible(false);
				} else {
					setButtonText("Dismiss");
					Toolkit.getDefaultToolkit().beep();
					getToolkit().beep();
				}
			}
		}
	}
	
	/**
	 * sets value of phase progress bar
	 * @param i
	 */
	public synchronized void setPhaseValue(int i) {
		phaseBar.setValue(i);
	}
	public int getPhaseValue() {
		return phaseBar.getValue();
	}
	/**
	 * sets length of phase progress bar
	 * @param i
	 */
	public synchronized void setPhaseLength(int i) {
		phaseBar.setMinimum(0);
		phaseBar.setMaximum(i);
	}
	public int getPhaseLength() {
		return phaseBar.getMaximum();
	}
	/**
	 * sets test of phase bar
	 * @param i
	 */
	public synchronized void setPhaseText(String i) {
		phaseBar.setString(i);
	}


	public synchronized void setButtonText(String text) {
		closeButton.setText(text);
	}
	/**
	 * makes progress bar determinate, sets length to particular value
	 * @param i
	 */
	public synchronized void setLength(int i) {
		if (i < 0) {
			setIndeterminate(true);
		} else {
			setIndeterminate(false);
			if (progressBar.getMaximum() != i) {
				progressBar.setMinimum(0);
				progressBar.setMaximum(i);
			}
		}
	}
	public int getLength() {
		if (indeterminate)
			return -1;
		else
			return progressBar.getMaximum();
	}
	/**
	 * sets value of progress bar
	 * @param i
	 */
	public synchronized void setValue(int i) {
		progressBar.setValue(i);
	}
	public int getValue() {
		return progressBar.getValue();
	}
	public synchronized void incrValue(int i) {
		setValue(getValue() + i);
	}
	/**
	 * sets determinate state of progress bar
	 * @param flag
	 */
	public synchronized void setIndeterminate(boolean flag) {
		// actually, this only works in jdk 1.4 and up...
		progressBar.setIndeterminate(flag);
	}

	public LoadProgress2(String title, Frame f) {
		super(f, title, true);
		phaseBar = new JProgressBar();
		phaseBar.setStringPainted(true);
		loadTimer = new javax.swing.Timer(200, new TimerListener());
		loadTimer.stop();


		progressBar = new JProgressBar();
		progressBar.setValue(0);
		progressBar.setStringPainted(true);

		taskOutput = new JTextArea(10, 40);
		taskOutput.setMargin(new Insets(5,5,5,5));
		taskOutput.setEditable(false);


		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(phaseBar);
		panel.add(progressBar);

		JPanel contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(panel, BorderLayout.NORTH);
		contentPane.add(new JScrollPane(taskOutput), BorderLayout.CENTER);
		closeButton = new JButton("Cancel");
		closeButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setCanceled(true);
				LoadProgress2.this.dispose();
			}
		});
		panel = new JPanel();
		panel.add(closeButton);
		contentPane.add(panel, BorderLayout.SOUTH);

		contentPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		setContentPane(contentPane);
	}
	/** Setter for canceled */
	public synchronized  void setCanceled(boolean canceled) {
		this.cancelled = canceled;
		setButtonText("Waiting...");
	}
	/** Getter for canceled */
	public boolean getCanceled() {
		return cancelled;
	}
	/** Setter for exception */
	public synchronized void setException(LoadException exception) {
		this.exception = exception;
	}
	/** Getter for exception */
	public LoadException getException() {
		return exception;
	}
	/** Setter for hadProblem */
	public synchronized void setHadProblem(boolean hadProblem) {
		this.hadProblem = hadProblem;
	}
	/** Getter for hadProblem */
	public boolean getHadProblem() {
		return hadProblem;
	}
	/** Setter for finished */
	public synchronized  void setFinished(boolean finished) {
		this.finished = finished;
		if (getHadProblem() == false) { 
			setVisible(false);
		} else {
			setButtonText("Dismiss");
			Toolkit.getDefaultToolkit().beep();
			getToolkit().beep();
		}
	}
	/** Getter for finished */
	public boolean getFinished() {
		return finished;
	}
	public String getPhaseText() {
		// TODO Auto-generated method stub
		return phaseBar.getString();
	}
	public synchronized void setPhase(int i) {
		setPhaseValue(i+1);
		setPhaseText(phases[i]);
	}
	
	public synchronized void setPhases(String[] strings) {
		phases = strings;
		setPhaseLength(phases.length);
	}
}
