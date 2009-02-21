package org.cytoscape.work.internal.props;

import java.lang.reflect.*;
import java.util.*;
import java.io.File;

import org.cytoscape.work.Tunable;

public class URLPropHandler extends AbstractPropHandler {

	File file;
	
	public URLPropHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
		try{
			file = (File) f.get(o);
		}catch(Exception e){e.printStackTrace();}
	}

	public Properties getProps() {
		Properties p = new Properties();
		p.put( propKey, file.toString());
		return p;
	}
	
	public void add(Properties p) {
		try{
			p.put(propKey,file.toString());
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
