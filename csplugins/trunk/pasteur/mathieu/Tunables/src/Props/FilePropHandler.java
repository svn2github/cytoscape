package Props;

import java.lang.reflect.*;
import java.util.*;

import Tunable.*;
import Utils.myFile;

public class FilePropHandler extends AbstractPropHandler {

	myFile files;
	List<String>test;
	
	public FilePropHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
		try{
			files = (myFile) f.get(o);
		}catch(Exception e){e.printStackTrace();}
	}

	public Properties getProps() {
		Properties p = new Properties();
		p.put( propKey, files.getPaths());
		return p;
	}
	
	public void add(Properties p) {
		test = new ArrayList<String>();
		test.add(0,"");
		files.setPaths(test);
		try{
			p.put(propKey,files.getPaths());
		}catch(Exception e){e.printStackTrace();}
	}

		
	public void setProps(Properties p) {
		try {
		if ( p.containsKey( propKey ) ) {
			Object val = p.get( propKey );
			if ( val != null )
				files.setPaths((List<String>) val);
				f.set(o, files);			
			}
        } catch (IllegalAccessException iae) {
            iae.printStackTrace();
        }
	}
}
