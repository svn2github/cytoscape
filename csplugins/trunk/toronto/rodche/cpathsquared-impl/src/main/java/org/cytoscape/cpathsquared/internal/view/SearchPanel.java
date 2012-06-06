package org.cytoscape.cpathsquared.internal.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.cytoscape.cpathsquared.internal.CPath2Factory;
import org.cytoscape.cpathsquared.internal.CPath2Factory.SearchFor;
import org.cytoscape.cpathsquared.internal.view.GuiUtils.ToolTipsSearchHitsJList;
import org.cytoscape.util.swing.CheckBoxJList;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskMonitor;

import cpath.client.CPath2Client;
import cpath.client.util.NoResultsFoundException;
import cpath.service.jaxb.SearchHit;
import cpath.service.jaxb.SearchResponse;
import cpath.service.jaxb.TraverseEntry;
import cpath.service.jaxb.TraverseResponse;


final class SearchPanel extends JPanel
{
    private final HitsFilterPanel filterPanel;
	private final JList resList; 
    private final JTextPane summaryTextPane;
    private final DetailsPanel detailsPanel;
    private final JScrollPane ppwListScrollPane;
    private static final String ENTER_TEXT = "Enter a keyword (e.g., gene/protein name or ID)";
    private final CheckBoxJList organismList;
    private final CheckBoxJList dataSourceList;
    private final JTextField searchField;
    private final JLabel info;

	public SearchPanel() 
    {	   	 	
		setLayout(new BorderLayout());
		
		// Assembly the query panel
		JPanel searchQueryPanel = new JPanel();
		searchQueryPanel.setLayout(new BorderLayout());
		
    	organismList = new CheckBoxJList();
    	dataSourceList = new CheckBoxJList();
    	searchField = createSearchField();
        
        // create query field, examples/label, and button
        JButton searchButton = new JButton("Search");
        searchButton.setToolTipText("Full-Text Search");
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                executeSearch(searchField.getText(), 
                	organismList.getSelectedValues(), 
                	dataSourceList.getSelectedValues(),
                	CPath2Factory.searchFor.toString());
            }
        });
        searchButton.setAlignmentX(Component.LEFT_ALIGNMENT);  
                
        PulsatingBorder pulsatingBorder = new PulsatingBorder (searchField);
        searchField.setBorder (BorderFactory.createCompoundBorder(searchField.getBorder(),
                pulsatingBorder));
        searchField.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchField.setMaximumSize(new Dimension(1000, 100));
        
        JEditorPane label = new JEditorPane (
        		"text/html", "Examples:  <a href='TP53'>TP53</a>, " +
                "<a href='BRCA*'>BRCA*</a>, or <a href='SRY'>SRY</a>.");
        label.setEditable(false);
        label.setOpaque(false);
        label.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        label.addHyperlinkListener(new HyperlinkListener() {
            // Update search box with activated example.
            public void hyperlinkUpdate(HyperlinkEvent hyperlinkEvent) {
                if (hyperlinkEvent.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    searchField.setText(hyperlinkEvent.getDescription());
                }
            }
        });
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        Font font = label.getFont();
        Font newFont = new Font (font.getFamily(), font.getStyle(), font.getSize()-2);
        label.setFont(newFont);
        label.setBorder(new EmptyBorder(5,3,3,3));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
               
        info = new JLabel("", SwingConstants.CENTER);
        info.setFocusable(false);
        info.setFont(new Font(info.getFont().getFamily(), info.getFont().getStyle(), info.getFont().getSize()+1));
        info.setForeground(Color.BLUE);
        
        final JRadioButton button1 = new JRadioButton("pathways");
        button1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                CPath2Factory.searchFor = SearchFor.PATHWAY;
            }
        });
        final JRadioButton button2 = new JRadioButton("interactions");
        button2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                CPath2Factory.searchFor = SearchFor.INTERACTION;
            }
        });
        final JRadioButton button3 = new JRadioButton("physical entities");
        button3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                CPath2Factory.searchFor = SearchFor.PHYSICALENTITY;
            }
        });

        ButtonGroup group = new ButtonGroup();
        group.add(button1);
        group.add(button2);
        group.add(button3);
        
		switch (CPath2Factory.searchFor) {
		case PATHWAY:
			button1.setSelected(true);
			break;
		case PHYSICALENTITY:
			button3.setSelected(true);
			break;
		default:
			button2.setSelected(true);
			break;
		}     
        
    	JPanel groupPanel = new JPanel();
        groupPanel.setBorder(new TitledBorder("Search for"));
        groupPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 0;
        groupPanel.add(button1, c);
        c.gridx = 0;
        c.gridy = 1;
        groupPanel.add(button2, c);
        c.gridx = 0;
        c.gridy = 2;
        groupPanel.add(button3, c);
        groupPanel.setMaximumSize(new Dimension(50, 100));
        
        searchQueryPanel.add(groupPanel, BorderLayout.LINE_START);
        searchQueryPanel.add(createOrganismFilterBox(), BorderLayout.CENTER);
        searchQueryPanel.add(createDataSourceFilterBox(), BorderLayout.LINE_END);
       
        JPanel keywordPane = new JPanel();
        keywordPane.setLayout(new FlowLayout(FlowLayout.LEFT));
        keywordPane.add(searchButton);
        keywordPane.add(searchField);    
        keywordPane.add(label);
        keywordPane.setMaximumSize(new Dimension(1000, 15));
        
        searchQueryPanel.add(keywordPane, BorderLayout.PAGE_START);
        searchQueryPanel.add(info, BorderLayout.PAGE_END);
    	
        // Assembly the results panel
    	JPanel searchResultsPanel = new JPanel();
    	searchResultsPanel.setLayout(new BoxLayout(searchResultsPanel, BoxLayout.Y_AXIS));
    	
        detailsPanel = new DetailsPanel();
        summaryTextPane = detailsPanel.getTextPane();
    
        //create parent pathways panel (the second tab)
        final JList ppwList = new JList(new DefaultListModel());
        ppwList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        ppwList.setPrototypeCellValue("12345678901234567890");
        JPanel ppwListPane = new JPanel();
        ppwListPane.setLayout(new BorderLayout());
        ppwListScrollPane = new JScrollPane(ppwList);
        ppwListScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        ppwListPane.add(ppwListScrollPane, BorderLayout.CENTER);
                      
        // make (south) tabs
        JTabbedPane southPane = new JTabbedPane(); 
        southPane.add("Summary", detailsPanel);
        southPane.add("Parent Pathways", ppwListPane);
  
        // search hits list
        resList = new ToolTipsSearchHitsJList();
        resList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        resList.setPrototypeCellValue("12345678901234567890");
        // define a list item selection listener which updates the details panel, etc..
        resList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
            	ToolTipsSearchHitsJList l = (ToolTipsSearchHitsJList) listSelectionEvent.getSource();
                int selectedIndex = l.getSelectedIndex();
                //  ignore the "unselect" event.
                if (!listSelectionEvent.getValueIsAdjusting()) {
                    if (selectedIndex >=0) {
                    	SearchHit item = (SearchHit) l.getModel().getElementAt(selectedIndex);
                		// get/create and show hit's summary
                		String summary = GuiUtils.getSearchHitSummary(item);
                		summaryTextPane.setText(summary);
                		summaryTextPane.setCaretPosition(0);
                		
                		// TODO update pathways list in a Task...
                		DefaultListModel ppwListModel = (DefaultListModel) ppwList.getModel();
						ppwListModel.clear();
						if (!item.getPathway().isEmpty()) {
							TraverseResponse ppwNames = CPath2Factory.traverse(
									"Named/displayName", item.getPathway());
							if (ppwNames != null) {
								Map<String, String> map = new HashMap<String, String>();
								for (TraverseEntry e : ppwNames.getTraverseEntry())
									map.put(e.getUri(), e.getValue().get(0)); //uri: " + e.getUri());
								// update the map values with ppw component counts
								TraverseResponse ppwComponents = CPath2Factory.traverse(
									"Pathway/pathwayComponent", item.getPathway());
								for (TraverseEntry e : ppwComponents.getTraverseEntry()) {
									String val = map.get(e.getUri());
									if(!val.contains(" processes)")) //tmp hack against duplicated parent pathways (search index bug)
										map.put(e.getUri(), val + 
											" (" + e.getValue().size() + " processes)");
								}
								for (String uri : map.keySet())
									ppwListModel.addElement(new NameValuePairListItem(map.get(uri), uri));
							}
						}              			
//               			repaint();
                    }
                }
            }
        });
        
        JPanel hitListPane = new JPanel();
        hitListPane.setLayout(new BorderLayout());
        JScrollPane hitListScrollPane = new JScrollPane(resList);
        hitListScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // make (north) tabs       
        JSplitPane vSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, hitListScrollPane, southPane);
        vSplit.setDividerLocation(200);
        hitListPane.add(vSplit, BorderLayout.CENTER);
        
        //  Create search results extra filtering panel
        this.filterPanel = new HitsFilterPanel(resList);
        
        //  Create the Split Pane
        JSplitPane hSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, filterPanel, hitListPane);
        hSplit.setDividerLocation(200);
        hSplit.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchResultsPanel.add(hSplit);
        
        // finish
        JSplitPane queryAndResults = new JSplitPane(JSplitPane.VERTICAL_SPLIT, searchQueryPanel, searchResultsPanel);
        queryAndResults.setDividerLocation(160);
        add(queryAndResults);         
    }

      
    private final JComponent createOrganismFilterBox() {	
    	Map<String,String> map = CPath2Factory.getAvailableOrganisms();
    	//make sorted by name list
    	SortedSet<NameValuePairListItem> items = new TreeSet<NameValuePairListItem>();
    	for(String o : map.keySet()) {
    		items.add(new NameValuePairListItem(map.get(o), o));
    	}
    	DefaultListModel model = new DefaultListModel();
    	for(NameValuePairListItem nvp : items) {
    		model.addElement(nvp);
    	}
    	organismList.setModel(model);
        organismList.setToolTipText("Select Organisms");
        organismList.setAlignmentX(Component.LEFT_ALIGNMENT);
        JScrollPane scroll = new JScrollPane(organismList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(new TitledBorder("Limit to organism(s):"));
        return scroll;
    }
    
    private final JComponent createDataSourceFilterBox() {
        DefaultListModel dataSourceBoxModel = new DefaultListModel(); 
        Map<String,String> map = CPath2Factory.getLoadedDataSources();
    	for(String d : map.keySet()) {
    		dataSourceBoxModel.addElement(new NameValuePairListItem(map.get(d), d));
    	}
        
        dataSourceList.setModel(dataSourceBoxModel);
        dataSourceList.setToolTipText("Select Datasources");
        dataSourceList.setAlignmentX(Component.LEFT_ALIGNMENT);
        JScrollPane scroll = new JScrollPane(dataSourceList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(new TitledBorder("Limit to datasource(s):"));
        return scroll;
    }
    
    
    /**
     * Creates the Search Field and associated listener(s)
     *
     * @return JTextField Object.
     */
    private final JTextField createSearchField() {
        final JTextField searchField = new JTextField(ENTER_TEXT.length());
        searchField.setText(ENTER_TEXT);
        searchField.setToolTipText(ENTER_TEXT);
        searchField.setMaximumSize(new Dimension(200, 9999));
        searchField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent focusEvent) {
                if (searchField.getText() != null
                        && searchField.getText().startsWith("Enter")) {
                    searchField.setText("");
                }
            }
        });
        return searchField;
    }
    
    
    /**
     * Prepares and executes a search query.
     * 
     * @param keyword
     * @param organism
     * @param datasource
     * @param biopaxType
     */
    private void executeSearch(final String keyword, 
    		final Object[] organism, 
    		final Object[] datasource,
    		final String biopaxType) {
        
        final Set<String> organisms = new HashSet<String>();
        for(Object it : organism)
        	organisms.add(((NameValuePairListItem) it).getValue());
        
    	final Set<String> datasources = new HashSet<String>();
        for(Object it : datasource)
        	datasources.add(((NameValuePairListItem) it).getValue());
    	
        if (keyword == null || keyword.trim().length() == 0 || keyword.startsWith(ENTER_TEXT)) {
			info.setText("Error: Please enter a Gene Name or ID!");
		} else {
			info.setText("");
			TaskFactory search = CPath2Factory.newTaskFactory(new Task() {
				@Override
				public void run(TaskMonitor taskMonitor) throws Exception {
					DefaultListModel listModel = (DefaultListModel) resList.getModel();
					try {
						taskMonitor.setProgress(0);
						taskMonitor.setStatusMessage("Executing search for " + keyword);
						CPath2Client client = CPath2Factory.newClient();
						client.setOrganisms(organisms);
						client.setType(biopaxType);
						if (datasource != null)
							client.setDataSources(datasources);
						SearchResponse searchResponse = (SearchResponse) client.search(keyword);
						listModel.clear();
						List<SearchHit> searchHits = searchResponse.getSearchHit();
						if (searchHits.size() > 0)
							for (SearchHit searchHit : searchHits)
								listModel.addElement(searchHit);
						filterPanel.update(searchResponse);
						info.setText("Hits found:  " + searchResponse.getNumHits() 
								+ "; retrieved: " + searchHits.size()
								+ " (page: " + searchResponse.getPageNo() + ")");
					} catch (NoResultsFoundException e) {
						info.setText("No matches found for:  " + keyword);
						listModel.clear();
						filterPanel.update(new SearchResponse());
					} catch (Throwable e) { 
						// using Throwable helps catch unresolved runtime dependency issues!
						info.setText("Failed:  " + e);
						throw new RuntimeException(e);
					} finally {
						taskMonitor.setStatusMessage("Done");
						taskMonitor.setProgress(1);
					}
				}

				@Override
				public void cancel() {
				}
			});

			CPath2Factory.getTaskManager().execute(search.createTaskIterator());
		}
    }    
 
}