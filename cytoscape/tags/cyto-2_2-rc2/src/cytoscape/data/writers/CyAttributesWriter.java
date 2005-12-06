package cytoscape.data.writers;

import cytoscape.data.CyAttributes;
import cytoscape.data.attr.MultiHashMapDefinition;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

public class CyAttributesWriter
{

  public static void writeAttributes(CyAttributes cyAttrs,
                                     String attributeName,
                                     Writer fileOut) throws IOException
  {
    final BufferedWriter writer;
    if (fileOut instanceof BufferedWriter) {
      writer = (BufferedWriter) fileOut; }
    else {
      writer = new BufferedWriter(fileOut); }
    final byte cyType = cyAttrs.getType(attributeName);
    if (!(cyType == CyAttributes.TYPE_BOOLEAN ||
          cyType == CyAttributes.TYPE_FLOATING ||
          cyType == CyAttributes.TYPE_INTEGER ||
          cyType == CyAttributes.TYPE_STRING ||
          cyType == CyAttributes.TYPE_SIMPLE_LIST)) { return; }
    final byte mulType =
      cyAttrs.getMultiHashMapDefinition().getAttributeValueType(attributeName);
    writer.write(attributeName);
    writer.write(" (class=");
    {
      final String className;
      if (mulType == MultiHashMapDefinition.TYPE_BOOLEAN) {
        className = "java.lang.Boolean"; }
      else if (mulType == MultiHashMapDefinition.TYPE_INTEGER) {
        className = "java.lang.Integer"; }
      else if (mulType == MultiHashMapDefinition.TYPE_FLOATING_POINT) {
        className = "java.lang.Double"; }
      else {
        className = "java.lang.String"; }
      writer.write(className);
    }
    writer.write(")");
    writer.newLine();
    final Iterator keys =
      cyAttrs.getMultiHashMap().getObjectKeys(attributeName);
    while (keys.hasNext()) {
      final String key = (String) keys.next();
      writer.write(key);
      writer.write("=");
      if (cyType == CyAttributes.TYPE_BOOLEAN) {
        writer.write
          (cyAttrs.getBooleanAttribute(key, attributeName).toString()); }
      else if (cyType == CyAttributes.TYPE_INTEGER) {
        writer.write
          (cyAttrs.getIntegerAttribute(key, attributeName).toString()); }
      else if (cyType == CyAttributes.TYPE_FLOATING) {
        writer.write
          (cyAttrs.getDoubleAttribute(key, attributeName).toString()); }
      else if (cyType == CyAttributes.TYPE_STRING) {
        writer.write(cyAttrs.getStringAttribute(key, attributeName)); }
      else { // TYPE_SIMPLE_LIST
        writer.write("(");
        final Iterator listElms =
          cyAttrs.getAttributeList(key, attributeName).iterator();
        while (listElms.hasNext()) {
          writer.write(listElms.next().toString());
          if (listElms.hasNext()) { writer.write("::"); } }
        writer.write(")"); }
      writer.newLine();; }
      writer.flush();
  }

}
