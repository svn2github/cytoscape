package org.cytoscape.cpathsquared.internal.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.cytoscape.cpathsquared.internal.CPath2Factory;
import org.cytoscape.cpathsquared.internal.CPath2Properties;
import org.cytoscape.cpathsquared.internal.CPath2Properties.SearchFor;
import org.cytoscape.cpathsquared.internal.task.ExecuteSearchTask;
import org.cytoscape.cpathsquared.internal.task.ResultHandler;
import org.cytoscape.util.swing.CheckBoxJList;
import org.cytoscape.work.TaskFactory;

import java.util.*;

/**
 * Search Box Panel.
 *
 */
final class SearchQueryPanel extends JPanel {
	
    private static final String ENTER_TEXT = "Enter a keyword (e.g., gene/protein name or ID)";
    private final CheckBoxJList organismList;
    private final CheckBoxJList dataSourceList;
    private final JTextField searchField;
   
    /**
     * Constructor.
     */
    public SearchQueryPanel() {  
    	this.organismList = new CheckBoxJList();
    	this.dataSourceList = new CheckBoxJList();
    	this.searchField = createSearchField();
    	
        setLayout(new BorderLayout());
        
        // create query field, examples/label, and button
        JButton searchButton = new JButton("Search");
        searchButton.setToolTipText("Full-Text Search");
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                executeSearch(searchField.getText(), 
                	organismList.getSelectedValues(), 
                	dataSourceList.getSelectedValues(),
                	CPath2Properties.searchFor.toString());
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
        
        final JRadioButton button1 = new JRadioButton("pathways");
        button1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                CPath2Properties.searchFor = SearchFor.PATHWAY;
            }
        });
        final JRadioButton button2 = new JRadioButton("interactions");
        button2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                CPath2Properties.searchFor = SearchFor.INTERACTION;
            }
        });
        final JRadioButton button3 = new JRadioButton("physical entities");
        button3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                CPath2Properties.searchFor = SearchFor.PHYSICALENTITY;
            }
        });

        ButtonGroup group = new ButtonGroup();
        group.add(button1);
        group.add(button2);
        group.add(button3);
        
		switch (CPath2Properties.searchFor) {
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
        
        add(groupPanel, BorderLayout.LINE_START);
        add(createOrganismFilterBox(), BorderLayout.CENTER);
        add(createDataSourceFilterBox(), BorderLayout.LINE_END);
       
        JPanel keywordPane = new JPanel();
        keywordPane.setLayout(new FlowLayout(FlowLayout.LEFT));
        keywordPane.add(searchButton);
        keywordPane.add(searchField);    
        keywordPane.add(label);
        keywordPane.setMaximumSize(new Dimension(1000, 15));
        
        add(keywordPane, BorderLayout.SOUTH);
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
    
    
    private final void executeSearch(final String keyword, 
    		final Object[] organism, 
    		final Object[] datasource,
    		final String biopaxType) {
        
        Set<String> organisms = new HashSet<String>();
        for(Object it : organism)
        	organisms.add(((NameValuePairListItem) it).getValue());
        
    	Set<String> datasources = new HashSet<String>();
        for(Object it : datasource)
        	datasources.add(((NameValuePairListItem) it).getValue());
    	
    	Window window = CPath2Factory.getCySwingApplication().getJFrame();
        if (keyword == null || keyword.trim().length() == 0
                || keyword.startsWith(ENTER_TEXT)) {
            JOptionPane.showMessageDialog(window, "Please enter a Gene Name or ID.",
                    "Search Error", JOptionPane.ERROR_MESSAGE);
        } else {
        	final ResultHandler handler = new ResultHandler() {
        		@Override
        		public void finished(int matchesFound) throws Exception {
                    if (matchesFound == 0) {
                        JOptionPane.showMessageDialog(CPath2Factory.getCySwingApplication().getJFrame(),
                                "No matches found for:  " + keyword + 
                                "\nPlease try a different search term or filter.",
                                "No matches found.",
                                JOptionPane.WARNING_MESSAGE);
                    }
        		}
        	};
            TaskFactory search = CPath2Factory.newTaskFactory(new ExecuteSearchTask(
            		keyword.trim(), organisms, datasources, handler, biopaxType));
            CPath2Factory.getTaskManager().execute(search.createTaskIterator());
        }
    }
    
}
