package edu.ucsd.bioeng.coreplugin.tableImport.reader;

import java.util.List;

import edu.ucsd.bioeng.coreplugin.tableImport.reader.TextTableReader.ObjectType;


public interface MappingParameter {
	public int getColumnCount();

	public int getKeyIndex();

	public ObjectType getObjectType();

	public String getMappingAttribute();

	public boolean[] getImportFlag();

	public List<Integer> getAliasIndexList();

	public String[] getAttributeNames();

	public Byte[] getAttributeTypes();

	public String getListDelimiter();

}
