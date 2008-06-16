/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: alokito $
 * $RCSfile: XmlConfig.java,v $
 * $Revision: 1.15 $
 * $Date: 2006/09/25 22:56:44 $
 * $Name:  $
 *
 * This file is part of Java TreeView
 * Copyright (C) 2001-2003 Alok Saldanha, All Rights Reserved. Modified by Alex Segal 2004/08/13. Modifications Copyright (C) Lawrence Berkeley Lab.
 *
 * This software is provided under the GNU GPL Version 2. In particular, 
 *
 * 1) If you modify a source file, make a comment in it containing your name and the date.
 * 2) If you distribute a modified version, you must do it under the GPL 2.
 * 3) Developers are encouraged but not required to notify the Java TreeView maintainers at alok@genome.stanford.edu when they make a useful addition. It would be nice if significant contributions could be merged into the main distribution.
 *
 * A full copy of the license can be found in gpl.txt or online at
 * http://www.gnu.org/licenses/gpl.txt
 *
 * END_HEADER 
 */
package clusterMaker.treeview;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.*;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JOptionPane;

import cytoscape.logger.CyLogger;

import net.n3.nanoxml.*;

/**
 * This is a generic class for maintaining a configuration registry
 * for documents. The root element is managed by this class, and
 * configuration should be stored in children of the root.
 *
 * The class is actually implemented as wrapper around XMLElement that
 * is associated with a file, and knows how to store itself.
 */
public class XmlConfig {
	private boolean rawXML = false;

	/**
	 * Construct new configuration information source
	 * 
	 * @param xmlFile xml file associated with configuration info
	 */
	 public XmlConfig(String xmlFile, String tag) {
		 file = xmlFile;
		 XMLElement el = null;
		 try {
			 IXMLParser parser = XMLParserFactory.createDefaultXMLParser();
			 // IXMLReader reader = StdXMLReader.fileReader(file); // fails on pc
			 BufferedReader breader = new BufferedReader(new FileReader(file));
			 IXMLReader reader = new StdXMLReader(breader);
			 parser.setReader(reader);
			 el = (XMLElement) parser.parse();
		 } catch (XMLException ex) {
			 try {
				 int response = JOptionPane.showConfirmDialog (null, "Problem Parsing Settings " + xmlFile + ", Should I replace?", "Replace Faulty Settings File?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
				 if (response == JOptionPane.OK_OPTION) {
					 el = makeNewConfig(tag);
				 } else {
					 file = null;
					 el = new XMLElement(tag);
				 }
			 } catch (Exception e) {
				 System.out.println("Problem opening window: " + e.toString());
				 System.out.println("Error parsing XML code in configuration file");
				 System.out.println(file);
				 System.out.println("Manually deleting file may fix, but you'll lose settings.");
				 System.out.println("error was: " + ex);
				 file = null;
				 el = new XMLElement(tag);
			 }
		 } catch (java.io.FileNotFoundException e) {
		 	 try {
				 // We might have gotten raw XML
				 IXMLParser parser = XMLParserFactory.createDefaultXMLParser();
				 Reader sreader = new StringReader(xmlFile);
				 IXMLReader reader = new StdXMLReader(sreader);
				 parser.setReader(reader);
				 el = (XMLElement) parser.parse();
				 rawXML = true;
			 } catch (XMLException ex) {
				 el = null;
			 } catch (Exception ex) {
			 	 System.out.println(ex);
		 	 }
		 
		   if (el == null) {
			   	el = new XMLElement(tag);
		   }
		 } catch (Exception ex) {
			 System.out.println(ex);
		 }
		 root = new XmlConfigNode(el);
	 }
	 private XMLElement makeNewConfig(String tag) {
			 System.out.println("Making new configuration file " + file);
			 changed = true;
			 return  new XMLElement(tag);
	 }
	 
    /**
     * Construct new configuration information source
     * 
     * @param xmlUrl url from which the text came from, since nanoxml sucks so damn hard.
     */
	 public XmlConfig(java.net.URL xmlUrl, String tag) {
		 url = xmlUrl;
		 XMLElement el = null;
		 if (url != null) {
			 try {
					String xmlText     = "";
					Reader st          = new InputStreamReader(url.openStream());
					int ch             = st.read();
					while (ch != -1) {
						char[] cbuf  = new char[1];
						cbuf[0] = (char) ch;
						xmlText = xmlText + new String(cbuf);
						ch = st.read();
					}
			 	  IXMLParser parser = XMLParserFactory.createDefaultXMLParser();
		 		  Reader sreader = new StringReader(xmlText);
		 		  IXMLReader reader = new StdXMLReader(sreader);
		 		  parser.setReader(reader);
		 		  el = (XMLElement) parser.parse();
			 } catch (XMLException ex) {
				 CyLogger.getLogger(XmlConfig.class).error("Error parsing XML code in configuration url");
				 CyLogger.getLogger(XmlConfig.class).error(url.toString());
				 ex.printStackTrace();
				 url = null;
				 el = new XMLElement(ex.toString());
			 } catch (java.security.AccessControlException sec) {
				 sec.printStackTrace();
				 throw sec;
			 } catch (Exception ex) {
				 //	    el = new XMLElement(ex.toString());
				 CyLogger.getLogger(XmlConfig.class).error(ex.toString());
				 ex.printStackTrace();
			 }

			 if (el == null) {
			   el = new XMLElement(tag);
			 }
		 	 root = new XmlConfigNode(el);
		 }
		}
		 
    /**
     * returns node if it exists, otherwise makes a new one.
     */
    public ConfigNode getNode(String name) {
	ConfigNode t =root.fetchFirst(name);
	// just return if exists
	if (t != null) return t;
	System.out.println("Doesn't exist, creating node: "+name);
	//otherwise, create and return
	return root.create(name);
    }

    /**
     * returns node if it exists, otherwise makes a new one.
     */
    public ConfigNode getRoot() {
    		return root;
    }

    /**
     * Store current configuration data structure in XML file 
     *
     */
	 public void store() {
		 if (changed == false || rawXML == true) return;
		 if (file == null) {
			 CyLogger.getLogger(XmlConfig.class).error("Not printing config to file");
			 return;
		 }
		 try {
			 OutputStream os = new FileOutputStream(file);
			 XMLWriter w = new XMLWriter(os);
			 CyLogger.getLogger(XmlConfig.class).info("Storing config file " + file);
			 w.write(root.root);
			 changed = false;
		 } catch (Exception e) {
			 CyLogger.getLogger(XmlConfig.class).error("Caught exception " + e);
		 }
	 }
    
    /**
     * Unit test, tries to load arg[0] as an xml file
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
	XmlConfig c = new XmlConfig(args[0], "TestConfig");	
	System.out.println(c);
	c.store();
    }
    
    public String toString() {
	return "XmlConfig object based on file " + file + "\n" 
	    + " url " + url + "\n" + root;
    }

    // inner class, used to implement ConfigNode
    private class XmlConfigNode implements ConfigNode{
	IXMLElement root;
	public XmlConfigNode(IXMLElement e) {
	    root = e;
	}
	public void store() {
		XmlConfig.this.store();
	}
	public ConfigNode create(String name) {
	  if (root == null) return new DummyConfigNode(name);
	    XMLElement kid = new XMLElement(name);
	    root.addChild(kid);
	    XmlConfig.this.changed = true;
	    return new XmlConfigNode(kid);
 	}
	public ConfigNode[] fetch(String name) {
	    Vector kids = root.getChildrenNamed(name);
	    ConfigNode [] ret = new XmlConfigNode[kids.size()];
	    for (int i = 0; i < kids.size(); i++) {
		ret[i] = new XmlConfigNode((XMLElement) kids.elementAt(i));
	    }
	    return ret;
	}
	
	public ConfigNode fetchFirst(String string) {
	  	if (root == null) return null;
	    IXMLElement kid = root.getFirstChildNamed(string);
	    if (kid == null) return null;
	    return new XmlConfigNode(kid);
	}
	public ConfigNode fetchOrCreate(String string) {
		ConfigNode t = fetchFirst(string);
		// just return if exists
		if (t != null) return t;
		//otherwise, create and return
		return create(string);
	}

	public boolean equals(Object cn) {
	    return (((XmlConfigNode)cn).root == root);
	}
    
	public void remove(ConfigNode configNode) {
	    root.removeChild(((XmlConfigNode)configNode).root);
	    XmlConfig.this.changed = true;
	}

	public void removeAll(String string) {
	    ConfigNode [] ret = fetch(string);
	    for (int i = 0; i < ret.length; i++) {
		remove(ret[i]);
	    }
	}

	public void setLast(ConfigNode configNode) {
	    remove(configNode);
	    root.addChild(((XmlConfigNode) configNode).root);
	    XmlConfig.this.changed = true;
	}
	
	/**
	 * determine if a particular attribute is defined for this node.
	 */
	 public boolean hasAttribute(String string) {
	   return root.hasAttribute(string);
	 }

	public double getAttribute(String string, double d) {
	    Double val = Double.valueOf(root.getAttribute(string, Double.toString(d)));
	    return val.doubleValue();
	}
	public int getAttribute(String string, int i) {
	    return root.getAttribute(string, i);
	}
	public String getAttribute(String string, String dval) {
	    return root.getAttribute(string, dval);
	}

	public void setAttribute(String att, double val, double dval) {
	    double cur = getAttribute(att, dval);
	    if (cur != val) {
		XmlConfig.this.changed = true;
		root.setAttribute(att, Double.toString(val));
	    }
	}

	public void setAttribute(String att, int val, int dval) {
	    int cur = getAttribute(att, dval);
	    if (cur != val) {
		XmlConfig.this.changed = true;
		root.setAttribute(att, Integer.toString(val));
	    }
	}
	public void setAttribute(String att, String val, String dval) {
	    String cur = getAttribute(att, dval);
	    if ((cur == null) || (!cur.equals(val))) {
		XmlConfig.this.changed = true;
		root.setAttribute(att, val);
	    }
	}
	public String toString() {
	    String ret = "Root:" + root.getFullName() + "\n";
	    for (Enumeration e = root.enumerateChildren(); 
		 e.hasMoreElements(); ) {
		ret += " " + ((XMLElement) e.nextElement()).getFullName() + "\n";
	    }
	    return ret;
	}
    }
    
    private String file = null;
	private java.net.URL url = null;
    private XmlConfigNode root = null;
    private boolean changed = false;
    
    /**
     * This is a non-object-oriented general purpose static method 
     * to create a window listener that will call ConfigNode.store()
     * when the window it is listening to is closed. There is nothing
     * particular to the XmlConfigNode class about it, but I can't think
     * of a better place to put it. 
     * 
     * Wherenever a settings panel which affects the config is closed, we want those changes to be saved.
	 *
	 * returns a WindowListener which will store theconfig every time a window it listens on is closed.
	 * 
     * @param node node to store
     * @return window listener to attach to windows
     */
    public static WindowListener getStoreOnWindowClose(final ConfigNode node) {
		 // don't share, or you might end up listening to stale old windows...
		 // do window listeners keep a pointer to the things they listen to, or is it the other way around?
		 // it seems like it's probably the other way around.
		 // in which case, it's bad for observable things to stay around for longer than their observers.
		 // anyways, the overhead of making a new one is pretty small.
		 return new WindowListener() {
				 public void windowActivated(WindowEvent e) {
					 // nothing...
				 }
				 public void windowClosed(WindowEvent e) {
					 node.store();
				 }
				 public void windowClosing(WindowEvent e) {
					 // nothing...
				 }
				 public void windowDeactivated(WindowEvent e) {
					 // nothing...
				 }
				 public void windowDeiconified(WindowEvent e) {
					 // nothing...
				 }
				 public void windowIconified(WindowEvent e) {
					 // nothing...
				 }
				 public void windowOpened(WindowEvent e) {
					 // nothing...
				 }
		 };
    }
}
