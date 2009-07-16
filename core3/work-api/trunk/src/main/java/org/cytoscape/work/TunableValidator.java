package org.cytoscape.work;


/**
 * If implemented, this interface is used to apply a test to the modified values of the class.
 * 
 * <p><pre>
 * 	<b>example</b> :
 * 
 * 	If using this Test class :
 * 	<code>
 * 		public class Test implements TunableValidator{
 * 			<code>@Tunable(...)</code>
 * 			String name = new String("John");
 * 		}
 * 
 * Then we can provide a method to check if the new value for this tunable matches with the conditions that we have set :
 * 
 * 	public class Test implements TunableValidator{
 * 		<code>@Tunable(...)</code>
 * 		String name = new String("John");
 * 
 * 		String validate(){
 * 			if(name.equals("Johnny"))
 * 			return new String("Please provide a different name!");
 * 			else return null;
 * 		}
 * 	}
 * 	</code></pre></p>
 * The String message returned by <code>validate()</code> method is displayed to the user. 
 * 
 * @author Pasteur
 *
 */
public interface TunableValidator{
	
	/**
	 * Executes the validation test on the annotated <code>Tunables</code> present in the class whose Objects have been modified.
	 * 
	 * @return The message that will be displayed if the test failed, or <i>null</i> if the test succeeded.
	 */
	void validate() throws Exception;
}
