package cytoscape.data.readers;

import static cytoscape.data.readers.TextTableReader.ObjectType.EDGE;

import java.util.List;

/**
 * Text table <---> CyAttribute & CyNetwork mapping parameters for network
 * table.
 * 
 * @since Cytoscape 2.4
 * @version 0.9
 * 
 * @author Keiichiro Ono
 * 
 */
public class NetworkTableMappingParameters extends AttributeMappingParameters {

	private static final String DEF_INTERACTION = "pp";
	
	private final int source;
	private final int target;
	private final int interaction;
	
	private final String defInteraction;

	public NetworkTableMappingParameters(List<String> delimiters,
			String listDelimiter, String[] attributeNames,
			byte[] attributeTypes, boolean[] importFlag, int source,
			int target, int interaction, final String defInteraction) throws Exception {
		super(EDGE, delimiters, listDelimiter, -1, null, null, attributeNames,
				attributeTypes, importFlag);

		this.source = source;
		this.target = target;
		this.interaction = interaction;
		
		this.defInteraction = defInteraction;
	}

	public int getSourceIndex() {
		return source;
	}

	public int getTargetIndex() {
		return target;
	}

	public int getInteractionIndex() {
		return interaction;
	}
	
	public String getDefaultInteraction() {
		if(defInteraction == null) {
			return DEF_INTERACTION;
		} else {
			return defInteraction;
		}
	}

}
