//package org.cytoscape.work.internal.gui;
//
//
//import cytoscape.bookmarks.*;
//import cytoscape.util.*;
//import cytoscape.Cytoscape;
//import java.awt.BorderLayout;
//import java.awt.Component;
//import java.awt.event.ActionListener;
//import java.lang.reflect.Field;
//import java.util.List;
//import java.awt.event.KeyListener;
//import javax.swing.*;
//import javax.xml.*;
//
//import org.cytoscape.work.Tunable;
//
//import Tunable.*;
//import Utils.myURL;
//import GuiInterception.AbstractGuiHandler;
//
//
//public class URLHandler extends AbstractGuiHandler {
//
//	myURL url;
//	
//	Bookmarks theBookmarks = null; // get it from session
//	String bookmarkCategory = "network";
//	//String URLstr;
//	BookmarkComboBoxEditor bookmarkEditor = new BookmarkComboBoxEditor();
//	JComboBox networkFileComboBox;
//
//	
//	public URLHandler(Field f, Object o, Tunable t) {
//		super(f,o,t);
//		try{
//			this.url= (myURL) f.get(o);
//		}catch(Exception e){e.printStackTrace();}
//
//
//		//theBookmarks = Cytoscape.getBookmarks();
//		// if theBookmarks doesnot exist, create an empty one
//		if (theBookmarks == null) {
//			theBookmarks = new Bookmarks();
//			Cytoscape.setBookmarks(theBookmarks);
//		}
//
//		// if bookmarkCategory "network" does not exist, create a "network" with
//		// empty DataSource
//		Category category = new Category();
//		category.setName(bookmarkCategory);
//		List<Category> theCategoryList = theBookmarks.getCategory();
//		theCategoryList.add(category);
//
//
//		
//		
//		networkFileComboBox = new JComboBox();
//		//networkFileComboBox.setRenderer(new MyCellRenderer());
//		networkFileComboBox.setEditor(bookmarkEditor);
//		networkFileComboBox.setEditable(true);
//		networkFileComboBox.setName("networkFileComboBox");
//		networkFileComboBox.setToolTipText("<html><body>You can specify URL by the following:<ul><li>Type URL</li><li>Select from pull down menu</li><li>Drag & Drop URL from Web Browser</li></ul></body><html>");
//		
//		panel = new JPanel(new BorderLayout());
//		panel.add(networkFileComboBox,BorderLayout.WEST);
//		loadBookmarkCMBox();
//	}
//
//	public void handle() {		
//		
//	}
//
//
//	public void returnPanel(){
//		panel.removeAll();
//		panel.add(new JLabel("has been imported"),BorderLayout.EAST);
//		System.out.println(t.description());
//		panel.add(new JTextField(bookmarkEditor.getURLstr()),BorderLayout.WEST);
//	}
//	
//    public String getState() {
//		String s;
//		try {
//			s = f.get(o).toString();
//		} catch (IllegalAccessException e) {
//			e.printStackTrace();
//			s = "";
//		}
//		return s;
//    }
//    
//	class BookmarkComboBoxEditor implements ComboBoxEditor {
//		DataSource theDataSource = new DataSource();
//		JTextField tfInput = new JTextField("ZUTTTT");
//		public String getURLstr() {
//			return tfInput.getText();
//		}
//		public void addActionListener(ActionListener l) {
//			tfInput.addActionListener(l);
//		}
//		public void addKeyListener(KeyListener l) {
//			tfInput.addKeyListener(l);
//		}
//		public Component getEditorComponent() {
//			return tfInput;
//		}
//		public Object getItem() {
//			return theDataSource;
//		}
//		public void removeActionListener(ActionListener l) {
//		}
//		public void selectAll() {
//		}
//		public void setItem(Object anObject) {
//			if (anObject == null) {
//				return;
//			}
//
//			if (anObject instanceof DataSource) {
//				theDataSource = (DataSource) anObject;
//				tfInput.setText(theDataSource.getHref());
//			}
//		}
//	}
//	
//	private void loadBookmarkCMBox() {
//		networkFileComboBox.removeAllItems();
//
//		DefaultComboBoxModel theModel = new DefaultComboBoxModel();
//
//		DataSource firstDataSource = new DataSource();
//		firstDataSource.setName("");
//		firstDataSource.setHref(null);
//		
//		theModel.addElement(firstDataSource);
//
//		// Extract the URL entries
//		List<DataSource> theDataSourceList = BookmarksUtil.getDataSourceList(bookmarkCategory,theBookmarks.getCategory());
//
//		if (theDataSourceList != null) {
//			for (int i = 0; i < theDataSourceList.size(); i++) {
//				theModel.addElement(theDataSourceList.get(i));
//			}
//		}
//
//		networkFileComboBox.setModel(theModel);
//	}
//	
//	
//}
