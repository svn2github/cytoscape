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
	      break;
            case CyAttributes.TYPE_INTEGER:
              val = attrs.getIntegerAttribute(obj, attributeName);
	      break;
            case CyAttributes.TYPE_FLOATING:
              val = attrs.getDoubleAttribute(obj, attributeName);
	      break;
            case CyAttributes.TYPE_STRING:
              val = attrs.getStringAttribute(obj, attributeName);
	      break;
            case CyAttributes.TYPE_SIMPLE_LIST:
              List l = attrs.getAttributeList(obj, attributeName);
              if (l.size() > 0) val = l.get(0);
              else val = null;
	      break;
            case CyAttributes.TYPE_SIMPLE_MAP:
              val = attrs.getAttributeMap(obj, attributeName);
	      break;
            default:
              val = null; }
          }
          returnThis.put(obj, val); } }
      attrMap = returnThis.size() == 0 ? null : returnThis;
    }
    return attrMap;
  }

  public static Map getAttributes(String canonicalName, CyAttributes attrs)
  {
	Map returnThis = new HashMap();
        final String[] attrNames = attrs.getAttributeNames();
        for (int i = 0; i < attrNames.length; i++) {
          final byte type = attrs.getType(attrNames[i]);
          if (attrs.hasAttribute(canonicalName, attrNames[i])) {
            if (type == CyAttributes.TYPE_SIMPLE_LIST) {
              List l = attrs.getAttributeList(canonicalName, attrNames[i]);
              if (l != null && l.size() > 0) {
                returnThis.put(attrNames[i], l.get(0)); 
	      } }
            else if (type == CyAttributes.TYPE_BOOLEAN) {
              returnThis.put(attrNames[i],attrs.getBooleanAttribute(canonicalName, attrNames[i])); }
            else if (type == CyAttributes.TYPE_INTEGER) {
              returnThis.put(attrNames[i],attrs.getIntegerAttribute(canonicalName, attrNames[i])); }
            else if (type == CyAttributes.TYPE_FLOATING) {
              returnThis.put(attrNames[i],attrs.getDoubleAttribute(canonicalName, attrNames[i])); }
            else if (type == CyAttributes.TYPE_STRING) {
              returnThis.put(attrNames[i],attrs.getStringAttribute(canonicalName, attrNames[i])); } } }

        return returnThis;
  }

  public static Class getClass(String attributeName, CyAttributes attrs) {
        Class cl = null;
        switch (attrs.getType(attributeName)) {
            case CyAttributes.TYPE_BOOLEAN:
              cl = Boolean.class;
              break;
            case CyAttributes.TYPE_INTEGER:
              cl = Integer.class;
              break;
            case CyAttributes.TYPE_FLOATING:
              cl = Double.class;
              break;
            case CyAttributes.TYPE_STRING:
              cl = String.class;
              break;
            case CyAttributes.TYPE_SIMPLE_LIST:
              cl = List.class;
              break;
            case CyAttributes.TYPE_SIMPLE_MAP:
              cl = Map.class;
              break;
            default:
              cl = null;
        }
        return cl;
  }
}
