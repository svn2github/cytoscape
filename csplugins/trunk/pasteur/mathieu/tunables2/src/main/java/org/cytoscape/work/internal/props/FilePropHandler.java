package org.cytoscape.work.internal.props;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Properties;

import java.io.File;

import org.cytoscape.work.AbstractPropHandler;
import org.cytoscape.work.Tunable;


public class FilePropHandler extends AbstractPropHandler {

	File file;
	List<String> paths;
	
	public FilePropHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
		try{
			file = (File) f.get(o);
		}catch(Exception e){e.printStackTrace();}
	}

	public Properties getProps() {
		Properties p = new Properties();
		p.put( propKey, file.getPath());
		return p;
	}
	
	public void add(Properties p) {
		if(file!=null){
			try{
				p.put(propKey,file.getAbsolutePath());
			}catch(Exception e){e.printStackTrace();}
		}
	}

		
	public void setProps(Properties p) {
		try {
			if ( p.containsKey( propKey ) ) {
				String val = p.getProperty( propKey );
				if ( val != null )
					f.set(o, val);	
			}
        } catch (IllegalAccessException iae) {iae.printStackTrace();}
	}
}
