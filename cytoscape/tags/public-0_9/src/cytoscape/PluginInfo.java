// PluginInfo: 
//-----------------------------------------------------------------------------------------
// $Revision$   
// $Date$ 
// $Author$
//-----------------------------------------------------------------------------------
package cytoscape;
//-----------------------------------------------------------------------------------
public class PluginInfo {
  String className;
  String fileExtension;
  String attributeName; 
//-----------------------------------------------------------------------------------
public PluginInfo ()
{

}
//-----------------------------------------------------------------------------------
public PluginInfo (String className, String fileExtension, String attributeName)
{
  this.className = className;
  this.fileExtension = fileExtension;
  this.attributeName = attributeName;
  
}
//-----------------------------------------------------------------------------------
public void setClassName (String newValue)
{
  className = newValue;
}
//-----------------------------------------------------------------------------------
public void setFileExtension (String newValue)
{
  fileExtension = newValue;
}
//-----------------------------------------------------------------------------------
public void setAttributeName (String newValue)
{
  attributeName = newValue;
}
//-----------------------------------------------------------------------------------
public String getClassName ()
{
  return className;
}
//-----------------------------------------------------------------------------------
public String getFileExtension ()
{
  return fileExtension;
}
//-----------------------------------------------------------------------------------
public String getAttributeName ()
{
  return attributeName;
}
//-----------------------------------------------------------------------------------
public String toString ()
{
  StringBuffer sb = new StringBuffer ();
  sb.append (" -- PluginInfo\n");
  sb.append ("      className: " + className + "\n");
  sb.append (" file extension: " + fileExtension + "\n");
  sb.append (" attribute name: " + attributeName + "\n");
  return sb.toString ();
}
//-----------------------------------------------------------------------------------
} // class PluginInfo
