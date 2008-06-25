
package org.cytoscape.tunable.impl.props;

import org.cytoscape.tunable.*;
import java.util.*;

public interface PropHandler extends Handler {
	public void setProps(Properties p);
	public Properties getProps();
}
