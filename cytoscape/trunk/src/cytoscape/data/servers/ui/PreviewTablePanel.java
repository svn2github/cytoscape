package cytoscape.data.servers.ui;

import static cytoscape.data.servers.ui.enums.ImportDialogColorTheme.*;
import static cytoscape.data.servers.ui.enums.ImportDialogIconSets.*;
import static cytoscape.data.servers.ui.enums.ImportDialogFontTheme.*;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.jdesktop.layout.GroupLayout;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.readers.GeneAssociationTags;
import cytoscape.data.readers.TextFileDelimiters;
import cytoscape.data.servers.ui.ImportTextTableDialog.FileTypes;
import cytoscape.util.URLUtil;
import cytoscape.util.swing.ColumnResizer;

/**
 * General purpose preview table panel.
 * 
 * @author kono
 * 
 */
public class PreviewTablePanel extends JPanel {

	/*
	 * Define type of preview.
	 */
	public static final int ATTRIBUTE_PREVIEW = 1;
	public static final int NETWORK_PREVIEW = 2;

	/*
	 * Default messages
	 */
	private static final String DEF_MESSAGE = "Right Click: ON/OFF Column";
	private static final String DEF_TAB_MESSAGE = "Data File Preview Window";
	private static final String EXCEL_EXT = ".xls";

	// Lines start with this char will be ignored.
	private static final String COMMENT_CHAR = "!";

	private final String message;

	private boolean loadFlag = false;

	// Tracking attribute data type.
	// private Byte[] dataTypes;
	private Map<String, Byte[]> dataTypeMap;

	/*
	 * GUI Components
	 */
	private JLabel rightClickLabel;
	private javax.swing.JLabel offLabel;
	private javax.swing.JLabel onLabel;
	private javax.swing.JLabel leftClickLabel;
	private JLabel rightArrowLabel;
	private JLabel fileTypeLabel;
	private JScrollPane previewScrollPane;
	private JTable previewTable;

	// Tables for each worksheet.

	private Map<String, FileTypes> fileTypes;
	private Map<String, JTable> previewTables;

	private JTabbedPane tableTabbedPane;
	private JScrollPane keyPreviewScrollPane;

	private JList keyPreviewList;

	private DefaultListModel keyListModel;

	private PropertyChangeSupport changes = new PropertyChangeSupport(this);

	private int panelType;

	public PreviewTablePanel() {
		this(DEF_MESSAGE, ATTRIBUTE_PREVIEW);
	}

	public PreviewTablePanel(String message) {
		this(message, ATTRIBUTE_PREVIEW);
	}

	public PreviewTablePanel(String message, int panelType) {

		if (message == null) {
			this.message = DEF_MESSAGE;
		} else {
			this.message = message;
		}

		this.panelType = panelType;

		dataTypeMap = new HashMap<String, Byte[]>();

		// This object will track the file types of each table.
		fileTypes = new HashMap<String, FileTypes>();

		initComponents();

		hideUnnecessaryComponents();
	}

	private void hideUnnecessaryComponents() {

		fileTypeLabel.setVisible(false);

		if (panelType == NETWORK_PREVIEW) {
			keyPreviewScrollPane.setVisible(false);
			rightArrowLabel.setVisible(false);

			repaint();
		}
	}

	public void setKeyAttributeList(Set data) {

		keyPreviewScrollPane.setBackground(Color.white);
		keyListModel.clear();

		for (Object item : data) {
			keyListModel.addElement(item);
		}
		keyPreviewList.repaint();
	}

	public void addPropertyChangeListener(PropertyChangeListener l) {
		changes.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		changes.removePropertyChangeListener(l);
	}

	private void initComponents() {

		rightClickLabel = new JLabel();
		leftClickLabel = new javax.swing.JLabel();

		onLabel = new javax.swing.JLabel();
		offLabel = new javax.swing.JLabel();
		previewScrollPane = new JScrollPane();
		rightArrowLabel = new JLabel();
		tableTabbedPane = new JTabbedPane();
		keyListModel = new DefaultListModel();
		keyPreviewList = new JList(keyListModel);
		keyPreviewScrollPane = new JScrollPane();

		// model = new DefaultTableModel();

		previewTables = new HashMap<String, JTable>();
		previewTable = new JTable();
		previewTable.setOpaque(false);
		previewTable.setBackground(Color.white);

		fileTypeLabel = new JLabel();
		fileTypeLabel.setFont(new Font("Sans-Serif", Font.BOLD, 14));

		keyPreviewScrollPane.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Key Attributes"));

		keyPreviewList.setOpaque(false);
		keyPreviewList.setCellRenderer(new KeyAttributeListRenderer());
		keyPreviewScrollPane.setViewportView(keyPreviewList);

		previewScrollPane.setOpaque(false);
		previewScrollPane.setViewportView(previewTable);
		previewScrollPane.setBackground(Color.WHITE);

		final BufferedImage datasourceImage = getBufferedImage(Cytoscape.class
				.getResource("images/ximian/data_sources_trans.png"));

		final BufferedImage bi = getBufferedImage(Cytoscape.class
				.getResource("images/icon100_trans.png"));

		tableTabbedPane.setBackground(Color.white);
		tableTabbedPane
				.addChangeListener(new javax.swing.event.ChangeListener() {
					public void stateChanged(javax.swing.event.ChangeEvent evt) {
						tableTabbedPaneStateChanged(evt);
					}
				});

		previewScrollPane.getViewport().setOpaque(false);
		previewScrollPane.setViewportBorder(new CentredBackgroundBorder(
				datasourceImage));
		keyPreviewScrollPane.getViewport().setOpaque(false);
		keyPreviewScrollPane.setViewportBorder(new CentredBackgroundBorder(bi));

		tableTabbedPane.addTab(DEF_TAB_MESSAGE, previewScrollPane);
		// tableTabbedPane.setBorder(new CentredBackgroundBorder(bi));

		rightArrowLabel.setIcon(RIGHT_ARROW_ICON.getIcon());

		JTableHeader hd = previewTable.getTableHeader();
		hd.setReorderingAllowed(false);
		hd
				.setDefaultRenderer(new HeaderRenderer(hd.getDefaultRenderer(),
						null));

		/*
		 * Setting table properties
		 */
		previewTable.setCellSelectionEnabled(false);
		previewTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		previewTable.setDefaultEditor(Object.class, null);

		this
				.setBorder(BorderFactory
						.createTitledBorder(
								javax.swing.BorderFactory
										.createBevelBorder(javax.swing.border.BevelBorder.RAISED),
								"Preview",
								javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
								javax.swing.border.TitledBorder.DEFAULT_POSITION,
								new java.awt.Font("Dialog", 1, 11)));

		leftClickLabel
				.setHorizontalAlignment(SwingConstants.CENTER);
		leftClickLabel.setText("Left Click: Edit Column");
		leftClickLabel.setFont(LABEL_FONT.getFont());
		leftClickLabel.setForeground(Color.red);

		rightClickLabel.setFont(LABEL_FONT.getFont());
		rightClickLabel.setForeground(Color.red);
		rightClickLabel.setHorizontalAlignment(SwingConstants.CENTER);
		rightClickLabel.setText(message);

		onLabel.setBackground(HEADER_BACKGROUND_COLOR.getColor());
		onLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		onLabel
				.setIcon(CHECKED_ICON.getIcon());
		onLabel.setText("ON");
		onLabel.setToolTipText("Columns in this color will be imported.");
		onLabel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(
				0, 0, 0), 1, true));
		onLabel.setOpaque(true);

		offLabel.setBackground(HEADER_UNSELECTED_BACKGROUND_COLOR.getColor());
		offLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		offLabel
				.setIcon(UNCHECKED_ICON.getIcon());
		offLabel.setText("OFF");
		offLabel.setToolTipText("Columns in this color will NOT be imported.");
		offLabel.setBorder(new javax.swing.border.LineBorder(
				new java.awt.Color(0, 0, 0), 1, true));
		offLabel.setOpaque(true);

		GroupLayout previewPanelLayout = new GroupLayout(this);
		this.setLayout(previewPanelLayout);
		
		
		previewPanelLayout.setHorizontalGroup(
	            previewPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
	            .add(org.jdesktop.layout.GroupLayout.TRAILING, previewPanelLayout.createSequentialGroup()
	                .add(tableTabbedPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 705, Short.MAX_VALUE)
	                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
	                .add(rightArrowLabel)
	                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
	                .add(keyPreviewScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 208, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
	            .add(previewPanelLayout.createSequentialGroup()
	                .add(fileTypeLabel)
	                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
	                .add(leftClickLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
	                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
	                .add(rightClickLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 280, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
	                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
	                .add(onLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
	                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
	                .add(offLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
	                .addContainerGap())
	        );
	        previewPanelLayout.setVerticalGroup(
	            previewPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
	            .add(previewPanelLayout.createSequentialGroup()
	                .add(previewPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
	                    .add(fileTypeLabel)
	                    .add(onLabel)
	                    .add(offLabel)
	                    .add(rightClickLabel)
	                    .add(leftClickLabel))
	                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
	                .add(previewPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
	                    .add(tableTabbedPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
	                    .add(keyPreviewScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
	                    .add(org.jdesktop.layout.GroupLayout.LEADING, rightArrowLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)))
	        );
	}

	public JTable getPreviewTable() {
		JScrollPane selected = (JScrollPane) tableTabbedPane
				.getSelectedComponent();
		return (JTable) selected.getViewport().getComponent(0);
	}

	public int getTableCount() {
		return tableTabbedPane.getTabCount();
	}

	public String getSheetName(int index) {
		return tableTabbedPane.getTitleAt(index);
	}

	public Byte[] getDataTypes(final String selectedTabName) {
		return dataTypeMap.get(selectedTabName);
	}

	public Byte[] getCurrentDataTypes() {
		return dataTypeMap.get(getSelectedSheetName());
	}

	public FileTypes getFileType() {

		final String sheetName = getSheetName(tableTabbedPane
				.getSelectedIndex());

		if (sheetName.startsWith("gene_association")) {
			return FileTypes.GENE_ASSOCIATION_FILE;
		}

		return FileTypes.ATTRIBUTE_FILE;
	}

	/**
	 * Get selected tab name.
	 * 
	 * @return name of the selected tab (i.e., sheet name)
	 */
	public String getSelectedSheetName() {
		return tableTabbedPane.getTitleAt(tableTabbedPane.getSelectedIndex());
	}

	public JTable getPreviewTable(int index) {
		JScrollPane selected = (JScrollPane) tableTabbedPane
				.getComponentAt(index);
		return (JTable) selected.getViewport().getComponent(0);
	}

	private void tableTabbedPaneStateChanged(javax.swing.event.ChangeEvent evt) {
		System.out.println("tab cnahged!");
		if (tableTabbedPane.getSelectedComponent() != null
				&& ((JScrollPane) tableTabbedPane.getSelectedComponent())
						.getViewport().getComponent(0) != null
				&& loadFlag == true) {

			changes.firePropertyChange(ImportTextTableDialog.SHEET_CHANGED,
					null, null);
		}
	}

	/**
	 * Get backgroung images for table & list.
	 * 
	 * @param url
	 * @return
	 */
	private BufferedImage getBufferedImage(URL url) {
		BufferedImage image;
		try {
			image = ImageIO.read(url);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		}
		return image;
	}

	/**
	 * Load file and show preview.
	 * 
	 * @param sourceURL
	 * @param delimiters
	 * @param renderer
	 *            renderer for this table. Can be null.
	 * @param size
	 * @throws IOException
	 */
	public void setPreviewTable(URL sourceURL, List<String> delimiters,
			TableCellRenderer renderer, int size) throws IOException {
		TableCellRenderer curRenderer = renderer;

		/*
		 * If rendrer is null, create default one.
		 */
		if (curRenderer == null) {
			curRenderer = new AttributePreviewTableCellRenderer(0,
					new ArrayList<Integer>(),
					AttributePreviewTableCellRenderer.PARAMETER_NOT_EXIST,
					AttributePreviewTableCellRenderer.PARAMETER_NOT_EXIST,
					null, TextFileDelimiters.PIPE.toString());
		}

		/*
		 * Reset current state
		 */
		for (int i = 0; i < tableTabbedPane.getTabCount(); i++) {
			tableTabbedPane.removeTabAt(i);
		}
		previewTables = new HashMap<String, JTable>();

		TableModel newModel;

		fileTypeLabel.setVisible(true);
		if (sourceURL.toString().endsWith(EXCEL_EXT)) {
			fileTypeLabel.setIcon(SPREADSHEET_ICON.getIcon());
			fileTypeLabel.setText("Excel" + '\u2122' + " Workbook");

			POIFSFileSystem excelIn = new POIFSFileSystem(sourceURL
					.openStream());
			HSSFWorkbook wb = new HSSFWorkbook(excelIn);

			/*
			 * Load each sheet in the workbook.
			 */
			for (int i = 0; i < wb.getNumberOfSheets(); i++) {
				HSSFSheet sheet = wb.getSheetAt(i);
				newModel = parseExcel(sourceURL, size, curRenderer, sheet);
				guessDataTypes(newModel, wb.getSheetName(i));
				addTableTab(newModel, wb.getSheetName(i), curRenderer);
			}

		} else {
			fileTypeLabel.setIcon(TEXT_FILE_ICON.getIcon());
			fileTypeLabel.setText("Text File");

			newModel = parseText(sourceURL, size, curRenderer, delimiters);

			String[] urlParts = sourceURL.toString().split("/");
			final String tabName = urlParts[urlParts.length - 1];
			guessDataTypes(newModel, tabName);
			addTableTab(newModel, tabName, curRenderer);
		}

		if (getFileType() == FileTypes.GENE_ASSOCIATION_FILE) {
			fileTypeLabel.setText("Gene Association");
			fileTypeLabel
					.setToolTipText("This is a fixed-format Gene Association file.");
		}

		loadFlag = true;
	}

	private void addTableTab(TableModel newModel, final String tabName,
			TableCellRenderer renderer) {
		JTable newTable = new JTable(newModel);
		previewTables.put(tabName, newTable);
		JScrollPane newScrollPane = new JScrollPane();
		newScrollPane.setViewportView(newTable);
		newScrollPane.setBackground(Color.WHITE);

		tableTabbedPane.add(tabName, newScrollPane);

		/*
		 * Initialize data type atrray. By default, everything is a String.
		 */
		// dataTypes = new Byte[newModel.getColumnCount()];
		// dataTypeMap.put(tabName, new Byte[newModel.getColumnCount()]);
		// for (int j = 0; j < newModel.getColumnCount(); j++) {
		// dataTypes[j] = CyAttributes.TYPE_STRING;
		// }

		/*
		 * Setting table properties
		 */
		newTable.setCellSelectionEnabled(false);
		newTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		newTable.setDefaultEditor(Object.class, null);

		if (panelType == NETWORK_PREVIEW) {
			final int colCount = newTable.getColumnCount();
			final boolean[] importFlag = new boolean[colCount];
			for (int i = 0; i < colCount; i++) {
				importFlag[i] = false;
			}
			TableCellRenderer netRenderer = new AttributePreviewTableCellRenderer(
					AttributePreviewTableCellRenderer.PARAMETER_NOT_EXIST,
					new ArrayList<Integer>(),
					AttributePreviewTableCellRenderer.PARAMETER_NOT_EXIST,
					AttributePreviewTableCellRenderer.PARAMETER_NOT_EXIST,
					importFlag, TextFileDelimiters.PIPE.toString());

			newTable.setDefaultRenderer(Object.class, netRenderer);
		} else {
			newTable.setDefaultRenderer(Object.class, renderer);
		}

		JTableHeader hd = newTable.getTableHeader();
		hd.setReorderingAllowed(false);
		hd.setDefaultRenderer(new HeaderRenderer(hd.getDefaultRenderer(),
				dataTypeMap.get(tabName)));

		newTable.getTableHeader().addMouseListener(new TableHeaderListener());

		ColumnResizer.adjustColumnPreferredWidths(newTable);

		newTable.revalidate();
		newTable.repaint();
		newTable.getTableHeader().repaint();
	}

	/**
	 * Based on the file type, setup the initial column names.
	 * <p>
	 * </p>
	 * 
	 * @param colCount
	 * @return
	 */
	private Vector<String> getDefaultColumnNames(final int colCount,
			final URL sourceURL) {

		final Vector<String> colNames = new Vector<String>();

		String[] parts = sourceURL.toString().split("/");
		final String fileName = parts[parts.length - 1];

		if (colCount == GeneAssociationTags.values().length
				&& fileName.startsWith("gene_association")) {
			for (Object colName : GeneAssociationTags.values()) {
				colNames.add(colName.toString());
			}
		} else {
			for (int i = 0; i < colCount; i++) {
				colNames.add("Column " + (i + 1));
			}
		}

		return colNames;
	}

	private TableModel parseText(URL sourceURL, int size,
			TableCellRenderer renderer, List<String> delimiters)
			throws IOException {

		final BufferedReader bufRd = new BufferedReader(new InputStreamReader(
				URLUtil.getInputStream(sourceURL)));
		String line;

		/*
		 * Generate reg. exp. for delimiter.
		 */
		StringBuffer delimiterBuffer = new StringBuffer();

		if (delimiters.size() != 0) {
			delimiterBuffer.append("[");
			for (String delimiter : delimiters) {
				delimiterBuffer.append(delimiter);
			}
			delimiterBuffer.append("]");
		}

		/*
		 * Read & extract one line at a time. The line can be Tab delimited,
		 */
		boolean importAll = false;
		if (size == -1) {
			importAll = true;
		}

		int counter = 0;
		int maxColumn = 0;
		String[] parts;
		Vector data = new Vector();

		final String delimiterRegEx = delimiterBuffer.toString();

		while ((line = bufRd.readLine()) != null) {

			if (line.startsWith(COMMENT_CHAR) || line.trim().length() == 0) {
				// ignore
			} else {
				Vector row = new Vector();

				if (delimiterRegEx.length() == 0) {
					parts = new String[1];
					parts[0] = line;
				} else {
					parts = line.split(delimiterRegEx);
				}

				for (String entry : parts) {
					row.add(entry);
				}
				if (parts.length > maxColumn) {
					maxColumn = parts.length;
				}
				data.add(row);
			}
			counter++;
			if (importAll == false && counter >= size) {
				break;
			}
		}

		bufRd.close();

		return new DefaultTableModel(data, getDefaultColumnNames(maxColumn,
				sourceURL));

	}

	private TableModel parseExcel(URL sourceURL, int size,
			TableCellRenderer renderer, HSSFSheet sheet) throws IOException {

		int maxCol = 0;
		Vector data = new Vector();

		int rowCount = 0;
		HSSFRow row;
		while ((row = sheet.getRow(rowCount)) != null && rowCount < size) {

			Vector<Object> rowVector = new Vector<Object>();
			if (maxCol < row.getPhysicalNumberOfCells()) {
				maxCol = row.getPhysicalNumberOfCells();
			}
			for (short j = 0; j < maxCol; j++) {
				HSSFCell cell = row.getCell(j);
				if (cell == null) {
					rowVector.add(null);
				} else if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
					rowVector.add(cell.getStringCellValue());
				} else if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
					final Double dblValue = cell.getNumericCellValue();
					final Integer intValue = dblValue.intValue();

					if (intValue.doubleValue() == dblValue) {
						rowVector.add(intValue.toString());
					} else {
						rowVector.add(dblValue.toString());
					}

				} else if (cell.getCellType() == HSSFCell.CELL_TYPE_BOOLEAN) {
					rowVector.add(Boolean.toString(cell.getBooleanCellValue()));
				} else if (cell.getCellType() == HSSFCell.CELL_TYPE_BLANK
						|| cell.getCellType() == HSSFCell.CELL_TYPE_ERROR) {
					rowVector.add(null);
				} else {
					rowVector.add(null);
				}

			}

			data.add(rowVector);
			rowCount++;
		}

		return new DefaultTableModel(data, this.getDefaultColumnNames(maxCol,
				sourceURL));
	}

	private void guessDataTypes(final TableModel model, final String tableName) {
		/*
		 * Assume: Row1 = Boolean Row2 = Integer Row3 = Double Row4 = String
		 */

		final Integer[][] typeChecker = new Integer[4][model.getColumnCount()];
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < model.getColumnCount(); j++) {
				typeChecker[i][j] = 0;
			}
		}

		String cell = null;
		for (int i = 0; i < model.getRowCount(); i++) {

			for (int j = 0; j < model.getColumnCount(); j++) {
				cell = (String) model.getValueAt(i, j);
				boolean found = false;
				if (cell != null && Boolean.valueOf(cell)) {
					try {
						Integer.valueOf(cell);
						typeChecker[1][j]++;
						found = true;
					} catch (NumberFormatException e) {

					}
					if (found == false) {
						typeChecker[0][j]++;
						found = true;
					}
				} else if (cell != null) {

					try {
						Integer.valueOf(cell);
						typeChecker[1][j]++;
						found = true;
					} catch (NumberFormatException e) {

					}

					try {
						Double.valueOf(cell);
						typeChecker[2][j]++;
						found = true;
					} catch (NumberFormatException e) {

					}
				}

				if (found == false) {
					typeChecker[3][j]++;
				}

			}
		}

		Byte[] dataType = dataTypeMap.get(tableName);
		if (dataType == null || dataType.length != model.getColumnCount()) {
			dataType = new Byte[model.getColumnCount()];
		}
		for (int i = 0; i < dataType.length; i++) {

			int maxVal = 0;
			int maxIndex = 0;

			for (int j = 0; j < 4; j++) {
				if (maxVal < typeChecker[j][i]) {
					maxVal = typeChecker[j][i];
					maxIndex = j;
				}
			}

			if (maxIndex == 0) {
				dataType[i] = CyAttributes.TYPE_BOOLEAN;
				System.out.print(i + " Boolean, ");
			} else if (maxIndex == 1) {
				dataType[i] = CyAttributes.TYPE_INTEGER;
				System.out.print(i + " Int, ");
			} else if (maxIndex == 2) {
				dataType[i] = CyAttributes.TYPE_FLOATING;
				System.out.print(i + " Float, ");
			} else {
				dataType[i] = CyAttributes.TYPE_STRING;
				System.out.print(i + " String, ");
			}

			System.out.println("");

		}

		dataTypeMap.put(tableName, dataType);

	}

	/**
	 * Not yet implemented.
	 * <p>
	 * </p>
	 * 
	 * @param targetColumn
	 * @return
	 */
	public int checkKeyMatch(int targetColumn) {
		// final List fileKeyList = new ArrayList();
		// int matched = 0;
		//
		// TableModel curModel = getPreviewTable().getModel();
		// for (int i = 0; i < curModel.getRowCount(); i++) {
		// fileKeyList.add(curModel.getValueAt(i, targetColumn));
		// }
		//
		// for (int i = 0; i < keyPreviewList.getModel().getSize(); i++) {
		// if (fileKeyList.contains(keyPreviewList.getModel().getElementAt(i)))
		// {
		// matched++;
		// }
		// }

		return 0;
	}

	public void setAliasColumn(int column, boolean flag) {

		AttributePreviewTableCellRenderer rend = (AttributePreviewTableCellRenderer) getPreviewTable()
				.getCellRenderer(0, column);
		rend.setAliasFlag(column, flag);
		// rend.setImportFlag(column, !rend.getImportFlag(column));
		getPreviewTable().getTableHeader().resizeAndRepaint();
		getPreviewTable().repaint();
	}

	private final class TableHeaderListener implements MouseListener {
		public void mouseClicked(MouseEvent e) {

			JTable targetTable = getPreviewTable();
			final String selectedTabName = getSelectedSheetName();
			Byte[] dataTypes = dataTypeMap.get(selectedTabName);

			final int column = targetTable.getColumnModel().getColumnIndexAtX(
					e.getX());

			if (SwingUtilities.isRightMouseButton(e)) {
				/*
				 * Right click: This action pops up an dialog to edit the
				 * attribute type and name.
				 */
				AttributeTypeDialog atd = new AttributeTypeDialog(Cytoscape
						.getDesktop(), true, targetTable.getColumnModel()
						.getColumn(column).getHeaderValue().toString(),
						dataTypes[column], column);

				atd.setLocationRelativeTo(targetTable.getParent());
				atd.setVisible(true);

				final String name = atd.getName();
				final byte newType = atd.getType();

				if (name != null) {
					targetTable.getColumnModel().getColumn(column)
							.setHeaderValue(name);
					targetTable.getTableHeader().resizeAndRepaint();

					if (newType == CyAttributes.TYPE_SIMPLE_LIST) {
						// listDelimiter = atd.getListDelimiterType();
						System.out.println("Fireing: "
								+ ImportTextTableDialog.LIST_DELIMITER_CHANGED);
						changes.firePropertyChange(
								ImportTextTableDialog.LIST_DELIMITER_CHANGED,
								null, atd.getListDelimiterType());
					}
					final Vector keyValPair = new Vector();
					keyValPair.add(column);
					keyValPair.add(newType);
					changes.firePropertyChange(
							ImportTextTableDialog.ATTR_DATA_TYPE_CHANGED, null,
							keyValPair);

					final Vector colNamePair = new Vector();
					colNamePair.add(column);
					colNamePair.add(name);
					changes.firePropertyChange(
							ImportTextTableDialog.ATTRIBUTE_NAME_CHANGED, null,
							colNamePair);

					dataTypes[column] = newType;

					targetTable.getTableHeader().setDefaultRenderer(
							new HeaderRenderer(targetTable.getTableHeader()
									.getDefaultRenderer(), dataTypes));
					dataTypeMap.put(selectedTabName, dataTypes);

				}
			} else if (SwingUtilities.isLeftMouseButton(e)
					&& e.getClickCount() == 1) {
				AttributePreviewTableCellRenderer rend = (AttributePreviewTableCellRenderer) targetTable
						.getCellRenderer(0, column);
				rend.setImportFlag(column, !rend.getImportFlag(column));
				targetTable.getTableHeader().resizeAndRepaint();
				targetTable.repaint();
			}
		}

		public void mouseEntered(MouseEvent arg0) {
		}

		public void mouseExited(MouseEvent arg0) {
		}

		public void mousePressed(MouseEvent arg0) {
		}

		public void mouseReleased(MouseEvent arg0) {
		}
	}
}

class KeyAttributeListRenderer extends JLabel implements ListCellRenderer {

	private static final Font KEY_LIST_FONT = new Font("Sans-Serif", Font.BOLD,
			16);
	private static final Color FONT_COLOR = Color.BLACK;

	public KeyAttributeListRenderer() {
	}

	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {

		setFont(KEY_LIST_FONT);
		setForeground(FONT_COLOR);
		setText(value.toString());

		this.setOpaque(false);
		return this;

	}
}

class CentredBackgroundBorder implements Border {
	private final BufferedImage image;

	public CentredBackgroundBorder(BufferedImage image) {
		this.image = image;
	}

	public void paintBorder(Component c, Graphics g, int x, int y, int width,
			int height) {
		x += (width - image.getWidth()) / 2;
		y += (height - image.getHeight()) / 2;
		((Graphics2D) g).drawRenderedImage(image, AffineTransform
				.getTranslateInstance(x, y));
	}

	public Insets getBorderInsets(Component c) {
		return new Insets(0, 0, 0, 0);
	}

	public boolean isBorderOpaque() {
		return true;
	}
}
