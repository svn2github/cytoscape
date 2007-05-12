package SessionExporterPlugin;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import javax.swing.JDialog;

import cytoscape.visual.VisualStyle;
import cytoscape.visual.ui.LegendDialog;

public class VisualStyleToImage
{
	public static BufferedImage convert(VisualStyle visualStyle)
	{
		JPanel legend = LegendDialog.generateLegendPanel(visualStyle);

		// What the hell is this?
		//
		// For some reason, legend's dimensions remain 0x0.
		// Inserting it into a JDialog is the only way
		// that it will be properly sized. If someone knows
		// a better way to do this, go ahead and fix it.
		//
		JDialog dialog = new JDialog();
		dialog.setContentPane(legend);
		dialog.setPreferredSize(new Dimension(400,400));
		dialog.pack();

		BufferedImage image = new BufferedImage(legend.getWidth(),
							legend.getHeight(),
							BufferedImage.TYPE_INT_RGB);
		legend.printAll(image.getGraphics());
		return image;
	}
}
