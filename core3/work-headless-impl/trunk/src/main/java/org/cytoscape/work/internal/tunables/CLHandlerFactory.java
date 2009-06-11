package org.cytoscape.work.internal.tunables;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.cytoscape.work.HandlerFactory;
import org.cytoscape.work.Tunable;
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


public class CLHandlerFactory implements HandlerFactory<CLHandler> {

	public CLHandler getHandler(Method m, Object o, Tunable t) {
		return null;
	}

	public CLHandler getHandler(Method gmethod, Method smethod, Object o, Tunable tg, Tunable ts){
		Class<?>[] paramsTypes = smethod.getParameterTypes();
		Class<?> returnType = gmethod.getReturnType();
		if ( paramsTypes.length != 1 ) {
			System.err.println("found bad method");
			return null;
		}
		Class<?> type = paramsTypes[0];
		if(!type.equals(returnType)) {
			System.err.println("return type and parameter type are differents for the methods " + gmethod.getName() + " and " + smethod.getName());
			return null;
		}
		
		if( type == int.class || type == Integer.class)
			return new IntCLHandler(gmethod,smethod,o,tg,ts);
		else if( type == Boolean.class || type == boolean.class)
			return new BooleanCLHandler(gmethod,smethod,o,tg,ts);
		else if( type == String.class)
			return new StringCLHandler(gmethod,smethod,o,tg,ts);
		
		else if( type == BoundedInteger.class)
			return new BoundedCLHandler<BoundedInteger>(gmethod,smethod,o,tg,ts);
		else if( type == BoundedDouble.class)
			return new BoundedCLHandler<BoundedDouble>(gmethod,smethod,o,tg,ts);
		
		else if(type == FlexiblyBoundedInteger.class)
			return new FlexiblyBoundedCLHandler<FlexiblyBoundedInteger>(gmethod,smethod,o,tg,ts);
		else if(type == FlexiblyBoundedDouble.class)
			return new FlexiblyBoundedCLHandler<FlexiblyBoundedDouble>(gmethod,smethod,o,tg,ts);
		else if(type == ListSingleSelection.class)
			return new ListSingleSelectionCLHandler<Object>(gmethod,smethod,o,tg,ts);
		else if(type == ListMultipleSelection.class)
			return new ListMultipleSelectionCLHandler<Object>(gmethod,smethod,o,tg,ts);
		
		else if(type == File.class)
			return new FileCLHandler(gmethod,smethod,o,tg,ts);
		else
			return null;
	}

	
	
	public CLHandler getHandler(Field f, Object o, Tunable t) {
		Class<?> type = f.getType();

		if ( type == int.class || type == Integer.class )
			return new IntCLHandler(f,o,t);
		else if ( type == String.class )
			return new StringCLHandler(f,o,t);
		else if ( type == boolean.class || type == Boolean.class )
			return new BooleanCLHandler(f,o,t);
		
		else if ( type == BoundedDouble.class )
			return new BoundedCLHandler<BoundedDouble>(f,o,t);
		else if ( type == BoundedInteger.class )
			return new BoundedCLHandler<BoundedInteger>(f,o,t);
		else if ( type == BoundedFloat.class )
			return new BoundedCLHandler<BoundedFloat>(f,o,t);
		else if ( type == BoundedLong.class )
			return new BoundedCLHandler<BoundedLong>(f,o,t);
		
		else if( type == FlexiblyBoundedDouble.class )
			return new FlexiblyBoundedCLHandler<FlexiblyBoundedDouble>(f,o,t);
		else if( type == FlexiblyBoundedInteger.class )
			return new FlexiblyBoundedCLHandler<FlexiblyBoundedInteger>(f,o,t);
		else if( type == FlexiblyBoundedFloat.class )
			return new FlexiblyBoundedCLHandler<FlexiblyBoundedFloat>(f,o,t);
		else if( type == FlexiblyBoundedLong.class )
				return new FlexiblyBoundedCLHandler<FlexiblyBoundedLong>(f,o,t);
		
		else if ( type == ListSingleSelection.class)
			return new ListSingleSelectionCLHandler<Object>(f,o,t);
		else if ( type == ListMultipleSelection.class)
			return new ListMultipleSelectionCLHandler<Object>(f,o,t);
		else 
			return null;
	}

}