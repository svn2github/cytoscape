package cytoscape.data;

import cytoscape.data.attr.CountedIterator;
import cytoscape.data.attr.MultiHashMap;
import cytoscape.data.attr.MultiHashMapDefinition;

import java.util.List;
import java.util.Map;

public class CyAttributesImpl implements CyAttributes
{

  private MultiHashMap mmap;
  private MultiHashMapDefinition mmapDef;

  public String[] getAttributeNames()
  {
    final CountedIterator citer = mmapDef.getDefinedAttributes();
    final String[] names = new String[citer.numRemaining()];
    int inx = 0;
    while (citer.hasNext()) {
      names[inx++] = (String) citer.next(); }
    return names;
  }

  public boolean hasAttribute(String id, String attributeName)
  {
    final byte valType = mmapDef.getAttributeValueType(attributeName);
    if (valType < 0) return false;
    final byte[] dimTypes = mmapDef.getAttributeKeyspaceDimensionTypes
      (attributeName);
    if (dimTypes.length == 0) {
      return mmap.getAttributeValue(id, attributeName, null) != null; }
    else {
      return mmap.getAttributeKeyspan
        (id, attributeName, null).numRemaining() > 0; }
  }

  public void setAttribute(String id, String attributeName, Boolean value)
  {
  }

  public void setAttribute(String id, String attributeName, Integer value)
  {
  }

  public void setAttribute(String id, String attributeName, Double value)
  {
  }

  public void setAttribute(String id, String attributeName, String value)
  {
  }

  public Boolean getBooleanAttribute(String id, String attributeName)
  {
    return null;
  }

  public Integer getIntegerAttribute(String id, String attributeName)
  {
    return null;
  }

  public Double getDoubleAttribute(String id, String attributeName)
  {
    return null;
  }

  public String getStringAttribute(String id, String attributeName)
  {
    return null;
  }

  public byte getType(String attributeName)
  {
    return 0;
  }

  public boolean deleteAttribute(String id, String attributeName)
  {
    return false;
  }

  public boolean deleteAttribute(String attributeName)
  {
    return false;
  }

  public void setAttributeList(String id, String attributeName, List list)
  {
  }

  public List getAttributeList(String id, String attributeName)
  {
    return null;
  }

  public void setAttributeMap(String id, String attributeName, Map map)
  {
  }

  public Map getAttributeMap(String id, String attributeName)
  {
    return null;
  }

  public MultiHashMap getMultiHashMap()
  {
    return null;
  }

  public MultiHashMapDefinition getMultiHashMapDefinition()
  {
    return null;
  }

}
