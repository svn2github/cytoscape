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

import giny.model.Node;
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
import cytoscape.data.Semantics;

/**
 * eQTL browser plugin
 */
public class EqbPlugin extends CytoscapePlugin
    implements PropertyChangeListener, NodeContextMenuListener
{

    private static String _defaultFile = "default.eqt";
    private Map _eMap;

    private String _eShape = "eQTLShape";
    private String _eNodeAttr = "eQTLValue";
    private Double _zero = new Double(0.0);
    private Double _targetValue = new Double(-1.0);
    private JFileChooser _fileChooser;

    /**
     * This constructor creates an action and adds it to the Plugins menu.
     */
    public EqbPlugin() {
        
        Cytoscape.getDesktop().getSwingPropertyChangeSupport().addPropertyChangeListener( CytoscapeDesktop.NETWORK_VIEW_CREATED, this );

        CyNetworkView currentView = Cytoscape.getCurrentNetworkView();
        if(currentView !=null)
        {
            System.out.println("Adding eQTL Browser to current network");
            currentView.addNodeContextMenuListener(this);
        }
        
        //create a new action to respond to menu activation
        MainMenuAction action = new MainMenuAction();
        //set the preferred menu
        action.setPreferredMenu("Plugins");
        //and add it to the menus
        Cytoscape.getDesktop().getCyMenus().addAction(action);

        
        //create a new action to respond to menu activation
        ClearAction clear = new ClearAction();
        //set the preferred menu
        clear.setPreferredMenu("Plugins");
        //and add it to the menus
        Cytoscape.getDesktop().getCyMenus().addAction(clear);

        
        _fileChooser = new JFileChooser();
        setDataFile(_defaultFile);
    }

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
            _eMap = parser.parseEqtl();

            System.out.println("eQTL Browser: read " + _eMap.size() + " eQTLs");
            printEqtls(_eMap);
        }
        catch(Exception e)
        {
            System.err.println("exception: "+e);
            e.printStackTrace();
        }

    }
        
    public void propertyChange (PropertyChangeEvent evnt)
    {
        if (evnt.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_CREATED)
        {
            System.out.println("[EqbNetworkListener]: propertyChange called");
            
            Cytoscape.getCurrentNetworkView().addNodeContextMenuListener(this);
        }
    }

    public void addNodeContextMenuItems (NodeView nodeView, JPopupMenu menu)
    {
        System.out.println("[EqbContextMenuListener]: addNodeContextMenuItem called");
        
        if(menu==null){
            menu=new JPopupMenu();
        }

        JMenuItem j = new JMenuItem (new PopupAction(nodeView));

        menu.add(j);
    }
        
    
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
        }
    }
    
    /**
     * This class gets attached to the popup menu.
     */
    public class PopupAction extends CytoscapeAction {

        private NodeView _n;
        /**
         * The constructor sets the text that should appear on the menu item.
         */
        public PopupAction(NodeView nodeView)
        {
            super("eQTL");
            _n = nodeView;
        }
        
        private void noMatch(CyNetworkView view, String name)
        {
            JOptionPane.showMessageDialog(view.getComponent(),
                                          "No eQTL data for " + name);
        }
        
        /**
         * This method is called when the user selects the menu item.
         */
        public void actionPerformed(ActionEvent ae)
        {

            //get the network object; this contains the graph
            CyNetwork network = Cytoscape.getCurrentNetwork();
            //get the network view object
            CyNetworkView view = Cytoscape.getCurrentNetworkView();
            //can't continue if either of these is null
            if (network == null || view == null) {return;}
            /*put up a dialog if there are no selected nodes
               if (view.getSelectedNodes().size() == 0) {
                JOptionPane.showMessageDialog(view.getComponent(),
                        "Please select one or more nodes.");
            }
            */
            
            CyAttributes nodeAttr = Cytoscape.getNodeAttributes();

            resetNodeValues();
            
            //iterate over every node view
            //for (Iterator i = view.getSelectedNodes().iterator(); i.hasNext(); ) {
            //NodeView nView = (NodeView)i.next();

                //first get the corresponding node in the network
                CyNode node = (CyNode) _n.getNode();

                String name = getNodeName(network, node);
                if (name == null) {return;}

                if(!_eMap.containsKey(name))
                {
                    noMatch(view, name);
                    return;
                }
                
                Eqtl eqtl = (Eqtl) _eMap.get(name);
               
                nodeAttr.setAttribute(node.getIdentifier(),
                                      _eNodeAttr,
                                      _targetValue);
                
                for (Iterator ni = network.nodesIterator(); ni.hasNext(); ) {
                    CyNode n2 = (CyNode)ni.next();

                    String name2 = getNodeName(network, n2);
                    if (name2 == null) {continue;}

                    if(eqtl.isLocus(name2))
                    {
                        NodeView view2 = view.getNodeView(n2);
                        //view2.setSelected(true);

                        System.out.println(name2 + " setting " + _eNodeAttr +
                                           " to " + eqtl.get(name2));
                        
                        nodeAttr.setAttribute(n2.getIdentifier(),
                                              _eNodeAttr,
                                              eqtl.get(name2));
                    }
                }
                //}
            
            //tell the view to redraw since we've changed the selection
            view.redrawGraph(false, true);
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
    }

    
    /**
     * This class gets attached to the popup menu.
     */
    public class MainMenuAction extends CytoscapeAction
    {

        /**
         * The constructor sets the text that should appear on the menu item.
         */
        public MainMenuAction()
        {
            super("Set eQTL data file...");
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
                System.out.println("You chose to open this file: " + f);

                setDataFile(f);
            }
        }
    }

    
    /**
     * This class gets attached to the popup menu.
     */
    public class ClearAction extends CytoscapeAction
    {

        /**
         * The constructor sets the text that should appear on the menu item.
         */
        public ClearAction()
        {
            super("Clear eQTL data from network");
        }
        
        /**
         * This method is called when the user selects the menu item.
         */
        public void actionPerformed(ActionEvent ae)
        {
            resetNodeValues();
        }
    }
}

