package Props;

import java.util.*;
import GuiInterception.*;


public class StorePropsInterceptor extends HiddenTunableInterceptor<PropHandler> {
	private Properties inputProps;

	public StorePropsInterceptor(Properties inputProps) {
		super(new PropHandlerFactory<PropHandler>());
		this.inputProps = inputProps;
	}

	protected void processProps(java.util.List<PropHandler> lh) {
		for (PropHandler p : lh) {
			inputProps.putAll(p.getProps());
		}
	}

	protected void display(List<PropHandler> handlerList) {
	}

	protected void save(List<PropHandler> handlerList) {
	}

	protected void getInputPanes(List<PropHandler> handlerList) {		
	}

	protected void addProps(List<PropHandler> handlerList) {
		
	}

}
