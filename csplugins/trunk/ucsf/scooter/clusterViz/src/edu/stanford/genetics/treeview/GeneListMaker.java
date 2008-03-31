/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: alokito $
 * $RCSfile: GeneListMaker.java,v $
 * $Revision: 1.14 $
 * $Date: 2006/03/20 06:17:44 $
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


import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

/** This class is designed to save lists of genes to a file.  
 *
 * The class will pop up a window and prompt the user for further
 * interaction before killing itself like a good slave.
 */

public class GeneListMaker extends JDialog implements ConfigNodePersistent {
	/**
	 * @author aloksaldanha
	 *
	 * Table model to support preview of data. Probably should base export off of it for simplicity.
	 */
	private class GeneListTableModel extends AbstractTableModel {
		/**
		 * 
		 * @return call to indicate table structure changed.
		 */
		public void dataChanged() {
			fireTableStructureChanged();
		}
		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getRowCount()
		 */
		public int getRowCount() {
			if (fieldRow.includeHeader()) {
				return geneSelection.getNSelectedIndexes() + 1;
			} else {
				return geneSelection.getNSelectedIndexes();
			}
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getColumnCount()
		 */
		public int getColumnCount() {
			int [] selectedPrefix = fieldRow.getSelectedPrefix();
			if (fieldRow.includeExpr()) {
				return nArray + selectedPrefix.length;
			} else {
				return selectedPrefix.length;
			}
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getValueAt(int, int)
		 */
		public Object getValueAt(int rowIndex, int columnIndex) {
			int [] selectedPrefix = fieldRow.getSelectedPrefix();
			if (fieldRow.includeHeader()) {
				if (rowIndex == 0) {
					String [] pNames = headerInfo.getNames();
					if (columnIndex < selectedPrefix.length) {
						// gene annotation column headers
						return pNames[selectedPrefix[columnIndex]];
					} else if (fieldRow.includeExpr()) {
						// array headers
	    					int gidRow = aHeaderInfo.getIndex("GID");
	    					if (gidRow == -1) gidRow = 0;
	    					String [] headers = aHeaderInfo.getHeader(columnIndex-selectedPrefix.length);
	    					return headers[gidRow];
					}
				} else if (rowIndex == 1 && eRow != -1) {
					//eweight
					if ((selectedPrefix.length > 0 )&&(columnIndex == 0)) {
						return "EWEIGHT";
					} else if (columnIndex < selectedPrefix.length) {
						return "";
					} else {
						String [] headers = aHeaderInfo.getHeader(columnIndex-selectedPrefix.length);
    						return headers[eRow];
    					}
				} else {
					rowIndex--;
				}
			}
			if (columnIndex < selectedPrefix.length) {
				String [] headers = headerInfo.getHeader(rowIndex + top);
				return headers[selectedPrefix[columnIndex]];
			} else {
				double val = dataMatrix.getValue(columnIndex - selectedPrefix.length,  
						rowIndex +top);
				if (val == DataModel.NODATA) {
					return null;
				}
				if (val == DataModel.EMPTY) {
					return null;
				}
				return new Double(val);
			}
			/*
			for (int i = top; i <= bot; i++) {
    			if (geneSelection.isIndexSelected(i) == false) continue;
    			String [] headers = headerInfo.getHeader(i);
    			output.print(headers[selectedPrefix[0]]);
    			for (int j = 1; j < selectedPrefix.length; j++) {
    				output.print("\t");
    				output.print(headers[selectedPrefix[j]]);
    			}
    			if (fieldRow.includeExpr()) {
    				for (int j = 0; j < nArray; j++) {
    					output.print("\t");
    					double val = dataMatrix.getValue(j,  i);
    					if (val != noData)
    						output.print(val);
    				}
    			}
    			output.print("\n");
    			*/
		}

	}
    private ConfigNode root = null;
    private GeneListTableModel tableModel;
    final private Notifier notifier = new Notifier();
    private class Notifier implements ActionListener, ListSelectionListener {
		public void actionPerformed(ActionEvent e) {
			if (tableModel != null)
				tableModel.dataChanged();
		}

		public void valueChanged(ListSelectionEvent e) {
			if (tableModel != null)
				tableModel.dataChanged();
		}
    };
    
    public void bindConfig(ConfigNode configNode)
    {
        root = configNode;
    }
    public ConfigNode createSubNode()
    {
        return root.create("File");
    }
	public String getFile() {
	  if (root == null) {
		return defaultFile;
	  } else {
		return root.getAttribute("file", defaultFile);
	  }
	}
    FileRow fileRow = null;
    public void setFile(String newdir) {
	root.setAttribute("file", newdir, "                    ");
	if (fileRow != null) {
	    fileRow.setFile(newdir);
	}
    }
    private  TreeSelectionI geneSelection;
    private HeaderInfo headerInfo, aHeaderInfo;
	private int nArray = 0;
	private DataMatrix dataMatrix = null;
	private double noData;
	private String defaultFile;
    public GeneListMaker(JFrame f, TreeSelectionI n, HeaderInfo hI, String dd) {
		super(f, "Gene Text Export", true);
		geneSelection = n;
		headerInfo = hI;
		defaultFile = dd;
		
		
			top = geneSelection.getMinIndex();
			bot = geneSelection.getMaxIndex();
			if (top > bot) {
				int swap = top;
				top = bot;
				bot = swap;
			}
			String [] first = headerInfo.getHeader(top);
			String [] last = headerInfo.getHeader(bot);
			int yorf = headerInfo.getIndex("YORF");
			fieldRow = new FieldRow();
			fieldRow.setSelectedIndex(yorf);
			fileRow = new FileRow();
			JPanel center = new JPanel();
			center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
			center.add(new JLabel("Genes from " + first[yorf] + " to " + last[yorf] + " selected"));
			tableModel = new GeneListTableModel();
			JTable jTable =new JTable(tableModel);
			jTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			center.add(new JScrollPane(jTable));
			center.add(fieldRow);
			center.add(fileRow);
			
			
			getContentPane().setLayout(new BorderLayout());
			getContentPane().add(center, BorderLayout.CENTER);
			
			JPanel bottom = new JPanel();
			JButton saveButton = new JButton("Save");
			saveButton.addActionListener(new ActionListener () {
				public void actionPerformed(ActionEvent e) {
					GeneListMaker.this.saveList();
				}
			});
			bottom.add(saveButton);
			
			JButton cancelButton = new JButton("Cancel");
			cancelButton.addActionListener(new ActionListener () {
				public void actionPerformed(ActionEvent e) {
					GeneListMaker.this.dispose();
				}
			});
			bottom.add(cancelButton);
			getContentPane().add(bottom, BorderLayout.SOUTH);

    }
	public void setDataMatrix(DataMatrix data, HeaderInfo ahi, double noData) {
	  this.dataMatrix = data;
	  this.nArray = dataMatrix.getNumCol();
	  this.aHeaderInfo = ahi;
	  this.eRow = aHeaderInfo.getIndex("EWEIGHT");
	  this.noData = noData;
	}

    FieldRow fieldRow;
    int top, bot, eRow;
    private void saveList() {
    	try {
    		int [] selectedPrefix = fieldRow.getSelectedPrefix();
    		if (selectedPrefix.length == 0) return;
    		setFile(fileRow.getFile());
    		PrintStream output = new PrintStream(new BufferedOutputStream
    				(new FileOutputStream(new File(fileRow.getFile()))));
    		
    		if (fieldRow.includeHeader()) {
    			// gid row...
    			String [] pNames = headerInfo.getNames();
    			output.print(pNames[selectedPrefix[0]]);
    			for (int j = 1; j < selectedPrefix.length; j++) {
    				output.print("\t");
    				output.print(pNames[selectedPrefix[j]]);
    			}
    			if (fieldRow.includeExpr()) {
    				int gidRow = aHeaderInfo.getIndex("GID");
    				if (gidRow == -1) gidRow = 0;
    				for (int j = 0; j < nArray; j++) {
    					output.print("\t");
    					try {
    						String [] headers = aHeaderInfo.getHeader(j);
    						String out = headers[gidRow];
    						output.print(out);
    					} catch(java.lang.ArrayIndexOutOfBoundsException e) {
    					}
    				}
    				output.print("\n");		  
    				//EWEIGHT row
    				output.print("EWEIGHT");
    				for (int j = 1; j < selectedPrefix.length; j++) {
    					output.print("\t");
    				}			
    				int eRow = aHeaderInfo.getIndex("EWEIGHT");
    				for (int j = 0; j < nArray; j++) {
    					output.print("\t");
    					try {
    						String [] headers = aHeaderInfo.getHeader(j);
    						String out = headers[eRow];
    						output.print(out);
    					} catch(java.lang.ArrayIndexOutOfBoundsException e) {
    						output.print("1");
    					}
    				}
    			}
    			output.print("\n");		  
    		}
    		for (int i = top; i <= bot; i++) {
    			if (geneSelection.isIndexSelected(i) == false) continue;
    			String [] headers = headerInfo.getHeader(i);
    			output.print(headers[selectedPrefix[0]]);
    			for (int j = 1; j < selectedPrefix.length; j++) {
    				output.print("\t");
    				output.print(headers[selectedPrefix[j]]);
    			}
    			if (fieldRow.includeExpr()) {
    				for (int j = 0; j < nArray; j++) {
    					output.print("\t");
    					double val = dataMatrix.getValue(j,  i);
    					if (val != noData)
    						output.print(val);
    				}
    			}
    			output.print("\n");
    		}
    		output.close();
    		dispose();
    	} catch (Exception e) {
    		e.printStackTrace();
    		LogBuffer.println("In GeneListMaker.saveList(), got exception " + e);
    	}

    }
	public void includeAll() {
		fieldRow.includeAll();
		tableModel.dataChanged();
	}
	class FieldRow extends JPanel {
		JList list;
		JCheckBox exprBox, headerBox;
		
		public void includeAll() {
			list.setSelectionInterval(0, (headerInfo.getNames()).length-1);
			exprBox.setSelected(true);
			headerBox.setSelected(true);
			
		}
		public int [] getSelectedPrefix() {
			return list.getSelectedIndices();
		}
		public void setSelectedIndex(int i) {
			list.setSelectedIndex(i);
		}
		public boolean includeExpr() {
			return exprBox.isSelected();
		}
		public boolean includeHeader() {
			return headerBox.isSelected();
		}
		public FieldRow() {
			super();
			add(new JLabel("Field(s) to print: "));
			list = new JList(headerInfo.getNames());
			list.addListSelectionListener(notifier);
			add(list);
			exprBox = new JCheckBox("Expression Data?");
			exprBox.addActionListener(notifier);
			add(exprBox);
			headerBox = new JCheckBox("Header Line?");
			headerBox.addActionListener(notifier);
			add(headerBox);
		}
	}
	class FileRow extends JPanel {
		JTextField file;
		public void setFile(String newfile) {
			file.setText(newfile);
		}
		public String  getFile() {
			return file.getText();
		}
		public FileRow() {
			super();
			add(new JLabel("Export To: "));
			file = new JTextField(GeneListMaker.this.getFile());
			add(file);
			JButton chooseButton = new JButton("Browse");
			chooseButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						JFileChooser chooser = new JFileChooser();
						chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
						int returnVal = chooser.showOpenDialog(GeneListMaker.this);
						if(returnVal == JFileChooser.APPROVE_OPTION) {
							if (chooser.getSelectedFile().isDirectory()) {
								File currentF = new File(getFile());
								GeneListMaker.this.setFile(chooser.getSelectedFile().getCanonicalPath() +
										File.separator+
										currentF.getName());
							} else {
								GeneListMaker.this.setFile(chooser.getSelectedFile().getCanonicalPath());
							}
						}
					} catch (java.io.IOException ex) {
						System.out.println("Got exception " + ex);
					}
				}
			});
			add(chooseButton);
		}
	}

}
