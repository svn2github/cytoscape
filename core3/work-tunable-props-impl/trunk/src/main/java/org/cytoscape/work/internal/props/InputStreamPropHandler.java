package org.cytoscape.work.internal.props;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Properties;
import java.io.InputStream;

import org.cytoscape.work.Tunable;


public class InputStreamPropHandler extends AbstractPropHandler {

	InputStream str;
	List<String> paths;
	String path;
	
	
	public InputStreamPropHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
		try{
			str = (InputStream) f.get(o);
		}catch(Exception e){e.printStackTrace();}
	}

	
	public Properties getProps() {
		Properties p = new Properties();
		p.put( propKey,str);
		return p;
	}
	
	public void add(Properties p) {
		if(str!=null){
			p.put(propKey,str);
		}
		else {
			p.put(propKey,"");
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
