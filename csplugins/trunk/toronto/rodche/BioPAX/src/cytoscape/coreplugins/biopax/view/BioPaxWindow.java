// $Id: BioPaxWindow.java,v 1.4 2006/06/15 22:06:02 grossb Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander
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
package cytoscape.coreplugins.biopax.view;

import java.awt.*;

import javax.swing.*;


/**
 * BioPAX Window.
 * <p/>
 * Currently only used for local developer testing.
 *
 * @author Ethan Cerami.
 */
public class BioPaxWindow extends JFrame {
	private static BioPaxContainer bpContainer = null;
	private Color bgColor = new Color(236, 233, 216);

	//  Window Width/Height
	private int width = 350;
	private int height = 400;

	/**
	 * Private Constructor, to enforce singleton pattern.
	 */
	public BioPaxWindow() {
		this.setResizable(false);
		this.setBackground(bgColor);
		this.setTitle("BioPAX Plugin");

		Container container = this.getContentPane();
		bpContainer = BioPaxContainer.getInstance();
		container.add(bpContainer);
		setSize(width, height);
	}

	/**
	 * Used to local testing purposes only.
	 *
	 * @param args Command Line Arguments
	 * @throws Exception All Exceptions.
	 */
	public static void main(String[] args) throws Exception {
		//FileInputStream in = new FileInputStream("testData/biopax_complex.owl");
		final BioPaxWindow bioPaxWindow = new BioPaxWindow();
		SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					try {
						BioPaxContainer bpContainer = BioPaxContainer.getInstance();
						BioPaxDetailsPanel bpPanel = bpContainer.getBioPaxDetailsPanel();
						bpPanel.showDetails("CPATH-124");
						//bioPaxWindow.add(bpPanel);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
	}
}
