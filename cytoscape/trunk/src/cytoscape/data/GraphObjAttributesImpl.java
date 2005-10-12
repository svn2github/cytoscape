package cytoscape.data;

import cytoscape.task.TaskMonitor;
import java.io.File;
import java.util.HashMap;
import java.util.List;

public class GraphObjAttributesImpl implements GraphObjAttributes
{

  private final CyAttributes m_cyAttrs;

  public GraphObjAttributesImpl(CyAttributes cyAttrs)
  {
    m_cyAttrs = cyAttrs;
  }

  public boolean set(String attributeName, String id, Object value)
  {
    return false;
  }

  public boolean append(String attributeName, String id, Object value)
  {
    return false;
  }

  public boolean set(String attributeName, String id, double value)
  {
    return false;
  }

  public void setTaskMonitor(TaskMonitor taskMonitor)
  {
  }

  public int numberOfAttributes()
  {
    return 0;
  }

  public String[] getAttributeNames()
  {
    return null;
  }

  public boolean hasAttribute(String attributeName)
  {
    return false;
  }

  public boolean hasAttribute(String attributeName, String id)
  {
    return false;
  }

  public void deleteAttribute(String attributeName)
  {
  }

  public void deleteAttribute(String attributeName, String id)
  {
  }

  public Class getClass(String attributeName)
  {
    return null;
  }

  public List getList(String attributeName, String id)
  {
    return null;
  }

  public Object getValue(String attributeName, String id)
  {
    return null;
  }

  public Object get(String attributeName, String id)
  {
    return null;
  }

  public Double getDoubleValue(String attributeName, String id)
  {
    return null;
  }

  public Integer getIntegerValue(String attributeName, String id)
  {
    return null;
  }

  public String getStringValue(String attributeName, String id)
  {
    return null;
  }

  public HashMap getAttribute(String attributeName)
  {
    return null;
  }

  public String[] getStringArrayValues(String attributeName, String id)
  {
    return null;
  }

  public String toString()
  {
    return null;
  }

  public boolean set(String graphObjName, HashMap bundle)
  {
    return false;
  }

  public void clearNameMap()
  {
  }

  public void clearObjectMap()
  {
  }

  public HashMap getClassMap()
  {
    return null;
  }

  public void addClassMap(HashMap newClassMap)
  {
  }

  public HashMap getObjectMap()
  {
    return null;
  }

  public void addNameMap(HashMap nameMapping)
  {
  }

  public void addObjectMap(HashMap objectMapping)
  {
  }

  public void set(GraphObjAttributes attributes)
  {
  }

  public void deleteAttributeValue(String attributeName,
                                   String graphObjName, Object value)
  {
  }

  public void readAttributesFromFile(File file)
  {
  }

  public HashMap getSummary()
  {
    return null;
  }

  public int countIdentical(String graphObjName)
  {
    return 0;
  }

  public int getObjectCount(String attributeName)
  {
    return 0;
  }

  public String getCanonicalName(Object graphObj)
  {
    return null;
  }

  public HashMap getAttributes(String canonicalName)
  {
    return null;
  }

  public void addNameMapping(String canonicalName, Object graphObject)
  {
  }

  public Object getGraphObject(String canonicalName)
  {
    return null;
  }

  public String[] getObjectNames(String attributeName)
  {
    return null;
  }

  public void removeNameMapping(String canonicalName)
  {
  }

  public boolean setClass(String attributeName, Class attributeClass)
  {
    return false;
  }

  public void removeObjectMapping(Object graphObj)
  {
  }

  public Object[] getArrayValues(String attributeName,
                                 String graphObjectName)
  {
    return null;
  }

  public void readAttributesFromFile(String filename)
  {
  }

  public HashMap getNameMap()
  {
    return null;
  }

  public Object[] getUniqueValues(String attributeName)
  {
    return null;
  }

  public String[] getUniqueStringValues(String attributeName)
  {
    return null;
  }

  public String processFileHeader(String text)
  {
    return null;
  }

}
