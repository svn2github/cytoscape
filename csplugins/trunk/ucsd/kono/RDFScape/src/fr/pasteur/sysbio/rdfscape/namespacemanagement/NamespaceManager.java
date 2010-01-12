/**
 * Copyright 2006 Andrea Splendiani
 * Released under GPL license
 */

/*
 * Created on Jul 5, 2005
 * Rev 0 very dirty code
 * 
 */
package fr.pasteur.sysbio.rdfscape.namespacemanagement;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.regex.Pattern;

import javax.swing.table.AbstractTableModel;

import com.ibm.icu.util.StringTokenizer;

import fr.pasteur.sysbio.rdfscape.CommonMemory;
import fr.pasteur.sysbio.rdfscape.Contextualizable;
import fr.pasteur.sysbio.rdfscape.DefaultSettings;
import fr.pasteur.sysbio.rdfscape.MemoryViewer;
import fr.pasteur.sysbio.rdfscape.RDFScape;
import fr.pasteur.sysbio.rdfscape.RDFScapeModuleInterface;
import fr.pasteur.sysbio.rdfscape.context.ContextManager;

/**
 * @author andrea@pasteur
 * manages namespaces and their representation. The namespace class has not a proper state since this is mantained in CommonMemory.
 * What this class does i manage user interaction and I/O of namespace settings.
 * Plus it's responsible for the consistency of namespace decaorations (prefixes, colors, selection).
 * The information are also in CommonMemory
 * 
 */
public class NamespaceManager extends AbstractTableModel implements RDFScapeModuleInterface, Contextualizable, MemoryViewer {
	//private RDFScape myRDFScapeInstance=null;
	//Using Common Memory Data Structure
	
	private Pattern nonNamespacePattern=null;
	private NameSpaceManagerPanel myPanel=null;
	private int nsSeed;
	
	/**
	 * @throws Exception 
	 * 
	 */
	public NamespaceManager() throws Exception {
		super();
		System.out.print("\tNamesapaceMAnager... ");
		if(RDFScape.getContextManager()==null) {
			throw new Exception("Cannot build NamespaceManager: Missing Common Memory!");
		}
		if(RDFScape.getCommonMemory()==null) {
			throw new Exception("Cannot build NamespaceManager: Missing Context Manager!");
		}
		RDFScape.getContextManager().addContextualizableElement(this);
		RDFScape.getCommonMemory().addViewerElement(this);
		    
		/**
		 * TODO this may be delegated to CommonMemory
		 */
		nsSeed=0;
		initialize(); 
		System.out.println("Ok");
	}
	
	/**
	 * Set up memory required from NamespaceManager (delegated to commonMemory)
	 */
	public boolean initialize() {
		RDFScape.getCommonMemory().initNamespaces();
		return true;
	}
	
	/**
	 * Issues a re-initialization of information for this namespace, and update views.
	 */
	public void reset() {
		initialize();
		nsSeed=0;
		
	}
	
	/**
	 * @return the namespace panel
	 */
	public NameSpaceManagerPanel getNameSpaceManagerPanel() {
		myPanel= new NameSpaceManagerPanel(this);
		return myPanel;
		
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return 4;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnName(int)
	 */
	public String getColumnName(int arg0) {
		if(arg0==0) return("Namespace");
		if(arg0==1) return("Prefix");
		if(arg0==2) return("Color");
		if(arg0==3) return("View");
		return "";
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return RDFScape.getCommonMemory().getNamespaces().length;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int x, int y) {
		
		if(y==0) {
			return (String) RDFScape.getCommonMemory().getNamespaceNumber(x);
		}
		if(y==1) {
			return RDFScape.getCommonMemory().getPrefixFromNs((String) RDFScape.getCommonMemory().getNamespaceNumber(x));
		}
		if(y==2) {
			return RDFScape.getCommonMemory().getNamespaceColor((String) RDFScape.getCommonMemory().getNamespaceNumber(x));
		}
		if(y==3) {
			return RDFScape.getCommonMemory().getIsActiveFromNs((String) RDFScape.getCommonMemory().getNamespaceNumber(x));
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
	 */
	public void setValueAt(Object myItem, int x, int y) {
		//System.out.println("Changing something");
		if(y==1) {
			System.out.println("ns: "+RDFScape.getCommonMemory().getNamespaceNumber(x));
			System.out.println("prefix: "+myItem);
			RDFScape.getCommonMemory().registerPrefix((String) RDFScape.getCommonMemory().getNamespaceNumber(x),(String)myItem);
		}
		if(y==2) {
			RDFScape.getCommonMemory().registerNameSpaceColor((String) RDFScape.getCommonMemory().getNamespaceNumber(x),(Color)myItem);
			//nameSpaceColor.put((String) nameSpaces.get(arg1),arg0);
			//prefixColor.put(getNameSpacePrefix((String)nameSpaces.get(arg1)),arg0);
		}
		if(y==3) {
			RDFScape.getCommonMemory().setActive((String) RDFScape.getCommonMemory().getNamespaceNumber(x),((Boolean)myItem).booleanValue());
		}
		touch();
	}
	
	public Class getColumnClass(int c) {
		if(c==2) return Color.class;
        if(c==3) return Boolean.class;
		return String.class;
       
    }
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#isCellEditable(int, int)
	 */
	public boolean isCellEditable(int arg0, int arg1) {
		if(arg1==0) return false;
		else return true;
	}
	
	
	public Color[] getPossibleNameSpaceColors() {
		return DefaultSettings.getPossibleNameSpaceColors();
	}
	
	
	
	
	/*
	public boolean isInValidState() {
	/*
		Hashtable myhash=new Hashtable();
		for (int i = 0; i < nameSpaces.size(); i++) {
			String prefix=getNameSpacePrefix((String)nameSpaces.get(i));
			if(myhash.get(prefix)!=null) return false;
			if(prefix.equals("")) return false;
			myhash.put(prefix,prefix);
		}
		
		return true;
	}
	*/
	
	/**
	 * @param nameSpaceToDelete
	 */
	public void removeNameSpace(String nameSpaceToDelete) {
		if(nameSpaceToDelete==null) return;
		RDFScape.getCommonMemory().removeNamespace(nameSpaceToDelete);
		fireTableDataChanged();
	}
	
	
	
	/* (non-Javadoc)
	 * @see fr.pasteur.sysbio.rdfscape.RDFScapeModule#loadFromContext()
	 */
	public boolean loadFromActiveContext() {
		reset();
		//TODO fill here...
		String myFileName=RDFScape.getContextManager().getActiveContext().getNamespacesListFileName();
//		nameSpaceFile=new File(myPath+"/namespaces");
		int counter = 0;
		String line;
		
		try {
		  BufferedReader inFile = new BufferedReader(new FileReader(myFileName));
		  initialize();  //remember it does nothing
		  while((line = inFile.readLine()) != null) {
		    counter++; 
		    System.out.println("Counter "+counter);
		    StringTokenizer st=new StringTokenizer(line);
		    int tokenCounter=0;
		    String currNamespace=new String();
		    String token=null;
		    while(st.hasMoreTokens()) {
		    	token=st.nextToken();
		    	if(tokenCounter==0) {
		    		currNamespace=token;
		    		System.out.println("1-> "+token);
		    		RDFScape.getCommonMemory().registerNameSpace(currNamespace);
		    		System.out.println("2-> "+token);
		    	}
		    	if(tokenCounter==1) {
		    		if(token.equalsIgnoreCase("default")) token="";
		    		RDFScape.getCommonMemory().registerPrefix(currNamespace,token);
		    	}
		    	if(tokenCounter==2) RDFScape.getCommonMemory().registerNameSpaceColor(currNamespace,token);
		    	if(tokenCounter==3) RDFScape.getCommonMemory().setActive(currNamespace,true);
		    	tokenCounter++;
		    	
		    }
		   }
		 inFile.close();
		 touch();
		}
		catch(IOException ioe)
		{
		  System.out.println("Unable to load namespace file");
		  return false;
		}
		return true;
	
		
		
	}
	/* (non-Javadoc)
	 * @see fr.pasteur.sysbio.rdfscape.RDFScapeModule#saveToContext()
	 */
	public boolean saveToContext() {
		String myFileName=RDFScape.getContextManager().getActiveContext().getNamespacesListFileName();
		try {
			FileWriter fw=new FileWriter(new File(myFileName));
			for (Iterator iter = RDFScape.getCommonMemory().getNamespacesList().iterator(); iter.hasNext();) {
				String namespace = (String) iter.next();
				String prefix=RDFScape.getCommonMemory().getPrefixFromNs(namespace);
				String color=DefaultSettings.translateColor2String(RDFScape.getCommonMemory().getNamespaceColor(namespace));
				String active;
				if(RDFScape.getCommonMemory().getActive(namespace)) active="Y";
				else active="N";
				if(prefix.equalsIgnoreCase("")) prefix="DEFAULT";
				fw.write(namespace+" "+prefix+" "+color+" "+active+"\n");
			}
			fw.close();
		} catch (Exception e) {
			System.out.println("Unable to write namespace file");
			return false;
		}
		return true;
	}
	/* (non-Javadoc)
	 * @see fr.pasteur.sysbio.rdfscape.RDFScapeModule#checkPreconditions()
	 */
	public boolean canOperate() {
		/**
		 * There is really some "nonsense" condition, like uninitialized colors...
		 * or two equal namespaces. But this should not happen for the way ns are added.
		 */
		return true;
	}
	/**
	 * Fix namespaces. We need to check that every namespace has a different prefix.
	 * Everything else is garanteed from the initialization. 
	 */
	public void touch() {
		nsSeed=0;
		HashSet myhash=new HashSet();
		String[] nslist=RDFScape.getCommonMemory().getNamespaces();
		for (int i = 0; i < nslist.length; i++) {
			String currentPrefix=RDFScape.getCommonMemory().getNamespacePrefix(nslist[i]);
			if(currentPrefix==null) currentPrefix="";
			if(currentPrefix.equalsIgnoreCase("")) currentPrefix="def";
			if(myhash.contains(currentPrefix)) {
				// this prefix was already in use. Need to change it.
				String tentativePrefix="ns"+nsSeed;
				while (myhash.contains(tentativePrefix)) {
					nsSeed++;
					tentativePrefix="ns"+nsSeed;
				}
				currentPrefix=tentativePrefix;
				
			}
			myhash.add(currentPrefix);
			RDFScape.getCommonMemory().registerPrefixAsync(nslist[i],currentPrefix);
			if(RDFScape.getCommonMemory().getNamespaceColor(nslist[i])==null) RDFScape.getCommonMemory().registerNameSpaceColor(nslist[i],DefaultSettings.defaultColor);
			// Note: for activity of namespace default is in memory!
		}
		
		
		
	}
	
	
	
	/**
	 * @param namespace to be added for the fisrt time to memory (set defaults)
	 */
	public void addNewNameSpaceToMemory(String myNameSpace) {
		RDFScape.getCommonMemory().registerNameSpace(myNameSpace);
		if(RDFScape.getCommonMemory().getPrefixFromNs(myNameSpace)==null) RDFScape.getCommonMemory().registerPrefixAsync(myNameSpace,"");
		if(RDFScape.getCommonMemory().getNamespaceColor(myNameSpace)==null)RDFScape.getCommonMemory().registerNameSpaceColorAsync(myNameSpace,DefaultSettings.defaultColor);
		// Note for for activity of namespaces default is in memory!
		//if(RDFScape.getCommonMemory().getPrefixFromNs(myNameSpace)==null) RDFScape.getCommonMemory().setActiveAsync(myNameSpace,true);
		touch();
		RDFScape.getCommonMemory().updateViews();
		
	}
	
	/**
	 * 
	 */
	public void updateView() {
		fireTableDataChanged();
		
	}
	public void updateNamespaceView(String namespace) {
		fireTableDataChanged();
		
	}
	
}
