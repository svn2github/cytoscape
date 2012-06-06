package org.cytoscape.cpathsquared.internal;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.biopax.paxtools.model.Model;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.cpathsquared.internal.util.BioPaxUtil;
import org.cytoscape.cpathsquared.internal.util.EmptySetException;
import org.cytoscape.cpathsquared.internal.view.BinarySifVisualStyleFactory;
import org.cytoscape.io.read.CyNetworkReaderManager;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.session.CyNetworkNaming;
import org.cytoscape.util.swing.OpenBrowser;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.undo.UndoSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cpath.client.CPath2Client;
import cpath.client.util.CPathException;
import cpath.service.OutputFormat;
import cpath.service.jaxb.SearchResponse;
import cpath.service.jaxb.TraverseResponse;

// TODO: This is a "God" object.  Probably shouldn't exist, but it's better than having to
//       propagate all of the injected dependencies throughout all the implementation classes.
//       Lesser of two evils.
public final class CPath2Factory {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CPath2Factory.class);
	
	private static CySwingApplication application;
	private static TaskManager taskManager;
	private static OpenBrowser openBrowser;
	private static CyNetworkManager networkManager;
	private static CyApplicationManager applicationManager;
	private static CyNetworkViewManager networkViewManager;
	private static CyNetworkReaderManager networkViewReaderManager;
	private static CyNetworkNaming naming;
	private static CyNetworkFactory networkFactory;
	private static CyLayoutAlgorithmManager layoutManager;
	private static UndoSupport undoSupport;
	private static BinarySifVisualStyleFactory binarySifVisualStyleUtil;
	private static VisualMappingManager mappingManager;
	
	public static final String JVM_PROPERTY_CPATH2_URL = "cPath2Url";
	public static final String DEFAULT_CPATH2_URL = "http://www.pathwaycommons.org/pc2/";
	
    public static String cPathUrl = System.getProperty(JVM_PROPERTY_CPATH2_URL, DEFAULT_CPATH2_URL);
    
    public static String serverName = "Pathway Commons (BioPAX L3)";
    
    public static String blurb = 
    		"<span class='bold'>Pathway Commons</span> is a convenient point of access " +
            "to biological pathway " +
            "information collected from public pathway databases, which you can " +
            "browse or search. <BR><BR>Pathways include biochemical reactions, complex " +
            "assembly, transport and catalysis events, and physical interactions " +
            "involving proteins, DNA, RNA, small molecules and complexes. Now using BioPAX Level3!";
    
    public static String iconToolTip  = "Import Pathway Data from Pathway Commons (cPathSquared web services, BioPAX L3)";
    
    public static String iconFileName = "pc.png";
    
    public static OutputFormat downloadMode = OutputFormat.BINARY_SIF;
    
    public static enum SearchFor {
    	PATHWAY,
    	INTERACTION,
    	PHYSICALENTITY;
    }

    public static SearchFor searchFor = SearchFor.INTERACTION;    
    
	// non-instantiable static factory class
	private CPath2Factory() {
		throw new AssertionError();
	}
	
	public static void init(CySwingApplication app, TaskManager tm, OpenBrowser ob, 
			CyNetworkManager nm, CyApplicationManager am, CyNetworkViewManager nvm, 
			CyNetworkReaderManager nvrm, CyNetworkNaming nn, CyNetworkFactory nf, 
			CyLayoutAlgorithmManager lam, UndoSupport us, 
			BinarySifVisualStyleFactory bsvsf, VisualMappingManager mm) 
	{
		application = app;
		taskManager = tm;
		openBrowser = ob;
		networkManager = nm;
		applicationManager = am;
		networkViewManager = nvm;
		networkViewReaderManager = nvrm;
		naming = nn;
		layoutManager = lam;
		networkFactory = nf;
		undoSupport = us;
		binarySifVisualStyleUtil = bsvsf;
		mappingManager = mm;
	}
	
	/**
	 * Creates a new universal task factory
	 * (can contain one or more different tasks)
	 * 
	 * @return
	 */
	public static TaskFactory newTaskFactory(final Task... tasks) {
		return new TaskFactory() {
			@Override
			public TaskIterator createTaskIterator() {
				return new TaskIterator(tasks);
			}

			@Override
			public boolean isReady() {
				return true; //TODO really? ;)
			}
		};
	}

	public static OpenBrowser getOpenBrowser() {
		return openBrowser;
	}

	public static CySwingApplication getCySwingApplication() {
		return application;
	}

	public static TaskManager getTaskManager() {
		return taskManager;
	}

	public static CyNetworkManager getNetworkManager() {
		return networkManager;
	}

	public static CyApplicationManager getCyApplicationManager() {
		return applicationManager;
	}

	public static CyNetworkViewManager getCyNetworkViewManager() {
		return networkViewManager;
	}

	public static CyNetworkReaderManager getCyNetworkViewReaderManager() {
		return networkViewReaderManager;
	}

	public static CyNetworkNaming getCyNetworkNaming() {
		return naming;
	}

	public static CyNetworkFactory getCyNetworkFactory() {
		return networkFactory;
	}

	public static UndoSupport getUndoSupport() {
		return undoSupport;
	}

	public static CyNetworkManager getCyNetworkManager() {
		return networkManager;
	}

	public static CyLayoutAlgorithmManager getCyLayoutAlgorithmManager() {
		return layoutManager;
	}
	
	public static BinarySifVisualStyleFactory getBinarySifVisualStyleUtil() {
		return binarySifVisualStyleUtil;
	}

	public static CyApplicationManager getApplicationManager() {
		return applicationManager;
	}

	public static CyNetworkViewManager getNetworkViewManager() {
		return networkViewManager;
	}

	public static CyNetworkReaderManager getNetworkViewReaderManager() {
		return networkViewReaderManager;
	}

	public static CyNetworkNaming getNaming() {
		return naming;
	}

	public static CyNetworkFactory getNetworkFactory() {
		return networkFactory;
	}

	public static CyLayoutAlgorithmManager getLayoutManager() {
		return layoutManager;
	}

	public static VisualMappingManager getMappingManager() {
		return mappingManager;
	}
    
    public static CPath2Client newClient() {
        CPath2Client client = CPath2Client.newInstance();
        client.setEndPointURL(cPathUrl);
		return client;
	}

	/**
     * Gets One or more records by Primary ID.
     * @param ids               Array of URIs.
     * @param format            Output format. TODO
     * @return data string.
     * @throws EmptySetException    Empty Set Error.
     */
    public static String getRecordsByIds(String[] ids, OutputFormat format) 
    {
    	//TODO client to return other formats as well
    	CPath2Client cli = newClient();
    	Model res = cli.get(Arrays.asList(ids));  
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BioPaxUtil.getBiopaxIO().convertToOWL(res, baos);
        
        return baos.toString();
    }


    public static Map<String, String> getAvailableOrganisms() {
        return newClient().getValidOrganisms();
    }

    
    public static Map<String, String> getLoadedDataSources() {
        return newClient().getValidDataSources();
    }


	public static SearchResponse topPathways(String keyword, Set<String> organism,
			Set<String> datasource) {
		return newClient().getTopPathways();
	}
	
	
    public static TraverseResponse traverse(String path, Collection<String> uris) 
    {
    	if(LOGGER.isDebugEnabled())
    		LOGGER.debug("traverse: path=" + path);
    	
    	// TODO Notify all listeners of start

        CPath2Client client = newClient();
        client.setPath(path);
        
        TraverseResponse res = null;
		try {
			res = client.traverse(uris);;
		} catch (CPathException e) {
			LOGGER.error("getting " + path + 
				" failed; uri:" + uris.toString(), e);
		}
      	
		// TODO Notify all listeners of end
			
       	return res;
    }
}
