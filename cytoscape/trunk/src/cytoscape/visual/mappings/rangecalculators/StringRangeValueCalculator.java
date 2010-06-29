package cytoscape.visual.mappings.rangecalculators;

import java.util.List;

import cytoscape.visual.mappings.RangeValueCalculator;

public class StringRangeValueCalculator implements RangeValueCalculator<String> {

	
	public String getRange(Object attrValue) {
		if (attrValue instanceof List<?>) {
			final List<?> list = (List<?>) attrValue;
			int idx = 1;
			final StringBuilder buf = new StringBuilder();
			final int size = list.size();

			for (final Object attrSubValue : list) {
				buf.append(attrSubValue);

				if (idx != size)
					buf.append("\n");
				idx++;
			}

			return buf.toString();
		}

		// OK, try returning the attrValue itself
		if (attrValue instanceof String)
			return (String) attrValue;
		else
			return attrValue.toString();
	}


	public boolean isCompatible(Class<?> type) {
		if(String.class.isAssignableFrom(type))
			return true;
		else
			return false;
	}
}
