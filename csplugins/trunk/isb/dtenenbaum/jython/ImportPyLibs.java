package csplugins.isb.dtenenbaum.jython;

import java.io.*; 
import java.net.URL;
import java.net.URLDecoder;
import java.util.zip.ZipFile;

import org.python.core.*;

/**
 * Modified by Dan Tenenbaum
 * @author Kent Johnson
 */
public class ImportPyLibs {

    /**
     * This is the startup class for MyApp when run from Java Web Start.
     * It duplicates some of the functionality of org.python.util.jython.
     * The difference is that we look for the jar containing the .py files
     * on the classpath instead of getting the path from the command line.
     * That way we can work with Java Web Start where we have no control
     * over the name or location of our jar files.
     */


    // This is a list of modules whose jars should be added to sys.path
    // For every jar containing .py files, pick one module in the jar and
    // add it to this list. It doesn't matter what module you use, it is
    // just a marker to allow us to find the jar

    private static final String[] MODULES = {
        "__run__.py",    // The Jython startup module is in myjython.jar
        "string.py",     // Nothing special about string.py, any module in jython-lib.jar would do
    };


    public static String[] importLibs() {
    	String[] out = new String[MODULES.length];
        // Put the cachedir in user.home, otherwise it goes in the JWS application dir
//        String cachePath = System.getProperty("user.home") + "/jython/cachedir";
//        System.setProperty("python.cachedir", cachePath);

        // Setup the basic python system state
        PySystemState.initialize();

        // Look up each module in MODULES and add its containing jar file to sys.path
        for (int i = 0; i < MODULES.length; i++) {
            String moduleName = MODULES[i];
            String jarPath = findJarContaining(moduleName);
            if (jarPath == null) {
                System.out.println("Unable to find jar file containing " + moduleName + " - exiting");
                return null;
            }

            // The jar file names don't necessarily end with '.jar'; sometimes there is a
            // number appended after that. So adding the jar file path to sys.path won't
            // work. Adding a SyspathArchive forces the runtime to recognize the file as a jar
            // But the SyspathArchive itself wants a .jar so we use SyspathArchiveHack.
            try {
                SyspathArchive archive = new SyspathArchiveHack(new ZipFile(jarPath), jarPath);
                out[i] = archive.toString();
                //Py.getSystemState().path.append(archive);
            } catch (IOException e) {
                System.out.println("Unable to create SyspathArchive for " + moduleName + " - exiting");
                e.printStackTrace();
                //return;
            }
        }
        return out;

        // Load and run the startup module
        //runResource("__run__.py");

        //Py.getSystemState().callExitFunc();
    }


    /**
     * Find the jar file containing a .py file and return its path.
     * This has to work when running from Java Web Start. So use the
     * class loader to find a URL to the jar file and munge that
     * to get the path.
     */
    private static String findJarContaining(String item) {
        String header = "jar:file:";
        String trailer = "!/" + item;

        URL url = ImportPyLibs.class.getResource("/" + item);
        // We should get something like
        // "jar:file:/C:/Documents and Settings/kejohnson/.javaws/cache/http/Dlocalhost/P80/DMdemo/DMlib/RMmyjython.jar!/__run__.py"
        if (url == null) {
            System.out.println("Main: getResource failed for " + item);
            return null;
        }

        String path = url.toString();
        System.out.println(path);

        if (!path.startsWith(header) || !path.endsWith(trailer)) {
            System.out.println("Main: Can't interpret URL for " + item + ": " + path);
            return null;
        }

        // Strip header and trailer
        path = path.substring(header.length(), path.length() - trailer.length());
        while (path.startsWith("/"))
            path = path.substring(1);

        path = URLDecoder.decode(path);
		//path = URLDecoder.decode(path,"UTF-8");
		if (path.charAt(1) != ':') {
			path = "/" + path;
		}
        System.out.println("Path for " + item + ": " + path);
        return path;
    }

	// TODO - use this
    /** Load a module from a resource and run it as __main__. */
    protected static void runResource(String name) {
        try {
            PyStringMap locals = new PyStringMap();
            locals.__setitem__("__name__", new PyString("__main__"));

            InputStream file = ImportPyLibs.class.getResourceAsStream("/" + name);
            PyCode code;
            try {
                code = Py.compile(file, name, "exec");
            } finally {
                file.close();
            }

            Py.runCode(code, locals, locals);
        }
        catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

	/** Return the code from a python module as a string--ONLY use this from
	 * plugins to return the bootstrap code (__run__.py). All other code should
	 * be accessed by importing. */
	protected static String getResourceCode(String name) {
		try {
			String out = "";
			StringBuffer sb = new StringBuffer();
			
			
			InputStream file = ImportPyLibs.class.getResourceAsStream("/" + name);
			BufferedReader br = new BufferedReader(new InputStreamReader(file));

			String line = "";
			while((line = br.readLine()) != null) {
				out += "\n" + line;
			}			
			//System.out.println("out = \n"+out);
			return out;
		}
		catch (java.io.IOException e) {
			e.printStackTrace();
			return null;
		}
	}


}
