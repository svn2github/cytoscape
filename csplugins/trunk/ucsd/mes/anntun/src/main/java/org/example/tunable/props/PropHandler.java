
package org.example.tunable.props;

import org.example.tunable.*;
import java.util.*;

public interface PropHandler extends Handler {
	public void setProps(Properties p);
	public Properties getProps();
}
