package SawdVisualizer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.text.*;

public class ErrorDialog extends JDialog
{
	private ErrorDialog()
	{
		setTitle("SawdVisualizer: Errors");
		
		jScrollPane1 = new javax.swing.JScrollPane();
		jTextArea1 = new javax.swing.JTextArea();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		jTextArea1.setColumns(20);
		jTextArea1.setRows(5);
		jScrollPane1.setViewportView(jTextArea1);

		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
		    layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		    .add(layout.createSequentialGroup()
			.addContainerGap()
			.add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 569, Short.MAX_VALUE)
			.addContainerGap())
		);
		layout.setVerticalGroup(
		    layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		    .add(layout.createSequentialGroup()
			.addContainerGap()
			.add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 363, Short.MAX_VALUE)
			.addContainerGap())
		);
		pack();
	}
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;

	private void addMessage(String message)
	{
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		Date date = new Date();
		jTextArea1.append("[");
		jTextArea1.append(dateFormat.format(date));
		jTextArea1.append("] ");
		jTextArea1.append(message);
		jTextArea1.append("\n");
	}

	private static ErrorDialog errorDialog = null;
	public static void report(String message)
	{
		if (errorDialog == null)
			errorDialog = new ErrorDialog();
		errorDialog.setVisible(true);
		errorDialog.addMessage(message);
	}
}
