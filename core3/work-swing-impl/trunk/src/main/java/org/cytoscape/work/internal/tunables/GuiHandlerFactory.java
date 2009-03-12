package org.cytoscape.work.internal.tunables;

import java.lang.reflect.Field;
import java.net.URL;

import java.io.File;

import org.cytoscape.property.bookmark.Bookmarks;
import org.cytoscape.property.bookmark.BookmarksUtil;
import org.cytoscape.property.CyProperty;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.HandlerFactory;
import org.cytoscape.work.util.*;
import cytoscape.util.FileUtil;

public class GuiHandlerFactory implements HandlerFactory<Guihandler> {

	private Bookmarks bookmarks;
	private BookmarksUtil bkUtil;
	private FileUtil flUtil;
	
	public GuiHandlerFactory(CyProperty<Bookmarks> book, BookmarksUtil bkUtil) {
		this.bookmarks = book.getProperties();
		this.bkUtil = bkUtil;
		this.flUtil = flUtil;
	}

	public Guihandler getHandler(Field f, Object o, Tunable t){
		
		Class<?> type = f.getType();
		
		if(type == Integer.class || type == int.class)
			return new IntegerHandler(f,o,t);
		else if(type == Double.class || type == double.class)
			return new DoubleHandler(f,o,t);
		else if(type == Float.class || type == float.class)
			return new FloatHandler(f,o,t);
		else if(type == Long.class || type == long.class)
			return new LongHandler(f,o,t);	
		else if(type == BoundedDouble.class)
			return new BoundedDoubleHandler(f,o,t);
		else if(type == BoundedInteger.class)
			return new BoundedIntegerHandler(f,o,t);
		else if(type == BoundedFloat.class)
			return new BoundedFloatHandler(f,o,t);
		else if(type == BoundedLong.class)
			return new BoundedLongHandler(f,o,t);
		else if(type == Boolean.class || type == boolean.class)
			return new BooleanHandler(f,o,t);
		else if(type == String.class)
			return new StringHandler(f,o,t);
		else if(type == ListSingleSelection.class)
			return new ListSingleHandler<String>(f,o,t);
		else if(type == ListMultipleSelection.class)
			return new ListMultipleHandler<String>(f,o,t);
		else if(type == File.class)
			return new FileHandler(f,o,t,flUtil);
		else if(type == URL.class)
			return new URLHandler(f,o,t,bookmarks,bkUtil);
//		else if(type == URL.class)
//			return new InputStreamHandler(f,o,t,bookmarkrs,bkUtil,flUtil);
		return null;
	}
}
