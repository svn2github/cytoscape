package org.cytoscape.work.internal.props;

import java.lang.reflect.*;
import java.net.URL;
import java.util.*;

import org.cytoscape.work.AbstractPropHandler;
import org.cytoscape.work.Tunable;

public class URLPropHandler extends AbstractPropHandler {

	URL url;
	
	public URLPropHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
		try{
			url = (URL) f.get(o);
		}catch(Exception e){e.printStackTrace();}
	}

	public Properties getProps() {
		Properties p = new Properties();
		p.put( propKey, url.getFile());
		return p;
	}
	
	public void add(Properties p) {
		
		try{
			//p.put(propKey,url.getFile());
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
