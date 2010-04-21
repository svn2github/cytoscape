package cytoscape.visual.parsers;

import giny.view.ObjectPosition;
import giny.view.Position;
import giny.view.Justification;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ding.view.ObjectPositionImpl;

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
			p.setJustify(Justification.parse(m.group(3)));
			p.setOffsetX(Double.parseDouble(m.group(4)));
			p.setOffsetY(Double.parseDouble(m.group(6)));

			return p;
		} else
			return null;
	}

}
