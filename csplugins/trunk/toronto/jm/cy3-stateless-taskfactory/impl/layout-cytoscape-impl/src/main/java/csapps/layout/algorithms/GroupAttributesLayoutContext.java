package csapps.layout.algorithms;

import java.util.List;
import java.util.Set;

import org.cytoscape.view.layout.LayoutContextImpl;
import org.cytoscape.work.Tunable;

public class GroupAttributesLayoutContext extends LayoutContextImpl {
	/*
	  Layout parameters:
	    - spacingx: Horizontal spacing (on the x-axis) between two partitions in a row.
	    - spacingy: Vertical spacing (on the y-axis) between the largest partitions of two rows.
	    - maxwidth: Maximum width of a row
	    - minrad:   Minimum radius of a partition.
	    - radmult:  The scale of the radius of the partition. Increasing this value
	                will increase the size of the partition proportionally.
	 */
	@Tunable(description="Horizontal spacing between two partitions in a row")
	public double spacingx = 400.0;
	@Tunable(description="Vertical spacing between the largest partitions of two rows")
	public double spacingy = 400.0;
	@Tunable(description="Maximum width of a row")
	public double maxwidth = 5000.0;
	@Tunable(description="Minimum width of a partition")
	public double minrad = 100.0;
	@Tunable(description="Scale of the radius of the partition")
	public double radmult = 50.0;
	
	//@Tunable(description="The attribute to use for the layout")
	public String attributeName;
	//@Tunable(description="The namespace of the attribute to use for the layout")
	public String attributeNamespace;
	
	public GroupAttributesLayoutContext(boolean supportsSelectedOnly,
			Set<Class<?>> supportedNodeAttributeTypes,
			Set<Class<?>> supportedEdgeAttributeTypes) {
		super(supportsSelectedOnly, supportedNodeAttributeTypes,
				supportedEdgeAttributeTypes);
		// TODO Auto-generated constructor stub
	}

	
	
	@Override
	public void setLayoutAttribute(String value) {
		if (value.equals("(none)"))
			this.attributeName = null;
		else
			this.attributeName = value;
	}
	
	
	@Override
	public List<String> getInitialAttributeList() {
		return null;
	}


	//TODO
	public boolean tunablesAreValid(final Appendable errMsg) {
		return true;
	}
}
