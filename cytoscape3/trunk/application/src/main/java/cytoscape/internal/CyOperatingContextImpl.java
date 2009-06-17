
/*
 File: CyOpertatingContextImpl.java

 Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */

package cytoscape.internal;

import cytoscape.CyOperatingContext;

import java.util.Properties;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.cytoscape.property.CyProperty;

/**
 * Basic access to Cytoscape's operating context. 
 */
public class CyOperatingContextImpl implements CyOperatingContext {

	private Properties props;

	public CyOperatingContextImpl(CyProperty<Properties> props) {
		if ( props == null )
			throw new NullPointerException("Cytoscape Properties is null");

		this.props = props.getProperties();

		loadLocalProps();
	}

	private void loadLocalProps() {
		try {
            File vmp = getConfigFile(PROPS);

            if (vmp != null)
                props.load(new FileInputStream(vmp));
            else
                System.out.println("couldn't read " + PROPS + " from " + CONFIG_DIR);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	/**
	 * Returns cytoscape.props.
	 */
	// TODO Should we be returning a copy here to keep thing synchronized or
	// do we want just one properties object?
	public Properties getProperties() {
		return props;
	}

	/**
	 * Returns a {@link File} pointing to the config directory.
	 */
	public File getConfigDirectory() {
        try {
            String dirName = props.getProperty("alternative.config.dir", System.getProperty("user.home"));
            File parent_dir = new File(dirName, CONFIG_DIR);

            if (parent_dir.mkdir())
                System.err.println("Parent_Dir: " + parent_dir + " created.");

            return parent_dir;
        } catch (Exception e) {
            System.err.println("error getting config directory");
        }

        return null;
	}

	/**
	 * Returns the specified file if it's found in the config directory. 
	 */
	public File getConfigFile(String file_name) {
		try {
			File parent_dir = getConfigDirectory();
			File file = new File(parent_dir, file_name);

			if (file.createNewFile())
				System.err.println("Config file: " + file + " created.");

			return file;
		} catch (Exception e) {
			System.err.println("error getting config file:" + file_name);
		}

		return null;
	}
}

