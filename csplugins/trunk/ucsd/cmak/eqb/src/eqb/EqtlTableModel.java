package eqb;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;


import javax.swing.table.AbstractTableModel;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.CyNetwork;
import cytoscape.CyNode;

public class EqtlTableModel extends AbstractTableModel
{
    private Map _data; // map
    private Map _nodeLookupTable; // map name to Node object
    
    private String[] _columnNames = {"Gene", "# loci"};

    private List _nodeNames;
    
    public EqtlTableModel()
    {
        _data = new HashMap();
        _nodeNames = new ArrayList();
        _nodeLookupTable = new HashMap();
    }

    public void updateTableData(Map newData, CyNetwork network, String attr)
    {
        _nodeLookupTable.clear();
        _data.clear();
        
        CyAttributes nodeAttr = Cytoscape.getNodeAttributes();

        for(Iterator i = network.nodesIterator(); i.hasNext();)
        {
            CyNode n = (CyNode) i.next();
            String val = nodeAttr.getStringAttribute(n.getIdentifier(), attr);
            if(val != null)
            {
                _nodeLookupTable.put(val, n);
            }
        }

        for(Iterator i = newData.keySet().iterator(); i.hasNext();)
        {
            Object nodeName = i.next();
            // If the node is in the network, add eQTL data for it
            if(_nodeLookupTable.containsKey(nodeName))
            {
                _nodeNames.add(nodeName);
                _data.put(_nodeLookupTable.get(nodeName),
                          newData.get(nodeName));
            }
        }

        fireTableDataChanged();
    }

    public CyNode getNode(String nodeName)
    {
        return (CyNode) _nodeLookupTable.get(nodeName);
    }

    
    public String getColumnName(int col)
    {
        return _columnNames[col];
    }

    public Class getColumnClass(int c)
    {
        return getValueAt(0, c).getClass();
    }


    public boolean containsNode(Object key)
    {
        return _data.containsKey(key);
    }

    public Object getEqtlData(Object key)
    {
        return _data.get(key);
    }
    
    public int getRowCount() { return _data.size(); }
    public int getColumnCount() { return _columnNames.length; }

    public Object getValueAt(int row, int col)
    {
        if(col == 0)
        {
            return _nodeNames.get(row);
        }
        else
        {
            CyNode node = getNode((String) _nodeNames.get(row));
            return new Integer(((Eqtl) _data.get(node)).numLoci());
        }

    }
    
    public boolean isCellEditable(int row, int col)
        { return false; }

}
