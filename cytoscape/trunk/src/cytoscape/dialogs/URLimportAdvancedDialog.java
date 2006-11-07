package cytoscape.dialogs;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.awt.Component;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.xml.bind.JAXBException;
import javax.swing.JLabel;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import java.awt.Color;
import java.net.MalformedURLException;
import java.net.Proxy;
import cytoscape.Cytoscape;
import cytoscape.bookmarks.Bookmarks;
import cytoscape.bookmarks.DataSource;
import cytoscape.util.BookmarksUtil;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.util.List;
import java.util.ArrayList;
import java.net.URL;
import java.io.File;
import java.net.InetSocketAddress;

public class URLimportAdvancedDialog extends JDialog implements ActionListener, ListSelectionListener {

	JDialog parent;
	private String bookmarkCategory;
	private URL bookmarkURL;
	private Proxy theProxyServer;
		
	private Bookmarks bookmarks;
	
    /** Creates new form URLimportAdvancedDialog */
    public URLimportAdvancedDialog(JDialog pParent, boolean modal, String pBookmarkCategory, 
    		URL bookmarkURL, Proxy pProxyServer) {
        super(pParent, modal);
        this.setTitle("Advanced Setting for " + pBookmarkCategory +" import");
        this.parent = pParent;
        this.bookmarkURL = bookmarkURL; 
        this.theProxyServer = pProxyServer;
        bookmarkCategory = pBookmarkCategory;
        initComponents();
        loadBookmarks();
    }
    
    public Proxy getProxyServer() {
    	return theProxyServer;
    }

    
    // for test only
    public URLimportAdvancedDialog() {
    	this.setTitle("Advanced Setting for network import");
        bookmarkCategory = "ontology";

    	File bookmarkFile = new File("C:/work/cytoscape/Cytoscape-2.4/bookmarks.xml");
    	try {
        	bookmarkURL = bookmarkFile.toURL();    		
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}
        initComponents();
        loadBookmarks();
    }
    
    
    private void loadBookmarks()
    {	
    	// Load the Bookmarks object from given xml file  
   		//Bookmarks theBookmarks = null;
    	MyListModel theModel = null;
    	List<DataSource> theDataSourceList = null;
   		try {
   	   		bookmarks = BookmarksUtil.getBookmarks(bookmarkURL);   			
   	    	
   	   		// Extract the URL entries
   	    	theDataSourceList = BookmarksUtil.getDataSourceList(bookmarkCategory, bookmarks.getCategory());
   		}
    	catch (IOException e)
    	{
    		System.out.println("IOException -- bookmarkSource");
    		//e.printStackTrace();
    		//return false;
    	}
    	catch (JAXBException e)
    	{
    		System.out.println("JAXBException -- bookmarkSource");    
    		//return false;
    	}
    	catch (Exception e) {
    		System.out.println("Failed to read the bookmark, the bookmark file may not exist!");        		
    	}
	    
    	theModel = new MyListModel(theDataSourceList);
 	
    	bookmarkList.setModel(theModel);    	
    }
    
   	
   	
   	private boolean deleteBookmark(DataSource pDataSource)
   	{   		
        try {
        	bookmarks = BookmarksUtil.getBookmarks(bookmarkURL);   			
        
        	if (BookmarksUtil.isInBookmarks(bookmarks, "network", pDataSource)) {
        		if (BookmarksUtil.deleteBookmark(bookmarkURL.getFile(), bookmarks, bookmarkCategory, pDataSource)) {
                	System.out.println(pDataSource.getName() + " is deleted from the Bookmarks!");        			
        		}
        		else {
                	System.out.println(pDataSource.getName() + " is not deleted from the Bookmarks!");        			
        		}
        	}
        }
        catch (JAXBException e)
        {
        	e.printStackTrace();
        	return false;
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        	return false;
        }
   		
   		return false;
   	}

    
 	public void actionPerformed(ActionEvent e)
 	{
		Object _actionObject = e.getSource();

		// handle Button events
		if (_actionObject instanceof JButton)
		{
			JButton _btn = (JButton)_actionObject;

			if (_btn == btnOK) {				
				this.dispose();
			}
			else if (_btn == btnAddBookmark){
				NewBookmarkDialog theNewDialog = new NewBookmarkDialog(this, true, bookmarkURL, bookmarkCategory);
				theNewDialog.setSize(300, 250);
				theNewDialog.setLocationRelativeTo(this);

				theNewDialog.setVisible(true);
				loadBookmarks(); // reload is required to update the GUI
			}
			else if (_btn == btnModifyBookmark){
				System.out.println("BtnModifyBookmark is pressed!");
			}
			else if (_btn == btnDeleteBookmarks){
				DataSource theDataSource = (DataSource) bookmarkList.getSelectedValue();
				
				MyListModel theModel = (MyListModel) bookmarkList.getModel();
				theModel.removeElement(bookmarkList.getSelectedIndex());
							
				deleteBookmark(theDataSource);// delete the selected bookmark from file
				
				if (theModel.getSize() == 0) {
					btnModifyBookmark.setEnabled(false);
					btnDeleteBookmarks.setEnabled(false);
				}
			}
			else if (_btn == btnSetProxy) {
				java.net.Proxy.Type proxyType = java.net.Proxy.Type.valueOf(cmbProxyType.getSelectedItem().toString());

				if (proxyType == java.net.Proxy.Type.DIRECT) {
					theProxyServer = null;
					lbProxyServer.setText("None");
					return;
				}
				
				int thePort;
				try {
					Integer tmpInteger = new Integer(tfPort.getText().trim());
					thePort = tmpInteger.intValue();
				}
				catch (Exception exp) {
				    JOptionPane.showMessageDialog(this, "Port error!", "Warning", JOptionPane.INFORMATION_MESSAGE);
					return;					
				}
				
				InetSocketAddress theAddress = new InetSocketAddress(tfHost.getText().trim(), thePort);
				
				try {
					theProxyServer = new Proxy(proxyType, theAddress);					
				}
				catch (Exception expProxy) {
				    JOptionPane.showMessageDialog(this, "Proxy server error!", "Warning", JOptionPane.INFORMATION_MESSAGE);					
					return;
				}
				
				lbProxyServer.setText(theProxyServer.toString());
			}
		}
 	}
 	
 	
	/**
	 * Called by ListSelectionListener interface when a table item is selected.
	 * @param	pListSelectionEvent	
	 */
	public void valueChanged(ListSelectionEvent pListSelectionEvent)
	{
		if (bookmarkList.getSelectedIndex() == -1) { // nothing is selected
			btnModifyBookmark.setEnabled(false);
			btnDeleteBookmarks.setEnabled(false);						
		}
		else {
			// enable buttons
			btnModifyBookmark.setEnabled(true);
			btnDeleteBookmarks.setEnabled(true);			
		}

	}

 	
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        lbProxyServer = new javax.swing.JLabel();
        lbProxyTitle = new javax.swing.JLabel();

        proxyPanel = new javax.swing.JPanel(new java.awt.GridBagLayout());
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        cmbProxyType = new javax.swing.JComboBox();
        tfHost = new javax.swing.JTextField();
        tfPort = new javax.swing.JTextField();
        
        btnSetProxy = new javax.swing.JButton();
        lbBookmarks = new javax.swing.JLabel();
        bookmarkList = new JList();
        jScrollPane1 = new javax.swing.JScrollPane(bookmarkList);
        
        btnDeleteBookmarks = new javax.swing.JButton();
        btnOK = new javax.swing.JButton();
        btnAddBookmark =new javax.swing.JButton();
    	btnModifyBookmark = new javax.swing.JButton();
    	btnPanelBookmark = new javax.swing.JPanel(new java.awt.GridBagLayout());
        	
        getContentPane().setLayout(new java.awt.GridBagLayout());

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        
        lbProxyTitle.setText("Proxy server");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        getContentPane().add(lbProxyTitle, gridBagConstraints);

        if (theProxyServer == null) {
        	lbProxyServer.setText("None");	
        }
        else {
        	lbProxyServer.setText(theProxyServer.toString());
        }
        
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 10);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = gridBagConstraints.WEST;
        getContentPane().add(lbProxyServer, gridBagConstraints);
        
        //
        jLabel1.setText("Type");
        proxyPanel.add(jLabel1, new java.awt.GridBagConstraints());

        jLabel2.setText("Host");
        proxyPanel.add(jLabel2, new java.awt.GridBagConstraints());

        jLabel3.setText("Port");
        proxyPanel.add(jLabel3, new java.awt.GridBagConstraints());

        cmbProxyType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "DIRECT", "HTTP", "SOCKS" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 10, 5);
        cmbProxyType.setMinimumSize(new Dimension(61,22));
        proxyPanel.add(cmbProxyType, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 10, 5);
        proxyPanel.add(tfHost, gridBagConstraints);

        tfPort.setMinimumSize(new java.awt.Dimension(50, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 10, 0);
        proxyPanel.add(tfPort, gridBagConstraints);
        //
        
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        getContentPane().add(proxyPanel, gridBagConstraints);

        btnSetProxy.setText("Set");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        getContentPane().add(btnSetProxy, gridBagConstraints);

        lbBookmarks.setText("Bookmarks");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        getContentPane().add(lbBookmarks, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        getContentPane().add(jScrollPane1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        //gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        getContentPane().add(btnPanelBookmark, gridBagConstraints);


        btnOK.setText("OK");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.insets = new java.awt.Insets(20, 10, 20, 20);
        getContentPane().add(btnOK, gridBagConstraints);

        
        //
        btnAddBookmark.setText("Add");
        btnAddBookmark.setPreferredSize(new Dimension(65,23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 5, 10);
        btnPanelBookmark.add(btnAddBookmark, gridBagConstraints);

        btnModifyBookmark.setText("Modify");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        btnPanelBookmark.add(btnModifyBookmark, gridBagConstraints);
        
        btnDeleteBookmarks.setText("Delete");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        btnPanelBookmark.add(btnDeleteBookmarks, gridBagConstraints);
        
        btnModifyBookmark.setVisible(false); // we may this button in the future
        
		btnModifyBookmark.setEnabled(false);
		btnDeleteBookmarks.setEnabled(false);
        
        // add event listeners
        btnOK.addActionListener(this);
        btnSetProxy.addActionListener(this);
        btnAddBookmark.addActionListener(this);
        btnModifyBookmark.addActionListener(this);
        btnDeleteBookmarks.addActionListener(this);
        
        bookmarkList.addListSelectionListener(this);
        
    	bookmarkList.setCellRenderer(new MyListCellRenderer());
    	bookmarkList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //pack();
    }// </editor-fold>
    
    
    class MyListModel extends javax.swing.AbstractListModel {
    	List<DataSource> theDataSourceList = new ArrayList<DataSource>(0);
    	
    	public MyListModel(List<DataSource> pDataSourceList)
    	{
    		theDataSourceList = pDataSourceList;
    	}
    	
    	public int getSize() {
    		if (theDataSourceList == null) {
    			return 0;    			
    		}
    		return theDataSourceList.size();
    	}
    	
    	public Object getElementAt(int i)
    	{
    		if (theDataSourceList == null) {
    			return null;    			
    		}
    		return theDataSourceList.get(i);
    	}
    	
    	public void addElement(DataSource pDataSource) {
    		theDataSourceList.add(pDataSource);
    	}
    	
       	public void removeElement(int pIndex) {
    		theDataSourceList.remove(pIndex);
    		fireContentsChanged(this, pIndex, pIndex);
    	}
    	
    } //MyListModel
    
    
    //class MyListCellrenderer 
    class MyListCellRenderer extends JLabel implements ListCellRenderer {
        public MyListCellRenderer() {
            setOpaque(true);
        }
        public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus)
        {
        	DataSource theDataSource = (DataSource) value;
            setText(theDataSource.getName());
            setToolTipText(theDataSource.getHref());
            setBackground(isSelected ? Color.red : Color.white);
            setForeground(isSelected ? Color.white : Color.black);
            return this;
        }
    }

    // Variables declaration - do not modify
    private javax.swing.JButton btnDeleteBookmarks;
    private javax.swing.JButton btnSetProxy;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbBookmarks;
    private javax.swing.JLabel lbProxyTitle;
    private javax.swing.JLabel lbProxyServer;    
    private javax.swing.JTextField tfProxyServer;
    private javax.swing.JButton btnOK;
    private javax.swing.JButton btnAddBookmark;
    private javax.swing.JButton btnModifyBookmark;
    private javax.swing.JPanel btnPanelBookmark;
	private JList bookmarkList;
	
	private javax.swing.JPanel proxyPanel;
	private javax.swing.JComboBox cmbProxyType;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JTextField tfHost;
    private javax.swing.JTextField tfPort;

    // End of variables declaration
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JDialog theDialog = new URLimportAdvancedDialog();
		theDialog.setPreferredSize(new Dimension(350,400));
		theDialog.pack();
		theDialog.setVisible(true);
	}

	
	public class NewBookmarkDialog extends JDialog implements ActionListener {
	    
		private String name;
		private String URLstr;
		private JDialog parent;
		//private Bookmarks bookmarks;
		private String categoryName;
		private URL bookmarkURL;
		
	    /** Creates new form NewBookmarkDialog */
	    public NewBookmarkDialog(JDialog parent, boolean modal, URL bookmarkURL, String categoryName) {
	        super(parent, modal);
	        this.parent = parent;
	        this.bookmarkURL = bookmarkURL;
	        this.categoryName = categoryName;
	        this.setTitle("Add new bookmark");
	        initComponents();
	    }
	    
	 	public void actionPerformed(ActionEvent e)
	 	{
			Object _actionObject = e.getSource();

			// handle Button events
			if (_actionObject instanceof JButton)
			{
				JButton _btn = (JButton)_actionObject;

				if (_btn == btnOK) {
						
					name = tfName.getText();
					URLstr = tfURL.getText();
					
					if (name.trim().equals("")||URLstr.trim().equals("")) {
						String msg = "Please provide a name/URL!";
					    // display info dialog
					    JOptionPane.showMessageDialog(parent, msg, "Warning", JOptionPane.INFORMATION_MESSAGE);
					    return;
					}					
					
					DataSource theDataSource = new DataSource();
					theDataSource.setName(name);
					theDataSource.setHref(URLstr);
										
					if (BookmarksUtil.isInBookmarks(bookmarkURL, categoryName, theDataSource)) {
						String msg = "Bookmark already existed!";
					    // display info dialog
					    JOptionPane.showMessageDialog(parent, msg, "Warning", JOptionPane.INFORMATION_MESSAGE);
					    return;
					}
					
					System.out.println("URLimportAdvancedDialog: bookmarkURL = " + bookmarkURL.toString());
					System.out.println("URLimportAdvancedDialog: bookmarkURL = " + bookmarkURL.getFile());
					
					if (BookmarksUtil.saveBookmark(bookmarkURL, categoryName,theDataSource)) {
						System.out.println("Bookmark is saved!");
						this.dispose();
					}
					else {
						System.out.println("Failed to save the bookmark!");						
					}
				}
				else if (_btn == btnCancel){
					this.dispose();
				}
			}
	 	}
	    

	 	
	    /** This method is called from within the constructor to
	     * initialize the form.
	     * WARNING: Do NOT modify this code. The content of this method is
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

	        getContentPane().setLayout(new java.awt.GridBagLayout());

	        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
	        lbName.setText("Name");
	        gridBagConstraints = new java.awt.GridBagConstraints();
	        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
	        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
	        getContentPane().add(lbName, gridBagConstraints);

	        gridBagConstraints = new java.awt.GridBagConstraints();
	        gridBagConstraints.gridx = 0;
	        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
	        gridBagConstraints.weightx = 1.0;
	        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
	        getContentPane().add(tfName, gridBagConstraints);

	        lbURL.setText("URL");
	        gridBagConstraints = new java.awt.GridBagConstraints();
	        gridBagConstraints.gridx = 0;
	        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
	        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
	        getContentPane().add(lbURL, gridBagConstraints);

	        gridBagConstraints = new java.awt.GridBagConstraints();
	        gridBagConstraints.gridx = 0;
	        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
	        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
	        getContentPane().add(tfURL, gridBagConstraints);

	        btnOK.setText("OK");
	        btnOK.setPreferredSize(new java.awt.Dimension(65, 23));
	        jPanel1.add(btnOK);

	        btnCancel.setText("Cancel");
	        jPanel1.add(btnCancel);

	        gridBagConstraints = new java.awt.GridBagConstraints();
	        gridBagConstraints.gridx = 0;
	        gridBagConstraints.insets = new java.awt.Insets(20, 20, 20, 20);
	        getContentPane().add(jPanel1, gridBagConstraints);

	        btnOK.addActionListener(this);
	        btnCancel.addActionListener(this);
	        
	        pack();
	    }// </editor-fold>
	    	    
	    // Variables declaration - do not modify
	    private javax.swing.JButton btnCancel;
	    private javax.swing.JButton btnOK;
	    private javax.swing.JPanel jPanel1;
	    private javax.swing.JLabel lbName;
	    private javax.swing.JLabel lbURL;
	    private javax.swing.JTextField tfName;
	    private javax.swing.JTextField tfURL;
	    // End of variables declaration
	    
	}

	
}
