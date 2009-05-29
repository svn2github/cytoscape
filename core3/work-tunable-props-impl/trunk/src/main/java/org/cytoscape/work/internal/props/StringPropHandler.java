package org.cytoscape.work.internal.props;

import java.lang.reflect.*;
import java.util.*;

import org.cytoscape.work.Tunable;


public class StringPropHandler extends AbstractPropHandler {

	public StringPropHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
	}

	public Properties getProps() {
		Properties p = new Properties();
		try {
			p.setProperty( propKey,f.get(o).toString());
        }catch(IllegalAccessException iae) {iae.printStackTrace();}
		return p;
	}

	
	public void setProps(Properties p) {
		try {
			if ( p.containsKey( propKey ) ) {
				String val = p.getProperty(propKey).toString();
				if ( val != null )
					f.set(o, val);
			}
	    }catch(IllegalAccessException iae) {iae.printStackTrace();}
	}
}
