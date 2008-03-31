/*
 * Created on Sep 20, 2006
 *
 * Copyright Alok Saldnaha, all rights reserved.
 */
package edu.stanford.genetics.treeview.core;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import javax.swing.JOptionPane;

import edu.stanford.genetics.treeview.*;

public class PluginManager {
	private static String s_pluginclassfile = "tv_plugins.cd";
	/** holds global plugin manager 
	 * I hate static variables, but this one looks necessary.
	 * */
	private static PluginManager pluginManager= new PluginManager();
	public static PluginManager getPluginManager() {
		return pluginManager;
	}

	/**
	 * holds list of all plugin factories
	 */
	private java.util.Vector pluginFactories = new Vector();

	public File[] readdir(String s_dir) {
		File f_dir = new File(s_dir);
		FileFilter fileFilter = new FileFilter() {
			public boolean accept(File file) {
				return file.getName().endsWith("jar");
			}
		};
		return f_dir.listFiles(fileFilter);
	}

	/*
	 * EFFECTS: Reads the file <s_pluginclassfile> from a jar RETURNS: string
	 * array of all classes to be declared NOTE: This method should probably be
	 * moved into a PluginManager class
	 */
	private ArrayList getClassDeclarations(JarFile jf)
			throws NullPointerException, IOException {

		ZipEntry ze = null;
		try {
			/*
			 * See if class declarations file exists
			 */
			Enumeration e = jf.entries();
			JarEntry je = null;
			String classfile = null;
			for (; e.hasMoreElements();) {
				je = (JarEntry) e.nextElement();
				if (je.toString().indexOf(s_pluginclassfile) >= 0) {
					classfile = je.toString();
				}
			}
			ze = jf.getEntry(classfile);
		} catch (NullPointerException e) {
			LogBuffer.println("JarFile has no tv_plugins.cd" +jf.getName());
			throw e;
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*
		 * Classfile exists (otherwise exception thrown) Read classes into array
		 * list
		 */
		BufferedReader br = new BufferedReader(new InputStreamReader(jf
				.getInputStream(ze)));
		ArrayList al = new ArrayList();
		String s = null;
		while ((s = br.readLine()) != null) {
			al.add(s);
		}
		return al;
	}

	public void loadPlugins(File[] f_jars, boolean showPopup) {
		String s_loadedPlugins = "";
		String s_notloadedPlugins = "";

		for (int i = 0; i < f_jars.length; i++) {
			
			try {
				URL jarURL = new URL("jar:"+f_jars[i].toURL().toString()+"!/");

				boolean b_loadedPlugin = loadPlugin(jarURL);
				
				if (b_loadedPlugin) {
					s_loadedPlugins += "<li>" + f_jars[i].getName() + "</li>";
				} else {
					s_loadedPlugins += "<li>" + f_jars[i].getName() + "*</li>";
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
				s_notloadedPlugins += "<li>" + f_jars[i].getName() + "</li>";
				Debug.print(e);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				Debug.print(e);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Debug.print(e);
			} 
		}
		if (showPopup) {
		JOptionPane.showMessageDialog(null,
				"<html> Found jar files: <ol>" + s_loadedPlugins
						+ "<br>* Already loaded</ol> <p>Unable to load: <ol>"
						+ s_notloadedPlugins + "</ol></html>");
		}
	}
	
	  public boolean loadPlugin(URL jarURL) {
		  boolean b_loadedPlugin = false;
		  try {
			  LogBuffer.println("Plugin Jar " + jarURL);
			  JarURLConnection conn = (JarURLConnection)jarURL.openConnection();
			  JarFile jarFile = conn.getJarFile();
			  ArrayList al_classnames = getClassDeclarations(jarFile);
			  for (int j = 0; j < al_classnames.size(); j++) {
				  Class thisClass = getClass();
				  URLClassLoader urlcl = new URLClassLoader(
						  new URL[] { jarURL }, thisClass.getClassLoader());
				  if (!pluginExists(
						  (String) al_classnames.get(j))) {
					  Class c = urlcl
					  .loadClass((String) al_classnames.get(j));
					  /*
					   * XXX: Supposedly, loadClass should call the static
					   * initializer for the class, but I'm finding it doesn't
					   * so I'm forcing an instantiation with new Instance.
					   */
					  PluginFactory pp = (PluginFactory) c.newInstance();
					  b_loadedPlugin |= true;
				  } else {
					  b_loadedPlugin |= false;
				  }
			  }
		  }catch (ClassNotFoundException e) {
			  e.printStackTrace();
			  LogBuffer.println("ClassNotFound " + e);
		  } catch (InstantiationException e) {
			  // TODO Auto-generated catch block
			  Debug.print(e);
		  } catch (IllegalAccessException e) {
			  // TODO Auto-generated catch block
			  Debug.print(e);
		  } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  return b_loadedPlugin;
	}

	/**
	   * 
	   * remember, static methods cannot be overridden.
	   * @param pf
	   */
	public static void registerPlugin(PluginFactory pf) {
		LogBuffer.println("Registering Plugin " + pf.getPluginName());
		getPluginManager().pluginFactories.add(pf);
	}

	/**
	 * Assigns corresponding nodes from the global config to the appropriate
	 * plugin factories.
	 * 
	 * needs to be called whenever a plugin is loaded or 
	 * the global confignode changes.
	 *
	 */
	public void pluginAssignConfigNodes(ConfigNode node) {
		PluginFactory [] plugins =  getPluginFactories();
		for (int i = 0; i < plugins.length; i++) {
			ConfigNode pluginNode = null;
			ConfigNode [] pluginPresets = node.fetch("PluginGlobal");
			for (int j = 0; j < pluginPresets.length; j++) {
				// scan existing pluginPresets for correct name
				if (pluginPresets[j].getAttribute("name", "").equals(
						plugins[i].getPluginName())) {
					pluginNode = pluginPresets[j];
					break;
				}
			}
			if (pluginNode == null) {
				// no existing presets node for plugin, must create
				pluginNode = node.create("PluginGlobal");
				pluginNode.setAttribute("name", plugins[i].getPluginName(),"");
			}
			plugins[i].setGlobalNode(pluginNode);
		}
	}
	
	public PluginFactory [] getPluginFactories() {
		PluginFactory [] ret = new PluginFactory [pluginFactories.size()];
		Enumeration e = pluginFactories.elements();
		int i = 0;
		while (e.hasMoreElements())
			ret[i++] = (PluginFactory) e.nextElement();
		return ret;
	}
	public String [] getPluginNames() {
		String [] names = new String[pluginFactories.size()];
		for (int i =0; i < names.length; i++) {
			names[i] = ((PluginFactory) pluginFactories.elementAt(i)).getPluginName();
			LogBuffer.println("Pluginclassname ---- " + names[i]);
		}
		return names;
	}
	/**
	 * 
	 * @param i
	 * @return ith plugin, or null if no such plugin.
	 */
	public PluginFactory getPluginFactory(int i) {
		if ((i < pluginFactories.size()) &&
			(i >= 0)) {
			return (PluginFactory) pluginFactories.elementAt(i);
		} else {
			return null;
		}
	}
	public boolean pluginExists(String qualified_name) {
		Enumeration e = pluginFactories.elements();
		String s = null;
		while (e.hasMoreElements()) {
			s = ((PluginFactory) e.nextElement()).toString();
			/*
			 * The toString function on an (Object) always adds some funny strings
			 * to the end of the string, so we have to use "contains".
			 */
			if (s.indexOf(qualified_name) != -1) {
				return true;
			}
		}
		return false;
	}
}
