package org.cytoscape.work.internal.props;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import java.io.File;

import org.cytoscape.work.Tunable;


public class FilePropHandler extends AbstractPropHandler {

	File file;
	List<String> paths;
	String path;
	
	public FilePropHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
		try{
			file = (File) f.get(o);
		}catch(Exception e){e.printStackTrace();}
	}

	public Properties getProps() {
		Properties p = new Properties();
		p.put( propKey, file.getAbsolutePath());
		return p;
	}
	
	public void add(Properties p) {
		//test = new ArrayList<String>();
		//test.add(0,"");
		// TODO
		//files.setPaths(test);
		try{
			p.put(propKey,file.getAbsolutePath());
		}catch(Exception e){e.printStackTrace();}
	}

		
	@SuppressWarnings("unchecked")
	public void setProps(Properties p) {
//		try {
			if ( p.containsKey( propKey ) ) {
				Object val = p.get( propKey );
				// TODO
				//if ( val != null ) 
					//files.setPaths((List<String>) val);
					//f.set(o, files);			
			}
//        } catch (IllegalAccessException iae) {
 //           iae.printStackTrace();
  //      }
	}
}
