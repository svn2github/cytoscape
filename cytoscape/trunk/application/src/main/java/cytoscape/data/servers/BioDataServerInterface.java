/*
 File: BioDataServerInterface.java

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

// BioDataServerInterface.java:  define the interface

//----------------------------------------------------------------------------------------
// $Revision$   
// $Date$ 
// $Author$
//----------------------------------------------------------------------------------------
package cytoscape.data.servers;

import cytoscape.data.annotation.Annotation;
import cytoscape.data.annotation.AnnotationDescription;

import cytoscape.data.synonyms.Thesaurus;

//----------------------------------------------------------------------------------------
import java.rmi.Remote;


//----------------------------------------------------------------------------------------
/**
 *
  */
public interface BioDataServerInterface extends Remote {
	// annotations (typically of genes, but possibly of any entity whatsoever
	/**
	 *  DOCUMENT ME!
	 *
	 * @param annotation DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void addAnnotation(Annotation annotation) throws Exception;

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public int getAnnotationCount() throws Exception;

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public AnnotationDescription[] getAnnotationDescriptions() throws Exception;

	/**
	 *  DOCUMENT ME!
	 *
	 * @param species DOCUMENT ME!
	 * @param curator DOCUMENT ME!
	 * @param type DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public Annotation getAnnotation(String species, String curator, String type)
	    throws Exception;

	/**
	 *  DOCUMENT ME!
	 *
	 * @param description DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public Annotation getAnnotation(AnnotationDescription description) throws Exception;

	/**
	 *  DOCUMENT ME!
	 *
	 * @param species DOCUMENT ME!
	 * @param curator DOCUMENT ME!
	 * @param type DOCUMENT ME!
	 * @param entity DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public int[] getClassifications(String species, String curator, String type, String entity)
	    throws Exception;

	/**
	 *  DOCUMENT ME!
	 *
	 * @param description DOCUMENT ME!
	 * @param entity DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public int[] getClassifications(AnnotationDescription description, String entity)
	    throws Exception;

	/**
	 *  DOCUMENT ME!
	 *
	 * @param description DOCUMENT ME!
	 * @param entity DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public String[][] getAllAnnotations(AnnotationDescription description, String entity)
	    throws Exception;

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void clear() throws Exception;

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public String describe() throws Exception;

	// names & synonyms, typically of genes, but possibly of any entity
	/**
	 *  DOCUMENT ME!
	 *
	 * @param species DOCUMENT ME!
	 * @param thesaurus DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void addThesaurus(String species, Thesaurus thesaurus) throws Exception;

	/**
	 *  DOCUMENT ME!
	 *
	 * @param species DOCUMENT ME!
	 * @param commonName DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public String getCanonicalName(String species, String commonName) throws Exception;

	/**
	 *  DOCUMENT ME!
	 *
	 * @param species DOCUMENT ME!
	 * @param commonName DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public String[] getAllCommonNames(String species, String commonName) throws Exception;

	/**
	 *  DOCUMENT ME!
	 *
	 * @param species DOCUMENT ME!
	 * @param canonicalName DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public String getCommonName(String species, String canonicalName) throws Exception;

	// ----------------------------------------------------------------------------------------
} // interface BioDataServerInterface
