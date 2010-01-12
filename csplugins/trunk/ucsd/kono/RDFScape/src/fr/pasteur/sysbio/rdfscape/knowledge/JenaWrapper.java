/**
 * Copyright 2006-2007 Andrea Splendiani
 * Released under GPL license
 *
 */
package fr.pasteur.sysbio.rdfscape.knowledge;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.mindswap.pellet.jena.PelletInfGraph;
import org.mindswap.pellet.jena.PelletReasonerFactory;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryException;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QueryParseException;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;
import com.hp.hpl.jena.vocabulary.ReasonerVocabulary;

import fr.pasteur.sysbio.rdfscape.CommonMemory;
import fr.pasteur.sysbio.rdfscape.RDFScape;
import fr.pasteur.sysbio.rdfscape.RichResource;
import fr.pasteur.sysbio.rdfscape.query.AbstractQueryResultTable;
import fr.pasteur.sysbio.rdfscape.query.ClassQueryEngine;
import fr.pasteur.sysbio.rdfscape.query.GraphQueryAnswerer;
import fr.pasteur.sysbio.rdfscape.query.JenaQueryResultTable;
import fr.pasteur.sysbio.rdfscape.query.RDQLQueryAnswerer;
import fr.pasteur.sysbio.rdfscape.query.RDQLQueryEngine;
import fr.pasteur.sysbio.rdfscape.query.SPARQLQueryAnswerer;
import fr.pasteur.sysbio.rdfscape.query.SPARQLQueryEngine;
import fr.pasteur.sysbio.rdfscape.query.StringQueryEngine;

/**
 * @author andrea Implementation of a KnowledgeWrapper based in Jena. See the
 *         documention of {@link}KnowledgeWrapper or the implemented interfaces:
 *         {@link}RDQLQueryAnswerer {@link}GraphQueryAnswerer {@link}
 *         SPARQLQueryAnswerer for details on its functionalities
 * 
 */
public class JenaWrapper extends KnowledgeWrapper implements RDQLQueryAnswerer,
		GraphQueryAnswerer, SPARQLQueryAnswerer {
	private Model myBasicKnowledge = null; // asserted facts
	private OntModel myPreProcessedKnowledge = null; // facts custom rules (per
														// OWL/RDF inference)
	private OntModel myOntoKnowledge = null; // facts inferred through OWL/RDF
												// inference
	private Model myKnowledge = null; // facts inferred through custom reasoning

	String reasonerURI = "http://localhost:8081";

	// private RDFScape myRDFScapeInstance=null; //used to send messages to the
	// user

	// private InfRuleObject[] rulesArray=null;
	private ArrayList preRuleList = null;
	private ArrayList postRuleList = null;
	private int validStatus; // Satus=0 : nothing known
	private int status; // Status=1 : only base knowledge is stable.
	private int targetStatus; // Status=2 : pre knowledge+unif rule
	// Status=3 : owl/rdf inf.
	// Status=4 : post rules
	/*
	 * private String[][]
	 * options={{"RDFS","OWL"},{"NONE","Low","Medium","High"}}; private String[]
	 * defaultOptions={"RDFS","Low"}; private String[]
	 * paramNames={"Language","Level"};
	 */
	private String[][] ruleOptions = { { "POST" }, { "JENA" } };
	private String[] ruleDefaultOptions = { "POST", "JENA" };
	private String[] ruleOptionNames = { "STAGE", "LANGUAGE" };

	/*
	 * private String ontologyLanguage=defaultOptions[0]; private String
	 * jenaReasoningLevel=defaultOptions[1];
	 */

	// TODO to re-introduce private String[][]
	// options={{"None","RDFS-1","RDFS-2","OWL-low","OWL-high","Pellet","DIG:8080","DIG:Ask"}};
	private String[][] options = { { "None", "RDFS-1", "RDFS-2", "OWL-low",
			"OWL-high", "Pellet-fast", "Pellet", "DIG" } };
	private String[] defaultOptions = { "None" };
	private String[] paramNames = { "Level" };
	private String jenaReasoningLevel = defaultOptions[0];

	private OntModelSpec mySpec;

	public JenaWrapper() throws Exception {
		super();
		System.out.println("\tStarring: Jena Wrapper");
		// mySpec=getBaseModelSpec();
		initialize();
		System.out.println("\t          - ready");

	}

	public void initialize() {
		myBasicKnowledge = ModelFactory
				.createOntologyModel(OntModelSpec.OWL_MEM);
		myKnowledge = myBasicKnowledge;
		preRuleList = new ArrayList();
		postRuleList = new ArrayList();
		status = 0;
		validStatus = 0;
		targetStatus = 3;

	}

	public void reset() {
		myBasicKnowledge.removeAll();
		preRuleList = new ArrayList();
		postRuleList = new ArrayList();
		status = 0;
		validStatus = 0;
		targetStatus = 3;
		System.out.println("After reset mu basic knowldge contains "
				+ myBasicKnowledge.size() + " sharp!");
	}

	public String[][] getRuleOptions() {
		return ruleOptions;
	}

	public String[] getRuleOptionDefaultValues() {
		return ruleDefaultOptions;
	}

	public String[] getRuleOptionsNames() {
		return ruleOptionNames;
	}

	public String[][] getReasonerOptions() {
		return options;
	}

	public String[] getReasonerOptionNames() {
		return paramNames;
	}

	public String[] getReasonerOptionsDefaultValues() {
		return defaultOptions;
	}

	/**
	 * TODO in theory, this method shouldn't be here. It should be in the
	 * superclass... and it should be static. There seems to be some problem
	 * with this now... leaving it here... This is needed by ContextElement,
	 * when it creates a default object. It asks for a default knowledge engine
	 * in DefaultSettings, and given this, via this static method, it asks which
	 * parameters should be there.
	 */
	/*
	 * Maybe we don't need this anymore. Default says nothing expect the
	 * reasoner... if nothing is known from the context. A default is provided
	 * by the context consumer. public static Hashtable
	 * getDefaultKnowledgeEngineOptions() { Hashtable table=new Hashtable();
	 * table.put("Language","RDFS"); table.put("Level","Low"); return table;
	 * 
	 * }
	 */

	public String[] getReasonerActualOptions() {
		String[] answer = new String[2];
		// answer[0]=ontologyLanguage;
		answer[0] = jenaReasoningLevel;
		return answer;
	}

	/*
	 * public void setReasonerActualOptions(String[] optionsSelected) {
	 * ontologyLevel=optionsSelected[0]; jenaReasoningMode=optionsSelected[1]; }
	 */

	public boolean canAddontologies() {
		return true;
	}

	public boolean canAddRules() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.pasteur.sysbio.rdfscape.knowledge.KnowledgeWrapper#addOntology(java
	 * .lang.String)
	 */
	public String addOntology(String address) {
		CommonMemory commonMemory = RDFScape.getCommonMemory();
		String result = null;
		OntModel myModel = ModelFactory
				.createOntologyModel(OntModelSpec.OWL_MEM);
		try {
			myModel.read(address);
			result = (new Long(myModel.size())).toString();
			myBasicKnowledge.add(myModel);
			System.out.println("My basic knowledge gets to..."
					+ myBasicKnowledge.size() + " sharp!");
			Map nameSpacesFound = myModel.getNsPrefixMap();
			Collection keys = nameSpacesFound.keySet();
			for (Iterator iter = keys.iterator(); iter.hasNext();) {
				String key = (String) iter.next();
				String namespace = (String) nameSpacesFound.get(key);
				System.out.println(key + " : " + namespace);
				commonMemory.registerNameSpace(namespace);
				/**
				 * Note: never override prefixes declared before! This make more
				 * sense becouse they include the ones explicitely declared by
				 * the user
				 */
				if (commonMemory.getPrefixFromNs(namespace) == null)
					commonMemory.registerPrefix(namespace, key);

			}
			commonMemory.touch();
			if (status < 1)
				status = 1; // We know now, at least.
			validStatus = 0; // What we know it's consistent. Everything else
								// must change.
			if (targetStatus < 1)
				targetStatus = 1; // We should at least get here.
			System.out.println("JenaWrapperStatus: S:" + status + " V:"
					+ validStatus + " T: " + targetStatus);
		} catch (Exception e) {
			System.out.println("Problem while adding ontology");
			e.printStackTrace();
		}
		System.out.println("Mu knowledge get to " + myBasicKnowledge.size()
				+ " statements");
		return result;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see fr.pasteur.sysbio.rdfscape.knowledge.KnowledgeWrapper#addRuleSet(java.lang.String[])
	 */
	public boolean addRuleSet(ArrayList rules) {
		ArrayList tempPreRuleList = new ArrayList();
		ArrayList tempPostRuleList = new ArrayList();
		for (Iterator iter = rules.iterator(); iter.hasNext();) {
			InfRuleObject rule = (InfRuleObject) iter.next();
			if (rule.isActive()) {
				if (rule.getParam("STAGE") == null) {
					System.out.println("JenaWrapper: rule " + rule.getName()
							+ " has no stage info");
					return false;
				}
				if (rule.getParam("STAGE").equalsIgnoreCase("PRE"))
					tempPreRuleList.add(rule);
				else if (rule.getParam("STAGE").equalsIgnoreCase("POST"))
					tempPostRuleList.add(rule);
				else {
					System.out.println("JenaWrapper: rule " + rule.getName()
							+ " has invalid stage info :"
							+ rule.getParam("STAGE"));
					return false;
				}
			}
		}

		System.out.print(".");
		boolean isUnchangedPre = true;
		boolean isUnchangedPost = true;
		for (Iterator iter = tempPreRuleList.iterator(); iter.hasNext();) {
			InfRuleObject element = (InfRuleObject) iter.next();
			if (!preRuleList.contains(element))
				isUnchangedPre = false;
			else {
				InfRuleObject ruleToCompare = (InfRuleObject) preRuleList
						.get(preRuleList.indexOf(element));
				if (!ruleToCompare.getRule().equals(element.getRule()))
					isUnchangedPre = false;
				if (!ruleToCompare.isActive() != element.isActive())
					isUnchangedPre = false;
			}

		}
		System.out.print(".");
		for (Iterator iter = preRuleList.iterator(); iter.hasNext();) {
			InfRuleObject element = (InfRuleObject) iter.next();
			if (!tempPreRuleList.contains(element))
				isUnchangedPre = false;
			else {
				InfRuleObject ruleToCompare = (InfRuleObject) tempPreRuleList
						.get(tempPreRuleList.indexOf(element));
				if (!ruleToCompare.getRule().equals(element.getRule()))
					isUnchangedPre = false;
				if (!ruleToCompare.isActive() != element.isActive())
					isUnchangedPre = false;
			}
		}
		System.out.print(".");
		for (Iterator iter = tempPostRuleList.iterator(); iter.hasNext();) {
			InfRuleObject element = (InfRuleObject) iter.next();
			if (!postRuleList.contains(element))
				isUnchangedPost = false;
			else {
				InfRuleObject ruleToCompare = (InfRuleObject) postRuleList
						.get(postRuleList.indexOf(element));
				if (!ruleToCompare.getRule().equals(element.getRule()))
					isUnchangedPre = false;
				if (!ruleToCompare.isActive() != element.isActive())
					isUnchangedPre = false;
			}
		}
		System.out.print(".");
		for (Iterator iter = postRuleList.iterator(); iter.hasNext();) {
			InfRuleObject element = (InfRuleObject) iter.next();
			if (!tempPostRuleList.contains(element))
				isUnchangedPost = false;
			else {
				InfRuleObject ruleToCompare = (InfRuleObject) tempPostRuleList
						.get(tempPostRuleList.indexOf(element));
				if (!ruleToCompare.getRule().equals(element.getRule()))
					isUnchangedPre = false;
				if (!ruleToCompare.isActive() != element.isActive())
					isUnchangedPre = false;
			}
		}
		System.out.println(".");
		if (!isUnchangedPre) {
			preRuleList = tempPreRuleList;
			if (validStatus > 1)
				validStatus = 1;
			if (targetStatus < 2)
				targetStatus = 2;

		}
		if (!isUnchangedPost) {
			postRuleList = tempPostRuleList;
			if (validStatus > 3)
				validStatus = 3;
			targetStatus = 4;

		}

		if (preRuleList.size() == 0) {
			if (targetStatus == 2)
				targetStatus = 1;
		}
		if (postRuleList.size() == 0) {
			if (targetStatus == 4)
				targetStatus = 3;
		}
		System.out.println("JenaWrapperStatus: S:" + status + " V:"
				+ validStatus + "T: " + targetStatus);
		// touch();
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.pasteur.sysbio.rdfscape.knowledge.KnowledgeWrapper#getResourceHandler
	 * (java.lang.String)
	 */
	public RichResource getResourceHandler(String URI) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.pasteur.sysbio.rdfscape.knowledge.KnowledgeWrapper#setReasonerParameter
	 * (java.lang.String, java.lang.String)
	 */
	public void setReasonerParameter(String arg, String value) {
		System.out.println("JenaWrapper: asked to set " + arg + " as " + value);
		if (arg == null || value == null) {
			System.out.println("Missing something...");
			return;
		}
		/*
		 * if(arg.equalsIgnoreCase("Language")) { boolean valid=false; for (int
		 * i = 0; i < options[0].length; i++) {
		 * if(value.equalsIgnoreCase(options[0][i])) valid=true; }
		 * if(valid==true) switchLanguageLevel(value); else
		 * System.out.println("JenaWrapper: "+ value
		 * +" is not a valid value for "+ arg); }
		 */
		else if (arg.equalsIgnoreCase("Level")) {
			boolean valid = false;
			for (int i = 0; i < options[0].length; i++) {
				if (value.equalsIgnoreCase(options[0][i]))
					valid = true;
			}
			if (valid == true)
				switchJenaLevel(value);
			else
				System.out.println("JenaWrapper: " + value
						+ " is not a valid value for " + arg);
		} else {
			System.out.println("JenaWrapper: unkwown parameter " + arg);
		}
		// touch();

	}

	/**
	 * @return the Jena ModelSpec associated to current settings
	 */
	private OntModelSpec getJenaMode() {
		// System.out.println("Getting mode for "+ontologyLanguage+" "+jenaReasoningLevel);

		if (jenaReasoningLevel.equalsIgnoreCase("None")) {
			System.out.println("r1");
			return OntModelSpec.OWL_MEM;
		} else if (jenaReasoningLevel.equalsIgnoreCase("RDFS-1")) {
			System.out.println("r2");
			return OntModelSpec.RDFS_MEM_TRANS_INF;
		} else if (jenaReasoningLevel.equalsIgnoreCase("RDFS-2")) {
			System.out.println("r3");
			return OntModelSpec.RDFS_MEM_RDFS_INF;
		} else if (jenaReasoningLevel.equalsIgnoreCase("OWL-low")) {
			System.out.println("r4");
			return OntModelSpec.OWL_MEM_MICRO_RULE_INF;
		} else if (jenaReasoningLevel.equalsIgnoreCase("OWL-high")) {
			System.out.println("r5");
			return OntModelSpec.OWL_MEM_MINI_RULE_INF;
		} else if (jenaReasoningLevel.equalsIgnoreCase("Pellet-fast")) {
			System.out.println("r5");
			return OntModelSpec.OWL_MEM;
		} else if (jenaReasoningLevel.equalsIgnoreCase("Pellet")) {
			System.out.println("r6");
			return PelletReasonerFactory.THE_SPEC;
		} else if (jenaReasoningLevel.equalsIgnoreCase("DIG")) {
			System.out.println("r7");
			return OntModelSpec.OWL_DL_MEM;
		} else if (jenaReasoningLevel.equalsIgnoreCase("DIG:Ask")) {
			System.out.println("r8");
			return OntModelSpec.OWL_DL_MEM;
		} else {
			System.out.println("No suitable spec found");
			return OntModelSpec.OWL_MEM;
		}

	}

	private OntModelSpec getBaseModelSpec() {
		// System.out.println("BASE: "+ontologyLanguage);
		/*
		 * if(ontologyLanguage.equalsIgnoreCase("RDFS")) return
		 * OntModelSpec.RDFS_MEM; else
		 * if(ontologyLanguage.equalsIgnoreCase("OWL")) return
		 * OntModelSpec.OWL_MEM; else {System.out.println(
		 * "Unable to select base model. Something wrong in the internals...");
		 * return null; }
		 */
		if (jenaReasoningLevel.equalsIgnoreCase("RDFS-1")
				|| jenaReasoningLevel.equalsIgnoreCase("RDFS-2")) {
			return OntModelSpec.RDFS_MEM;
		} else
			return OntModelSpec.OWL_MEM;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.pasteur.sysbio.rdfscape.knowledge.KnowledgeWrapper#clear()
	 */
	public void clear() {
		RDFScape.getCommonMemory().initialize();

	}

	/**
	 * Here we update our whole knowledge!
	 */
	public void touch() {
		System.out.println("Checking knowledge");
		System.out.println("JenaWrapperStatus: S:" + status + " V:"
				+ validStatus + " T: " + targetStatus);
		/**
		 * A new touch, simplified and integrated...
		 */
		System.out.println("Basic knowledge has " + myBasicKnowledge.size()
				+ " known facts");
		if (myBasicKnowledge.size() < 10) {
			System.out.println("Why not loading some ontology first ?");
		}

		if (jenaReasoningLevel.equalsIgnoreCase("RDFS-2")
				|| jenaReasoningLevel.equalsIgnoreCase("OWL-low")
				|| jenaReasoningLevel.equalsIgnoreCase("OWL-high")) {
			System.out.println("Mhhh this is a rule based reasoner...");
			System.out.println("Going to hack it directly");
			String ruleBlock = new String("");
			if (jenaReasoningLevel.equalsIgnoreCase("RDFS-2"))
				ruleBlock = makeRuleBlock("RDFS");
			if (jenaReasoningLevel.equalsIgnoreCase("OWL-low"))
				ruleBlock = makeRuleBlock("OWLMicro");
			if (jenaReasoningLevel.equalsIgnoreCase("OWL-high"))
				ruleBlock = makeRuleBlock("OWLMini");
			List tempRules;

			try {
				tempRules = Rule.parseRules(Rule
						.rulesParserFromReader(new BufferedReader(
								new StringReader(ruleBlock))));
			} catch (Exception e) {
				System.out.println("Parser Error :\n" + e);
				return;
			}
			OntModelSpec rulesSpec = getJenaMode();
			GenericRuleReasoner reasoner = new GenericRuleReasoner(tempRules,
					rulesSpec.getReasonerFactory());
			rulesSpec.setReasoner(reasoner);
			myOntoKnowledge = ModelFactory.createOntologyModel(rulesSpec);
			myOntoKnowledge.add(myBasicKnowledge);
			myKnowledge = myOntoKnowledge;
			// myKnowledge.prepare();

		} else if (jenaReasoningLevel.equalsIgnoreCase("Pellet-fast")
				|| jenaReasoningLevel.equalsIgnoreCase("Pellet")) {
			System.out.println("You want a Pellet reasoner here...("
					+ jenaReasoningLevel + ")");

			// We speed up things here...
			if (jenaReasoningLevel.equalsIgnoreCase("Pellet-fast")) {

				// OntModel
				// tempModel2=ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC,myBasicKnowledge);
				//				
				// System.out.println("----------Before touch");
				// tempModel2.prepare();
				// System.out.println("----------Trying to speed up Pellet here...");
				// OWLReasoner reasoner =
				// ((PelletInfGraph)tempModel2.getGraph()).getOWLReasoner();
				// reasoner.classify();
				// System.out.println("----------Classify done");
				// reasoner.realize();
				// System.out.println("----------Realize done");
				// myOntoKnowledge=ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
				//				
				//				
				//				
				// //Model m=reasoner.extractModel(false);
				//				
				// System.out.println("Adding my basic knowledge ("+myBasicKnowledge.size()+" statements)");
				// myOntoKnowledge.add(myBasicKnowledge);
				// System.out.print("Adding properties from Pellet ");
				// Model m=reasoner.extractPropertyModel(true);
				// System.out.println("("+m.size()+" statements )");
				// myOntoKnowledge.add(m);
				// System.out.print("Adding classes from Pellet");
				// Model m2=reasoner.extractClassModel(true);
				// System.out.println("("+m2.size()+" statements )");
				// myOntoKnowledge.add(m2);
				// System.out.println("Know I know ("+myOntoKnowledge.size()+" statements )");
				// System.out.println("Prepare");
				// myOntoKnowledge.prepare();
				// System.out.println("Know I know ("+myOntoKnowledge.size()+" statements )");
			} else {
				myOntoKnowledge = ModelFactory.createOntologyModel(
						PelletReasonerFactory.THE_SPEC, myBasicKnowledge);
				myOntoKnowledge.prepare();
			}
			System.out.println("----------Dealing with rules...");
			//

			if (preRuleList.size() + postRuleList.size() > 0) {
				System.out
						.println("Let me see if I can add some support for your rules...(on top of this)");
				String ruleBlock = makeRuleBlock(null);
				if (jenaReasoningLevel.equalsIgnoreCase("Pellet-fast"))
					ruleBlock = makeRuleBlock("RDFS");
				List tempRules;
				try {
					tempRules = Rule.parseRules(Rule
							.rulesParserFromReader(new BufferedReader(
									new StringReader(ruleBlock))));
				} catch (Exception e) {
					System.out.println("Parser Error :\n" + e);
					return;
				}
				GenericRuleReasoner reasoner = new GenericRuleReasoner(
						tempRules);
				// reasoner.setMode(GenericRuleReasoner.BACKWARD);
				// reasoner.setOWLTranslation(false);
				// reasoner.setTransitiveClosureCaching(true);

				OntModelSpec specs = OntModelSpec.OWL_MEM;
				specs.setReasoner(reasoner);
				myKnowledge = ModelFactory.createOntologyModel(specs);
				myKnowledge.add(myOntoKnowledge);

				// myKnowledge=ModelFactory.createInfModel(reasoner,myOntoKnowledge);
			} else {
				myKnowledge = myOntoKnowledge;
			}
		} else if (jenaReasoningLevel.equalsIgnoreCase("RDFS-1")) {
			System.out.println("You want fast and cheap RDFS entailments...");
			myOntoKnowledge = ModelFactory.createOntologyModel(getJenaMode());
			myOntoKnowledge.add(myBasicKnowledge);
			myOntoKnowledge.prepare();
			if (preRuleList.size() + postRuleList.size() > 0) {
				System.out
						.println("Let me see if I can add some support for your rules...(on top of this)");
				String ruleBlock = makeRuleBlock(null);
				List tempRules;
				try {
					tempRules = Rule.parseRules(Rule
							.rulesParserFromReader(new BufferedReader(
									new StringReader(ruleBlock))));
				} catch (Exception e) {
					System.out.println("Parser Error :\n" + e);
					return;
				}
				GenericRuleReasoner reasoner = new GenericRuleReasoner(
						tempRules);
				reasoner.setMode(GenericRuleReasoner.BACKWARD);
				reasoner.setOWLTranslation(false);
				reasoner.setTransitiveClosureCaching(true);

				OntModelSpec specs = OntModelSpec.OWL_MEM;
				specs.setReasoner(reasoner);
				myKnowledge = ModelFactory.createOntologyModel(specs);
				myKnowledge.add(myOntoKnowledge);

				// myKnowledge=ModelFactory.createInfModel(reasoner,myOntoKnowledge);
			} else {
				myKnowledge = myOntoKnowledge;
			}

		} else if (jenaReasoningLevel.equalsIgnoreCase("None")) {
			System.out.println("You want no entailments, maybe just rules...");

			System.out
					.println("Let me see if I can add some support for your rules...");
			String ruleBlock = makeRuleBlock(null);
			List tempRules;
			try {
				tempRules = Rule.parseRules(Rule
						.rulesParserFromReader(new BufferedReader(
								new StringReader(ruleBlock))));
			} catch (Exception e) {
				System.out.println("Parser Error :\n" + e);
				return;
			}
			GenericRuleReasoner reasoner = new GenericRuleReasoner(tempRules);
			reasoner.setMode(GenericRuleReasoner.BACKWARD);
			reasoner.setOWLTranslation(false);
			reasoner.setTransitiveClosureCaching(true);

			OntModelSpec specs = OntModelSpec.OWL_MEM;
			specs.setReasoner(reasoner);
			myKnowledge = ModelFactory.createOntologyModel(specs);
			myKnowledge.add(myBasicKnowledge);

			// myKnowledge=ModelFactory.createInfModel(reasoner,myBasicKnowledge);

		} else if (jenaReasoningLevel.equalsIgnoreCase("DIG")) {

//			reasonerURI = JOptionPane.showInputDialog(
//					"Plese specify reasoner URI", reasonerURI);
//			if (reasonerURI == null)
//				reasonerURI = "http://localhost:8081";
//			System.out.println("--> " + reasonerURI);
//			Model cModel = ModelFactory.createDefaultModel();
//			Resource conf = cModel.createResource();
//			conf.addProperty(ReasonerVocabulary.EXT_REASONER_URL, cModel
//					.createResource(reasonerURI));
//
//			// create the reasoner factory and the reasoner
//			DIGReasonerFactory drf = (DIGReasonerFactory) ReasonerRegistry
//					.theRegistry().getFactory(DIGReasonerFactory.URI);
//			DIGReasoner r = (DIGReasoner) drf.create(conf);
//			System.out.println("Reasoner created");
//			// now make a model
//			OntModelSpec spec = new OntModelSpec(OntModelSpec.OWL_DL_MEM);
//			spec.setReasoner(r);
//			OntModel myPreOntoKnowledge = ModelFactory.createOntologyModel(
//					spec, null);
//			myPreOntoKnowledge.add(myBasicKnowledge);
//			System.out.println("My knowledge pre-rules has size >= "
//					+ myPreOntoKnowledge.size());
//			if (preRuleList.size() + postRuleList.size() > 0) {
//				System.out
//						.println("Let me see if I can add some support for your rules...(on top of this)");
//				String ruleBlock = makeRuleBlock(null);
//				List tempRules;
//				try {
//					tempRules = Rule.parseRules(Rule
//							.rulesParserFromReader(new BufferedReader(
//									new StringReader(ruleBlock))));
//				} catch (Exception e) {
//					System.out.println("Parser Error :\n" + e);
//					return;
//				}
//				GenericRuleReasoner reasoner = new GenericRuleReasoner(
//						tempRules);
//				reasoner.setMode(GenericRuleReasoner.BACKWARD);
//				reasoner.setOWLTranslation(false);
//				reasoner.setTransitiveClosureCaching(true);
//
//				OntModelSpec specs = OntModelSpec.OWL_MEM;
//				specs.setReasoner(reasoner);
//				myOntoKnowledge = ModelFactory.createOntologyModel(specs);
//				myOntoKnowledge.add(myPreOntoKnowledge);
//				System.out.println("My knowledge post-rules has size >= "
//						+ myPreOntoKnowledge.size());
//
//				// myKnowledge=ModelFactory.createInfModel(reasoner,myOntoKnowledge);
//			}
//			myKnowledge = myPreOntoKnowledge;

		}
		System.out.println("My knowledge get to a minimum of "
				+ myKnowledge.size() + " statements");
		// TODO to re-introduce
		/*
		 * else if(jenaReasoningLevel.equalsIgnoreCase("DIG:8080") ||
		 * jenaReasoningLevel.equalsIgnoreCase("DIG:Ask")) {
		 * 
		 * 
		 * Model cModel = ModelFactory.createDefaultModel(); Resource conf =
		 * cModel.createResource(); conf.addProperty(
		 * ReasonerVocabulary.EXT_REASONER_URL, cModel.createResource(
		 * reasonerURI ) );
		 * 
		 * // create the reasoner factory and the reasoner DIGReasonerFactory
		 * drf = (DIGReasonerFactory) ReasonerRegistry.theRegistry()
		 * .getFactory( DIGReasonerFactory.URI ); DIGReasoner r = (DIGReasoner)
		 * drf.create( conf );
		 * 
		 * // now make a model OntModelSpec spec = new OntModelSpec(
		 * OntModelSpec.OWL_DL_MEM ); spec.setReasoner( r ); myOntoKnowledge =
		 * ModelFactory.createOntologyModel( spec, null );
		 * 
		 * }
		 */

		/*
		 * if(validStatus==targetStatus) {
		 * System.out.println("Nothing to do, my knowledge is fine"); return; }
		 * if(validStatus==0 && targetStatus>0) {System.out.println(
		 * "Nothing known yet... but you prentend I know something...");
		 * if(status==1) {
		 * myKnowledge=ModelFactory.createOntologyModel(getBaseModelSpec());
		 * myKnowledge.add(myBasicKnowledge); myKnowledge.prepare();
		 * validStatus=1; status=1;
		 * System.out.println("Setting up a model with no inference");
		 * System.out
		 * .println("My knowledge get to a minimum of "+myKnowledge.size()+
		 * " statements"); } else {
		 * System.out.println("Would you mind loading some ontology first ?");
		 * return; }
		 * 
		 * }
		 */
		/*
		 * if(validStatus==1 && targetStatus>1) {
		 * System.out.println("("+validStatus+","+targetStatus+
		 * ") I have a basic knowledge, ad you want me at least to apply apply pre-rdf/owl rules"
		 * ); makePreProcessedKnowledge(); } if(validStatus==2 &&
		 * targetStatus>2) {
		 * System.out.println("("+validStatus+","+targetStatus+
		 * ") You also want me to apply rdf/owl inference");
		 * makeOntoKnowledge(); } if(validStatus==3 && targetStatus==4) {
		 * System.out.println("("+validStatus+","+targetStatus+
		 * ") Have to apply custom rules..."); makePostProcessedKnowledge(); }
		 */
		// makePreProcessedKnowledge();
		// makeOntoKnowledge();
		// makePostProcessedKnowledge();
	}

	/**
	 * Applies a first level of rules (directive: PRE) TODO to complete!!!
	 */
	/*
	 * private void makePreProcessedKnowledge() { CommonMemory
	 * commonMemory=RDFScape.getCommonMemory();
	 * 
	 * if(preRuleList.size()==0) { //myKnowledge=myBasicKnowledge;
	 * myPreProcessedKnowledge
	 * =ModelFactory.createOntologyModel(getBaseModelSpec());
	 * myPreProcessedKnowledge.add(myBasicKnowledge);
	 * myPreProcessedKnowledge.prepare(); myPreProcessedKnowledge.reset();
	 * myKnowledge=myPreProcessedKnowledge; status=2; validStatus=2;
	 * System.out.println("This was pretty easy...");
	 * System.out.println("My knowledge get to "+myKnowledge.size()+
	 * " statements"); return; } String[]
	 * namespaces=commonMemory.getNamespaces(); String ruleBlock=new String();
	 * for (int i = 0; i < namespaces.length; i++) { String
	 * tempPrefix=commonMemory.getNamespacePrefix(namespaces[i]);
	 * if(tempPrefix!=null)
	 * ruleBlock=ruleBlock.concat("@prefix "+tempPrefix+": "
	 * +"<"+namespaces[i]+">\n"); } ruleBlock=ruleBlock.concat("\n\n"); for
	 * (Iterator iter = preRuleList.iterator(); iter.hasNext();) { InfRuleObject
	 * rule = (InfRuleObject) iter.next(); if(rule.isActive()) {
	 * ruleBlock=ruleBlock.concat(rule.getRule()+"\n\n"); }
	 * 
	 * }
	 * 
	 * System.out.println("Your rules...\n"+ruleBlock); List tempRules; try {
	 * tempRules=Rule.parseRules(Rule.rulesParserFromReader(new
	 * BufferedReader(new StringReader(ruleBlock)))); } catch(Exception e) {
	 * System.out.println("Parser Error :\n"+e); return; }
	 * System.out.println("My knowledge has "+myKnowledge.size()+
	 * " statements"); //OntModelSpec rulesSpec=new OntModelSpec(getJenaMode());
	 * System.out.println("A"); //OntModelSpec rulesSpec=getJenaMode();
	 * OntModelSpec ontModelSpecs=getBaseModelSpec(); System.out.println("B");
	 * GenericRuleReasoner reasoner=new GenericRuleReasoner(tempRules);
	 * reasoner.setMode(GenericRuleReasoner.BACKWARD);
	 * reasoner.setOWLTranslation(false);
	 * //reasoner.setTransitiveClosureCaching(true);
	 * ontModelSpecs.setReasoner(reasoner); System.out.println("C");
	 * ontModelSpecs.setReasoner(reasoner); System.out.println("D");
	 * myPreProcessedKnowledge=ModelFactory.createOntologyModel(ontModelSpecs);
	 * myPreProcessedKnowledge.add(myBasicKnowledge);
	 * myPreProcessedKnowledge.reset(); System.out.println("E");
	 * myKnowledge=myPreProcessedKnowledge;System.out.println(
	 * "After Pre processing rules,  my knowledge get to a minimum of"
	 * +myKnowledge.size()+ " statements");
	 * 
	 * //myPreProcessedKnowledge=myBasicKnowledge;
	 * 
	 * status=2; validStatus=2; }
	 */

	/**
	 * Standard entailments as specified by Language + Reasoning mode
	 * 
	 */
	/*
	 * private void makeOntoKnowledge() {
	 * 
	 * if(jenaReasoningLevel.equalsIgnoreCase("None")) {
	 * myOntoKnowledge=myPreProcessedKnowledge; myKnowledge=myOntoKnowledge;
	 * System.out.println("This was preatty easy..."); status=3; validStatus=3;
	 * System.out.println("My knowledge get to "+myKnowledge.size()+
	 * " statements"); return; }
	 * System.out.println("Making ontoknowledge: Level= "
	 * +jenaReasoningLevel+";");
	 * //myOntoKnowledge=ModelFactory.createOntologyModel(getJenaMode()); String
	 * reasonerURI="http://localhost:8080";
	 * if(jenaReasoningLevel.equalsIgnoreCase("DIG:8080") ||
	 * jenaReasoningLevel.equalsIgnoreCase("DIG:Ask")) {
	 * if(jenaReasoningLevel.equalsIgnoreCase("DIG:Ask")) {
	 * reasonerURI=JOptionPane.showInputDialog("Plese specify reasoner URI");
	 * if(reasonerURI==null) reasonerURI="http://localhost:8080"; }
	 * 
	 * Model cModel = ModelFactory.createDefaultModel(); Resource conf =
	 * cModel.createResource(); conf.addProperty(
	 * ReasonerVocabulary.EXT_REASONER_URL, cModel.createResource( reasonerURI )
	 * );
	 * 
	 * // create the reasoner factory and the reasoner DIGReasonerFactory drf =
	 * (DIGReasonerFactory) ReasonerRegistry.theRegistry() .getFactory(
	 * DIGReasonerFactory.URI ); DIGReasoner r = (DIGReasoner) drf.create( conf
	 * );
	 * 
	 * // now make a model OntModelSpec spec = new OntModelSpec(
	 * OntModelSpec.OWL_DL_MEM ); spec.setReasoner( r ); myOntoKnowledge =
	 * ModelFactory.createOntologyModel( spec, null );
	 * 
	 * }
	 * 
	 * else { System.out.println("Jena mode : "+getJenaMode());
	 * myOntoKnowledge=ModelFactory.createOntologyModel(getJenaMode());
	 * //myOntoKnowledge=new OntModelImpl(getJenaMode()); }
	 * 
	 * myOntoKnowledge.add(myPreProcessedKnowledge); myOntoKnowledge.rebind();
	 * myOntoKnowledge.prepare(); myKnowledge=myOntoKnowledge; status=3;
	 * validStatus=3;
	 * System.out.println("My knowledge get to "+myKnowledge.size()+
	 * " statements"); }
	 */

	/**
	 * Additional rules user defined
	 */
	/*
	 * private void makePostProcessedKnowledge() { CommonMemory
	 * commonMemory=RDFScape.getCommonMemory();
	 * 
	 * if(postRuleList.size()==0) { myKnowledge=myOntoKnowledge; status=4;
	 * validStatus=4; System.out.println("This was pretty easy...");
	 * System.out.println("My knowledge get to "+myKnowledge.size()+
	 * " statements"); return; } String[]
	 * namespaces=commonMemory.getNamespaces(); String ruleBlock=new String();
	 * for (int i = 0; i < namespaces.length; i++) { String
	 * tempPrefix=commonMemory.getNamespacePrefix(namespaces[i]);
	 * if(tempPrefix!=null)
	 * ruleBlock=ruleBlock.concat("@prefix "+tempPrefix+": "
	 * +"<"+namespaces[i]+">\n"); } ruleBlock=ruleBlock.concat("\n\n"); for
	 * (Iterator iter = postRuleList.iterator(); iter.hasNext();) {
	 * InfRuleObject rule = (InfRuleObject) iter.next(); if(rule.isActive()) {
	 * ruleBlock=ruleBlock.concat(rule.getRule()+"\n\n"); }
	 * 
	 * }
	 * 
	 * System.out.println("Your rules...\n"+ruleBlock); List tempRules; try {
	 * tempRules=Rule.parseRules(Rule.rulesParserFromReader(new
	 * BufferedReader(new StringReader(ruleBlock)))); } catch(Exception e) {
	 * System.out.println("Parser Error :\n"+e); return; }
	 * 
	 * //OntModelSpec rulesSpec=new OntModelSpec(getJenaMode()); OntModelSpec
	 * rulesSpec=getJenaMode(); GenericRuleReasoner reasoner=new
	 * GenericRuleReasoner(tempRules,rulesSpec.getReasonerFactory());
	 * rulesSpec.setReasoner(reasoner);
	 * myKnowledge=ModelFactory.createOntologyModel(rulesSpec);
	 * myKnowledge.add(myOntoKnowledge); //myKnowledge.rebind();
	 * System.out.println("My knowledge get to "+myKnowledge.size()+
	 * " statements");
	 * 
	 * //myPreProcessedKnowledge=myBasicKnowledge;
	 * 
	 * status=4; validStatus=4; //String
	 * header="@include <"+getSelectedBaseReasonerLevel()+">\n"; /* String
	 * header=new String(); String[] namespaces=commonMemory.getNamespaces();
	 * for (int i = 0; i < namespaces.length; i++) { String
	 * tempPrefix=commonMemory.getNamespacePrefix(namespaces[i]);
	 * if(tempPrefix!=null)
	 * header=header.concat("@prefix "+tempPrefix+": "+"<"+namespaces[i]+">\n");
	 * } header=header.concat("\n\n"); for(int i=0;i<rulesArray.length ;i++) {
	 * header=header.concat(rulesArray[i].getRule()+"\n\n"); }
	 * System.out.println("-->"+header); List tempRules; try {
	 * tempRules=Rule.parseRules(Rule.rulesParserFromReader(new
	 * BufferedReader(new StringReader(header)))); } catch(Exception e) {
	 * System.out.println("Parser Error :\n"+e); return; }
	 * 
	 * //OntModelSpec rulesSpec=new OntModelSpec(getJenaMode()); OntModelSpec
	 * rulesSpec=getJenaMode(); GenericRuleReasoner reasoner=new
	 * GenericRuleReasoner(tempRules,rulesSpec.getReasonerFactory());
	 * rulesSpec.setReasoner(reasoner);
	 * myKnowledge=ModelFactory.createOntologyModel(rulesSpec,myOntoKnowledge);
	 */
	/*
	 * }
	 */

	/*
	 * private void switchLanguageLevel(String s) {
	 * if(s.equalsIgnoreCase(ontologyLanguage)) return; else {
	 * ontologyLanguage=s; if(validStatus>2) validStatus=2; if(targetStatus<3)
	 * targetStatus=3;
	 * System.out.println("JenaWrapperStatus: S:"+status+" V:"+validStatus
	 * +"T: "+targetStatus); } }
	 */

	private void switchJenaLevel(String s) {
		if (s.equalsIgnoreCase(jenaReasoningLevel))
			return;
		else {
			jenaReasoningLevel = s;

			if (validStatus > 2)
				validStatus = 2;
			if (targetStatus < 3)
				targetStatus = 3;
			System.out.println("JenaWrapperStatus: S:" + status + " V:"
					+ validStatus + "T: " + targetStatus);

		}
	}

	/**
	 * Asserts wether a rule is valid or not for this reasoner
	 * 
	 * @param rule
	 *            the rule to be validate
	 * @return null is the rule is vallid. Otherwise, it returns an error
	 *         message (String)
	 */
	public String validateRule(String rule) {
		Rule parsedRule = null;

		try {
			parsedRule = Rule.parseRule(rule);
		} catch (Exception e) {
			return e.getMessage();
		}
		if (!parsedRule.isBackward()) {
			return ("Backward rules only, please...");
		}
		return null;
	}

	public ArrayList getAvailableQueryManagers() {
		ArrayList myMethodList = new ArrayList();
		try {
			myMethodList.add(new RDQLQueryEngine());
			myMethodList.add(new ClassQueryEngine());
			myMethodList.add(new StringQueryEngine());
			myMethodList.add(new SPARQLQueryEngine());
		} catch (Exception e) {
			System.out
					.println("I got confused while setting up query facilities...");
		}
		return myMethodList;
	}

	public AbstractQueryResultTable makeRDQLQuery(String queryString) {
		ResultSet queryResults = null;
		QueryExecution qe = null;
		Query query = null;

		JenaQueryResultTable myResultTable = new JenaQueryResultTable();
		Vector result = new Vector();
		System.out.println("Ah");
		try {
			System.out.println("Making RDQL query:");
			System.out.println(queryString);
			System.out.println("On a model of at least " + myKnowledge.size()
					+ " statements");
			query = QueryFactory.create(queryString, Syntax.syntaxRDQL);
			qe = QueryExecutionFactory.create(query, myKnowledge);
			queryResults = qe.execSelect();
			System.out.println(4);

		} catch (QueryException e) {
			// System.out.println("Aa");

			RDFScape.warn(e.getMessage()); // TODO maybe we should have some
											// flag or delegate this...

			// System.out.println("Ab");
			return myResultTable;
		}
		// System.out.println("C");
		List queryVariables = query.getResultVars();
		// System.out.println("D");
		int i = 0;
		// System.out.println("RDQL query yielded "+queryResults.getRowNumber()+" results");
		for (; queryResults.hasNext();) {
			QuerySolution sol = queryResults.nextSolution();
			int j = 0;
			// System.out.println("E");
			for (Iterator iterator = queryVariables.iterator(); iterator
					.hasNext();) {
				// System.out.println("F");
				String var = (String) iterator.next();
				RDFNode tempNode = (RDFNode) (sol.get(var));
				// String tempString="";
				// if(tempNode!=null) tempString=tempNode.toString();

				// System.out.println("->"+res.get(var).getClass());

				myResultTable.addObject(tempNode, i, j);
				myResultTable.addVar(var, j);

				j++;
			}

			i++;
		}
		System.out.println("Result table: " + myResultTable.getRowCount()
				+ " x " + myResultTable.getColumnCount());
		return myResultTable;
	}

	public AbstractQueryResultTable getLeftOfNode(Object node) {
		JenaQueryResultTable myAnswer = new JenaQueryResultTable();
		StmtIterator statements = myKnowledge.listStatements(null, null,
				(RDFNode) node);
		putStmtIteratorToTable(statements, myAnswer, true, false);
		return myAnswer;
	}

	public AbstractQueryResultTable getRightOfNode(Object node) {
		JenaQueryResultTable myAnswer = new JenaQueryResultTable();
		RDFNode myNode = (RDFNode) node;
		if (myNode.isResource()) {
			StmtIterator statements = myKnowledge.listStatements(
					(Resource) node, null, (RDFNode) null);
			putStmtIteratorToTable(statements, myAnswer, false, true);

		}
		return myAnswer;

	}

	private void putStmtIteratorToTable(StmtIterator statements,
			JenaQueryResultTable table, boolean includeSubject,
			boolean includeObject) {

		int y = 0;
		int subjectCol = 0;
		int propertyCol = 1;
		int objectCol = 2;
		if (!includeSubject) {
			propertyCol = 0;
			objectCol = 1;
		}
		while (statements.hasNext()) {
			Statement myStatement = statements.nextStatement();
			Resource subject = myStatement.getSubject();
			RDFNode object = myStatement.getObject();
			Property predicate = myStatement.getPredicate();
			if (includeSubject)
				table.addObject(subject, y, subjectCol);
			table.addObject(predicate, y, propertyCol);
			if (includeObject)
				table.addObject(object, y, objectCol);
			y++;

		}

	}

	public static String getNamespaceFromURI(String uri) {
		Resource tempResource = ResourceFactory.createResource(uri);
		return tempResource.getNameSpace();
	}

	public String getRDFLabelForURI(String uri) {
		Resource myResource = ResourceFactory.createResource(uri);
		Property rdfsLabel = ResourceFactory
				.createProperty("http://www.w3.org/2000/01/rdf-schema#label");
		NodeIterator myLabels = myKnowledge.listObjectsOfProperty(myResource,
				rdfsLabel);
		if (myLabels.hasNext()) {
			RDFNode myNode = myLabels.nextNode();
			if (myNode.isLiteral()) {
				return ((Literal) myNode).getString();
			}
		}
		return null;
	}

	public String getShortLabelForURI(String uri) {
		Resource myURI = ResourceFactory.createResource(uri);
		String namespace = myURI.getNameSpace();
		String localName = myURI.getLocalName();
		String prefix = RDFScape.getCommonMemory()
				.getNamespacePrefix(namespace);
		if ((prefix != null) && (localName != null))
			return prefix + ":" + localName;
		else
			return null;
	}

	public String[][] getDatatypeAttributeBox(String uri) {
		String[][] answer = new String[0][2];
		ArrayList attList = new ArrayList();
		StmtIterator statements = myKnowledge.listStatements(ResourceFactory
				.createResource(uri), null, (RDFNode) null);
		while (statements.hasNext()) {
			Statement statement = statements.nextStatement();
			RDFNode target = statement.getObject();
			if (target.isLiteral()) {
				String property = statement.getPredicate().getURI();
				String value = ((Literal) statement.getObject()).getValue()
						.toString();
				if (getRDFLabelForURI(property) != null)
					property = getRDFLabelForURI(property);
				else if (getShortLabelForURI(property) != null)
					property = getShortLabelForURI(property);
				String[] tempEntry = { property, value };
				attList.add(tempEntry);

			}
		}
		return (String[][]) attList.toArray(answer);
	}

	public String[] getClassURIList() {
		String[] answer = new String[0];
		if (myKnowledge == null)
			return answer;
		System.out.println("+");
		// ResIterator resList=
		// myKnowledge.listSubjectsWithProperty(ResourceFactory.createProperty(""),ResourceFactory.createResource(""));
		Iterator myIterator = null;
		ArrayList classes = new ArrayList();
		try {
			myIterator = ((OntModel) myKnowledge).listNamedClasses();
		} catch (Exception e) {
			return answer;
		}
		while (myIterator.hasNext()) {
			Resource currentClass = (Resource) myIterator.next();
			System.out.println("-->" + currentClass.getURI());
			classes.add(currentClass.getURI());
		}
		return (String[]) classes.toArray(answer);
	}

	public AbstractQueryResultTable makeSPAQRLQuery(String queryString) {
		JenaQueryResultTable myResultTable = new JenaQueryResultTable();

		if (myKnowledge == null)
			return myResultTable;
		com.hp.hpl.jena.query.Query query = QueryFactory.create(queryString);
		com.hp.hpl.jena.query.QueryExecution qexec = QueryExecutionFactory
				.create(query, myKnowledge);
		ResultSet queryResults = null;
		try {
			queryResults = qexec.execSelect();
		} catch (QueryParseException e) {
			System.out.println("Problems in SPARQL query");
			RDFScape.warn(e.getMessage());
			e.printStackTrace();
			return myResultTable;
		} // finally {qexec.close();}

		if (queryResults != null) {
			List queryVariables = query.getResultVars();
			System.out.println("Vars # " + queryVariables.size());
			int i = 0;
			// System.out.println("SPQRL query yielded "+queryResults.getRowNumber()+" results");
			while (queryResults.hasNext()) {

				QuerySolution res = (QuerySolution) queryResults.next();
				int j = 0;
				System.out.println("E");
				for (Iterator iterator = queryVariables.iterator(); iterator
						.hasNext();) {
					// System.out.println("F");
					String var = (String) iterator.next();
					RDFNode tempNode = (RDFNode) (res.get(var));
					// String tempString="";
					// if(tempNode!=null) tempString=tempNode.toString();

					// System.out.println("->"+res.get(var).getClass());

					myResultTable.addObject(tempNode, i, j);
					myResultTable.addVar(var, j);

					j++;
				}

				i++;
			}

		}
		System.out.println("Result table: " + myResultTable.getRowCount()
				+ " x " + myResultTable.getColumnCount());
		return myResultTable;
	}

	public void addDataStatement(String uri, String prop, String labelString) {
		myBasicKnowledge.add(ResourceFactory.createResource(uri),
				ResourceFactory.createProperty(prop), ResourceFactory
						.createPlainLiteral(labelString));
		validStatus = 1;

	}

	private String makeRuleBlock(String levelToInclude) {
		String[] namespaces = RDFScape.getCommonMemory().getNamespaces();
		String ruleBlock = new String();
		for (int i = 0; i < namespaces.length; i++) {
			String tempPrefix = RDFScape.getCommonMemory().getNamespacePrefix(
					namespaces[i]);
			if (tempPrefix != null)
				ruleBlock = ruleBlock.concat("@prefix " + tempPrefix + ": "
						+ "<" + namespaces[i] + ">\n");
		}
		if (levelToInclude != null)
			ruleBlock = ruleBlock.concat("@include <" + levelToInclude + ">\n");
		ruleBlock = ruleBlock.concat("\n\n");
		for (Iterator iter = preRuleList.iterator(); iter.hasNext();) {
			InfRuleObject rule = (InfRuleObject) iter.next();
			if (rule.isActive()) {
				ruleBlock = ruleBlock.concat(rule.getRule() + "\n\n");
			}

		}
		ruleBlock = ruleBlock.concat("\n\n");
		for (Iterator iter = postRuleList.iterator(); iter.hasNext();) {
			InfRuleObject rule = (InfRuleObject) iter.next();
			if (rule.isActive()) {
				ruleBlock = ruleBlock.concat(rule.getRule() + "\n\n");
			}

		}
		System.out.println("Your rules...\n" + ruleBlock);
		return ruleBlock;
	}

	public void addTypedDataStatement(String uri, String property,
			Object value, String type) {
		System.out.println("Trying to get " + uri + " " + property + " "
				+ value + " " + " " + type);
		/*
		 * Literal myLiteral=null; if(type.equalsIgnoreCase("String")) {
		 * myLiteral=ResourceFactory.createTypedLiteral(((String)value),
		 * XSDDatatype.XSDstring); } else if(type.equalsIgnoreCase("Double")) {
		 * myLiteral
		 * =ResourceFactory.createTypedLiteral(Double.toString(((Double)value)),
		 * XSDDatatype.XSDdouble); } else if(type.equalsIgnoreCase("Boolean")) {
		 * myLiteral
		 * =ResourceFactory.createTypedLiteral(Boolean.toString(((Boolean
		 * )value)), XSDDatatype.XSDboolean); } else
		 * if(type.equalsIgnoreCase("Integer")) {
		 * myLiteral=ResourceFactory.createTypedLiteral
		 * (Integer.toString(((Integer)value)), XSDDatatype.XSDinteger); } else
		 * {
		 * System.out.println("Unable to create literal for "+value+" of type "
		 * +type); return; }
		 * myBasicKnowledge.add(ResourceFactory.createResource(uri),
		 * ResourceFactory.createProperty(property), myLiteral);
		 */
		myBasicKnowledge.add(ResourceFactory.createResource(uri),
				ResourceFactory.createProperty(property), ResourceFactory
						.createTypedLiteral(value));
		// TODO Auto-generated method stub

	}
}
