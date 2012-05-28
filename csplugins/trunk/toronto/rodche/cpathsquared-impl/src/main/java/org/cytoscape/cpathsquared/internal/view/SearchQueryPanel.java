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

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.cytoscape.cpathsquared.internal.CPath2;
import org.cytoscape.cpathsquared.internal.CPath2Factory;
import org.cytoscape.cpathsquared.internal.task.ExecuteSearchTask;
import org.cytoscape.cpathsquared.internal.task.ResultHandler;
import org.cytoscape.util.swing.CheckBoxJList;
import org.cytoscape.work.TaskFactory;

import java.util.*;

/**
 * Search Box Panel.
 *
 */
public final class SearchQueryPanel extends JPanel {
    private static final String ENTER_TEXT = "Enter Gene Name or ID";
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
    	
        GradientHeader header = new GradientHeader("Search");
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        BoxLayout boxLayout = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(boxLayout);
        add(header);
        add(Box.createVerticalStrut(5));

        JPanel queryPanel = new JPanel();
        queryPanel.setLayout(new BoxLayout(queryPanel, BoxLayout.Y_AXIS));
        queryPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        queryPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        
        JPanel queryFiltersPanel = new JPanel();
        queryFiltersPanel.setLayout(new BoxLayout(queryFiltersPanel, BoxLayout.X_AXIS));
        queryFiltersPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        queryFiltersPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        
        // create query field, examples/label, and button
        JButton searchButton = new JButton("Search");
        searchButton.setToolTipText("Execute Search");
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                executeSearch(searchField.getText(), organismList.getSelectedValues(), dataSourceList.getSelectedValues());
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
        
      	queryPanel.add(searchField); 
      	queryPanel.add(label);
      	queryPanel.add(searchButton);
      	queryFiltersPanel.add(queryPanel);
      	queryFiltersPanel.add(createOrganismFilterBox());
      	queryFiltersPanel.add(createDataSourceFilterBox());
      	
        add(queryFiltersPanel);
    }


    private final JComponent createOrganismFilterBox() {	
    	SortedJListModel<NameValuePairListItem> model = new SortedJListModel<NameValuePairListItem>();
    	Map<String,String> map = CPath2.getAvailableOrganisms();
    	for(String o : map.keySet()) {
    		model.addElement(new NameValuePairListItem(map.get(o), o));
    	}
    	organismList.setModel(model);
        organismList.setToolTipText("Select Organisms");
        organismList.setAlignmentX(Component.LEFT_ALIGNMENT);
        JScrollPane scroll = new JScrollPane(organismList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        return scroll;
    }
    
    private final JComponent createDataSourceFilterBox() {
        DefaultListModel dataSourceBoxModel = new DefaultListModel();
        
        Map<String,String> map = CPath2.getLoadedDataSources();
    	for(String d : map.keySet()) {
    		dataSourceBoxModel.addElement(new NameValuePairListItem(map.get(d), d));
    	}
        
        dataSourceList.setModel(dataSourceBoxModel);
        dataSourceList.setToolTipText("Select Datasources");
        dataSourceList.setAlignmentX(Component.LEFT_ALIGNMENT);
        JScrollPane scroll = new JScrollPane(dataSourceList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
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
                    		organismList.getSelectedValues(), dataSourceList.getSelectedValues());
                }
            }
        });
        return searchField;
    }
    
    
    private final void executeSearch(final String keyword, final Object[] organism, final Object[] datasource) {
        
        Set<String> organisms = new HashSet<String>();
        for(Object it : organism)
        	organisms.add(((NameValuePairListItem)it).getValue());
        
    	Set<String> datasources = new HashSet<String>();
        for(Object it : datasource)
        	datasources.add(((NameValuePairListItem)it).getValue());
    	
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
            CPath2Factory.getTaskManager().execute(search.createTaskIterator());
        }
    }
   
    static class SortedJListModel<E> extends AbstractListModel {
		SortedSet<E> items;

		public SortedJListModel() {
			super();
			items = new TreeSet<E>();
		}

		@Override
		public Object getElementAt(int index) {
			return items.toArray()[index];
		}

		@Override
		public int getSize() {
			return items.size();
		}
		
		public void addAll(Object elements[]) {
		    Collection c = Arrays.asList(elements);
		    items.addAll(c);
		    fireContentsChanged(this, 0, getSize());
		}
		
		public void addElement(E o) {
			items.add(o);
			fireContentsChanged(this, 0, getSize());
		}
		
		public boolean removeElement(Object element) {
			boolean removed = items.remove(element);
			if (removed) {
				fireContentsChanged(this, 0, getSize());
			}
			return removed;
		}

		public void clear() {
			items.clear();
			fireContentsChanged(this, 0, getSize());
		}

		public boolean contains(Object element) {
			return items.contains(element);
		}

		public Object firstElement() {
			return items.first();
		}

		public Iterator iterator() {
			return items.iterator();
		}

		public Object lastElement() {
			return items.last();
		}
	}
}
