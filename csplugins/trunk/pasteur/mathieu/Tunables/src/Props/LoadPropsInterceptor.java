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
			p.add(inputProps);
		}
	}
	
	protected void display(List<PropHandler> handlerList) {
	}
	
	protected void save(List<PropHandler> handlerList) {	
	}

	protected void process(List<PropHandler> handlerList) {		
	}
	
	protected void processProps(List<PropHandler> lh) {
		for (PropHandler p : lh) {
			p.setProps(inputProps);
		}
	}

}