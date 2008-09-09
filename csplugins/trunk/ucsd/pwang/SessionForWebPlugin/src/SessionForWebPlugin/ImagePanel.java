package SessionForWebPlugin;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class ImagePanel extends JPanel
{
	public JSpinner zoomSpinner;
	public JCheckBox setMaxImageSizeCheckBox;
	private JLabel maxImageWidthLabel;
	public JSpinner maxImageWidthSpinner;
	private JLabel maxImageHeightLabel;
	public JSpinner maxImageHeightSpinner;
	public JComboBox formatComboBox;
	
	public ImagePanel(GridBagLayout pLayout) {
		this.setLayout(pLayout);
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

		//JPanel imagePanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;		c.gridy = 0;
		c.gridwidth = 1;	c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;	c.weighty = 0.0;
		this.add(zoomPanel, c);

		c.gridx = 0;		c.gridy = 1;
		c.gridwidth = 1;	c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;	c.weighty = 0.0;
		this.add(setMaxImageSizeCheckBox , c);

		c.gridx = 0;		c.gridy = 2;
		c.gridwidth = 1;	c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;	c.weighty = 0.0;
		this.add(maxImageSizePanel, c);
		
		c.gridx = 0;		c.gridy = 3;
		c.gridwidth = 1;	c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.weightx = 1.0;	c.weighty = 1.0;
		this.add(formatPanel, c);

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
}
