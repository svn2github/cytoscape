package SessionForWebPlugin;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.text.*;

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
public class SessionExporterDialog extends JDialog
{
	private JTabbedPane tabbedPane;
	private NetworksTable networksTable;
	private NetworkSpeciesPanel speciesPanel;
	
	private JRadioButton filesToDirButton;
	private JRadioButton filesToZipButton;
	private JRadioButton filesToZipButton_4CellCircuits;
	
	private JSpinner numNetworksPerRowSpinner;
	private JCheckBox separateIntoPagesCheckBox;
	private JLabel numNetworksPerPageLabel;
	private JSpinner numNetworksPerPageSpinner;
	private JComboBox sortImagesComboBox;
	private JSpinner zoomSpinner;
	private JCheckBox setMaxImageSizeCheckBox;
	private JLabel maxImageWidthLabel;
	private JSpinner maxImageWidthSpinner;
	private JLabel maxImageHeightLabel;
	private JSpinner maxImageHeightSpinner;
	private JComboBox formatComboBox;
	private JSpinner maxThumbnailWidthSpinner;
	private JSpinner maxThumbnailHeightSpinner;
	private JButton exportButton;

	private ActionListener updateNetworksTableListener;

	public SessionExporterDialog(Frame owner)
	{
		super(owner, "Session for Web");

		// Did I handwrite this dialog?
		// You better goddamn believe it.

		JLabel titleLabel = new JLabel("<html><h3>Session for Web</h3></html>");
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

		// Species panel
		{
			speciesPanel = new NetworkSpeciesPanel();
			JScrollPane networksScrollPane = new JScrollPane(speciesPanel);
			tabbedPane.add("Species", speciesPanel);
		}

		// Files panel
		{
			JLabel filesLabel = new JLabel("Save files:");
			filesToDirButton = new JRadioButton("To a directory");
			filesToDirButton.setSelected(true);
			filesToZipButton = new JRadioButton("As a zip archive");
			filesToZipButton_4CellCircuits = new JRadioButton("As a zip archive for Cell Circuits web site");

			ButtonGroup filesGroup = new ButtonGroup();
			filesGroup.add(filesToDirButton);
			filesGroup.add(filesToZipButton);
			filesGroup.add(filesToZipButton_4CellCircuits);

			JPanel filesButtonsPanel = new JPanel(new GridBagLayout());
			filesButtonsPanel.setBorder(BorderFactory.createEmptyBorder(0,10,0,0));

			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;		c.gridy = 0;
			c.gridwidth = 1;	c.gridheight = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 1.0;	c.weighty = 0.0;
			filesButtonsPanel.add(filesToDirButton, c);

			c.gridx = 0;		c.gridy = 1;
			c.gridwidth = 1;	c.gridheight = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 1.0;	c.weighty = 0.0;
			filesButtonsPanel.add(filesToZipButton, c);

			c.gridx = 0;		c.gridy = 2;
			c.gridwidth = 1;	c.gridheight = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 1.0;	c.weighty = 0.0;
			filesButtonsPanel.add(filesToZipButton_4CellCircuits, c);
			
			JPanel filesPanel = new JPanel(new GridBagLayout());

			c.insets = new Insets(5,5,5,5);
			c.gridx = 0;		c.gridy = 0;
			c.gridwidth = 1;	c.gridheight = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 1.0;	c.weighty = 0.0;
			filesPanel.add(filesLabel, c);

			c.gridx = 0;		c.gridy = 1;
			c.gridwidth = 1;	c.gridheight = 1;
			c.fill = GridBagConstraints.NONE;
			c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.weightx = 1.0;	c.weighty = 1.0;
			filesPanel.add(filesButtonsPanel, c);

			tabbedPane.add("Files", filesPanel);
		}

		// HTML page panel
		{
			JLabel numNetworksPerRowLabel = new JLabel("Number of networks per row:");
			numNetworksPerRowSpinner = newIntSpinner(3);

			JPanel numNetworksPerRowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			numNetworksPerRowPanel.add(numNetworksPerRowLabel);
			numNetworksPerRowPanel.add(numNetworksPerRowSpinner);

			separateIntoPagesCheckBox = new JCheckBox("Separate into pages");
			separateIntoPagesCheckBox.addActionListener(new SeparateIntoPagesAction());
			numNetworksPerPageLabel = new JLabel("Number of networks per page:");
			numNetworksPerPageLabel.setEnabled(false);
			numNetworksPerPageSpinner = newIntSpinner(20);
			numNetworksPerPageSpinner.setEnabled(false);

			JPanel numNetworksPerPagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			numNetworksPerPagePanel.setBorder(BorderFactory.createEmptyBorder(0,10,0,0));
			numNetworksPerPagePanel.add(numNetworksPerPageLabel);
			numNetworksPerPagePanel.add(numNetworksPerPageSpinner);
			numNetworksPerPagePanel.setEnabled(false);

			JLabel sortImagesLabel = new JLabel("Sort images:");
			String[] sortImagesChoices = { "Alphabetically", "By visual style", "As is" };
			sortImagesComboBox = new JComboBox(sortImagesChoices);
			JPanel sortImagesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			sortImagesPanel.add(sortImagesLabel);
			sortImagesPanel.add(sortImagesComboBox);

			JPanel htmlPagePanel = new JPanel(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();

			c.gridx = 0;		c.gridy = 0;
			c.gridwidth = 1;	c.gridheight = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 1.0;	c.weighty = 0.0;
			htmlPagePanel.add(numNetworksPerRowPanel, c);

			c.gridx = 0;		c.gridy = 1;
			c.gridwidth = 1;	c.gridheight = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 1.0;	c.weighty = 0.0;
			htmlPagePanel.add(separateIntoPagesCheckBox, c);

			c.gridx = 0;		c.gridy = 2;
			c.gridwidth = 1;	c.gridheight = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 1.0;	c.weighty = 0.0;
			htmlPagePanel.add(numNetworksPerPagePanel, c);
			
			c.gridx = 0;		c.gridy = 3;
			c.gridwidth = 1;	c.gridheight = 1;
			c.fill = GridBagConstraints.NONE;
			c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.weightx = 1.0;	c.weighty = 1.0;
			htmlPagePanel.add(sortImagesPanel, c);

			tabbedPane.add("HTML Page", htmlPagePanel);
		}

		// Image panel
		{
			JLabel zoomLabel = new JLabel("Zoom:");
			zoomSpinner = newDoubleSpinner(1.0);
			JPanel zoomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			zoomPanel.add(zoomLabel);
			zoomPanel.add(zoomSpinner);

			setMaxImageSizeCheckBox = new JCheckBox("Set maximum image size:");
			setMaxImageSizeCheckBox.addActionListener(new SetMaxImageSizeAction());
			maxImageWidthLabel = new JLabel("Width: ");
			maxImageWidthLabel.setEnabled(false);
			maxImageWidthSpinner = newIntSpinner(1000);
			maxImageWidthSpinner.setEnabled(false);
			maxImageHeightLabel = new JLabel("Height:");
			maxImageHeightLabel.setEnabled(false);
			maxImageHeightSpinner = newIntSpinner(1000);
			maxImageHeightSpinner.setEnabled(false);

			JPanel maxImageSizePanel = new JPanel(new GridBagLayout());
			{
				JPanel maxImageWidthPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
				{
					maxImageWidthPanel.add(maxImageWidthLabel);
					maxImageWidthPanel.add(maxImageWidthSpinner);
				}

				JPanel maxImageHeightPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
				{
					maxImageHeightPanel.add(maxImageHeightLabel);
					maxImageHeightPanel.add(maxImageHeightSpinner);
				}

				GridBagConstraints c = new GridBagConstraints();

				c.gridx = 0;		c.gridy = 0;
				c.gridwidth = 1;	c.gridheight = 1;
				c.fill = GridBagConstraints.HORIZONTAL;
				c.weightx = 1.0;	c.weighty = 0.0;
				maxImageSizePanel.add(maxImageWidthPanel, c);
				
				c.gridx = 0;		c.gridy = 1;
				c.gridwidth = 1;	c.gridheight = 1;
				c.fill = GridBagConstraints.HORIZONTAL;
				c.weightx = 1.0;	c.weighty = 0.0;
				maxImageSizePanel.add(maxImageHeightPanel, c);
			}
			maxImageSizePanel.setBorder(BorderFactory.createEmptyBorder(0,10,0,0));

			JLabel formatLabel = new JLabel("Format:");
			String[] formatChoices = { "png", "jpg" };
			formatComboBox = new JComboBox(formatChoices);
			JPanel formatPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			formatPanel.add(formatLabel);
			formatPanel.add(formatComboBox);

			JPanel imagePanel = new JPanel(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();

			c.gridx = 0;		c.gridy = 0;
			c.gridwidth = 1;	c.gridheight = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 1.0;	c.weighty = 0.0;
			imagePanel.add(zoomPanel, c);

			c.gridx = 0;		c.gridy = 1;
			c.gridwidth = 1;	c.gridheight = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 1.0;	c.weighty = 0.0;
			imagePanel.add(setMaxImageSizeCheckBox , c);

			c.gridx = 0;		c.gridy = 2;
			c.gridwidth = 1;	c.gridheight = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 1.0;	c.weighty = 0.0;
			imagePanel.add(maxImageSizePanel, c);
			
			c.gridx = 0;		c.gridy = 3;
			c.gridwidth = 1;	c.gridheight = 1;
			c.fill = GridBagConstraints.NONE;
			c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.weightx = 1.0;	c.weighty = 1.0;
			imagePanel.add(formatPanel, c);

			tabbedPane.add("Image", imagePanel);
		}

		// Thumbnail panel
		{
			JLabel maxThumbnailSizeLabel = new JLabel("Maximum size:");
			JLabel maxThumbnailWidthLabel = new JLabel("Width: ");
			maxThumbnailWidthSpinner = newIntSpinner(300);
			JLabel maxThumbnailHeightLabel = new JLabel("Height:");
			maxThumbnailHeightSpinner = newIntSpinner(300);
			JPanel maxThumbnailSizePanel = new JPanel(new GridBagLayout());
			{
				JPanel maxThumbnailWidthPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
				{
					maxThumbnailWidthPanel.add(maxThumbnailWidthLabel);
					maxThumbnailWidthPanel.add(maxThumbnailWidthSpinner);
				}

				JPanel maxThumbnailHeightPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
				{
					maxThumbnailHeightPanel.add(maxThumbnailHeightLabel);
					maxThumbnailHeightPanel.add(maxThumbnailHeightSpinner);
				}

				GridBagConstraints c = new GridBagConstraints();

				c.gridx = 0;		c.gridy = 0;
				c.gridwidth = 1;	c.gridheight = 1;
				c.fill = GridBagConstraints.HORIZONTAL;
				c.weightx = 1.0;	c.weighty = 0.0;
				maxThumbnailSizePanel.add(maxThumbnailWidthPanel, c);
				
				c.gridx = 0;		c.gridy = 1;
				c.gridwidth = 1;	c.gridheight = 1;
				c.fill = GridBagConstraints.BOTH;
				c.weightx = 1.0;	c.weighty = 1.0;
				maxThumbnailSizePanel.add(maxThumbnailHeightPanel, c);
			}
			maxThumbnailSizePanel.setBorder(BorderFactory.createEmptyBorder(0,10,0,0));

			JPanel thumbnailPanel = new JPanel(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.insets = new Insets(5,5,5,5);

			c.gridx = 0;		c.gridy = 0;
			c.gridwidth = 1;	c.gridheight = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 1.0;	c.weighty = 0.0;
			thumbnailPanel.add(maxThumbnailSizeLabel, c);

			c.gridx = 0;		c.gridy = 1;
			c.gridwidth = 1;	c.gridheight = 1;
			c.fill = GridBagConstraints.BOTH;
			c.weightx = 1.0;	c.weighty = 1.0;
			thumbnailPanel.add(maxThumbnailSizePanel, c);
			
			tabbedPane.add("Thumbnail", thumbnailPanel);
		}
		
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
	// Methods for creating spinners
	// ----------------------------------------------
	//

	private JSpinner newDoubleSpinner(double defaultValue)
	{
		return newNumberSpinner(new SpinnerNumberModel(defaultValue, Double.MIN_VALUE, Double.POSITIVE_INFINITY, 0.1));
	}

	private JSpinner newIntSpinner(int defaultValue)
	{
		return newNumberSpinner(new SpinnerNumberModel(defaultValue, 1, Integer.MAX_VALUE, 1));
	}

	private JSpinner newNumberSpinner(SpinnerNumberModel numberModel)
	{
		JSpinner spinner = new JSpinner();
		spinner.setModel(numberModel);
		new JSpinner.NumberEditor(spinner);
		((JSpinner.DefaultEditor)spinner.getEditor()).getTextField().setColumns(4);
		return spinner;
	}

	//
	// ----------------------------------------------
	// Classes for listening to dialog actions & events
	// ----------------------------------------------
	//

	class SeparateIntoPagesAction implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			boolean value = separateIntoPagesCheckBox.isSelected();
			numNetworksPerPageLabel.setEnabled(value);
			numNetworksPerPageSpinner.setEnabled(value);
		}
	}

	class AboutAction extends MouseAdapter
	{
		public void mouseClicked(MouseEvent e)
		{
			AboutDialog dialog = new AboutDialog(null);
			dialog.setVisible(true);
		}
	}

	class SetMaxImageSizeAction implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			boolean value = setMaxImageSizeCheckBox.isSelected();
			maxImageWidthLabel.setEnabled(value);
			maxImageWidthSpinner.setEnabled(value);
			maxImageHeightLabel.setEnabled(value);
			maxImageHeightSpinner.setEnabled(value);
		}
	}

	class CancelAction implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			SessionExporterDialog.this.dispose();
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
	public void addNetwork(String networkID, String networkName, String species, int imageWidth, int imageHeight, boolean hasView)
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

		NetworksTableItem item = new NetworksTableItem(networkName, species, imageWidth, imageHeight, status);
		networksTable.addNetwork(networkID, item);
	}

	public SessionExporterSettings getSettings()
	{
		SessionExporterSettings settings = new SessionExporterSettings();

		settings.networks = networksTable.getAllNetworksToExport();
		if (filesToDirButton.isSelected())
			settings.destination = SessionExporterSettings.DESTINATION_DIRECTORY;
		else if (filesToZipButton.isSelected())
			settings.destination = SessionExporterSettings.DESTINATION_ZIP_ARCHIVE;
		else if (filesToZipButton_4CellCircuits.isSelected()) {
			settings.destination = SessionExporterSettings.DESTINATION_ZIP_ARCHIVE_4CELLCIRCUITS;			
		}

		settings.numNetworksPerRow = ((Number) numNetworksPerRowSpinner.getValue()).intValue();
		settings.doSeparateIntoPages = separateIntoPagesCheckBox.isSelected();
		settings.numNetworksPerPage = ((Number) numNetworksPerPageSpinner.getValue()).intValue();

		if (sortImagesComboBox.getSelectedItem().equals("Alphabetically"))
			settings.sortImages = SessionExporterSettings.SORT_IMAGES_ALPHABETICALLY;
		else if (sortImagesComboBox.getSelectedItem().equals("By visual style"))
			settings.sortImages = SessionExporterSettings.SORT_IMAGES_BY_VISUAL_STYLE;
		else
			settings.sortImages = SessionExporterSettings.SORT_IMAGES_AS_IS;

		settings.imageZoom = ((Number) zoomSpinner.getValue()).doubleValue();
		settings.doSetMaxImageSize = setMaxImageSizeCheckBox.isSelected();
		settings.maxImageWidth = ((Number) maxImageWidthSpinner.getValue()).intValue();
		settings.maxImageHeight = ((Number) maxImageHeightSpinner.getValue()).intValue();

		if (formatComboBox.getSelectedItem().equals("png"))
			settings.imageFormat = SessionExporterSettings.FORMAT_PNG;
		else
			settings.imageFormat = SessionExporterSettings.FORMAT_JPG;

		settings.maxThumbnailWidth = ((Number) maxThumbnailWidthSpinner.getValue()).intValue();
		settings.maxThumbnailHeight = ((Number) maxThumbnailHeightSpinner.getValue()).intValue();

		return settings;
	}
}

// ****************************************************************************
//
//                                  AboutDialog
//
// ****************************************************************************

class AboutDialog extends JDialog
{
	public AboutDialog(Frame owner)
	{
		super(owner, "About Session for Web");
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		ImageIcon aboutIcon = new ImageIcon(getClass().getResource("SessionForWeb.png"));
		JLabel iconLabel = new JLabel(aboutIcon);
		JLabel authorLabel = new JLabel("Author: Samad Lotia");
		JLabel versionLabel = new JLabel("Version: 3.0");
		JButton closeButton = new JButton(new AbstractAction("Close")
		{
			public void actionPerformed(ActionEvent e)
			{
				AboutDialog.this.dispose();
			}
		});
		JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonsPanel.add(closeButton);
		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(10,10,10,10);

		c.gridx = 0;		c.gridy = 0;
		c.weightx = 1.0;	c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		add(iconLabel, c);
		
		c.gridx = 0;		c.gridy = 1;
		c.weightx = 1.0;	c.weighty = 0.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		add(authorLabel, c);
		
		c.gridx = 0;		c.gridy = 2;
		c.weightx = 1.0;	c.weighty = 0.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		add(versionLabel, c);
		
		c.gridx = 0;		c.gridy = 3;
		c.weightx = 1.0;	c.weighty = 0.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.LAST_LINE_END;
		add(buttonsPanel, c);

		pack();
	}
}

// ****************************************************************************
//
//                                   NetworksTable
//
// ****************************************************************************

/**
 * Manages the networks table.
 * Because JTable is extremely flexible, it requires a lot of code to do
 * relatively basic things. This class takes care of the following
 * aspects of the table:
 *
 * 1) Appearance: column widths are specified and all cells
 *    except those in the Export column are uneditable.
 * 2) Model: manages the contents of the table appropriately.
 * 3) Status cell rendering: because one cannot change the text color
 *    of a cell, a custom rendering class must be provided to do this.
 * 4) Mouse events: one cannot ask the JTable to dispatch an event
 *    when a certain cell has been clicked. This is done at a lower level.
 * 5) Dispatching update events
 */
class NetworksTable extends JTable
{
	private static final String[] columnNames   = {"Export?", "Network", "Species","Image Size", "Status"}; 
	private static final Class[]  columnClasses = { Boolean.class, String.class,String.class, String.class, String.class };

	private ActionListener updateListener;
	private LinkedList<NetworksTableItem> tableItems = new LinkedList<NetworksTableItem>();
	private HashMap<String,NetworksTableItem> networkIDToTableItemMap = new HashMap<String,NetworksTableItem>();

	public NetworksTable()
	{
		setModel(new NetworksTableModel());
		getColumnModel().getColumn(4).setCellRenderer(new StatusCellRenderer());
		getColumnModel().getColumn(0).setMaxWidth(60);
		addMouseListener(new NetworksTableMouseListener());
		addMouseMotionListener(new NetworksTableMouseMotionListener());
	}

	public void addNetwork(String networkID, NetworksTableItem item)
	{
		tableItems.add(item);
		networkIDToTableItemMap.put(networkID, item);
	}

	public void setUpdateListener(ActionListener l)
	{
		updateListener = l;
		fireUpdateAction();
	}

	public void fireUpdateAction()
	{
		if (updateListener == null)
			return;

		HashMap<String,NetworksTableItem> oldIDToItemMap = networkIDToTableItemMap; 
		networkIDToTableItemMap = new HashMap<String,NetworksTableItem>();
		tableItems.clear();

		updateListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "updateNetworksTable"));

		for (String networkID : networkIDToTableItemMap.keySet())
			if (oldIDToItemMap.containsKey(networkID))
				networkIDToTableItemMap.get(networkID).setExport(oldIDToItemMap.get(networkID).doExport());
		
		repaint();
	}

	public ArrayList<String> getAllNetworksToExport()
	{
		ArrayList<String> networks = new ArrayList<String>();
		for (String networkID : networkIDToTableItemMap.keySet())
			if (networkIDToTableItemMap.get(networkID).doExport())
				networks.add(networkID);
		return networks;
	}

	class NetworksTableModel extends AbstractTableModel
	{

		public String getColumnName(int c)
		{
			return columnNames[c];
		}

		public int getRowCount()
		{
			return tableItems.size();
		}

		public int getColumnCount()
		{
			return columnNames.length;
		}

		public Class getColumnClass(int c)
		{
			return columnClasses[c];
		}

		public Object getValueAt(int row, int col)
		{
			NetworksTableItem item = tableItems.get(row);
			if (col == 0)
				return item.doExport();
			else if (col == 1)
				return item.getNetworkName();
			else if (col == 2)
				return item.getSpecies();
			else if (col == 3)
				return item.getImageSize();
			else
				return null;
		}

		public boolean isCellEditable(int row, int col)
		{
			NetworksTableItem item = tableItems.get(row);
			if (col != 0)
				return false;
			return item.isEnabled();
		}

		public void setValueAt(Object value, int row, int col)
		{
			if (col == 0)
			{
				NetworksTableItem item = tableItems.get(row);
				item.setExport(((Boolean) value).booleanValue());
				NetworksTable.this.repaint();
			}
		}
	}

	class StatusCellRenderer extends JLabel implements TableCellRenderer
	{
		public Component getTableCellRendererComponent(	JTable table,
								Object value,
								boolean isSelected,
								boolean hasFocus,
								int row, int column)
		{
			NetworksTableItem item = tableItems.get(row);
			setText(item.getStatusShortMsg());
			setOpaque(true);
			setForeground(item.getStatusColor());
			setBackground(Color.WHITE);
			setToolTipText("Click on the status for a more detailed explanation.");
			return this;
		}
	}

	// Cannot use a MouseListener to the TableCellRenderer as JTable does not dispatch
	// mouse events to the renderer
	class NetworksTableMouseListener extends MouseAdapter
	{
		public void mouseClicked(MouseEvent e)
		{
			int row = NetworksTable.this.rowAtPoint(e.getPoint());
			int col = NetworksTable.this.convertColumnIndexToModel(NetworksTable.this.columnAtPoint(e.getPoint()));
			if (col == 3)
			{
				NetworksTableItem item = tableItems.get(row);
				new StatusDialog(item, null);
			}
		}
	}

	// Cannot use a MouseMotionListener to the TableCellRenderer as JTable does not dispatch
	// mouse events to the renderer
	class NetworksTableMouseMotionListener extends MouseMotionAdapter
	{
		public void mouseMoved(MouseEvent e)
		{
			int col = NetworksTable.this.convertColumnIndexToModel(NetworksTable.this.columnAtPoint(e.getPoint()));
			if (col == 3)
				NetworksTable.this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			else
				NetworksTable.this.setCursor(Cursor.getDefaultCursor());
		}
	}
}

// ****************************************************************************
//
//                             NetworksTableItem
//
// ****************************************************************************

/**
 * Specifies an entry in a NetworksTable.
 * Status messages of a network is stored in the StatusMsgs.properties
 * file. This class looks up status message information from this file.
 */
class NetworksTableItem
{
	private static ResourceBundle statusMsgs = ResourceBundle.getBundle("StatusMsgs");

	public static final byte STATUS_OK		= 0;
	public static final byte STATUS_NOT_ENOUGH_MEM	= 1;
	public static final byte STATUS_NO_VIEW		= 2;

	private boolean enabled;
	private boolean export;
	private String networkName;
	private String species;
	private int imageWidth;
	private int imageHeight;
	private byte status;

	public NetworksTableItem(String networkName, String species, int imageWidth, int imageHeight, byte status)
	{
		this.networkName = networkName;
		this.species = species;		
		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
		this.status = status;

		if (status == STATUS_OK || status == STATUS_NOT_ENOUGH_MEM)
		{
			enabled = true;
			export = true;
		}
		else if (status == STATUS_NO_VIEW)
		{
			enabled = false;
			export = false;
		}
	}

	public boolean isEnabled()
	{
		return enabled;
	}

	public boolean doExport()
	{
		if (status == STATUS_NO_VIEW)
			return false;
		else
			return export;
	}

	public void setExport(boolean export)
	{
		this.export = export;
	}

	public String getNetworkName()
	{
		return networkName;
	}

	public String getSpecies()
	{
		return species;
	}

	public String getImageSize()
	{
		if (status == STATUS_NO_VIEW)
			return "n/a";
		else
			return imageWidth + "x" + imageHeight;
	}

	public long getImageMemoryUse()
	{
		return ((long) imageWidth) * ((long) imageHeight) * 4L;
	}

	private String getKeyPrefix()
	{
		String keyPrefix = null;
		if (status == STATUS_OK)
			keyPrefix = "ok";
		else if (status == STATUS_NOT_ENOUGH_MEM)
			keyPrefix = "not_enough_mem";
		else if (status == STATUS_NO_VIEW)
			keyPrefix = "no_view";
		if (enabled == true && export == false)
			keyPrefix = "no_export";
		return keyPrefix;
	}

	public Color getStatusColor()
	{
		return Color.decode(statusMsgs.getString(getKeyPrefix() + ".color"));
	}

	public String getStatusShortMsg()
	{
		return statusMsgs.getString(getKeyPrefix() + ".short");
	}

	public String getStatusLongMsg()
	{
		StringBuffer longMsg = new StringBuffer(statusMsgs.getString(getKeyPrefix() + ".long"));
		String memneededReplace = "%memneeded";
		int memneededIndex = longMsg.indexOf(memneededReplace);
		if (memneededIndex >= 0)
		{
			long memneeded = ((long) imageWidth) * ((long) imageHeight) * 4L;
			longMsg.replace(memneededIndex, memneededIndex + memneededReplace.length(), formatBytes(memneeded));
		}
		String availmemReplace = "%availmem";
		int availmemIndex = longMsg.indexOf(availmemReplace);
		if (availmemIndex >= 0)
		{
			long availmem = Runtime.getRuntime().freeMemory();
			longMsg.replace(availmemIndex, availmemIndex + availmemReplace.length(), formatBytes(availmem));
		}
		return longMsg.toString();
	}

	private String formatBytes(long bytes)
	{
		final long kilobyte = 1024L;
		final long megabyte = 1024L * kilobyte;
		final long gigabyte = 1024L * megabyte;

		String suffix;
		double value;

		if (bytes > gigabyte)
		{
			value = ((double) bytes) / gigabyte;
			suffix = "GB";
		}
		else if (bytes > megabyte)
		{
			value = ((double) bytes) / megabyte;
			suffix = "MB";
		}
		else if (bytes > kilobyte)
		{
			value = ((double) bytes) / kilobyte;
			suffix = "KB";
		}
		else
		{
			value = (double) bytes;
			suffix = "B";
		}

		NumberFormat format = NumberFormat.getNumberInstance();
		format.setMaximumFractionDigits(2);
		return format.format(value) + " " + suffix;
	}
}

// ****************************************************************************
//
//                                StatusDialog
//
// ****************************************************************************

/**
 * Displays detailed information about the status of a network.
 */
class StatusDialog extends JDialog
{
	public StatusDialog(NetworksTableItem item, Frame owner)
	{
		super(owner, "Session For Web: Status for " + item.getNetworkName());
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		JLabel titleText = new JLabel("<html><h1>Status for " + item.getNetworkName() + "</h1></html>");
		JEditorPane helpText = new JEditorPane("text/html", item.getStatusLongMsg());
		helpText.setEditable(false);
		JScrollPane helpPane = new JScrollPane(helpText);
		helpPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				StatusDialog.this.dispose();
			}
		});
		JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonsPanel.add(closeButton);

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5,5,5,5);

		c.gridx = 0;		c.gridy = 0;
		c.gridwidth = 1;	c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;	c.weighty = 0.0;
		add(titleText, c);

		c.gridx = 0;		c.gridy = 1;
		c.gridwidth = 1;	c.gridheight = 1;
		c.weightx = 1.0;	c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		add(helpPane, c);

		c.gridx = 0;		c.gridy = 2;
		c.gridwidth = 1;	c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;	c.weighty = 0.0;
		add(buttonsPanel, c);

		pack();
		setVisible(true);
	}
}

