package Props;

import java.util.*;
import GuiInterception.HiddenTunableInterceptor;


public class LoadPropsInterceptor extends HiddenTunableInterceptor<PropHandler> {
	private Properties inputProps;

	public LoadPropsInterceptor(final Properties inputProps){
		super(new PropHandlerFactory<PropHandler>());
		this.inputProps = inputProps;
	}

	protected void processProps(List<PropHandler> lh) {
		for (PropHandler p : lh) {
			p.setProps(inputProps);
		}
	}
	
	protected int process(List<PropHandler> lh) {
		for (PropHandler p : lh) {
			p.add(inputProps);
		}
		return 0;
	}


}