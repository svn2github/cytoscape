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

import org.cytoscape.cpathsquared.internal.CPath2WebService;
import org.cytoscape.cpathsquared.internal.task.ExecuteSearchTaskFactory;
import org.cytoscape.cpathsquared.internal.task.ExecuteSearchTaskFactory.ResultHandler;

/**
 * Search Box Panel.
 *
 * @author Ethan Cerami, Igor Rodchenkov (refactoring)
 */
public class SearchQueryPanel extends JPanel {
    private JButton searchButton;
    private final CPath2WebService webApi;
    private static final String ENTER_TEXT = "Enter Gene Name or ID";
    private PulsatingBorder pulsatingBorder;
    private JList organismBox;
    private JList dataSourceBox;
	private final CPath2Factory factory;
	
	
    /**
     * Constructor.
     *
     * @param webApi CPath Web Service Object.
     */
    public SearchQueryPanel(CPath2WebService webApi, CPath2Factory factory) {
        this.webApi = webApi;
        this.factory = factory;
        
        GradientHeader header = new GradientHeader("Search");
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        BoxLayout boxLayout = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(boxLayout);
        add (header);
        add (Box.createVerticalStrut(5));

        JPanel centerPanel = new JPanel();
        BoxLayout boxLayoutMain = new BoxLayout(centerPanel, BoxLayout.X_AXIS);
        centerPanel.setLayout(boxLayoutMain);
        centerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        final JTextField searchField = createSearchField();

        pulsatingBorder = new PulsatingBorder (searchField);
        searchField.setBorder (BorderFactory.createCompoundBorder(searchField.getBorder(),
                pulsatingBorder));

        organismBox = createOrganismBox();
        dataSourceBox = createDataSourceBox();
        
        searchButton = createSearchButton(searchField);

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

        organismBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerPanel.add(new JScrollPane(organismBox));
        
        dataSourceBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerPanel.add(new JScrollPane(dataSourceBox));
        
        searchButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerPanel.add(searchButton);

        add(centerPanel);
        add(label);
    }


    private final JList createOrganismBox() {
       	//TODO fill the lists dynamically (from the web service)
    	DefaultListModel organismBoxModel = new DefaultListModel();
    	
    	organismBoxModel.addElement(new FilterBoxItem("All Organisms", null));
    	organismBoxModel.addElement(new FilterBoxItem("Human", "urn:miriam:taxonomy:9606"));
        organismBoxModel.addElement(new FilterBoxItem("Mouse", "urn:miriam:taxonomy:10090"));
        organismBoxModel.addElement(new FilterBoxItem("Rat", "urn:miriam:taxonomy:10116"));
        organismBoxModel.addElement(new FilterBoxItem("S. cerevisiae", "urn:miriam:taxonomy:4932"));
    	
    	JList orgBox = new JList(organismBoxModel);
        orgBox.setToolTipText("Select Organisms");
        //orgBox.setMaximumSize(new Dimension(200, 9999));
        return orgBox;
    }
    

    private final JList createDataSourceBox() {
       	//TODO fill the lists dynamically (from the web service)
        DefaultListModel dataSourceBoxModel = new DefaultListModel();
        dataSourceBoxModel.addElement(new FilterBoxItem("All Datasources", null));
        dataSourceBoxModel.addElement(new FilterBoxItem("Reactome", "urn:miriam:reactome"));
        dataSourceBoxModel.addElement(new FilterBoxItem("NCI_Nature Curated", "urn:miriam:pid.pathway"));
    	
    	JList dsBox = new JList(dataSourceBoxModel);
        dsBox.setToolTipText("Select Datasources");
        //dsBox.setMaximumSize(new Dimension(200, 9999));
        return dsBox;
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

    /**
     * Creates the Search Button and associated action listener.
     *
     * @param searchField JTextField searchField
     * @return
     */
    private final JButton createSearchButton(final JTextField searchField) {
        URL url = GradientHeader.class.getResource("resources/run_tool.gif");
        ImageIcon icon = new ImageIcon(url);
        searchButton = new JButton("Search");
        searchButton.setToolTipText("Execute Search");
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                executeSearch(searchField.getText(), 
                		organismBox.getSelectedValues(), dataSourceBox.getSelectedValues());
            }
        });
        return searchButton;
    }

    
    private final void executeSearch(final String keyword, final Object[] organism, final Object[] datasource) {
        
        Set<String> organisms = new HashSet<String>();
        for(Object it : organism)
        	organisms.add(((FilterBoxItem)it).getValue());
        
    	Set<String> datasources = new HashSet<String>();
        for(Object it : datasource)
        	datasources.add(((FilterBoxItem)it).getValue());
    	
    	Window window = factory.getCySwingApplication().getJFrame();
        if (keyword == null || keyword.trim().length() == 0
                || keyword.startsWith(ENTER_TEXT)) {
            JOptionPane.showMessageDialog(window, "Please enter a Gene Name or ID.",
                    "Search Error", JOptionPane.ERROR_MESSAGE);
        } else {
        	ResultHandler handler = new ResultHandler() {
        		@Override
        		public void finished(int matchesFound) throws Exception {
                    if (matchesFound == 0) {
                        JOptionPane.showMessageDialog(factory.getCySwingApplication().getJFrame(),
                                "No matches found for:  " + keyword + 
                                "\nPlease try a different search term or filter.",
                                "No matches found.",
                                JOptionPane.WARNING_MESSAGE);
                    }
        		}
        	};
            ExecuteSearchTaskFactory search = new ExecuteSearchTaskFactory
                    (webApi, keyword.trim(), organisms, datasources, handler);
            factory.getTaskManager().execute(search);
        }
    }

    
    /**
     * Initializes Focus to the Search Button.
     */
    public final void initFocus() {
        searchButton.requestFocusInWindow();
    }
}
