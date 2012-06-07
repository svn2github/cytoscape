package org.cytoscape.cpathsquared.internal.view;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.lang.StringUtils;
import org.cytoscape.cpathsquared.internal.CPath2Factory;
import org.cytoscape.cpathsquared.internal.CPath2Factory.SearchFor;
import org.cytoscape.cpathsquared.internal.task.ExecuteGetRecordByCPathIdTask;
import org.cytoscape.work.TaskFactory;

import cpath.service.OutputFormat;
import cpath.service.jaxb.SearchHit;
import cpath.service.jaxb.SearchResponse;
import cpath.service.jaxb.TraverseResponse;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Graphical User Interface (GUI) Utilities,
 * a Public Static Factory.
 */
public final class GuiUtils {

    private GuiUtils() {
		throw new AssertionError("not instantiable");
	}

    
	/**
     * Creates a Titled Border with appropriate font settings.
     * @param title Title.
     * @return TitledBorder Object.
     */
    public static TitledBorder createTitledBorder (String title) {
        TitledBorder border = new TitledBorder(title);
        Font font = border.getTitleFont();
        Font newFont = new Font (font.getFamily(), Font.BOLD, font.getSize()+2);
        border.setTitleFont(newFont);
        border.setTitleColor(new Color(102,51,51));
        return border;
    }
    
    public static JScrollPane createOptionsPane() {
    	JPanel panel = new JPanel();
    	panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    	
        // download oprions group
        final JRadioButton button1 = new JRadioButton("Download BioPAX");
        button1.setSelected(true);
        button1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                CPath2Factory.downloadMode = OutputFormat.BIOPAX;
            }
        });
        JTextArea textArea1 = new JTextArea(3, 20);
        textArea1.setLineWrap(true);
        textArea1.setWrapStyleWord(true);
        textArea1.setEditable(false);
        textArea1.setOpaque(false);
        Font font = textArea1.getFont();
        Font smallerFont = new Font(font.getFamily(), font.getStyle(), font.getSize() - 2);
        textArea1.setFont(smallerFont);
        textArea1.setText("Retrieve the full model, i.e., the BioPAX "
                + "representation.  In this representation, nodes within a network can "
                + "refer either to physical entities or processes.");
        textArea1.setBorder(new EmptyBorder(5, 20, 0, 0));
       
        final JRadioButton button2 = new JRadioButton("Download SIF");
        button2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                CPath2Factory.downloadMode = OutputFormat.BINARY_SIF;
            }
        });
        JTextArea textArea2 = new JTextArea(3, 20);
        textArea2.setLineWrap(true);
        textArea2.setWrapStyleWord(true);
        textArea2.setEditable(false);
        textArea2.setOpaque(false);
        textArea2.setFont(smallerFont);
        textArea2.setText("Retrieve a simplified binary network, as inferred from the original "
                + "BioPAX representation.  In this representation, nodes within a network refer "
                + "to physical entities only, and edges refer to inferred interactions.");
        textArea2.setBorder(new EmptyBorder(5, 20, 0, 0));

        ButtonGroup group = new ButtonGroup();
        group.add(button1);
        group.add(button2);

    	JPanel configPanel = new JPanel();
        configPanel.setBorder(new TitledBorder("Download Options"));
        configPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.gridx = 0;
        c.gridy = 0;
        configPanel.add(button1, c);
        c.gridy = 1;
        configPanel.add(textArea1, c);
        c.gridy = 2;
        configPanel.add(button2, c);
        c.gridy = 3;
        configPanel.add(textArea2, c); 
        //  Add invisible filler to take up all remaining space
        c.gridy = 4;
        c.weighty = 1.0;
        JPanel filler = new JPanel();
        configPanel.add(filler, c);
        
        panel.add(configPanel);
        return new JScrollPane(panel);
    }
    
	
	public static JDialog createDownloadDetails(List<SearchHit> passedRecordList) {
		return new DownloadDetails(passedRecordList);
	}


	public static JPanel createTopPathwaysPanel() {
        final JPanel panel = new JPanel(); // to return
       
//		CPath2.addApiListener(panel); // required if 
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        //  Create Info Panel (the first tab)
        final DetailsPanel detailsPanel = new DetailsPanel();
        final JTextPane summaryTextPane = detailsPanel.getTextPane();
        
//        //create participants (the third tab is about Entity References, Complexes, and Genes...)
//        final JList mList = new JList(new DefaultListModel());
//        mList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
//        mList.setPrototypeCellValue("12345678901234567890");
//        JPanel mListPane = new JPanel();
//        mListPane.setLayout(new BorderLayout());
//        JScrollPane mListScrollPane = new JScrollPane(mList);
//        mListScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
//        mListPane.add(mListScrollPane, BorderLayout.CENTER);
        
        // make (south) tabs
        JTabbedPane southPane = new JTabbedPane(); 
        southPane.add("Summary", detailsPanel);
//        southPane.add("Members", mListPane);
        
        //create top pathways panel (north)
        //TODO if slow, - wrap as a new Task and run...
        SearchResponse resp = CPath2Factory.newClient().getTopPathways();
        TopPathwaysJList tpwJList = new TopPathwaysJList();
		DefaultListModel listModel = (DefaultListModel) tpwJList.getModel();
		listModel.clear();
		List<SearchHit> searchHits = resp.getSearchHit();
		for (SearchHit searchHit : searchHits)
			listModel.addElement(searchHit);
		
        //  Create off-line filtering panel
        final HitsFilterPanel filterPanel = new HitsFilterPanel(tpwJList);
        filterPanel.update(resp);
        tpwJList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tpwJList.setPrototypeCellValue("12345678901234567890");
        // define a list item selection listener which updates the details panel, etc..
        tpwJList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
            	TopPathwaysJList l = (TopPathwaysJList) listSelectionEvent.getSource();
                int selectedIndex = l.getSelectedIndex();
                //  ignore the "unselect" event.
                if (!listSelectionEvent.getValueIsAdjusting()) {
                    if (selectedIndex >=0) {
                    	SearchHit item = (SearchHit) l.getModel().getElementAt(selectedIndex);
                		// get/create and show hit's summary
                		String summary = getSearchHitSummary(item);
                		summaryTextPane.setText(summary);
                		summaryTextPane.setCaretPosition(0);
                		
                		// update members list
//						DefaultListModel m = (DefaultListModel) mList.getModel();
//						m.clear();
//                		TraverseResponse tres = CPath2Factory.traverse(
//                			"Pathway/pathwayComponent:Named/displayName",
//								Collections.singleton(item.getUri()));
//						if (tres != null)
//							if (!tres.getTraverseEntry().isEmpty())
//								for (String v : tres.getTraverseEntry().get(0).getValue())
//									m.addElement(v);						
                    }
                }
            }
        });
        
        JPanel tpwFilterListPanel = new JPanel();
        tpwFilterListPanel.setLayout(new BorderLayout());
        JScrollPane tpwListScrollPane = new JScrollPane(tpwJList);
        tpwListScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        //add the filter text field and scroll pane
        tpwFilterListPanel.add(tpwJList.getFilterField(), BorderLayout.NORTH);
        tpwFilterListPanel.add(tpwListScrollPane, BorderLayout.CENTER);
        
        JSplitPane vSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tpwFilterListPanel, southPane);
        vSplit.setDividerLocation(300);
        
        //  Create the Split Pane
        JSplitPane hSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, filterPanel, vSplit);
        hSplit.setDividerLocation(300);
        hSplit.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(hSplit);
        panel.repaint();
            
        return panel;
	}

	
	static String getSearchHitSummary(SearchHit item) {
		// get/create and show hit's summary
		StringBuilder html = new StringBuilder();
		html.append("<html>");

		if (item.getName() != null)
			html.append("<h2>" + item.getName() + "</h2>");
		html.append("<h3>Class: " + item.getBiopaxClass() + ", URI: "
				+ item.getUri() + "</h3>");

		String primeExcerpt = item.getExcerpt();
		if (primeExcerpt != null) {
			html.append("<h4>Excerpt:</h4>");
			html.append("<span class='excerpt'>" + primeExcerpt + "</span><br/>");
		}
		
		List<String> items = item.getOrganism();
		if (items != null && !items.isEmpty()) {
			html.append("<h3>Organisms:</h3>"
				+ StringUtils.join(items, "<br/>"));
		}
		
		items = item.getDataSource();
		if (items != null && !items.isEmpty()) {
			html.append("<h3>Data sources:</h3>"
				+ StringUtils.join(items, "<br/>"));
		}

		// add about components/participants	
		String path = null;
		if("Pathway".equalsIgnoreCase(item.getBiopaxClass()))
			path = "Pathway/pathwayComponent";
		else if("Complex".equalsIgnoreCase(item.getBiopaxClass()))
				path = "Complex/component";
		else if(CPath2Factory.searchFor == SearchFor.INTERACTION)
			path = "Interaction/participant";
		else if(CPath2Factory.searchFor == SearchFor.PHYSICALENTITY)
				path = "PhysicalEntity/memberPhysicalEntity";		
		TraverseResponse members = CPath2Factory
			.traverse(path + ":Named/displayName", Collections.singleton(item.getUri()));
		if (members != null) {
			List<String> values  = members.getTraverseEntry().get(0).getValue();
			if (!values.isEmpty())
				html.append("<h3>Contains " + values.size()
					+ " components/members (direct):</h3>" + StringUtils.join(values, "<br/>"));
		}

		html.append("</html>");

		return html.toString();
	}
	
	

	static class ToolTipsSearchHitsJList extends JList {

		public ToolTipsSearchHitsJList() {
			super(new DefaultListModel());
		}

		@Override
		public String getToolTipText(MouseEvent mouseEvent) {
			int index = locationToIndex(mouseEvent.getPoint());
			if (index >= 0 && getModel() != null) {
				SearchHit record = (SearchHit) getModel().getElementAt(index);
				StringBuilder html = new StringBuilder();
				html.append("<html><table cellpadding=10><tr><td>");
				html.append("<B>").append(record.getBiopaxClass());
				if (!record.getDataSource().isEmpty())
					html.append("&nbsp;").append(
							record.getDataSource().toString());
				if (!record.getOrganism().isEmpty())
					html.append("&nbsp;").append(
							record.getOrganism().toString());
				html.append("</B>&nbsp;");
				html.append("</td></tr></table></html>");
				return html.toString();
			} else {
				return null;
			}
		}

	}
	
	
    static class TopPathwaysJList extends FilterdJList<SearchHit> {
    	
    	@Override
    	public String getToolTipText(MouseEvent mouseEvent) {
    		int index = locationToIndex(mouseEvent.getPoint());
    		if (-1 < index) {
    			SearchHit record = (SearchHit) getModel().getElementAt(index);
    			StringBuilder html = new StringBuilder();
    			html.append("<html><table cellpadding=10><tr><td>");
    			html.append("<B>");
    			if(!record.getDataSource().isEmpty())
    				html.append("&nbsp;").append(record.getDataSource().toString());
    			if(!record.getOrganism().isEmpty())
    				html.append("&nbsp;").append(record.getOrganism().toString());
    			html.append("</B>&nbsp;");
    			html.append("</td></tr></table></html>");
    			return html.toString();
    		} else {
    			return null;
    		}
    	}
    }

	
    static class FilterdJList<T> extends JList {
    	private final FilterField<T> filterField;
    	private final int DEFAULT_FIELD_WIDTH = 20;
    	
    	public FilterdJList() {
    		super();
    		setModel(new FilterListModel<T>());
			filterField = new FilterField<T>(DEFAULT_FIELD_WIDTH);
		}
    	   	
    	public void setModel(ListModel m) {
    		if(m instanceof FilterListModel)
    			super.setModel(m);
    		else 
    			throw new IllegalArgumentException("is not a FilterListMode!");
		}  	
    	
    	public JTextField getFilterField() {
			return filterField;
		}
    	
    	class FilterField<E> extends JTextField implements DocumentListener {

    		public FilterField(int width) {
				super(width);
				getDocument().addDocumentListener(this);
			}
    		
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				((FilterListModel<E>)getModel()).refilter();
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				((FilterListModel<E>)getModel()).refilter();
			}

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				((FilterListModel<E>)getModel()).refilter();
			}	
    	}
    	    	
		class FilterListModel<E> extends DefaultListModel {
			ArrayList<E> items;
			ArrayList<E> filterItems;

			public FilterListModel() {
				super();
				items = new ArrayList<E>();
				filterItems = new ArrayList<E>();
			}

			@Override
			public Object getElementAt(int index) {
				if (index < filterItems.size())
					return filterItems.get(index);
				else
					return null;
			}

			@Override
			public int getSize() {
				return filterItems.size();
			}
			
			@Override
			public void addElement(Object o) {
				items.add((E) o);
				refilter();
			}

			@Override
			public void setElementAt(Object obj, int index) {
				throw new UnsupportedOperationException();
			}
			
			private void refilter() {
				filterItems.clear();
				String term = getFilterField().getText();
				for (E it : items)
					if (it.toString().indexOf(term, 0) != -1)
						filterItems.add(it);
				fireContentsChanged(this, 0, getSize());
			}
			
			@Override
			public void clear() {
				super.clear();
				items.clear();
				refilter();
			}
		}
    }

    

	
    //TODO assign to a button or link
    private static void downloadPathway(int[] rows, DefaultTableModel model) {
    	if(rows.length < 1) {
    		return;
    	}
    	
        int i= rows[0];
    	SearchHit hit = (SearchHit) model.getDataVector().get(i);
        String internalId = hit.getUri();
        String title = model.getValueAt(i, 0)
        	+ " (" + model.getValueAt(i, 1) + ")";

        OutputFormat format = CPath2Factory.downloadMode;

        TaskFactory taskFactory = CPath2Factory.newTaskFactory(new ExecuteGetRecordByCPathIdTask(
        	new String[]{internalId}, format, title));
        CPath2Factory.getTaskManager().execute(taskFactory.createTaskIterator());
    }
 
    
    //TODO 
    private static final JButton createDownloadButton() {
    	JButton downlodButton = new JButton("Download");
        downlodButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {               	
//                    DownloadDetails detailsFrame = factory.createDownloadDetails(passedRecordList);
//                    if (dialog != null) {
//                            SwingUtilities.invokeLater(new Runnable() {
//                                public void run() {
//                                    dialog.dispose();
//                                }
//                            });
//                    }
//                    detailsFrame.setVisible(true);
            }
        });
        
        return downlodButton;
    }


	public static JPanel createSearchPanel() {
		return new SearchPanel();
	}
}
