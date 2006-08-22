package cytoscape.data;

import cytoscape.data.readers.GraphReader;
import cytoscape.util.CyFileFilter;
import cytoscape.util.SIFFileFilter;
import cytoscape.util.XGMMLFileFilter;
import cytoscape.util.GMLFileFilter;

import java.util.*;
import java.io.*;
import java.lang.String;

public class ImportHandler {
	
	//natures of all standard supported files
	
	public static String GRAPH_NATURE = "NETWORK";
	
	public static String NODE_NATURE = "NODE";
	
	public static String EDGE_NATURE = "EDGE";
	
	public static String PROPERTIES_NATURE = "PROPERTIES";
	
	
    protected static Set cyFileFilters = new HashSet();

    /**
     * return false if fail i.e. if a suffix being added
     *         already exists in the cyFileFilters suffixes
     */
   public ImportHandler() {
    	addFilter(new SIFFileFilter());
    	addFilter(new XGMMLFileFilter());
    	addFilter(new GMLFileFilter());
    }
    
    
    public boolean addFilter(CyFileFilter cff) {
        Set check = cff.getExtensionSet();

        for (Iterator it = check.iterator(); it.hasNext();) {
            if (this.getAllExtensions().contains(it.next())) {
                return false;
            }
        }

        cyFileFilters.add(cff);

        return true;
    }

    public boolean addFilter(CyFileFilter[] cff) {
        //first check if suffixes are unique
        boolean flag = true;

        for (int j = 0; (j < cff.length) && (flag == true); j++) {
            flag = addFilter(cff[j]);
        }

        return flag;
    }

    /**
     * return null if no reader exists for file
     */
    public GraphReader getReader(String fileName) {
        //1.check if fileType is available
        CyFileFilter cff;

        for (Iterator it = cyFileFilters.iterator(); it.hasNext();) {
            cff = (CyFileFilter) it.next();

            if (cff.accept(fileName)) {
                return cff.getReader(fileName);
            }
        }

        return null;
    }

    /**
     * signature: public List getFileTypes()
     * description: returns a List contains all of the supported file types; no extensions
     */
    public Collection getAllTypes(String fileNature) {
        Collection ans = new HashSet();
        CyFileFilter cff;

         for (Iterator it = cyFileFilters.iterator(); it.hasNext();) {
            cff = (CyFileFilter) it.next();

            //if statement to check if nature equals fileNature
            if (cff.getFileNature().equals(fileNature)) {
                cff.setExtensionListInDescription(false);
                ans.add(cff.getDescription());
                cff.setExtensionListInDescription(true);
            }
         }

        return ans;
    }

    /**
     * signature: public List getFileTypeSuffixes()
     * description: returns a List contains all suffixes of all the supported file types; only extensions
     */
    public Collection getAllExtensions() {
        Collection ans = new HashSet();

        for (Iterator it = cyFileFilters.iterator(); it.hasNext();)
            ans.addAll(((CyFileFilter) (it.next())).getExtensionSet());
        

        return ans;
    }

    /**
     * signature: List getFileTypeDescriptions()
     * description: returns a List contains all descriptions file types; with extensions
     */
    public Collection getAllDescriptions() {
        Collection ans = new HashSet();

        for (Iterator it = cyFileFilters.iterator(); it.hasNext();) {
            ans.add(((CyFileFilter) it.next()).getDescription());
        }
       

        return ans;
    }

    public String getFileType(String fileName) {
        CyFileFilter cff;

        String ans = null;

        for (Iterator it = cyFileFilters.iterator(); it.hasNext();) {
            cff = (CyFileFilter) it.next();

            if (cff.accept(fileName)) {
                cff.setExtensionListInDescription(false);
                ans = (cff.getDescription());
                cff.setExtensionListInDescription(true);
            }
        }

        return ans;
    }
    
    
    /**
     * get all filters and add a dummy filter to the end
     * 
     */
    public List getAllFilters() {   	
		List ans = new ArrayList();

         for (Iterator it = cyFileFilters.iterator(); it.hasNext();) {
            ans.add((CyFileFilter) it.next());
         }	
         if (ans.size() > 1) {
        	 String[] allTypes = concatAllExtensions(ans);
        	 ans.add(new CyFileFilter( allTypes, "All Natures"));
         }
         return ans;
    }
    
    
    /**
     * get all filters of a certain nature and add a filter with all extensions
     * 
     */
    public List getAllFilters(String fileNature) {
    	List ans = new ArrayList();
        CyFileFilter cff;

         for (Iterator it = cyFileFilters.iterator(); it.hasNext();) {
            cff = (CyFileFilter) it.next();

            //if statement to check if nature equals fileNature
            if (cff.getFileNature().equals(fileNature)) {
            	ans.add(cff);
            }
         }	
         if (ans.size() > 1) {
        	 String[] allTypes = concatAllExtensions(ans);
        	 ans.add(new CyFileFilter( allTypes, "All " + fileNature.toLowerCase() + " files", fileNature));
         }
        return ans;
    }
    
    
    /**
     *Creates a String array of all extensions
     */   
    private String[] concatAllExtensions(List cffs) {
    	Set ans = new HashSet();
    	
    	for (Iterator it = cffs.iterator(); it.hasNext();)
            ans.addAll(((CyFileFilter) (it.next())).getExtensionSet());
    	
	    String[] stringAns = (String[]) ans.toArray(new String[0]);
	    return stringAns;
    }


}
