package cytoscape.coreplugins.biopax.util;

import java.util.*;


/**
* BioPAX Constants.
*
* @deprecated
*/

public class BioPax3Constants {
	/** BioPAX Classes:	 */
	public static final String ENTITY = "entity";
	public static final String PHYSICAL_ENTITY = "physicalEntity";
	public static final String COMPLEX = "complex";
	public static final String DNA = "dna";
	public static final String PROTEIN = "protein";
	public static final String RNA = "rna";
	public static final String SMALL_MOLECULE = "smallMolecule";
	public static final String INTERACTION = "interaction";
	public static final String PHYSICAL_INTERACTION = "physicalInteraction";
	public static final String CONTROL = "control";
	public static final String CATALYSIS = "catalysis";
	public static final String MODULATION = "modulation";
	public static final String CONVERSION = "conversion";
	public static final String BIOCHEMICAL_REACTION = "biochemicalReaction";
	public static final String TRANSPORT_WITH_BIOCHEMICAL_REACTION = "transportWithBiochemicalReaction";
	public static final String COMPLEX_ASSEMBLY = "complexAssembly";
	public static final String TRANSPORT = "transport";
	public static final String PATHWAY = "pathway";
	public static final String PHOSPHORYLATION_SITE = "phosphorylation site";
	public static final String PROTEIN_PHOSPHORYLATED = "protein-phosphorylated";

	public static final String GENE = "gene";
	public static final String GENETIC_INTERACTION = "geneticInteraction";
	public static final String MOLECULAR_INTERACTION = "molecularInteraction";
	
	public static final String TEMPLATE_REACTION = "templateReaction";
	public static final String TEMPLATE_REACTION_REGULATION = "templateReactionRegulation";
	public static final String DEGRADATION = "degradation";
	
	private static final HashMap<String,String> superClass = new HashMap<String,String>(25);
	private static final HashMap<String,ArrayList<String>> subClasses = new HashMap<String,ArrayList<String>>(25);
	
	public static String getBiopaxSuperclass(String biopaxClass) {
		return superClass.get(biopaxClass);
	}
	
	public static List<String> getBiopaxSubclasses(String biopaxClass) { 
		return subClasses.get(biopaxClass); 
	}
	
	/**
	 * BioPAX Namespace Prefix.
	 */
	public static final String BIOPAX_NAMESPACE_PREFIX = "bp";

	/**
	 * BioPAX Level 1 Namespace URI.
	 */
	public static final String BIOPAX_LEVEL_1_NAMESPACE_URI = "http://www.biopax.org/release/biopax-level1.owl#";

	/**
	 * BioPAX Level 2 Namespace URI.
	 */
	public static final String BIOPAX_LEVEL_2_NAMESPACE_URI = "http://www.biopax.org/release/biopax-level2.owl#";

	/**
	 * Set of All Physical Entity Types.
	 */
	private static Set physicalEntitySet = new HashSet();

	/**
	 * Set of All Interaction Types.
	 */
	private static Set interactionSet = new HashSet();

	/**
	 * Set of All Pathway Types.
	 */
	private static Set pathwaySet = new HashSet();

	/**
	 * Set of All Control Interactions
	 */
	private static Set controlInteractionSet = new HashSet();

	/**
	 * Set of All Conversion Interactions
	 */
	private static Set conversionInteractionSet = new HashSet();

	/**
	 * Constructor.
	 */
	static {
		// NOTE:
		//
		// IF SOMETHING CHANGES HERE, BioPaxPlainEnglish.java
		// SHOULD GET UPDATED TOO.
		//
	    superClass.put(PATHWAY,ENTITY);
	    superClass.put(INTERACTION,ENTITY);
	    superClass.put(PHYSICAL_ENTITY,ENTITY);
	    superClass.put(GENE,ENTITY);
	    superClass.put(CONTROL,INTERACTION);
	    superClass.put(CONVERSION,INTERACTION);
	    superClass.put(GENETIC_INTERACTION,INTERACTION);
	    superClass.put(MOLECULAR_INTERACTION,INTERACTION);
	    superClass.put(TEMPLATE_REACTION,INTERACTION);
	    superClass.put(CATALYSIS,CONTROL);
	    superClass.put(MODULATION,CONTROL);
	    superClass.put(TEMPLATE_REACTION_REGULATION,CONTROL);
	    superClass.put(BIOCHEMICAL_REACTION,CONVERSION);
	    superClass.put(COMPLEX_ASSEMBLY,CONVERSION);
	    superClass.put(DEGRADATION,CONVERSION);
	    superClass.put(TRANSPORT,CONVERSION);
	    superClass.put(TRANSPORT_WITH_BIOCHEMICAL_REACTION,CONVERSION);
	    superClass.put(COMPLEX,PHYSICAL_ENTITY);
	    superClass.put(DNA,PHYSICAL_ENTITY);
	    superClass.put(PROTEIN,PHYSICAL_ENTITY);
	    superClass.put(RNA,PHYSICAL_ENTITY);
	    superClass.put(SMALL_MOLECULE,PHYSICAL_ENTITY);
	    for (String sub: superClass.keySet()) subClasses.put(sub, new ArrayList<String>());
	    subClasses.put(ENTITY, new ArrayList<String>());
	    for (String sub: superClass.keySet()) {
	    	subClasses.get(superClass.get(sub)).add(sub);
	    }

		//  Initialize Physical Entity Set
		physicalEntitySet.add(PHYSICAL_ENTITY);
		physicalEntitySet.add(COMPLEX);
		physicalEntitySet.add(DNA);
		physicalEntitySet.add(PROTEIN);
		physicalEntitySet.add(RNA);
		physicalEntitySet.add(SMALL_MOLECULE);

		//  Initialize Interaction Set
		interactionSet.add(INTERACTION);
		interactionSet.add(PHYSICAL_INTERACTION);
		interactionSet.add(CONTROL);
		interactionSet.add(CATALYSIS);
		interactionSet.add(MODULATION);
		interactionSet.add(CONVERSION);
		interactionSet.add(BIOCHEMICAL_REACTION);
		interactionSet.add(TRANSPORT_WITH_BIOCHEMICAL_REACTION);
		interactionSet.add(COMPLEX_ASSEMBLY);
		interactionSet.add(TRANSPORT);

		//  Initialize Control Interaction Subtypes
		controlInteractionSet.add(CONTROL);
		controlInteractionSet.add(CATALYSIS);
		controlInteractionSet.add(MODULATION);

		//  Initialize Conversion Interaction Subtypes
		conversionInteractionSet.add(CONVERSION);
		conversionInteractionSet.add(BIOCHEMICAL_REACTION);
		conversionInteractionSet.add(TRANSPORT_WITH_BIOCHEMICAL_REACTION);
		conversionInteractionSet.add(COMPLEX_ASSEMBLY);
		conversionInteractionSet.add(TRANSPORT);

		//  Intialize Pathway Set
		pathwaySet.add(PATHWAY);
	}

	/**
	 * Determines if the Specified Element is of type:  physical entity.
	 *
	 * @param elementName Element Name.
	 * @return boolean value.
	 */
	public static boolean isPhysicalEntity(String elementName) {
		return physicalEntitySet.contains(elementName);
	}

	/**
	 * Determines if the Specified Element is of type:  interaction.
	 *
	 * @param elementName Element Name.
	 * @return boolean value.
	 */
	public static boolean isInteraction(String elementName) {
		return interactionSet.contains(elementName.trim());
	}

	/**
	 * Determines if the Specified Element is of type:  pathway.
	 *
	 * @param elementName Element Name.
	 * @return boolean value.
	 */
	public static boolean isPathway(String elementName) {
		return pathwaySet.contains(elementName.trim());
	}

	/**
	 * Determines if the Specified Element is of type:  control.
	 *
	 * @param elementName Element Name.
	 * @return boolean value.
	 */
	public static boolean isControlInteraction(String elementName) {
		return controlInteractionSet.contains(elementName.trim());
	}

	/**
	 * Determines if the Specified Element is of type:  conversion.
	 *
	 * @param elementName Element Name.
	 * @return boolean value.
	 */
	public static boolean isConversionInteraction(String elementName) {
		return conversionInteractionSet.contains(elementName.trim());
	}

	/**
	 * Gets a Set of all Physical Entity Names
	 *
	 * @return Set of Strings
	 */
	public static Set getPhysicalEntitySet() {
		return physicalEntitySet;
	}

	/**
	 * Gets a Set of all Interaction Entity Names.
	 *
	 * @return Set of Strings.
	 */
	public static Set getInteractionSet() {
		return interactionSet;
	}
}
