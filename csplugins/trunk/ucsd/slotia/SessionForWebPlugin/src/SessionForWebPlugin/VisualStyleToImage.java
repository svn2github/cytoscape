package SessionForWebPlugin;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import javax.swing.JDialog;
import javax.swing.JScrollPane;

import cytoscape.visual.VisualStyle;
import cytoscape.visual.ui.LegendDialog;

public class VisualStyleToImage
{
	public static BufferedImage convert(VisualStyle visualStyle)
	{
		if (visualStyle == null)
			return null;

		JPanel legend = LegendDialog.generateLegendPanel(visualStyle);

		//
		// What the hell is this?
		//
		// A JPanel and its components remain at size (0,0)
		// if it is not placed within a JFrame or JDialog.
		// Inserting it into a JDialog is the only way
		// that it will be properly sized. If someone knows
		// a better way to do this, go ahead and fix it.
		// Moreover, the legend panel is placed in a
		// JScrollPane to ensure the entire legend
		// is converted.
		//
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(legend);
		JDialog dialog = new JDialog();
		dialog.setContentPane(scrollPane);
		dialog.setPreferredSize(new Dimension(400,400));
		System.out.println(dialog.getSize());
		System.out.println(dialog.getMaximumSize());
		dialog.pack();

		BufferedImage image = new BufferedImage(legend.getWidth(),
							legend.getHeight(),
							BufferedImage.TYPE_INT_RGB);
		legend.printAll(image.getGraphics());
		return image;
	}
}
