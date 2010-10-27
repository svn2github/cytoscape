package org.cytoscape.work;


/**
 * If implemented, this interface is used to apply a test to the modified values of a Tunable.
 * 
 * <p><pre>
 * 	<b>Example</b>:
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
public interface TunableValidator {
	/**
	 * Executes the validation test on the annotated <code>Tunables</code>.
	 * 
	 * @param  errMsg  if the validation failed an explanatory message can be found here and accessed via <code>errMsg.toString()</code>
	 * @return true if the test succeeded and false otherwise
	 */
	boolean tunablesAreValid(final Appendable errMsg);
}
