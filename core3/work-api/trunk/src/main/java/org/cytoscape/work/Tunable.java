package org.cytoscape.work;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.TYPE})

/**
 * This interface describes the different parameters that can be used in the <code> @Tunable(...) </code> to display the GUI, add some dependencies...
 * 
 * Here is an example of how to use it these <code>Tunable's annotation</code> :
 * 
 * <br>
 * <code>
 * 	@Tunable(description="your last name", group={"Human","pupil"}, flag={Param.collapsed})<br>
 * 	public String lastName = "Smith";<br>
 * </code>
 * <br>
 * 
 * This tunable will take part of a group("<code>pupil</code>"), which is also a part of a metagroup("<code>Human</code>").<br>
 * The initial state of the panel that will display the JTextField with the <code>lastName</code> will be collapsed<code>Param.collapsed</code> : need to expand it to see its components<br>
 * 
 *  */

public @interface Tunable{
	/**
	 * Description of the Tunable that will be displayed in the panel to identify it
	 */
	String description();
	
	
	
	/**
	 * Parameters to interact directly with the way the <code>Tunable</code> will be represented in its JPanel :<br>
	 * 	- <code>slider</code> to display a JSlider for the Tunable with bounds<br>
	 * 	- <code>horizontal / vertical</code> to display the tunables that belong to the same group horizontally, or vertically( default representation is <code> Param.vertical</code> <br>
	 * 	- <code>uncollapsed / collapsed</code> to allow the tunable's JPanel to be collapsable, and to define its initial state<br>
	 * 	- <code>network / session / attributes</code> to add some filters to the <code>File</code> Tunable.<br>
	 */
	Param[] flag() default {};
	
	
	
	/**
	 * Used to define all the groups in which the Tunable takes part (by default, its doesn't belong to any group)<br>
	 */
	String[] group() default {};

	
	
	/**
	 * Boolean value to choose if the Tunable will control the display of other JPanels as children
	 */
	boolean xorChildren() default false;
	
	
	/**
	 * Key that will refer to the value of the Tunable which has <code>xorChildren=true</code>
	 */
	String xorKey() default "";
	
	
	/**
	 * To add a dependency between Tunables<br>
	 * The JPanel of the Tunable that depends on the other one will be activated only if the value which is required is set.<br>
	 * Here is an example of how to add dependencies between Tunables :<br>
	 * <code>
	 * 	@Tunable(description="Type")<br>
	 *  public boolean type = false;<br>
	 * <br>
	 * 	@Tunable(description="Host name",dependsOn="type=true")<br>
	 *  public String hostname="";<br>
	 *  </code>
	 *  <br>
	 *  So <code>hostname</code> will be activated if <code>type</code> is set to "true"
	 */
	String dependsOn() default "";
	
	
	
	/**
	 * Parameters to display the Tunables of a same group horizontally or vertically inside.
	 */
	Param[] alignment() default {};

	/**
	 * Parameters to choose if the name of a Tunable's group has to be displayed or not in the <code>titleBorder</code>  of the Panel representing this group.
	 */
	Param[] groupTitles() default {};
	
	
	/**
	 * Enumeration that contains the parameters used for <code>flag{}</code> and <code>alignment{}</code>
	 */
	enum Param {slider,horizontal,vertical,uncollapsed,collapsed,network,session,attributes,hidden,displayed}
}
