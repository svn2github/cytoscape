/*
 Copyright (c) 2009, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */
package cytoscape.dialogs.internal;

import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ToolTipManager;

import org.cytoscape.property.bookmark.Bookmarks;
import org.cytoscape.property.bookmark.BookmarksUtil;
import org.cytoscape.property.bookmark.Category;
import org.cytoscape.property.bookmark.DataSource;

import cytoscape.dialogs.ImportNetworkDialog;
import cytoscape.util.FileUtil;

/**
 * @author kono
 */
public class ImportNetworkDialogImpl extends JDialog implements
		ImportNetworkDialog {
	private final static long serialVersionUID = 120233987313024L;
	private boolean status;
	private File[] networkFiles;

	// Will be injected through DI container
	private Bookmarks bookmarks;
	private BookmarksUtil bkUtil;

	private String bookmarkCategory = "network";
	private String URLstr;
	private BookmarkComboBoxEditor bookmarkEditor = new BookmarkComboBoxEditor();
	private String pleaseMessage = "Please provide URL or select from list";

	private static final String URL_TOOLTIP = "<html>Enter URL or <strong><font color=\"red\">Drag and Drop local/remote files.</font></strong></html>";
	private static final String LOCAL_TOOLTIP = "<html>Specify path to local files.</html>";


	private FileUtil fileUtil;
	private Component desk;

	/**
	 * Creates new form NetworkImportDialog
	 */
	public ImportNetworkDialogImpl(Frame parent, boolean modal,
			FileUtil fileUtil, Bookmarks bookmarks, BookmarksUtil bkUtil) {
		super(parent, modal);
		desk = parent;
		this.bookmarks = bookmarks;
		this.bkUtil = bkUtil;
		this.fileUtil = fileUtil;

		setTitle("Import Network");
		initComponents();
		addListeners();

		// By default, import from local
		switchImportView("Local");

		status = false;
		networkFiles = null;

		// if bookmarkCategory "network" does not exist, create a "network" with
		// empty DataSource
		Category theCategory = bkUtil.getCategory(bookmarkCategory,
				bookmarks.getCategory());

		if (theCategory == null) {
			theCategory = new Category();
			theCategory.setName(bookmarkCategory);

			List<Category> theCategoryList = bookmarks.getCategory();
			theCategoryList.add(theCategory);
		}

	}

	/**
	 * Get first file only.
	 * 
	 * @return
	 */
	public File getFile() {
		if ((networkFiles != null) && (networkFiles.length > 0)) {
			return networkFiles[0];
		} else {
			return null;
		}
	}

	/**
	 * Get all files selected.
	 * 
	 * @return
	 */
	public File[] getFiles() {
		return networkFiles;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public boolean getStatus() {
		return status;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public boolean isRemote() {
		return remoteRadioButton.isSelected();
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public String getURLStr() {
		return URLstr;
	}

	public void showDialog() {
		pack();
		setLocationRelativeTo(desk);
		setVisible(true);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */

	// <editor-fold defaultstate="collapsed" desc=" Generated Code ">
	private void initComponents() {

		buttonGroup1 = new javax.swing.ButtonGroup();
		titleLabel = new javax.swing.JLabel();
		localRadioButton = new javax.swing.JRadioButton();
		remoteRadioButton = new javax.swing.JRadioButton();
		networkFileTextField = new javax.swing.JTextField();
		networkFileComboBox = new javax.swing.JComboBox();
		selectButton = new javax.swing.JButton();
		importButton = new javax.swing.JButton();
		cancelButton = new javax.swing.JButton();
		titleSeparator = new javax.swing.JSeparator();
		radioButtonPanel = new javax.swing.JPanel();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		titleLabel.setFont(new java.awt.Font("Sans-Serif", Font.BOLD, 14));
		titleLabel.setText("Import Network File");

		radioButtonPanel.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Data Source Type"));
		buttonGroup1.add(localRadioButton);
		localRadioButton.setSelected(true);
		localRadioButton.setText("Local");
		localRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(
				0, 0, 0, 0));
		localRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
		localRadioButton.setToolTipText(LOCAL_TOOLTIP);

		buttonGroup1.add(remoteRadioButton);
		remoteRadioButton.setText("Remote/URL");
		remoteRadioButton.setBorder(javax.swing.BorderFactory
				.createEmptyBorder(0, 0, 0, 0));
		remoteRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
		remoteRadioButton.setToolTipText(URL_TOOLTIP);

		networkFileTextField.setText("Please select a network file...");
		networkFileTextField.setName("networkFileTextField");
		networkFileTextField.addFocusListener(this);

		selectButton.setText("Select");

		importButton.setText("Import");
		importButton.setName("btnImport");
		importButton.setEnabled(false);

		cancelButton.setText("Cancel");
		cancelButton.setName("btnCancel");

		networkFileComboBox.setRenderer(new MyCellRenderer());
		networkFileComboBox.setEditor(bookmarkEditor);
		networkFileComboBox.setEditable(true);
		networkFileComboBox.setName("networkFileComboBox");
		networkFileComboBox
				.setToolTipText("<html><body>You can specify URL by the following:<ul><li>Type URL</li><li>Select from pull down menu</li><li>Drag & Drop URL from Web Browser</li></ul></body><html>");
		final ToolTipManager tp = ToolTipManager.sharedInstance();
		tp.setInitialDelay(1);
		tp.setDismissDelay(7500);

		org.jdesktop.layout.GroupLayout radioButtonPanelLayout = new org.jdesktop.layout.GroupLayout(
				radioButtonPanel);
		radioButtonPanel.setLayout(radioButtonPanelLayout);
		radioButtonPanelLayout
				.setHorizontalGroup(radioButtonPanelLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								radioButtonPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.add(localRadioButton)
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(remoteRadioButton)
										.addContainerGap(250, Short.MAX_VALUE)));
		radioButtonPanelLayout
				.setVerticalGroup(radioButtonPanelLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								radioButtonPanelLayout
										.createSequentialGroup()
										.add(
												radioButtonPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)
														.add(localRadioButton)
														.add(remoteRadioButton))));

		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout
				.setHorizontalGroup(layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								layout
										.createSequentialGroup()
										.addContainerGap()
										.add(
												layout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.LEADING)
														.add(
																networkFileComboBox,
																0, 350,
																Short.MAX_VALUE)
														.add(
																titleLabel,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																350,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
														.add(
																titleSeparator,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																350,
																Short.MAX_VALUE)
														.add(
																radioButtonPanel,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.add(
																layout
																		.createSequentialGroup()
																		.add(
																				networkFileTextField,
																				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																				350,
																				Short.MAX_VALUE)
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED)
																		.add(
																				selectButton))
														.add(
																org.jdesktop.layout.GroupLayout.TRAILING,
																layout
																		.createSequentialGroup()
																		.add(
																				importButton)
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED)
																		.add(
																				cancelButton)))
										.addContainerGap()));
		layout
				.setVerticalGroup(layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								layout
										.createSequentialGroup()
										.addContainerGap()
										.add(titleLabel)
										.add(8, 8, 8)
										.add(
												titleSeparator,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
										.add(7, 7, 7)
										.add(
												radioButtonPanel,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(
												layout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)
														.add(selectButton)
														.add(
																networkFileTextField))
										.add(
												networkFileComboBox,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED,
												3, Short.MAX_VALUE)
										.add(
												layout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)
														.add(cancelButton).add(
																importButton))
										.addContainerGap()));
		pack();
	} // </editor-fold>

	private void addListeners() {
		LocalRemoteListener l = new LocalRemoteListener();
		localRadioButton.addActionListener(l);
		remoteRadioButton.addActionListener(l);

		// ButtonActionListener btnActionListener = new ButtonActionListener();
		selectButton.addActionListener(this);
		cancelButton.addActionListener(this);
		importButton.addActionListener(this);

		bookmarkEditor.addActionListener(this);
	}

	private void switchImportView(String pLocation) {
		if (pLocation.equalsIgnoreCase("Local")) {
			// for the case of local import
			networkFileComboBox.setVisible(false);

			networkFileTextField.setVisible(true);
			selectButton.setVisible(true);
		} else { // Remote
			networkFileComboBox.setVisible(true);

			networkFileTextField.setVisible(false);
			selectButton.setVisible(false);

			loadBookmarkCMBox();
		}
	}

	private void loadBookmarkCMBox() {
		networkFileComboBox.removeAllItems();

		DefaultComboBoxModel theModel = new DefaultComboBoxModel();

		DataSource firstDataSource = new DataSource();
		firstDataSource.setName("");
		firstDataSource.setHref(null);

		theModel.addElement(firstDataSource);

		// Extract the URL entries
		List<DataSource> theDataSourceList = bkUtil.getDataSourceList(
				bookmarkCategory, bookmarks.getCategory());

		if (theDataSourceList != null) {
			for (int i = 0; i < theDataSourceList.size(); i++) {
				theModel.addElement(theDataSourceList.get(i));
			}
		}

		networkFileComboBox.setModel(theModel);
	}

	private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
		status = false;
		this.dispose();
	}

	private void importButtonActionPerformed(java.awt.event.ActionEvent evt) {
		// See if the user just typed in the desired file
		if (!isRemote() && networkFiles == null
				&& networkFileTextField.getText() != null) {
			networkFiles = new File[1];
			networkFiles[0] = new File(networkFileTextField.getText());
		}

		status = true;
		this.dispose();
	}

	private void selectNetworkFileButtonActionPerformed(
			java.awt.event.ActionEvent evt) {

		networkFiles = fileUtil.getFiles(this, "Import Network Files",
				FileUtil.LOAD);

		if (networkFiles != null) {
			/*
			 * Accept multiple files
			 */
			StringBuffer fileNameSB = new StringBuffer();
			StringBuffer tooltip = new StringBuffer();
			tooltip
					.append("<html><body><strong><font color=RED>The following files will be loaded:</font></strong><br>");

			for (int i = 0; i < networkFiles.length; i++) {
				fileNameSB.append(networkFiles[i].getAbsolutePath() + ", ");
				tooltip.append("<p>" + networkFiles[i].getAbsolutePath()
						+ "</p>");
			}

			tooltip.append("</body></html>");
			networkFileTextField.setText(fileNameSB.toString());
			networkFileTextField.setToolTipText(tooltip.toString());

			importButton.setEnabled(true);
		}
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param e
	 *            DOCUMENT ME!
	 */
	public void actionPerformed(java.awt.event.ActionEvent e) {
		Object _actionObject = e.getSource();

		// handle Button events
		if (_actionObject instanceof JButton) {
			JButton _btn = (JButton) _actionObject;

			// process radio button events
			if (_btn == selectButton) {
				selectNetworkFileButtonActionPerformed(e);
			} else if (_btn == importButton) {
				if (localRadioButton.isSelected()) // local import
				{
					importButtonActionPerformed(e);
				} else // case for remote import
				{
					URLstr = bookmarkEditor.getURLstr().trim();
					importButtonActionPerformed(e);
				}
			} else if (_btn == cancelButton) {
				cancelButtonActionPerformed(e);
			}
		}

		if (_actionObject instanceof JTextField) {
			URLstr = bookmarkEditor.getURLstr().trim();
			importButtonActionPerformed(e);
		}
	}

	/**
	 * Listen for focus events in the text field. Mostly, this is used to clear
	 * the text field for alternative input, but we only want to do this when
	 * the content of the text field is our instruction text
	 * 
	 * @param e
	 *            the FocusEvent
	 */
	public void focusGained(FocusEvent e) {
		// Get get the text from the text field
		String text = networkFileTextField.getText();
		// If it's our initial text, erase it.
		if (text.equals("Please select a network file...")) {
			networkFileTextField.setText("");
			// We assume that the user is going to type in a filename. If so,
			// we want to set the import button to be the default
			getRootPane().setDefaultButton(importButton);
			importButton.setEnabled(true);
		}
	}

	/**
	 * Listen for focus lost events in the text field. These are ignored.
	 * 
	 * @param e
	 *            the FocusEvent
	 */
	public void focusLost(FocusEvent e) {
	};

	class LocalRemoteListener implements java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			Object _actionObject = e.getSource();

			// handle radioButton events
			if (_actionObject instanceof JRadioButton) {
				JRadioButton _rbt = (JRadioButton) _actionObject;

				// process radio button events
				if (_rbt == localRadioButton) {
					switchImportView("Local");
				} else { // from rbtRemote
					switchImportView("Remote");
					importButton.setEnabled(true);
				}

				pack();
			}
		} // actionPerformed()
	}

	class MyCellRenderer extends JLabel implements ListCellRenderer {
		private final static long serialVersionUID = 1202339872997986L;

		public MyCellRenderer() {
			setOpaque(true);
		}

		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			DataSource dataSource = (DataSource) value;
			setText(dataSource.getName());

			if (isSelected) {
				if (0 < index) {
					list.setToolTipText(dataSource.getHref());
				}
			}

			return this;
		}
	} // MyCellRenderer

	class BookmarkComboBoxEditor implements javax.swing.ComboBoxEditor {
		DataSource theDataSource = new DataSource();
		JTextField tfInput = new JTextField(pleaseMessage);

		public String getURLstr() {
			return tfInput.getText();
		}

		public void addActionListener(ActionListener l) {
			tfInput.addActionListener(l);
		}

		public void addKeyListener(KeyListener l) {
			tfInput.addKeyListener(l);
		}

		public Component getEditorComponent() {
			return tfInput;
		}

		public Object getItem() {
			return theDataSource;
		}

		public void removeActionListener(ActionListener l) {
		}

		public void selectAll() {
		}

		public void setItem(Object anObject) {
			if (anObject == null) {
				return;
			}

			if (anObject instanceof DataSource) {
				theDataSource = (DataSource) anObject;
				tfInput.setText(theDataSource.getHref());
			}
		}
	} // BookmarkComboBoxEditor

	// Variables declaration - do not modify
	private javax.swing.JButton cancelButton;
	private javax.swing.JButton importButton;
	private JPanel radioButtonPanel;
	private javax.swing.JButton selectButton;
	private javax.swing.ButtonGroup buttonGroup1;
	private javax.swing.JComboBox networkFileComboBox;
	private javax.swing.JLabel titleLabel;
	private javax.swing.JRadioButton remoteRadioButton;
	private javax.swing.JRadioButton localRadioButton;
	private javax.swing.JTextField networkFileTextField;
	private javax.swing.JSeparator titleSeparator;

	// End of variables declaration
}
