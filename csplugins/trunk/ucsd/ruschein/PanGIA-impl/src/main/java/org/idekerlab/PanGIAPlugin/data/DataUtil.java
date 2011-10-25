package org.idekerlab.PanGIAPlugin.data;

import java.util.List;

public class DataUtil {
	public static IntVector getSListIs(List<String> sList, StringVector names)
	{
		IntVector is = new IntVector(names.size());
		
		for (int i=0;i<names.size();i++)
		{
			int index = sList.indexOf(names.get(i));
			if (index!=-1) is.add(index);
		}
		
		return is;
	}
	
	public static IntVector getSListIs(List<String> sList, String[] names)
	{
		IntVector is = new IntVector(names.length);
		
		for (int i=0;i<names.length;i++)
		{
			int index = sList.indexOf(names[i]);
			if (index!=-1) is.add(index);
		}
		
		return is;
	}
	
	public static int getSListIs(List<String> sList, String name)
	{
		int index = sList.indexOf(name);
			
		return index;
	}
	
	public static IntVector getSListIs(List<String> sList, List<String> names)
	{
		IntVector is = new IntVector(names.size());
		
		for (int i=0;i<names.size();i++)
		{
			int index = sList.indexOf(names.get(i));
			if (index!=-1) is.add(index);
		}
		
		return is;
	}
}
