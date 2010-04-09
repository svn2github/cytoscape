package cytoscape.dialogs.preferences;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import cytoscape.bookmarks.Bookmarks;
import cytoscape.bookmarks.DataSource;
import cytoscape.util.BookmarksUtil;

public class EditBookmarkDialog extends JDialog implements ActionListener {
	//private String name_orig = null;
	private String name;
	private String URLstr;
	private JDialog parent;
	private Bookmarks theBookmarks;
	private String categoryName;
	private URL bookmarkURL;
	private String mode = "new"; // new/edit
	private DataSource dataSource = null;
	
	/** Creates new form NewBookmarkDialog */
	public EditBookmarkDialog(JDialog parent, boolean modal, Bookmarks pBookmarks,
	                          String categoryName, String pMode, DataSource pDataSource) {
		super(parent, modal);
		this.parent = parent;
		this.theBookmarks = pBookmarks;
		this.categoryName = categoryName;
		this.mode = pMode;
		this.dataSource = pDataSource;
		
		initComponents();

		lbCategoryValue.setText(categoryName);

		if (pMode.equalsIgnoreCase("new")) {
			this.setTitle("Add new bookmark");
		}

		if (pMode.equalsIgnoreCase("edit")) {
			this.setTitle("Edit bookmark");
			tfName.setText(dataSource.getName());
			tfURL.setText(dataSource.getHref());
		}
	}

	public DataSource getDataSource(){
		return dataSource;
	}
	
	public void actionPerformed(ActionEvent e) {
		Object _actionObject = e.getSource();

		// handle Button events
		if (_actionObject instanceof JButton) {
			JButton _btn = (JButton) _actionObject;

			if ((_btn == btnOK) && (mode.equalsIgnoreCase("new"))) {
				name = tfName.getText();
				URLstr = tfURL.getText();

				if (name.trim().equals("") || URLstr.trim().equals("")) {
					String msg = "Please provide a name/URL!";
					// display info dialog
					JOptionPane.showMessageDialog(parent, msg, "Warning",
					                              JOptionPane.INFORMATION_MESSAGE);

					return;
				}

				DataSource theDataSource = new DataSource();
				theDataSource.setName(name);
				theDataSource.setHref(URLstr);

				if (BookmarksUtil.isInBookmarks(bookmarkURL, categoryName, theDataSource)) {
					String msg = "Bookmark already existed!";
					// display info dialog
					JOptionPane.showMessageDialog(parent, msg, "Warning",
					                              JOptionPane.INFORMATION_MESSAGE);

					return;
				}

				BookmarksUtil.saveBookmark(theBookmarks, categoryName, theDataSource);
				this.dispose();
				dataSource = theDataSource;
			}

			if ((_btn == btnOK) && (mode.equalsIgnoreCase("edit"))) {
				name = tfName.getText().trim();
				URLstr = tfURL.getText();
				
				if (name.trim().equals("")) {
					String msg = "The name field is empty!";
					// display info dialog
					JOptionPane.showMessageDialog(parent, msg, "Warning",
					                              JOptionPane.INFORMATION_MESSAGE);

					return;
				}
				
				if (URLstr.trim().equals("")) {
					String msg = "URL is empty!";
					// display info dialog
					JOptionPane.showMessageDialog(parent, msg, "Warning",
					                              JOptionPane.INFORMATION_MESSAGE);

					return;
				}
				
				// There is no change, do nothing
				if (this.dataSource.getName().equalsIgnoreCase(name) && this.dataSource.getHref().equalsIgnoreCase(URLstr.trim())){
					this.dispose();
					return;
				}
				
				if (!this.name.equalsIgnoreCase(this.dataSource.getName())){
					// The bookmark name has been changed
					DataSource newDataSource = new DataSource();
					newDataSource.setName(name);
					newDataSource.setHref(URLstr);

					if (BookmarksUtil.isInBookmarks(theBookmarks, categoryName, newDataSource)){
						// The bookmark name must be unique
						JOptionPane.showMessageDialog(parent, "Bookmark with this name already existed!", "Warning",
						                              JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					
					// first delete the old one, then add (note: name is key of DataSource)
					BookmarksUtil.deleteBookmark(theBookmarks, categoryName, dataSource);
					BookmarksUtil.saveBookmark(theBookmarks, categoryName, newDataSource);
				}
				else { // The bookmark name has not been changed
										
					// first delete the old one, then add (note: name is key of DataSource)
					BookmarksUtil.deleteBookmark(theBookmarks, categoryName, dataSource);
					
					dataSource.setHref(URLstr);
					BookmarksUtil.saveBookmark(theBookmarks, categoryName, dataSource);						
				}

				this.dispose();
			} else if (_btn == btnCancel) {
				this.dispose();
			}
		}
	} // End of actionPerformed()

	/**
	 * This method is called from within the constructor to initialize the
	 * form. WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */

	// <editor-fold defaultstate="collapsed" desc=" Generated Code ">
	private void initComponents() {
		java.awt.GridBagConstraints gridBagConstraints;

		lbName = new javax.swing.JLabel();
		tfName = new javax.swing.JTextField();
		lbURL = new javax.swing.JLabel();
		tfURL = new javax.swing.JTextField();
		jPanel1 = new javax.swing.JPanel();
		btnOK = new javax.swing.JButton();
		btnCancel = new javax.swing.JButton();
		lbCategory = new javax.swing.JLabel();
		lbCategoryValue = new javax.swing.JLabel();

		getContentPane().setLayout(new java.awt.GridBagLayout());

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		lbName.setText("Name:");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridy = 1;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
		getContentPane().add(lbName, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
		getContentPane().add(tfName, gridBagConstraints);

		lbURL.setText("URL:");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
		getContentPane().add(lbURL, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
		getContentPane().add(tfURL, gridBagConstraints);

		btnOK.setText("OK");
		btnOK.setPreferredSize(new java.awt.Dimension(65, 23));
		jPanel1.add(btnOK);

		btnCancel.setText("Cancel");
		jPanel1.add(btnCancel);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.insets = new java.awt.Insets(20, 20, 20, 20);
		getContentPane().add(jPanel1, gridBagConstraints);

		lbCategory.setText("Category:");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = new java.awt.Insets(20, 10, 10, 0);
		getContentPane().add(lbCategory, gridBagConstraints);

		lbCategoryValue.setText("network");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = new java.awt.Insets(20, 10, 10, 0);
		getContentPane().add(lbCategoryValue, gridBagConstraints);

		btnOK.addActionListener(this);
		btnCancel.addActionListener(this);

		pack();
	} // </editor-fold>

	// Variables declaration - do not modify
	private javax.swing.JButton btnCancel;
	private javax.swing.JButton btnOK;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JLabel lbCategory;
	private javax.swing.JLabel lbCategoryValue;
	private javax.swing.JLabel lbName;
	private javax.swing.JLabel lbURL;
	private javax.swing.JTextField tfName;
	private javax.swing.JTextField tfURL;

	// End of variables declaration

}
