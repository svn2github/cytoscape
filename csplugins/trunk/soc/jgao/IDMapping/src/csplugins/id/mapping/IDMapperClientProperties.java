/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package csplugins.id.mapping;

import cytoscape.CytoscapeInit;

import cytoscape.layout.Tunable;
import cytoscape.layout.TunableListener;

import cytoscape.util.ModulePropertiesImpl;

import java.util.Enumeration;
import java.util.Properties;

/**
 *
 * @author gjj
 */
public class IDMapperClientProperties extends ModulePropertiesImpl 
        //implements TunableListener
{
    /**
     * Constructor.
     *
     * @param propertyPrefix String representing the prefix to be used
     *                       when pulling properties from the property
     *                       list.
     */
    public IDMapperClientProperties(String propertyPrefix) {
        super(propertyPrefix, FinalStaticValues.CLIENT_SESSION_PROPS);
    }

    /**
     * saveProperties is used to add modified properties to the Cytoscape properties
     * so they can be saved in the properties file.
     *
     */
    public void saveProperties(Tunable tunable) {
        if (!getTunables().contains(tunable)) return;

        String prefix = getPrefix();
        Properties props = CytoscapeInit.getProperties();
        props.setProperty(prefix + tunable.getName(), tunable.getValue().toString());
    }

    public void release() {
        String prefix = getPrefix();
        Properties props = CytoscapeInit.getProperties();

        // Find all properties with this prefix
        Enumeration iter = props.propertyNames();

        while (iter.hasMoreElements()) {
            String property = (String) iter.nextElement();

            if (property.startsWith(prefix)) {
                props.remove(property);
            }
        }
    }

//    public void tunableChanged(Tunable tunable) {
//        setProperty(tunable.getName(), tunable.getValue().toString());
//        //saveProperties();
//    }

}