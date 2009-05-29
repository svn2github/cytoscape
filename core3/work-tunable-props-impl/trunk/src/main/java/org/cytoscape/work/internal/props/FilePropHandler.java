package org.cytoscape.work.internal.props;

import java.lang.reflect.Field;
import java.util.Properties;
import java.io.File;
import org.cytoscape.work.Tunable;


public class FilePropHandler extends AbstractPropHandler {

	public FilePropHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
	}

	public Properties getProps() {
		Properties p = new Properties();
		try{
			p.setProperty(propKey,((File)f.get(o)).getPath().toString());
		}catch(Exception e){e.printStackTrace();}
		return p;
	}
		
	public void setProps(Properties p) {
		try {
			if ( p.containsKey( propKey ) ) {
				String val = p.getProperty(propKey);
				if ( val != null )
					f.set(o, new File(val.toString()));	
			}
        } catch (IllegalAccessException iae) {iae.printStackTrace();}
	}
}
