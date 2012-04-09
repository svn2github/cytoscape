package org.cytoscape.app.internal.swing.main;

import java.io.File;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

public class InstallNewAppsPanel extends javax.swing.JPanel {

    private javax.swing.JButton installFromFileButton;
    private javax.swing.JButton installFromURLButton;
    private javax.swing.JButton installSelectedButton;
    private javax.swing.JLabel resultsLabel;
    private javax.swing.JScrollPane resultsScrollPane;
    private javax.swing.JTable resultsTable;
    private javax.swing.JButton searchButton;
    private javax.swing.JComboBox searchComboBox;
    private javax.swing.JLabel searchLabel;
    private javax.swing.JSeparator searchSeparator;
	
	private JFileChooser fileChooser;
	
    public InstallNewAppsPanel() {
        initComponents();
        
        setupFileChooser();
        setupResultsTable();
    }

    private void initComponents() {
        installFromFileButton = new javax.swing.JButton();
        installFromURLButton = new javax.swing.JButton();
        searchSeparator = new javax.swing.JSeparator();
        searchLabel = new javax.swing.JLabel();
        searchComboBox = new javax.swing.JComboBox();
        searchButton = new javax.swing.JButton();
        resultsLabel = new javax.swing.JLabel();
        resultsScrollPane = new javax.swing.JScrollPane();
        resultsTable = new javax.swing.JTable();
        installSelectedButton = new javax.swing.JButton();

        installFromFileButton.setText("Install from External File ..");
        installFromFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                installFromFileButtonActionPerformed(evt);
            }
        });

        installFromURLButton.setText("Install from URL ..");
        installFromURLButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                installFromURLButtonActionPerformed(evt);
            }
        });

        searchLabel.setText("Search for Apps from the Web Store:");

        searchComboBox.setEditable(true);
        searchComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        searchButton.setText("Search");
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });

        resultsLabel.setText("Search Results:");
        
        TableModel tableModel = new TableModel() {

        	private ArrayList<ArrayList<String>> data;
        	
			@Override
			public void addTableModelListener(TableModelListener l) {
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return String.class;
			}

			@Override
			public int getColumnCount() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public String getColumnName(int columnIndex) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int getRowCount() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void removeTableModelListener(TableModelListener l) {
			}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
				// TODO Auto-generated method stub
				
			}
			
		};
        
        resultsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {},
            new String [] {
                "Name", "Version", "Author", "Description"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        
        resultsScrollPane.setViewportView(resultsTable);

        installSelectedButton.setText("Install Selected Apps");
        installSelectedButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                installSelectedButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(searchSeparator)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, resultsScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 558, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(installFromFileButton)
                            .add(installSelectedButton)
                            .add(resultsLabel)
                            .add(installFromURLButton)
                            .add(searchLabel)
                            .add(layout.createSequentialGroup()
                                .add(searchComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 288, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(searchButton)))
                        .add(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(installFromFileButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(installFromURLButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(searchSeparator, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(searchLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(searchComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(searchButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(resultsLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(resultsScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(installSelectedButton)
                .addContainerGap())
        );
    }

    private void installFromFileButtonActionPerformed(java.awt.event.ActionEvent evt) {
        int returnValue = fileChooser.showOpenDialog(this);
        
        if (returnValue == JFileChooser.APPROVE_OPTION) {
        	File[] files = fileChooser.getSelectedFiles();
        	
        	System.out.println("Selected files: " + files);
        }
    }

    private void installFromURLButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void installSelectedButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void setupFileChooser() {
    	fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select App Files");
        fileChooser.setApproveButtonText("Install");
        fileChooser.setApproveButtonMnemonic('I');
        fileChooser.setMultiSelectionEnabled(true);
        
        fileChooser.addChoosableFileFilter(new FileFilter(){

			@Override
			public boolean accept(File file) {
				if (file.getName().endsWith("jar") || file.isDirectory()) {
					return true;
				} else {
					return false;
				}
			}

			@Override
			public String getDescription() {
				return "Jar-packaged App Files (*.jar)";
			}
        	
        });
	}
    
    private void setupResultsTable() {
    	// resultsTable.setCellSelectionEnabled(false);

    	// resultsTable.setDefaultEditor(null, null);
    	resultsTable.setOpaque(true);
    }
}
