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
		p.put( propKey, (String)f.get(o) );
        } catch (IllegalAccessException iae) {
            iae.printStackTrace();
        }
		return p;
	}
	
	public void add(Properties p) {
		try{
			p.put(propKey,f.get(o));
		}catch(Exception e){e.printStackTrace();}
	}

		
	public void setProps(Properties p) {
		try {
		if ( p.containsKey( propKey ) ) {
			String val = p.getProperty( propKey );
			if ( val != null )
				f.set(o, val);
		}
        } catch (IllegalAccessException iae) {
            iae.printStackTrace();
        }
	}
}
