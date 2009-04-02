package org.cytoscape.work.internal.props;

//import java.io.File;
import java.io.File;
import java.lang.reflect.*;
import java.util.*;

import org.cytoscape.work.AbstractPropHandler;
import org.cytoscape.work.Tunable;


public class FilePropHandler extends AbstractPropHandler {

	File file;
	List<String> list;
	
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
		//list = new ArrayList<String>();
		//list.add(0,"");
		//infile = new File("");
		//System.out.println("path = " + file.getAbsolutePath());
		try{
			p.put(propKey,file.getPath());
		}catch(Exception e){}
	}

		
	@SuppressWarnings("unchecked")
	public void setProps(Properties p) {
		try {
		if ( p.containsKey( propKey ) ) {
			Object val = p.get( propKey );
			if ( val != null )
				//file.setPath((String) val);
				f.set(o, file);
			}
        } catch (IllegalAccessException iae) {
            iae.printStackTrace();
        }
	}
}
