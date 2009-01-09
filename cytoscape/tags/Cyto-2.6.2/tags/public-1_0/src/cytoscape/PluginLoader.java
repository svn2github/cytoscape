// PluginLoader.java

/** Copyright (c) 2002 Institute for Systems Biology and the Whitehead Institute
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 ** 
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute of Systems Biology and the Whitehead Institute 
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute of Systems Biology and the Whitehead Institute 
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute of Systems Biology and the Whitehead Institute 
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/

//
/**
 * a plugin is loaded based upon information in the java.util.Properties 'props',
 * which is a data member of this class. there are three mechanisms, all of which 
 * operate via those properties:
 * <ol>
 *    <li> a data file with a recognized file extension (via properties) is loaded
 *    <li> node or edge attributes include an attribute whose name (via properties)
 *         is known to require a plugin
 *    <li> the plugin is explicitly listed in the properties
 * </ol>
 *
 * Here are some examples from a properties file: <p>
 *
 * <code>
 *
 * </code>
 */
//------------------------------------------------------------------------------
// $Revision$   
// $Date$ 
// $Author$
//-----------------------------------------------------------------------------------
package cytoscape;
//-----------------------------------------------------------------------------------
import java.util.*;
import java.lang.reflect.*;
//-----------------------------------------------------------------------------------
public class PluginLoader {
  protected CytoscapeWindow cytoscapeWindow;
  protected CytoscapeConfig config;
  protected Vector classesToLoad = new Vector ();
  protected Properties props;
  protected GraphObjAttributes nodeAttributes;
  protected GraphObjAttributes edgeAttributes;
  StringBuffer messageBuffer = new StringBuffer ();

//-----------------------------------------------------------------------------------
public PluginLoader (CytoscapeWindow cytoscapeWindow, CytoscapeConfig config,
                     GraphObjAttributes nodeAttributes, GraphObjAttributes edgeAttributes)
{
  this.cytoscapeWindow = cytoscapeWindow;
  this.config = config;
  this.props = config.getProperties ();
  this.nodeAttributes = nodeAttributes;
  this.edgeAttributes = edgeAttributes;

  String [] pluginProps = extractPluginProperties (props);
  for (int i=0; i < pluginProps.length; i++)
    messageBuffer.append ("  " + pluginProps [i] + "\n");

  findUnconditionallyLoadedClasses (pluginProps);
  findConditionallyLoadedClasses (pluginProps);

   messageBuffer.append (this.toString () + "\n");

} // ctor
//-----------------------------------------------------------------------------------
String [] extractPluginProperties (Properties props)
{
  Vector relevantProps = new Vector ();
  Enumeration propNames = props.propertyNames ();
  while (propNames.hasMoreElements ()) {
    String propName = (String) propNames.nextElement ();
    if (propName.startsWith ("plugin"))
      relevantProps.add (propName);
    } // while

  String [] result = new String [relevantProps.size ()];
  for (int i=0; i < relevantProps.size (); i++)
    result [i] = (String) relevantProps.elementAt (i);

  return result;

} // extractPluginProperties
//-----------------------------------------------------------------------------------
protected void findUnconditionallyLoadedClasses (String [] pluginProps)
{
  for (int i=0; i < pluginProps.length; i++) {
    String propName = pluginProps [i];
    if (propName.endsWith (".load")) {
      String className = props.getProperty (propName);
      messageBuffer.append (" PluginLoader, unconditional: " + className + "\n");
      addClassForLoading (className);
      } // if endswith 
    } // for
  
} // findUnconditionallyLoadedClasses
//-----------------------------------------------------------------------------------
protected void findConditionallyLoadedClasses (String [] pluginProps)
{
  HashMap pluginHash = new HashMap ();  //  a hash of (pluginName, PluginInfo) 

  for (int i=0; i < pluginProps.length; i++) {
    String propName = pluginProps [i];
    if (!propName.endsWith (".load")) {
      try {
        int start = "plugin.".length ();
        int end = propName.indexOf (".", start + 1); 
        String pluginName = propName.substring (start, end);
        String category = propName.substring (end + 1);
        messageBuffer.append (" PluginLoader, conditional: " + pluginName + " category: " + category + "\n");
        PluginInfo info = (PluginInfo) pluginHash.get (pluginName);
        if (info == null) {
          info = new PluginInfo ();
          pluginHash.put (pluginName, info);
          }
        String value = props.getProperty (propName);
        if (category.equals ("fileExtension"))
          info.setFileExtension (value);
        else if (category.equals ("attributeName"))
          info.setAttributeName (value);
        else if (category.equals ("className"))
          info.setClassName (value);
        }
      catch (Exception e) {
       System.err.println ("-- PluginLoader error parsing: " + propName);
       }
      } // if
    } // for i


  String [] pluginNameKeys = (String []) pluginHash.keySet().toArray (new String [0]);
  String [] allDataFileExtensions = config.getAllDataFileExtensions ();

    // convert extensions to a vectors, for easier 'contains' comparisons
  Vector extensions = new Vector ();
  for (int i=0; i < allDataFileExtensions.length; i++) {
    extensions.add (allDataFileExtensions [i]);
    }
  
  for (int i=0; i < pluginNameKeys.length; i++) {
    PluginInfo pluginInfo = (PluginInfo) pluginHash.get (pluginNameKeys [i]);
    if (extensions.contains (pluginInfo.getFileExtension ()))
      addClassForLoading (pluginInfo.getClassName ());
    } // for i


  String [] nodeAttributeNames = nodeAttributes.getAttributeNames ();
  String [] edgeAttributeNames = edgeAttributes.getAttributeNames ();

    // convert node & edge attribute names vectors, for easier 'contains' comparisons

  Vector attributes = new Vector ();
  for (int i=0; i < nodeAttributeNames.length; i++)
    attributes.add (nodeAttributeNames [i]);

  for (int i=0; i < edgeAttributeNames.length; i++)
    attributes.add (edgeAttributeNames [i]);
  
  for (int i=0; i < pluginNameKeys.length; i++) {
    PluginInfo pluginInfo = (PluginInfo) pluginHash.get (pluginNameKeys [i]);
    if (attributes.contains (pluginInfo.getFileExtension ()))
      addClassForLoading (pluginInfo.getClassName ());
    } // for i



} // findClassesByFileExtension
//-----------------------------------------------------------------------------------
protected void addClassForLoading (String className)
{
  if (className != null && className.length () > 0 && !classesToLoad.contains (className)) {
    classesToLoad.add (className);
    messageBuffer.append (" PluginLoader, by file extension: " + className + "\n");
    } // if 
 
} // addClassForLoading
//-----------------------------------------------------------------------------------
public String [] getClassesToLoad ()
{
  return (String []) classesToLoad.toArray (new String [0]);
}
//-----------------------------------------------------------------------------------
public void load ()
{
  String [] classNames = getClassesToLoad ();
  List classList = new ArrayList( Arrays.asList(classNames) );
  Collections.sort(classList);
  for (Iterator li = classList.iterator(); li.hasNext(); ) {
      String className = (String)li.next();
      loadPlugin(className, cytoscapeWindow);
  }

} // load
//-----------------------------------------------------------------------------------
protected void loadPlugin (String className, CytoscapeWindow cytoscapeWindow)
{
 try {
    Class pluginClass = Class.forName (className);
    Class [] argClasses = new Class [1];
    argClasses [0] =  cytoscapeWindow.getClass ();
    Object [] args = new Object [1];
    args [0] = cytoscapeWindow;
    Constructor [] ctors = pluginClass.getConstructors ();
    Constructor ctor = pluginClass.getConstructor (argClasses);
    messageBuffer.append ("  now loading plugin:  " + ctor + "\n");
    Object plugin = ctor.newInstance (args);
    }
  catch (Exception e) {
    e.printStackTrace ();
    System.err.println (e.getMessage ());
    }

} // loadPlugin
//------------------------------------------------------------------------------
public String toString ()
{
  StringBuffer sb = new StringBuffer ();

  sb.append ("-- PluginLoader, classes to load:\n   ");
  sb.append (classesToLoad);
  sb.append ("\n");

  return sb.toString ();

} // toString
//------------------------------------------------------------------------------
public String getMessages ()
{
  return messageBuffer.toString ();
}
//------------------------------------------------------------------------------
} // class PluginLoader


