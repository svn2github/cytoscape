package SessionForWebPlugin;

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
		if (visualStyle == null)
			return null;

		JPanel legend = new JPanel();
		legend.setLayout(new javax.swing.BoxLayout(legend, javax.swing.BoxLayout.Y_AXIS));
		legend.setBackground(java.awt.Color.white);

		cytoscape.visual.NodeAppearanceCalculator nac = visualStyle.getNodeAppearanceCalculator();
		java.util.List<cytoscape.visual.calculators.Calculator> calcs = nac.getCalculators();
		for ( cytoscape.visual.calculators.Calculator calc : calcs ) {

			// AAARGH
			if ( nac.getNodeSizeLocked() ) {
				if ( calc.getType() == cytoscape.visual.ui.VizMapUI.NODE_WIDTH ) 
					continue;
				else if ( calc.getType() == cytoscape.visual.ui.VizMapUI.NODE_HEIGHT ) 
					continue;
			} else {
				if ( calc.getType() == cytoscape.visual.ui.VizMapUI.NODE_SIZE ) 
					continue;
			}


			cytoscape.visual.mappings.ObjectMapping om = calc.getMapping(0);
			JPanel mleg = om.getLegend(calc.getTypeName(),calc.getType()); 
			// Add passthrough mappings to the top since they don't
			// display anything besides the title.
			if ( om instanceof cytoscape.visual.mappings.PassThroughMapping )
				legend.add( mleg, 0 ); 
			else
				legend.add( mleg ); 
		}

		cytoscape.visual.EdgeAppearanceCalculator eac = visualStyle.getEdgeAppearanceCalculator();
		calcs = eac.getCalculators();
		int top = legend.getComponentCount(); 
		for ( cytoscape.visual.calculators.Calculator calc : calcs ) {
			cytoscape.visual.mappings.ObjectMapping om = calc.getMapping(0);
			JPanel mleg = om.getLegend(calc.getTypeName(),calc.getType()); 
			// Add passthrough mappings to the top since they don't
			// display anything besides the title.
			if ( om instanceof cytoscape.visual.mappings.PassThroughMapping )
				legend.add( mleg, top ); 
			else
				legend.add( mleg ); 
		}

		// What the hell is this?
		//
		// For some reason, legend's dimensions remain 0 by 0.
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
