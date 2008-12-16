package Props;


import java.util.*;
import HandlerFactory.*;

public interface PropHandler extends Handler {

	public void setProps(Properties p);
	public Properties getProps();
}
