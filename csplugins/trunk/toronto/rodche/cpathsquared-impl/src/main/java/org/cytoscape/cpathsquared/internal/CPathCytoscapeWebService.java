package org.cytoscape.cpathsquared.internal;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.cytoscape.cpathsquared.internal.task.CPathNetworkImportTask;
import org.cytoscape.cpathsquared.internal.view.CPathSearchPanel;
import org.cytoscape.cpathsquared.internal.view.GuiUtils;
import org.cytoscape.cpathsquared.internal.view.TabUi;
import org.cytoscape.io.webservice.NetworkImportWebServiceClient;
import org.cytoscape.io.webservice.SearchWebServiceClient;
import org.cytoscape.io.webservice.client.AbstractWebServiceClient;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.Tunable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cpath.service.OutputFormat;

/**
 * CPath Web Service, integrated into the Cytoscape Web Services Framework.
 */
public class CPathCytoscapeWebService extends AbstractWebServiceClient 
	implements NetworkImportWebServiceClient, SearchWebServiceClient<Object> 
{
    private static final Logger LOGGER = LoggerFactory.getLogger(CPathCytoscapeWebService.class);
	
	// Display name of this client.
    private static final String DISPLAY_NAME = CPathProperties.serverName + " Client";

    /**
     * NCBI Taxonomy ID Filter.
     */
    public static final String NCBI_TAXONOMY_ID_FILTER = "ncbi_taxonomy_id_filter";

    /**
     * Response Format.
     */
    public static final String RESPONSE_FORMAT = "response_format";

	@Tunable(description="Filter by Organism - NCBI Taxonomy ID")
	Integer taxonomyId = -1; //TODO consider several values (logical 'OR')
//	@Tunable(description="Filter by BioPAX Class")
//	String biopaxType = null;
//	@Tunable(description="Filter by Data Source")
//	String dataSource = null; //TODO consider several values (logical 'OR')
	
    private JPanel mainPanel;

	private final CPath2Factory factory;

	private CPathWebService webApi;

    @Override
    public Container getQueryBuilderGUI() {
    	return mainPanel;
    }
    
    @Override
    public Set<CyNetwork> getNetworks() {
    	// TODO Auto-generated method stub
    	return null;
    }
    
    @Override
    public Object getSearchResult() {
    	// TODO Auto-generated method stub
    	return null;
    }
    
    @Override
    public TaskIterator getTaskIterator() {
    	String query = "";
		CPathNetworkImportTask task = factory.createCPathNetworkImportTask(query, webApi, OutputFormat.BINARY_SIF);
    	return new TaskIterator(task);
    }
    
    /**
     * Creates a new Web Services client.
     * @param factory 
     */
    public CPathCytoscapeWebService(CPath2Factory factory) {
    	super(CPathProperties.cPathUrl, DISPLAY_NAME, makeDescription());
    	this.factory = factory;

        mainPanel = new JPanel();
        mainPanel.setPreferredSize(new Dimension (500,400));
        mainPanel.setLayout (new BorderLayout());

        webApi = CPathWebServiceImpl.getInstance();
        CPathSearchPanel cpathPanel = new CPathSearchPanel(webApi, factory);

        TabUi tabbedPane = TabUi.getInstance();
        tabbedPane.add("Search", cpathPanel);

        JScrollPane configPanel = GuiUtils.createConfigPanel();
        tabbedPane.add("Options", configPanel);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
    	if(LOGGER.isDebugEnabled())
    		LOGGER.debug("CPathCytoscapeWebService created!");
    }

    private static String makeDescription() {
        String desc = CPathProperties.blurb;
        desc = desc.replaceAll("<span class='bold'>", "<B>");
        desc = desc.replaceAll("</span>", "</B>");
        return "<html><body>" + desc + "</body></html>";
	}

}

