// $Id$
//------------------------------------------------------------------------------
/** Copyright (c) 2007 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami, Benjamin Gross
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander, Benjamin Gross
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package org.mskcc.pathway_commons.view;

// imports
import java.util.Timer;
import java.util.TimerTask;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.BorderLayout;
import java.awt.AlphaComposite;
import javax.swing.border.LineBorder;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import javax.swing.JComponent;


/**
 * Popup Panel to show protein/pathway/interaction info.
 * Provides fade-in / fade-out transitions.
 *
 * @author Benjamin Gross
 */
public class PopupPanel extends JPanel {
	
	/**
	 * ref to ourself - used by animation timer
	 */
	private PopupPanel m_popupPanel;

	/**
	 * current level of opacity
	 */
	private int m_opacity;

	/**
	 * our owner - used by animation timer
	 */
	private JComponent m_owner;

	/**
	 * component that contains content we fade-in/out to
	 */
	private JComponent m_wrapped_component;
	
	/**
	 * timer used for animations.
	 */
	private Timer m_timer;

	/**
	 * ref to our border color
	 */
	private Color m_border_color;

	/**
	 * Constructor.
	 */
	public PopupPanel(JComponent owner, JComponent component, Color border_color) {

		// init member vars
		m_owner = owner;
		m_popupPanel = this;
		m_wrapped_component = component;
		m_border_color = border_color;

		// follow lines are needed to properly render component
		// underneath curtain - for cool fade-in effect
		setLayout(new BorderLayout());
		add(m_wrapped_component, BorderLayout.CENTER);
		setVisible(false);

		// if we don't do the follow, swing will render 
		// the component opaque automatically
		m_wrapped_component.setVisible(false);
	}

	/**
	 * Set opacity level of component - used for transitions.
	 */
	public void setOpacity(int opacity) {
		m_opacity = opacity;
	}

	/**
	 * Set border of this component, given opacity level.
	 */
	public void setBorder(int opacity) {
		Color newColor = new Color(m_border_color.getRed(), m_border_color.getGreen(),
								   m_border_color.getBlue(), (float)(opacity/255.0));
		setBorder(new LineBorder(newColor));
	}

	/**
	 * Our implementation of set bounds.
	 */
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		m_wrapped_component.setBounds(x, y, width, height);
	}

	/**
	 * Our implementation of paint component.
	 */
	public void paintComponent(Graphics g) {

		Graphics2D image2D = (Graphics2D)g;

		// the draw wrapper component into the swing context at proper alpha value
		Composite origComposite = image2D.getComposite();
		Composite newComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)(m_opacity/255.0));
		image2D.setComposite(newComposite);
		m_wrapped_component.paint((Graphics)image2D);
		image2D.setComposite(origComposite);
	}

	/**
	 * Method called to start fade-in effect.
	 */
	public void fadeIn() {
		setVisible(true);
		m_timer = new java.util.Timer(true);
		m_timer.scheduleAtFixedRate(new FaderTask(true), 10, 10);
	}

	/**
	 * Method called to start fade-out effect
	 */
	public void fadeOut() {
		setVisible(true);
		m_timer = new java.util.Timer(true);
		m_timer.scheduleAtFixedRate(new FaderTask(false), 10, 10);
	}

	/**
	 * Method call to cancel effect.
	 */
	public void cancelTransition() {
		m_timer.cancel();
		setVisible(false);
	}

	/**
	 * Class to provide animation.
	 */
	class FaderTask extends TimerTask {
		private boolean fadeIn;
		private int popupOpacity;
		public FaderTask(boolean fadeIn) {
			this.fadeIn = fadeIn;
			popupOpacity = (fadeIn) ? 0: 255;
		}
		public void run() {
			popupOpacity = (fadeIn) ? popupOpacity + 5 : popupOpacity - 5;
			if (popupOpacity > 255) popupOpacity = 255;
			if (popupOpacity < 0) popupOpacity = 0;
			m_popupPanel.setOpacity(popupOpacity);
			m_popupPanel.setBorder(popupOpacity);
			if (fadeIn && popupOpacity >= 255) {
				m_timer.cancel();
			}
			else if (!fadeIn && popupOpacity <= 0) {
				m_timer.cancel();
				m_popupPanel.setVisible(false);
			}
			m_owner.repaint();
		}
	}
}