/*
  File: CreditScreenImpl.java

  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

  The Cytoscape Consortium is:
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Institut Pasteur
  - Agilent Technologies

  This library is free software; you can redistribute it and/or modify it
  under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation; either version 2.1 of the License, or
  any later version.

  This library is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
  documentation provided hereunder is on an "as is" basis, and the
  Institute for Systems Biology and the Whitehead Institute
  have no obligations to provide maintenance, support,
  updates, enhancements or modifications.  In no event shall the
  Institute for Systems Biology and the Whitehead Institute
  be liable to any party for direct, indirect, special,
  incidental or consequential damages, including lost profits, arising
  out of the use of this software and its documentation, even if the
  Institute for Systems Biology and the Whitehead Institute
  have been advised of the possibility of such damage.  See
  the GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/
package org.cytoscape.splash.internal; 

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Color;
import java.util.List;

import org.cytoscape.splash.CreditScreen;

/**
 *
 */
public class CreditScreenImpl extends AbstractScreen implements CreditScreen {

	private Timer timer;
	private ImageIcon image;
	private List<String> lines;

	public CreditScreenImpl(ImageIcon image, List<String> lines) {
		super();
		this.image = image;
		this.lines = lines;
	}

	public void showCredits() {
		window = new JWindow();
		final ScrollingLinesPanel panel = new ScrollingLinesPanel(image, lines);
		window.add(panel);
		window.pack();
		window.validate();
		window.setPreferredSize(panel.getPreferredSize());
		window.requestFocusInWindow();
		centerWindowLocation(window);
		window.setAlwaysOnTop(true);
		window.setVisible(true);

		Action scrollText = new AbstractAction() {
			private final static long serialVersionUID = 1202340446391603L;
			boolean shouldDraw = false;

			public void actionPerformed(ActionEvent e) {
				panel.incrementYPos();
				window.repaint();
			}
		};

		timer = new Timer(100, scrollText);

		window.addMouseListener(new MouseListener() {
				public void mouseClicked(MouseEvent e) {
					hideCredits();
				}
				public void mouseEntered(MouseEvent e) { }
				public void mouseExited(MouseEvent e) { }
				public void mousePressed(MouseEvent e) { }
				public void mouseReleased(MouseEvent e) { }
			});

		timer.start();
	}

	public void hideCredits() {
		hideScreen();
		if ( timer != null )
			timer.stop();
	}

	private static class ScrollingLinesPanel extends JPanel {
		private final static long serialVersionUID = 1202339874718767L;
		int yPos;
		int xPos;
		ImageIcon background;
		List<String> lines;

		public ScrollingLinesPanel(ImageIcon background, List<String> lines) {
			super();
			this.background = background;
			this.lines = lines;
			yPos = background.getIconHeight();
			xPos = (int) ((float) background.getIconWidth() / 2.0f);
			setOpaque(false);
			setPreferredSize(new Dimension(background.getIconWidth(), background.getIconHeight()));
		}

		protected void paintComponent(Graphics g) {
			g.drawImage(background.getImage(), 0, 0, null);
			((Graphics2D) g).setPaint(Color.white);

			int i = 1;
			int y = yPos;

			for ( String sub : lines ) {
				y = yPos + (12 * i);

				if (y > 80)
					g.drawString(sub, xPos, y);

				i++;
			}

			super.paintComponent(g);
		}

		public void incrementYPos() {
			yPos -= 2;
		}
	}
}
