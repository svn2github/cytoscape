package cytoscape.work.internal.util;

import cytoscape.work.Progressable;

public class Interfaces
{
	public static boolean implementsProgressable(final Object object)
	{
		final Class[] interfaces = object.getClass().getInterfaces();
		for (int i = 0; i < interfaces.length; i++)
			if (interfaces[i].equals(Progressable.class))
				return true;
		return false;
	}

}
