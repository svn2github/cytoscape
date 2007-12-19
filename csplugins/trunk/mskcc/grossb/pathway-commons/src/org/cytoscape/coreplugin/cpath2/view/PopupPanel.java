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
package org.cytoscape.coreplugin.cpath2.view;

// imports
import java.util.Timer;
import java.util.TimerTask;
import java.awt.Robot;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.AlphaComposite;
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
	 * total animation time in ms
	 */
	private static int TOTAL_ANIMATION_TIME = 300;

	/**
	 * animation step in ms
	 */
	private static int ANIMATION_STEP = TOTAL_ANIMATION_TIME / 10;
	
	/**
	 * ref to ourself - used by animation timer
	 */
	private PopupPanel m_popupPanel;

	/**
	 * current level of opacity
	 */
	private float m_opacity;

	/**
	 * our owner - used by animation timer
	 */
	private JComponent m_owner;

	/**
	 * component that contains content we fade-in/out to
	 */
	private JComponent m_wrapped_component;

	/**
	 * modal panel
	 */
	private JComponent m_modal_panel;
	
	/**
	 * timer used for animations.
	 */
	private Timer m_timer;

	/**
	 * animation time in ms
	 */
	private long m_animation_time;


	/**
	 * robot used to capture screen
	 */
	private Robot m_robot;

	/**
	 * image that we fade into or fade out from.
	 */
	private BufferedImage m_curtain_image;

	/**
	 * image that we used to draw into.
	 */
	private BufferedImage m_this_panel_image;

	/**
	 * Constructor.
	 */
	public PopupPanel(JComponent owner, JComponent component, JComponent modalPanel) {

		// init member vars
		m_owner = owner;
		m_popupPanel = this;
		m_wrapped_component = component;
		m_modal_panel = modalPanel;
		try {
			m_robot = new Robot();
		}
		catch (AWTException e) {
			e.printStackTrace();
		}		

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
	 * Performs a screen capture of the desktop at
	 * specified coordinates and uses it as "curtain" for fade in/out.
	 */
	public void setCurtain(int x, int y, int width, int height) {

		m_curtain_image = m_robot.createScreenCapture(new Rectangle(x, y, width, height));
	}

	/**
	 * Set opacity level of component - used for transitions.
	 */
	public void setOpacity(float opacity) {
		m_opacity = opacity;
	}

	/**
	 * Our implementation of set bounds.
	 */
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		m_wrapped_component.setBounds(x, y, width, height);
		m_this_panel_image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	}

	/**
	 * Our implementation of paint component.
	 */
	public void paintComponent(Graphics g) {

		// only paint if we have an image
		if (m_this_panel_image != null) {

			// get this component's image context
			Graphics2D image2D = ((BufferedImage) m_this_panel_image).createGraphics();

			// draw wrapped component into it
			m_wrapped_component.paint((Graphics)image2D);

			// the draw "curtain" into it at proper alpha value
			Composite origComposite = image2D.getComposite();
			Composite newComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, m_opacity);

			image2D.setComposite(newComposite);
			image2D.drawImage(m_curtain_image, 0, 0, null);
			image2D.setComposite(origComposite);

			// now draw our image into swing device context
			g.drawImage(m_this_panel_image, 0, 0, null);
		}
	}

	/**
	 * Method called to start fade-in effect.
	 */
	public void fadeIn() {
		setVisible(true);
		m_timer = new java.util.Timer(true);
		m_animation_time = TOTAL_ANIMATION_TIME;
		m_timer.scheduleAtFixedRate(new FaderTask(true), 10, ANIMATION_STEP);
	}

	/**
	 * Method called to start fade-out effect
	 */
	public void fadeOut() {
		setVisible(true);
		m_wrapped_component.setVisible(false);
		m_modal_panel.setVisible(false);
		m_timer = new java.util.Timer(true);
		m_animation_time = 0;
		m_timer.scheduleAtFixedRate(new FaderTask(false), 10, ANIMATION_STEP);
	}

	/**
	 * Method call to cancel effect.
	 */
	public void cancelTransition() {
		m_timer.cancel();
		setVisible(false);
		m_modal_panel.setVisible(false);
	}

	/**
	 * Class to provide animation.
	 */
	class FaderTask extends TimerTask {
		private boolean fadeIn;
		public FaderTask(boolean fadeIn) {
			this.fadeIn = fadeIn;
		}
		public void run() {
			m_animation_time = (fadeIn)  ? m_animation_time - ANIMATION_STEP : m_animation_time + ANIMATION_STEP;
			if (m_animation_time < 0) m_animation_time = 0;
			if (m_animation_time > TOTAL_ANIMATION_TIME) m_animation_time = TOTAL_ANIMATION_TIME;
			m_opacity = (float)m_animation_time / TOTAL_ANIMATION_TIME;
			if (fadeIn && m_animation_time <= 0) {
				m_timer.cancel();
				m_wrapped_component.setVisible(true);
				m_modal_panel.setVisible(true);
			}
			else if (!fadeIn && m_animation_time >= TOTAL_ANIMATION_TIME) {
				m_timer.cancel();
				m_popupPanel.setVisible(false);
			}
			m_owner.repaint();
		}
	}
}