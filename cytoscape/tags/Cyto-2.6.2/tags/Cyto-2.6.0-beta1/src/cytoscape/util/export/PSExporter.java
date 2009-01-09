package cytoscape.util.export;

import java.io.FileOutputStream;
import java.io.IOException;
import cytoscape.Cytoscape;
import cytoscape.ding.DingNetworkView;
import cytoscape.view.CyNetworkView;
import cytoscape.view.InternalFrameComponent;
import org.freehep.graphicsio.ps.PSGraphics2D;
import java.util.Properties;
/**

 */
public class PSExporter implements Exporter
{
	//private boolean exportTextAsFont = true;

	public PSExporter()
	{
	}


	public void export(CyNetworkView view, FileOutputStream stream) throws IOException
	{
		//DingNetworkView theView = (DingNetworkView) view;
		//theView.setPrintingTextAsShape(!exportTextAsFont);

		InternalFrameComponent ifc = Cytoscape.getDesktop().getNetworkViewManager().getInternalFrameComponent(view);
		
		Properties p = new Properties();
	    p.setProperty(PSGraphics2D.PAGE_SIZE,"Letter");
	    //p.setProperty(PSGraphics2D.ORIENTATION,"Portrait");
	    
	    PSGraphics2D g = new PSGraphics2D(stream,ifc); 
	    g.setMultiPage(false); // true for PS file
	    
	    g.setProperties(p); 
	    g.startExport(); 
	    ifc.print(g); 
	    g.endExport();

	}
	
	//public void setExportTextAsFont(boolean pExportTextAsFont) {
	//	exportTextAsFont = pExportTextAsFont;
	//}

}
