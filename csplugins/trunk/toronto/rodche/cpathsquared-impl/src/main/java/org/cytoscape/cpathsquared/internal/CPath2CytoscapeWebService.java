package org.cytoscape.cpathsquared.internal;

import java.awt.Container;

import javax.swing.JPanel;

import org.cytoscape.cpathsquared.internal.task.CPath2NetworkImportTask;
import org.cytoscape.cpathsquared.internal.view.GuiUtils;
import org.cytoscape.io.webservice.NetworkImportWebServiceClient;
import org.cytoscape.io.webservice.SearchWebServiceClient;
import org.cytoscape.io.webservice.swing.AbstractWebServiceGUIClient;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cpath.service.OutputFormat;

/**
 * CPathSquared Web Service UI, integrated into the Cytoscape Web Services Framework.
 */
public final class CPath2CytoscapeWebService extends AbstractWebServiceGUIClient 
	implements NetworkImportWebServiceClient, SearchWebServiceClient
{
    private static final Logger LOGGER = LoggerFactory.getLogger(CPath2CytoscapeWebService.class);
	
	// Display name of this client.
    private static final String DISPLAY_NAME = CPath2Properties.serverName + " Client";

    private JPanel mainPanel;

    @Override
    public Container getQueryBuilderGUI() {
    	return mainPanel;
    }
    
    
    @Override
    public TaskIterator createTaskIterator(Object query) {
		Task task = new CPath2NetworkImportTask((String) query, OutputFormat.BINARY_SIF);
    	return new TaskIterator(task);
    }
    
    /**
     * Creates a new Web Services client.
     */
    public CPath2CytoscapeWebService() {
    	super(CPath2Properties.cPathUrl, DISPLAY_NAME, makeDescription());
        
    	mainPanel = GuiUtils.createUI();
        
    	if(LOGGER.isDebugEnabled())
    		LOGGER.debug("CPathSquared Web Service UI Created!");
    }

    private static String makeDescription() {
        String desc = CPath2Properties.blurb;
        desc = desc.replaceAll("<span class='bold'>", "<B>");
        desc = desc.replaceAll("</span>", "</B>");
        return "<html><body>" + desc + "</body></html>";
	}

}

