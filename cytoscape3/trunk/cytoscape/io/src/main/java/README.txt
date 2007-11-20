IO MODULE

This module should contain 6 basic packages:

org.cytoscape.io.network.reader
org.cytoscape.io.network.writer

org.cytoscape.io.attribute.reader
org.cytoscape.io.attribute.writer

org.cytoscape.io.event
org.cytoscape.io.exception

Event and exception could be further broken down into the network/attribute packages if necessary.  Basic subpackages to io may include db (database connection interfaces),
webservice (basic webservice interfaces) and ontology (for generic ontology interfaces).  Specific implementations such as GO or OBO should be discussed and may be
best handled by plugins.

NOTE: Any classes in the org.cytoscape.io.util package are unclear as to where they belong and should not stay in that package.  They may not even
belong in IO or need to be refactored in order to be part of the IO package.

