package org.cytoscape.work.internal.props;

import java.lang.reflect.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import org.cytoscape.work.Tunable;

public class URLPropHandler extends AbstractPropHandler {

	public URLPropHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
	}

	public Properties getProps() {
		Properties p = new Properties();
		try{
			p.setProperty(propKey, f.get(o).toString());
		}catch(IllegalAccessException iae){iae.printStackTrace();}
		return p;
	}
	
	
	public void setProps(Properties p) {
		try {
			if ( p.containsKey( propKey ) ) {
				URL url = (URL)f.get(o);
				String val = p.getProperty(propKey);
				if ( val != null ){
					try {
						url = new URL(val);
						f.set(o, url);
					} catch (MalformedURLException mue){mue.printStackTrace();}
				}
			}
        } catch (IllegalAccessException iae) {iae.printStackTrace();}
	}
}
