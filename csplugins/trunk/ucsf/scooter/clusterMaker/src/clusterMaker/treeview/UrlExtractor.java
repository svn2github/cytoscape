/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: alokito $
 * $RCSfile: UrlExtractor.java,v $
 * $Revision: 1.6 $
 * $Date: 2005/12/05 05:27:53 $
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
import cytoscape.logger.CyLogger;

import java.awt.Frame;
import java.io.UnsupportedEncodingException;
/**
 * This class extracts Urls from HeaderInfo.
 * Also included is a class to pop up a configuration window.
 */
 public class UrlExtractor {
   /**
   * This class must be constructed around gene header info
   */
 	public UrlExtractor(HeaderInfo hI) {
		 headerInfo = hI;
		 urlTemplate = dUrlTemplate;
		 index  = dindex;
		 isEnabled = isDefaultEnabled;
		 uPresets = null;
	}
 	
 	public UrlExtractor(HeaderInfo hI, UrlPresets uPresets) {
		 headerInfo = hI;
		 urlTemplate = dUrlTemplate;
		 index  = dindex;
		 isEnabled = isDefaultEnabled;
		 this.uPresets = uPresets;
	}
   /**
   * can be bound to config node to provide persistence
   */
   public void bindConfig(ConfigNode n) {
	 root = n;
	 // extract state...
	 urlTemplate = root.getAttribute("urlTemplate" , dUrlTemplate);
	 index  = root.getAttribute("index"  , dindex);
	 // some shennanigans since I can't store booleans in a confignode...
	 int ide = 0;
	 if (isDefaultEnabled == true) ide = 1;
	 isEnabled = (root.getAttribute("isEnabled", ide) == 1);
   }
   
   /**
   * most common use, returns a String rep of a url given an index 
   * returns null if not enabled, or if the header for this gene is null.
   */
   public String getUrl(int i) {
	 if (isEnabled() == false) return null;
	 String [] headers = headerInfo.getHeader(i);
	 if (headers == null)
	   return null;
	 return substitute(urlTemplate, headers[index]);
	 
  }
   
   public String getUrl(int i, String header) {
   	 if(uPresets == null)
   	 {
   	 	return null;
   	 }
	 if (isEnabled() == false) return null;
	 String [] headers = headerInfo.getHeader(i);
	 if (headers == null)
	   return null;
	 return substitute(uPresets.getTemplateByHeader(header), headers[index]);
  }
   
   public String substitute(String val) {
	 return substitute(urlTemplate, val);
   }
   private String substitute(String temp, String val) {
	 if (val == null) return null;
	 int into = temp.indexOf("HEADER");
	 if (into < 0) return temp;
	 	try {
			return temp.substring(0, into) + java.net.URLEncoder.encode(val, "UTF-8") + temp.substring(into+6);
		} catch (UnsupportedEncodingException e) {
			CyLogger.getLogger(UrlExtractor.class).warn("unsupported encoding? this shouldn't happen. " + e);
			e.printStackTrace();
			return temp;
		}
   }
   
   /**
   * pops up a configuration dialog.
   */
   public void showConfig(Frame f) {
	 // deprecated...
   }
   //accessors
   public void setIndex(int i) {
	 index = i;
	 if (root != null)
	   root.setAttribute("index", index, dindex);
   }
   public int getIndex() {
	 return index;
   }
   
   public void setUrlTemplate(String c) {
	 urlTemplate = c;
	 if (root != null)
	   root.setAttribute("urlTemplate", urlTemplate, dUrlTemplate);
   }
   public String getUrlTemplate() {
	 return urlTemplate;
   }
   
   public void setDefaultTemplate(String temp) {
	 dUrlTemplate = temp;
   }
   public void setDefaultIndex(int i) {
	 dindex = i;
   }
   
   public void setDefaultEnabled(boolean b) {
	 isDefaultEnabled = b;
   }

   public void setEnabled(boolean b) {
	 isEnabled = b;
	 // some shennanigans since I can't store booleans in a confignode...
	 int ide = 0;
	 if (isDefaultEnabled == true) ide = 1;

	 int ie = 0;
	 if (isEnabled == true) ie = 1;
	 
	 if (root != null)
	   root.setAttribute("isEnabled", ie, ide);

   }
   public boolean isEnabled() {
	 return isEnabled;
   }
   // does the user actually want linking to happen?
   private boolean isEnabled;
   private boolean isDefaultEnabled = true;
   
   // durlTemplate is the actual text of the url to be substituted
   private String urlTemplate;
   private String dUrlTemplate = "http://www.google.com/search?q=HEADER";

   // the index is the header column of the cdt/pcl which is used for substitution
   private int index;
   private int dindex  = 1;
   
   private HeaderInfo headerInfo;
   /** Setter for headerInfo */
   public void setHeaderInfo(HeaderInfo headerInfo) {
	   this.headerInfo = headerInfo;
   }
   /** Getter for headerInfo */
   public HeaderInfo getHeaderInfo() {
	   return headerInfo;
   }
   private ConfigNode root;
   UrlPresets uPresets;
}
