package org.cytoscape.work.internal.gui;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.List;
import java.awt.event.KeyListener;
import javax.swing.*;
import javax.xml.*;

//import org.cytoscape.io.FileDefinition.Category;
import org.cytoscape.work.AbstractGuiHandler;
import org.cytoscape.work.Tunable;


public class URLHandler extends AbstractGuiHandler {

	URL url;
	
//	Bookmarks theBookmarks = null; // get it from session
	String bookmarkCategory = "network";
	String urlstr;
	BookmarkComboBoxEditor bookmarkEditor = new BookmarkComboBoxEditor();
	JComboBox networkFileComboBox;

	private String pleaseMessage = "Please provide URL or select from list";
	
	public URLHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
		try{
			this.url= (URL) f.get(o);
		}catch(Exception e){e.printStackTrace();}
		System.out.println("test");


		//theBookmarks = Cytoscape.getBookmarks();
		// if theBookmarks doesnot exist, create an empty one
//		if (theBookmarks == null) {
//			theBookmarks = new Bookmarks();
//			Cytoscape.setBookmarks(theBookmarks);
//		}

		// if bookmarkCategory "network" does not exist, create a "network" with
		// empty DataSource
//		Category category = new Category();
//		category.setName(bookmarkCategory);
//		List<Category> theCategoryList = theBookmarks.getCategory();
//		theCategoryList.add(category);

		
		networkFileComboBox = new JComboBox();
		//networkFileComboBox.setRenderer(new MyCellRenderer());
		networkFileComboBox.setEditor(bookmarkEditor);
		networkFileComboBox.setEditable(true);
		networkFileComboBox.setName("networkFileComboBox");
		networkFileComboBox.setToolTipText("<html><body>You can specify URL by the following:<ul><li>Type URL</li><li>Select from pull down menu</li><li>Drag & Drop URL from Web Browser</li></ul></body><html>");
		
		panel = new JPanel(new BorderLayout());
		panel.add(networkFileComboBox,BorderLayout.WEST);
//		loadBookmarkCMBox();
	}

	public void handle() {
		urlstr = bookmarkEditor.getURLstr();
			try{
				url = new URL(urlstr);
				if ( url != null ) {
				f.set(o,url);
//				path.setText("File: " + myfile.getPath());
				}
			}catch (Exception e){}
	}


	
    public String getState() {
		String s;
		try {
			s = f.get(o).toString();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			s = "";
		}
		return s;
    }
    
	class BookmarkComboBoxEditor implements ComboBoxEditor {
		//DataSource theDataSource = new DataSource();
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

//////		DataSource firstDataSource = new DataSource();
////		firstDataSource.setName("");
////		firstDataSource.setHref(null);
////		
////		theModel.addElement(firstDataSource);
////
////		// Extract the URL entries
////		List<DataSource> theDataSourceList = BookmarksUtil.getDataSourceList(bookmarkCategory,theBookmarks.getCategory());
//
//		if (theDataSourceList != null) {
//			for (int i = 0; i < theDataSourceList.size(); i++) {
//				theModel.addElement(theDataSourceList.get(i));
//			}
//		}

		networkFileComboBox.setModel(theModel);
	}

}
