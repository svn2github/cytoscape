/* * Modified Date: Jul.27.2010
 * * by : Steven Maere
 * */

/*
 * ResultPanel.java
 *
 * Created on July 31, 2006, 3:37 PM
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * The software and documentation provided hereunder is on an "as is" basis,
 * and the Pasteur Institut
 * has no obligations to provide maintenance, support,
 * updates, enhancements or modifications.  In no event shall the
 * Pasteur Institut
 * be liable to any party for direct, indirect, special,
 * incidental or consequential damages, including lost profits, arising
 * out of the use of this software and its documentation, even if
 * the Pasteur Institut
 * has been advised of the possibility of such damage. See the
 * GNU General Public License for more details: 
 *                http://www.gnu.org/licenses/gpl.txt.
 *
 * Authors: Olivier Garcia
 */

package pingo.GOlorize;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolTip;
import javax.swing.SwingUtilities;
import javax.swing.plaf.metal.MetalToolTipUI;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.plugin.CyPluginAdapter;
import org.cytoscape.view.model.CyNetworkView;

import pingo.Gene;
import pingo.ModuleNetwork;
import pingo.PingoAnalysis;
import pingo.SignificantFigures;
import BiNGO.ontology.OntologyTerm;

/**
 * 
 * @author ogarcia
 */
public class ResultPanel extends JPanel implements ResultAndStartPanel {

	private ModuleNetwork M;
	private Map<PingoAnalysis.TestInstance, Double> pvals;
	/** hashmap with key termID and value x. */
	private Map<PingoAnalysis.TestInstance, Integer> smallX;
	/** hashmap with key termID and value n. */
	private Map<PingoAnalysis.TestInstance, Integer> smallN;
	/** hashmap with X. */
	private Map<PingoAnalysis.TestInstance, Integer> bigX;
	/** hashmap with N. */
	private Map<PingoAnalysis.TestInstance, Integer> bigN;
	private Map<PingoAnalysis.TestInstance, Set<Gene>> neighbors;
	/** String with used test. */
	private String testString;
	/** String with used correction. */
	private String correctionString;
	private String fileName;
	private String annotationFile;
	/** the ontology file path. */
	private String ontologyFile;

	myTable jTable1;
	JScrollPane jScrollPane;
	private JPanel jPanelTableau;
	private JPanel jPanelDeBase;
	Object[][] data;
	private Cursor hand;
	private Object[] columnNames;

	private CyNetwork originalNetwork;

	private CyNetworkView originalNetworkView;
	HashMap goColor = new HashMap();

	GoBin goBin;
	final static int SELECT_COLUMN = 0;
	final static int GO_ID_COLUMN = 1;
	final static int GO_DESCRIPTION_COLUMN = 2;
	final static int GENE_COLUMN = 3;
	final static int ALIAS_COLUMN = 4;
	final static int GENE_DESCRIPTION_COLUMN = 5;
	final static int PVAL_COLUMN = 8;
	
	private final CyPluginAdapter adapter;

	/**
	 * Creates a new instance of ResultPanel
	 */

	public ResultPanel(ModuleNetwork M, Map<PingoAnalysis.TestInstance, Double> pvals,
			Map<PingoAnalysis.TestInstance, Integer> smallX, Map<PingoAnalysis.TestInstance, Integer> smallN,
			Map<PingoAnalysis.TestInstance, Integer> bigX, Map<PingoAnalysis.TestInstance, Integer> bigN,
			Map<PingoAnalysis.TestInstance, Set<Gene>> neighbors, String fileName, String annotationFile,
			String ontologyFile, String testString, String correctionString, CyNetwork currentNetwork,
			CyNetworkView currentNetworkview, GoBin goB, final CyPluginAdapter adapter) {
		this.adapter = adapter;
		this.M = M;
		this.pvals = pvals;
		this.smallX = smallX;
		this.smallN = smallN;
		this.bigX = bigX;
		this.bigN = bigN;
		this.neighbors = neighbors;
		this.fileName = fileName;
		this.annotationFile = annotationFile;
		this.ontologyFile = ontologyFile;
		this.testString = testString;
		this.correctionString = correctionString;
		this.hand = new Cursor(Cursor.HAND_CURSOR);
		this.originalNetwork = currentNetwork;
		this.originalNetworkView = currentNetworkview;
		this.goBin = goB;

		initComponents();

	}

	void initComponents() {
		columnNames = makeHeadersForJTable();
		data = makeDataForJTable(columnNames.length);

		jPanelDeBase = new javax.swing.JPanel();
		this.setLayout(new java.awt.BorderLayout());
		jPanelDeBase.setLayout(new BorderLayout());

		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();

		JPanel jPanelButtons = new JPanel();

		JLabel annotation_Description = new JLabel(annotationFile + ", " + ontologyFile);
		annotation_Description.setBorder(javax.swing.BorderFactory.createLineBorder(Color.black));
		jPanelButtons.add(annotation_Description);

		JButton jCloseButton = new JButton("close");
		jCloseButton.addActionListener(new CloseResultPanelListener(this));
		jPanelButtons.add(jCloseButton);

		jPanelDeBase.add(jPanelButtons, java.awt.BorderLayout.NORTH);

		jTable1 = new myTable(new ResultTableModel(columnNames, data));
		TableColumnModel tcModel = jTable1.getColumnModel();

		jTable1.setDragEnabled(false);
		final ResultTableSortFilterModel sorter = new ResultTableSortFilterModel(jTable1.getModel());
		jTable1 = new myTable(sorter);
		jTable1.setCellSelectionEnabled(true);
		tcModel = jTable1.getColumnModel();
		for (int i = 0; i < columnNames.length; i++) {
			if (columnNames[i].equals(" "))
				tcModel.getColumn(i).setPreferredWidth(15);
			if (columnNames[i].equals("GO ID"))
				tcModel.getColumn(i).setPreferredWidth(50);
			if (columnNames[i].equals("GO Description"))
				tcModel.getColumn(i).setPreferredWidth((screenSize.width - 15 - 50 - 6 * 95) / 3);
			if (columnNames[i].equals("Gene name"))
				tcModel.getColumn(i).setPreferredWidth(95);
			if (columnNames[i].equals("Gene alias"))
				tcModel.getColumn(i).setPreferredWidth(95);
			if (columnNames[i].equals("Neighbor freq"))
				tcModel.getColumn(i).setPreferredWidth(95);
			if (columnNames[i].equals("Data freq"))
				tcModel.getColumn(i).setPreferredWidth(95);
			if (columnNames[i].equals("Neighbors"))
				tcModel.getColumn(i).setPreferredWidth(95);
			if (columnNames[i].equals("Gene description"))
				tcModel.getColumn(i).setPreferredWidth((screenSize.width - 15 - 50 - 6 * 95) / 3);
			if (columnNames[i].equals("P value"))
				tcModel.getColumn(i).setPreferredWidth(95);
			if (columnNames[i].equals("Existing annotations"))
				tcModel.getColumn(i).setPreferredWidth((screenSize.width - 15 - 50 - 6 * 95) / 3);
		}

		jTable1.getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() < 2)
					return;
				int tableColumn = jTable1.columnAtPoint(e.getPoint());
				int modelColumn = jTable1.convertColumnIndexToModel(tableColumn);
				sorter.sort(modelColumn);
			}
		});
		// ////////////////////////////////////////////////les environs 40
		// lignes precedentes peut etre a virer

		jScrollPane = new javax.swing.JScrollPane(jTable1);
		jPanelDeBase.add(jScrollPane);

		// ////////a faire : utiliser le meme listener pour tout les tabs
		// displayPieChartListener = new DisplayPieChart2(this,
		// this.goBin.getNetwork_Options());

		JPanel jPanelApplyButtons = new JPanel();
		GridBagLayout blayout = new GridBagLayout();
		GridBagConstraints constr = new GridBagConstraints();
		jPanelApplyButtons.setLayout(blayout);
		constr.weightx = 0;
		constr.anchor = constr.WEST;

		JButton selectAllButton = new JButton();
		selectAllButton.setText("Select All");
		selectAllButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectAll();
			}
		});
		jPanelApplyButtons.add(selectAllButton, constr);

		JButton unSelectAllButton = new JButton();
		unSelectAllButton.setText("Unselect All");
		unSelectAllButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				unselectAll();
			}
		});
		jPanelApplyButtons.add(unSelectAllButton, constr);

		JButton jButton4 = new JButton("Select nodes");
		jButton4.addActionListener(new ZSelectNodes((ResultAndStartPanel) this, adapter));
		jPanelApplyButtons.add(jButton4, constr);

		jPanelDeBase.add(jPanelApplyButtons, java.awt.BorderLayout.SOUTH);

		jPanelDeBase.add(jPanelApplyButtons, java.awt.BorderLayout.SOUTH);

		this.add(jPanelDeBase, java.awt.BorderLayout.CENTER);
		goBin.synchroSelections(this);

	}

	public int getSelectColumn() {
		return SELECT_COLUMN;
	}

	public int getGeneColumn() {
		return this.GENE_COLUMN;
	}

	public int getAliasColumn() {
		return this.ALIAS_COLUMN;
	}

	public int getGoIdColumn() {
		return this.GO_ID_COLUMN;
	}

	public int getGeneDescriptionColumn() {
		return this.GENE_DESCRIPTION_COLUMN;
	}

	public int getGoDescriptionColumn() {
		return this.GO_DESCRIPTION_COLUMN;
	}

	public int getPvalColumn() {
		return this.PVAL_COLUMN;
	}

	public JTable getJTable() {
		return this.jTable1;
	}

	public boolean isSelected(String gene) {
		for (int i = 0; i < jTable1.getRowCount(); i++) {
			if (((Boolean) jTable1.getValueAt(i, this.getSelectColumn())).booleanValue()
					&& ((String) jTable1.getValueAt(i, this.getGeneColumn())).equals(gene))
				return true;

		}
		return false;
	}

	public void unselectAll() {
		for (int i = 0; i < jTable1.getRowCount(); i++) {
			jTable1.setValueAt(new Boolean(false), i, this.getSelectColumn());
		}
	}

	public void selectAll() {
		for (int i = 0; i < jTable1.getRowCount(); i++) {
			jTable1.setValueAt(new Boolean(true), i, this.getSelectColumn());
		}
	}

	public boolean unselect(String gene) {
		for (int i = 0; i < jTable1.getRowCount(); i++) {
			if (((Boolean) jTable1.getValueAt(i, this.getSelectColumn())).booleanValue()
					&& ((String) jTable1.getValueAt(i, this.getGeneColumn())).equals(gene))
				jTable1.setValueAt(new Boolean(true), i, this.getSelectColumn());
			return true;

		}
		return false;
	}

	public boolean select(String gene) {
		for (int i = 0; i < jTable1.getRowCount(); i++) {
			if (!((Boolean) jTable1.getValueAt(i, this.getSelectColumn())).booleanValue()
					&& ((String) jTable1.getValueAt(i, this.getGeneColumn())).equals(gene))
				jTable1.setValueAt(new Boolean(false), i, this.getSelectColumn());
			return true;

		}
		return false;
	}

	private Object[][] makeDataForJTable(int nb_columns) {

		// orden GO labels by increasing p-value or increasing smallX
		Object[][] data;
		ArrayList dataList = new ArrayList();
		int j = 0; // c'est le nombre de lignes du tableau = le nombre de
					// predictions

		PingoAnalysis.TestInstance[] keyLabels = new PingoAnalysis.TestInstance[smallX.size()];
		int count = 0;
		for (PingoAnalysis.TestInstance t : smallX.keySet()) {
			keyLabels[count] = t;
			count++;
		}
		PingoAnalysis.TestInstance[] ordenedKeySet;
		if (pvals != null) {
			ordenedKeySet = ordenKeysByPvalues(keyLabels);
		} else {
			ordenedKeySet = ordenKeysBySmallX(keyLabels);
		}

		boolean ok = true;

		for (int i = 0; (i < ordenedKeySet.length) && (ok == true); i++) {

			PingoAnalysis.TestInstance t = ordenedKeySet[i];
			String sX;
			String sN;
			String bX;
			String bN;

			Gene g = M.geneMap.get(t.m.name);

			try {
				sX = smallX.get(t).toString();
			} catch (Exception e) {
				sX = "N/A";
			}
			try {
				sN = smallN.get(t).toString();
			} catch (Exception e) {
				sN = "N/A";
			}
			try {
				bX = bigX.get(t).toString();
			} catch (Exception e) {
				bX = "N/A";
			}
			try {
				bN = bigN.get(t).toString();
			} catch (Exception e) {
				bN = "N/A";
			}

			int percentmp = (Integer.parseInt(sX) * 1000) / Integer.parseInt(bX);
			int percentmp2 = (Integer.parseInt(sN) * 1000) / Integer.parseInt(bN);
			double percentClusterFreq = (double) percentmp / 10;
			double percentTotalFreq = (double) percentmp2 / 10;

			Object[] dataTmp = new Object[11];
			dataTmp[0] = new Boolean(false);
			dataTmp[1] = new Integer(t.o.getId());
			dataTmp[2] = t.o.getName();
			dataTmp[3] = g.name;
			dataTmp[4] = g.alias;
			dataTmp[5] = g.description;
			dataTmp[6] = percentClusterFreq + " " + smallX.get(t) + "/" + bigX.get(t);
			dataTmp[7] = percentTotalFreq + " " + smallN.get(t) + "/" + bigN.get(t);
			dataTmp[8] = SignificantFigures.sci_format(pvals.get(t).toString(), 5);

			String neighborGenes = new String("");
			for (Gene g2 : neighbors.get(t)) {
				// System.out.println(g2.name);
				neighborGenes = neighborGenes.concat(g2.name + " \n");
			}
			dataTmp[9] = neighborGenes;

			String existingAnnotations = new String("");
			for (OntologyTerm o : g.GOannotations) {
				boolean good = true;
				for (OntologyTerm o2 : g.GOannotations) {
					if (!o.equals(o2)) {
						if (isAncestorOf(o, o2)) {
							good = false;
						}
					}
				}
				if (good == true) {
					existingAnnotations = existingAnnotations.concat(o.getName() + "(" + o.getId() + ") \n");
				}
			}
			dataTmp[10] = existingAnnotations;
			dataList.add(dataTmp);
			j++;
			this.goColor.put(t.o.getId(), Color.getColor("white"));

		}

		// //// remplissage du tableau qui sera donne au modele de swing.table
		Iterator iter = dataList.listIterator();
		data = new Object[j][nb_columns];

		for (int i = 0; i < j; i++) {// le "+1" utile que pour combobox choix
										// SGD amigo
			data[i] = (Object[]) iter.next();

		}
		return data;

	}

	private Object[] makeHeadersForJTable() {

		Object[] header = new Object[11];

		header[0] = " ";
		header[1] = "GO ID";
		header[2] = "GO Description";
		header[3] = "Gene name";
		header[4] = "Gene alias";
		header[5] = "Gene description";
		header[6] = "Neighbor freq";
		header[7] = "Data freq";
		header[8] = "P value";
		header[9] = "Neighbors";
		header[10] = "Existing annotations";

		return header;

	}

	public PingoAnalysis.TestInstance[] ordenKeysByPvalues(PingoAnalysis.TestInstance[] labels) {

		for (int i = 1; i < labels.length; i++) {
			int j = i;
			// get the first unsorted value ...
			PingoAnalysis.TestInstance insert_label = labels[i];
			Double val = pvals.get(labels[i]);
			// ... and insert it among the sorted
			while ((j > 0) && (val < pvals.get(labels[j - 1]))) {
				labels[j] = labels[j - 1];
				j--;
			}
			// reinsert value
			labels[j] = insert_label;
		}
		return labels;
	}

	public PingoAnalysis.TestInstance[] ordenKeysBySmallX(PingoAnalysis.TestInstance[] labels) {

		for (int i = 1; i < labels.length; i++) {
			int j = i;
			// get the first unsorted value ...
			PingoAnalysis.TestInstance insert_label = labels[i];
			Integer val = smallX.get(labels[i]);
			// ... and insert it among the sorted
			while ((j > 0) && (val < smallX.get(labels[j - 1]))) {
				labels[j] = labels[j - 1];
				j--;
			}
			// reinsert value
			labels[j] = insert_label;
		}
		return labels;
	}

	public CyNetworkView getNetworkView() {
		return originalNetworkView;
	}

	public GoBin getGoBin() {
		return goBin;
	}

	public void setTabName(String name) {
		this.fileName = name;
	}

	public String getTabName() {
		return this.fileName;
	}

	public class myTable extends JTable {

		public myTable(AbstractTableModel a) {
			super(a);
		}

		public JToolTip createToolTip() {
			return new MyToolTip();
		}

		public String getToolTipText(MouseEvent event) {
			Point p = event.getPoint();
			int hitColumnIndex = columnAtPoint(p);
			int hitRowIndex = rowAtPoint(p);
			String s = "" + getValueAt(hitRowIndex, hitColumnIndex);
			/*
			 * int strIndex = 0; for(int i = 0;i<s.length()/200;i++){
			 * if(strIndex + 200 <s.length()){ s = new
			 * StringBuffer(s).insert(strIndex + 200, "\n").toString(); }
			 * strIndex += 200; //compensate for character that we've just added
			 * strIndex++; }
			 */
			return s;
		}
	}

	public class MyToolTip extends JToolTip {
		public MyToolTip() {
			setUI(new MyToolTipUI());
		}
	}

	public class MyToolTipUI extends MetalToolTipUI {
		private String[] strs;

		private int maxWidth = 0;

		public void paint(Graphics g, JComponent c) {
			FontMetrics metrics = Toolkit.getDefaultToolkit().getFontMetrics(g.getFont());
			Dimension size = c.getSize();
			g.setColor(c.getBackground());
			g.fillRect(0, 0, size.width, size.height);
			g.setColor(c.getForeground());
			if (strs != null) {
				for (int i = 0; i < strs.length; i++) {
					g.drawString(strs[i], 3, (metrics.getHeight()) * (i + 1));
				}
			}
		}

		public Dimension getPreferredSize(JComponent c) {
			FontMetrics metrics = Toolkit.getDefaultToolkit().getFontMetrics(c.getFont());
			String tipText = ((JToolTip) c).getTipText();
			if (tipText == null) {
				tipText = "";
			}
			BufferedReader br = new BufferedReader(new StringReader(tipText));
			String line;
			int maxWidth = 0;
			Vector v = new Vector();
			try {
				while ((line = br.readLine()) != null) {
					int width = SwingUtilities.computeStringWidth(metrics, line);
					maxWidth = (maxWidth < width) ? width : maxWidth;
					v.addElement(line);
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			int lines = v.size();
			if (lines < 1) {
				strs = null;
				lines = 1;
			} else {
				strs = new String[lines];
				int i = 0;
				for (Enumeration e = v.elements(); e.hasMoreElements(); i++) {
					strs[i] = (String) e.nextElement();
				}
			}
			int height = metrics.getHeight() * lines;
			this.maxWidth = maxWidth;
			return new Dimension(maxWidth + 6, height + 4);
		}
	}

	public boolean isAncestorOf(OntologyTerm parent, OntologyTerm child) {
		if (parent.isParentOrContainerOf(child)) {
			return true;
		}

		int[] childParents = child.getParentsAndContainers();

		for (int i = 0; i < childParents.length; i++) {
			OntologyTerm childParent = M.ontology.getTerm(childParents[i]);
			if ((childParent != null) && isAncestorOf(parent, childParent)) {
				return true;
			}
		}

		return false;
	}

}
