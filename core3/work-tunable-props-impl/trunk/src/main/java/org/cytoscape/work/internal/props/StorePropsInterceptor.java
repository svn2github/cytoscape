package org.cytoscape.work.internal.props;

import java.util.*;

import org.cytoscape.work.AbstractTunableInterceptor;


public class StorePropsInterceptor extends AbstractTunableInterceptor<PropHandler> {
	private Properties inputProps;

	public StorePropsInterceptor(Properties inputProps) {
		super(new PropHandlerFactory());
		this.inputProps = inputProps;
	}

	
//	protected void processProps(List<PropHandler> lh) {
//		for (PropHandler p : lh) {
//			p.setProps(inputProps);
//		}
//	}
	protected void processProps(List<PropHandler> lh) {
		for (PropHandler p : lh) {
			inputProps.putAll(p.getProps());
		}
	}
	


	protected void getResultsPanels(List<PropHandler> handlers) {
	}


	public int createUI(Object... objs) {return 0;}

	public void createProperties(Object... obs) {
		java.util.List<PropHandler> lh = new ArrayList<PropHandler>();
		for ( Object o : obs ) {
			if ( !handlerMap.containsKey( o ) )
				throw new IllegalArgumentException("No Tunables exist for Object yet!");
			lh.addAll( handlerMap.get(o).values() );
		}
		for (PropHandler p : lh) {
			inputProps.putAll(p.getProps());
		}
	}
}
