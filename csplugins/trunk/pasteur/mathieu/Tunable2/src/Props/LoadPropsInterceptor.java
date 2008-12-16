package Props;


import java.util.*;
import GuiInterception.HiddenTunableInterceptor;


public class LoadPropsInterceptor extends HiddenTunableInterceptor<PropHandler> {
	private Properties inputProps;

	public LoadPropsInterceptor(final Properties inputProps) {
		super(new PropHandlerFactory());
		this.inputProps = inputProps;
	}
	protected void process(java.util.List<PropHandler> lh) {
		for (PropHandler p : lh) {
			p.setProps(inputProps);
		}
	}
	
	protected void cancel(List<PropHandler> handlerList) {	
	}

	protected void display(List<PropHandler> handlerList) {
	}
	
	protected void save(List<PropHandler> handlerList) {	
	}
}
