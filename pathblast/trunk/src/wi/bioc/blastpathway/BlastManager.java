package wi.bioc.blastpathway;

/**
 * <p>Title: pathblast</p>
 * <p>Description: pathblast</p>
 * <p>Copyright: Copyright (c) 2002 -- 2006 </p>
 * <p>Company: Whitehead Institute</p>
 * <p>Company: University of California, San Diego</p>
 * @author Bingbing Yuan
 * @author Michael Smoot
 * @version 1.2
 */

import java.io.*;
import java.util.*;

/**
 * This class manages all path blast request. <p>
 */

public class BlastManager {
	private static BlastManager m_manager;
	private Hashtable<String,PathBlast> m_pathblasts;

	private BlastManager() {
		m_pathblasts = new Hashtable<String,PathBlast>(11);
	}
	public static BlastManager getInstance() {
		if (m_manager == null) {
			m_manager = new BlastManager();
		}
		return m_manager;
	}

	//public void runBlast(String xmlfile, String uid) 
	public void runBlast(String uid, Protein[] proteins, double e_value, String t_org, boolean useZero, boolean blastAllDip)
		throws Exception 
	{
		String outputdir = getOutputDir(uid);
		if (! new java.io.File(outputdir).mkdir()) {
			throw new Exception("Cannot create directory : " + outputdir);
		}
		//PathBlast blast = new PathBlast(this, xmlfile, getOutputDir(uid), uid);
		PathBlast blast = new PathBlast(this, getOutputDir(uid), uid, proteins, e_value, t_org, useZero,blastAllDip);
		m_pathblasts.put(uid, blast);
		new Thread(blast).start(); // we may want to queue request if too many
	}

	public void stopBlast(String uid) {
		PathBlast blast = m_pathblasts.remove(uid);
		blast.stop();
		blast = null;
	}

	public String getOutputDir(String uid) {
		return Config.TMP_DIR_NAME+"/"+uid+"/";
	}

	public String getOutputUrlBase(String uid) {
		return Config.TMP_URL_BASE+"/"+uid+"/";
	}

	public PathBlast getBlast(String uid) {
		return m_pathblasts.get(uid);
	}
}
