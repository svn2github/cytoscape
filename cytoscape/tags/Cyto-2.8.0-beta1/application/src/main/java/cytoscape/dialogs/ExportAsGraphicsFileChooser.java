package cytoscape.dialogs;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import com.lowagie.text.Font;

import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.util.CyFileFilter;
import cytoscape.util.FileUtil;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.VisualStyle;
import cytoscape.visual.calculators.Calculator;

/**
 * Dialog that chooses file to export to.
 * @author Samad Lotia
 */
public class ExportAsGraphicsFileChooser extends JDialog implements ActionListener {
	
	private static final long serialVersionUID = 4895622023575071394L;
	
	private final String SEPARATOR = System.getProperty("file.separator");
	
	private static final Dimension DIALOG_SIZE = new Dimension(650, 165);

	private File selectedFile;
	private CyFileFilter selectedFilter;

	private boolean exportTextAsFont = !(Boolean.getBoolean(CytoscapeInit.getProperties().getProperty("exportTextAsShape")));
	
	@Deprecated
	public ExportAsGraphicsFileChooser(final CyFileFilter[] formats) {
		this(formats, null);
	}

	public ExportAsGraphicsFileChooser(final CyFileFilter[] formats, final CyFileFilter defaultFilter) {
		
		super(Cytoscape.getDesktop(), "Export Network View as Graphics");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		this.selectedFilter = defaultFilter;
		
		initComponents();
		
		setModal(true);
		
		chooseFileButton.addActionListener(this);
		cancelButton.addActionListener(this);
		formatComboBox.addActionListener(this);
		chkExportTextAsFont.addActionListener(this);
		
		// Set the init value based on Cytoscape property "exportTextAsShape"
		chkExportTextAsFont.setSelected(exportTextAsFont);
				
		formatComboBox.setModel(new DefaultComboBoxModel(formats));
		
		if(selectedFilter == null) {
			formatComboBox.setSelectedIndex(0);
			this.selectedFilter = (CyFileFilter) formatComboBox.getSelectedItem();
		} else 
			formatComboBox.setSelectedItem(defaultFilter);
		
		formatComboBox.addItemListener(new FormatItemListener());
		
		removeFile();
		
		setDefaultFileName();

		setLocationRelativeTo(Cytoscape.getDesktop());
		pack();
		okButton.requestFocusInWindow();
	}
	
	
	private void setDefaultFileName() {
		final File recentDir = CytoscapeInit.getMRUD();
		final CyNetworkView view = Cytoscape.getCurrentNetworkView();
		
		final String title = view.getTitle();
		
		this.selectedFile = new File(recentDir.toString() + SEPARATOR + title);
		this.updateExtension();
		this.filePathField.setText(selectedFile.toString());
	}
	

	// This is a work-around, because ItemEvent is received twice in MyItemListener.
	private static int eventCount =0;

	private class FormatItemListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			// Ignore double event
			eventCount++;
			if (eventCount%2 == 0) {
				eventCount =0;
				return;
			}
			
			if (formatComboBox.getSelectedItem().toString().equalsIgnoreCase("EPS (*.eps)") && useTransparency()) {
				JOptionPane.showMessageDialog(	Cytoscape.getDesktop(),
							"Could not export in EPS format, because transparency is used in the Visual Style!");
					okButton.setEnabled(false);
					chooseFileButton.setEnabled(false);
			}
			else {
				if (selectedFile !=null)
					okButton.setEnabled(true);
				else
					okButton.setEnabled(false);
				
				chooseFileButton.setEnabled(true);
			}
		}		
	}
	
	// Check if transparency is used in the visual style
	private boolean useTransparency(){

		VisualStyle vs = Cytoscape.getVisualMappingManager().getVisualStyle();
		
		// Check opacity in default setting
		Properties node_default_props = vs.getNodeAppearanceCalculator().getDefaultAppearance().getDefaultProperties("");
		Properties edge_default_props = vs.getEdgeAppearanceCalculator().getDefaultAppearance().getDefaultProperties("");

		Enumeration nodePropNames = node_default_props.propertyNames();
		Enumeration edgePropNames = edge_default_props.propertyNames();
		
		while (nodePropNames.hasMoreElements()) {
			String tmp = (String) nodePropNames.nextElement();
			if (tmp.contains("Opacity")) {
				if ((new Float(node_default_props.getProperty(tmp))).floatValue() != 255) {
					return true;
				}
			}
		}

		while (edgePropNames.hasMoreElements()) {
			String tmp = (String) edgePropNames.nextElement();
			if (tmp.contains("Opacity")) {
				if ((new Float(edge_default_props.getProperty(tmp))).floatValue() != 255) {
					return true;										
				}
			}
		}

		//Check opacity in calculators
		List<Calculator> node_calculators = vs.getNodeAppearanceCalculator().getCalculators();
		for (Calculator cal: node_calculators) {
			if (cal.getVisualPropertyType() == cytoscape.visual.VisualPropertyType.NODE_OPACITY || 
			cal.getVisualPropertyType() == cytoscape.visual.VisualPropertyType.NODE_BORDER_OPACITY ||
			(cal.getVisualPropertyType() == cytoscape.visual.VisualPropertyType.NODE_LABEL_OPACITY))
			{
				return true;
			}
		}
		
		List<Calculator> edge_calculators = vs.getEdgeAppearanceCalculator().getCalculators();
		for (Calculator cal: edge_calculators) {
			if (cal.getVisualPropertyType()== cytoscape.visual.VisualPropertyType.EDGE_OPACITY ||
					cal.getVisualPropertyType()== cytoscape.visual.VisualPropertyType.EDGE_LABEL_OPACITY ||
					cal.getVisualPropertyType()== cytoscape.visual.VisualPropertyType.EDGE_TGTARROW_OPACITY ||
					cal.getVisualPropertyType()== cytoscape.visual.VisualPropertyType.EDGE_SRCARROW_OPACITY
			) {
				return true;
			}
		}
		
		return false;
	}

	public CyFileFilter getSelectedFormat() {
		return (CyFileFilter) formatComboBox.getSelectedItem();
	}

	
	public File getSelectedFile() {
		return selectedFile;
	}

	
	public void addActionListener(ActionListener l) {
		okButton.addActionListener(l);
	}

	
	protected void assignFile(File file) {
		selectedFile = file;
		filePathField.setText(selectedFile.getPath());
		okButton.setEnabled(true);
		if (formatComboBox.getSelectedItem().toString().equalsIgnoreCase("EPS (*.eps)") && useTransparency()) {
			okButton.setEnabled(false);
		}
	}

	
	protected void removeFile() {
		selectedFile = null;
		filePathField.setText("");
		okButton.setEnabled(false);
	}

	
	private void updateExtension() {
		if (selectedFile == null)
			return;

		// Strip away the extension
		String name = selectedFile.getName();
		int extensionIndex = name.lastIndexOf('.');
		if (extensionIndex != -1)
			name = name.substring(0, extensionIndex);
		
		// Figure out what extension to append
		CyFileFilter filter = (CyFileFilter) formatComboBox.getSelectedItem();
		String newExtension = (String) filter.getExtensionSet().iterator().next();
		
		selectedFile = new File(selectedFile.getParent(), name + "." + newExtension);
		filePathField.setText(selectedFile.getPath());
		okButton.setEnabled(true);
		if (formatComboBox.getSelectedItem().toString().equalsIgnoreCase("EPS (*.eps)") && useTransparency()) {
			okButton.setEnabled(false);
		}
	}

	
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj instanceof JButton) {
			JButton btn = (JButton) obj;
			if (btn == okButton) {
				//handle in ExportAsGraphicsAction
			}
			else if (btn == cancelButton) {
				this.dispose();
			}
			else if (btn == chooseFileButton) {
				final CyFileFilter filter = getSelectedFormat();
				String extension = "." + (String) filter.getExtensionSet().iterator().next();
				CyFileFilter[] filters = new CyFileFilter[1];
				filters[0] = filter;
				File[] files = FileUtil.getFiles("Export Network View as Graphics", FileUtil.SAVE, filters, null, null, false);
				if (files != null && files.length != 0 && files[0] != null)
				{
					File file = files[0];
					if (!file.getName().endsWith(extension))
						file = new File(file.getPath() + extension);
					assignFile(file);
				}
			}
		}
		if (obj instanceof JComboBox) {
			updateExtension();
			CyFileFilter filter = (CyFileFilter) formatComboBox.getSelectedItem();
			
			if (filter.getClass().getName().equalsIgnoreCase("cytoscape.actions.PDFExportFilter") ||
					filter.getClass().getName().equalsIgnoreCase("cytoscape.actions.SVGExportFilter") ||
					filter.getClass().getName().equalsIgnoreCase("cytoscape.actions.PSExportFilter")) {
				this.chkExportTextAsFont.setEnabled(true);
			}
			else {
				this.chkExportTextAsFont.setEnabled(false);
			}
		}
		if (obj instanceof JCheckBox) {
			exportTextAsFont = chkExportTextAsFont.isSelected();
			// Update Cytoscape property "exportTextAsShape"
			String exportTextAsShape = "false";
			if (!exportTextAsFont) {
				exportTextAsShape = "true";
			}
			CytoscapeInit.getProperties().setProperty("exportTextAsShape", exportTextAsShape);
		}
	}
	
	
	public boolean getExportTextAsFont() {
		return exportTextAsFont;
	}
	
	
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">                          
    private void initComponents() {
    		this.setMinimumSize(DIALOG_SIZE);
    		this.setPreferredSize(DIALOG_SIZE);
    		
        java.awt.GridBagConstraints gridBagConstraints;

        selectFileLabel = new javax.swing.JLabel();
        filePathField = new javax.swing.JTextField();
        chooseFileButton = new javax.swing.JButton();
        formatLabel = new javax.swing.JLabel();
        formatComboBox = new javax.swing.JComboBox();
        btnPanel = new javax.swing.JPanel();
        cancelButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        chkExportTextAsFont = new javax.swing.JCheckBox();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        selectFileLabel.setText("File to export to:");
        selectFileLabel.setFont(new java.awt.Font("SansSerif", Font.BOLD, 12));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(20, 10, 10, 0);
        getContentPane().add(selectFileLabel, gridBagConstraints);

        filePathField.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 0);
        getContentPane().add(filePathField, gridBagConstraints);

        chooseFileButton.setText("Choose...");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
        getContentPane().add(chooseFileButton, gridBagConstraints);

        formatLabel.setText("Format:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        getContentPane().add(formatLabel, gridBagConstraints);

        formatComboBox.setPreferredSize(new java.awt.Dimension(120, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        getContentPane().add(formatComboBox, gridBagConstraints);

        final Dimension buttonSize = new Dimension(100, 25);
        okButton.setText("OK");
        okButton.setPreferredSize(buttonSize);
        btnPanel.add(okButton);

        cancelButton.setText("Cancel");
        cancelButton.setPreferredSize(buttonSize);
        btnPanel.add(cancelButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 350, 15, 0);
        getContentPane().add(btnPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        getContentPane().add(jSeparator1, gridBagConstraints);

        chkExportTextAsFont.setText("Export text as font");
        chkExportTextAsFont.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        chkExportTextAsFont.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 0);
        getContentPane().add(chkExportTextAsFont, gridBagConstraints);

    }// </editor-fold>                        
	
    // Variables declaration - do not modify                     
    private javax.swing.JPanel btnPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JCheckBox chkExportTextAsFont;
    private javax.swing.JButton chooseFileButton;
    private javax.swing.JTextField filePathField;
    private javax.swing.JComboBox formatComboBox;
    private javax.swing.JLabel formatLabel;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JButton okButton;
    private javax.swing.JLabel selectFileLabel;
    // End of variables declaration                   
}
