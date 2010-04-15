package cytoscape.visual.parsers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cytoscape.visual.ObjectPosition;
import cytoscape.visual.ObjectPositionImpl;
import cytoscape.visual.Position;

public class ObjectPositionParser implements ValueParser<ObjectPosition> {

	private static final Pattern P = Pattern
			.compile("^([NSEWC]{1,2}+),([NSEWC]{1,2}+),([clr]{1}+),(-?\\d+(.\\d+)?),(-?\\d+(.\\d+)?)$");

	@Override
	public ObjectPosition parseStringValue(String value) {
		return parse(value);
	}

	/**
	 * Convert string representation to ObjectPosition object. 
	 * 
	 * @param value
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	private ObjectPosition parse(final String value) {
		final Matcher m = P.matcher(value);

		if (m.matches()) {
			final ObjectPosition p = new ObjectPositionImpl();
			p.setTargetAnchor(Position.parse(m.group(1)));
			p.setAnchor(Position.parse(m.group(2)));
			p.setJustify(Position.parse(m.group(3)));
			p.setOffsetX(Double.parseDouble(m.group(4)));
			p.setOffsetY(Double.parseDouble(m.group(6)));

			return p;
		} else
			return null;
	}

}
