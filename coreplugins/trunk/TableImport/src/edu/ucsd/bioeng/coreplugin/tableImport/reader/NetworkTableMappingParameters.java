package edu.ucsd.bioeng.coreplugin.tableImport.reader;

import java.util.List;
import static edu.ucsd.bioeng.coreplugin.tableImport.reader.TextTableReader.ObjectType.*;

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
			Byte[] attributeTypes, Byte[] listAttributeTypes, boolean[] importFlag,
			int source, int target, int interaction, final String defInteraction) throws Exception {
		super(EDGE, delimiters, listDelimiter, -1, null, null, attributeNames,
				attributeTypes, listAttributeTypes, importFlag);

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
