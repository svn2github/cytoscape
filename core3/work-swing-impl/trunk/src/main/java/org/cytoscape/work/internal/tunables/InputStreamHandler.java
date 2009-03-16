package org.cytoscape.work.internal.tunables;


import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.List;
import java.awt.event.KeyListener;
import javax.swing.*;

import org.cytoscape.property.bookmark.Bookmarks;
import org.cytoscape.property.bookmark.BookmarksUtil;
import org.cytoscape.property.bookmark.Category;
import org.cytoscape.property.bookmark.DataSource;
import org.cytoscape.work.Tunable;
import org.jdesktop.layout.GroupLayout;


public class InputStreamHandler extends AbstractGuiHandler {

	Bookmarks theBookmarks;
	String bookmarkCategory = "network";
	String urlstr;
	BookmarkComboBoxEditor bookmarkEditor = new BookmarkComboBoxEditor();
	JComboBox networkFileComboBox;
	//FileUtil flUtil;
	BookmarksUtil bkUtil;
	//StreamUtil stUtil;
	
	InputStream InStream = null;
	URL url;
	JButton button;
	File myFile;
	JFileChooser fileChooser;
	boolean filechoosen;
	JTextField path;
	private javax.swing.JSeparator titleSeparator;
	
	private String pleaseMessage = "Please provide URL or select from list";
	
	//private JPanel valuePanel;
	private JLabel titleLabel = new JLabel("Import Network File");
	private JPanel radioButtonPanel;
	private JButton selectButton;
	private JRadioButton remoteRadioButton;
	private JRadioButton localRadioButton;
	private JTextField networkFileTextField;
	private static final String URL_TOOLTIP = "<html>Enter URL or <strong><font color=\"red\">Drag and Drop local/remote files.</font></strong></html>";
	private static final String LOCAL_TOOLTIP = "<html>Specify path to local files.</html>";

	
	public InputStreamHandler(Field f, Object o, Tunable t,Bookmarks bookmarks,BookmarksUtil bkUtil) {
		super(f,o,t);
		this.bkUtil=bkUtil;
		//this.flUtil=flUtil;
		//this.stUtil=stUtil;
		this.theBookmarks=bookmarks;
		filechoosen = false;
		fileChooser = new JFileChooser();
		
		try{
			this.InStream = (InputStream) f.get(o);
		}catch(Exception e){e.printStackTrace();}

		Category theCategory = bkUtil.getCategory(bookmarkCategory,bookmarks.getCategory());	
		if (theCategory == null) {
			theCategory = new Category();
			theCategory.setName(bookmarkCategory);

			List<Category> theCategoryList = bookmarks.getCategory();
			theCategoryList.add(theCategory);
		}
		

		//panel.add(new JLabel("Path :"));
		//path = new JTextField("select file",12);
		//path.setFont(new Font(null, Font.ITALIC,10));
		//panel.add(path);

		initComponents();
		addListeners();
		switchImportView("remote");
		
		//panel = new JPanel(new BorderLayout());
		//panel.add(radioButtonPanel,BorderLayout.NORTH);
		//panel.add(valuePanel,BorderLayout.SOUTH);
	}
	
	
	public void handle() {
		if(localRadioButton.isSelected()){
			if(!filechoosen){
				int ret = fileChooser.showOpenDialog(null);
				if (ret == JFileChooser.APPROVE_OPTION){
					//File file = flUtil.getFile("Import Network File",flUtil.LOAD);
				    File file = fileChooser.getSelectedFile();
					if ( file != null ){
						try{
							InStream = new FileInputStream(file.getAbsolutePath());
							//InStream = flUtil.getInputStream(file.getAbsolutePath());
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
					url = new URL(urlstr);
					//InStream = stUtil.getInputStream(url);
					f.set(o,InStream);
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
		localRadioButton = new javax.swing.JRadioButton();
		remoteRadioButton = new javax.swing.JRadioButton();
		networkFileTextField = new javax.swing.JTextField();
		networkFileComboBox = new javax.swing.JComboBox();
		selectButton = new javax.swing.JButton();
		titleSeparator = new JSeparator();
		radioButtonPanel = new javax.swing.JPanel();
		//valuePanel = new javax.swing.JPanel();

		radioButtonPanel.setBorder(BorderFactory.createTitledBorder("Data Source Type"));
		
		localRadioButton.setSelected(true);
		localRadioButton.setText("Local");
		localRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
		localRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
		localRadioButton.setToolTipText(LOCAL_TOOLTIP);

		remoteRadioButton.setText("Remote/URL");
		remoteRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
		remoteRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
		remoteRadioButton.setToolTipText(URL_TOOLTIP);

	
		//radioButtonPanel.add(localRadioButton);
		//radioButtonPanel.add(remoteRadioButton);
		//valuePanel.add(networkFileTextField);
		//valuePanel.add(selectButton);
		//valuePanel.add(networkFileComboBox);
		
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
	
		GroupLayout radioButtonPanelLayout = new GroupLayout(radioButtonPanel);
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

		GroupLayout layout = new org.jdesktop.layout.GroupLayout(panel);
		panel.setLayout(layout);
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
											)
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
								.addContainerGap()));
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
		DataSource theDataSource = new DataSource();
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
	}
	
	private void loadBookmarkCMBox() {
		networkFileComboBox.removeAllItems();

		DefaultComboBoxModel theModel = new DefaultComboBoxModel();

		DataSource firstDataSource = new DataSource();
		firstDataSource.setName("");
		firstDataSource.setHref(null);

		theModel.addElement(firstDataSource);

		// Extract the URL entries
		List<DataSource> theDataSourceList = bkUtil.getDataSourceList(bookmarkCategory,theBookmarks.getCategory());
		if (theDataSourceList != null) {
			for (int i = 0; i < theDataSourceList.size(); i++) {
				theModel.addElement(theDataSourceList.get(i));
			}
		}
		networkFileComboBox.setModel(theModel);
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
	}
	
	
}
