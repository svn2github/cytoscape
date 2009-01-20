package Props;

import java.util.*;
import GuiInterception.HiddenTunableInterceptor;


public class LoadPropsInterceptor extends HiddenTunableInterceptor<PropHandler> {
	private Properties inputProps;

	public LoadPropsInterceptor(final Properties inputProps){
		super(new PropHandlerFactory<PropHandler>());
		this.inputProps = inputProps;
	}
	protected void addProps(java.util.List<PropHandler> lh) {
		for (PropHandler p : lh){
			if(p.getClass()!= GroupPropHandler.class) p.add(inputProps);
		}
	}
	
	protected void display(List<PropHandler> handlerList) {
	}
	
	protected void save(List<PropHandler> handlerList) {	
	}

	protected void getInputPanes(List<PropHandler> handlerList) {		
	}
	
	protected void processProps(List<PropHandler> lh) {
		for (PropHandler p : lh) {
			if(p.getClass()!= GroupPropHandler.class) p.setProps(inputProps);
		}
	}

}