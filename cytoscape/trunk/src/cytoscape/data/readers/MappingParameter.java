package cytoscape.data.readers;

import java.util.List;

import cytoscape.data.readers.TextTableReader.ObjectType;

public interface MappingParameter {
	public int getColumnCount();

	public int getKeyIndex();

	public ObjectType getObjectType();

	public String getMappingAttribute();

	public boolean[] getImportFlag();

	public List<Integer> getAliasIndexList();

	public String[] getAttributeNames();

	public byte[] getAttributeTypes();

	public String getListDelimiter();

}
