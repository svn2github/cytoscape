package org.cytoscape.log.internal;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.*;

import org.apache.log4j.Level;
import org.cytoscape.log.statusbar.CytoStatusBar;
import cytoscape.view.CySwingApplication;

/**
 * Displays the Console's dialog.
 * This class does not read the output of the user log;
 * it only specifies the user interface.
 * @author Pasteur
 */
class ConsoleDialog extends JDialog
{
	/**
	 * Messages in the Console alternate background colors to improve readability.
	 * The COLOR_PARITY variables specify the colors of the background.
	 */
	final static String COLOR_PARITY_TRUE  = "ffffff";
	final static String COLOR_PARITY_FALSE = "eeeeee";

	/**
	 * The HTML template for each message in the Console.
	 */
	final static String ENTRY_TEMPLATE
		= "<html><body bgcolor=\"#%s\">"
		+ "<table border=0 width=\"100%%\" cellspacing=5>"
		+ "<tr><td width=\"0%%\"><img src=\"%s\"></td>"
		+ "<td><h3>%s</h3></td></tr>"
		+ "<tr><td></td><td><font size=\"-2\" color=\"#555555\">"
		+ "%s</font></td></tr></table></body></html>";
	
	/**
	 * The icons to use for each log message level.
	 */
	static final Map<Integer,String> LEVEL_TO_ICON_MAP = new TreeMap<Integer,String>();
	static
	{
		LEVEL_TO_ICON_MAP.put(Level.DEBUG.toInt(),	"console-info.png");
		LEVEL_TO_ICON_MAP.put(Level.ERROR.toInt(),	"console-error.png");
		LEVEL_TO_ICON_MAP.put(Level.FATAL.toInt(),	"console-error.png");
		LEVEL_TO_ICON_MAP.put(Level.INFO.toInt(),	"console-info.png");
		LEVEL_TO_ICON_MAP.put(Level.TRACE.toInt(),	"console-info.png");
		LEVEL_TO_ICON_MAP.put(Level.WARN.toInt(),	"console-warning.png");
	}

	/**
	 * Retrieves the icon from LEVEL_TO_ICON_MAP, defaulting to "console-info" if
	 * level is not found.
	 */
        static String getIcon(int level)
        {
		String path = LEVEL_TO_ICON_MAP.get(level);
		if (path == null)
			path = "console-info.png";
		return path;
        }

	/**
	 * The resource path to the base HTML file. When the Console is empty,
	 * it displays the base HTML file.
	 */
	final static String BASE_HTML_PATH = "/consoledialogbase.html";

	/**
	 * A reference to the status bar is needed because
	 * when the user clicks the Clear button,
	 * this will also clear the status bar.
	 */
	CytoStatusBar statusBar;
	JEditorPane editorPane;
	HTMLDocument document;
	Element root;
	boolean colorParity = true;
	JScrollPane scrollPane;

	public ConsoleDialog(CytoStatusBar statusBar, CySwingApplication app)
	{
		super(app.getJFrame(), "Console", false);
		this.statusBar = statusBar;

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
			statusBar.setMessage(null, null);
		}
	}


	void append(Level level, String message, String timeStamp)
	{
		String icon = getIcon(level.toInt());
		String bgColor = (colorParity ? COLOR_PARITY_TRUE : COLOR_PARITY_FALSE);
		try
		{
			document.insertBeforeEnd(root,
				String.format(ENTRY_TEMPLATE,
						bgColor, icon,
						message, timeStamp));
			scrollToBottom();
		}
		catch (BadLocationException e) {}
		catch (IOException e) {}
		colorParity = !colorParity;
	}

	void scrollToBottom()
	{
		// If we scroll the bottom immediately after
		// we call document.insertBeforeEnd(), the scroll bar won't go to
		// end because the scroll bar by then does not recognize the latest
		// update to document. If we wrap the scrolling code in an
		// invokeLater() call, this will ensure the scroll bar will move
		// to the bottom.
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
