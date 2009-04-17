package org.cytoscape.work.internal.tunables;


import java.awt.Component;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
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
import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;

public class URLHandler extends AbstractGuiHandler {

	public URL url;
	private BookmarksUtil bkUtil;
	private Bookmarks theBookmarks;
	private String bookmarkCategory = "network";
	private String urlstr;
	private BookmarkComboBoxEditor bookmarkEditor;
	private JComboBox networkFileComboBox;
	private JLabel titleLabel;
	private JSeparator titleSeparator;
	private String pleaseMessage = "Please provide URL or select from list";
	
	public URLHandler(Field f, Object o, Tunable t,Bookmarks bookmarks,BookmarksUtil bkUtil) {
		super(f,o,t);
		this.bkUtil=bkUtil;
		this.theBookmarks=bookmarks;
		this.bookmarkEditor = new BookmarkComboBoxEditor();
		titleSeparator = new JSeparator();
		titleLabel = new JLabel("Import URL file");
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
		networkFileComboBox = new JComboBox();
		networkFileComboBox.setRenderer(new MyCellRenderer());
		networkFileComboBox.setEditor(bookmarkEditor);
		networkFileComboBox.setEditable(true);
		networkFileComboBox.setName("networkFileComboBox");
		networkFileComboBox.setToolTipText("<html><body>You can specify URL by the following:<ul><li>Type URL</li><li>Select from pull down menu</li><li>Drag & Drop URL from Web Browser</li></ul></body><html>");
		
		
		final ToolTipManager tp = ToolTipManager.sharedInstance();
		tp.setInitialDelay(1);
		tp.setDismissDelay(7500);


		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.LEADING)
				.add(
					layout.createSequentialGroup()
						.addContainerGap()
						.add(layout.createParallelGroup(GroupLayout.LEADING)
							.add(networkFileComboBox,0, 350,Short.MAX_VALUE)
							.add(titleLabel,GroupLayout.PREFERRED_SIZE,350,GroupLayout.PREFERRED_SIZE)
							.add(titleSeparator,GroupLayout.DEFAULT_SIZE,350,Short.MAX_VALUE)
							)
						.addContainerGap()));
		
		layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.LEADING)
				.add(
					layout.createSequentialGroup()
						.addContainerGap()
						.add(titleLabel)
						.add(8, 8, 8)
						.add(titleSeparator,GroupLayout.PREFERRED_SIZE,GroupLayout.DEFAULT_SIZE,GroupLayout.PREFERRED_SIZE)
						.add(7, 7, 7)
						.addPreferredGap(LayoutStyle.RELATED)
						.add(networkFileComboBox,GroupLayout.PREFERRED_SIZE,GroupLayout.DEFAULT_SIZE,GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(LayoutStyle.RELATED,3, Short.MAX_VALUE)
						.addContainerGap()));
		
		panel.add(new JLabel("URL Path = "));
		panel.add(networkFileComboBox);
		bookmarkEditor.setStr(pleaseMessage);
		loadBookmarkCMBox();
	}

	public void handle() {
		urlstr = bookmarkEditor.getURLstr();
		try{
			if ( urlstr != null ) {
				try {
					url = new URL(urlstr);
					f.set(o,url);
				}catch (MalformedURLException e){e.printStackTrace();}
			}
		}catch (Exception e){}
	}

	
	public void resetValue(){
		try{
			f.set(o, new URL(""));
//			System.out.println("#########Value will be reset to initial value = "+ ((URL) f.get(o)).getPath()+ "#########");				
		}catch(Exception e){e.printStackTrace();}
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
    
    
	private class BookmarkComboBoxEditor implements ComboBoxEditor {
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

	
	private class MyCellRenderer extends JLabel implements ListCellRenderer {
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
