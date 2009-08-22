package org.cytoscape.view.ui.networkpanel.internal;

import java.awt.BorderLayout;
import java.awt.Image;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Simple panel to display given image in the center.
 * 
 * @author kono
 *
 */
public class ModuleGraphicsPanel extends JPanel {

	private final JLabel graphics;
	private Icon currentIcon;

	public ModuleGraphicsPanel() {
		this.graphics = new JLabel();
		this.graphics.setText("Image N/A");
		this.graphics.setHorizontalTextPosition(SwingConstants.CENTER);
		
		this.setLayout(new BorderLayout());
		this.add(graphics, SwingConstants.CENTER);
	}

	public void setGraphics(Image graphics) {
		if (graphics != null) {
			this.currentIcon = new ImageIcon(graphics);
			this.graphics.setIcon(currentIcon);
			this.graphics.setText("");
		}
	}

}
