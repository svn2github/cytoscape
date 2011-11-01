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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;

import BiNGO.ontology.Annotation;
import BiNGO.ontology.Ontology;
import BiNGO.ontology.OntologyTerm;

public class ModuleNetwork {

	public static final String NONE = "---";

	// List of genes
	public ArrayList<Gene> geneSet;
	// List of modules
	public HashSet<Module> moduleSet;
	// Graph underlying module network
	public HashMap<Gene, HashMap<Gene, Double>> G;
	// Number of modules
	public int numModules;
	// Number of genes
	public int numGenes;

	// properties in a hashmap, used for GO info
	public HashMap<String, String> hp = new HashMap();
	public Ontology ontology;
	public Annotation annotation;
	public HashMap<OntologyTerm, HashSet<Gene>> annotatedGenes;

	// For each gene, the modules to which it belongs
	public HashMap<Gene, HashSet<Module>> inModule;
	// name-gene map to find genes by name
	public Map<String, Gene> geneMap = new HashMap<String, Gene>();
	// alias-gene map to find genes by alias
	public HashMap<String, Gene> aliasMap = new HashMap<String, Gene>();
	public PingoParameters pp;


	public ModuleNetwork(PingoParameters pp) {
		this.pp = pp;
		this.setGraph(pp.getGraph());
		this.ontology = pp.getOntology();
		this.annotation = pp.getAnnotation();
		this.geneMap = pp.getGeneMap();
		this.geneSet = new ArrayList<Gene>(this.geneMap.values());
		annotateGenes();
	}

	public void setGraph(Map<Gene, Map<Gene, Double>> H) {
		this.G = new HashMap<Gene, HashMap<Gene, Double>>();
		for (Gene i : H.keySet()) {
			// double linkage, with autocorrelations
			for (Gene j : H.get(i).keySet()) {
				if (G.get(i) == null) {
					G.put(i, new HashMap<Gene, Double>());
				}
				if (G.get(j) == null) {
					G.put(j, new HashMap<Gene, Double>());
				}
				G.get(j).put(i, H.get(i).get(j));
				G.get(i).put(j, H.get(i).get(j));
			}
		}
	}

	public void setModules(HashSet<Module> modules) {
		this.moduleSet = new HashSet<Module>();
		moduleSet.addAll(modules);
		this.inModule = new HashMap<Gene, HashSet<Module>>();
		this.numModules = modules.size();
		for (Module m : modules) {
			m.moduleNetwork = this;
			for (Gene g : m.Genes) {
				if (inModule.get(g) == null) {
					inModule.put(g, new HashSet<Module>());
				}
				inModule.get(g).add(m);
			}
		}
		// put empty HashSets for all genes without modules
		for (Gene g : this.geneSet) {
			if (inModule.get(g) == null) {
				inModule.put(g, new HashSet<Module>());
			}
		}
	}

	public void annotateGenes() {
		// store gene annotations
		for (Gene g : this.geneSet) {
			importClassificationsForGene(g);
		}
		// fill annotatedGenes
		this.annotatedGenes = new HashMap<OntologyTerm, HashSet<Gene>>();
		for (Gene g : this.geneSet) {
			for (OntologyTerm o : g.GOannotations) {
				if (!this.annotatedGenes.containsKey(o)) {
					this.annotatedGenes.put(o, new HashSet<Gene>());
				}
				this.annotatedGenes.get(o).add(g);
			}
		}
	}

	public void importClassificationsForGene(Gene g) {
		// clear current classifications
		g.GOannotations = new HashSet<OntologyTerm>();
		HashSet<String> identifiers = new HashSet<String>();
		if (pp.getAlias().containsKey(g.name)) {
			identifiers = (HashSet<String>) pp.getAlias().get(g.name);
		}
		HashSet<String> cls = new HashSet<String>();
		// array for go labels.
		for (String s : identifiers) {
			int[] goLabelsName = annotation.getClassifications(s);
			for (int t = 0; t < goLabelsName.length; t++) {
				cls.add(goLabelsName[t] + "");
			}
		}

		// store classifications for gene
		for (String s : cls) {
			g.GOannotations.add(ontology.getTerm(Integer.parseInt(s)));
		}
	}

	// read gene descriptions
	public void readGeneDescription(String geneDescriptionFile) {
		System.out.println("Processing gene descriptions ...");
		// create hashmap to find genes in geneSet by their alias
		// aliasMap = new HashMap<String, Gene>();
		if (!geneDescriptionFile.equals(NONE)) {
			try {
				Scanner descrScanner = new Scanner(new File(geneDescriptionFile)).useDelimiter("\\n");
				// walk through file
				// first line = header
				// descrScanner.next();
				while (descrScanner.hasNext()) {
					Scanner line = new Scanner(descrScanner.next().toString()).useDelimiter("\\t");
					// read primary ID
					String name = line.next().trim().toUpperCase();
					// read alias
					String alias = line.next().trim().toUpperCase();
					if ((alias == null) || (alias.equals(""))) {
						alias = name;
					}
					String description = new String();
					if (line.hasNext()) {
						description = line.next();
					}
					// add description to gene

					if (geneMap.containsKey(name)) {
						geneMap.get(name).description = description;
						geneMap.get(name).alias = alias;
						if (aliasMap.containsKey(geneMap.get(name).alias)) {
							System.out.println("WARNING: alias for gene " + geneMap.get(name).name
									+ "multiply defined: " + aliasMap.get(geneMap.get(name).alias).name);
						}
						aliasMap.put(geneMap.get(name).alias, geneMap.get(name));
					} else if (geneMap.containsKey(alias)) {
						geneMap.get(alias).description = description;
						geneMap.get(alias).alias = alias;
						if (aliasMap.containsKey(geneMap.get(alias).alias)) {
							System.out.println("WARNING: alias for gene " + geneMap.get(alias).name
									+ "multiply defined: " + aliasMap.get(geneMap.get(alias).alias).name);
						}
						aliasMap.put(geneMap.get(alias).alias, geneMap.get(alias));
					}
				}
				// make sure every gene has alias
				for (Gene g : geneMap.values()) {
					if (g.alias.equals("")) {
						g.alias = g.name;
						aliasMap.put(g.name, g);
					}
				}

			} catch (Exception e) {
				System.out.println(e.toString());
			}
		}
	}

	// output stats
	public void output_stats(String dataDir, String outputfile) {
		try {
			File results = new File(dataDir, outputfile);
			BufferedWriter o = new BufferedWriter(new FileWriter(results));
			System.out.println("PiNGO graph stats file : " + results.getPath());
			o.write("ORF_name\tGene_name\tDescription\tk\tC\tNr_modules\tModules\n");
			for (Object g : G.keySet()) {
				if (G.get(g).size() > 0) {
					// remove autocorrelation
					int posDegree = G.get(g).size() - 1;
					o.write(((Gene) g).name + "\t" + ((Gene) g).alias + "\t" + ((Gene) g).description + "\t"
							+ posDegree + "\t" + inModule.get(g).size() + "\t");
					for (Module m : inModule.get(g)) {
						o.write(m.number + "\t");
					}
					o.write("\n");
				}
			}
			o.close();
		} catch (IOException e) {
			System.out.println(e);
		}

	}

	public void outputModules(String dataDir, String outputfile) {
		try {
			File results = new File(dataDir, outputfile);
			BufferedWriter o = new BufferedWriter(new FileWriter(results));
			System.out.println("PiNGO module file : " + results.getPath());
			o.write("Module_number\tModule_size\tMember Genes\n");
			for (Module c : this.moduleSet) {
				o.write(c.number + "\t" + c.Genes.size() + "\t");
				for (Gene g : c.Genes) {
					o.write(g.alias + "\t");
				}
				o.write("\n");
			}
			o.close();
		} catch (IOException e) {
			System.out.println(e);
		}

	}

}
