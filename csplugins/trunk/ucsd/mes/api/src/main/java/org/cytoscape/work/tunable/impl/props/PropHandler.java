
package org.cytoscape.work.tunable.impl.props;

import org.cytoscape.work.tunable.*;
import java.util.*;

public interface PropHandler extends Handler {
	public void setProps(Properties p);
	public Properties getProps();
}
