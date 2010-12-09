package org.cytoscape.view.vizmap.gui.internal.task.generators;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.cytoscape.session.CyApplicationManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.vizmap.VisualMappingFunction;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.gui.SelectedVisualStyleManager;
import org.cytoscape.view.vizmap.gui.internal.VizMapperProperty;
import org.cytoscape.view.vizmap.gui.internal.event.CellType;
import org.cytoscape.view.vizmap.gui.util.DiscreteMappingGenerator;
import org.cytoscape.view.vizmap.mappings.DiscreteMapping;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

import com.l2fprod.common.propertysheet.Property;
import com.l2fprod.common.propertysheet.PropertySheetTable;
import com.l2fprod.common.propertysheet.PropertySheetTableModel.Item;

public class GenerateValuesTask extends AbstractTask {

	private final DiscreteMappingGenerator<?> generator;

	private final PropertySheetTable table;
	private final SelectedVisualStyleManager manager;
	private final CyApplicationManager appManager;
	
	//private final CyTableManager tableManager;

	public GenerateValuesTask(final DiscreteMappingGenerator<?> generator,
			final PropertySheetTable table,
			final SelectedVisualStyleManager manager,
			final CyApplicationManager appManager) {
		this.generator = generator;
		this.appManager = appManager;
		this.manager = manager;
		this.table = table;
		//this.tableManager = tableManager;
	}

	@Override
	public void run(TaskMonitor monitor) throws Exception {
		System.out.println("Running task...");
		
		int selectedRow = table.getSelectedRow();

		// If not selected, do nothing.
		if (selectedRow < 0)
			return;

		final Item value = (Item) table.getValueAt(selectedRow, 0);

		if (value.isProperty()) {
			final VizMapperProperty<?, ?, ?> prop = (VizMapperProperty<?, ?, ?>) value
					.getProperty();

			if (prop.getCellType() == CellType.VISUAL_PROPERTY_TYPE) {
				final VisualProperty<?> vp = (VisualProperty<?>) prop.getKey();
				System.out.println("Type of VP: " + vp.getRange().getType());
				
				if(vp.getRange().getType().isAssignableFrom(generator.getDataType())) {
					System.out.println("This is compatible: " + generator.getDataType());
					generateMapping(prop, prop.getValue().toString(), vp);
					
				}
					
				
			}
		}
	}
	
	private void generateMapping(final VizMapperProperty<?, ?, ?> prop, final String attrName, final VisualProperty<?> vp ) {
		System.out.println("Target Attr name = " + attrName);
		
		
		final Property[] subProps = prop.getSubProperties();
		final VisualStyle style = manager.getCurrentVisualStyle();
		final VisualMappingFunction<?, ?> mapping = style.getVisualMappingFunction(vp);
		
		if(mapping == null)
			return;
		
		final DiscreteMapping<Object, Object> discMapping = (DiscreteMapping)mapping;
		
		final Set<Object> keySet = new HashSet<Object>();
		for(Property p: subProps) {
			final VizMapperProperty<?, ?, ?> vmp = (VizMapperProperty<?, ?, ?>)p;
			if(vmp.getCellType().equals(CellType.DISCRETE)) {
				System.out.print("Key = " + vmp.getKey());
				System.out.print(" Key Class = " + vmp.getKey().getClass());
				System.out.println(" Val = " + vmp.getValue());
				keySet.add(vmp.getKey());
			}
		}
		
		Map<Object, ?> map = generator.generateMap(keySet);
		
		discMapping.putAll(map);
		
		final CyNetworkView networkView = appManager.getCurrentNetworkView();
		style.apply(networkView);
		networkView.updateView();
		
		for(Property p: subProps) {
			final VizMapperProperty<?, ?, ?> vmp = (VizMapperProperty<?, ?, ?>)p;
			if(vmp.getCellType().equals(CellType.DISCRETE)) {
				
				System.out.print("New Key = " + vmp.getKey());
				System.out.print(" New Key Class = " + vmp.getKey().getClass());
				
				vmp.setValue(discMapping.getMapValue(vmp.getKey()));
				System.out.println(" New Val = " + vmp.getValue());
			}
		}

	}

}
