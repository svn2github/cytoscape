// JarClassLoader
//--------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------
package cytoscape.jarLoader;
//--------------------------------------------------------------------------
import cytoscape.*;
import java.net.*;
import java.lang.reflect.*;
import java.util.jar.*;
import java.util.*;
import javax.swing.*;

/**
 * A class loader for loading jar files, both local and remote.
 * This class is derived from an online example:
 * http://java.sun.com/docs/books/tutorial/jar/api/jarclassloader.html
 */
public class JarClassLoader extends URLClassLoader {
    private String urlString;
    private URL url;
    private CytoscapeWindow cytoscapeWindow;
    /**
     * Creates a new JarClassLoader for the specified url.
     *
     * @param urlString the url of the jar file
     * @param cytoscapeWindow the parent
     * {@link CytoscapeWindow CytoscapeWindow}
     */
    public JarClassLoader(String urlString,
			  CytoscapeWindow cytoscapeWindow)
	throws MalformedURLException {
	super(new URL[] { new URL(urlString) });
	this.urlString = urlString;
	this.cytoscapeWindow = cytoscapeWindow;
	this.url = new URL("jar", "", urlString + "!/");
    }
    
    /**
     * browses the jar file for any classes that extend AbstractPlugin;
     * upon finding such classes, constructs them with their single
     * constructor argument, {@link #cytoscapeWindow}.
     */
    public void loadRelevantClasses() {
	JarURLConnection uc=null;
	try {
	    uc = (JarURLConnection)url.openConnection();
	    if(uc==null) throw(new Exception("null URL Connection"));
	    
	    JarFile jf = uc.getJarFile();
	    if(jf==null) throw(new Exception("null JarFile from URL"));
	    
	    //System.out.println("- - - - entries begin");
	    Enumeration entries = jf.entries();
	    if(entries==null) throw(new Exception("null jar entries"));
	    //System.out.println("entries is not null");
	    
	    int totalEntries=0;
	    int totalClasses=0;
	    int totalPlugins=0;
	    
	    while(entries.hasMoreElements()) {
		totalEntries++;
		Object entry_o = entries.nextElement();
		String entry_s = entry_o.toString();
		//System.out.println(entry_s);
		if(!(entry_s.endsWith(".class"))) continue;
		totalClasses++;
		//System.out.println(" CLASS: " + entry_s);
		String entry_s2 = entry_s.replaceAll("\\.class$","");
		//System.out.println(" CLASS: " + entry_s2);
		if(!(isClassPlugin(entry_s2))) {
		    //System.out.println(" not plugin.");
		    continue;
		}
		//System.out.println(" PLUGIN!");
		totalPlugins++;
		invokePlugin(entry_s2);
	    }
	    //System.out.println("- - - - entries finis");
	    System.out.println(".jar summary: " +
			       " entries=" + totalEntries +
			       " classes=" + totalClasses + 
			       " plugins=" + totalPlugins); 
	}
	catch (Exception e) {
	    System.err.println ("Error thrown: " + e.getMessage ());
	    if(uc==null) System.out.println("uc is null 4e");
	}
    }
    
    /**
     * Invokes the application in this jar file given the name of the
     * main class and an array of arguments. The class must define a
     * static method "main" which takes an array of String arguemtns
     * and is of return type "void".
     *
     * @param name the name of the main class
     * @param args the arguments for the application
     * @exception ClassNotFoundException if the specified class could not
     *            be found
     * @exception NoSuchMethodException if the specified class does not
     *            contain a "main" method
     * @exception InvocationTargetException if the application raised an
     *            exception
     */
    public void invokeClass(String name, String[] args)
	throws ClassNotFoundException,
	       NoSuchMethodException,
	       InvocationTargetException
    {
	Class c = loadClass(name);
	Method m = c.getMethod("main", new Class[] { args.getClass() });
	m.setAccessible(true);
	int mods = m.getModifiers();
	if (m.getReturnType() != void.class || !Modifier.isStatic(mods) ||
	    !Modifier.isPublic(mods)) {
	    throw new NoSuchMethodException("main");
	}
	try {
	    m.invoke(null, new Object[] { args });
	} catch (IllegalAccessException e) {
	    // This should not happen, as we have disabled access checks
	}
    }
    
    /**
     * Invokes the application in this jar file given the name of the
     * main class and assuming it is a plugin.
     *
     * @param name the name of the plugin class
     */
    public void invokePlugin(String name)
    {
	try {
	    if(!(isClassPlugin(name)))
		throw(new Exception("not plugin: " + name));
	    Class pluginClass = loadClass (name);
	    Class [] argClasses = new Class [1];
	    argClasses [0] =  cytoscapeWindow.getClass ();
	    Object [] args = new Object [1];
	    args [0] = cytoscapeWindow;
	    Constructor [] ctors = pluginClass.getConstructors ();
	    Constructor ctor = pluginClass.getConstructor (argClasses);
	    Object plugin = ctor.newInstance (args);
	    System.out.println("Loaded plugin: " + name);

	    /**
	     * if the "No plugins loaded" message is currently
	     * in the operations menu, and it is no longer valid
	     * because there are other items in the operations menu,
	     * then remove it.
	     */
	    JMenu ops = cytoscapeWindow.getOperationsMenu();
	    if(ops.getItemCount()>0) {
		String s = ops.getItem(0).getText();
		if (s.equals(CytoscapeWindow.NO_PLUGINS)) {
		    ops.remove(0);
		    //System.out.println("REMOVED");
		}
	    }
	}
	catch (Exception e) {
	    System.err.println ("Error instantiating plugin: "
				+ e.getMessage ());
	}
    }
    
    
    /**
     * Determines whether the class with a particular name
     * extends AbstractPlugin.
     *
     * @param name the name of the putative plugin class
     */
    public boolean isClassPlugin(String name)
	throws ClassNotFoundException,
	       NoSuchMethodException,
	       InvocationTargetException
    {
	Class c = loadClass(name);
	Class p = AbstractPlugin.class;
	if (p.isAssignableFrom(c)) { return true; }
	else { return false; }
    }
	
    /**
     * Returns the class you request, or throws an exception.
     */
    public Class getClass(String name)
	throws ClassNotFoundException
    { return findClass(name); }
	
} // JarClassLoader

	
