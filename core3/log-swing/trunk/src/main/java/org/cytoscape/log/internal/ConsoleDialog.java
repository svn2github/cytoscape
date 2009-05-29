package org.cytoscape.log.internal;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.*;

class ConsoleDialog extends JDialog
{
	final static String COLOR_PARITY_TRUE  = "ffffff";
	final static String COLOR_PARITY_FALSE = "eeeeee";
	final static String ENTRY_TEMPLATE
		= "<html><body bgcolor=\"#%s\">"
		+ "<table border=0 width=\"100%%\" cellspacing=5>"
		+ "<tr><td width=\"0%%\"><img src=\"%s\"></td>"
		+ "<td><h3>%s</h3></td></tr>"
		+ "<tr><td></td><td><font size=\"-2\" color=\"#555555\">"
		+ "%s</font></td></tr></table></body></html>";
	final static Map<String,String> ICON_NAMES = new TreeMap<String,String>();
	static
	{
		ICON_NAMES.put("error",		"console-error.png");
		ICON_NAMES.put("info",		"console-info.png");
		ICON_NAMES.put("warning",	"console-warning.png");
	}
	final static String BASE_HTML_PATH = "/consoledialogbase.html";

	JEditorPane editorPane;
	HTMLDocument document;
	Element root;
	boolean colorParity = true;
	JScrollPane scrollPane;

	public ConsoleDialog()
	{
		super((java.awt.Frame) null, "Console", false);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setPreferredSize(new Dimension(650, 350));
		setLayout(new BorderLayout());
		editorPane = new JEditorPane();
		editorPane.setEditable(false);
		clearConsole();
		scrollPane = new JScrollPane(editorPane);

		add(scrollPane, BorderLayout.CENTER);
		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton clearButton = new JButton("  Clear  ");
		clearButton.addActionListener(new ClearAction());
		buttons.add(clearButton);
		add(buttons, BorderLayout.PAGE_END);
		pack();
	}

	class ClearAction implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			clearConsole();
		}
	}


	void append(String iconName, String message, String timeStamp)
	{
		final String bgColor = (colorParity ? COLOR_PARITY_TRUE : COLOR_PARITY_FALSE);
		try
		{
			document.insertBeforeEnd(root,
				String.format(ENTRY_TEMPLATE,
						bgColor, ICON_NAMES.get(iconName),
						message, timeStamp));
			scrollToBottom();
		}
		catch (BadLocationException e) {}
		catch (IOException e) {}
		colorParity = !colorParity;
	}

	void scrollToBottom()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				JScrollBar scrollBar = scrollPane.getVerticalScrollBar();
				if (scrollBar != null)
					scrollBar.setValue(scrollBar.getMaximum());
			}
		});
	}

	void clearConsole()
	{
		editorPane.setText("");
		editorPane.setContentType("text/html");
		try
		{
			editorPane.setPage(getClass().getResource(BASE_HTML_PATH));
		}
		catch (IOException e) {}
		document = (HTMLDocument) editorPane.getDocument();
		root = document.getRootElements()[0];
		colorParity = true;
	}
}
