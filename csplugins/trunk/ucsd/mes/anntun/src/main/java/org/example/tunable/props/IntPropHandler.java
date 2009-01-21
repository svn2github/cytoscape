

package org.example.tunable.props;

import java.lang.reflect.*;
import java.util.*;
import org.example.tunable.*;

public class IntPropHandler extends AbstractPropHandler {

	public IntPropHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
	}

	
	public Properties getProps() {
		Properties p = new Properties();
		try {
		p.put( propKey, Integer.toString(f.getInt(o)) );
		} catch (IllegalAccessException iae) {
			iae.printStackTrace();
		}
		return p;
	}
		
	public void setProps(Properties p) {
		try {
		if ( p.containsKey( propKey ) ) {
			String val = p.getProperty( propKey );
			if ( val != null )
				f.setInt(o, Integer.valueOf(val));
		}
		} catch (IllegalAccessException iae) {
			iae.printStackTrace();
		}
	}
}
