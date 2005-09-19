package cytoscape.util.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.StringTokenizer;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.JLabel;


import cytoscape.CytoscapeInit;

/*
 * Final Step for the new file format.
 * This panel creates the manifest file and load the biodataserver.
 */
public class BioDataServerPanel4 extends JPanel {

	private JLabel anotherBlankSpace;

	private JLabel blankSpace;

	private ButtonGroup connectorGroup;

	private JLabel jLabel1;

	private JPanel jPanel1;

	private JLabel progressDescription;

	private JProgressBar progressSent;

	private JLabel welcomeTitle;

	private JLabel yetAnotherBlankSpace1;

	private JPanel contentPanel;

	private JLabel iconLabel;

	private JSeparator separator;

	private JLabel textLabel;

	private JPanel titlePanel;

	private JLabel jLabel2;

	private JLabel jLabel3;

	private JComboBox spList;

	File start;
	
	String species;

	private JTextField taxonNameBox;

	private JButton setButton;
	
	private ButtonGroup selectionType;

	private JRadioButton comboBoxButton;

	private JRadioButton textBoxButton;

	private JPanel jPanel2;

	private JPanel jPanel3;

	private JTextField currentSpBox;

	private JTextField manifestFileName;

	private JButton selectManifestFile;

	private String manifestName;

	private JFileChooser jc1;
	

	public BioDataServerPanel4() {

		super();

		species = null;
		start = CytoscapeInit.getMRUD();
		spList = new JComboBox();

		contentPanel = getContentPanel();
		ImageIcon icon = getImageIcon();

		titlePanel = new javax.swing.JPanel();
		textLabel = new javax.swing.JLabel();
		iconLabel = new javax.swing.JLabel();
		separator = new javax.swing.JSeparator();

		setLayout(new java.awt.BorderLayout());

		titlePanel.setLayout(new java.awt.BorderLayout());
		titlePanel.setBackground(Color.gray);

		textLabel.setBackground(Color.gray);
		textLabel.setFont(new Font("Sans Serif", Font.BOLD, 14));
		textLabel.setText("Load Manifest File");
		textLabel.setBorder(new EmptyBorder(new Insets(10, 10, 10, 10)));
		textLabel.setOpaque(true);

		iconLabel.setBackground(Color.gray);
		if (icon != null)
			iconLabel.setIcon(icon);

		titlePanel.add(textLabel, BorderLayout.CENTER);
		titlePanel.add(iconLabel, BorderLayout.EAST);
		titlePanel.add(separator, BorderLayout.SOUTH);

		add(titlePanel, BorderLayout.NORTH);
		JPanel secondaryPanel = new JPanel();
		secondaryPanel.add(contentPanel, BorderLayout.NORTH);
		add(secondaryPanel, BorderLayout.WEST);

	}
	
	public void createManifestFileChooser() {
		jc1 = new JFileChooser( start );
		jc1.setDialogTitle("Select Manifest File");
	}
	
	public String getManifestFileNameFromTextBox() {
		manifestName = manifestFileName.getText();
		return manifestName;
	}
	
	public void addSelectButtonActionListener( ActionListener l ) {
        selectManifestFile.addActionListener(l);
    }

	
	public File getManifestFile( boolean show ) {
		File targetFile = null;
		if( show == true ) {
			int result = jc1.showOpenDialog(null);
		}
		targetFile = jc1.getSelectedFile();
		return targetFile;
	}
	
	public void setManifestFileName( String fileName ) {
		manifestFileName.setText( fileName );
		manifestName = fileName;
	}
	
	public String getManifestFileName() {
		return manifestName;
	}
	
	
	private JPanel getContentPanel() {
	
		JPanel contentPanel1 = new JPanel();
		String filePath = start + "/testData/annotation/tax_report.txt";
	    File taxonFile = new File(filePath);
	    
	    manifestFileName = new JTextField(40);
		selectManifestFile = new JButton("Select Manifest File");
        
        welcomeTitle = new JLabel();
        jPanel1 = new JPanel();
        
        blankSpace = new JLabel();
        anotherBlankSpace = new JLabel();
        yetAnotherBlankSpace1 = new JLabel();
        
        jLabel1 = new JLabel();
        jLabel2 = new JLabel();
        jLabel3 = new JLabel();
        
        contentPanel1.setLayout(new java.awt.BorderLayout());

        welcomeTitle.setText("Please select the manifest file:");
        contentPanel1.add(welcomeTitle, java.awt.BorderLayout.NORTH);
        
        jLabel2.setText("The old manifest file should include both annotation and ontology files.");
                
        jPanel1.setLayout(new java.awt.GridLayout(8, 0));
                
        jPanel1.add(blankSpace);
        jPanel1.add(jLabel2);
        jPanel1.add(anotherBlankSpace);
        jPanel1.add(manifestFileName);
        jPanel1.add(selectManifestFile);
        jPanel1.add(anotherBlankSpace);
        contentPanel1.add(jPanel1, java.awt.BorderLayout.CENTER);

        jLabel1.setText("Please press the Finish button to load the files in the manifest.");
        contentPanel1.add(jLabel1, java.awt.BorderLayout.SOUTH);
        
        return contentPanel1;
    }	

	public void setComboBoxState( boolean state ) {
		spList.setEnabled( state );
	}

	public void setTextBoxState( boolean state ) {
		taxonNameBox.setEnabled( state );
	}

	
	private ImageIcon getImageIcon() {
		return null;
	}

}
