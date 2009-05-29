package org.cytoscape.work.internal.tunables;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;

import java.io.File;
import java.io.InputStream;

import org.cytoscape.io.util.StreamUtil;
import org.cytoscape.property.bookmark.Bookmarks;
import org.cytoscape.property.bookmark.BookmarksUtil;
import org.cytoscape.property.CyProperty;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.HandlerFactory;
import org.cytoscape.work.util.BoundedDouble;
import org.cytoscape.work.util.BoundedFloat;
import org.cytoscape.work.util.BoundedInteger;
import org.cytoscape.work.util.BoundedLong;
import org.cytoscape.work.util.FlexiblyBoundedDouble;
import org.cytoscape.work.util.FlexiblyBoundedFloat;
import org.cytoscape.work.util.FlexiblyBoundedInteger;
import org.cytoscape.work.util.FlexiblyBoundedLong;
import org.cytoscape.work.util.ListMultipleSelection;
import org.cytoscape.work.util.ListSingleSelection;







/**
 * Provides a factory to create <code>Guihandler</code> depending on their type
 * A <code>Guihandler</code> is simply a <code>Handler</code> with the GUI aspect to display the handled object to the user in a proper way.
 * 
 * @author pasteur
 */
public class GuiHandlerFactory implements HandlerFactory<Guihandler> {

	private Bookmarks bookmarks;
	private BookmarksUtil bkUtil;
//	public FileUtil flUtil;
//	public StreamUtil stUtil;
	
	
	/**
	 * creates a new GuiHandlerFactory object
	 * @param book	informations and properties of the <code>Bookmarks</code> registered
	 * @param bkUtil object that provides tools to manage the <code>Bookmarks</code>
	 */
	public GuiHandlerFactory(CyProperty<Bookmarks> book, BookmarksUtil bkUtil) {
		this.bookmarks = book.getProperties();
		this.bkUtil = bkUtil;
//		this.flUtil = flUtil;
//		this.stUtil = stUtil;
	}
	
	
	/**
	 * To get a <code>Handler</code> for a Method annotated as <code>Tunable</code>
	 * 
	 * @param	m	the annotated method
	 * @param	o	object of the <code>Handler</code>
	 * @param	t	tunable of the <code>Handler</code>
	 * @return a <code>Guihandler</code> object depending on the Method's type
	 */	
	public Guihandler getHandler(Method m, Object o, Tunable t) {
		return null;
	}

	
	/**
	 * To get a <code>Handler</code> for a Field annotated as <code>Tunable</code>
	 * 
	 * @param	m	the annotated field
	 * @param	o	object of the <code>Handler</code>
	 * @param	t	tunable of the <code>Handler</code>
	 * @return a <code>Guihandler</code> object depending on the Field's type
	 */
	public Guihandler getHandler(Field f, Object o, Tunable t){
		Class<?> type = f.getType();

		if(type == Boolean.class || type == boolean.class)
			return new BooleanHandler(f,o,t);
		else if(type == String.class)
			return new StringHandler(f,o,t);

		if(type == Integer.class || type == int.class)
			return new IntegerHandler(f,o,t);
		else if(type == Double.class || type == double.class)
			return new DoubleHandler(f,o,t);
		else if(type == Float.class || type == float.class)
			return new FloatHandler(f,o,t);
		else if(type == Long.class || type == long.class)
			return new LongHandler(f,o,t);

		//Flexibly and Bounded Handlers rewritten
		else if ( type == BoundedInteger.class ) 
			return new BoundedHandler<BoundedInteger>(f,o,t);
		else if ( type == BoundedLong.class ) 
			return new BoundedHandler<BoundedLong>(f,o,t);
		else if ( type == BoundedFloat.class ) 
			return new BoundedHandler<BoundedFloat>(f,o,t);
		else if ( type == BoundedDouble.class ) 
			return new BoundedHandler<BoundedDouble>(f,o,t);

		else if ( type == FlexiblyBoundedInteger.class ) 
			return new FlexiblyBoundedHandler<FlexiblyBoundedInteger>(f,o,t);
		else if ( type == FlexiblyBoundedLong.class ) 
			return new FlexiblyBoundedHandler<FlexiblyBoundedLong>(f,o,t);
		else if ( type == FlexiblyBoundedFloat.class ) 
			return new FlexiblyBoundedHandler<FlexiblyBoundedFloat>(f,o,t);
		else if ( type == FlexiblyBoundedDouble.class ) 
			return new FlexiblyBoundedHandler<FlexiblyBoundedDouble>(f,o,t);
		
		else if(type == ListSingleSelection.class)
			return new ListSingleHandler<String>(f,o,t);
		else if(type == ListMultipleSelection.class)
			return new ListMultipleHandler<String>(f,o,t);

		else if(type == File.class)
//			return new FileHandler(f,o,t,flUtil);
			return new FileHandler(f,o,t);
		else if(type == URL.class)
			return new URLHandler(f,o,t,bookmarks,bkUtil);
		else if(type == InputStream.class)
//			return new InputStreamHandler(f,o,t,bookmarks,bkUtil,flUtil,stUtil);
			return new InputStreamHandler(f,o,t,bookmarks,bkUtil);
		return null;
	}
}
