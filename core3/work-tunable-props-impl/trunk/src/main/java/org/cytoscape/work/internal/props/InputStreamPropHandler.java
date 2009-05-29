package org.cytoscape.work.internal.props;

import java.lang.reflect.Field;
import java.util.Properties;
import java.io.InputStream;

import org.cytoscape.work.Tunable;


public class InputStreamPropHandler extends AbstractPropHandler {
	
	public InputStreamPropHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
	}

	
	public Properties getProps() {
		Properties p = new Properties();
		try{
			p.setProperty(propKey, ((InputStream)f.get(o)).toString());
		}catch(Exception e){e.printStackTrace();}
		return p;
	}

	public void setProps(Properties p) {
		try {
			if ( p.containsKey(propKey) ) {
				String val = p.getProperty(propKey);
				if ( val != null )
					f.set(o, val);
			}
        } catch (IllegalAccessException iae) {iae.printStackTrace();}
	}
}
