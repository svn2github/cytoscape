/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: rqluk $
 * $RCSfile: SummaryViewWizard.java,v $
 * $Revision: 1.1 $
 * $Date: 2006/08/16 19:13:45 $
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
package edu.stanford.genetics.treeview.plugin.dendroview;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;

/**
* this class exposes a GUI for configuring a summary view.
*/

public class SummaryViewWizard extends JPanel {
	private DendroView dendroView;
	private GeneListPanel geneListPanel;
	private JRadioButton selectionButton, listButton;
	
	public SummaryViewWizard(DendroView dendroView) {
		this.dendroView = dendroView;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		geneListPanel = new GeneListPanel();

		selectionButton = new JRadioButton();
		selectionButton.setSelected(true);
		listButton = new JRadioButton();
		ButtonGroup group = new ButtonGroup(); 
		group.add(selectionButton);
		group.add(listButton);

		JPanel selectionPanel = new JPanel();
		selectionPanel.add(selectionButton);
		selectionPanel.add(new JLabel("Selected Genes"));

		JPanel listPanel = new JPanel();
		listPanel.add(listButton);
		listPanel.add(geneListPanel);
		
		add(selectionPanel);
		add(listPanel);
	}
	
	public int [] getIndexes() {
		if (listButton.isSelected()) {
			return geneListPanel.getIndexes();
		}
		return  dendroView.getGeneSelection().getSelectedIndexes();
	}
	class GeneListPanel extends JPanel {
		JTextArea textArea;
//	JTextField textArea;
		public GeneListPanel() {
			textArea  = new JTextArea("Paste one ID per row", 10, 50);
			textArea.append("\nNote: use Ctrl-V on mac (Java is cross-platform!?)");
//			textArea  = new JTextField("Paste one ID per row");
			textArea.setEditable(true);
			textArea.getDocument().addDocumentListener(new DocumentListener() {
				public void changedUpdate (DocumentEvent e) {
					listButton.setSelected(true);
				}
				public void insertUpdate (DocumentEvent e) {
					listButton.setSelected(true);
				}
				public void removeUpdate (DocumentEvent e) {
					listButton.setSelected(true);
				}
			});


			add(new JScrollPane(textArea));
		}
		public int [] getIndexes() {
			LineReader lineReader = new LineReader();
			String next = lineReader.readLine();
			int nLines = 0;
			while (next != null) {
				if (next.length() > 0) {
					nLines++;
				}
				next = lineReader.readLine();
			}
			String [] subStrings = new String[nLines];

			lineReader = new LineReader();
			next = lineReader.readLine();
			nLines = 0;
			while (next != null) {
				if (next.length() > 0) {
					subStrings[nLines++] = next;
				}
				next = lineReader.readLine();
			}
			
			dendroView.getViewFrame().getGeneFinder().findGenesById(subStrings);
			dendroView.getViewFrame().getGeneFinder().seekAll();
			return dendroView.getGeneSelection().getSelectedIndexes();
		}
		class LineReader {
			char[] lineTerminator = System.getProperty ("line.separator").toCharArray (); 
			int documentPosition = 0;
			Segment seg = new Segment();
			public String readLine () {
				StringBuffer buf = new StringBuffer ();
				char[] save = new char[lineTerminator.length]; int pos = 0; 
				Document doc = textArea.getDocument();
				try {
					doc.getText(documentPosition++, 1, seg);
				} catch (BadLocationException e) {
					return null;
				}
				int ch = seg.first();
				boolean done = false; 
				do {
					if (ch == lineTerminator[pos]) {
						save[pos] = (char) ch;
						pos++;
					} else {
						// if a char in the line terminator is returned 
						//   but one was skipped, then skip it by moving pos 
						//   up by two 
						if (pos + 1 < lineTerminator.length && ch == lineTerminator[pos + 1]) {
							pos += 2; 
						} else {
							if (pos > 0) {
								buf.append (save, 0, pos);
								pos = 0;
							}
							buf.append ((char) ch);
						}
					}
					done = pos >= lineTerminator.length; 
					try {
						doc.getText(documentPosition++, 1, seg);
					} catch (BadLocationException e) {
						done = true;
					}
					if (!done) ch = seg.first(); 
				} while (!done); 
				String tempString = new String (buf);
				return (tempString.trim());
			}
		}
	}
}
