package eqb;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;


import javax.swing.table.AbstractTableModel;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;

import cytoscape.CyNetwork;
import cytoscape.CyNode;

public class EqtlTableModel extends AbstractTableModel
{
    private Map _data; // map Node object to Eqtl object
    private Map _nodeLookupTable; // map name to Node object
    
    private String[] _columnNames = {"Gene", "# loci"};

    private List _row2DisplayName; // index names by row
    private List _row2Node; // index Node objects by row

    private String _displayAttr = "GeneName";
    
    public EqtlTableModel()
    {
        _data = new HashMap();
        _row2Node = new ArrayList();
        _row2DisplayName = new ArrayList();
        _nodeLookupTable = new HashMap();
    }

    public void updateTableData(Map newData, CyNetwork network, String attr)
    {
        _nodeLookupTable.clear();
        _data.clear();
        _row2Node.clear();
        _row2DisplayName.clear();

        System.out.println("updateTableData called");
        
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
            Object nodeId = i.next();
            // If the node is in the network, add eQTL data for it
            if(_nodeLookupTable.containsKey(nodeId))
            {
                CyNode n = (CyNode) _nodeLookupTable.get(nodeId);
                String displayName = nodeAttr.getStringAttribute(n.getIdentifier(),
                                                                 _displayAttr);
                if(displayName == null)
                {
                    displayName = nodeId.toString();
                }
                _row2Node.add(n);
                _row2DisplayName.add(displayName);
                
                _data.put(n, newData.get(nodeId));
            }
        }

        fireTableDataChanged();
    }

    public String getDisplayAttribute()
    {
        return _displayAttr;
    }
    
    public void updateDisplayAttribute(String attribute)
    {
        CyAttributes nodeAttr = Cytoscape.getNodeAttributes();
        _row2DisplayName.clear();

        _displayAttr = attribute;
        
        for(int x=0, N=getRowCount(); x < N; x++)
        {
            String val = nodeAttr.getStringAttribute(getNode(x).getIdentifier(),
                                                     attribute);
            if(val == null)
            {
                val = nodeAttr.getStringAttribute(getNode(x).getIdentifier(),
                                                  Semantics.CANONICAL_NAME);
            }
            _row2DisplayName.add(val);
        }
        fireTableDataChanged();
    }
    
    public CyNode getNode(String nodeId)
    {
        return (CyNode) _nodeLookupTable.get(nodeId);
    }

    
    public CyNode getNode(int row)
    {
        return (CyNode) _row2Node.get(row);
    }

    
    public String getColumnName(int col)
    {
        return _columnNames[col];
    }

    public Class getColumnClass(int c)
    {
        return getValueAt(0, c).getClass();
    }


    public boolean containsNode(CyNode key)
    {
        return _data.containsKey(key);
    }

    public Eqtl getEqtlData(CyNode key)
    {
        return (Eqtl) _data.get(key);
    }
    
    public int getRowCount() { return _data.size(); }
    public int getColumnCount() { return _columnNames.length; }
    
    public Object getValueAt(int row, int col)
    {
        if(row > getRowCount() || row < 0 || col < 0 || col > 1)
        {
            return null;
        }
        
        if(col == 0)
        {
            return _row2DisplayName.get(row);
        }
        else
        {
            CyNode node = getNode(row);
            return new Integer(getEqtlData(node).numLoci());
        }

    }
    
    public boolean isCellEditable(int row, int col)
        { return false; }

}
