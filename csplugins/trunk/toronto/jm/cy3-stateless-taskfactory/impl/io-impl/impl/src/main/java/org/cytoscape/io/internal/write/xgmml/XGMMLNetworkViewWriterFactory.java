package org.cytoscape.io.internal.write.xgmml;

import java.io.OutputStream;

import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.internal.util.UnrecognizedVisualPropertyManager;
import org.cytoscape.io.internal.write.AbstractCyNetworkViewWriterFactory;
import org.cytoscape.io.write.CyNetworkViewWriterContext;
import org.cytoscape.io.write.CyNetworkViewWriterContextImpl;
import org.cytoscape.io.write.CyWriter;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.presentation.RenderingEngineManager;

public class XGMMLNetworkViewWriterFactory extends AbstractCyNetworkViewWriterFactory {

	protected final RenderingEngineManager renderingEngineManager;
	protected final UnrecognizedVisualPropertyManager unrecognizedVisualPropertyMgr;
	protected final CyNetworkManager networkManager;
	protected final CyRootNetworkManager rootNetworkManager;

	public XGMMLNetworkViewWriterFactory(final CyFileFilter filter,
		                                 final RenderingEngineManager renderingEngineManager,
		                                 final UnrecognizedVisualPropertyManager unrecognizedVisualPropertyMgr,
										 final CyNetworkManager networkManager,
										 final CyRootNetworkManager rootNetworkManager) {
		super(filter);
		this.renderingEngineManager = renderingEngineManager;
		this.unrecognizedVisualPropertyMgr = unrecognizedVisualPropertyMgr;
		this.networkManager = networkManager;
		this.rootNetworkManager = rootNetworkManager;
	}

	@Override
	public CyNetworkViewWriterContext createTaskContext() {
		return new CyNetworkViewWriterContextImpl();
	}
	
	@Override
    public CyWriter createWriterTask(CyNetworkViewWriterContext context) {
		CyNetwork network = context.getNetwork();
		CyNetworkView view = context.getNetworkView();
		OutputStream outputStream = context.getOutputStream();
		
        if (view != null) {
        	return new XGMMLWriter(outputStream, renderingEngineManager, view, unrecognizedVisualPropertyMgr,
        			networkManager, rootNetworkManager);
        }
        
        return new XGMMLWriter(outputStream, renderingEngineManager, network, unrecognizedVisualPropertyMgr,
        		networkManager, rootNetworkManager);
    }
}
