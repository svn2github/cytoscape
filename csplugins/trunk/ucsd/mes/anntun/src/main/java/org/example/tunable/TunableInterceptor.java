
package org.example.tunable;

import org.example.command.Command;

public interface TunableInterceptor { 

	/**
	 * Intercepts a {@link Command} object and should look
	 * for {@link Tunable} annotated {@link Field}s in the command,
	 * process the {@link Tunable}s using the {@link Handler}s
	 * specified in the implementation.
	 */
	public void intercept(Command c);
}
