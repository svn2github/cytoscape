

package org.example.tunable.props;

import java.lang.reflect.*;
import java.util.*;
import org.example.tunable.*;

public class StringPropHandler implements PropHandler {

	Field f;
	Object o;
	Tunable t;
	String propKey;

	public StringPropHandler(Field f, Object o, Tunable t) {
		this.f = f;
		this.o = o;
		this.t = t;
		propKey = t.namespace() + "." + f.getName();	
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
