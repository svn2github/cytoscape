package HandlerFactory;

import java.util.Map;

public interface HandlerController {
	void controlHandlers(Map<String,Handler> tunableMap);
}
