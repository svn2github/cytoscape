// JarLoaderAction: prompts user for which Jar to load a plugin from.
//--------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------
package cytoscape.jarLoader;
//--------------------------------------------------------------------------
import java.awt.event.*;
import javax.swing.*;
import cytoscape.*;
import java.net.*;
import java.lang.reflect.*;
import java.util.jar.*;
import java.io.*;

public class JarLoaderAction extends AbstractAction {
    protected CytoscapeWindow cytoscapeWindow;
    protected File file;
    JarLoaderAction(CytoscapeWindow cytoscapeWindow) {
	super ("Jar Loader");
	this.cytoscapeWindow = cytoscapeWindow;
    }
    public void actionPerformed (ActionEvent e) {
	if(!getFile()) return;
	String jarString = file.getPath();
	System.out.println("Chose: " + jarString);
	//String jarString ="http://web.mit.edu/ooze/www/wi/shrink.jar";
	try {
	    JarClassLoader jcl = new JarClassLoader("file:" + jarString);
	    try {
		String mainClassString = jcl.getMainClassName();
		System.out.println("The main class is... " + mainClassString);
		try {
		    Class pluginClass = jcl.getClass (mainClassString);
		    Class [] argClasses = new Class [1];
		    argClasses [0] =  cytoscapeWindow.getClass ();
		    Object [] args = new Object [1];
		    args [0] = cytoscapeWindow;
		    Constructor [] ctors = pluginClass.getConstructors ();
		    Constructor ctor = pluginClass.getConstructor (argClasses);
		    Object plugin = ctor.newInstance (args);
		}
		catch (Exception e3) {
		    System.err.println ("Error 3: " + e3.getMessage ());
		}
	    }
	    catch (Exception e2) {
		System.err.println ("Error 2: " + e2.getMessage ());
	    }
	}
	catch (Exception e1) {
	    System.err.println ("Error 1: " + e1.getMessage ());
	}
    }
    

    /**
     * A class loader for loading jar files, both local and remote.
     * This class is derived from an online example:
     * http://java.sun.com/docs/books/tutorial/jar/api/jarclassloader.html
     */
    private class JarClassLoader extends URLClassLoader {
	private String urlString;
	
	/**
	 * Creates a new JarClassLoader for the specified url.
	 *
	 * @param urlString the url of the jar file
	 */
	public JarClassLoader(String urlString) throws MalformedURLException {
	    super(new URL[] { new URL(urlString) });
	    this.urlString = urlString;
	}
	
	/**
	 * Returns the name of the jar file main class, or null if
	 * no "Main-Class" manifest attributes was defined.
	 */
	public String getMainClassName() throws MalformedURLException {
	    URL u = new URL("jar", "", urlString + "!/");
	    try {
		JarURLConnection uc = (JarURLConnection)u.openConnection();
		Attributes attr = uc.getMainAttributes();
		if(attr!=null)
		    return attr.getValue(Attributes.Name.MAIN_CLASS);
	    }
	    catch (Exception e) {}
	    return null;
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
	 * Returns the class you request, or throws an exception.
	 */
	public Class getClass(String name)
	    throws ClassNotFoundException
	{ return findClass(name); }
	
    }


    private boolean getFile() {
        JFileChooser fChooser =
	    new JFileChooser(cytoscapeWindow.getCurrentDirectory());
        fChooser.setDialogTitle("Load Plugin from Jar");
        switch (fChooser.showOpenDialog(null)) {
                
        case JFileChooser.APPROVE_OPTION:
            file = fChooser.getSelectedFile();
            String s;

            try {
                FileReader fin = new FileReader(file);
                BufferedReader bin = new BufferedReader(fin);
                fin.close();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.toString(),
					      "Error Reading \"" +
					      file.getName()+"\"",
					      JOptionPane.ERROR_MESSAGE);
                return false;
            }
	    
            return true;

        default:
            // cancel or error
            return false;
        }
    }
    



} // JarLoaderAction

	
