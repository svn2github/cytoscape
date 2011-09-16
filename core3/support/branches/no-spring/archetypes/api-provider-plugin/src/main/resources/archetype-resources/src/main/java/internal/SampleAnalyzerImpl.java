#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.internal;


import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

import ${package}.SampleAnalyzer;

import java.util.Collection;

/**
 * This is an implementation of the SampleAnalyzer interface. 
 * This will be registered as a service in the Spring Dynamic
 * Modules XML configuration files that can be found in
 * the directory src/main/resources/META-INF/spring.
 * <br/>
 * The reason that this class is in a separate "internal" package
 * is so that consumers of this bundle cannot access this code
 * directly. The osgi.bnd file in the top level directory of this
 * project defines this package as "private."  That means that 
 * while any of the packages in this bundle can access this package,
 * OSGi forbids any OTHER bundles for accessing this package.
 * OSGi only allows access to this class as a registered service
 * of the SampleAnalyzer interface. This approach ensures that 
 * consumers of the SampleAnalyzer API will only  ever use the API 
 * interface and never the actual implementation. This gives YOU 
 * the flexibility to change this implemenation without breaking 
 * anyone else's code!!!
 */
public class SampleAnalyzerImpl implements SampleAnalyzer {

	/**
	 * Basic implemenation of the SampleAnalyzer API.
	 *
	 * @param n The network to be analyzed.
	 * @return A collection of analyzed nodes. 
	 */
	public Collection<CyNode> analyzeNodes(CyNetwork n) {
		// Check preconditions for your method. 
		if ( n == null )
			throw new NullPointerException("network is null!");

		// Our "analysis" involves returning the first half of
		// the nodes found in the network - hopefully you will
		// do something more substantial!
		return n.getNodeList().subList( 0, n.getNodeCount()/2 );
	}
}
