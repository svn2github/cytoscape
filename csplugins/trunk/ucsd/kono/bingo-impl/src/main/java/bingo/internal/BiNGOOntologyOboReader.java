package bingo.internal;

/* * Copyright (c) 2005 Flanders Interuniversitary Institute for Biotechnology (VIB)
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
 **/

/* * Created Date: Mar.15.2010
 * * by : Steven Maere
 * * Copyright (c) 2005-2010 Flanders Interuniversitary Institute for Biotechnology (VIB)
 * */

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import bingo.internal.ontology.Ontology;
import bingo.internal.ontology.OntologyTerm;
import bingo.internal.reader.TextFileReader;
import bingo.internal.reader.TextHttpReader;


public class BiNGOOntologyOboReader {
	
	Ontology ontology;
	Ontology fullOntology;
	String curator = "unknown";
	String ontologyType = "unknown";
	String namespace;
	String[] namespaces;
	String filename;
	String fullText;
	String[] lines;
	HashMap synonymHash;
	HashMap goMap;


	public BiNGOOntologyOboReader(File file, String namespace) throws IllegalArgumentException, IOException, Exception {
		this(file.getPath(), namespace);
	}


	public BiNGOOntologyOboReader(String filename, String namespace) throws IllegalArgumentException, IOException,
			Exception {
		this.filename = filename;
		this.namespace = namespace;
		this.namespaces = namespace.trim().split("\\t");
		if ((this.namespaces == null) || (this.namespace.equals(""))) {
			this.namespace = BingoAlgorithm.NONE;
			this.namespaces = new String[1];
			namespaces[0] = BingoAlgorithm.NONE;
		}
		try {
			if (filename.trim().startsWith("jar:")) {
				BiNGOJarReader reader = new BiNGOJarReader(filename);
				reader.read();
				fullText = reader.getText();
			} else if (filename.trim().startsWith("http://")) {
				TextHttpReader reader = new TextHttpReader(filename);
				reader.read();
				fullText = reader.getText();
			} else {
				TextFileReader reader = new TextFileReader(filename);
				reader.read();
				fullText = reader.getText();
			}
		} catch (IOException e0) {
			System.err.println("-- Exception while reading ontology obo file " + filename);
			System.err.println(e0.getMessage());
			throw e0;
			// return;
		}
		this.synonymHash = new HashMap();
		this.goMap = new HashMap();
		lines = fullText.split("\n");
		parse(parseHeader());

	} // ctor
	// -------------------------------------------------------------------------

	private int stringToInt(String s) {
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException nfe) {
			return -1;
		}
	}

	// -------------------------------------------------------------------------

	private int parseHeader() throws Exception {
		int i = 0;
		while (!lines[i].trim().equals("[Term]")) {
			i++;
		}
		curator = "unknown";
		ontologyType = "unknown";
		return i;

	} // parseHeader
	// -------------------------------------------------------------------------

	private void parse(int c) throws Exception {
		ontology = new Ontology(curator, ontologyType);
		fullOntology = new Ontology(curator, ontologyType);
		int i = c;
		while (i < lines.length && !lines[i].trim().equals("[Typedef]")) {
			i++;
			String name = new String();
			String id = new String();
			HashSet<String> geneNamespaces = new HashSet<String>();
			HashSet<String> alt_id = new HashSet<String>();
			HashSet<String> is_a = new HashSet<String>();
			HashSet<String> part_of = new HashSet<String>();
			boolean obsolete = false;
			while (!lines[i].trim().equals("[Term]") && !lines[i].trim().equals("[Typedef]") && i < lines.length) {
				if (!lines[i].trim().equals("")) {
					String ref = lines[i].substring(0, lines[i].indexOf(":")).trim();
					String value = lines[i].substring(lines[i].indexOf(":") + 1).trim();
					if (ref.equals("name")) {
						name = value.trim();
					} else if (ref.equals("namespace")) {
						geneNamespaces.add(value.trim());
					} else if (ref.equals("subset")) {
						geneNamespaces.add(value.trim());
					} else if (ref.equals("id")) {
						id = value.trim().substring(3);
					} else if (ref.equals("alt_id")) {
						alt_id.add(value.trim().substring(3));
					} else if (ref.equals("is_a")) {
						is_a.add(value.split("!")[0].trim().substring(3));
					} else if (ref.equals("relationship")) {
						if (value.startsWith("part_of")) {
							part_of.add(value.substring(7).split("!")[0].trim().substring(3));
						}
					} else if (ref.equals("is_obsolete")) {
						if (value.trim().equals("true")) {
							obsolete = true;
						}
					}
				}
				i++;
			}
			if (obsolete == false) {
				for (String n : this.namespaces) {
					if (n.equals(BingoAlgorithm.NONE) || geneNamespaces.contains(n)) {
						Integer id2 = new Integer(id);
						synonymHash.put(id2, id2);
						OntologyTerm term = new OntologyTerm(name, id2);
						if (!ontology.containsTerm(id2)) {
							ontology.add(term);
							fullOntology.add(term);
							for (String s : alt_id) {
								synonymHash.put(new Integer(s), id2);
							}
							for (String s : is_a) {
								term.addParent(new Integer(s));
							}
							for (String s : part_of) {
								term.addContainer(new Integer(s));
							}
						}
					} else {
						Integer id2 = new Integer(id);
						OntologyTerm term = new OntologyTerm(name, id2);
						if (!fullOntology.containsTerm(id2)) {
							fullOntology.add(term);
							for (String s : is_a) {
								term.addParent(new Integer(s));
							}
							for (String s : part_of) {
								term.addContainer(new Integer(s));
							}
						}
					}
				}
			}
		}
		// explicitely reroute all connections (parent-child relationships) that
		// are missing in subontologies like GOSlim
		// avoid transitive connections
		if (!namespace.equals("biological_process") && !namespace.equals("molecular_function")
				&& !namespace.equals("cellular_component") && !namespace.equals(BingoAlgorithm.NONE)) {
			for (Integer j : (Set<Integer>) ontology.getTerms().keySet()) {
				OntologyTerm o = ontology.getTerm(j);
				HashSet<OntologyTerm> ancestors = findNearestAncestors(new HashSet<OntologyTerm>(), j);
				HashSet<OntologyTerm> prunedAncestors = new HashSet<OntologyTerm>(ancestors);
				for (OntologyTerm o2 : ancestors) {
					HashSet<OntologyTerm> o2Ancestors = getAllAncestors(new HashSet<OntologyTerm>(), o2);
					for (OntologyTerm o3 : o2Ancestors) {
						if (ancestors.contains(o3)) {
							System.out.println("removed " + o3.getName());
							prunedAncestors.remove(o3);
						}
					}
				}
				for (OntologyTerm o2 : prunedAncestors) {
					o.addParent(o2.getId());
				}
			}
		}

		// makeOntologyFile(System.getProperty("user.home"));

	} // read
	// -------------------------------------------------------------------------

	private HashSet<OntologyTerm> findNearestAncestors(HashSet<OntologyTerm> ancestors, Integer k) {
		for (Integer i : fullOntology.getTerm(k).getParentsAndContainers()) {
			if (!ontology.containsTerm(i)) {
				findNearestAncestors(ancestors, i);
			} else {
				ancestors.add(ontology.getTerm(i));
			}
		}
		return ancestors;
	}

	private HashSet<OntologyTerm> getAllAncestors(HashSet<OntologyTerm> ancestors, OntologyTerm o) {
		for (Integer i : o.getParentsAndContainers()) {
			ancestors.add(fullOntology.getTerm(i));
			getAllAncestors(ancestors, fullOntology.getTerm(i));
		}
		return ancestors;
	}

	public Ontology getOntology() {
		return ontology;
	}

	public HashMap getSynonymHash() {
		return synonymHash;
	}

	public void makeOntologyFile(String outputDir) {
		File f = new File(outputDir, "GO_" + namespace);
		try {
			FileWriter fw = new FileWriter(f);
			PrintWriter pw = new PrintWriter(fw);

			pw.println("(curator=bingo)(type=namespace)");
			for (Object a : ontology.getTerms().keySet()) {
				OntologyTerm o = fullOntology.getTerm(new Integer(a.toString()));
				pw.print(o.getId() + " = " + o.getName());
				boolean ok = false;
				for (int i : o.getParentsAndContainers()) {
					if (ok == false) {
						pw.print("[isa: ");
						ok = true;
					}
					pw.print(i + " ");
				}
				if (ok == true) {
					pw.println("]");
				} else {
					pw.println();
				}
			}
			fw.close();
		} catch (IOException e) {
			System.out.println("IOException: " + e);
		}
	}

	// -------------------------------------------------------------------------
} // class bingoOntologyFlatFileReader
