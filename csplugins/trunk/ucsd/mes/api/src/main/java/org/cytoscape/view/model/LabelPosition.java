
package org.cytoscape.view.model; 


/**
 * Used to define the position of a label relative to 
 * a target object.
 * <p>
 * We should probably have get X offset and get Y offset as well.  Ugh.
 */
public interface LabelPosition extends Saveable {

	/**
	 * This identifies where on the target object the label
	 * will be drawn.
	 *
	 * @return The {@link AnchorLocation} on the target object.
	 */
	public AnchorLocation getTargetAnchor();

	/**
	 * This identifies where label will be drawn relative to
	 * to the label's bounding box once the 
	 * target {@link AnchorLocation} has been identified.
	 *
	 * @return The {@link AnchorLocation} on the label bounding box. 
	 */
	public AnchorLocation getLabelAnchor();

	/**
	 * This identifies how the text should be justified within the
	 * bounding box that the label is drawn in.
	 * @return The {@link LabelJustify} that defines how the label
	 * will be drawn within it's bounding box.
	 */
	public LabelJustify getJustify();
}

