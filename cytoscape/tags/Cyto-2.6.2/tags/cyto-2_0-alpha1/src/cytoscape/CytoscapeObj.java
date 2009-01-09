/** Copyright (c) 2002 Institute for Systems Biology and the Whitehead Institute
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute for Systems Biology and the Whitehead Institute
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute for Systems Biology and the Whitehead Institute
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute for Systems Biology and the Whitehead Institute
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape;
//-------------------------------------------------------------------------
import java.util.logging.Logger;
import java.io.File;

import cytoscape.data.servers.BioDataServer;
import cytoscape.visual.CalculatorCatalog;
import cytoscape.visual.CalculatorCatalogFactory;
import cytoscape.visual.CalculatorIO;
import cytoscape.plugin.JarLoaderCommandLineParser;
import cytoscape.plugin.PluginRegistry;
import cytoscape.plugin.PluginLoader;
import cytoscape.plugin.AbstractPlugin;
//-------------------------------------------------------------------------
/**
 * An object representing a single instance of Cytoscape. This class holds
 * references to globally unique objects like the CytoscapeConfig and the
 * bioDataServer.
 */
public class CytoscapeObj {

    protected CyMain parentApp;
    protected CytoscapeConfig config;
    protected Logger logger;
    protected BioDataServer bioDataServer;
    protected PluginRegistry pluginRegistry;
    protected static CalculatorCatalog calculatorCatalog;
    protected File currentDirectory;
    //List networks = new ArrayList();
    //List cyWindows = new ArrayList();

/**
 * Constructor taking just a CytoscapeConfig object. The constructor
 * will attempt to use the specifications in the config object to
 * construct the other global objects.
 *
 * @throws NullPointerException  if the argument is null
 */
public CytoscapeObj(CytoscapeConfig config) {
    this.parentApp = null;
    this.config = config;
    this.logger = Logger.getLogger("global");
    this.bioDataServer = null;
    this.pluginRegistry = new PluginRegistry();
    if (config.getBioDataDirectory() != null) {
        try {
            this.bioDataServer = new BioDataServer(config.getBioDataDirectory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    registerCommandLinePlugins();
    //eventually should wait until a window requests the catalog
    loadCalculatorCatalog();
    this.currentDirectory = new File(System.getProperty("user.dir"));
}
//------------------------------------------------------------------------------
/**
 * Constructor that assumes that someone else has constructed most of
 * the global objects, which are passed in as arguments.
 *
 * The CytoscapeConfig argument should not be null, as many operations
 * expect a fully functional CytoscapeConfig object to exist. The other
 * arguments may be null; a new Logger will be constructed if that
 * argument is null.
 */
public CytoscapeObj(CyMain parentApp, CytoscapeConfig config,
                    Logger logger, BioDataServer bioDataServer) {
    this.parentApp = parentApp;
    this.config = config;
    if (logger == null) {
        this.logger = Logger.getLogger("global");
    } else {
        this.logger = logger;
    }
    this.bioDataServer = bioDataServer;
    this.pluginRegistry = new PluginRegistry();
    registerCommandLinePlugins();
    //eventually should wait until a window requests the catalog
    loadCalculatorCatalog();
    this.currentDirectory = new File(System.getProperty("user.dir"));
}
//------------------------------------------------------------------------------
/**
 * Loads plugins by via the plugin loading helper classes.
 *
 * @see AbstractPlugin
 * @see PluginLoader
 */
public void registerCommandLinePlugins() {
    JarLoaderCommandLineParser parser = new JarLoaderCommandLineParser(this);
    parser.parseArgs(config.getArgs());
    logger.info(parser.getMessages());
    PluginLoader pluginLoader = new PluginLoader(this);
    pluginLoader.load(config.getProperties());
    logger.info(pluginLoader.getMessages());

}
//------------------------------------------------------------------------------
/**
 * If this CytoscapeObj object was constructed from cytoscape.java,
 * then this method returns a reference to that parent object;
 * else returns null.
 */
public CyMain getParentApp() {return parentApp;}
//------------------------------------------------------------------------------
/**
 * Returns the global config object. Almost always non-null.
 */
public CytoscapeConfig getConfiguration() {return config;}
//------------------------------------------------------------------------------
/**
 * Returns the global logging object; guaranteed to be non-null.
 */
public Logger getLogger() {return logger;}
//------------------------------------------------------------------------------
/**
 * Returns the (possibly null) bioDataServer.
 *
 * @see BioDataServer
 */
public BioDataServer getBioDataServer() {return bioDataServer;}
//------------------------------------------------------------------------------
/**
 * Sets the global bioDataServer. null values are allowed.
 */
public void setBioDataServer(BioDataServer newServer) {
    this.bioDataServer = newServer;
}
//------------------------------------------------------------------------------
/**
 * Returns the PluginRegistry.
 *
 * @see PluginRegistry
 */
public PluginRegistry getPluginRegistry() {return pluginRegistry;}
//------------------------------------------------------------------------------
/**
 * Attempts to load a CalculatorCatalog object, using the information
 * from the CytoscapeConfig object.
 *
 * Does nothing if a catalog has already been loaded.
 *
 * @see CalculatorCatalog
 * @see CalculatorCatalogFactory
 */
public void loadCalculatorCatalog() {
    if (calculatorCatalog == null) {
        calculatorCatalog =
            CalculatorCatalogFactory.loadCalculatorCatalog(getConfiguration());
    }
}
//------------------------------------------------------------------------------
/**
 * Saves the CalculatorCatalog to the file 'vizmap.props' in the user's
 * home directory.
 *
 * @see CalculatorCatalog
 * @see CalculatorIO
 */
public void saveCalculatorCatalog() {
  File userHomePropsFile = new File(System.getProperty ("user.home"), "vizmap.props");
  CalculatorIO.storeCatalog(calculatorCatalog, userHomePropsFile);
}
//------------------------------------------------------------------------------
/**
 * Returns the global catalog of visual mapping calculators.
 *
 * @see CalculatorCatalog
 */
public CalculatorCatalog getCalculatorCatalog() {
    return calculatorCatalog;
}
//------------------------------------------------------------------------------
/**
 * Returns the directory set by the last call to setCurrentDirectory;
 * initialized to the value of System.getProperty("user.dir").
 *
 * @see #setCurrentDirectory
 */
public File getCurrentDirectory() {return currentDirectory;}
//------------------------------------------------------------------------------
/**
 * Sets the current directory. File browsing operations often set this
 * so that later browsing will start from the last opened location.
 */
public void setCurrentDirectory(File newDirectory) {
    this.currentDirectory = newDirectory;
}
//------------------------------------------------------------------------------
}

