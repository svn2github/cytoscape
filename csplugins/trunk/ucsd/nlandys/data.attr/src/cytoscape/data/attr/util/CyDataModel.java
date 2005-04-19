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
    private final HashMap objMap; // Keys are nodeKey.
    private final byte valueType;
    private final byte[] keyTypes;
    private final String[] keyNames;
    private AttrDefData(HashMap objMap, byte valueType,
                        byte[] keyTypes, String[] keyNames)
    {
      this.objMap = objMap;
      this.valueType = valueType;
      this.keyTypes = keyTypes;
      this.keyNames = keyNames;
    }
  }

  private final static class Iterator2Enumeration implements Enumeration
  {
    private final java.util.Iterator iter;
    private Iterator2Enumeration(java.util.Iterator iter) {
      this.iter = iter; }
    public boolean hasMoreElements() { return iter.hasNext(); }
    public Object nextElement() { return iter.next(); }
  }

  private final static class NodeAttrDefLisChain
    implements CyNodeDataDefinitionListener
  {
    // Use only the static methods from outside this inner class.
    private final CyNodeDataDefinitionListener a, b;
    private NodeAttrDefLisChain(final CyNodeDataDefinitionListener a,
                                final CyNodeDataDefinitionListener b) {
      this.a = a;
      this.b = b; }
    public final void nodeAttributeDefined(final String attributeName) {
      a.nodeAttributeDefined(attributeName);
      b.nodeAttributeDefined(attributeName); }
    public final void nodeAttributeUndefined(final String attributeName) {
      a.nodeAttributeUndefined(attributeName);
      b.nodeAttributeUndefined(attributeName); }
    private final static CyNodeDataDefinitionListener add(
                                        final CyNodeDataDefinitionListener a,
                                        final CyNodeDataDefinitionListener b) {
      if (a == null) return b;
      if (b == null) return a;
      return new NodeAttrDefLisChain(a, b); }
    private final static CyNodeDataDefinitionListener remove(
                                     final CyNodeDataDefinitionListener l,
                                     final CyNodeDataDefinitionListener oldl) {
      if (l == oldl || l == null) return null;
      else if (l instanceof NodeAttrDefLisChain)
        return ((NodeAttrDefLisChain) l).remove(oldl);
      else return l; }
    private final CyNodeDataDefinitionListener remove(
                                     final CyNodeDataDefinitionListener oldl) {
      if (oldl == a) return b;
      if (oldl == b) return a;
      final CyNodeDataDefinitionListener a2 = remove(a, oldl);
      final CyNodeDataDefinitionListener b2 = remove(b, oldl);
      if (a2 == a && b2 == b) return this;
      return add(a2, b2); }
  }

  private final static class NodeAttrLisChain implements CyNodeDataListener
  {
    // Use only the static methods from outside this inner class.
    private final CyNodeDataListener a, b;
    private NodeAttrLisChain(final CyNodeDataListener a,
                             final CyNodeDataListener b) {
      this.a = a;
      this.b = b; }
    public final void nodeAttributeValueAssigned(final String nodeKey,
                                                 final String attributeName,
                                                 final Object[] keyIntoValue,
                                                 final Object oldAttrVal,
                                                 final Object newAttrVal) {
      a.nodeAttributeValueAssigned(nodeKey, attributeName, keyIntoValue,
                                   oldAttrVal, newAttrVal);
      b.nodeAttributeValueAssigned(nodeKey, attributeName, keyIntoValue,
                                   oldAttrVal, newAttrVal); }
    public final void nodeAttributeValueRemoved(final String nodeKey,
                                                final String attributeName,
                                                final Object[] keyIntoValue,
                                                final Object attributeValue) {
      a.nodeAttributeValueRemoved(nodeKey, attributeName, keyIntoValue,
                                  attributeValue);
      b.nodeAttributeValueRemoved(nodeKey, attributeName, keyIntoValue,
                                  attributeValue); }
    private final static CyNodeDataListener add(final CyNodeDataListener a,
                                                final CyNodeDataListener b) {
      if (a == null) return b;
      if (b == null) return a;
      return new NodeAttrLisChain(a, b); }
    private final static CyNodeDataListener remove(
                                               final CyNodeDataListener l,
                                               final CyNodeDataListener oldl) {
      if (l == oldl || l == null) return null;
      else if (l instanceof NodeAttrLisChain)
        return ((NodeAttrLisChain) l).remove(oldl);
      else return l; }
    private final CyNodeDataListener remove(final CyNodeDataListener oldl) {
      if (oldl == a) return b;
      if (oldl == b) return a;
      final CyNodeDataListener a2 = remove(a, oldl);
      final CyNodeDataListener b2 = remove(b, oldl);
      if (a2 == a && b2 == b) return this;
      return add(a2, b2); }
  }

  private final static class EdgeAttrDefLisChain
    implements CyEdgeDataDefinitionListener
  {
    // Use only the static methods from outside this inner class.
    private final CyEdgeDataDefinitionListener a, b;
    private EdgeAttrDefLisChain(final CyEdgeDataDefinitionListener a,
                                final CyEdgeDataDefinitionListener b) {
      this.a = a;
      this.b = b; }
    public final void edgeAttributeDefined(final String attributeName) {
      a.edgeAttributeDefined(attributeName);
      b.edgeAttributeDefined(attributeName); }
    public final void edgeAttributeUndefined(final String attributeName) {
      a.edgeAttributeUndefined(attributeName);
      b.edgeAttributeUndefined(attributeName); }
    private final static CyEdgeDataDefinitionListener add(
                                        final CyEdgeDataDefinitionListener a,
                                        final CyEdgeDataDefinitionListener b) {
      if (a == null) return b;
      if (b == null) return a;
      return new EdgeAttrDefLisChain(a, b); }
    private final static CyEdgeDataDefinitionListener remove(
                                     final CyEdgeDataDefinitionListener l,
                                     final CyEdgeDataDefinitionListener oldl) {
      if (l == oldl || l == null) return null;
      else if (l instanceof EdgeAttrDefLisChain)
        return ((EdgeAttrDefLisChain) l).remove(oldl);
      else return l; }
    private final CyEdgeDataDefinitionListener remove(
                                     final CyEdgeDataDefinitionListener oldl) {
      if (oldl == a) return b;
      if (oldl == b) return a;
      final CyEdgeDataDefinitionListener a2 = remove(a, oldl);
      final CyEdgeDataDefinitionListener b2 = remove(b, oldl);
      if (a2 == a && b2 == b) return this;
      return add(a2, b2); }
  }

  private final static class EdgeAttrLisChain implements CyEdgeDataListener
  {
    // Use only the static methods from outside this inner class.
    private final CyEdgeDataListener a, b;
    private EdgeAttrLisChain(final CyEdgeDataListener a,
                             final CyEdgeDataListener b) {
      this.a = a;
      this.b = b; }
    public final void edgeAttributeValueAssigned(final String edgeKey,
                                                 final String attributeName,
                                                 final Object[] keyIntoValue,
                                                 final Object oldAttrVal,
                                                 final Object newAttrVal) {
      a.edgeAttributeValueAssigned(edgeKey, attributeName, keyIntoValue,
                                   oldAttrVal, newAttrVal);
      b.edgeAttributeValueAssigned(edgeKey, attributeName, keyIntoValue,
                                   oldAttrVal, newAttrVal); }
    public final void edgeAttributeValueRemoved(final String edgeKey,
                                                final String attributeName,
                                                final Object[] keyIntoValue,
                                                final Object attributeValue) {
      a.edgeAttributeValueRemoved(edgeKey, attributeName, keyIntoValue,
                                  attributeValue);
      b.edgeAttributeValueRemoved(edgeKey, attributeName, keyIntoValue,
                                  attributeValue); }
    private final static CyEdgeDataListener add(final CyEdgeDataListener a,
                                                final CyEdgeDataListener b) {
      if (a == null) return b;
      if (b == null) return a;
      return new EdgeAttrLisChain(a, b); }
    private final static CyEdgeDataListener remove(
                                               final CyEdgeDataListener l,
                                               final CyEdgeDataListener oldl) {
      if (l == oldl || l == null) return null;
      else if (l instanceof EdgeAttrLisChain)
        return ((EdgeAttrLisChain) l).remove(oldl);
      else return l; }
    private final CyEdgeDataListener remove(final CyEdgeDataListener oldl) {
      if (oldl == a) return b;
      if (oldl == b) return a;
      final CyEdgeDataListener a2 = remove(a, oldl);
      final CyEdgeDataListener b2 = remove(b, oldl);
      if (a2 == a && b2 == b) return this;
      return add(a2, b2); }
  }

  private final static Enumeration s_the_empty_enumeration =
    new Enumeration() {
      public final boolean hasMoreElements() { return false; }
      public final Object nextElement() {
        throw new java.util.NoSuchElementException(); } };

  // Keys are attributeName, values are AttrDefData.
  private final HashMap m_nodeAttrMap;
  private final HashMap m_edgeAttrMap;

  private CyNodeDataDefinitionListener m_nodeDataDefListener;
  private CyNodeDataListener m_nodeDataListener;
  private CyEdgeDataDefinitionListener m_edgeDataDefListener;
  private CyEdgeDataListener m_edgeDataListener;

  CyDataModel()
  {
    m_nodeAttrMap = new HashMap();
    m_edgeAttrMap = new HashMap();
    m_nodeDataDefListener = null;
    m_nodeDataListener = null;
    m_edgeDataDefListener = null;
    m_edgeDataListener = null;
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
        ("node attributeName '" + attributeName + "' already exists");

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
            ("keyTypes[" + i + "] is unrecognized");
      }

    // Error-check keyNamesCopy.  Make sure that all are names are distinct.
    // Unfortunately there are currently no contraints on the length and
    // content of the key names.
    final HashMap tempHash = new HashMap();
    for (int i = 0; i < keyNamesCopy.length; i++) {
      if (keyNamesCopy[i] == null)
        throw new NullPointerException("keyNames[" + i + "] is null");
      if (tempHash.put(keyNamesCopy[i], keyNamesCopy[i]) != null)
        throw new IllegalArgumentException
          ("duplicate name keyNames[" + i + "]"); }

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
    return new Iterator2Enumeration(m_nodeAttrMap.keySet().iterator());
  }

  public final byte getNodeAttributeValueType(final String attributeName)
  {
    if (attributeName == null)
      throw new NullPointerException("attributeName is null");
    final AttrDefData def = (AttrDefData) m_nodeAttrMap.get(attributeName);
    if (def == null) return -1;
    return def.valueType;
  }

  public final int getNodeAttributeKeyspaceDimensionality(
                                                    final String attributeName)
  {
    if (attributeName == null)
      throw new NullPointerException("attributeName is null");
    final AttrDefData def = (AttrDefData) m_nodeAttrMap.get(attributeName);
    if (def == null) return -1;
    return def.keyTypes.length;
  }

  public final void copyNodeAttributeKeyspaceInfo(final String attributeName,
                                                  final byte[] keyTypes,
                                                  final String[] keyNames)
  {
    if (attributeName == null)
      throw new NullPointerException("attributeName is null");
    final AttrDefData def = (AttrDefData) m_nodeAttrMap.get(attributeName);
    if (def == null)
      throw new IllegalStateException
        ("no node attributeName '" + attributeName + "' exists");
    System.arraycopy(def.keyTypes, 0, keyTypes, 0, def.keyTypes.length);
    System.arraycopy(def.keyNames, 0, keyNames, 0, def.keyNames.length);
  }

  public final void undefineNodeAttribute(final String attributeName)
  {
    if (attributeName == null)
      throw new NullPointerException("attributeName is null");
    final Object o = m_nodeAttrMap.remove(attributeName);
    if (o != null) { // attributeName was in fact deleted.
      final CyNodeDataDefinitionListener l = m_nodeDataDefListener;
      if (l != null) l.nodeAttributeUndefined(attributeName); }
  }

  public final void addNodeDataDefinitionListener(
                                   final CyNodeDataDefinitionListener listener)
  {
    m_nodeDataDefListener = NodeAttrDefLisChain.add(m_nodeDataDefListener,
                                                    listener);
  }

  public final void removeNodeDataDefinitionListener(
                                   final CyNodeDataDefinitionListener listener)
  {
    m_nodeDataDefListener = NodeAttrDefLisChain.remove(m_nodeDataDefListener,
                                                       listener);
  }

  public final Object setNodeAttributeValue(final String nodeKey,
                                            final String attributeName,
                                            final Object attributeValue,
                                            final Object[] keyIntoValue)
  {
    // Pull out the definition, error-checking attributeName in the process.
    if (attributeName == null)
      throw new NullPointerException("attributeName is null");
    final AttrDefData def = (AttrDefData) m_nodeAttrMap.get(attributeName);
    if (def == null)
      throw new IllegalStateException
        ("no node attributeName '" + attributeName + "' exists");

    // Error-check nodeKey.  Right now there are no constraints on the length
    // or the contents of nodeKey.  In the future, consider having
    // a "registry" of all nodeKeys and permitting only assignment to those
    // nodeKeys.
    if (nodeKey == null) throw new NullPointerException("nodeKey is null");

    // Error-check attributeValue.  Note that the instanceof operation always
    // returns false for null values, and does not throw an exception.
    if (attributeValue == null)
      throw new NullPointerException("cannot set null attributeValue - " +
                                     "use removeNodeAttributeValue() instead");
    boolean passed = false;
    switch (def.valueType)
    { // I'm wondering what the most efficient way of doing this is.
      case CyNodeDataDefinition.TYPE_BOOLEAN:
        passed = (attributeValue instanceof java.lang.Boolean); break;
      case CyNodeDataDefinition.TYPE_FLOATING_POINT:
        passed = (attributeValue instanceof java.lang.Double); break;
      case CyNodeDataDefinition.TYPE_INTEGER:
        passed = (attributeValue instanceof java.lang.Long); break;
      case CyNodeDataDefinition.TYPE_STRING:
        passed = (attributeValue instanceof java.lang.String); break;
    }
    if (!passed) { // Go the extra effort to return an informational error.
      String className = null;
      switch (def.valueType)
      { // Repeat same switch logic here for efficiency in non-error case.
        case CyNodeDataDefinition.TYPE_BOOLEAN:
          className = "java.lang.Boolean"; break;
        case CyNodeDataDefinition.TYPE_FLOATING_POINT:
          className = "java.lang.Double"; break;
        case CyNodeDataDefinition.TYPE_INTEGER:
          className = "java.lang.Long"; break;
        case CyNodeDataDefinition.TYPE_STRING:
          className = "java.lang.String"; break;
      }
      throw new ClassCastException
        ("attributeValue must be of type " + className +
         " in node attributeName '" + attributeName + "' definition"); }

    // Error-check keyIntoValue.  Leave the type checks to the recursion.
    if (def.keyTypes.length == 0) {
      if (keyIntoValue != null || keyIntoValue.length != 0) {
        throw new IllegalArgumentException
          ("node attributeName '" + attributeName + "' has no keyspace" +
           " defined, yet keyIntoValue is not empty"); } }
    else { // Keyspace is not empty.
      if (def.keyTypes.length != keyIntoValue.length) { // May trigger NullPtr.
        throw new IllegalArgumentException
          ("keyIntoValue has incorrect dimensionality"); } }

    final CyNodeDataListener listener = m_nodeDataListener;
    if (def.keyTypes.length == 0) { // Don't even recurse.
      final Object returnThis = def.objMap.put(nodeKey, attributeValue);
      if (listener != null)
        listener.nodeAttributeValueAssigned(nodeKey, attributeName, null,
                                            returnThis, attributeValue);
      return returnThis; }
    else { // Recurse.
      final Object o = def.objMap.get(nodeKey);
      final HashMap firstDim;
      if (o == null) firstDim = new HashMap();
      else firstDim = (HashMap) o;
      final Object returnThis =
        r_setNodeAttributeValue(firstDim, attributeValue, keyIntoValue,
                                def.keyTypes, 0);
      // If firstDim is a new HashMap add it to the definition after the
      // recursion completes so that if an exception is thrown, we can avoid
      // cleanup.
      if (o == null) def.objMap.put(nodeKey, firstDim);
      if (listener != null)
        listener.nodeAttributeValueAssigned
          (nodeKey, attributeName, keyIntoValue, returnThis, attributeValue);
      return returnThis; }
  }

  // Recursive helper method.
  private final Object r_setNodeAttributeValue(final HashMap hash,
                                               final Object attributeValue,
                                               final Object[] keyIntoValue,
                                               final byte[] keyTypes,
                                               final int currOffset)
  {
    // Error check type of object keyIntoValue[currOffset].
    final Object currKey = keyIntoValue[currOffset];
    // Right now, key representatives cannot be null - that is the only
    // constraint.  This may or may not make sense; imagine a String key
    // representative being "".
    if (currKey == null)
      throw new NullPointerException("keyIntoValue[" + currOffset + "] null");
    boolean passed = false;
    switch (keyTypes[currOffset]) {
      case CyNodeDataDefinition.TYPE_BOOLEAN:
        passed = (currKey instanceof java.lang.Boolean); break;
      case CyNodeDataDefinition.TYPE_FLOATING_POINT:
        passed = (currKey instanceof java.lang.Double); break;
      case CyNodeDataDefinition.TYPE_INTEGER:
        passed = (currKey instanceof java.lang.Long); break;
      case CyNodeDataDefinition.TYPE_STRING:
        passed = (currKey instanceof java.lang.String); break; }
    if (!passed)
      throw new ClassCastException
        ("keyIntoValue[" + currOffset + "] is of incorrect object type");

    // Put something in.
    if (currOffset == keyIntoValue.length - 1) { // The final dimension.
      return hash.put(currKey, attributeValue); }
    else { // Must recurse further.
      final Object o = hash.get(currKey);
      final HashMap dim;
      if (o == null) dim = new HashMap();
      else dim = (HashMap) o;
      final Object returnThis =
        r_setNodeAttributeValue(dim, attributeValue, keyIntoValue, keyTypes,
                                currOffset + 1);
      // Put new HashMap in after recursive call to prevent the need for
      // cleanup in case exception is thrown.
      if (o == null) hash.put(currKey, dim);
      return returnThis; }
  }

  public final Object getNodeAttributeValue(final String nodeKey,
                                            final String attributeName,
                                            final Object[] keyIntoValue)
  {
    // Pull out the definition, error-checking attributeName in the process.
    if (attributeName == null)
      throw new NullPointerException("attributeName is null");
    final AttrDefData def = (AttrDefData) m_nodeAttrMap.get(attributeName);
    if (def == null)
      throw new IllegalStateException
        ("no node attributeName '" + attributeName + "' exists");

    // Error-check nodeKey.
    if (nodeKey == null) throw new NullPointerException("nodeKey is null");

    // Error-check keyIntoValue.  Leave the type checks to the recursion.
    if (def.keyTypes.length == 0) {
      if (keyIntoValue != null || keyIntoValue.length != 0) {
        throw new IllegalArgumentException
          ("node attributeName '" + attributeName + "' has no keyspace" +
           " defined, yet keyIntoValue is not empty"); } }
    else { // Keyspace is not empty.
      if (def.keyTypes.length != keyIntoValue.length) { // May trigger NullPtr.
        throw new IllegalArgumentException
          ("keyIntoValue has incorrect dimensionality"); } }

    if (def.keyTypes.length == 0) { // Don't even recurse.
      return def.objMap.get(nodeKey); }
    else { // Recurse.
      final Object o = def.objMap.get(nodeKey);
      if (o == null) return null;
      return r_getNodeAttributeValue((HashMap) o, keyIntoValue,
                                     def.keyTypes, 0); }
  }

  private final Object r_getNodeAttributeValue(final HashMap hash,
                                               final Object[] keyIntoValue,
                                               final byte[] keyTypes,
                                               final int currOffset)
  {
    // Error-check type of object keyIntoValue[currOffset].
    final Object currKey = keyIntoValue[currOffset];
    if (currKey == null)
      throw new NullPointerException("keyIntoValue[" + currOffset + "] null");
    boolean passed = false;
    switch (keyTypes[currOffset]) {
      case CyNodeDataDefinition.TYPE_BOOLEAN:
        passed = (currKey instanceof java.lang.Boolean); break;
      case CyNodeDataDefinition.TYPE_FLOATING_POINT:
        passed = (currKey instanceof java.lang.Double); break;
      case CyNodeDataDefinition.TYPE_INTEGER:
        passed = (currKey instanceof java.lang.Long); break;
      case CyNodeDataDefinition.TYPE_STRING:
        passed = (currKey instanceof java.lang.String); break; }
    if (!passed)
      throw new ClassCastException
        ("keyIntoValue[" + currOffset + "] is of incorrect object type");

    // Retrieve the value.
    if (currOffset == keyIntoValue.length - 1) { // The final dimension.
      return hash.get(currKey); }
    else { // Must recurse further.
      final Object o = hash.get(currKey);
      if (o == null) return null;
      return r_getNodeAttributeValue((HashMap) o, keyIntoValue, keyTypes,
                                     currOffset + 1); }
  }

  public final Object removeNodeAttributeValue(final String nodeKey,
                                               final String attributeName,
                                               final Object[] keyIntoValue)
  {
    // Pull out the definition, error-checking attributeName in the process.
    if (attributeName == null)
      throw new NullPointerException("attributeName is null");
    final AttrDefData def = (AttrDefData) m_nodeAttrMap.get(attributeName);
    if (def == null)
      throw new IllegalStateException
        ("no node attributeName '" + attributeName + "' exists");

    // Error-check nodeKey.
    if (nodeKey == null) throw new NullPointerException("nodeKey is null");

    // Error-check keyIntoValue.  Leave the type checks to the recursion.
    if (def.keyTypes.length == 0) {
      if (keyIntoValue != null || keyIntoValue.length != 0) {
        throw new IllegalArgumentException
          ("node attributeName '" + attributeName + "' has no keyspace" +
           " defined, yet keyIntoValue is not empty"); } }
    else { // Keyspace is not empty.
      if (def.keyTypes.length != keyIntoValue.length) { // May trigger NullPtr.
        throw new IllegalArgumentException
          ("keyIntoValue has incorrect dimensionality"); } }

    final CyNodeDataListener listener = m_nodeDataListener;
    if (def.keyTypes.length == 0) { // Don't even recurse.
      final Object returnThis = def.objMap.remove(nodeKey);
      if (listener != null && returnThis != null)
        listener.nodeAttributeValueRemoved
          (nodeKey, attributeName, null, returnThis);
      return returnThis; }
    else { // Recurse.
      final Object o = def.objMap.get(nodeKey);
      if (o == null) return null;
      final HashMap dim = (HashMap) o;
      final Object returnThis =
        r_getNodeAttributeValue(dim, keyIntoValue, def.keyTypes, 0);
      if (returnThis != null) {
        if (dim.size() == 0) def.objMap.remove(nodeKey);
        if (listener != null)
          listener.nodeAttributeValueRemoved
            (nodeKey, attributeName, keyIntoValue, returnThis); }
      return returnThis; }
  }

  private final Object r_removeNodeAttributeValue(final HashMap hash,
                                                  final Object[] keyIntoValue,
                                                  final byte[] keyTypes,
                                                  final int currOffset)
  {
    // Error check type of object keyIntoValue[currOffset].
    final Object currKey = keyIntoValue[currOffset];
    if (currKey == null)
      throw new NullPointerException("keyIntoValue[" + currOffset + "] null");
    boolean passed = false;
    switch (keyTypes[currOffset]) {
      case CyNodeDataDefinition.TYPE_BOOLEAN:
        passed = (currKey instanceof java.lang.Boolean); break;
      case CyNodeDataDefinition.TYPE_FLOATING_POINT:
        passed = (currKey instanceof java.lang.Double); break;
      case CyNodeDataDefinition.TYPE_INTEGER:
        passed = (currKey instanceof java.lang.Long); break;
      case CyNodeDataDefinition.TYPE_STRING:
        passed = (currKey instanceof java.lang.String); break; }
    if (!passed)
      throw new ClassCastException
        ("keyIntoValue[" + currOffset + "] is of incorrect object type");

    // Retrieve the value.
    if (currOffset == keyIntoValue.length - 1) { // The final dimension.
      return hash.remove(currKey); }
    else { // Must recurse further.
      final Object o = hash.get(currKey);
      if (o == null) return null;
      final HashMap dim = (HashMap) o;
      final Object returnThis =
        r_getNodeAttributeValue(dim, keyIntoValue, keyTypes, currOffset + 1);
      if (dim.size() == 0) hash.remove(currKey);
      return returnThis; }
  }

  public final int getNodeAttributeKeyspanCount(final String nodeKey,
                                                final String attributeName,
                                                final Object[] keyPrefix)
  {
    // Pull out the definition, error-checking attributeName in the process.
    if (attributeName == null)
      throw new NullPointerException("attributeName is null");
    final AttrDefData def = (AttrDefData) m_nodeAttrMap.get(attributeName);
    if (def == null)
      throw new IllegalStateException
        ("no node attributeName '" + attributeName + "' exists");

    // Error-check nodeKey.
    if (nodeKey == null) throw new NullPointerException("nodeKey is null");

    // Error-check keyPrefix.  Leave the type checks to the recursion.
    if (def.keyTypes.length == 0)
      throw new IllegalStateException
        ("node attributeName '" + attributeName + "' has no keyspace, so" +
         " calling this method makes no sense");
    if (keyPrefix != null && keyPrefix.length >= def.keyTypes.length)
      throw new IllegalArgumentException
        ("the length of keyPrefix must be strictly less than the" +
         " dimensionality of keyspace");

    if (keyPrefix == null || keyPrefix.length == 0) { // Don't even recurse.
      final HashMap dim = (HashMap) def.objMap.get(nodeKey);
      if (dim == null) return 0;
      return dim.size(); }
    else { // Recurse.
      final HashMap dim = (HashMap) def.objMap.get(nodeKey);
      if (dim == null) return 0;
      return r_getNodeAttributeKeyspanCount(dim, keyPrefix, def.keyTypes, 0); }
  }

  private final int r_getNodeAttributeKeyspanCount(final HashMap hash,
                                                   final Object[] keyPrefix,
                                                   final byte[] keyTypes,
                                                   final int currOffset)
  {
    // Error-check type of object keyPrefix[currOffset].
    final Object currKey = keyPrefix[currOffset];
    if (currKey == null)
      throw new NullPointerException("keyPrefix[" + currOffset + "] is null");
    boolean passed = false;
    switch (keyTypes[currOffset]) {
      case CyNodeDataDefinition.TYPE_BOOLEAN:
        passed = (currKey instanceof java.lang.Boolean); break;
      case CyNodeDataDefinition.TYPE_FLOATING_POINT:
        passed = (currKey instanceof java.lang.Double); break;
      case CyNodeDataDefinition.TYPE_INTEGER:
        passed = (currKey instanceof java.lang.Long); break;
      case CyNodeDataDefinition.TYPE_STRING:
        passed = (currKey instanceof java.lang.String); break; }
    if (!passed)
      throw new ClassCastException
        ("keyPrefix[" + currOffset + "] is of incorrect object type");

    if (currOffset == keyPrefix.length - 1) { // The dimension.
      final HashMap dim = (HashMap) hash.get(currKey);
      if (dim == null) return 0;
      return dim.size(); }
    else { // Recurse.
      final HashMap dim = (HashMap) hash.get(currKey);
      if (dim == null) return 0;
      return r_getNodeAttributeKeyspanCount(dim, keyPrefix, keyTypes,
                                            currOffset + 1); }
  }

  public final Enumeration getNodeAttributeKeyspan(final String nodeKey,
                                                   final String attributeName,
                                                   final Object[] keyPrefix)
  {
    // Pull out the definition, error-checking attributeName in the process.
    if (attributeName == null)
      throw new NullPointerException("attributeName is null");
    final AttrDefData def = (AttrDefData) m_nodeAttrMap.get(attributeName);
    if (def == null)
      throw new IllegalStateException
        ("no node attributeName '" + attributeName + "' exists");

    // Error-check nodeKey.
    if (nodeKey == null) throw new NullPointerException("nodeKey is null");

    // Error-check keyPrefix.  Leave the type checks to the recursion.
    if (def.keyTypes.length == 0)
      throw new IllegalStateException
        ("node attributeName '" + attributeName + "' has no keyspace, so" +
         " calling this method makes no sense");
    if (keyPrefix != null && keyPrefix.length >= def.keyTypes.length)
      throw new IllegalArgumentException
        ("the length of keyPrefix must be strictly less than the" +
         " dimensionality of keyspace");

    if (keyPrefix == null || keyPrefix.length == 0) { // Don't even recurse.
      final HashMap dim = (HashMap) def.objMap.get(nodeKey);
      if (dim == null) return s_the_empty_enumeration;
      return new Iterator2Enumeration(dim.keySet().iterator()); }
    else { // Recurse.
      final HashMap dim = (HashMap) def.objMap.get(nodeKey);
      if (dim == null) return s_the_empty_enumeration;
      return r_getNodeAttributeKeyspan(dim, keyPrefix, def.keyTypes, 0); }
  }

  private final Enumeration r_getNodeAttributeKeyspan(final HashMap hash,
                                                      final Object[] keyPrefix,
                                                      final byte[] keyTypes,
                                                      final int currOffset)
  {
    // Error-check type of object keyPrefix[currOffset].
    final Object currKey = keyPrefix[currOffset];
    if (currKey == null)
      throw new NullPointerException("keyPrefix[" + currOffset + "] is null");
    boolean passed = false;
    switch (keyTypes[currOffset]) {
      case CyNodeDataDefinition.TYPE_BOOLEAN:
        passed = (currKey instanceof java.lang.Boolean); break;
      case CyNodeDataDefinition.TYPE_FLOATING_POINT:
        passed = (currKey instanceof java.lang.Double); break;
      case CyNodeDataDefinition.TYPE_INTEGER:
        passed = (currKey instanceof java.lang.Long); break;
      case CyNodeDataDefinition.TYPE_STRING:
        passed = (currKey instanceof java.lang.String); break; }
    if (!passed)
      throw new ClassCastException
        ("keyPrefix[" + currOffset + "] is of incorrect object type");

    if (currOffset == keyPrefix.length - 1) { // The dimension.
      final HashMap dim = (HashMap) hash.get(currKey);
      if (dim == null) return s_the_empty_enumeration;
      return new Iterator2Enumeration(dim.keySet().iterator()); }
    else { // Recurse further.
      final HashMap dim = (HashMap) hash.get(currKey);
      if (dim == null) return s_the_empty_enumeration;
      return r_getNodeAttributeKeyspan(dim, keyPrefix, keyTypes,
                                       currOffset + 1); }
  }

  public final void addNodeDataListener(final CyNodeDataListener listener)
  {
    m_nodeDataListener = NodeAttrLisChain.add(m_nodeDataListener, listener);
  }

  public final void removeNodeDataListener(final CyNodeDataListener listener)
  {
    m_nodeDataListener = NodeAttrLisChain.remove(m_nodeDataListener, listener);
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

  public Object setEdgeAttributeValue(String edgeKey, String attributeName,
                                      Object attributeValue,
                                      Object[] keyIntoValue)
  {
    return null;
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

  public void addEdgeDataListener(CyEdgeDataListener listener)
  {
  }

  public void removeEdgeDataListener(CyEdgeDataListener listener)
  {
  }

}
