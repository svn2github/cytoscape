package org.cytoscape.work.internal.tunables;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.awt.event.KeyListener;
import javax.swing.*;

import org.cytoscape.property.bookmark.Bookmarks;
import org.cytoscape.property.bookmark.BookmarksUtil;
import org.cytoscape.property.bookmark.Category;
import org.cytoscape.property.bookmark.DataSource;
import org.cytoscape.work.Tunable;


public class URLHandler extends AbstractGuiHandler {

	URL url;
	BookmarksUtil bkUtil;
	Bookmarks theBookmarks;
	String bookmarkCategory = "network";
	String urlstr;
	BookmarkComboBoxEditor bookmarkEditor = new BookmarkComboBoxEditor();
	JComboBox networkFileComboBox;

	private String pleaseMessage = "Please provide URL or select from list";
	
	public URLHandler(Field f, Object o, Tunable t,Bookmarks bookmarks,BookmarksUtil bkUtil) {
		super(f,o,t);
		this.bkUtil=bkUtil;
		this.theBookmarks=bookmarks;
		try{
			this.url= (URL) f.get(o);
		}catch(Exception e){e.printStackTrace();}

		Category theCategory = bkUtil.getCategory(bookmarkCategory,bookmarks.getCategory());	
		if (theCategory == null) {
			theCategory = new Category();
			theCategory.setName(bookmarkCategory);

			List<Category> theCategoryList = bookmarks.getCategory();
			theCategoryList.add(theCategory);
		}
		
//		System.out.println("TEST URLHandler");
		
		networkFileComboBox = new JComboBox();
		networkFileComboBox.setRenderer(new MyCellRenderer());
		networkFileComboBox.setEditor(bookmarkEditor);
		networkFileComboBox.setEditable(true);
		networkFileComboBox.setName("networkFileComboBox");
		networkFileComboBox.setToolTipText("<html><body>You can specify URL by the following:<ul><li>Type URL</li><li>Select from pull down menu</li><li>Drag & Drop URL from Web Browser</li></ul></body><html>");
		
		panel = new JPanel(new BorderLayout());
		panel.add(networkFileComboBox,BorderLayout.WEST);
		loadBookmarkCMBox();
	}

	public void handle() {
		urlstr = bookmarkEditor.getURLstr();
		System.out.println("ttt"+urlstr);
		try{
			if ( urlstr != null ) {
				try {
					url = new URL(urlstr);
					f.set(o,url);
				}catch (MalformedURLException e){e.printStackTrace();}
			}
		}catch (Exception e){}
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
    
	class BookmarkComboBoxEditor implements ComboBoxEditor {
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
	}
	
	private void loadBookmarkCMBox() {
		networkFileComboBox.removeAllItems();

		DefaultComboBoxModel theModel = new DefaultComboBoxModel();

		DataSource firstDataSource = new DataSource();
		firstDataSource.setName(pleaseMessage);
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
