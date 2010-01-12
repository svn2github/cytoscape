/**
 * Copyright 2006 Andrea Splendiani
 * Released under GPL license
 */

/*
 * Created on Oct 11, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package fr.pasteur.sysbio.rdfscape.browsing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import fr.pasteur.sysbio.rdfscape.AbstractModulePanel;
import fr.pasteur.sysbio.rdfscape.InteractivePanel;
import fr.pasteur.sysbio.rdfscape.RDFScape;
import fr.pasteur.sysbio.rdfscape.Utilities;
import fr.pasteur.sysbio.rdfscape.help.HelpManager;
import fr.pasteur.sysbio.rdfscape.query.AbstractQueryResultTable;
import fr.pasteur.sysbio.rdfscape.query.JenaQueryResultTable;
import fr.pasteur.sysbio.rdfscape.query.MyQueryEngine;

/**
 * @author andrea
 * This is a view for the BrowserManager component. It contains panels for 
 * the available query methods and manage user interaction with queries.
 * It also provides an interface for some other service implemented through 
 * BrowseManager
 * 
 */
public class BrowserPanel extends AbstractModulePanel{
	private BrowserManager browserManager=null;	
	
	private JComboBox queryEnginesBox=null;	//list of available query engines
	private Hashtable queryEngines=null;
	
	/**
	 * These button are relative to operations on networks
	 */
	private JButton editNetworkButton=null;
	private JButton getNetworkPatternButton=null;
	private JButton newNetworkButton=null;
	
	
	
	/**
	 * These buttons are relative to operations on queries
	 */
	private JButton makeQueryButton=null;
	private JButton clearQueryButton=null;
	
	/**
	 * These elements are relative to query results
	 */
	private JButton selectAll=null;
	private JButton plotSelection=null;
	private JButton searchSelection=null;
	
	// TODO private ResultTableViewer resultTable=null;						//Table displaying results
	private ResultTableViewer resultTable=null;
	private AbstractQueryResultTable queryResult=null;				//holds the result of a query
	
	/**
	 * Support elements
	 */
	private JPanel upPanel=null;							//This Panel holds the query panel in its central position
	private JComponent currentQueryPanel=null;				//The current query panel
	private MyQueryEngine currentQueryEngine=null;			//This is the current query Engine. While related to currentQueryPanel, 
	private BrowseModeListener browseModeListener=null;		//changes browse more
	
	private JPanel northPanel=null;
	private JPanel patternPanel=null;
	private JPanel newNetworkPanel=null;
	
	
	/**
	 * Here the panel is built.
	 */
	public BrowserPanel(BrowserManager bm) {
		super();
		browserManager=bm;
		browseModeListener=new BrowseModeListener();
		
		redLightText="<html>No ontologies correctly loaded are present</html>";
		yellowLightText="<html>This should not happen... please report it!</html>";
		greenLightText= "<html>Ontologies present (to browse ontologies you need to plot something first)</html>";
		myTabText="4) Query and browse ontologies";
		myTabTooltip="<html>Query ontologies, plot results (create a graph first!), browse ontologies, extract patterns</html>";
		
		/**
		 * Edit Button: set the graph as editable (var declaration) or not.
		 */
		editNetworkButton=new JButton("Make Editable");
		ActionListener editActionListener=new ActionListener () {
				public void actionPerformed(ActionEvent arg0) {
					browserManager.setEditable();
				
			}
		};
		editNetworkButton.addActionListener(editActionListener);
		
		/**
		 * Capture Pattern: get Current Pattern
		 */
		getNetworkPatternButton=new JButton("Get Pattern");
		ActionListener getSnapshotListener=new ActionListener () {
			public void actionPerformed(ActionEvent arg0) {
				String pname=JOptionPane.showInputDialog("Insert pattern name : ");
				if(pname!=null)
					browserManager.getSnapshot(pname);
			
			}
		};
		getNetworkPatternButton.addActionListener(getSnapshotListener);
		
		
		/**
		 * New network: creates a new Cytoscape Network
		 */
		
		newNetworkButton=new JButton("Create new network");
		ActionListener addNetworkListener=new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				browserManager.makeNewCytoscapePanel((String)queryEnginesBox.getSelectedItem());
			}
			
		};
		newNetworkButton.addActionListener(addNetworkListener);
		
		newNetworkPanel=new JPanel();
		newNetworkPanel.setBorder(new TitledBorder("Network"));
		newNetworkPanel.add(newNetworkButton);
		
		
		patternPanel=new JPanel();
		patternPanel.setBorder(new TitledBorder("Pattern definition"));
		patternPanel.add(editNetworkButton);
		patternPanel.add(getNetworkPatternButton);
		

		
		
		
		
		
		
		/**
		 * Buttons and items related to query and display results (they have listeners ouside this method)
		 */
		
		resultTable=new ResultTableViewer(); //Our results
		//resultTable=new JTable();
		makeQueryButton=new JButton("Make Query");
		clearQueryButton=new JButton("Clear Query");
		selectAll=new JButton("Select All");
		plotSelection=new JButton("Plot Selection");
		searchSelection=new JButton("Search Selection");
		makeQueryButton.addActionListener(new QueryExecListener());
		
		ActionListener selectAllListener=new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				resultTable.selectAll();
				
				
			}
			
		};
		clearQueryButton.addActionListener(new ClearButtonListener());
		selectAll.addActionListener(selectAllListener);
		plotSelection.addActionListener(new PlotSelectionListener());
		searchSelection.addActionListener(new SearchSelectionListener());

		/**
		 * Making of the panel
		 */
		
		
		
		upPanel=new JPanel();
		upPanel.setLayout(new BorderLayout());
	
		/**
		 * Note: initBrosweOptions() is not a real function. It changes global properties of this object.
		 * Hence the order in which it called within this constructors matters. It defines engineBox, and requires upPanel.
		 */
		
		
		
		
		
		northPanel=new JPanel();
		northPanel.setLayout(new GridLayout(1,3));
	
		
		
		
		JPanel midPanel=new JPanel();
		midPanel.setLayout(new GridLayout(1,5));
		midPanel.add(makeQueryButton);
		midPanel.add(clearQueryButton);
		midPanel.add(selectAll);
		midPanel.add(plotSelection);
		midPanel.add(searchSelection);
		
		upPanel.add(northPanel,BorderLayout.NORTH);
		upPanel.add(midPanel,BorderLayout.SOUTH);
		currentQueryPanel=new JPanel();
		upPanel.add(currentQueryPanel,BorderLayout.CENTER);
		
		//upPanel.add(variablePanel,BorderLayout.CENTER);
		//upPanel.add(rdqlBrowsePanel,BorderLayout.CENTER);
		setLayout(new GridLayout(2,1));
		add(upPanel);
		
		JScrollPane queryResultScrollPane=new JScrollPane(resultTable);
		queryResultScrollPane.setBorder(new TitledBorder("Results"));
		add(queryResultScrollPane);
		/**
		 * Note: initBrosweOptions() is not a real function. It changes global properties of this object.
		 * Hence the order in which it called within this constructors matters. It defines engineBox, and requires upPanel.
		 */
		initBrowseOptions();
		northPanel.add(queryEnginesBox);
		northPanel.add(newNetworkPanel);
		northPanel.add(patternPanel);
		
		
		
		
	}
	
	private void initBrowseOptions() {
		ArrayList queryItemsList=browserManager.getAvailableQueryElements();
		System.out.println("Available query engines: "+queryItemsList.size());
		queryEngines=new Hashtable();
		if(queryItemsList.size()==0) {
			System.out.println("case 0");
			String[] answer={"NO QUERY AVAILABLE"};
			queryEnginesBox=new JComboBox(answer);
			queryEnginesBox.setBorder(new TitledBorder("Query mode"));
			queryEnginesBox.setEnabled(false);
			currentQueryEngine=null;
			if(currentQueryPanel!=null) {
				upPanel.remove(currentQueryPanel);
				currentQueryPanel=new JPanel();
				upPanel.add(currentQueryPanel,BorderLayout.CENTER);
			}
			makeQueryButton.setEnabled(false);
			clearQueryButton.setEnabled(false);
			plotSelection.setEnabled(false);
			searchSelection.setEnabled(false);
			selectAll.setEnabled(false);
			System.out.println("case 0 completed");
			return;
		}
		int i=0;
		String[] browseOptions=new String[queryItemsList.size()];
		for (Iterator iter = queryItemsList.iterator(); iter.hasNext();) {
			MyQueryEngine queryItem = (MyQueryEngine) iter.next();
			queryEngines.put(queryItem.getLabel(),queryItem);
			browseOptions[i]=queryItem.getLabel();
			i++;
		}
		queryEnginesBox=new JComboBox(browseOptions);
		queryEnginesBox.setBorder(new TitledBorder("Browse mode"));
		queryEnginesBox.addActionListener(browseModeListener);
		
		makeQueryButton.setEnabled(true);
		clearQueryButton.setEnabled(true);
		plotSelection.setEnabled(true);
		searchSelection.setEnabled(true);
		selectAll.setEnabled(true);
		//setting defaults...
		queryEnginesBox.setSelectedIndex(0);
		currentQueryEngine=(MyQueryEngine)queryEngines.get((String)queryEnginesBox.getSelectedItem());
		currentQueryPanel=currentQueryEngine.getPanel();
	}
	
	public void reset() {
		resetQuery();
		
	}
	
	public void resetQuery() {
		Enumeration tempEngines=queryEngines.elements();
		while(tempEngines.hasMoreElements()) {
			((MyQueryEngine)(tempEngines.nextElement())).reset();
		}
		resultTable.setModel(new JenaQueryResultTable());
		
	}
	
	private class BrowseModeListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			System.out.println("Something changed");
			if(queryEngines==null) return;
			
			String newSelection=(String)queryEnginesBox.getSelectedItem();
			System.out.println("Changing query engine to :"+newSelection);
			if(queryEngines==null) {
				System.out.println("No query engines yet.");
				return;
			}
			MyQueryEngine newEngine=(MyQueryEngine) queryEngines.get(newSelection);
			if(currentQueryEngine==newEngine) {
				System.out.println("... I already had it as deafult. Nothing done.");
				return;
			}
			upPanel.remove(currentQueryPanel);
			currentQueryEngine=newEngine;
			currentQueryPanel=newEngine.getPanel();
			upPanel.add(currentQueryPanel,BorderLayout.CENTER);
			
			updateUI();
			validate();
		}
		
	}
	
	private class ClearButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			resetQuery();
			//rdqlQueryResult.display(new MyResultTable());
			
		}
		
	}
	
	private class QueryExecListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			queryResult=currentQueryEngine.makeQuery();
			resultTable.setModel(queryResult);
			//resultTable=new ResultTableViewer(queryResult);
			//System.out.println("Table: "+resultTable.getSelectionModel().toString());
			/*
			resultTable.setRowSelectionAllowed(true);
			resultTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			resultTable.getColumnModel().setColumnSelectionAllowed(false);
			System.out.println("Table: "+resultTable.getSelectionModel().toString());
			*/
		}
		
	}
	
	
	
	
	
	private class PlotSelectionListener implements ActionListener {
		
		String[] myNodesStringArray=null;
		public void actionPerformed(ActionEvent arg0) {
			if(queryResult==null) {
				System.out.println("No query to plot");
				return; //TODO we may also initialize an empty query insetad of this
			}
			
			if(queryResult.getRowCount()==0) {
				System.out.println("Nothing to plot at all ");
				return; //No table
			}
			//System.out.println("BrowserPanel: Mapping selection to a Cytoscape graph");
			if(queryResult.hasGraph()==true) {
				System.out.println("Graph results are unsupported yet");
			}
			if(queryResult.hasTable()==true) {
				System.out.println("I've seen you have selected "+resultTable.getSelectedRowCount()+" rows");
				browserManager.plotValuesInResultTable(queryResult.getSubsetByRows(resultTable.getSelectedRows()));
			}
			
			
		}
			
	}
private class SearchSelectionListener implements ActionListener {
		
		String[] myNodesStringArray=null;
		public void actionPerformed(ActionEvent arg0) {
			if(queryResult==null) {
				System.out.println("No query to search");
				return; //TODO we may also initialize an empty query insetad of this
			}
			
			if(queryResult.getRowCount()==0) {
				System.out.println("Nothing to search at all ");
				return; //No table
			}
			//System.out.println("BrowserPanel: Mapping selection to a Cytoscape graph");
			if(queryResult.hasGraph()==true) {
				System.out.println("Graph results are unsupported yet");
			}
			if(queryResult.hasTable()==true) {
				System.out.println("I've seen you have selected "+resultTable.getSelectedRowCount()+" rows");
				browserManager.searchValuesInResultTable(queryResult.getSubsetByRows(resultTable.getSelectedRows()));
			}
			
			
		}
			
	}
			
		
	
		public void refreshAfterEngineChange() {
			northPanel.remove(queryEnginesBox);
			northPanel.remove(newNetworkPanel);
			northPanel.remove(patternPanel);
			initBrowseOptions();
			resetQuery();
			
			northPanel.add(queryEnginesBox);
			northPanel.add(newNetworkPanel);
			northPanel.add(patternPanel);
		}
	
		

		public String getHelpLink() {
			return "http://www.bioinformatics.org/rdfscape/wiki/Main/HowToBrowseAndQueryOntologies";
		}

		public JPanel getHelpPanel() {
			JPanel help=new JPanel();
			
			help.setLayout(new BoxLayout(help,BoxLayout.Y_AXIS));
			
			JLabel tempLabel=new JLabel(
					"<html>Use this panel to select a query mode. Available queries are SPARQL, RDQL, String based and class based.</html>",
					Utilities.getHelpIcon("queryModeSelection.png"),
					SwingConstants.LEFT);
			tempLabel.setPreferredSize(new Dimension(HelpManager.panelWidth,200));
			help.add(tempLabel);
			help.add(Box.createRigidArea(new Dimension(10,10)));
			
			 tempLabel=new JLabel(
					"<html>The <b>SPARQL</b> query option present a panel where to input the text of the SPARQL query (SELECT).<b>" +
					" The text is initialized with a template for the query, with the prefixes associated to namespaces in the preference panel. <br>" +
					"The <b>RDQL</b> option is equivalent, but it present the text for the older RDQL language.</html>",
					Utilities.getHelpIcon("sparqlQueryPanel.png"),
					SwingConstants.LEFT);
			tempLabel.setPreferredSize(new Dimension(HelpManager.panelWidth,200));
			help.add(tempLabel);
			help.add(Box.createRigidArea(new Dimension(10,10)));
			
			tempLabel=new JLabel(Utilities.getHelpIcon("stringQueryPanel.png"));
			tempLabel.setPreferredSize(new Dimension(HelpManager.panelWidth,200));
			help.add(tempLabel);
			tempLabel=new JLabel(
					"<html>This query method supports string based search. <br>" +
					"All resources whose URI (or the object of one of its property) matches the given string are returned.<br>" +
					"When <b>approx</b> is checked, substring matching is used instead of string matching. <i>We recommend tha you check this option in most of the cases</i></html>");
			tempLabel.setPreferredSize(new Dimension(HelpManager.panelWidth,100));
			help.add(tempLabel);
			help.add(Box.createRigidArea(new Dimension(10,10)));
			
			
			
			tempLabel=new JLabel(Utilities.getHelpIcon("classQueryPanel.png"));
			tempLabel.setPreferredSize(new Dimension(HelpManager.panelWidth,200));
			help.add(tempLabel);
			tempLabel=new JLabel(
						"<html>For ontologies specified in OWL, this query panel allows to select a class, and the relative querty returns all of its instances. <br>" +
						"<b>notes:</b><ul>" +
						"<li>If the ontology is not specified in OWL, the list of classes will be empty</li>" +
						"<li>You need to <b>clear</b> the query in order for the list of classes to be initialized</li>" +
						"<li>Make sure the appropraite level of reasoning is selected (at least RDFS-2 if you want transitive is-A relations to be resolved)</li>" +
						"</ul></html>");
			tempLabel.setPreferredSize(new Dimension(HelpManager.panelWidth,100));
			help.add(tempLabel);
			help.add(Box.createRigidArea(new Dimension(10,10)));
			
			
			 tempLabel=new JLabel(
					"<html>Once you have specified a query in the above panel, <b>make Query</b> will perform the query and display the results in the box below" +
					"(note that color and visibility of results dependends on namespaces as specifies in the relative preference panel. Note also that blank nodes display in light gray)<br>" +
					"You can at this point select rows directly on the table. A Button <b>Select All</b> is a shortcut to select all results displayed on  the table.<br>" +
					"<b>Clear query</b> reset the query panel and clear the list of results.<br>" +
					"It is the selection of results (and not the results themselves) that is objet of subsequent manipulation, like searching and plotting in a network.</html>",
					Utilities.getHelpIcon("makeClearSelectQueryButtons.png"),
					SwingConstants.LEFT);
			tempLabel.setPreferredSize(new Dimension(HelpManager.panelWidth,200));
			help.add(tempLabel);
			help.add(Box.createRigidArea(new Dimension(10,10)));
			 tempLabel=new JLabel(
					"<html>You need to create a network in Cytoscape in order to plot your results there. This is the function of this button.<br>" +
					"Note that networks created by RDFScape (via the <b>create new network</b> button) and networks created in Cytoscape are not equivalent.<br>" +
					"A network created within Cytoscape can be \"understood\" by RDFScape after <b>Map</b> in <b>Map ontologies on my network</b> is performed.<br>" +
					"If the use of RDFScape is only to browse and query ontologies, we suggest to only use networks created through RDFScape.</html>",
					Utilities.getHelpIcon("createNetworkButton.png"),
					SwingConstants.LEFT);
			tempLabel.setPreferredSize(new Dimension(HelpManager.panelWidth,200));
			help.add(tempLabel);
			help.add(Box.createRigidArea(new Dimension(10,10)));
			 tempLabel=new JLabel(
					"<html>Plot the selected entries on the network. Blank nodes and literals will not be plotted, and properties will be plotted as resources.</html>",
					Utilities.getHelpIcon("plotSelectionButton.png"),
					SwingConstants.LEFT);
			tempLabel.setPreferredSize(new Dimension(HelpManager.panelWidth,100));
			help.add(tempLabel);
			help.add(Box.createRigidArea(new Dimension(10,10)));
			 tempLabel=new JLabel(
					"<html>Serach the selected entries in teh current network (this does not apply to literals and blank nodes)</html>",
					Utilities.getHelpIcon("searchSelectionButton.png"),
					SwingConstants.LEFT);
			tempLabel.setPreferredSize(new Dimension(HelpManager.panelWidth,100));
			help.add(tempLabel);
			help.add(Box.createRigidArea(new Dimension(10,10)));
			
			 tempLabel=new JLabel(
					"<html><h2>Browsing the ontology content</h2>" +
					"Once a network is originated from RDFScape (or after a  <b>Map</b> action in <b>Map ontologies on my network</b>, right selecting one node will prompt a menu that will make possible its iterative expansion.<br>" +
					"This provides <i>browsing</b> of the ontology context, as it expand the network via related resources.</html>"
					);
			tempLabel.setPreferredSize(new Dimension(HelpManager.panelWidth,100));
			help.add(tempLabel);
		
			help.add(tempLabel);
			help.add(Box.createRigidArea(new Dimension(10,10)));
			 tempLabel=new JLabel(
					 
					Utilities.getHelpIcon("exampleBrowsing.png"),
					SwingConstants.CENTER);
			tempLabel.setPreferredSize(new Dimension(HelpManager.panelWidth,300));
			help.add(tempLabel);
			help.add(Box.createRigidArea(new Dimension(10,10)));
			tempLabel=new JLabel(
					"<html><b>Make editable</b> adds new entries to the context-sensitive (right click) menu. In particular for both nodes and edges it makes possible to declare them as variables. <br>" +
					"When <b>Get Pattern</b> is selected, the current network (with variables) is taken as a pattern or visual query, and it appears in the <b>Pattern Library</b> section of RDFScape.<br>" +
					"Refer to this section for help on how to use patterns.</html>",
					Utilities.getHelpIcon("patternDefinitionPanel.png"),
					SwingConstants.LEFT);
			tempLabel.setPreferredSize(new Dimension(HelpManager.panelWidth,200));
			help.add(tempLabel);
			help.add(Box.createRigidArea(new Dimension(10,10)));
			tempLabel=new JLabel(
					"<html>Example of variable definition in RDFScape. Note that both nodes and edges can be declared as variables. For labels, a filter (regular expression) can be associated to the varibale. For instance a label made a variable with the filter /P53/ will match in the" +
					"query all labels including the string \"P53\".<br>" +
					"Variable can be converted back to standard nodes." +
					"<br>" +
					"<font color=red>Note: variables are restored as nodes if their original nodes are plotted again to the network.</red></html>",
					Utilities.getHelpIcon("patternEditExample.png"),
					SwingConstants.LEFT);
			tempLabel.setPreferredSize(new Dimension(HelpManager.panelWidth,380));
			help.add(tempLabel);
			
			
			
			
			return help;
		}

		public String getPanelName() {
			return "Browse and Query";
		}

		public int getStatusLevel() {
			if(!RDFScape.getOntologyManager().ontologiesPresentAndCorrectlyLoaded()) return 1;
			else return 3;
		}

		
		public void refresh() {
			resetQuery();
			
		}
	
	
	
}
