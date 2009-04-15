package org.cytoscape.work.internal.gui;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.awt.event.KeyListener;
import javax.swing.*;

//import org.cytoscape.property.bookmark.Bookmarks;
import org.cytoscape.property.bookmark.BookmarksUtil;
//import org.cytoscape.property.bookmark.Category;
//import org.cytoscape.property.bookmark.DataSource;
import org.cytoscape.work.AbstractGuiHandler;
import org.cytoscape.work.Tunable;

public class InputStreamHandler extends AbstractGuiHandler {

	URL url;
	BookmarksUtil bkUtil;
//	Bookmarks theBookmarks;
	String bookmarkCategory = "network";
	String urlstr;
	BookmarkComboBoxEditor bookmarkEditor = new BookmarkComboBoxEditor();
	JComboBox networkFileComboBox;
	
	JButton button;
	File myFile;
	JFileChooser fileChooser;
	boolean filechoosen;
	JTextField path;
	
	private String pleaseMessage = "Please provide URL or select from list";
	
	InputStream InStream = null;
	
	private JPanel radioButtonPanel;
	private JPanel valuePanel;
	private javax.swing.JButton selectButton;
	private javax.swing.ButtonGroup buttonGroup1;
	private javax.swing.JRadioButton remoteRadioButton;
	private javax.swing.JRadioButton localRadioButton;
	private javax.swing.JTextField networkFileTextField;
	private static final String URL_TOOLTIP = "<html>Enter URL or <strong><font color=\"red\">Drag and Drop local/remote files.</font></strong></html>";
	private static final String LOCAL_TOOLTIP = "<html>Specify path to local files.</html>";

	
	public InputStreamHandler(Field f, Object o, Tunable t){//,Bookmarks bookmarks,BookmarksUtil bkUtil) {
		super(f,o,t);
		this.bkUtil=bkUtil;
//		this.theBookmarks=bookmarks;
		filechoosen = false;
		fileChooser = new JFileChooser();
		
		try{
			this.InStream = (InputStream) f.get(o);
		}catch(Exception e){e.printStackTrace();}

//		Category theCategory = bkUtil.getCategory(bookmarkCategory,bookmarks.getCategory());	
//		if (theCategory == null) {
//			theCategory = new Category();
//			theCategory.setName(bookmarkCategory);
//
//			List<Category> theCategoryList = bookmarks.getCategory();
//			theCategoryList.add(theCategory);
//		}
		
		//System.out.println("Test INPUTSTREAM");

		//panel.add(new JLabel("Path :"));
		//path = new JTextField("select file",12);
		//path.setFont(new Font(null, Font.ITALIC,10));
		//panel.add(path);

		initComponents();
		addListeners();
		switchImportView("Local");
		
		panel = new JPanel(new BorderLayout());
		panel.add(radioButtonPanel,BorderLayout.NORTH);
		panel.add(valuePanel,BorderLayout.SOUTH);
	}
	
	
	public void handle() {
		if(localRadioButton.isSelected()){
			if(!filechoosen){
				int ret = fileChooser.showOpenDialog(null);
				if (ret == JFileChooser.APPROVE_OPTION){
				    File file = fileChooser.getSelectedFile();
					if ( file != null ){
						try{
							InStream = new FileInputStream(file);
							f.set(o,InStream);
						}catch (Exception e) { e.printStackTrace();}
						networkFileTextField.setFont(new Font(null, Font.PLAIN,10));
						networkFileTextField.setText(file.getPath());
					}
				}
			}
			filechoosen=true;
		}
		else if(remoteRadioButton.isSelected()){
			urlstr = bookmarkEditor.getURLstr();
			try{
				if ( urlstr != null ) {
					try {
						url = new URL(urlstr);
						InStream = new FileInputStream(url.getPath());
						System.out.println(url.getPath());
						//InStream = URLUtil.getBasicInputStream(url);
						f.set(o,InStream);
					}catch (MalformedURLException e){e.printStackTrace();}
				}
			}catch (Exception e){}
		}
	}


	
	
    public String getState() {
		String s = null;
		try {
			if(f.get(o)!=null) s = f.get(o).toString();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			s = "";
		}
		return s;
    }
    
    
    private void initComponents() {
		buttonGroup1 = new javax.swing.ButtonGroup();
		localRadioButton = new javax.swing.JRadioButton();
		remoteRadioButton = new javax.swing.JRadioButton();
		networkFileTextField = new javax.swing.JTextField();
		networkFileComboBox = new javax.swing.JComboBox();
		selectButton = new javax.swing.JButton();
		radioButtonPanel = new javax.swing.JPanel();
		valuePanel = new javax.swing.JPanel();

		radioButtonPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Data Source Type"));
		
		//buttonGroup1.add(localRadioButton);
		localRadioButton.setSelected(true);
		localRadioButton.setText("Local");
		localRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
		localRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
		localRadioButton.setToolTipText(LOCAL_TOOLTIP);

		//buttonGroup1.add(remoteRadioButton);
		remoteRadioButton.setText("Remote/URL");
		remoteRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
		remoteRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
		remoteRadioButton.setToolTipText(URL_TOOLTIP);

	
		radioButtonPanel.add(localRadioButton);
		radioButtonPanel.add(remoteRadioButton);
		valuePanel.add(networkFileTextField);
		valuePanel.add(selectButton);
		valuePanel.add(networkFileComboBox);
		
		networkFileTextField.setText("Please select a network file...");
		networkFileTextField.setName("networkFileTextField");
		//networkFileTextField.addFocusListener(this);

		selectButton.setText("Select");

		networkFileComboBox.setRenderer(new MyCellRenderer());
		networkFileComboBox.setEditor(bookmarkEditor);
		networkFileComboBox.setEditable(true);
		networkFileComboBox.setName("networkFileComboBox");
		networkFileComboBox.setToolTipText("<html><body>You can specify URL by the following:<ul><li>Type URL</li><li>Select from pull down menu</li><li>Drag & Drop URL from Web Browser</li></ul></body><html>");
		final ToolTipManager tp = ToolTipManager.sharedInstance();
		tp.setInitialDelay(1);
		tp.setDismissDelay(7500);
	}
    
    
    private void addListeners() {
		LocalRemoteListener l = new LocalRemoteListener();
		localRadioButton.addActionListener(l);
		remoteRadioButton.addActionListener(l);

		// ButtonActionListener btnActionListener = new ButtonActionListener();
		selectButton.addActionListener(this);
		//bookmarkEditor.addActionListener(this);
	}

    
    
    
    
	private void switchImportView(String pLocation) {
		if (pLocation.equalsIgnoreCase("Local")) {
			networkFileComboBox.setVisible(false);
			networkFileTextField.setVisible(true);
			localRadioButton.setSelected(true);
			remoteRadioButton.setSelected(false);
			selectButton.setVisible(true);
		} else {
			networkFileComboBox.setVisible(true);
			networkFileTextField.setVisible(false);
			selectButton.setVisible(false);
			localRadioButton.setSelected(false);
			remoteRadioButton.setSelected(true);
			loadBookmarkCMBox();
		}
	}
    
	
	
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
				}
			}
		} // actionPerformed()
	}
    
    
	class BookmarkComboBoxEditor implements ComboBoxEditor {
//		DataSource theDataSource = new DataSource();
		JTextField tfInput = new JTextField(pleaseMessage);
		
		public String getURLstr() {
			return tfInput.getText();
		}
		
		public void setStr(String txt){
			tfInput.setText(txt);
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
//		public Object getItem() {
//			return theDataSource;
//		}
		public void removeActionListener(ActionListener l) {
		}
		public void selectAll() {
		}
		public void setItem(Object anObject) {
			if (anObject == null) {
				return;
			}

//			if (anObject instanceof DataSource) {
//				theDataSource = (DataSource) anObject;
//				tfInput.setText(theDataSource.getHref());
//			}
		}

		public Object getItem() {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	private void loadBookmarkCMBox() {
		networkFileComboBox.removeAllItems();

		DefaultComboBoxModel theModel = new DefaultComboBoxModel();

//		DataSource firstDataSource = new DataSource();
//		firstDataSource.setName("");
//		firstDataSource.setHref(null);

//		theModel.addElement(firstDataSource);

		// Extract the URL entries
//		List<DataSource> theDataSourceList = bkUtil.getDataSourceList(bookmarkCategory,theBookmarks.getCategory());
//		if (theDataSourceList != null) {
//			for (int i = 0; i < theDataSourceList.size(); i++) {
//				theModel.addElement(theDataSourceList.get(i));
//			}
//		}
		networkFileComboBox.setModel(theModel);
	}

	
	class MyCellRenderer extends JLabel implements ListCellRenderer {
		private final static long serialVersionUID = 1202339872997986L;
		public MyCellRenderer() {
			setOpaque(true);
		}

		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
//			DataSource dataSource = (DataSource) value;
//			setText(dataSource.getName());
//			if (isSelected) {
//				if (0 < index) {
//					list.setToolTipText(dataSource.getHref());
//				}
//			}
			return this;
		}
	}

	@Override
	public void resetValue() {
		// TODO Auto-generated method stub
		
	}
	
	
}
