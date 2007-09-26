package cytoscape.view;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.actions.WorkflowExportAsGraphicsAction;
import cytoscape.actions.WorkflowImportNetworkFromFileAction;
import cytoscape.actions.WorkflowImportNetworkFromTableAction;
import cytoscape.actions.WorkflowImport_Annotation_Action;
import cytoscape.actions.WorkflowImport_Edge_Attributes_Action;
import cytoscape.actions.WorkflowImport_Expression_Matrix_Action;
import cytoscape.actions.WorkflowImport_Node_Attributes_Action;
import cytoscape.actions.WorkflowImport_Table_Attributes_Action;
import cytoscape.actions.WorkflowPanelAction;
import cytoscape.actions.Workflow_Agilent_Literature_Search_Action;
import cytoscape.actions.Workflow_BiNGO_Action;
import cytoscape.actions.Workflow_Filters_Action;
import cytoscape.actions.Workflow_cPath_Action;
import cytoscape.actions.Workflow_jActiveModules_Action;
import cytoscape.actions.Workflow_mCode_Action;

public class WorkflowTree extends JPanel
                      implements TreeSelectionListener {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    private JTree tree;
    private static boolean DEBUG = false;

    //Optionally play with line styles.  Possible values are
    //"Angled" (the default), "Horizontal", and "None".
    private static boolean playWithLineStyle = true;
    private static String lineStyle = "Horizontal";
    
    //Optionally set the look and feel.
    private static boolean useSystemLookAndFeel = true;
    
    // workflow steps
    private static final String NATURE_PROTOCOLS = "Nature Protocols";
    private static final String IMPORT = "Import Networks";
    private static final String IMPORT_FROM_FILE = "from File";
    private static final String IMPORT_FROM_TABLE = "from Table";
    private static final String IMPORT_FROM_AGILENT_LITERATURE_SEARCH = "Agilent Literature Search";
    private static final String IMPORT_FROM_CPATH = "cPath";
    private static final String IMPORT_FROM_PATHWAY_COMMONS = "Pathway Commons";
    private static final String IMPORT_FROM_INACT = "InAct";
    private static final String IMPORT_FROM_DIP = "DIP";
    private static final String DATA = "Load Data and Annotations";
    private static final String DATA_GO = "GO Annotations";
    private static final String DATA_ATTRIBUTE = "Attribute File";
    private static final String DATA_EXPRESSION = "Expression File";
    private static final String DATA_SYNONYMS = "Synonyms";
    private static final String ANALYZE = "Analyze Networks";
    private static final String ANALYZE_MCODE = "mCODE";
    private static final String ANALYZE_JACTIVE_MODULES = "jActiveModules";
    private static final String ANALYZE_BINGO = "BiNGO";
    private static final String ANALYZE_FILTERS = "Filters";
    private static final String PUBLISH = "Publish";
    private static final String PUBLISH_EXPORT_GRAPHICS = "Export Graphics";
    private static final String PUBLISH_EXPORT_SESSION = "Export Session to Web";
    private static final String NODE_ATTRIBUTES = "Node Attributes";
    private static final String EDGE_ATTRIBUTES = "Edge Attributes";
    private static final String TABLE_ATTRIBUTES = "Attributes from Table";
    

//    public WorkflowTree () {
//    	super(new BorderLayout());
//    	
//    	//Create the nodes.
//        this.setAlignmentX(Component.CENTER_ALIGNMENT);
//        DefaultMutableTreeNode top =
//            new DefaultMutableTreeNode(NATURE_PROTOCOLS);
//        createNodes(top);
//
//        //Create a tree that allows one selection at a time.
//        tree = new JTree(top);
//       
//        tree.getSelectionModel().setSelectionMode
//                (TreeSelectionModel.SINGLE_TREE_SELECTION);
//
//        //Listen for when the selection changes.
//        tree.addTreeSelectionListener(this);
//        
//
//        if (playWithLineStyle) {
//           tree.putClientProperty("JTree.lineStyle", lineStyle);
//        }
//        
//        tree.setPreferredSize(new Dimension (new Dimension (400,
//        		Cytoscape.getDesktop().getNetworkPanel().getHeight())));
//     
//
//        //Create the scroll pane and add the tree to it. 
//        JScrollPane treeView = new JScrollPane(tree);
//
//        treeView.setPreferredSize(new Dimension (new Dimension (250,
//        		Cytoscape.getDesktop().getNetworkPanel().getHeight())));
//        treeView.setMinimumSize(new Dimension (new Dimension (250,
//        		Cytoscape.getDesktop().getNetworkPanel().getHeight())));
//        treeView.setAlignmentX(Component.RIGHT_ALIGNMENT);
//        
//
//        //Add the split pane to this panel.
//        add(treeView, BorderLayout.NORTH);
//
//    }

    /**
     * from Ben Gross
     */
	public WorkflowTree () {

		// create the nodes
		DefaultMutableTreeNode top = new DefaultMutableTreeNode(NATURE_PROTOCOLS);
		createNodes(top);

		// create the tree
		tree = new JTree(top);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.addTreeSelectionListener(this);
		if (playWithLineStyle) {
			tree.putClientProperty("JTree.lineStyle", lineStyle);
		}

		// scrollpane for tree
		JScrollPane treeView = new JScrollPane(tree);
        treeView.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        treeView.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // from Ben Gross
        treeView.setBorder
        (new javax.swing.border.MatteBorder(15,15,15,15, java.awt.Color.white));

        
		// set scrollpane size:
		// width - a little bigger than longest string in tree
		// height - we are really only concerned with width, set height to anything
		// (note: longest string should be found programatically)
		String longestString = new String("Load Data and Annotations");
		java.awt.FontMetrics fm = treeView.getFontMetrics(treeView.getFont());
		int width = fm.stringWidth(longestString);
		Dimension treeViewDim = new Dimension(width + width / 2, 1);
        treeView.setMinimumSize(treeViewDim);

		// add tree to this pane
        setLayout(new BorderLayout());
		add(treeView, BorderLayout.CENTER);
	}

    
    /** Required by TreeSelectionListener interface. */
    /**
     * This is too hard-coded!  Change this to be more data-driven!
     */
    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                           tree.getLastSelectedPathComponent();
        
        CyNetwork currentNetwork = Cytoscape.getCurrentNetwork();

        if (node == null) return;
        else
        	
        {
        	Object userObject = node.getUserObject();
        	if (userObject == null) return;
        	
        	if (userObject instanceof WorkflowPanelAction)
        	{
        		((WorkflowPanelAction) userObject).actionPerformed (null);
        	}
        	else
        	{
        		// user may have selected an internal (Non-leaf) node.  Do nothing
        		
//        		JOptionPane.showMessageDialog(Cytoscape.getDesktop(), 
//        				new String("Boo Hoo, INTERNAL ERROR: Bad menu item in Workflow panel" + userObject));
        	}
        }

    }

  
    private void createNodes(DefaultMutableTreeNode top) {
        DefaultMutableTreeNode category = null;
        DefaultMutableTreeNode tool = null;
        DefaultMutableTreeNode subTool = null;

        category = new DefaultMutableTreeNode(IMPORT);
        top.add(category);

        tool = new DefaultMutableTreeNode(new WorkflowImportNetworkFromFileAction(IMPORT_FROM_FILE)); 
      
        category.add(tool);
        tool = new DefaultMutableTreeNode(new WorkflowImportNetworkFromTableAction(IMPORT_FROM_TABLE));
        category.add(tool);
  
        tool = new DefaultMutableTreeNode(new Workflow_Agilent_Literature_Search_Action(IMPORT_FROM_AGILENT_LITERATURE_SEARCH));
        category.add(tool);
        
        tool = new DefaultMutableTreeNode(new Workflow_cPath_Action(IMPORT_FROM_CPATH));
        category.add(tool);
        
        
        tool = new DefaultMutableTreeNode(new WorkflowPanelAction(IMPORT_FROM_PATHWAY_COMMONS));
   
        category.add(tool);
        tool = new DefaultMutableTreeNode(new WorkflowPanelAction(IMPORT_FROM_INACT));
        category.add(tool);
        tool = new DefaultMutableTreeNode(new WorkflowPanelAction(IMPORT_FROM_DIP));       
        category.add(tool);
      
        
        
        category = new DefaultMutableTreeNode(DATA);
        top.add(category);
        tool = new DefaultMutableTreeNode(new WorkflowImport_Annotation_Action(DATA_GO));

        category.add(tool);
        
        
        // import attributes: Node, Edge, and from Table
        tool = new DefaultMutableTreeNode(DATA_ATTRIBUTE);
        category.add(tool);
        subTool = new DefaultMutableTreeNode(new WorkflowImport_Node_Attributes_Action(NODE_ATTRIBUTES));
        tool.add(subTool);
        subTool = new DefaultMutableTreeNode(new WorkflowImport_Edge_Attributes_Action(EDGE_ATTRIBUTES));
        tool.add(subTool);
        subTool = new DefaultMutableTreeNode(new WorkflowImport_Table_Attributes_Action(TABLE_ATTRIBUTES));
        tool.add(subTool);
        
        
        
        
        tool = new DefaultMutableTreeNode(new WorkflowImport_Expression_Matrix_Action(DATA_EXPRESSION));
        category.add(tool);
        tool = new DefaultMutableTreeNode(new WorkflowPanelAction(DATA_SYNONYMS));
        category.add(tool);
        
        category = new DefaultMutableTreeNode(ANALYZE);
        top.add(category);
        tool = new DefaultMutableTreeNode(new Workflow_mCode_Action(ANALYZE_MCODE));
        category.add(tool);
        tool = new DefaultMutableTreeNode(new Workflow_jActiveModules_Action(ANALYZE_JACTIVE_MODULES));
        category.add(tool);
        tool = new DefaultMutableTreeNode(new Workflow_BiNGO_Action(ANALYZE_BINGO));
        category.add(tool);
        tool = new DefaultMutableTreeNode(new Workflow_Filters_Action(ANALYZE_FILTERS));
        category.add(tool);
        
        category = new DefaultMutableTreeNode(PUBLISH);
        top.add(category);
        tool = new DefaultMutableTreeNode(new WorkflowExportAsGraphicsAction(PUBLISH_EXPORT_GRAPHICS));
        category.add(tool);
        tool = new DefaultMutableTreeNode(new WorkflowPanelAction(PUBLISH_EXPORT_SESSION));
        category.add(tool);
    }
}
