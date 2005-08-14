/*
 * Created on May 31, 2005
 *
 */
package cytoscape.editor.event;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import cytoscape.editor.impl.BasicCytoShapeEntity;

/**
 * transfer handler for shapes that are dragged from the palette onto the canvas.
 * Creates appropriate data flavor and transferrable.
 * part of drag/drop editor framework.
 * <p>
 * not available in Cytoscape 2.2.
 * @author Allan Kuchinsky
 * @version 1.0
 * @see GraphicalEntity, BasicCytoShapeEntity
 *
 */
public class BasicCytoShapeTransferHandler extends TransferHandler {

	DataFlavor basicCytoShapeFlavor; 
    	
    BasicCytoShapeEntity _cytoShape;
    
	/**
	 * @return Returns the _attributeName.
	 */
	public String get_attributeName() {
		return _attributeName;
	}
	/**
	 * @return Returns the _attributeValue.
	 */
	public String get_attributeValue() {
		return _attributeValue;
	}
	
    String _attributeName;
    String _attributeValue;
    Object [] _args;
    
    /**
     * creates a DataFlavor for the BasicCytoShapeEntity class
     *
     */
    public BasicCytoShapeTransferHandler ()
    {
    	try
		{
    		basicCytoShapeFlavor = new DataFlavor(BasicCytoShapeEntity.class, "BasicCytoShapeEntity");
		} 
    	catch (Exception e) { e.printStackTrace(); }
    }
    

    /**
     * creates a DataFlavor and sets instance variables for a BasicCytoShapeEntity that is 
     * added to the palette
     * @param cytoShape shape that is added to the palette
     * @param args arbitrary list of arguments that can be passed in
     */
    public BasicCytoShapeTransferHandler (BasicCytoShapeEntity cytoShape, Object [] args)
    {
    	try
		{
    		basicCytoShapeFlavor = new DataFlavor(BasicCytoShapeEntity.class, "BasicCytoShapeEntity");
		} 
    	catch (Exception e) { e.printStackTrace(); }
    	_cytoShape = cytoShape;
    	_args = args;
    	_attributeName = cytoShape.getAttributeName();
    	_attributeValue = cytoShape.getAttributeValue();
    }
    
	/**
	 * @return Returns the _args.
	 */
	public Object[] get_args() {
		return _args;
	}
	
	/**
	 * sets the _args instance variable
	 * @param _args The _args to set.
	 */
	public void set_args(Object[] _args) {
		this._args = _args;
	}


	
    public boolean importData(JComponent c, Transferable t) {
    	System.out.println("importing data from transferable " + t);
        if (canImport(c, t.getTransferDataFlavors())) {   
        }
        return false;
    }

    public Transferable createTransferable(JComponent c) {
        return new BasicCytoShapeTransferable(c);
    }

    public boolean canImport(JComponent c, DataFlavor[] flavors) {
        for (int i = 0; i < flavors.length; i++) {
            if (basicCytoShapeFlavor.equals(flavors[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * packages the BasicCytoShapeEntity for transfer upon a drag/drop operation
     * @author Allan Kuchinsky
     * @version 1.0
     *
     * 
     */
    class BasicCytoShapeTransferable implements Transferable {
    	
        private BasicCytoShapeEntity _cytoShape;
        private String _attributeName;
        private String _attributeValue;

        BasicCytoShapeTransferable(JComponent obj) {
        	if (obj instanceof BasicCytoShapeEntity)
        	{
        		_cytoShape = (BasicCytoShapeEntity) obj;
        		_attributeName = _cytoShape.getAttributeName();
        		_attributeValue = _cytoShape.getAttributeValue();
        	}
            
        }

        public Object getTransferData(DataFlavor flavor)
                                 throws UnsupportedFlavorException {
            if (!isDataFlavorSupported(flavor)) {
                throw new UnsupportedFlavorException(flavor);
            }
            return _cytoShape;
        }

        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[] { basicCytoShapeFlavor };
        }

        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return basicCytoShapeFlavor.equals(flavor);
        }
    }
	/**
	 * @return Returns the _cytoShape.
	 */
	public BasicCytoShapeEntity get_cytoShape() {
		return _cytoShape;
	}
	/**
	 * sets the instance variable for a BasicCytoShapeEntity
	 * @param shape The _cytoShape to set.
	 */
	public void set_cytoShape(BasicCytoShapeEntity shape) {
		_cytoShape = shape;
	}
}

