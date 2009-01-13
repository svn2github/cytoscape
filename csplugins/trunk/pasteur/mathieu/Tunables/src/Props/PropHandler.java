package Props;

import java.util.Properties;
import HandlerFactory.*;

public interface PropHandler extends Handler {

	public void setProps(Properties p);
	public Properties getProps();
	public void add(Properties p);
}
