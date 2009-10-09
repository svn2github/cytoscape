package org.cytoscape.work.internal.tunables;

import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.ListCellRenderer;
import javax.swing.ToolTipManager;

import org.cytoscape.property.bookmark.Bookmarks;
import org.cytoscape.property.bookmark.BookmarksUtil;
import org.cytoscape.property.bookmark.Category;
import org.cytoscape.property.bookmark.DataSource;
import org.cytoscape.work.Tunable;

public class InputStreamHandler extends AbstractGuiHandler {

	InputStream InStream;

	private Bookmarks theBookmarks;
	private BookmarkComboBoxEditor bookmarkEditor;
	private BookmarksUtil bkUtil;
	private String bookmarkCategory = "network";
	private String urlstr;
	private JComboBox networkFileComboBox;
	private JFileChooser fileChooser;
	private JSeparator titleSeparator;
	private String pleaseMessage = "Please provide URL or select from list";

	private JLabel titleLabel;
	private JPanel radioButtonPanel;
	private JButton selectButton;
	private JRadioButton remoteRadioButton;
	private JRadioButton localRadioButton;
	private JTextField networkFileTextField;
	private MouseClic mc;

	// private StreamUtil stUtil;
	private static final String URL_TOOLTIP = "<html>Enter URL or <strong><font color=\"red\">Drag and Drop local/remote files.</font></strong></html>";
	private static final String LOCAL_TOOLTIP = "<html>Specify path to local files.</html>";

	protected InputStreamHandler(Field f, Object o, Tunable t,
			Bookmarks bookmarks, BookmarksUtil bkUtil) {
		super(f, o, t);
		this.bkUtil = bkUtil;
		// this.flUtil=flUtil;

		// this.stUtil=stUtil; System.out.println("StUtil = " + stUtil);

		titleLabel = new JLabel("Import Network File");
		this.theBookmarks = bookmarks;
		fileChooser = new JFileChooser();
		bookmarkEditor = new BookmarkComboBoxEditor();
		try {
			this.InStream = (InputStream) f.get(o);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Category theCategory = bkUtil.getCategory(bookmarkCategory, bookmarks
				.getCategory());
		if (theCategory == null) {
			theCategory = new Category();
			theCategory.setName(bookmarkCategory);
			List<Category> theCategoryList = bookmarks.getCategory();
			theCategoryList.add(theCategory);
		}
		initComponents();
		addListeners();
		switchImportView("local");
	}

	public void handle() {
		if (localRadioButton.isSelected()) {
			try {
				InStream = new BufferedInputStream(new FileInputStream(
						networkFileTextField.getText()));
				f.set(o, InStream);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (remoteRadioButton.isSelected()) {
			urlstr = bookmarkEditor.getURLstr();
			try {
				if (urlstr != null) {
					URL url = new URL(urlstr);
					f.set(o, url.openStream());
				}
			} catch (Exception e) {
			}
		}
	}

	public void resetValue() {
		try {
			if (localRadioButton.isSelected()) {
				f.set(o, new FileInputStream(""));
				// System.out.println("#########Value will be reset to initial value = "+
				// ((FileInputStream) f.get(o)).toString()+ "#########");
			} else if (remoteRadioButton.isSelected()) {
				f.set(o, new URL(""));
				// System.out.println("#########Value will be reset to initial value = "+
				// ((URL) f.get(o)).getPath()+ "#########");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getState() {
		String s = null;
		try {
			if (f.get(o) != null)
				s = f.get(o).toString();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			s = "";
		}
		return s;
	}

	private void initComponents() {
		localRadioButton = new JRadioButton();
		remoteRadioButton = new JRadioButton();
		networkFileTextField = new JTextField();
		networkFileComboBox = new JComboBox();
		selectButton = new JButton();
		titleSeparator = new JSeparator();
		radioButtonPanel = new JPanel();
		radioButtonPanel.setBorder(BorderFactory
				.createTitledBorder("Data Source Type"));

		localRadioButton.setSelected(true);
		localRadioButton.setText("Local");
		localRadioButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		localRadioButton.setMargin(new Insets(0, 0, 0, 0));
		localRadioButton.setToolTipText(LOCAL_TOOLTIP);

		remoteRadioButton.setText("Remote/URL");
		remoteRadioButton
				.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		remoteRadioButton.setMargin(new Insets(0, 0, 0, 0));
		remoteRadioButton.setToolTipText(URL_TOOLTIP);

		networkFileTextField.setText("Please select a network file...");
		networkFileTextField.setName("networkFileTextField");
		mc = new MouseClic(networkFileTextField);
		networkFileTextField.addMouseListener(mc);

		selectButton.setText("Select");
		selectButton.addActionListener(new myFileActionListener());
		selectButton.setActionCommand("open");

		networkFileComboBox.setRenderer(new MyCellRenderer());
		networkFileComboBox.setEditor(bookmarkEditor);
		networkFileComboBox.setEditable(true);
		networkFileComboBox.setName("networkFileComboBox");
		networkFileComboBox
				.setToolTipText("<html><body>You can specify URL by the following:<ul><li>Type URL</li><li>Select from pull down menu</li><li>Drag & Drop URL from Web Browser</li></ul></body><html>");
		final ToolTipManager tp = ToolTipManager.sharedInstance();
		tp.setInitialDelay(1);
		tp.setDismissDelay(7500);

		GroupLayout radioButtonPanelLayout = new GroupLayout(radioButtonPanel);
		radioButtonPanel.setLayout(radioButtonPanelLayout);
		radioButtonPanelLayout.setHorizontalGroup(radioButtonPanelLayout
				.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
						radioButtonPanelLayout.createSequentialGroup()
								.addContainerGap().addComponent(
										localRadioButton).addPreferredGap(
										LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(remoteRadioButton)
								.addContainerGap(250, Short.MAX_VALUE)));
		radioButtonPanelLayout
				.setVerticalGroup(radioButtonPanelLayout
						.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(
								radioButtonPanelLayout
										.createSequentialGroup()
										.addGroup(
												radioButtonPanelLayout
														.createParallelGroup(
																GroupLayout.Alignment.BASELINE)
														.addComponent(
																localRadioButton)
														.addComponent(
																remoteRadioButton))));

		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		layout
				.setHorizontalGroup(layout
						.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(
								layout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												layout
														.createParallelGroup(
																GroupLayout.Alignment.LEADING)
														.addComponent(
																networkFileComboBox,
																0, 350,
																Short.MAX_VALUE)
														.addComponent(
																titleLabel,
																GroupLayout.PREFERRED_SIZE,
																350,
																GroupLayout.PREFERRED_SIZE)
														.addComponent(
																titleSeparator,
																GroupLayout.DEFAULT_SIZE,
																350,
																Short.MAX_VALUE)
														.addComponent(
																radioButtonPanel,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.addGroup(
																layout
																		.createSequentialGroup()
																		.addComponent(
																				networkFileTextField,
																				GroupLayout.DEFAULT_SIZE,
																				350,
																				Short.MAX_VALUE)
																		.addPreferredGap(
																				LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				selectButton)))
										.addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(
				GroupLayout.Alignment.LEADING).addGroup(
				layout.createSequentialGroup().addContainerGap().addComponent(
						titleLabel).addGap(8, 8, 8).addComponent(
						titleSeparator, GroupLayout.PREFERRED_SIZE,
						GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addGap(7, 7, 7).addComponent(radioButtonPanel,
								GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE).addPreferredGap(
								LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(
								layout.createParallelGroup(
										GroupLayout.Alignment.BASELINE)
										.addComponent(selectButton)
										.addComponent(networkFileTextField))
						.addComponent(networkFileComboBox,
								GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE).addPreferredGap(
								LayoutStyle.ComponentPlacement.RELATED, 3,
								Short.MAX_VALUE).addContainerGap()));
	}

	private void addListeners() {
		LocalRemoteListener l = new LocalRemoteListener();
		localRadioButton.addActionListener(l);
		remoteRadioButton.addActionListener(l);
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

	private class LocalRemoteListener implements ActionListener {
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
		}
	}

	private class myFileActionListener implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			if (ae.getActionCommand().equals("open")) {
				int ret = fileChooser.showOpenDialog(panel);
				if (ret == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					// File file = flUtil.getFile("TEST",FileUtil.LOAD);
					if (file != null) {
						networkFileTextField.setFont(new Font(null, Font.PLAIN,
								10));
						networkFileTextField.setText(file.getAbsolutePath());
						networkFileTextField.removeMouseListener(mc);
					}
				}
			}
		}
	}

	private class MouseClic extends MouseAdapter implements MouseListener {
		JComponent component;

		public MouseClic(JComponent component) {
			this.component = component;
		}

		public void mouseClicked(MouseEvent e) {
			((JTextField) component).setText("");
		}
	}

	private class BookmarkComboBoxEditor implements ComboBoxEditor {
		DataSource theDataSource = new DataSource();
		JTextField tfInput = new JTextField(pleaseMessage);

		public String getURLstr() {
			return tfInput.getText();
		}

		public void setStr(String txt) {
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
		List<DataSource> theDataSourceList = bkUtil.getDataSourceList(
				bookmarkCategory, theBookmarks.getCategory());
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
