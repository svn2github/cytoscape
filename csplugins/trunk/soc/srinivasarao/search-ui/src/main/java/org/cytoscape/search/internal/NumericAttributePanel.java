package org.cytoscape.search.internal;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.lucene.store.RAMDirectory;
import org.cytoscape.model.CyDataTable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.search.EnhancedSearchIndex;
import org.cytoscape.search.EnhancedSearchQuery;
import org.cytoscape.session.CyNetworkManager;

public class NumericAttributePanel extends BasicDraggablePanel {

	private static final long serialVersionUID = 1L;
	private JLabel jLabel = null;
	private JTextField jTextField = null;
	private JPanel attrPanel = null;

	private CyNetworkManager netmgr = null;
	private String attrName = null;
	private String type = null;
	private String valType = null;
	private String attrQuery = null;  //  @jve:decl-index=0:
	private NumberRangeModel rangeModel = null; // @jve:decl-index=0:
	private JRangeSliderExtended rangeSlider = null;
	private int minValue = 0, maxValue = 0;

	/**
	 * This is the default constructor
	 */
	public NumericAttributePanel(CyNetworkManager nm, String attrname,
			String attrType, String attrValType) {
		super();
		this.netmgr = nm;
		this.attrName = attrname;
		this.type = attrType;
		this.valType = attrValType;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {

		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new Insets(5, 8, 5, 0);

		jLabel = new JLabel();
		jLabel.setText(attrName);
		jLabel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (attrPanel.isVisible()) {
					attrPanel.setVisible(false);
				} else {
					attrPanel.setVisible(true);
				}
			}
		});
		this.setLayout(new GridBagLayout());
		this.add(jLabel, gridBagConstraints);
		attrPanel = new JPanel();
		attrPanel.setLayout(new GridBagLayout());
		attrPanel.setVisible(false);
		jTextField = this.getJTextField();
		jTextField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String term = attrName + ":" + jTextField.getText();
				if (attrQuery == null) {
					attrQuery = term;
				} else {
					attrQuery = attrQuery + " OR " + term;

				}
				SearchPanelFactory.getGlobalInstance(netmgr)
						.updateSearchField();
				jTextField.setText(null);
			}
		});

		GridBagConstraints gc = new GridBagConstraints();
		gc.anchor = GridBagConstraints.WEST;
		gc.insets = new Insets(0, 12, 4, 0);
		gc.gridx = 0;
		gc.gridy = 0;
		gc.weightx = 0;
		attrPanel.add(jTextField, gc);
		gc.gridx = 1;
		gc.weightx = 1.0;
		gc.fill = GridBagConstraints.HORIZONTAL;
		attrPanel.add(Box.createHorizontalStrut(0), gc);

		
		if (valType.equals("java.lang.Integer")) {
			List<Integer> l = getIntAttrValues();
			int[] values = new int[l.size()];
			for (int i = 0; i < l.size(); i++)
				values[i] = NumberUtils.toInt(((Integer) l.get(i)).toString());
			minValue = NumberUtils.min(values);
			maxValue = NumberUtils.max(values);
		} else if (valType.equals("java.lang.Double")) {
			List<Double> l1 = getDoubleAttrValues();
			double[] values1 = new double[l1.size()];
			for (int i = 0; i < l1.size(); i++)
				values1[i] = NumberUtils.toDouble(((Double) l1.get(i))
						.toString());
			minValue = (int) NumberUtils.min(values1);
			maxValue = ((int) NumberUtils.max(values1)) + 1;
		}

		rangeModel = new NumberRangeModel(minValue, maxValue, minValue,
				maxValue);
		rangeSlider = new JRangeSliderExtended(netmgr, rangeModel,
				JRangeSlider.HORIZONTAL, JRangeSlider.LEFTRIGHT_TOPBOTTOM);
		rangeSlider.setPreferredSize(new Dimension(150, 15));
		gc.gridx = 0;
		gc.gridy = 1;
		gc.weightx = 1;
		gc.fill = GridBagConstraints.HORIZONTAL;
		attrPanel.add(rangeSlider, gc);

		GridBagConstraints gg = new GridBagConstraints();
		gg.fill = GridBagConstraints.HORIZONTAL;
		gg.gridwidth = GridBagConstraints.REMAINDER;
		gg.weightx = 1.0;

		this.add(attrPanel, gg);
	}

	private JTextField getJTextField() {
		if (jTextField == null) {
			jTextField = new JTextField();
			jTextField.setPreferredSize(new Dimension(40, 20));
		}
		return jTextField;
	}

	public List<Integer> getIntAttrValues() {
		final CyNetwork network = netmgr.getCurrentNetwork();
		CyDataTable datatable = network.getCyDataTables(type).get(
				CyNetwork.DEFAULT_ATTRS);
		List<Integer> l = datatable.getColumnValues(attrName, Integer.class);
		return l;
	}

	public List<Double> getDoubleAttrValues() {
		final CyNetwork network = netmgr.getCurrentNetwork();
		CyDataTable datatable = network.getCyDataTables(type).get(
				CyNetwork.DEFAULT_ATTRS);
		List<Double> l = datatable.getColumnValues(attrName, Double.class);
		return l;
	}

	public String getQueryFromSearchBox() {
		String res = "(";
		if (attrQuery != null) {
			res = res + attrQuery + ")";
			return res;
		} else
			return null;
	}

	public String rangeQuery() {
		if(rangeSlider.query!=null){
			return attrName + ":[" + rangeSlider.query + "]";
		} else
			return null;
	}
	
	public void clearAll()
	{
		jTextField.setText(null);
		attrQuery = null;
		rangeSlider.query = null;
		rangeSlider.setRange(minValue, maxValue);
		rangeSlider.resetPopup();
	}

}
