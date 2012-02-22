package org.cytoscape.cpathsquared.internal.view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.cytoscape.cpathsquared.internal.CPath2Factory;
import org.cytoscape.cpathsquared.internal.task.ExecuteSearchTask;
import org.cytoscape.cpathsquared.internal.task.ResultHandler;
import org.cytoscape.work.TaskFactory;


/**
 * Search Box Panel.
 *
 */
public class SearchQueryPanel extends JPanel {
    private JButton searchButton;
    private static final String ENTER_TEXT = "Enter Gene Name or ID";
    private final JList organismBox;
    private final JList dataSourceBox;
    private final JTextField searchField;
	
    /**
     * Constructor.
     */
    public SearchQueryPanel() {  
    	this.organismBox = new JList();
    	this.dataSourceBox = new JList();
    	this.searchField = createSearchField();        
    	
        GradientHeader header = new GradientHeader("Search");
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        BoxLayout boxLayout = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(boxLayout);
        add(header);
        add(Box.createVerticalStrut(5));

        JPanel centerPanel = new JPanel();
        BoxLayout boxLayoutMain = new BoxLayout(centerPanel, BoxLayout.X_AXIS);
        centerPanel.setLayout(boxLayoutMain);
        centerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // create query field, examples/label, and button
        URL url = GradientHeader.class.getResource("resources/run_tool.gif");
        ImageIcon icon = new ImageIcon(url);
        searchButton = new JButton("Search");
        searchButton.setToolTipText("Execute Search");
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                executeSearch(searchField.getText(), organismBox.getSelectedValues(), dataSourceBox.getSelectedValues());
            }
        });
        searchButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        PulsatingBorder pulsatingBorder = new PulsatingBorder (searchField);
        searchField.setBorder (BorderFactory.createCompoundBorder(searchField.getBorder(),
                pulsatingBorder));
        searchField.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JEditorPane label = new JEditorPane ("text/html", "Examples:  <a href='TP53'>TP53</a>, " +
                "<a href='BRCA1'>BRCA1</a>, or <a href='SRY'>SRY</a>.");
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
        
        centerPanel.add(searchField);
        centerPanel.add(createOrganismFilterBox());
        centerPanel.add(createDataSourceFilterBox());
        
        JButton clearButton = new JButton("Clear");
        clearButton.setToolTipText("Clear");
        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                searchField.setText(null);
                organismBox.clearSelection();
                dataSourceBox.clearSelection();
            }
        });
        
        centerPanel.add(clearButton);
        centerPanel.add(searchButton);

        add(centerPanel);
        add(label);
    }


    private final JComponent createOrganismFilterBox() {
    	//TODO fill the lists dynamically (from the web service)
    	DefaultListModel organismBoxModel = new DefaultListModel();
    	organismBoxModel.addElement(new FilterBoxItem("Human", "urn:miriam:taxonomy:9606"));
        organismBoxModel.addElement(new FilterBoxItem("Mouse", "urn:miriam:taxonomy:10090"));
        organismBoxModel.addElement(new FilterBoxItem("Rat", "urn:miriam:taxonomy:10116"));
        organismBoxModel.addElement(new FilterBoxItem("S. cerevisiae", "urn:miriam:taxonomy:4932"));
    	organismBox.setModel(organismBoxModel);
        organismBox.setToolTipText("Select Organisms");
        organismBox.setMaximumSize(new Dimension(200, 9999));
        organismBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        JScrollPane scroll = new JScrollPane(organismBox);
        scroll.setSize(100, 100);
        
        return scroll;
    }
    
    private final JComponent createDataSourceFilterBox() {
        //TODO fill the lists dynamically (from the web service)
        DefaultListModel dataSourceBoxModel = new DefaultListModel();
        dataSourceBoxModel.addElement(new FilterBoxItem("Reactome", "urn:miriam:reactome"));
        dataSourceBoxModel.addElement(new FilterBoxItem("NCI_Nature Curated", "urn:miriam:pid.pathway"));
        dataSourceBox.setModel(dataSourceBoxModel);
        dataSourceBox.setToolTipText("Select Datasources");
        dataSourceBox.setMaximumSize(new Dimension(200, 9999));
        dataSourceBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        JScrollPane scroll = new JScrollPane(dataSourceBox);
        scroll.setSize(100, 100);
        
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
        searchField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                if (keyCode == 10) {
                    executeSearch(searchField.getText(), 
                    		organismBox.getSelectedValues(), dataSourceBox.getSelectedValues());
                }
            }
        });
        return searchField;
    }
    
    
    private final void executeSearch(final String keyword, final Object[] organism, final Object[] datasource) {
        
        Set<String> organisms = new HashSet<String>();
        for(Object it : organism)
        	organisms.add(((FilterBoxItem)it).getValue());
        
    	Set<String> datasources = new HashSet<String>();
        for(Object it : datasource)
        	datasources.add(((FilterBoxItem)it).getValue());
    	
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
            		keyword.trim(), organisms, datasources, handler));
            CPath2Factory.getTaskManager().execute(search);
        }
    }

    
    /**
     * Initializes Focus to the Search Button.
     */
    public final void initFocus() {
        searchButton.requestFocusInWindow();
    }
}
