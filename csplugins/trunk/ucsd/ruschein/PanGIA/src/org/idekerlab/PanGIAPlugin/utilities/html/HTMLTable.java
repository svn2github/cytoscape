package org.idekerlab.PanGIAPlugin.utilities.html;

import org.idekerlab.PanGIAPlugin.data.StringTable;

public class HTMLTable extends HTMLTextBlock
{
	public HTMLTable(StringTable st, int border, Alignment al, int width)
	{
		this.add("<TABLE border=\""+border+"\" align=\""+al.toString()+"\" width="+width+">");
		
		if (st.hasColNames())
		{
			String header = "\t";
			
			header += "<TR>";
			if (st.hasRowNames()) header+="<TH></TH>";
			
			for (int ci=0;ci<st.dim(1);ci++)
				header+="<TH>"+st.getColName(ci)+"</TH>";
			
			header += "</TR>";
			this.add(header);
		}
		
		for (int ri=0;ri<st.dim(0);ri++)
		{
			String line = "\t";
			line += "<TR>";
			
			if (st.hasRowNames()) line+= "<TD>"+st.getRowName(ri)+"</TD>";
			
			for (int ci=0;ci<st.dim(1);ci++)
				line+="<TD>"+st.get(ri,ci)+"</TD>";
			
			line += "</TR>";
			this.add(line);
		}
		
		this.add("</TABLE>");
	}
}
