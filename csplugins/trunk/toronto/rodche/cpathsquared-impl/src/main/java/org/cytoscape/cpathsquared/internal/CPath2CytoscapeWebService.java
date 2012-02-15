package org.cytoscape.cpathsquared.internal;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.cytoscape.cpathsquared.internal.task.CPath2NetworkImportTask;
import org.cytoscape.cpathsquared.internal.view.CPath2SearchPanel;
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
public class CPath2CytoscapeWebService extends AbstractWebServiceClient 
	implements NetworkImportWebServiceClient, SearchWebServiceClient<Object> 
{
    private static final Logger LOGGER = LoggerFactory.getLogger(CPath2CytoscapeWebService.class);
	
	// Display name of this client.
    private static final String DISPLAY_NAME = CPath2Properties.serverName + " Client";

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

	private CPath2WebService webApi;

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
    public TaskIterator createTaskIterator() {
    	String query = "";
		CPath2NetworkImportTask task = factory.createCPathNetworkImportTask(query, webApi, OutputFormat.BINARY_SIF);
    	return new TaskIterator(task);
    }
    
    /**
     * Creates a new Web Services client.
     * @param factory 
     */
    public CPath2CytoscapeWebService(CPath2Factory factory) {
    	super(CPath2Properties.cPathUrl, DISPLAY_NAME, makeDescription());
    	this.factory = factory;

        mainPanel = new JPanel();
        mainPanel.setPreferredSize(new Dimension (500,400));
        mainPanel.setLayout (new BorderLayout());

        webApi = CPath2WebServiceImpl.getInstance();
        CPath2SearchPanel cpathPanel = new CPath2SearchPanel(webApi, factory);

        TabUi tabbedPane = TabUi.getInstance();
        tabbedPane.add("Search", cpathPanel);

        JScrollPane configPanel = GuiUtils.createConfigPanel();
        tabbedPane.add("Options", configPanel);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
    	if(LOGGER.isDebugEnabled())
    		LOGGER.debug("CPath2CytoscapeWebService created!");
    }

    private static String makeDescription() {
        String desc = CPath2Properties.blurb;
        desc = desc.replaceAll("<span class='bold'>", "<B>");
        desc = desc.replaceAll("</span>", "</B>");
        return "<html><body>" + desc + "</body></html>";
	}

}

