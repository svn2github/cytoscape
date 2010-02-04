
/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

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

package example;

import cytoscape.util.CytoscapeAction;
import java.awt.event.ActionEvent;
import java.awt.geom.Rectangle2D; 
import java.awt.Paint;
import java.awt.Color;
import java.awt.TexturePaint;
import javax.imageio.ImageIO;
import java.net.URL;
import java.util.Iterator;

import ding.view.DNodeView;
import cytoscape.Cytoscape; 
import cytoscape.view.CyNetworkView;
import cytoscape.render.stateful.NodeDetails; 

public class CustomGraphicsDemoAction extends CytoscapeAction {

	public CustomGraphicsDemoAction() {
		super("Custom Graphics Demo");
		setPreferredMenu("Plugins");
	}

	public void actionPerformed(ActionEvent e) {
		Rectangle2D rect = new Rectangle2D.Double(-11.0, -15.0, 22.0, 30.0);
		Paint paint = null;
		try {
			paint = new TexturePaint(
					ImageIO.read(new URL("http://cytoscape.org/people_photos/mike.jpg")), rect); 
		} catch (Exception exc) { 
			paint = Color.black; 
		}

		CyNetworkView view = Cytoscape.getCurrentNetworkView();
		Iterator it = view.getNodeViewsIterator();
		while (it.hasNext()) {
			DNodeView dnv = (DNodeView) it.next();
			dnv.addCustomGraphic(rect, paint, NodeDetails.ANCHOR_WEST);
		}
		view.redrawGraph(true,false);
	}
}
