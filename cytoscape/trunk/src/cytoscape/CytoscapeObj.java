package cytoscape;

import cytoscape.data.servers.BioDataServer;

import java.io.File;

public class CytoscapeObj {

    public CytoscapeObj() {
    }

    /**
     * Returns the (possibly null) bioDataServer.
     *
     * @see BioDataServer
     */
    public BioDataServer getBioDataServer() {
        return Cytoscape.getBioDataServer();
    }

    public CytoscapeConfig getConfiguration() {
        return new CytoscapeConfig();
    }


    public int getViewThreshold() {
        return 500;
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

