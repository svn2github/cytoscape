/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: alokito $
 * $RCSfile: HeaderFinder.java,v $
 * $Revision: 1.4 $
 * $Date: 2006/09/21 18:05:43 $
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

// for summary view...
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.stanford.genetics.treeview.plugin.dendroview.DendroView;
/**
 *  The purpose of this class is to allow searching on HeaderInfo objects.
 * The display of the headers and the matching is handled by this class,
 * wheras the actual manipulation of the selection objects and the 
 * associated views is handled by the relevant subclass.
 * 
 * @author aloksaldanha
 *
 */
public abstract class HeaderFinder extends JDialog {
	//"Search Gene Text for Substring"
  protected HeaderFinder(ViewFrame f, HeaderInfo hI, TreeSelectionI geneSelection, String title) {
	this((Frame) f, hI, geneSelection, title);
	this.viewFrame = f;
  }
  private HeaderFinder(Frame f, HeaderInfo hI, TreeSelectionI geneSelection, String title) {
	super(f, title);
	this.viewFrame = null;
	this.headerInfo = hI;
	this.geneSelection = geneSelection;
	choices = new int[hI.getNumHeaders()]; // could be wasteful of ram...
	
	JPanel mainPanel = new JPanel();
	mainPanel.setLayout(new BorderLayout());
	mainPanel.add(new SearchPanel(), BorderLayout.NORTH);
	
	rpanel = new ResultsPanel();
	mainPanel.add(new JScrollPane(rpanel), BorderLayout.CENTER);
	
	mainPanel.add(new ClosePanel(), BorderLayout.SOUTH);

	mainPanel.add(new SeekPanel() , BorderLayout.EAST);
	getContentPane().add(mainPanel);
	addWindowListener(new WindowAdapter () {
		public void windowClosing(WindowEvent we) {
		    hide();
		}
	    });
	pack();
    }
    
	/**
	* selects all the genes which are currently selected in the results panel.
	*/
	private void seek() {
		int first = rpanel.getFirstSelectedIndex(); 
		// in some jdks, selected index is set to -1 between selections.
		if (first == -1) return;
		int [] selected = results.getSelectedIndices();
		if (selected.length == 0) return;

		geneSelection.deselectAllIndexes();
		for (int i = 0; i < selected.length; i++) {
			geneSelection.setIndex(choices[selected[i]], true);
		}
		geneSelection.notifyObservers();
		scrollToIndex(choices[first]);
	}
	/* 		if (viewFrame != null)
	viewFrame.scrollToGene(choices[first]);
	*/
	abstract public void scrollToIndex(int i);
    private void seekNext() {
		int currentIndex = rpanel.getFirstSelectedIndex();
		if (currentIndex == -1) return; // no current selection.
		int nextIndex = (currentIndex + 1) % resultsModel.getSize();
		rpanel.setSelectedIndex(nextIndex);
		results.ensureIndexIsVisible(nextIndex);
		seek();
    }
	public void seekAll() {
		results.setSelectionInterval(0,resultsModel.getSize()-1);
		int [] selected = results.getSelectedIndices();
		geneSelection.setSelectedNode(null);
		geneSelection.deselectAllIndexes();
		for (int i = 0; i < selected.length; i++) {
			geneSelection.setIndex(choices[selected[i]], true);
		}
		geneSelection.notifyObservers();
		results.repaint();
		if ((viewFrame != null) && (selected.length > 0))
			scrollToIndex(choices[selected[0]]);
	}
	/**
	* selects all genes which match the specified id in their id column...
	*/
	public void findGenesById(String [] subs) {
		nchoices = 0;
		resultsModel.removeAllElements();
		
		int jmax  = headerInfo.getNumHeaders();
		int idIndex = headerInfo.getIndex("YORF"); //  actually, just 0, or 1 if 0 is GID.
		for  (int j = 0; j < jmax; j++) {
			String [] headers = headerInfo.getHeader(j);
			if (headers == null) continue;
			String id = headers[idIndex];
			if (id == null) continue;
			boolean match = false;
			for (int i=0; i < subs.length; i++) {
				if (subs[i] == null) {
					System.out.println("eek! HeaderFinder substring " + i + " was null!");
				}
				if (id.indexOf(subs[i]) >= 0) {
					match = true;
					break;
				}
			}
			if (match) {
				selectGene(j);
			}
		}
		
	}
	
    private void findGenes(String sub, boolean caseSensative) {
		nchoices = 0;
		resultsModel.removeAllElements();
		
		if (caseSensative == false) sub = sub.toLowerCase();
		int jmax = headerInfo.getNumHeaders();
		for (int j = 0; j < jmax; j++) {
			String []strings = headerInfo.getHeader(j);
			if (strings == null) continue;
			boolean match = false;
			for (int i = 0; i < strings.length; i++) {
				if (strings[i] == null) continue;
				String cand;
				if (caseSensative) {
					cand = strings[i];
				} else {
					cand = strings[i].toLowerCase();
				}
				if (cand.indexOf(sub) >= 0) {
					match = true;
					break;
				}
			}
			
			if (match) {
				selectGene(j);
			}
		}
	}

	private void selectGene(int j) {
		String [] strings = headerInfo.getHeader(j);
		String id = "";
		for (int i = 1; i < strings.length; i++) {		    
			if (strings[i] != null) {
				id += strings[i] + "; ";
			}
		}
		if (strings[0] != null) {
			id += strings[0] + "; ";
		}
		resultsModel.addElement(id);
		choices[nchoices++] = j;
	}
	
	SearchTextField search_text;
	JCheckBox caseBox;
    class SearchPanel extends JPanel {	
	  public SearchPanel() {
	    JLabel instr = new JLabel ("Enter Substring:");
	    add(instr);
	    
	    search_text = new SearchTextField(10);
	    search_text.addActionListener(search_text);
	    add(search_text);
		caseBox = new JCheckBox("Case Sensitive?");
		add(caseBox);
	  }
	}
	class SearchTextField extends JTextField implements ActionListener {
	    // why does java make me write this dumb constructor?
	    SearchTextField(int cols) {super(cols);}

	    public void actionPerformed(ActionEvent e) {
		findGenes(getText(), caseBox.isSelected());
	    }
	}
    
	class ResultsPanel extends JPanel {
		
		public ResultsPanel() {
//			setLayout(new BorderLayout());
			resultsModel = new DefaultListModel();
			results = new JList(resultsModel);
			results.setVisibleRowCount(10);
			results.addListSelectionListener(new ListSeeker());
//			add(results, BorderLayout.CENTER);
			add(results);
		}
		
		class ListSeeker implements ListSelectionListener {
			public void valueChanged(ListSelectionEvent e) {
				results.repaint();
				seek();
			}
		}
		
		public int getFirstSelectedIndex() {return results.getSelectedIndex();}
		public int [] getSelectedIndices() {return results.getSelectedIndices();}
		public void setSelectedIndex(int i) {results.setSelectedIndex(i);}
	}

    class SeekPanel extends JPanel {
	public SeekPanel () {
		super();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); 
	    search_button = new JButton("Search");
	    search_button.addActionListener(search_text);
	    add(search_button);

 	    seek_button = new JButton("Seek");
	    seek_button.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent evt) {
			seek();
		    }
		});

	    // add(seek_button);
 	    seekNext_button = new JButton("Next");
	    seekNext_button.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent evt) {
			seekNext();
		    }
		});

	    add(seekNext_button);

 	    seekAll_button = new JButton("All");
	    seekAll_button.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent evt) {
			seekAll();
		    }
		});
	    add(seekAll_button);
		
		summary_button = new JButton("Summary Popup");
		summary_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				seekAll();
				viewFrame.showSubDataModel(geneSelection.getSelectedIndexes(), 
						search_text.getText() +" matches in " + viewFrame.getDataModel().getSource(),
						search_text.getText() +" matches in " + viewFrame.getDataModel().getName()
						);
			}
		});
		add(summary_button);

		add(Box.createVerticalGlue());
	}
    }

    class ClosePanel extends JPanel {
	public ClosePanel () {
 	    JButton close_button = new JButton("Close");
	    close_button.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			HeaderFinder.this.hide();
		    }
		});
	    add(close_button);
	}
    }
        

	private TreeSelectionI geneSelection;
	protected ViewFrame viewFrame;
    private JButton search_button, seek_button, seekNext_button, seekAll_button, summary_button;
    private ResultsPanel rpanel;
    private HeaderInfo headerInfo;
    private int choices[];
    private int nchoices = 0;
    private JList results;
	private DefaultListModel resultsModel;
}
