package org.idekerlab.PanGIAPlugin.utilities.html;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;

public class HTMLHyperBlock extends HTMLBlock
{
	protected List<HTMLBlock> blocks;
	
	public HTMLHyperBlock()
	{
		blocks = new ArrayList<HTMLBlock>();
	}
	
	public HTMLHyperBlock(int numblocks)
	{
		blocks = new ArrayList<HTMLBlock>(numblocks);
	}
	
	public void add(HTMLBlock block)
	{
		this.blocks.add(block);
	}
	
	protected void write(BufferedWriter bw, int depth)
	{
		for (HTMLBlock block : blocks)
			block.write(bw, depth+1);
	}
}
