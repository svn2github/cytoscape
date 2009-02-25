package org.cytoscape.work.internal.props;

import java.util.*;

import org.cytoscape.work.AbstractTunableInterceptor;


public class LoadPropsInterceptor extends AbstractTunableInterceptor<PropHandler> {
	private Properties inputProps;

	public LoadPropsInterceptor(final Properties inputProps){
		super(new PropHandlerFactory());
		this.inputProps = inputProps;
	}

//	protected void processProps(List<PropHandler> lh) {
//		for (PropHandler p : lh) {
//			p.setProps(inputProps);
//		}
//	}

	public int createUI(Object... objs) {
		java.util.List<PropHandler> lh = new ArrayList<PropHandler>();
		for ( Object o : objs ) {
			if ( !handlerMap.containsKey( o ) )
				throw new IllegalArgumentException("No Tunables exist for Object yet!");
			
			lh.addAll( handlerMap.get(o).values() );
		}
		for (PropHandler p : lh) {
			p.add(inputProps);
		}
		return 0;
	}
}