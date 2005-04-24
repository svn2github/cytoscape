package cytoscape.data.attr.util;

import cytoscape.data.attr.CountedEnumeration;
import cytoscape.data.attr.CyData;
import cytoscape.data.attr.CyDataDefinition;
import cytoscape.data.attr.CyDataDefinitionListener;
import cytoscape.data.attr.CyDataListener;

import java.util.HashMap;

final class CyDataModel implements CyDataDefinition, CyData
{

  private final static class AttrDefData
  {
    private final HashMap objMap; // Keys are objectKey.
    private final byte valueType;
    private final byte[] keyTypes;
    private AttrDefData(final HashMap objMap, final byte valueType,
                        final byte[] keyTypes)
    {
      this.objMap = objMap;
      this.valueType = valueType;
      this.keyTypes = keyTypes;
    }
  }

  private final static class Iterator2Enumeration implements CountedEnumeration
  {
    private final java.util.Iterator iter;
    private int count;
    private Iterator2Enumeration(final java.util.Iterator iter,
                                 final int count) {
      this.iter = iter;
      this.count = count; }
    public final boolean hasMoreElements() { return iter.hasNext(); }
    public final Object nextElement() {
      if (iter.hasNext()) count--;
      return iter.next(); }
    public final int numElementsRemaining() { return count; }
  }

  private final static class AttrDefLisChain
    implements CyDataDefinitionListener
  {
    // Use only the static methods from outside this inner class.
    private final CyDataDefinitionListener a, b;
    private AttrDefLisChain(final CyDataDefinitionListener a,
                            final CyDataDefinitionListener b) {
      this.a = a;
      this.b = b; }
    public final void attributeDefined(final String attributeName) {
      a.attributeDefined(attributeName);
      b.attributeDefined(attributeName); }
    public final void attributeUndefined(final String attributeName) {
      a.attributeUndefined(attributeName);
      b.attributeUndefined(attributeName); }
    private final static CyDataDefinitionListener add(
                                            final CyDataDefinitionListener a,
                                            final CyDataDefinitionListener b) {
      if (a == null) return b;
      if (b == null) return a;
      return new AttrDefLisChain(a, b); }
    private final static CyDataDefinitionListener remove(
                                         final CyDataDefinitionListener l,
                                         final CyDataDefinitionListener oldl) {
      if (l == oldl || l == null) return null;
      else if (l instanceof AttrDefLisChain)
        return ((AttrDefLisChain) l).remove(oldl);
      else return l; }
    private final CyDataDefinitionListener remove(
                                         final CyDataDefinitionListener oldl) {
      if (oldl == a) return b;
      if (oldl == b) return a;
      final CyDataDefinitionListener a2 = remove(a, oldl);
      final CyDataDefinitionListener b2 = remove(b, oldl);
      if (a2 == a && b2 == b) return this;
      return add(a2, b2); }
  }

  private final static class AttrLisChain implements CyDataListener
  {
    // Use only the static methods from outside this inner class.
    private final CyDataListener a, b;
    private AttrLisChain(final CyDataListener a, final CyDataListener b) {
      this.a = a;
      this.b = b; }
    public final void attributeValueAssigned(final String objectKey,
                                             final String attributeName,
                                             final Object[] keyIntoValue,
                                             final Object oldAttrVal,
                                             final Object newAttrVal) {
      a.attributeValueAssigned(objectKey, attributeName, keyIntoValue,
                               oldAttrVal, newAttrVal);
      b.attributeValueAssigned(objectKey, attributeName, keyIntoValue,
                               oldAttrVal, newAttrVal); }
    public final void attributeValueRemoved(final String objectKey,
                                            final String attributeName,
                                            final Object[] keyIntoValue,
                                            final Object attributeValue) {
      a.attributeValueRemoved(objectKey, attributeName, keyIntoValue,
                              attributeValue);
      b.attributeValueRemoved(objectKey, attributeName, keyIntoValue,
                              attributeValue); }
    public final void allAttributeValuesRemoved(final String objectKey,
                                                final String attributeName) {
      a.allAttributeValuesRemoved(objectKey, attributeName);
      b.allAttributeValuesRemoved(objectKey, attributeName); }
    private final static CyDataListener add(final CyDataListener a,
                                            final CyDataListener b) {
      if (a == null) return b;
      if (b == null) return a;
      return new AttrLisChain(a, b); }
    private final static CyDataListener remove(final CyDataListener l,
                                               final CyDataListener oldl) {
      if (l == oldl || l == null) return null;
      else if (l instanceof AttrLisChain)
        return ((AttrLisChain) l).remove(oldl);
      else return l; }
    private final CyDataListener remove(final CyDataListener oldl) {
      if (oldl == a) return b;
      if (oldl == b) return a;
      final CyDataListener a2 = remove(a, oldl);
      final CyDataListener b2 = remove(b, oldl);
      if (a2 == a && b2 == b) return this;
      return add(a2, b2); }
  }

  private final static CountedEnumeration s_the_empty_enumeration =
    new CountedEnumeration() {
      public final int numElementsRemaining() { return 0; }
      public final boolean hasMoreElements() { return false; }
      public final Object nextElement() {
        throw new java.util.NoSuchElementException(); } };

  // Keys are attributeName, values are AttrDefData.
  private final HashMap m_attrMap;

  private CyDataDefinitionListener m_dataDefListener;
  private CyDataListener m_dataListener;

  CyDataModel()
  {
    m_attrMap = new HashMap();
    m_dataDefListener = null;
    m_dataListener = null;
  }

  public final void defineAttribute(final String attributeName,
                                    final byte valueType,
                                    final byte[] keyTypes)
  {
    // Error-check attributeName.  Unfortunately there are currently no
    // constraints on the length or the contents of attributeName.
    if (attributeName == null)
      throw new NullPointerException("attributeName is null");
    if (m_attrMap.containsKey(attributeName))
      throw new IllegalStateException
        ("attributeName '" + attributeName + "' already exists");

    // Error-check valueType.
    switch (valueType)
    {
      case CyDataDefinition.TYPE_BOOLEAN:
      case CyDataDefinition.TYPE_FLOATING_POINT:
      case CyDataDefinition.TYPE_INTEGER:
      case CyDataDefinition.TYPE_STRING:
        break;
      default:
        throw new IllegalArgumentException("valueType is unrecognized");
    }

    // Make copy of keyTypes.
    final int keyTypesLength = (keyTypes == null ? 0 : keyTypes.length);
    final byte[] keyTypesCopy = new byte[keyTypesLength];
    if (keyTypes != null)
      for (int i = 0; i < keyTypesLength; i++)
        keyTypesCopy[i] = keyTypes[i];

    // Error-check keyTypesCopy.
    for (int i = 0; i < keyTypesCopy.length; i++)
      switch (keyTypesCopy[i])
      {
        case CyDataDefinition.TYPE_BOOLEAN:
        case CyDataDefinition.TYPE_FLOATING_POINT:
        case CyDataDefinition.TYPE_INTEGER:
        case CyDataDefinition.TYPE_STRING:
          break;
        default:
          throw new IllegalArgumentException
            ("keyTypes[" + i + "] is unrecognized");
      }

    // Finally, create the definition.
    final AttrDefData def = new AttrDefData(new HashMap(), valueType,
                                            keyTypesCopy);
    m_attrMap.put(attributeName, def);

    // Call listeners.  Make sure this is done after we actually create def.
    final CyDataDefinitionListener l = m_dataDefListener;
    if (l != null) l.attributeDefined(attributeName);
  }

  public final CountedEnumeration getDefinedAttributes()
  {
    return new Iterator2Enumeration(m_attrMap.keySet().iterator(),
                                    m_attrMap.size());
  }

  public final byte getAttributeValueType(final String attributeName)
  {
    if (attributeName == null)
      throw new NullPointerException("attributeName is null");
    final AttrDefData def = (AttrDefData) m_attrMap.get(attributeName);
    if (def == null) return -1;
    return def.valueType;
  }

  public final int getAttributeKeyspaceDimensionality(
                                                    final String attributeName)
  {
    if (attributeName == null)
      throw new NullPointerException("attributeName is null");
    final AttrDefData def = (AttrDefData) m_attrMap.get(attributeName);
    if (def == null) return -1;
    return def.keyTypes.length;
  }

  public final void copyAttributeKeyspaceInfo(final String attributeName,
                                              final byte[] keyTypes)
  {
    if (attributeName == null)
      throw new NullPointerException("attributeName is null");
    final AttrDefData def = (AttrDefData) m_attrMap.get(attributeName);
    if (def == null)
      throw new IllegalStateException
        ("no attributeName '" + attributeName + "' exists");
    for (int i = 0; i < def.keyTypes.length; i++)
      keyTypes[i] = def.keyTypes[i];
  }

  public final void undefineAttribute(final String attributeName)
  {
    if (attributeName == null)
      throw new NullPointerException("attributeName is null");
    final Object o = m_attrMap.remove(attributeName);
    if (o != null) { // attributeName was in fact deleted.
      final CyDataDefinitionListener l = m_dataDefListener;
      if (l != null) l.attributeUndefined(attributeName); }
  }

  public final void addDataDefinitionListener(
                                       final CyDataDefinitionListener listener)
  {
    m_dataDefListener = AttrDefLisChain.add(m_dataDefListener, listener);
  }

  public final void removeDataDefinitionListener(
                                       final CyDataDefinitionListener listener)
  {
    m_dataDefListener = AttrDefLisChain.remove(m_dataDefListener, listener);
  }

  public final Object setAttributeValue(final String objectKey,
                                        final String attributeName,
                                        final Object attributeValue,
                                        final Object[] keyIntoValue)
  {
    // Pull out the definition, error-checking attributeName in the process.
    if (attributeName == null)
      throw new NullPointerException("attributeName is null");
    final AttrDefData def = (AttrDefData) m_attrMap.get(attributeName);
    if (def == null)
      throw new IllegalStateException
        ("no attributeName '" + attributeName + "' exists");

    // Error-check objectKey.  Right now there are no constraints on the length
    // or the contents of objectKey.  In the future, consider having
    // a "registry" of all objectKeys and permitting only assignment to those
    // objectKeys.
    if (objectKey == null) throw new NullPointerException("objectKey is null");

    // Error-check attributeValue.  Note that the instanceof operation always
    // returns false for null values, and does not throw an exception.
    if (attributeValue == null)
      throw new NullPointerException("cannot set null attributeValue - " +
                                     "use removeAttributeValue() instead");
    boolean passed = false;
    switch (def.valueType)
    { // I'm wondering what the most efficient way of doing this is.
      case CyDataDefinition.TYPE_BOOLEAN:
        passed = (attributeValue instanceof java.lang.Boolean); break;
      case CyDataDefinition.TYPE_FLOATING_POINT:
        passed = (attributeValue instanceof java.lang.Double); break;
      case CyDataDefinition.TYPE_INTEGER:
        passed = (attributeValue instanceof java.lang.Integer); break;
      case CyDataDefinition.TYPE_STRING:
        passed = (attributeValue instanceof java.lang.String); break;
    }
    if (!passed) { // Go the extra effort to return an informational error.
      String className = null;
      switch (def.valueType)
      { // Repeat same switch logic here for efficiency in non-error case.
        case CyDataDefinition.TYPE_BOOLEAN:
          className = "java.lang.Boolean"; break;
        case CyDataDefinition.TYPE_FLOATING_POINT:
          className = "java.lang.Double"; break;
        case CyDataDefinition.TYPE_INTEGER:
          className = "java.lang.Integer"; break;
        case CyDataDefinition.TYPE_STRING:
          className = "java.lang.String"; break;
      }
      throw new ClassCastException
        ("attributeValue must be of type " + className +
         " in attributeName '" + attributeName + "' definition"); }

    // Error-check keyIntoValue.  Leave the type checks to the recursion.
    if (def.keyTypes.length == 0) {
      if (keyIntoValue != null && keyIntoValue.length != 0) {
        throw new IllegalArgumentException
          ("attributeName '" + attributeName + "' has no keyspace" +
           " defined, yet keyIntoValue is not empty"); } }
    else { // Keyspace is not empty.
      final int keyIntoValueLength =
        (keyIntoValue == null ? 0 : keyIntoValue.length);
      if (def.keyTypes.length != keyIntoValueLength) {
        throw new IllegalArgumentException
          ("keyIntoValue has incorrect dimensionality"); } }

    final CyDataListener listener = m_dataListener;
    if (def.keyTypes.length == 0) { // Don't even recurse.
      final Object returnThis = def.objMap.put(objectKey, attributeValue);
      if (listener != null)
        listener.attributeValueAssigned(objectKey, attributeName, null,
                                        returnThis, attributeValue);
      return returnThis; }
    else { // Recurse.
      final Object o = def.objMap.get(objectKey);
      final HashMap firstDim;
      if (o == null) firstDim = new HashMap();
      else firstDim = (HashMap) o;
      final Object returnThis =
        r_setAttributeValue(firstDim, attributeValue, keyIntoValue,
                            def.keyTypes, 0);
      // If firstDim is a new HashMap add it to the definition after the
      // recursion completes so that if an exception is thrown, we can avoid
      // cleanup.
      if (o == null) def.objMap.put(objectKey, firstDim);
      if (listener != null)
        listener.attributeValueAssigned
          (objectKey, attributeName, keyIntoValue, returnThis, attributeValue);
      return returnThis; }
  }

  // Recursive helper method.
  private final Object r_setAttributeValue(final HashMap hash,
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
      case CyDataDefinition.TYPE_BOOLEAN:
        passed = (currKey instanceof java.lang.Boolean); break;
      case CyDataDefinition.TYPE_FLOATING_POINT:
        passed = (currKey instanceof java.lang.Double); break;
      case CyDataDefinition.TYPE_INTEGER:
        passed = (currKey instanceof java.lang.Integer); break;
      case CyDataDefinition.TYPE_STRING:
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
        r_setAttributeValue(dim, attributeValue, keyIntoValue, keyTypes,
                            currOffset + 1);
      // Put new HashMap in after recursive call to prevent the need for
      // cleanup in case exception is thrown.
      if (o == null) hash.put(currKey, dim);
      return returnThis; }
  }

  public final Object getAttributeValue(final String objectKey,
                                        final String attributeName,
                                        final Object[] keyIntoValue)
  {
    // Pull out the definition, error-checking attributeName in the process.
    if (attributeName == null)
      throw new NullPointerException("attributeName is null");
    final AttrDefData def = (AttrDefData) m_attrMap.get(attributeName);
    if (def == null)
      throw new IllegalStateException
        ("no attributeName '" + attributeName + "' exists");

    // Error-check objectKey.
    if (objectKey == null) throw new NullPointerException("objectKey is null");

    // Error-check keyIntoValue.  Leave the type checks to the recursion.
    if (def.keyTypes.length == 0) {
      if (keyIntoValue != null && keyIntoValue.length != 0) {
        throw new IllegalArgumentException
          ("attributeName '" + attributeName + "' has no keyspace" +
           " defined, yet keyIntoValue is not empty"); } }
    else { // Keyspace is not empty.
      final int keyIntoValueLength =
        (keyIntoValue == null ? 0 : keyIntoValue.length);
      if (def.keyTypes.length != keyIntoValueLength) {
        throw new IllegalArgumentException
          ("keyIntoValue has incorrect dimensionality"); } }

    if (def.keyTypes.length == 0) { // Don't even recurse.
      return def.objMap.get(objectKey); }
    else { // Recurse.
      final Object o = def.objMap.get(objectKey);
      if (o == null) return null;
      return r_getAttributeValue((HashMap) o, keyIntoValue, def.keyTypes, 0); }
  }

  private final Object r_getAttributeValue(final HashMap hash,
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
      case CyDataDefinition.TYPE_BOOLEAN:
        passed = (currKey instanceof java.lang.Boolean); break;
      case CyDataDefinition.TYPE_FLOATING_POINT:
        passed = (currKey instanceof java.lang.Double); break;
      case CyDataDefinition.TYPE_INTEGER:
        passed = (currKey instanceof java.lang.Integer); break;
      case CyDataDefinition.TYPE_STRING:
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
      return r_getAttributeValue((HashMap) o, keyIntoValue, keyTypes,
                                 currOffset + 1); }
  }

  public final Object removeAttributeValue(final String objectKey,
                                           final String attributeName,
                                           final Object[] keyIntoValue)
  {
    // Pull out the definition, error-checking attributeName in the process.
    if (attributeName == null)
      throw new NullPointerException("attributeName is null");
    final AttrDefData def = (AttrDefData) m_attrMap.get(attributeName);
    if (def == null)
      throw new IllegalStateException
        ("no attributeName '" + attributeName + "' exists");

    // Error-check objectKey.
    if (objectKey == null) throw new NullPointerException("objectKey is null");

    // Error-check keyIntoValue.  Leave the type checks to the recursion.
    if (def.keyTypes.length == 0) {
      if (keyIntoValue != null && keyIntoValue.length != 0) {
        throw new IllegalArgumentException
          ("attributeName '" + attributeName + "' has no keyspace" +
           " defined, yet keyIntoValue is not empty"); } }
    else { // Keyspace is not empty.
      final int keyIntoValueLength =
        (keyIntoValue == null ? 0 : keyIntoValue.length);
      if (def.keyTypes.length != keyIntoValueLength) {
        throw new IllegalArgumentException
          ("keyIntoValue has incorrect dimensionality"); } }

    final CyDataListener listener = m_dataListener;
    if (def.keyTypes.length == 0) { // Don't even recurse.
      final Object returnThis = def.objMap.remove(objectKey);
      if (listener != null && returnThis != null)
        listener.attributeValueRemoved
          (objectKey, attributeName, null, returnThis);
      return returnThis; }
    else { // Recurse.
      final Object o = def.objMap.get(objectKey);
      if (o == null) return null;
      final HashMap dim = (HashMap) o;
      final Object returnThis =
        r_removeAttributeValue(dim, keyIntoValue, def.keyTypes, 0);
      if (returnThis != null) {
        if (dim.size() == 0) def.objMap.remove(objectKey);
        if (listener != null)
          listener.attributeValueRemoved
            (objectKey, attributeName, keyIntoValue, returnThis); }
      return returnThis; }
  }

  private final Object r_removeAttributeValue(final HashMap hash,
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
      case CyDataDefinition.TYPE_BOOLEAN:
        passed = (currKey instanceof java.lang.Boolean); break;
      case CyDataDefinition.TYPE_FLOATING_POINT:
        passed = (currKey instanceof java.lang.Double); break;
      case CyDataDefinition.TYPE_INTEGER:
        passed = (currKey instanceof java.lang.Integer); break;
      case CyDataDefinition.TYPE_STRING:
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
        r_removeAttributeValue(dim, keyIntoValue, keyTypes, currOffset + 1);
      if (dim.size() == 0) hash.remove(currKey);
      return returnThis; }
  }

  public final boolean removeAllAttributeValues(final String objectKey,
                                                final String attributeName)
  {
    // Pull out the definition, error-checking attributeName in the process.
    if (attributeName == null)
      throw new NullPointerException("attributeName is null");
    final AttrDefData def = (AttrDefData) m_attrMap.get(attributeName);
    if (def == null)
      throw new IllegalStateException
        ("no attributeName '" + attributeName + "' exists");

    // Error-check objectKey.
    if (objectKey == null) throw new NullPointerException("objectKey is null");

    final boolean returnThis = (def.objMap.remove(objectKey) != null);
    if (returnThis) {
      final CyDataListener listener = m_dataListener;
      if (listener != null)
        listener.allAttributeValuesRemoved(objectKey, attributeName); }
    return returnThis;
  }

  public final CountedEnumeration getAttributeKeyspan(
                                                    final String objectKey,
                                                    final String attributeName,
                                                    final Object[] keyPrefix)
  {
    // Pull out the definition, error-checking attributeName in the process.
    if (attributeName == null)
      throw new NullPointerException("attributeName is null");
    final AttrDefData def = (AttrDefData) m_attrMap.get(attributeName);
    if (def == null)
      throw new IllegalStateException
        ("no attributeName '" + attributeName + "' exists");

    // Error-check objectKey.
    if (objectKey == null) throw new NullPointerException("objectKey is null");

    // Error-check keyPrefix.  Leave the type checks to the recursion.
    if (def.keyTypes.length == 0)
      throw new IllegalStateException
        ("attributeName '" + attributeName + "' has no keyspace, so" +
         " calling this method makes no sense");
    if (keyPrefix != null && keyPrefix.length >= def.keyTypes.length)
      throw new IllegalArgumentException
        ("the length of keyPrefix must be strictly less than the" +
         " dimensionality of keyspace");

    if (keyPrefix == null || keyPrefix.length == 0) { // Don't even recurse.
      final HashMap dim = (HashMap) def.objMap.get(objectKey);
      if (dim == null) return s_the_empty_enumeration;
      return new Iterator2Enumeration(dim.keySet().iterator(), dim.size()); }
    else { // Recurse.
      final HashMap dim = (HashMap) def.objMap.get(objectKey);
      if (dim == null) return s_the_empty_enumeration;
      return r_getAttributeKeyspan(dim, keyPrefix, def.keyTypes, 0); }
  }

  private final CountedEnumeration r_getAttributeKeyspan(
                                                      final HashMap hash,
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
      case CyDataDefinition.TYPE_BOOLEAN:
        passed = (currKey instanceof java.lang.Boolean); break;
      case CyDataDefinition.TYPE_FLOATING_POINT:
        passed = (currKey instanceof java.lang.Double); break;
      case CyDataDefinition.TYPE_INTEGER:
        passed = (currKey instanceof java.lang.Integer); break;
      case CyDataDefinition.TYPE_STRING:
        passed = (currKey instanceof java.lang.String); break; }
    if (!passed)
      throw new ClassCastException
        ("keyPrefix[" + currOffset + "] is of incorrect object type");

    if (currOffset == keyPrefix.length - 1) { // The dimension.
      final HashMap dim = (HashMap) hash.get(currKey);
      if (dim == null) return s_the_empty_enumeration;
      return new Iterator2Enumeration(dim.keySet().iterator(), dim.size()); }
    else { // Recurse further.
      final HashMap dim = (HashMap) hash.get(currKey);
      if (dim == null) return s_the_empty_enumeration;
      return r_getAttributeKeyspan(dim, keyPrefix, keyTypes,
                                   currOffset + 1); }
  }

  public final CountedEnumeration getObjectKeys(final String attributeName)
  {
    // Pull out the definition, error-checking attributeName in the process.
    if (attributeName == null)
      throw new NullPointerException("attributeName is null");
    final AttrDefData def = (AttrDefData) m_attrMap.get(attributeName);
    if (def == null)
      throw new IllegalStateException
        ("no attributeName '" + attributeName + "'exists");

    return new Iterator2Enumeration(def.objMap.keySet().iterator(),
                                    def.objMap.size());
  }

  public final void addDataListener(final CyDataListener listener)
  {
    m_dataListener = AttrLisChain.add(m_dataListener, listener);
  }

  public final void removeDataListener(final CyDataListener listener)
  {
    m_dataListener = AttrLisChain.remove(m_dataListener, listener);
  }

}
