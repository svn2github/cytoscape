package Props;


import Tunable.*;
import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;

import GuiInterception.*;

public class StorePropsInterceptor extends HiddenTunableInterceptor<PropHandler> {
	private Properties inputProps;

	public StorePropsInterceptor(Properties inputProps) {
		super(new PropHandlerFactory());
		this.inputProps = inputProps;
	}

	protected void processProps(java.util.List<PropHandler> lh) {
		for (PropHandler p : lh) {
			inputProps.putAll(p.getProps());
		}
	}


	protected void cancel(List<PropHandler> handlerList) {
	}

	protected void display(List<PropHandler> handlerList) {
	}

	protected void save(List<PropHandler> handlerList) {
	}

	@Override
	protected void process(List<PropHandler> handlerList) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void addProps(List<PropHandler> handlerList) {
		// TODO Auto-generated method stub
		
	}
}
