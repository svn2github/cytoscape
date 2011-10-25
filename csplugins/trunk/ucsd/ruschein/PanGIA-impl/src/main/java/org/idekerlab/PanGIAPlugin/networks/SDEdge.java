package org.idekerlab.PanGIAPlugin.networks;

public abstract class SDEdge extends SEdge{

	private double value;
	
	public SDEdge(String s1, String s2, double value)
	{
		super(s1,s2);
		this.value = value;
	}
	
	public double value()
	{
		return value;
	}
	
	public SDEdge(SDEdge inter)
	{
		super(inter);
		this.value = inter.value;
	}
	
	public void setValue(double val)
	{
		this.value = val;
	}
}
