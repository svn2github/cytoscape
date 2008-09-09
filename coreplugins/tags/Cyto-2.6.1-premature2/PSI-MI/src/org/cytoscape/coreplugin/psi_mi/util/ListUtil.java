/*
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
package org.cytoscape.coreplugin.psi_mi.util;

import org.cytoscape.coreplugin.psi_mi.schema.mi25.EntrySet;

import java.util.Map;


/**
 * Misc. List Utilities.
 *
 * @author Nisha Vinod
 */
public class ListUtil {
	private static int totalInteractors;
	private static String level;
	private static String version;
	private static int fileCount;
	private static EntrySet entrySet;
	private static org.cytoscape.coreplugin.psi_mi.schema.mi1.EntrySet psiOneEntrySet;
	private static Map interactionMap;

	/**
	 * Gets PSI One Entry Set.
	 * @return PSI One Entry Set.
	 */
	public static org.cytoscape.coreplugin.psi_mi.schema.mi1.EntrySet getPsiOneEntrySet() {
		return psiOneEntrySet;
	}

	/**
	 * Set PSI One Entry Set.
	 * @param psiOneEntrySet PSI One Entry Set.
	 */
	public static void setPsiOneEntrySet(org.cytoscape.coreplugin.psi_mi.schema.mi1.EntrySet psiOneEntrySet) {
		ListUtil.psiOneEntrySet = psiOneEntrySet;
	}

	/**
	 * Gets the Interaction Map.
	 * @return Interaction Map.
	 */
	public static Map getInteractionMap() {
		return interactionMap;
	}

	/**
	 * Sets the Interaction Map.
	 * @param interactionMap Interaction Map.
	 */
	public static void setInteractionMap(Map interactionMap) {
		ListUtil.interactionMap = interactionMap;
	}

	/**
	 * Gets the Entry Set.
	 * @return EntrySet Object.
	 */
	public static EntrySet getEntrySet() {
		return entrySet;
	}

	/**
	 * Sets the Entry Set.
	 * @param eSet Entry Set Object.
	 */
	public static void setEntrySet(EntrySet eSet) {
		entrySet = eSet;
	}

	/**
	 * Sets the Interactor Count.
	 * @param interactorCount interactor count.
	 */
	public static void setInteractorCount(int interactorCount) {
		totalInteractors = interactorCount;
	}

	/**
	 * Gets the Interactor Count.
	 * @return interactorCount.
	 */
	public static int getInteractorCount() {
		return totalInteractors;
	}

	/**
	 * Sets the PSI-MI Level.
	 * @param l PSI-MI Level.
	 */
	public static void setLevel(String l) {
		level = l;
	}

	/**
	 * Gets the PSI-MI Level.
	 * @return PSI-MI Level.
	 */
	public static String getLevel() {
		return level;
	}

	/**
	 * Sets the PSI-MI Version #.
	 * @param v PSI-MI Version #.
	 */
	public static void setVersion(String v) {
		version = v;
	}

	/**
	 * Gets the PSI-MI Version #.
	 * @return PSI-MI Version #.
	 */
	public static String getVersion() {
		return version;
	}

	/**
	 * Sets the File Entry Count.
	 * @param count file entry count.
	 */
	public static void setFileEntryCount(int count) {
		fileCount = count;
	}

	/**
	 * Gets the File Entry Count.
	 * @return file entry count.
	 */
	public static int getFileEntryCount() {
		return fileCount;
	}
}
