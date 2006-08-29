package eqb;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import java.util.Vector;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Component;

import javax.swing.*;
import javax.swing.JTable;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.ListSelectionModel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.PopupMenuEvent;


import javax.swing.table.TableModel;

import cytoscape.CyNode;

import cytoscape.Cytoscape;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;

/**
 * Panel used as a CytoPanel
 */
public class EqtlPanel extends JPanel
    implements ActionListener                    
{
    EqbPlugin _plugin;
    EqtlTableModel _tableModel;
    Map _nodeLookupTable; // map node attribute to node object

    private final static String noMessageCommand = "no";
    private final static String organicMessageCommand = "organic";
    private final static String heirarchicMessageCommand = "heirarchic";
    private final static String yHeirarchicMessageCommand = "yHeirarchic";

    private JLabel _dataFile;
    
    public EqtlPanel(EqbPlugin plugin)
    {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(181, 700));
        _plugin = plugin;
        _nodeLookupTable = new HashMap();

        // Need to map node indexes to gene names to enable selection
                 
        _tableModel = _plugin.getTableModel();
        
        final TableSorter sorter = new TableSorter(_tableModel);
        JTable table = new JTable(sorter);       
        sorter.setTableHeader(table.getTableHeader()); 

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        //Ask to be notified of selection changes.
        ListSelectionModel rowSM = table.getSelectionModel();
        rowSM.addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    //Ignore extra messages.
                    if (e.getValueIsAdjusting()) return;
                    
                    ListSelectionModel lsm = (ListSelectionModel)e.getSource();
                    if (! lsm.isSelectionEmpty())
                    {
                        int selectedRow = lsm.getMinSelectionIndex();

                        String gene = (String) sorter.getValueAt(selectedRow, 0);
                        System.out.println("eQTLPanel: selected " + gene);

                        CyNode node = _tableModel.getNode(sorter.modelIndex(selectedRow));
                        _plugin.showEqtl(node);
                    }
                }
            });
        
        JScrollPane scrollPane = new JScrollPane(table);
        //table.setPreferredScrollableViewportSize(new Dimension(500, 70));
        table.doLayout();
        this.add(scrollPane, BorderLayout.CENTER);

        final int numButtons = 4;
        JRadioButton[] layoutButtons = new JRadioButton[numButtons];
        
        final ButtonGroup group = new ButtonGroup();
        layoutButtons[0] = new JRadioButton("yFiles Organic");
        layoutButtons[0].setActionCommand(organicMessageCommand);
        layoutButtons[1] = new JRadioButton("yFiles Heirachic");
        layoutButtons[1].setActionCommand(yHeirarchicMessageCommand);
        layoutButtons[2] = new JRadioButton("Heirarchical");
        layoutButtons[2].setActionCommand(heirarchicMessageCommand);
        layoutButtons[3] = new JRadioButton("No auto layout");
        layoutButtons[3].setActionCommand(noMessageCommand);
        layoutButtons[0].setSelected(true);


        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.PAGE_AXIS));
        buttonPanel.add(new JLabel("Auto layout mode"));
        for (int i = 0; i < numButtons; i++) {
            layoutButtons[i].addActionListener(this);
            group.add(layoutButtons[i]);
            buttonPanel.add(layoutButtons[i]);
        }
        
        buttonPanel.add(new JSeparator(SwingConstants.HORIZONTAL));

        JPanel filePanel = new JPanel();
        filePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        filePanel.setLayout(new BoxLayout(filePanel, BoxLayout.LINE_AXIS));
        filePanel.add(new JLabel("Data file: "));
        _dataFile = new JLabel("");
        filePanel.add(_dataFile);
        filePanel.add(Box.createHorizontalGlue());
        JButton browse = new JButton("Browse...");
        browse.addActionListener(new ActionListener () {
                public void actionPerformed(ActionEvent e) {
                    _plugin.changeDataFile();
                }
            }
                                 );
        filePanel.add(browse);
        
        buttonPanel.add(filePanel);

        // add the node attribute chooser
        buttonPanel.add(new JSeparator(SwingConstants.HORIZONTAL));
        
        final JComboBox attrChoices = new JComboBox(getCurrentStringNodeAttrs());
        attrChoices.setAlignmentX(Component.LEFT_ALIGNMENT);
        attrChoices.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JComboBox cb = (JComboBox)e.getSource();
                    System.out.println("Action: " + e.getActionCommand());
                    System.out.println("  selected: " + cb.getSelectedItem());
                    if(cb.getSelectedItem() != null)
                    {
                        _tableModel.updateDisplayAttribute(cb.getSelectedItem().toString());
                    }
                }
            }                                      
                                      );

        attrChoices.addPopupMenuListener(new NodeAttrPopupListener(attrChoices));
        buttonPanel.add(attrChoices);
        
        this.add(buttonPanel, BorderLayout.PAGE_END);
    }

    class NodeAttrPopupListener implements PopupMenuListener
    {
        private JComboBox _myBox;

        NodeAttrPopupListener(JComboBox cb)
        {
            _myBox = cb;
        }
        
        public void popupMenuCanceled(PopupMenuEvent e) {}
        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}
        public void popupMenuWillBecomeVisible(PopupMenuEvent e)
        {
            System.out.println("Removing items from combo box");
            _myBox.removeAllItems();
            
            Vector v = getCurrentStringNodeAttrs();
            for(Iterator i = v.iterator(); i.hasNext();)
            {
                Object o = i.next();
                System.out.println("   adding " + o);
                _myBox.addItem(o);
            }
            _myBox.setMaximumRowCount(v.size()); 
            System.out.println("Populated combo box with " + v.size() + " items");
        }
    }

    private Vector getCurrentStringNodeAttrs()
    {
        CyAttributes nA = Cytoscape.getNodeAttributes(); 
        String[] attr = nA.getAttributeNames();
        Vector stringAttr = new Vector();
        for(int x=0; x < attr.length; x++)
        {
            if(nA.getType(attr[x]) == CyAttributes.TYPE_STRING)
            {
                stringAttr.add(attr[x]);
            }
        }
        return stringAttr;
    }
    
    public void actionPerformed(ActionEvent e)
    {
        String cmd = e.getActionCommand();

        if(cmd == noMessageCommand)
        {
            _plugin.setNoLayout();
        }
        else if (cmd == organicMessageCommand)
        {
            _plugin.setAutoLayout(EqbPlugin.ORGANIC_LAYOUT);
        }
        else if (cmd == heirarchicMessageCommand)
        {
            _plugin.setAutoLayout(EqbPlugin.HEIRARCHIC_LAYOUT);
        }
        else if (cmd == yHeirarchicMessageCommand)
        {
            _plugin.setAutoLayout(EqbPlugin.YFILES_HEIRARCHIC_LAYOUT);
        }
    }

    public void setCurrentDataFile(String file)
    {
        _dataFile.setText(file);
    }
}
