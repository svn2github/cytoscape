package Props;

import java.util.*;
import GuiInterception.*;


public class StorePropsInterceptor extends HiddenTunableInterceptor<PropHandler> {
	private Properties inputProps;

	public StorePropsInterceptor(Properties inputProps) {
		super(new PropHandlerFactory<PropHandler>());
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
	
	
	
	protected int process(java.util.List<PropHandler> lh) {
		for (PropHandler p : lh) {
			inputProps.putAll(p.getProps());
		}
		return 0;
	}

}
