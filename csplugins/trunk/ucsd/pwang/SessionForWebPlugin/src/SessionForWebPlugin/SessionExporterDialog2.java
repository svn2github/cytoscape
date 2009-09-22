package SessionForWebPlugin;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import SessionForWebPlugin.ImagePanel;

/**
 * Displays a dialog with various options for
 * exporting a session.
 *
 * <p>The policy of this dialog is to be
 * completely divorced from any aspects
 * of exporting the session and information
 * about networks. Because of this, it provides
 * two actions that must be handled to have
 * a properly functioning dialog:</p>
 * <p><ol>
 * <li>ActionPerformed - this action is
 * called when the user clicks "Export"</li>
 * <li>UpdateNetworksTableListener - this
 * is called whenever the table needs to be
 * updated.</li>
 * </ol></p>
 */
public class SessionExporterDialog2 extends JDialog
{
	private JTabbedPane tabbedPane;
	private NetworksTable networksTable;
	
	private JButton exportButton;

	private ActionListener updateNetworksTableListener;

	private ImagePanel imagePanel = new ImagePanel(new GridBagLayout());
	private ThumbnailPanel thumbnailPanel = new ThumbnailPanel();
	private SpeciesPanel speciesPanel = new SpeciesPanel(this);
	private PubInfoPanel pubInfoPanel = new PubInfoPanel();
	
	public SessionExporterDialog2(Frame owner)
	{
		super(owner, "Session for CellCircuits Web Site");
		this.setLocationRelativeTo(owner);
		JLabel titleLabel = new JLabel("<html><h3>Session for CellCircuits Web Site</h3></html>");
		titleLabel.addMouseListener(new AboutAction());
		titleLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		tabbedPane = new JTabbedPane();
		tabbedPane.addChangeListener(new TabbedPaneChangeListener());

		// Networks panel
		{
			networksTable = new NetworksTable();
			JScrollPane networksScrollPane = new JScrollPane(networksTable);
			tabbedPane.add("Networks", networksScrollPane);
		}

		// species panel
		tabbedPane.add("Species", speciesPanel);
		
		// Image panel		
		tabbedPane.add("Image", imagePanel);

		// Thumbnail panel			
		tabbedPane.add("Thumbnail", thumbnailPanel);

		tabbedPane.add("Pub info", pubInfoPanel);
		
		// Buttons panel
		exportButton = new JButton("Export");
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new CancelAction());
		JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonsPanel.add(cancelButton);
		buttonsPanel.add(exportButton);

		// Add components to main panel
		{
			Container content = getContentPane();
			content.setLayout(new GridBagLayout());

			GridBagConstraints c = new GridBagConstraints();

			c.gridx = 0;		c.gridy = 0;
			c.gridwidth = 1;	c.gridheight = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 1.0;	c.weighty = 0.0;
			content.add(titleLabel, c);
			
			c.gridx = 0;		c.gridy = 1;
			c.gridwidth = 1;	c.gridheight = 1;
			c.fill = GridBagConstraints.BOTH;
			c.weightx = 1.0;	c.weighty = 1.0;
			content.add(tabbedPane, c);

			c.gridx = 0;		c.gridy = 2;
			c.gridwidth = 1;	c.gridheight = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 1.0;	c.weighty = 0.0;
			content.add(buttonsPanel, c);
		}

		pack();
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(new Dimension(500,500));
		addWindowFocusListener(new GainedFocusListener());
	}

	//
	// ----------------------------------------------
	// Classes for listening to dialog actions & events
	// ----------------------------------------------
	//
	class AboutAction extends MouseAdapter
	{
		public void mouseClicked(MouseEvent e)
		{
			cytoscape.util.OpenBrowser.openURL("http://www.cellcircuits.org");
		}
	}


	class CancelAction implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			SessionExporterDialog2.this.dispose();
		}
	}

	class GainedFocusListener extends WindowAdapter
	{
		public void windowGainedFocus(WindowEvent e)
		{
			if (tabbedPane.getSelectedIndex() == 0)		
				networksTable.fireUpdateAction();
		}
	}

	class TabbedPaneChangeListener implements ChangeListener
	{
		public void stateChanged(ChangeEvent e)
		{
			if (tabbedPane.getSelectedIndex() == 0)
				networksTable.fireUpdateAction();
		}
	}

	//
	// ----------------------------------------------
	// Methods for listening to & firing dialog events
	// ----------------------------------------------
	//
	/**
	 * This action will be executed when the "Export" button is clicked
	 */
	public void addActionListener(ActionListener l)
	{
		exportButton.addActionListener(l);
	}

	/**
	 * This action will be executed when the networks table needs
	 * to be updated.
	 */
	public void setUpdateNetworksTableListener(ActionListener l)
	{
		networksTable.setUpdateListener(l);
	}

	/**
	 * Adds the network to the networks table; this should only be called
	 * by the updateNetworksTableActionListener.
	 */
	public void addNetwork(String networkID, String networkName, int imageWidth, int imageHeight, boolean hasView)
	{
		boolean hasMem = true;
		long memoryUse = ((long) imageWidth) * ((long) imageHeight) * 4L;
		long freeMemory = Runtime.getRuntime().freeMemory();
		if (memoryUse > freeMemory)
			hasMem = false;

		byte status = 0;
		if (!hasView)
			status = NetworksTableItem.STATUS_NO_VIEW;
		else if (!hasMem)
			status = NetworksTableItem.STATUS_NOT_ENOUGH_MEM;
		else
			status = NetworksTableItem.STATUS_OK;

		NetworksTableItem item = new NetworksTableItem(networkName, imageWidth, imageHeight, status);
		networksTable.addNetwork(networkID, item);
	}

	public SessionExporterSettings getSettings()
	{
		SessionExporterSettings settings = new SessionExporterSettings();

		settings.networks = networksTable.getAllNetworksToExport();
		//if (filesToDirButton.isSelected())
		//	settings.destination = SessionExporterSettings.DESTINATION_DIRECTORY;
		//else
		//	settings.destination = SessionExporterSettings.DESTINATION_ZIP_ARCHIVE;
		settings.destination = SessionExporterSettings.DESTINATION_ZIP_ARCHIVE_4CELLCIRCUITS;
		
		//settings.numNetworksPerRow = ((Number) numNetworksPerRowSpinner.getValue()).intValue();
		//settings.doSeparateIntoPages = separateIntoPagesCheckBox.isSelected();
		//settings.numNetworksPerPage = ((Number) numNetworksPerPageSpinner.getValue()).intValue();

		//if (sortImagesComboBox.getSelectedItem().equals("Alphabetically"))
		//	settings.sortImages = SessionExporterSettings.SORT_IMAGES_ALPHABETICALLY;
		//else if (sortImagesComboBox.getSelectedItem().equals("By visual style"))
		//	settings.sortImages = SessionExporterSettings.SORT_IMAGES_BY_VISUAL_STYLE;
		//else
		//	settings.sortImages = SessionExporterSettings.SORT_IMAGES_AS_IS;

		settings.imageZoom = ((Number) imagePanel.zoomSpinner.getValue()).doubleValue();
		settings.doSetMaxImageSize = imagePanel.setMaxImageSizeCheckBox.isSelected();
		settings.maxImageWidth = ((Number) imagePanel.maxImageWidthSpinner.getValue()).intValue();
		settings.maxImageHeight = ((Number) imagePanel.maxImageHeightSpinner.getValue()).intValue();
		
		if (imagePanel.formatComboBox.getSelectedItem().equals("png"))
			settings.imageFormat = SessionExporterSettings.FORMAT_PNG;
		else
			settings.imageFormat = SessionExporterSettings.FORMAT_JPG;

		settings.maxThumbnailWidth = ((Number) thumbnailPanel.maxThumbnailWidthSpinner.getValue()).intValue();
		settings.maxThumbnailHeight = ((Number) thumbnailPanel.maxThumbnailHeightSpinner.getValue()).intValue();

		return settings;
	}
}

