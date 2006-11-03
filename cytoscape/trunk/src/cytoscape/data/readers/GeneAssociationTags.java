package cytoscape.data.readers;

/**
 * Reserved keywords for Gene Association files.<br>
 * <p>
 * For more information about GA format, please visit:<br>
 * http://www.geneontology.org/GO.annotation.shtml#file
 * </p>
 * @author kono
 *
 */
public enum GeneAssociationTags {
	DB("DB"), DB_OBJECT_ID("DB_Object_ID"), DB_OBJECT_SYMBOL("DB_Object_Symbol"), QUALIFIER("Qualifier"), GO_ID("GO ID"), DB_REFERENCE("DB:Reference"),
	EVIDENCE("Evidence"), WITH_OR_FROM("With (or) From"), ASPECT("Aspect"), DB_OBJECT_NAME("DB_Object_Name"),
	DB_OBJECT_SYNONYM("DB_Object_Synonym"), DB_OBJECT_TYPE("DB_Object_Type"), TAXON("Taxon"), DATE("Date"),
	ASSIGNED_BY("Assigned_by");

	private String tag;
	
	private GeneAssociationTags (String tag) {
		this.tag = tag;
	}
	
	public String toString() {
		return tag;
	}
	
	
	/**
	 * Since this enum represents a column names, we can find the index of the tag
	 * by using this method.
	 * <br> 
	 * @return
	 */
	public int getPosition() {
		GeneAssociationTags[] tags = values();
		for(int i=0; i<tags.length; i++) {
			if(tags[i] == this) {
				return i;
			}
		}
		return 0;
	}
}
