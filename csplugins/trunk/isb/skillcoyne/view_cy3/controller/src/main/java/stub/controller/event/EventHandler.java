/**
 * 
 */
package stub.controller.event;

/**
 * @author skillcoy
 *
 */
public interface EventHandler<T> {

	public void handle(T evt);
	
}
