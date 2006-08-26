package eqb;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import java.util.Vector;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.*;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableModel;

import cytoscape.CyNode;

import cytoscape.Cytoscape;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.data.CyAttributes;

/**
 * Panel used as a CytoPanel
 */
public class EqtlPanel extends JPanel
{
    EqbPlugin _plugin;
    EqtlTableModel _tableModel;
    Map _nodeLookupTable; // map node attribute to node object
    
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

                        CyNode node = _tableModel.getNode(gene);
                        _plugin.showEqtl(node);
                    }
                }
            });
        
        JScrollPane scrollPane = new JScrollPane(table);
        //table.setPreferredScrollableViewportSize(new Dimension(500, 70));
        table.doLayout();
        this.add(scrollPane);
    }


    /** When a new network view is created, register the plugin
     * as a NodeContextMenuListener
     *
    public void propertyChange (PropertyChangeEvent evnt)
    {
        if (evnt.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_FOCUS)
        {
            System.out.println("[EqtlPanel NETWORK_VIEW_FOCUS]");
            System.out.println(" rebuilding lookup table");
            buildNodeLookupTable(Cytoscape.getCurrentNetwork(),
                                 _plugin.getNodeIdentifier());
        }
    }
    */

    
}
