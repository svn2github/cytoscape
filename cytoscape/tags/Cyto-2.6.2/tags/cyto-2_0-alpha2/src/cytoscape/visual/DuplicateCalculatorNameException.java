//------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//------------------------------------------------------------------------------
package cytoscape.visual;
//------------------------------------------------------------------------------
/**
 * This Exception is thrown when one attempts to add a Calculator to a
 * CalculatorCatalog that already has a Calculator of the same name and
 * interface type.
 */
public class DuplicateCalculatorNameException extends RuntimeException {
    public DuplicateCalculatorNameException(String s) {
	super(s);
    }
}
