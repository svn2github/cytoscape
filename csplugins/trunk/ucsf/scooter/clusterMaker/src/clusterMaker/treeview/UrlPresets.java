/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: alokito $
 * $RCSfile: UrlPresets.java,v $
 * $Revision: 1.5 $
 * $Date: 2004/12/21 03:28:14 $
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


/**
 * This class encapsulates a list of URL presets.
 * This is the class to edit the default presets in...
 */

public class UrlPresets {
    private ConfigNode root;
	private final static int dIndex = 0; // which preset to use if not by confignode?
    /**
     * creates a new UrlPresets object and binds it to the node
	 * 
	 * adds default Gene Presets if  none are currently set.
     */
    public UrlPresets (ConfigNode parent) {
	  super();
	  bindConfig(parent);
	  if (getPresetNames().length == 0) {
	    addDefaultGenePresets();
	  }
	  
    }
    public UrlPresets () {
	  super();
	}

	/**
	* returns default preset, for use when opening a new file which has no url settings
	*/
	public int getDefaultPreset() {
	  return root.getAttribute("default", dIndex);
	}

	public boolean isDefaultEnabled() {
	  return (getDefaultPreset() != -1);
	}

	public String getDefaultTemplate() {
	  int defaultPreset = getDefaultPreset();
	  if(defaultPreset == -1)
	  {
	  	return null;
	  }
	  try {
		return getTemplate(defaultPreset);
	  } catch (Exception e) {
		return getTemplate(0);
	  }
	}
	
	public void setDefaultPreset(int i) {
	  root.setAttribute("default", i, dIndex);
	}
	
    public void addDefaultGenePresets() {
	addPreset("SGD",
		  "http://genome-www4.stanford.edu/cgi-bin/SGD/locus.pl?locus=HEADER");
	addPreset("YPD",
		  "http://www.proteome.com/databases/YPD/reports/HEADER.html");
	addPreset("WormBase",
		  "http://www.wormbase.org/db/searches/basic?class=AnyGene&query=HEADER&Search=Search");
	addPreset("Source_CloneID",		  "http://genome-www4.stanford.edu/cgi-bin/SMD/source/sourceResult?option=CloneID&choice=Gene&criteria=HEADER");
	addPreset("FlyBase", "http://flybase.bio.indiana.edu/.bin/fbgenq.html?HEADER");
	addPreset("MouseGD",
	"http://www.informatics.jax.org/javawi/servlet/SearchTool?query=HEADER&selectedQuery=Genes+and+Markers");
	addPreset("GenomeNetEcoli",
		  "http://www.genome.ad.jp/dbget-bin/www_bget?eco:HEADER");
    }

    /** 
     * returns String [] of preset names for display
     */
    public String[] getPresetNames()
    {
        ConfigNode aconfigNode[] = root.fetch("Preset");
        String astring[] = new String[aconfigNode.length];
	for (int i = 0; i < aconfigNode.length; i++)
            astring[i] = aconfigNode[i].getAttribute("name", "");
        return astring;
    }
    
    public boolean[] getPresetEnablings()
    {
        ConfigNode aconfigNode[] = root.fetch("Preset");
        boolean aboolean[] = new boolean[aconfigNode.length];
        String temp;
		for (int i = 0; i < aconfigNode.length; i++)
		{
	            temp = aconfigNode[i].getAttribute("enabled", "false");
	            if(temp.toLowerCase().equals("true"))
	            {
	            	aboolean[i] = true;
	            }
	            else
	            {
	            	aboolean[i] = false;
	            }
		}
        return aboolean;
    }
    
    public String[] getPresetHeaders()
    {
        ConfigNode aconfigNode[] = root.fetch("Preset");
        String astring[] = new String[aconfigNode.length];
	for (int i = 0; i < aconfigNode.length; i++)
            astring[i] = aconfigNode[i].getAttribute("header", "");
        return astring;
    }

    
    
    /**
     * returns the template for the ith preset
     * or null, if i too large.
     */
    public String getTemplate(int index) {
        ConfigNode aconfigNode[] = root.fetch("Preset");
	if (index < aconfigNode.length) 
	    return aconfigNode[index].getAttribute("template", null);
	else
	    return null;
    }

    /**
     * returns the template for this name
     * or null, if name not found in kids
     */
    public String getTemplate(String name) {
        ConfigNode aconfigNode[] = root.fetch("Preset");
        for (int i = 0; i < aconfigNode.length; i++)
	    if (name.equals(aconfigNode[i].getAttribute("name", null))) 
		return aconfigNode[i].getAttribute("template", null);
	return null;
    }
    
    public String getTemplateByHeader(String header) {
        ConfigNode aconfigNode[] = root.fetch("Preset");
        boolean [] enablings = getPresetEnablings();
        for (int i = 0; i < aconfigNode.length; i++)
	    if (enablings[i] && matchPattern(header, aconfigNode[i].getAttribute("header", null))) // may cause compatibility issues with old .jtv files
	    	return aconfigNode[i].getAttribute("template", null);
	    return getDefaultTemplate();
    }
    
    private boolean matchPattern(String string, String pattern)
    {
    	for(int i = 0, j = 0; i < pattern.length(); i++, j++)
    	{
    		if(pattern.charAt(i) == '*')
    		{
    			if(i == pattern.length() - 1)
    			{
    				return true;
    			}
    			else if(j == string.length() - 1)
    			{
    				return false;
    			}
    			else if(pattern.charAt(i + 1) == '*')
    			{
    				j--;
    				continue;
    			}
    			else if(pattern.charAt(i + 1) == string.charAt(j + 1))
    			{
    				continue;
    			}
    			else
    			{
    				i--;
    				continue;
    			}
    		}
    		else if(pattern.charAt(i) != string.charAt(j))
    		{
    			return false;
    		}
    	}
    	return true;
    }

    public void setPresetName(int index, String name) {
        ConfigNode aconfigNode[] = root.fetch("Preset");
		try {
		  aconfigNode[index].setAttribute("name", name, null);
		} catch (java.lang.ArrayIndexOutOfBoundsException e) {
		  System.out.println("UrlPresets.setPresetName() got error: " + e);
		}
	}
    public void setPresetHeader(int index, String header) {
        ConfigNode aconfigNode[] = root.fetch("Preset");
		try {
		  aconfigNode[index].setAttribute("header", header, null);
		} catch (java.lang.ArrayIndexOutOfBoundsException e) {
		  System.out.println("UrlPresets.setPresetHeader() got error: " + e);
		}
	}
    public void setPresetEnabled(int index, boolean enabled) {
        ConfigNode aconfigNode[] = root.fetch("Preset");
		try {
		  if(enabled)
		  {
		  	aconfigNode[index].setAttribute("enabled", "true", null);
		  }
		  else
		  {
		  	aconfigNode[index].setAttribute("enabled", "false", null);
		  }
		} catch (java.lang.ArrayIndexOutOfBoundsException e) {
		  System.out.println("UrlPresets.setPresetEnabled() got error: " + e);
		}
	}
	public void setPresetTemplate(int index, String template) {
        ConfigNode aconfigNode[] = root.fetch("Preset");
		aconfigNode[index].setAttribute("template", template, null);
	}
	
	public void addPreset(String name, String template) {
		  ConfigNode preset = root.create("Preset");
		  preset.setAttribute("name", name, null);
		  preset.setAttribute("template", template, null);
		  preset.setAttribute("header", "*", null);
		  preset.setAttribute("enabled", "false", null);
	}
	public void addPreset(String name, String template, String header, boolean enabled) {
		  ConfigNode preset = root.create("Preset");
		  preset.setAttribute("name", name, null);
		  preset.setAttribute("template", template, null);
		  preset.setAttribute("header", header, null);
		  if(enabled)
		  {
		  	preset.setAttribute("enabled", "true", null);
		  }
		  else
		  {
		  	preset.setAttribute("enabled", "false", null);
		  }
	}

    public void bindConfig(ConfigNode configNode)
    {
        root = configNode;
    }

    private ConfigNode createSubNode()
    {
        return root.create("Preset");
    }
    
}
