///////////////////////////////////////////////////////////////////////////////
//                   
// Main Class File:  ImplementationTaskPlugin.java
// Class File:		 CountPanel.java
//
// Author:           Layla Oesper layla.oesper@gmail.com
//
//////////////////////////// 80 columns wide //////////////////////////////////

/**
 * This class defines a new panel that can by used in Cytoscape to display
 * the count of word information.
 */

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class CountPanel extends JPanel 
{
	//Variables
	private final static String newline = "\n";
	private final static String titleName = "Word Counts";
	
	
	private JTextArea text;
	private JLabel title;

	
	/**
	 * Main Constructor
	 */
	public CountPanel()
	{
		super(new BorderLayout());
		initComponents();
	}
	
	/**
	 * Initialize all swing components
	 */
	private void initComponents()
	{
		title = new JLabel(titleName);
		this.add(title);
	
		
		text = new JTextArea(20,100);
		text.setEditable(false);
		
		JScrollPane scrollPane = new JScrollPane(text);
		setPreferredSize(new Dimension(450,110));
		add(scrollPane,BorderLayout.CENTER);
	}
	
	/**
	 * Add text to the text component
	 * @param text to add to display.
	 */
	public void addText(String aText)
	{
		text.append(aText);
		repaint();
	}
	
	/**
	 * Clears all text
	 */
	public void clearText()
	{
		text.setText("");
	}
	
}
