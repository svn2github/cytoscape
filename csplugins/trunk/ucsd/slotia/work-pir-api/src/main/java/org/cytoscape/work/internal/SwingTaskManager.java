package org.cytoscape.work.internal;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.ResourceBundle;
import java.util.Locale;

import org.cytoscape.work.TaskManager;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;

import java.io.PrintWriter;
import java.io.StringWriter;

public class SwingTaskManager implements TaskManager
{

	Frame owner;
	Locale locale;

	public SwingTaskManager()
	{
		this(null, Locale.getDefault());
	}

	/**
	 * @param owner JDialogs created by this object
	 * will have its owner set to this parameter.
	 * @param locale The locale to display messages in
	 */
	public SwingTaskManager(Frame owner, Locale locale)
	{
		setOwner(owner);
		this.locale = locale;

		// We call this so we get MissingResourceException thrown
		// as early as possible in case the bundle is missing
		ResourceBundle.getBundle("SwingTaskManager", locale);
	}

	/**
	 * @param owner JDialogs created by this object
	 * will have its owner set to this parameter.
	 */
	public void setOwner(Frame owner)
	{
		this.owner = owner;
	}

	public void execute(final Task task)
	{
		Thread thread = new Thread(new TaskRunner(task));
		thread.start();
	}

	class TaskRunner implements Runnable
	{
		final Task task;

		public TaskRunner(Task task)
		{
			this.task = task;
		}

		public void run()
		{
			SwingTaskMonitor taskMonitor = new SwingTaskMonitor(owner, locale);
			task.run(taskMonitor);
			if (!taskMonitor.isShowingException())
				taskMonitor.close();
		}
	}
}

class SwingTaskMonitor implements TaskMonitor
{
	final ResourceBundle	messages;
	final JDialog		dialog;
	final JLabel		statusMessageLabel;
	final JProgressBar	progressBar;
	final JButton		closeButton;
	final JPanel		exceptionPanel;
	final JLabel		exceptionLabel;
	final JTextArea		exceptionArea;

	boolean cancelTask = false;

	public SwingTaskMonitor(Frame owner, Locale locale)
	{
		messages = ResourceBundle.getBundle("SwingTaskManager", locale);
		dialog = new JDialog(owner);
		dialog.setLayout(new BoxLayout(dialog.getContentPane(), BoxLayout.PAGE_AXIS));
		statusMessageLabel = new JLabel();
		progressBar = new JProgressBar(0, 100);
		progressBar.setIndeterminate(true);
		closeButton = new JButton(messages.getString("cancel"));
		closeButton.addActionListener(new CloseAction());

		exceptionPanel = new JPanel();
		exceptionPanel.setLayout(new BoxLayout(exceptionPanel, BoxLayout.PAGE_AXIS));
		exceptionLabel = new JLabel();
		exceptionArea = new JTextArea();
		exceptionPanel.add(exceptionLabel);
		exceptionPanel.add(exceptionArea);
		exceptionPanel.setVisible(false);
		
		dialog.add(statusMessageLabel);
		dialog.add(progressBar);
		dialog.add(closeButton);
		dialog.add(exceptionPanel);

		dialog.pack();
		dialog.setVisible(true);
	}

	public void close()
	{
		dialog.dispose();
	}

	class CloseAction implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			if (isShowingException())
				close();
			else
			{
				cancelTask = true;
				closeButton.setEnabled(false);
				closeButton.setText(messages.getString("canceling"));
			}
		}
	}

	public void setTitle(String title)
	{
		dialog.setTitle(title);
	}

	public void setStatusMessage(String statusMessage)
	{
		statusMessageLabel.setText(statusMessage);
	}

	public void setProgress(double progress)
	{
		progressBar.setStringPainted(true);
		progressBar.setIndeterminate(false);
		progressBar.setValue((int) (progress * 100));
	}

	public void setException(Throwable exception)
	{
		closeButton.setText(messages.getString("close"));
		closeButton.setEnabled(true);
		StringWriter stringWriter = new StringWriter();
		exception.printStackTrace(new PrintWriter(stringWriter));
		exceptionLabel.setText(exception.getMessage());
		exceptionArea.setText(stringWriter.toString());
		exceptionPanel.setVisible(true);
		dialog.pack();
	}

	public boolean needsToCancel()
	{
		return cancelTask;
	}

	public boolean isShowingException()
	{
		return closeButton.getText().equals(messages.getString("close"));
	}
}
