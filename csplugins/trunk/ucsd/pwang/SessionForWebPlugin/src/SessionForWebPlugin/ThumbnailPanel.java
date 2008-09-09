package SessionForWebPlugin;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class ThumbnailPanel extends JPanel
{
		public JSpinner maxThumbnailWidthSpinner;
		public JSpinner maxThumbnailHeightSpinner;


		public ThumbnailPanel() {
			this.setLayout(new GridBagLayout());
			
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

			//JPanel thumbnailPanel = new JPanel(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.insets = new Insets(5,5,5,5);

			c.gridx = 0;		c.gridy = 0;
			c.gridwidth = 1;	c.gridheight = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 1.0;	c.weighty = 0.0;
			this.add(maxThumbnailSizeLabel, c);

			c.gridx = 0;		c.gridy = 1;
			c.gridwidth = 1;	c.gridheight = 1;
			c.fill = GridBagConstraints.BOTH;
			c.weightx = 1.0;	c.weighty = 1.0;
			this.add(maxThumbnailSizePanel, c);

			
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

}
