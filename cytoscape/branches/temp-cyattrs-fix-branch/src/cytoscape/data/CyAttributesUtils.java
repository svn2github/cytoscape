package cytosccape.data;

import cytoscape.data.CyAttributes;
import cytoscape.data.attr.MultiHashMap;
import cytoscape.data.attr.MultiHashMapDefinition;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CyAttributesUtils
{

  public static Map getAttribute(String attributeName, CyAttributes attrs)
  {
    Map attrMap;
    {
      final HashMap returnThis = new HashMap();
      final MultiHashMap mmap = nodeAttributes.getMultiHashMap();
      final MultiHashMapDefinition mmapDef = nodeAttributes.getMultiHashMapDefinition();
      if (mmapDef.getAttributeValueType(attributeName) != -1) {
        final Iterator objs = mmap.getObjectKeys(attributeName);
        while (objs.hasNext()) {
          final String obj = (String) objs.next();
          Object val;
          {
            switch (nodeAttributes.getType(attributeName)) {
            case CyAttributes.TYPE_BOOLEAN:
              val = nodeAttributes.getBooleanAttribute(obj, attributeName);
            case CyAttributes.TYPE_INTEGER:
              val = nodeAttributes.getIntegerAttribute(obj, attributeName);
            case CyAttributes.TYPE_FLOATING:
              val = nodeAttributes.getDoubleAttribute(obj, attributeName);
            case CyAttributes.TYPE_STRING:
              val = nodeAttributes.getStringAttribute(obj, attributeName);
            case CyAttributes.TYPE_SIMPLE_LIST:
              List l = nodeAttributes.getAttributeList(obj, attributeName);
              if (l.size() > 0) val = l.get(0);
              else val = null;
            case CyAttributes.TYPE_SIMPLE_MAP:
              val = nodeAttributes.getAttributeMap(obj, attributeName);
            default:
              val = null; }
          }
          returnThis.put(obj, val); } }
      attrMap = returnThis.size() == 0 ? null : returnThis;
    }
    return returnThis;
  }

}
