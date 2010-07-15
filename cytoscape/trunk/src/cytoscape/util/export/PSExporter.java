/*
  File: PSExporter.java

  Copyright (c) 2010, The Cytoscape Consortium (www.cytoscape.org)

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
package cytoscape.util.export;


import java.io.FileOutputStream;
import java.io.IOException;
import cytoscape.Cytoscape;
import cytoscape.ding.DingNetworkView;
import cytoscape.view.CyNetworkView;
import cytoscape.view.InternalFrameComponent;
import org.freehep.graphicsio.ps.PSGraphics2D;
import java.util.Properties;


public class PSExporter implements Exporter {
	private boolean exportTextAsFont = true;

	public PSExporter() {
	}

	public void export(CyNetworkView view, FileOutputStream stream) throws IOException {
		DingNetworkView theView = (DingNetworkView) view;
		theView.setPrintingTextAsShape(!exportTextAsFont);

		InternalFrameComponent ifc = Cytoscape.getDesktop().getNetworkViewManager().getInternalFrameComponent(view);
		
		Properties p = new Properties();
		p.setProperty(PSGraphics2D.PAGE_SIZE,"Letter");
		p.setProperty("org.freehep.graphicsio.AbstractVectorGraphicsIO.TEXT_AS_SHAPES",
		              Boolean.toString(!exportTextAsFont)); 

		PSGraphics2D g = new PSGraphics2D(stream,ifc); 
		g.setMultiPage(false); // true for PS file
		g.setProperties(p); 

		g.startExport(); 
		ifc.printWithoutForeground(g); 
		g.endExport();
	}

	public void setExportTextAsFont(boolean pExportTextAsFont) {
		exportTextAsFont = pExportTextAsFont;
	}
}
