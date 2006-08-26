package eqb;

import ding.view.NodeContextMenuListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import java.util.*;
import java.io.File;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.JFileChooser;
import javax.swing.SwingConstants;
import javax.swing.table.TableModel;

import giny.model.Node;
import giny.model.Edge;
import giny.view.NodeView;

import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;
import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.CyEdge;
import cytoscape.data.CyAttributes;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.data.Semantics;
import cytoscape.visual.VisualStyle;

//import yfiles.YFilesLayout;

/**
 * eQTL browser plugin
 */
public class EqbPlugin extends CytoscapePlugin
    implements PropertyChangeListener, NodeContextMenuListener
{

    private static String _defaultFile = "default.eqt";
    private static String _currentFile = "";
    
    private EqtlTableModel _eMap;

    private String _nodeIdentifier = Semantics.CANONICAL_NAME;
    private String _eShape = "eQTLShape";
    private String _eNodeAttr = "eQTLValue";
    private Double _zero = new Double(0.0);
    private Double _targetValue = new Double(-1.0);
    private JFileChooser _fileChooser;

    private EqtlPanel _cytoPanel;
    private CyNetwork _resultNetwork;
    private CyNetwork _parentNetwork;

    public String getCurrentFile()
    {
        return _currentFile;
    }

    public EqtlTableModel getTableModel()
    {
        return _eMap;
    }
    
    /**
     * This constructor creates an action and adds it to the Plugins menu.
     */
    public EqbPlugin()
    {
        // Register the plugin to be notified when a new view is created
        Cytoscape.getDesktop().getSwingPropertyChangeSupport().addPropertyChangeListener( CytoscapeDesktop.NETWORK_VIEW_CREATED, this );

        CyNetworkView currentView = Cytoscape.getCurrentNetworkView();
        if(currentView !=null)
        {
            System.out.println("Adding eQTL Browser to current network");
            currentView.addNodeContextMenuListener(this);
        }
        
        // Create menu items
        addPluginMenuItem(new MainMenuAction());
        addPluginMenuItem(new ClearAction());
        addPluginMenuItem(new ShowPanelAction());

        // Initialize file chooser and look for default data file.
        _eMap = new EqtlTableModel();
        _fileChooser = new JFileChooser(new File("."));
        setDataFile(_defaultFile);

        // Panel

        _cytoPanel = new EqtlPanel(this);

        CytoscapeDesktop desktop = Cytoscape.getDesktop();
        CytoPanel cytoPanel = desktop.getCytoPanel (SwingConstants.WEST);

        cytoPanel.add("eQTL", _cytoPanel);
    }

    private void addPluginMenuItem(CytoscapeAction action)
    {
        //set the preferred menu
        action.setPreferredMenu("Plugins");
        //and add it to the menus
        Cytoscape.getDesktop().getCyMenus().addAction(action);
    }

    /**
     * 1. Parse data file
     * 2. Reset the parent network
     * 2. Update table model
     */
    private void setDataFile(String file)
    {
        try
        {
            File f = new File(file);

            if(!f.canRead())
            {
                System.out.println("eQTL Browser: can't read file "  + file);
                return;
            }
            
            System.out.println("eQTL Browser: reading data file " + file);
            EqtlLexer lexer =
                new EqtlLexer(new DataInputStream(new FileInputStream(f)));
            EqtlParser parser = new EqtlParser(lexer);
            Map m = parser.parseEqtl();

            System.out.println("eQTL Browser: read " + m.size() + " eQTLs");
            //printEqtls(m);

            setParentNetwork();
            _eMap.updateTableData(m, _parentNetwork, _nodeIdentifier);
            _currentFile = file;
        }
        catch(Exception e)
        {
            System.err.println("exception: "+e);
            e.printStackTrace();
        }

    }

    /** When a new network view is created, register the plugin
     * as a NodeContextMenuListener
     */
    public void propertyChange (PropertyChangeEvent evnt)
    {
        if (evnt.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_CREATED)
        {
            //System.out.println("[EqbNetworkListener]: propertyChange called");
            Cytoscape.getCurrentNetworkView().addNodeContextMenuListener(this);
        }
    }

    /*
     * When a node is right-clicked, add an eQTL menu option to the popup
     * menu
     */
    public void addNodeContextMenuItems (NodeView nodeView, JPopupMenu menu)
    {
        //System.out.println("[EqbContextMenuListener]: addNodeContextMenuItem called");
        if(menu==null){
            menu=new JPopupMenu();
        }

        JMenuItem j = new JMenuItem (new PopupAction(nodeView));

        menu.add(j);
    }
        

    /** For DEBUG */
    private static void printEqtls(Map m)
    {
        for(Iterator it = m.keySet().iterator(); it.hasNext();)
        {
            Eqtl e = (Eqtl) m.get(it.next());

            System.out.println(e);
        }
    }
    
    /**
     * Gives a description of this plugin.
     */
    public String describe() {
        StringBuffer sb = new StringBuffer();
        sb.append("eQTL browser plugin");
        return sb.toString();
    }


    /**
     * Reset all nodes to zero
     */
    private void resetNodeValues()
    {
        CyNetwork network = Cytoscape.getCurrentNetwork();
        CyAttributes nodeAttr = Cytoscape.getNodeAttributes();
        
        for (Iterator ni = network.nodesIterator(); ni.hasNext(); )
        {
            CyNode n = (CyNode)ni.next();
            
            nodeAttr.setAttribute(n.getIdentifier(),
                                  _eNodeAttr,
                                  _zero);
        }
        CyNetworkView view = Cytoscape.getCurrentNetworkView();
        if(view != null)
        {
            view.redrawGraph(false, true);
            view.updateView();
        }
    }

    private void setParentNetwork()
    {
        _parentNetwork = Cytoscape.getCurrentNetwork();
    }
    
    public void showEqtl(CyNode node)
    {
        //System.out.println("eQTL popup start");
        
        //get the network object; this contains the graph
        if(_parentNetwork == null)
        {
            setParentNetwork();
        }

        //can't continue if no network selected
        if (_parentNetwork == null) {return;}
        
        String name = getNodeName(_parentNetwork, node);

        if(_eMap == null || !_eMap.containsNode(node))
        {
            noMatch(name);
            return;
        }
        
        CyAttributes nodeAttr = Cytoscape.getNodeAttributes();
        
        //System.out.println("  resetting node vals");
        resetNodeValues();
        
        Eqtl eqtl = (Eqtl) _eMap.getEqtlData(node);
        
        nodeAttr.setAttribute(node.getIdentifier(),
                              _eNodeAttr,
                              _targetValue);
        
        List loci = new ArrayList();
        loci.add(node);
        loci.addAll(_parentNetwork.neighborsList(node));
        //System.out.println("   checking loci");
        for (Iterator ni = _parentNetwork.nodesIterator(); ni.hasNext(); ) {
            CyNode n2 = (CyNode)ni.next();
            
            String name2 = getNodeName(_parentNetwork, n2);
            if (name2 == null) {continue;}
            
            if(eqtl.isLocus(name2))
            {
                //NodeView view2 = view.getNodeView(n2);
                //view2.setSelected(true);
                
                loci.add(n2);
                loci.addAll(_parentNetwork.neighborsList(n2));
                
                nodeAttr.setAttribute(n2.getIdentifier(),
                                      _eNodeAttr,
                                      eqtl.get(name2));
            }
        }
        //System.out.println("   getting edges");
        List edges = _parentNetwork.getConnectingEdges(loci);
        
        //System.out.println("   creating network");
        createNetwork(loci, edges, name + " eQTLs");
        
        //tell the view to redraw since we've changed node attributes
        String parentId = _parentNetwork.getIdentifier();
        if(Cytoscape.viewExists(parentId))
        {
            CyNetworkView view = Cytoscape.getNetworkView(parentId);
            view.redrawGraph(false, true);
            view.updateView();
        }
    }
    

    private void noMatch(String name)
    {
        JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
                                      "No eQTL data for " + name);
    }

    private void initResultNetwork()
    {
        _resultNetwork = Cytoscape.createNetwork(new HashSet(), new HashSet(),
                                                 "eQTL",
                                                 _parentNetwork,
                                                 false);
    }

    private void clearResultNetwork()
    {
        int[] nodes = new int[_resultNetwork.getNodeCount()];
        int[] edges = new int[_resultNetwork.getEdgeCount()];

        int x = 0;
        for(Iterator i = _resultNetwork.nodesIterator(); i.hasNext();)
        {
            Node n = (Node) i.next();
            nodes[x] = n.getRootGraphIndex();
            x++;
        }
        for(int i=0; i < nodes.length; i++)
        {
            _resultNetwork.removeNode(nodes[i], false);
        }
        
        x = 0;
        for(Iterator i = _resultNetwork.edgesIterator(); i.hasNext();)
        {
            Edge e = (Edge) i.next();
            edges[x] = e.getRootGraphIndex();
            x++;
        }
        for(int i=0; i < edges.length; i++)
        {
            _resultNetwork.removeEdge(edges[i], false);
        }

    }
    
    private void createNetwork(Collection nodes,
                               Collection edges,
                               String name)
    {
        // clear the result network, creating it if necessary
        if(_resultNetwork == null)
        {
            initResultNetwork();
        }
        else
        {
            clearResultNetwork();
        }

        // add nodes and edges to the result network
        for(Iterator i = nodes.iterator(); i.hasNext();)
        {
            _resultNetwork.addNode((Node) i.next());
        }
        for(Iterator i = edges.iterator(); i.hasNext();)
        {
            _resultNetwork.addEdge((Edge) i.next());
        }
        //_resultNetwork.setTitle(name);
        
        CyNetworkView new_view;
        if(!Cytoscape.viewExists(_resultNetwork.getIdentifier()))
        {
            new_view = Cytoscape.createNetworkView(_resultNetwork,
                                                   "eQTL result");
        }
        else
        {
            new_view = Cytoscape.getNetworkView(_resultNetwork.getIdentifier());
            //new_view.setTitle(name);
        }

        if (new_view == Cytoscape.getNullNetworkView()) {
            return;
        }

        CyNetworkView parent_view = Cytoscape.getNetworkView(_parentNetwork.getIdentifier());
        
        if (parent_view != Cytoscape.getNullNetworkView())
        {
            Iterator i = _resultNetwork.nodesIterator();
            while (i.hasNext())
            {
                Node node = (Node) i.next();
                new_view.getNodeView( node ).setOffset( parent_view.getNodeView(node).getXPosition(),
                                                        parent_view.getNodeView(node).getYPosition()); 
            } 
            new_view.fitContent(); 
            
            // Set visual style
            VisualStyle newVS = parent_view.getVisualStyle();
            if(newVS != null) {
                new_view.setVisualStyle(newVS.getName());
            } else {
                new_view.setVisualStyle("default");
            }
            
        } else {
            new_view.setVisualStyle("default");
        }

        //System.out.println("Doing yfiles organic layout");
        //YFilesLayout layout = new YFilesLayout(new_view);
        //layout.doLayout(3, 0);
        
        System.out.println("Redrawing new view");
        
        new_view.redrawGraph(true, true);

        new_view.updateView();

        //new_view.getComponent().repaint();
    }
    
    /**
     * Gets the canonical name of the given node from the network object
     *
     * Returns null if a valid name cannot be obtained.
     */
    private String getNodeName(CyNetwork network, CyNode node) {
        String canonicalName =
            (String)network.getNodeAttributeValue(node,
                                                  Semantics.CANONICAL_NAME);
        //return nothing if we can't get a valid name
        if (canonicalName == null || canonicalName.length() == 0)
        {
            return null;
        }
        
        return canonicalName;
    }
    
    /**
     * This class gets attached to the popup menu.
     */
    public class PopupAction extends CytoscapeAction {

        private NodeView _nv;
        /**
         * The constructor sets the text that should appear on the menu item.
         */
        public PopupAction(NodeView nodeView)
        {
            super("eQTL");
            _nv = nodeView;
        }
        
        /**
         * This method is called when the user selects the menu item.
         */
        public void actionPerformed(ActionEvent ae)
        {
            //first get the corresponding node in the network
            CyNode node = (CyNode) _nv.getNode();
        
            showEqtl(node);
        }
        
    }
    
    
    /**
     * Class for selecting a new eQTL data file
     */
    public class MainMenuAction extends CytoscapeAction
    {

        /**
         * The constructor sets the text that should appear on the menu item.
         */
        public MainMenuAction()
        {
            super("eQTL: Load data file...");
        }
        
        /**
         * This method is called when the user selects the menu item.
         */
        public void actionPerformed(ActionEvent ae)
        {
            int returnVal = _fileChooser.showOpenDialog(Cytoscape.getDesktop());
            if(returnVal == JFileChooser.APPROVE_OPTION)
            {
                String f = _fileChooser.getSelectedFile().getName();
                setDataFile(f);
            }
        }
    }

    
    /**
     * Class for clearing eQTL data from the network
     */
    public class ClearAction extends CytoscapeAction
    {
        /**
         * The constructor sets the text that should appear on the menu item.
         */
        public ClearAction()
        {
            super("eQTL: Clear data from network");
        }
        
        /**
         * This method is called when the user selects the menu item.
         */
        public void actionPerformed(ActionEvent ae)
        {
            resetNodeValues();
        }
    }

        
    /**
     * Class for clearing eQTL data from the network
     */
    public class ShowPanelAction extends CytoscapeAction
    {
        /**
         * The constructor sets the text that should appear on the menu item.
         */
        public ShowPanelAction()
        {
            super("eQTL: show panel");
        }
        
        /**
         * This method is called when the user selects the menu item.
         */
        public void actionPerformed(ActionEvent ae)
        {
            CytoPanel cytoPanel = Cytoscape.getDesktop().getCytoPanel (SwingConstants.WEST);

            int index = cytoPanel.indexOfComponent(_cytoPanel);
            cytoPanel.setSelectedIndex(index);
        }
    }
}

