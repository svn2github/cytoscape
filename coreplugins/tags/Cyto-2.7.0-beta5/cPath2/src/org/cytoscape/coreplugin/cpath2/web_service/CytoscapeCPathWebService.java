package org.cytoscape.coreplugin.cpath2.web_service;

import cytoscape.Cytoscape;
import cytoscape.CyNode;
import cytoscape.CyNetwork;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.VisualStyle;
import cytoscape.data.webservice.*;
import cytoscape.data.webservice.ui.WebServiceClientGUI;
import cytoscape.data.CyAttributes;
import cytoscape.layout.Tunable;
import cytoscape.util.ModuleProperties;
import cytoscape.util.ModulePropertiesImpl;
import org.cytoscape.coreplugin.cpath2.schemas.search_response.SearchResponseType;
import org.cytoscape.coreplugin.cpath2.task.ExecuteGetRecordByCPathId;
import org.cytoscape.coreplugin.cpath2.task.ExpandNode;
import org.cytoscape.coreplugin.cpath2.util.NullTaskMonitor;
import org.cytoscape.coreplugin.cpath2.view.cPathSearchPanel;
import org.cytoscape.coreplugin.cpath2.view.TabUi;
import org.cytoscape.coreplugin.cpath2.view.SearchHitsPanel;
import org.cytoscape.coreplugin.cpath2.plugin.CPathPlugIn2;
import org.cytoscape.coreplugin.cpath2.cytoscape.BinarySifVisualStyleUtil;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.*;
import java.util.List;

import giny.view.NodeView;
import giny.view.EdgeView;
import giny.model.Node;

/**
 * CPath Web Service, integrated into the Cytoscape Web Services Framework.
 */
public class CytoscapeCPathWebService extends WebServiceClientImpl implements WebServiceClientGUI,
    NetworkImportWebServiceClient {
    // Display name of this client.
    private static final String DISPLAY_NAME = CPathProperties.getInstance().getCPathServerName() +
            " Web Service Client";

    // Client ID. This should be unique.
    private static final String CLIENT_ID = CPathProperties.getInstance().getWebServicesId();

    // Instance of this client.  This is a singleton.
    private static final WebServiceClient client = new CytoscapeCPathWebService();

    /**
     * NCBI Taxonomy ID Filter.
     */
    public static final String NCBI_TAXONOMY_ID_FILTER = "ncbi_taxonomy_id_filter";

    /**
     * Response Format.
     */
    public static final String RESPONSE_FORMAT = "response_format";

    private JPanel mainPanel;

    /**
     * Return instance of this client.
     *
     * @return WebServiceClient Object.
     */
    public static WebServiceClient getClient() {
        return client;
    }

    /**
     * Execute Request Service.
     * @param e CyWebService Object.
     * @throws Exception All Errors.
     */
    public void executeService(CyWebServiceEvent e) throws CyWebServiceException{
        if (e.getSource().equals(CLIENT_ID)) {
            if (e.getEventType().equals(CyWebServiceEvent.WSEventType.IMPORT_NETWORK)) {
                importNetwork(e);
            } else if (e.getEventType().equals(CyWebServiceEvent.WSEventType.EXPAND_NETWORK)) {

            } else if (e.getEventType().equals(CyWebServiceEvent.WSEventType.SEARCH_DATABASE)) {
                try {
                    searchDatabase(e);
                } catch (CPathException e1) {
                    throw new CyWebServiceException
                            (CyWebServiceException.WSErrorCode.REMOTE_EXEC_FAILED);
                } catch (EmptySetException e1) {
                    throw new CyWebServiceException
                            (CyWebServiceException.WSErrorCode.NO_RESULT);
                }
            }
        }
    }

    public List<JMenuItem> getNodeContextMenuItems(NodeView nodeView) {
        CyNetworkView networkView = (CyNetworkView) nodeView.getGraphView();
        CyNetwork cyNetwork = networkView.getNetwork();
        CyAttributes networkAttributes  = Cytoscape.getNetworkAttributes();
        Boolean b = networkAttributes.getBooleanAttribute(cyNetwork.getIdentifier(), 
                BinarySifVisualStyleUtil.BINARY_NETWORK);
        if (b != null) {
            List<JMenuItem> menuList = new ArrayList<JMenuItem>();
            JMenuItem menuItem = new JMenuItem ("Get Neighbors");
            menuItem.addActionListener(new ExpandNode(nodeView));
            menuList.add(menuItem);
            return menuList;
        }
        return null;
    }

    public List getEdgeContextMenuItems(EdgeView edgeView) {
        return null;
    }

    public Container getGUI() {
        return mainPanel;
    }

    public void setGUI(Container container) {
        //
    }

    public VisualStyle getDefaultVisualStyle() {
        return null;
    }

    public String getDescription() {
        String desc = CPathProperties.getInstance().getCPathBlurb();
        desc = desc.replaceAll("<span class='bold'>", "<B>");
        desc = desc.replaceAll("</span>", "</B>");
        return "<html><body>" + desc + "</body></html>";
    }

    public Icon getIcon(IconSize iconSize) {
        URL iconURL = SearchHitsPanel.class.getResource("resources/"
                + CPathProperties.getInstance().getIconFileName());
        return new ImageIcon(iconURL);
    }

    /**
     * Creates a new Web Services client.
     */
    private CytoscapeCPathWebService() {
        super(CLIENT_ID, DISPLAY_NAME, new WebServiceClientManager.ClientType[]
                {WebServiceClientManager.ClientType.NETWORK});
        // Set properties for this client.
        this.setClientStub(CPathWebServiceImpl.getInstance());
        setProperty();

        mainPanel = new JPanel();
        mainPanel.setPreferredSize(new Dimension (500,400));
        mainPanel.setLayout (new BorderLayout());

        CPathWebService webApi = CPathWebServiceImpl.getInstance();
        cPathSearchPanel cpathPanel = new cPathSearchPanel(webApi);

        TabUi tabbedPane = TabUi.getInstance();
        tabbedPane.add("Search", cpathPanel);

        JScrollPane configPanel = CPathPlugIn2.createConfigPanel();
        tabbedPane.add("Options", configPanel);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

    }

    /**
     * Set props for this client.
     */
    private void setProperty() {
        props = new ModulePropertiesImpl(clientID, "wsc");
        props.add(new Tunable(NCBI_TAXONOMY_ID_FILTER, "Filter by Organism - NCBI Taxonomy ID",
                Tunable.INTEGER, new Integer(-1)));
        props.add(new Tunable(RESPONSE_FORMAT, "Response Format",
                Tunable.INTEGER, CPathResponseFormat.BINARY_SIF.getFormatString()));
    }

    private void importNetwork(CyWebServiceEvent e) {
        CPathWebService webApi = CPathWebServiceImpl.getInstance();
        String q = e.getParameter().toString();

        String idStrs[] = q.split(" ");
        long ids[] = new long[idStrs.length];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = Long.parseLong(idStrs[i]);
        }

        ModuleProperties properties = this.getProps();
        Tunable tunable = properties.get(RESPONSE_FORMAT);
        CPathResponseFormat format = CPathResponseFormat.BINARY_SIF;
        if (tunable != null) {
            format = CPathResponseFormat.getResponseFormat((String) tunable.getValue());
        }

        //  Create the task
        ExecuteGetRecordByCPathId task = new ExecuteGetRecordByCPathId(webApi,
                ids, format, CPathProperties.getInstance().getCPathServerName());
        //  Run right here in this thread.
        task.run();
    }

    private void searchDatabase(CyWebServiceEvent e) throws CPathException, EmptySetException {
        String q = e.getParameter().toString();
        CPathWebService webApi = CPathWebServiceImpl.getInstance();
        ModuleProperties properties = this.getProps();
        Tunable tunable = properties.get(NCBI_TAXONOMY_ID_FILTER);
        Integer taxonomyId = -1;
        if (tunable != null) {
            taxonomyId = (Integer) tunable.getValue();
        }
        SearchResponseType response = webApi.searchPhysicalEntities(q, taxonomyId,
                new NullTaskMonitor());
        Integer totalNumHits = response.getTotalNumHits().intValue();

        //  Fire appropriate events.
        if (e.getNextMove() != null) {
            Cytoscape.firePropertyChange("SEARCH_RESULT", this.clientID,
                    new DatabaseSearchResult(totalNumHits, response,
                            e.getNextMove()));
        } else {
            Cytoscape.firePropertyChange("SEARCH_RESULT", this.clientID,
                    new DatabaseSearchResult(totalNumHits, response,
                            CyWebServiceEvent.WSEventType.IMPORT_NETWORK));
        }
    }
}

