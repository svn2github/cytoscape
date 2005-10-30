package cytoscape.data;

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
      final MultiHashMap mmap = attrs.getMultiHashMap();
      final MultiHashMapDefinition mmapDef = attrs.getMultiHashMapDefinition();
      if (mmapDef.getAttributeValueType(attributeName) != -1) {
        final Iterator objs = mmap.getObjectKeys(attributeName);
        while (objs.hasNext()) {
          final String obj = (String) objs.next();
          Object val;
          {
            switch (attrs.getType(attributeName)) {
            case CyAttributes.TYPE_BOOLEAN:
              val = attrs.getBooleanAttribute(obj, attributeName);
            case CyAttributes.TYPE_INTEGER:
              val = attrs.getIntegerAttribute(obj, attributeName);
            case CyAttributes.TYPE_FLOATING:
              val = attrs.getDoubleAttribute(obj, attributeName);
            case CyAttributes.TYPE_STRING:
              val = attrs.getStringAttribute(obj, attributeName);
            case CyAttributes.TYPE_SIMPLE_LIST:
              List l = attrs.getAttributeList(obj, attributeName);
              if (l.size() > 0) val = l.get(0);
              else val = null;
            case CyAttributes.TYPE_SIMPLE_MAP:
              val = attrs.getAttributeMap(obj, attributeName);
            default:
              val = null; }
          }
          returnThis.put(obj, val); } }
      attrMap = returnThis.size() == 0 ? null : returnThis;
    }
    return returnThis;
  }

}
