/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/
package cytoscape.util;

import org.jdom.Comment;

/**
 * Utility script to create an up-to-date jnlp file for Cytoscape webstart.
 *
 * It uses the plugin manifest files to figure out the CytoscapePlugin classes
 * or attempts to guess by looking for a class in the jar file with "Plugin" in the name.
 *
 * Meant to use as a standalone script through an ant bulidfile.
 */
import org.jdom.Document;
import org.jdom.Element;

import org.jdom.output.XMLOutputter;

import java.io.File;
import java.io.FileWriter;

import java.util.ArrayList;

//import org.jdom.output.Format; //jdom vs 1.0, currently on 0.9
import java.util.HashMap;
import java.util.jar.JarFile;
import java.util.jar.Manifest;


/**
 *
 */
public class JnlpWriterUtil {
	/**
	 *
	 */
	public Document document;

	/**
	 *
	 */
	public Element rootTag;

	/**
	 *
	 */
	public XMLOutputter out;
	protected String fileName;
	protected String cytoDir;
	protected String url = "http://your.jnlp.location";
	protected String saveDir;

	/**
	 *
	 */
	public String libDir;

	/**
	 *
	 */
	public String pluginDir;
	protected HashMap<String, String> options;

	/**
	 * Creates a new JnlpWriterUtil object.
	 *
	 * @param args  DOCUMENT ME!
	 */
	public JnlpWriterUtil(String[] args) {
		options = getOptions(args);
		fileName = options.get("filename");
		cytoDir = options.get("cyto_dir");
		saveDir = options.get("save_dir");

		if (options.containsKey("url"))
			url = options.get("url");

		libDir = cytoDir + "/lib";
		pluginDir = cytoDir + "/plugins";

		setupDoc();
	}

	private void setupDoc() {
		document = new Document();
		rootTag = new Element("jnlp");
		document.setRootElement(rootTag);

		if (!options.containsKey("url")) {
			Comment Codebase = new Comment("Replace the codebase URL with your own");
			rootTag.addContent(Codebase);
		}

		rootTag.setAttribute("codebase", url);
		rootTag.setAttribute("href", fileName);

		out = new XMLOutputter(org.jdom.output.Format.getPrettyFormat());
	}

	private static void print(String s) {
		System.out.println(s);
	}

	/*
	 * Creates jnlp file,
	 *
	 */

	/**
	 *  DOCUMENT ME!
	 *
	 * @param args DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public static void main(String[] args) throws Exception {
		JnlpWriterUtil jutil = new JnlpWriterUtil(args);

		ArrayList<String> MainLibs = jutil.getLibJars();
		print("Adding " + MainLibs.size() + " total main jars");

		ArrayList<String> PluginLibs = jutil.getPluginJars();
		ArrayList<String> PluginClasses = jutil.getMainClass(PluginLibs, jutil.pluginDir);
		print("Adding " + PluginLibs.size() + " total plugin jars");

		jutil.createInfoTag();
		jutil.createResourcesTag();
		jutil.addJars("lib/", MainLibs, null);
		jutil.addJars("plugins/", PluginLibs,
		              "These are the plugins you wish to load, edit as necessary.");

		jutil.addArguments(PluginClasses);

		jutil.writeToFile();
	}

	/**
	 * @return jdom.Document as a string
	 */
	public String getString() {
		return out.outputString(document);
	}

	/**
	 *
	 * @param Loc - directory to write xml file
	 * @throws java.io.IOException
	 */
	public void writeToFile() throws java.io.IOException {
		File SaveDirCheck = new File(saveDir);

		if (!SaveDirCheck.exists())
			SaveDirCheck.mkdir();

		FileWriter writer = new FileWriter(saveDir + "/" + fileName);
		out.output(document, writer);
	}

	/*
	 * Adding the <information> tag
	 */

	/**
	 *  DOCUMENT ME!
	 */
	public void createInfoTag() {
		Element Info = new Element("information");
		Info.addContent(new Element("title").setText("Cytoscape Webstart"));
		Info.addContent(new Element("vendor").setText("Cytoscape Collaboration"));
		Info.addContent(new Element("homepage").setAttribute("href", "http://cytoscape.org"));
		Info.addContent(new Element("offline-allowed"));

		rootTag.addContent(new Element("security").addContent(new Element("all-permissions")));

		rootTag.addContent(Info);
	}

	/*
	 * Adding the <resource> tag
	 */

	/**
	 *  DOCUMENT ME!
	 */
	public void createResourcesTag() {
		Element Resources = new Element("resources");
		Element JSE = new Element("j2se");
		JSE.setAttribute("version", "1.5+");
		JSE.setAttribute("max-heap-size", "1024M");
		Resources.addContent(JSE);
		Resources.addContent(new Comment("All lib jars that cytoscape requires to run should be in this list"));
		Resources.addContent(new Element("jar").setAttribute("href", "cytoscape.jar"));

		rootTag.addContent(Resources);
	}

	/**
	 *
	 * @param Prefix (append to beginning of jar file name)
	 * @param Jars
	 * @param Comment
	 */
	public void addJars(String Prefix, ArrayList<String> Jars, String Comment) {
		Element Resources = rootTag.getChild("resources");

		if (Comment != null)
			Resources.addContent(new Comment(Comment));

		for (int i = 0; i < Jars.size(); i++) {
			Resources.addContent(new Element("jar").setAttribute("href", Prefix + Jars.get(i)));
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public ArrayList<String> getLibJars() {
		return getJarList(libDir);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public ArrayList<String> getPluginJars() {
		return getJarList(pluginDir);
	}

	/**
	 * @param Dir
	 * @return ArrayList<String> of jars listed in given directory
	 */
	private ArrayList<String> getJarList(String Dir) {
		File JarDir = new File(Dir);

		if (!JarDir.exists()) {
			System.err.println("Required directory '" + JarDir.getAbsolutePath()
			                   + "' does not exist");
			System.exit(-1);
		}

		ArrayList<String> JarFiles = new ArrayList<String>();

		for (File Current : JarDir.listFiles()) {
			if (Current.isFile() && Current.getName().endsWith(".jar")) {
				JarFiles.add(Current.getName());
			}
		}

		return JarFiles;
	}

	/**
	 * @param JarFiles
	 * @param JarDir
	 * @return ArrayList<String> of the CytoscapePlugin classes that could be determined from each plugin
	 */
	public ArrayList<String> getMainClass(ArrayList<String> JarFiles, String JarDir) {
		ArrayList<String> PluginMainClass = new ArrayList<String>();

		for (int i = 0; i < JarFiles.size(); i++) {
			try {
				JarFile jf = new JarFile(JarDir + "/" + JarFiles.get(i));
				Manifest m = jf.getManifest();

				if (m != null) {
					String className = m.getMainAttributes().getValue("Cytoscape-Plugin");

					if (className != null) {
						// add to list
						PluginMainClass.add(className);

						continue;
					}
				}
			} catch (Exception E) {
				E.printStackTrace();
			}
		}

		return PluginMainClass;
	}

	/**
	 * @param args
	 * @return HashMap<String, String> of the command line options
	 */
	private HashMap<String, String> getOptions(String[] args) {
		String Usage = "Usage: java " + this + " [parameters]\n"
		               + "Option    : Description          Required\n"
		               + "-filename : Name of jnlp file    yes\n"
		               + "-cyto_dir : Cytoscape directory  yes\n"
		               + "-url      : Webstart url         no\n"
		               + "-save_dir : Save to dir          yes\n";

		HashMap<String, String> Opts = new HashMap<String, String>();

		if (args.length < 2) {
			System.err.println("Too few arguments (" + args.length + "). " + Usage);
			System.exit(-1);
		}

		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-filename"))
				Opts.put("filename", args[i + 1]);

			if (args[i].equals("-cyto_dir"))
				Opts.put("cyto_dir", args[i + 1]);

			if (args[i].equals("-url"))
				Opts.put("url", args[i + 1]);

			if (args[i].equals("-save_dir"))
				Opts.put("save_dir", args[i + 1]);
		}

		if (!Opts.containsKey("filename") || !Opts.containsKey("cyto_dir")
		    || !Opts.containsKey("save_dir")) {
			System.err.println("Required arguments missing. " + Usage);
			System.exit(-1);
		}

		return Opts;
	}

	/**
	 *
	 * @param Args
	 * These are all plugin arguments at the moment, only specifies the -p tag between each.
	 */
	public void addArguments(ArrayList<String> Args) {
		Element Application = rootTag.getChild("application-desc");

		if (Application == null) {
			rootTag.addContent(new Comment("This starts-up Cytoscape, specify your plugins to load, and other command line arguments.  Plugins not specified here will not be loaded."));

			Application = new Element("application-desc").setAttribute("main-class",
			                                                           "cytoscape.CyMain");
			rootTag.addContent(Application);
		}

		for (int i = 0; i < Args.size(); i++) {
			Application.addContent(new Element("argument").setText("-p"));
			Application.addContent(new Element("argument").setText(Args.get(i)));
		}
	}
}
