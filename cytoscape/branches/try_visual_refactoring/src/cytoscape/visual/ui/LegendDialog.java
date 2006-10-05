/*
 * LegendDialog.java
 */

package cytoscape.visual.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Dimension;
import java.util.Iterator;
import java.util.Map;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.table.TableCellRenderer;

import org.freehep.util.export.ExportDialog;
import org.jdesktop.layout.GroupLayout; 

import cytoscape.visual.Arrow;
import cytoscape.visual.LineType;
import cytoscape.visual.VisualStyle;
import cytoscape.visual.NodeAppearanceCalculator;
import cytoscape.visual.EdgeAppearanceCalculator;
import cytoscape.visual.calculators.Calculator;

import cytoscape.Cytoscape;

public class LegendDialog extends JDialog {

	Map legendMap;
	VisualStyle visualStyle;

	public LegendDialog(JFrame parent, VisualStyle vs) {
		super(parent,true);
		visualStyle = vs;
		initComponents();
	}

/*
	private JPanel getDiscretePanel(String title, Map legendMap) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		JLabel title = new JLabel(title);
		panel.add(title);

		Object[][] data = new Object[legendMap.keySet().size()][2];
		Object[] col = new Object[2];

		col[0] = "Attribute Value";
		col[1] = "Visual Representation";

		Iterator it = legendMap.keySet().iterator();
		for (int i = 0; i < legendMap.keySet().size(); i++) {
			Object key = it.next();
			data[i][0] = key;
			data[i][1] = legendMap.get(key);
		}

		javax.swing.JTable jTable1 = new javax.swing.JTable();
		jTable1.setModel(new javax.swing.table.DefaultTableModel(data, col));
		jTable1.setGridColor(new java.awt.Color(255, 255, 255));
		jTable1.setIntercellSpacing(new java.awt.Dimension(1, 1));
		jTable1.setFont(new java.awt.Font("SansSerif", 0, 14));
		jTable1.setDefaultRenderer(Object.class, new LegendTableCellRenderer());

		panel.add(jTable1);
	}
	*/

	private JPanel generateLegendPanel() {
		JPanel legend = new JPanel();
		legend.setLayout(new BoxLayout(legend, BoxLayout.Y_AXIS));

		NodeAppearanceCalculator nac = visualStyle.getNodeAppearanceCalculator();
		List<Calculator> calcs = nac.getCalculators();
		for ( Calculator calc : calcs ) {
			legend.add( calc.getMapping(0).getLegend(calc.getTypeName()) );
		}

		EdgeAppearanceCalculator eac = visualStyle.getEdgeAppearanceCalculator();
		calcs = eac.getCalculators();
		for ( Calculator calc : calcs ) {
			legend.add( calc.getMapping(0).getLegend(calc.getTypeName()) );
		}
		return legend;
	}

	private void initComponents() {

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

		jPanel1 = generateLegendPanel();

		jScrollPane1 = new javax.swing.JScrollPane();
		jScrollPane1.setViewportView(jPanel1);

		jButton1 = new javax.swing.JButton();
		jButton1.setText("Export");
		jButton1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton1ActionPerformed(evt);
			}
		});

		jButton2 = new javax.swing.JButton();
		jButton2.setText("Cancel");
		jButton2.addActionListener(new java.awt.event.ActionListener() {	
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				dispose();	
			}
		});

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(jButton1);
		buttonPanel.add(jButton2);
	
		JPanel containerPanel = new JPanel();
		containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS));
		containerPanel.add(jScrollPane1);
		containerPanel.add(buttonPanel);
		
		setContentPane(containerPanel);

		pack();
		setVisible(true);

	}

	private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
		ExportDialog export = new ExportDialog();
		export.showExportDialog(Cytoscape.getDesktop(), "Export legend as ...",
				jPanel1, "export");
	}

	private javax.swing.JPanel jPanel1;
	private javax.swing.JButton jButton1;
	private javax.swing.JButton jButton2;
	private javax.swing.JScrollPane jScrollPane1;
}
/*
class LegendTableCellRenderer extends JLabel implements TableCellRenderer {
	private Font normalFont = new Font("Sans-serif", Font.PLAIN, 12);
	private final Color metadataBackground = new Color(255, 210, 255);

	private static final String METADATA_ATTR_NAME = "Network Metadata";

	private int defaultCellWidth;
	private int defaultCellHeight;

	public LegendTableCellRenderer() {
		super();

		setOpaque(true);
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		// initialize everything
		setHorizontalAlignment(CENTER);
		setText("");
		setIcon(null);
		setBackground(Color.WHITE);
		setForeground(table.getForeground());
		setFont( normalFont );
	
		// now make column specific changes
		if (column == 0) {
			setText((value == null) ? "" : value.toString());
		} else if (column == 1) {
			if (value instanceof Byte) {
				ImageIcon i = getIcon(value);
				table.setRowHeight( row, i.getIconHeight() );	
				setIcon( i );
			} else if (value instanceof LineType) {
				ImageIcon i = getIcon(value);
				table.setRowHeight( row, i.getIconHeight() );	
				setIcon( i );
			} else if (value instanceof Arrow) {
				ImageIcon i = getIcon(value);
				table.setRowHeight( row, i.getIconHeight() );	
				setIcon( i );
			} else if (value instanceof Color) {
				setBackground((Color) value);
			} else if (value instanceof Font) {
				Font f = (Font) value;
				setFont(f);
				setText(f.getFontName());
			} else { 
				setText(value.toString()); // presumably a string or size 
			}
		}
		return this;
	}

	private ImageIcon getIcon(Object o) {
		
		if ( o == null )
			return null;

                ImageIcon[] icons = null;
                Map iToS = null;

                MiscDialog md = new MiscDialog();

		if ( o instanceof Arrow ) {
                        icons = md.getArrowIcons();
                        iToS = MiscDialog.getArrowToStringHashMap(25);
                } else if ( o instanceof Byte ) {
                        icons = MiscDialog.getShapeIcons();
                        iToS = MiscDialog.getShapeByteToStringHashMap();
                } else if ( o instanceof LineType ) {
                        icons = MiscDialog.getLineTypeIcons();
                        iToS = MiscDialog.getLineTypeToStringHashMap();
                } else {
			return null;
		}

		String name = (String) iToS.get(o);
		for (int i = 0; i < icons.length; i++) 
			if (icons[i].getDescription().equals(name))
				return icons[i];

		return null;
	}

}
*/
