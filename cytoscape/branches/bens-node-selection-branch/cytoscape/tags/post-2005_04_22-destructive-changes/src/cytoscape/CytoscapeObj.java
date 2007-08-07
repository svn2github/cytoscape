package cytoscape;


import java.io.File;

public class CytoscapeObj {

    public CytoscapeObj() {
    }

   
    public CytoscapeConfig getConfiguration() {
        return new CytoscapeConfig();
    }

    /**
     * Get the view threshold.
     *
     * @return The view threshold
     * @deprecated As of Cytoscape 2.1, replaced by {@link CytoscapeInit#getViewThreshold()};
     */
    public int getViewThreshold() {
        return CytoscapeInit.getViewThreshold();
    }

    /**
     * Set the view threshold.
     *
     * @deprecated As of Cytoscape 2.1, replaced by {@link CytoscapeInit#setViewThreshold(int)};
     */
    public void setViewThreshold(int threshold) {
        CytoscapeInit.setViewThreshold(threshold);
    }

    /**
     * Get the current directory.
     *
     * @return The startup directory if a user has not chosen another via a load or save operation,
     *         else returns the last user chosen directory
     * @deprecated As of Cytoscape 2.1, replaced by {@link CytoscapeInit#getMRUD()};
     */
    public File getCurrentDirectory() {
        return CytoscapeInit.getMRUD();
    }

    /**
     * Set the current directory
     *
     * @deprecated As of Cytoscape 2.1, replaced by {@link CytoscapeInit#getMRUD()};
     */
    public void setCurrentDirectory(File mrud) {
        CytoscapeInit.setMRUD(mrud);
    }
}

