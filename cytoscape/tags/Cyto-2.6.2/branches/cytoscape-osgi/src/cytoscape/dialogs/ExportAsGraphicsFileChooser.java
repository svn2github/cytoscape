package cytoscape.dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import cytoscape.Cytoscape;
import cytoscape.util.FileUtil;
import cytoscape.util.CyFileFilter;

/**
 * Dialog that chooses file to export to.
 * @author Samad Lotia
 */
public class ExportAsGraphicsFileChooser extends JDialog
{
	protected JTextField filePathField;
	protected JComboBox formatComboBox;
	protected JButton okButton;
	protected File selectedFile;

	public ExportAsGraphicsFileChooser(CyFileFilter[] formats)
	{
		super(Cytoscape.getDesktop(), "Export Network View as Graphics");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		Container content = getContentPane();
		content.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5,5,5,5);

		JLabel selectFileLabel = new JLabel("File to export to:");
		c.gridx = 0;			c.gridy = 0;
		c.gridwidth = 2;		c.gridheight = 1;
		c.weightx = 1.0;		c.weighty = 0.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		content.add(selectFileLabel, c);

		filePathField = new JTextField();
		filePathField.setColumns(30);
		filePathField.setEditable(false);
		c.gridx = 0;			c.gridy = 1;
		c.gridwidth = 1;		c.gridheight = 1;
		c.weightx = 1.0;		c.weighty = 0.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		content.add(filePathField, c);

		JButton chooseFileButton = new JButton("Choose...");
		chooseFileButton.addActionListener(new ChooseFileAction());
		c.gridx = 1;			c.gridy = 1;
		c.gridwidth = 1;		c.gridheight = 1;
		c.weightx = 0.0;		c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		content.add(chooseFileButton, c);

		JLabel formatLabel = new JLabel("Format:  ");
		formatComboBox = new JComboBox(formats);
		formatComboBox.addActionListener(new FormatChangedAction());
		JPanel formatPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		formatPanel.add(formatLabel);
		formatPanel.add(formatComboBox);
		c.gridx = 0;			c.gridy = 2;
		c.gridwidth = 2;		c.gridheight = 1;
		c.weightx = 1.0;		c.weighty = 0.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		content.add(formatPanel, c);

		JSeparator separator = new JSeparator();
		c.gridx = 0;			c.gridy = 3;
		c.gridwidth = 2;		c.gridheight = 1;
		c.weightx = 1.0;		c.weighty = 0.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		content.add(separator, c);

		okButton = new JButton("   OK   ");
		okButton.setDefaultCapable(true);
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new CancelAction());
		JPanel buttonsPanel = new JPanel(new FlowLayout());
		buttonsPanel.add(cancelButton);
		buttonsPanel.add(okButton);
		c.gridx = 0;			c.gridy = 4;
		c.gridwidth = 2;		c.gridheight = 1;
		c.weightx = 1.0;		c.weighty = 1.0;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.SOUTHEAST;
		content.add(buttonsPanel, c);

		formatComboBox.setSelectedIndex(0);
		removeFile();

		setLocationRelativeTo(Cytoscape.getDesktop());
		pack();
	}

	public CyFileFilter getSelectedFormat()
	{
		return (CyFileFilter) formatComboBox.getSelectedItem();
	}

	public File getSelectedFile()
	{
		return selectedFile;
	}

	public void addActionListener(ActionListener l)
	{
		okButton.addActionListener(l);
	}

	protected void assignFile(File file)
	{
		selectedFile = file;
		filePathField.setText(selectedFile.getPath());
		okButton.setEnabled(true);
	}

	protected void removeFile()
	{
		selectedFile = null;
		filePathField.setText("");
		okButton.setEnabled(false);
	}

	protected void updateExtension()
	{
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
	}

	private class ChooseFileAction implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			CyFileFilter filter = getSelectedFormat();
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

	private class CancelAction implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			ExportAsGraphicsFileChooser.this.dispose();
		}
	}

	private class FormatChangedAction implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			updateExtension();
		}
	}
}
