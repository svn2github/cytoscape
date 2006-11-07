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

	private final int source;
	private final int target;
	private final int interaction;

	public NetworkTableMappingParameters(List<String> delimiters,
			String listDelimiter, String[] attributeNames,
			byte[] attributeTypes, boolean[] importFlag, int source,
			int target, int interaction) throws Exception {
		super(EDGE, delimiters, listDelimiter, -1, null, null, attributeNames,
				attributeTypes, importFlag);

		this.source = source;
		this.target = target;
		this.interaction = interaction;
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

}
