package org.idekerlab.PanGIAPlugin.networks;

public final class UndirectedSEdge extends SEdge{

	public UndirectedSEdge(SEdge inter)
	{
		super(inter);
	}
	
	public UndirectedSEdge(String s1, String s2)
	{
		super(s1,s2);
	}
	
	public boolean isDirected()
	{
		return false;
	}
	
	public boolean equals(Object inter)
	{
		if (inter == null) return false;
		if (inter instanceof SEdge)
		{
			SEdge other = (SEdge)inter;
			if ((i1.equals(other.i1) && i2.equals(other.i2)) || (i1.equals(other.i2) && i2.equals(other.i1))) return true;
			else return false;
		}else return false;
	}
}
