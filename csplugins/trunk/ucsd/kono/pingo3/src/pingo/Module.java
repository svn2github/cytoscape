/**
 * * Copyright (c) 2010 Flanders Interuniversitary Institute for Biotechnology (VIB)
 * *
 * * Authors : Steven Maere
 * *
 * * This program is free software; you can redistribute it and/or modify
 * * it under the terms of the GNU General Public License as published by
 * * the Free Software Foundation; either version 2 of the License, or
 * * (at your option) any later version.
 * *
 * * This program is distributed in the hope that it will be useful,
 * * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * * The software and documentation provided hereunder is on an "as is" basis,
 * * and the Flanders Interuniversitary Institute for Biotechnology
 * * has no obligations to provide maintenance, support,
 * * updates, enhancements or modifications.  In no event shall the
 * * Flanders Interuniversitary Institute for Biotechnology
 * * be liable to any party for direct, indirect, special,
 * * incidental or consequential damages, including lost profits, arising
 * * out of the use of this software and its documentation, even if
 * * the Flanders Interuniversitary Institute for Biotechnology
 * * has been advised of the possibility of such damage. See the
 * * GNU General Public License for more details.
 * *
 * * You should have received a copy of the GNU General Public License
 * * along with this program; if not, write to the Free Software
 * * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * *
 * * Authors: Steven Maere
 * * Date: Jul.27.2010
 * * Description: PiNGO is a Cytoscape plugin that leverages functional enrichment
 * * analysis to discover lead genes from biological networks.          
 **/

/*

 ENIGMA - Expression Network Inference and Gene Module Analysis
 Copyright (C) 2007 Flanders Interuniversitary Intsitute for Biotechnology (VIB)

 This source code is freely distributed under the terms of the
 GNU General Public License. See the files COPYRIGHT and LICENSE
 for details.

 Written by Steven Maere

 */

/*

 LeMoNe - a software to build module networks from expression data 
 Copyright (C) 2005-2006 Flanders Interuniversitary Intsitute for Biotechnology (VIB)

 This source code is freely distributed under the terms of the
 GNU General Public License. See the files COPYRIGHT and LICENSE
 for details.

 Written by Eric Bonnet, Steven Maere, Tom Michoel, Yvan Saeys

 */

package pingo;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

import BiNGO.ontology.OntologyTerm;

public class Module {

	public ModuleNetwork moduleNetwork;
	public int number;
	public String name;
	public Set<Gene> Genes = new HashSet<Gene>();
	public LinkedHashMap<OntologyTerm, Double> allGO = new LinkedHashMap<OntologyTerm, Double>();


	public Module() {
	}

	public Module(String name, int number, Set Genes) {
		this.name = name;
		this.number = number;
		for (Object j : Genes) {
			this.Genes.add((Gene) j);
		}
	}

	public void addGene(Gene g) {
		Genes.add(g);
	}

	public void addGenes(HashSet<Gene> g) {
		Genes.addAll(g);
	}
}
