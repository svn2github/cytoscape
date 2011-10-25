package org.idekerlab.PanGIAPlugin.networks;

public abstract class SFEdge extends SEdge{

	private float value;
	
	public SFEdge(String s1, String s2, float value)
	{
		super(s1,s2);
		this.value = value;
	}
	
	public float value()
	{
		return value;
	}
	
	public SFEdge(SFEdge inter)
	{
		super(inter);
		this.value = inter.value;
	}
	
	public void setValue(float val)
	{
		this.value = val;
	}
}
