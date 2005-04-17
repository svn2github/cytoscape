package cytoscape.data.attr.util;

import cytoscape.data.attr.CyEdgeData;
import cytoscape.data.attr.CyEdgeDataDefinition;
import cytoscape.data.attr.CyEdgeDataDefinitionListener;
import cytoscape.data.attr.CyEdgeDataListener;
import cytoscape.data.attr.CyNodeData;
import cytoscape.data.attr.CyNodeDataDefinition;
import cytoscape.data.attr.CyNodeDataDefinitionListener;
import cytoscape.data.attr.CyNodeDataListener;

import java.util.Enumeration;
import java.util.HashMap;

final class CyDataModel
  implements CyNodeDataDefinition, CyNodeData, CyEdgeDataDefinition, CyEdgeData
{

  private final static class AttrDefData
  {
    private final HashMap objMap;
    private final Byte valueType;
    private final byte[] keyTypes;
    private final String[] keyNames;
    private AttrDefData(HashMap objMap, Byte valueType,
                        byte[] keyTypes, String[] keyNames)
    {
      this.objMap = objMap;
      this.valueType = valueType;
      this.keyTypes = keyTypes;
      this.keyNames = keyNames;
    }
  }

  private final static class NodeAttrDefLisChain
    implements CyNodeDataDefinitionListener
  {
    // Use only the static methods from outside this inner class.
    private final CyNodeDataDefinitionListener a, b;
    private NodeAttrDefLisChain(CyNodeDataDefinitionListener a,
                                CyNodeDataDefinitionListener b) {
      this.a = a;
      this.b = b; }
    public final void nodeAttributeDefined(final String attributeName) {
      a.nodeAttributeDefined(attributeName);
      b.nodeAttributeDefined(attributeName); }
    public final void nodeAttributeUndefined(final String attributeName) {
      a.nodeAttributeUndefined(attributeName);
      b.nodeAttributeUndefined(attributeName); }
    private final static CyNodeDataDefinitionListener add(
                                              CyNodeDataDefinitionListener a,
                                              CyNodeDataDefinitionListener b) {
      if (a == null) return b;
      if (b == null) return a;
      return new NodeAttrDefLisChain(a, b); }
    private final static CyNodeDataDefinitionListener remove(
                                           CyNodeDataDefinitionListener l,
                                           CyNodeDataDefinitionListener oldl) {
      if (l == oldl || l == null) return null;
      else if (l instanceof NodeAttrDefLisChain)
        return ((NodeAttrDefLisChain) l).remove(oldl);
      else return l; }
    private final CyNodeDataDefinitionListener remove(
                                           CyNodeDataDefinitionListener oldl) {
      if (oldl == a) return b;
      if (oldl == b) return a;
      CyNodeDataDefinitionListener a2 = remove(a, oldl);
      CyNodeDataDefinitionListener b2 = remove(b, oldl);
      if (a2 == a && b2 == b) return this;
      return add(a2, b2); }
  }

  // Keys are attributeName, values are AttrDefData.
  private final HashMap m_nodeAttrMap;
  private final HashMap m_edgeAttrMap;

  private CyNodeDataDefinitionListener m_nodeDataDefListener;

  CyDataModel()
  {
    m_nodeAttrMap = new HashMap();
    m_edgeAttrMap = new HashMap();
    m_nodeDataDefListener = null;
  }

  public final void defineNodeAttribute(final String attributeName,
                                        final byte valueType,
                                        final byte[] keyTypes,
                                        final String[] keyNames)
  {
    // Error-check attributeName.  Unfortunately there are currently no
    // constraints on the length or the contents of attributeName.
    if (attributeName == null)
      throw new NullPointerException("attributeName is null");
    if (m_nodeAttrMap.containsKey(attributeName))
      throw new IllegalStateException
        ("attributeName '" + attributeName + "' already exists");

    // Error-check valueType.
    switch (valueType)
    {
      case CyNodeDataDefinition.TYPE_BOOLEAN:
      case CyNodeDataDefinition.TYPE_FLOATING_POINT:
      case CyNodeDataDefinition.TYPE_INTEGER:
      case CyNodeDataDefinition.TYPE_STRING:
        break;
      default:
        throw new IllegalArgumentException("valueType is unrecognized");
    }

    // Make sure keyTypes and keyNames are the same length.
    final int keyTypesLength = (keyTypes == null ? 0 : keyTypes.length);
    final int keyNamesLength = (keyNames == null ? 0 : keyNames.length);
    if (keyTypesLength != keyNamesLength)
      throw new IllegalArgumentException
        ("lengths of keyTypes and keyNames arrays don't match");

    // Make copies of keyTypes and keyNames.
    final byte[] keyTypesCopy = new byte[keyTypesLength];
    if (keyTypes != null)
      System.arraycopy(keyTypes, 0, keyTypesCopy, 0, keyTypesLength);
    final String[] keyNamesCopy = new String[keyNamesLength];
    if (keyNames != null)
      System.arraycopy(keyNames, 0, keyNamesCopy, 0, keyNamesLength);

    // Error-check keyTypesCopy.
    for (int i = 0; i < keyTypesCopy.length; i++)
      switch (keyTypesCopy[i])
      {
        case CyNodeDataDefinition.TYPE_BOOLEAN:
        case CyNodeDataDefinition.TYPE_FLOATING_POINT:
        case CyNodeDataDefinition.TYPE_INTEGER:
        case CyNodeDataDefinition.TYPE_STRING:
          break;
        default:
          throw new IllegalArgumentException
            ("keyTypes at index " + i + " is unrecognized");
      }

    // Error-check keyNamesCopy.  Make sure that all are names are distinct.
    // Unfortunately there are currently no contraints on the length and
    // content of the key names.
    final HashMap tempHash = new HashMap();
    for (int i = 0; i < keyNamesCopy.length; i++) {
      if (keyNamesCopy[i] == null)
        throw new NullPointerException("keyNames at index " + i + " is null");
      if (tempHash.put(keyNamesCopy[i], keyNamesCopy[i]) != null)
        throw new IllegalArgumentException
          ("duplicate in keyNames at index " + i); }

    // Finally, create the definition.
    final AttrDefData def = new AttrDefData(new HashMap(), valueType,
                                            keyTypesCopy, keyNamesCopy);
    m_nodeAttrMap.put(attributeName, def);

    // Call listeners.  Make sure this is done after we actaully create def.
    final CyNodeDataDefinitionListener l = m_nodeDataDefListener;
    if (l != null) l.nodeAttributeDefined(attributeName);
  }

  public final Enumeration getDefinedNodeAttributes()
  {
    final java.util.Iterator keys = m_nodeAttrMap.keySet().iterator();
    return new Enumeration() {
        public final boolean hasMoreElements() { return keys.hasNext(); }
        public final Object nextElement() { return keys.next(); } };
  }

  public final byte getNodeAttributeValueType(final String attributeName)
  {
    final AttrDefData data = (AttrDefData) m_nodeAttrMap.get(attributeName);
    if (data == null) return -1;
    return data.valueType;
  }

  public final int getNodeAttributeKeyspaceDimensionality(
                                                    final String attributeName)
  {
    final AttrDefData data = (AttrDefData) m_nodeAttrMap.get(attributeName);
    if (data == null) return -1;
    return data.keyTypes.length;
  }

  public final void copyNodeAttributeKeyspaceInfo(final String attributeName,
                                                  final byte[] keyTypes,
                                                  final String[] keyNames)
  {
    final AttrDefData data = (AttrDefData) m_nodeAttrMap.get(attributeName);
    if (data == null) throw new IllegalStateException
                        ("no attributeName '" + attributeName + "' exists");
    System.arraycopy(data.keyTypes, 0, keyTypes, 0, data.keyTypes.length);
    System.arraycopy(data.keyNames, 0, keyNames, 0, data.keyNames.length);
  }

  public void undefineNodeAttribute(String attributeName)
  {
    Object o = m_nodeAttrMap.remove(attributeName);
    if (o != null) { // attributeName was in fact deleted.
      final CyNodeDataDefinitionListener l = m_nodeDataDefListener;
      if (l != null) l.nodeAttributeUndefined(attributeName); }
  }

  public void addNodeDataDefinitionListener(
                                         CyNodeDataDefinitionListener listener)
  {
    m_nodeDataDefListener = NodeAttrDefLisChain.add(m_nodeDataDefListener,
                                                    listener);
  }

  public void removeNodeDataDefinitionListener(
                                         CyNodeDataDefinitionListener listener)
  {
    m_nodeDataDefListener = NodeAttrDefLisChain.remove(m_nodeDataDefListener,
                                                       listener);
  }

  public void setNodeAttributeValue(String nodeKey, String attributeName,
                                    Object attributeValue,
                                    Object[] keyIntoValue)
  {
  }

  public Object getNodeAttributeValue(String nodeKey, String attributeName,
                                      Object[] keyIntoValue)
  {
    return null;
  }

  public Object removeNodeAttributeValue(String nodeKey, String attributeName,
                                         Object[] keyIntoValue)
  {
    return null;
  }

  public int getNodeAttributeKeyspanCount(String nodeKey, String attributeName,
                                          Object[] keyPrefix)
  {
    return -1;
  }

  public Enumeration getNodeAttributeKeyspan(String nodeKey,
                                             String attributeName,
                                             Object[] keyPrefix)
  {
    return null;
  }

  public int removeNodeAttributeKeyspan(String nodeKey,
                                        String attributeName,
                                        Object[] keyPrefix)
  {
    return -1;
  }

  public void addNodeDataListener(CyNodeDataListener listener)
  {
  }

  public void removeNodeDataListener(CyNodeDataListener listener)
  {
  }

  public void defineEdgeAttribute(String attributeName,
                                  byte valueType,
                                  byte[] keyTypes,
                                  String[] keyNames)
  {
  }

  public Enumeration getDefinedEdgeAttributes()
  {
    return null;
  }

  public byte getEdgeAttributeValueType(String attributeName)
  {
    return -1;
  }

  public int getEdgeAttributeKeyspaceDimensionality(String attributeName)
  {
    return -1;
  }

  public void copyEdgeAttributeKeyspaceInfo(String attributeName,
                                            byte[] keyTypes, String[] keyNames)
  {
  }

  public void undefineEdgeAttribute(String attributeName)
  {
  }

  public void addEdgeDataDefinitionListener(
                                         CyEdgeDataDefinitionListener listener)
  {
  }

  public void removeEdgeDataDefinitionListener(
                                         CyEdgeDataDefinitionListener listener)
  {
  }

  public void setEdgeAttributeValue(String edgeKey, String attributeName,
                                    Object attributeValue,
                                    Object[] keyIntoValue)
  {
  }

  public Object getEdgeAttributeValue(String edgeKey, String attributeName,
                                      Object[] keyIntoValue)
  {
    return null;
  }

  public Object removeEdgeAttributeValue(String edgeKey, String attributeName,
                                         Object[] keyIntoValue)
  {
    return null;
  }

  public int getEdgeAttributeKeyspanCount(String edgeKey, String attributeName,
                                          Object[] keyPrefix)
  {
    return -1;
  }

  public Enumeration getEdgeAttributeKeyspan(String edgeKey,
                                             String attributeName,
                                             Object[] keyPrefix)
  {
    return null;
  }

  public int removeEdgeAttributeKeyspan(String edgeKey,
                                        String attributeName,
                                        Object[] keyPrefix)
  {
    return -1;
  }

  public void addEdgeDataListener(CyEdgeDataListener listener)
  {
  }

  public void removeEdgeDataListener(CyEdgeDataListener listener)
  {
  }

}
