
package org.example.tunable;

import java.util.Map;

public interface TunableInterceptor<T extends Handler> { 

	public void loadTunables(Object o);

	public Map<String,T> getHandlers(Object o);

	public void createUI(Object o);
}
