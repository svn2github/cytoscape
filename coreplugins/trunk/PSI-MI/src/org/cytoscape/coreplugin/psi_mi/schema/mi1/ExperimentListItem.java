/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.4.3</a>, using an XML
 * Schema.
 * $Id$
 */

package org.cytoscape.coreplugin.psi_mi.schema.mi1;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

/**
 * Class ExperimentListItem.
 * 
 * @version $Revision$ $Date$
 */
public class ExperimentListItem implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * References an experiment already present in this entry.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi1.RefType _experimentRef;

    /**
     * An experiment in which this interaction has been determined.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi1.ExperimentType _experimentDescription;


      //----------------/
     //- Constructors -/
    //----------------/

    public ExperimentListItem() {
        super();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.ExperimentListItem()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method getExperimentDescriptionReturns the value of field
     * 'experimentDescription'. The field 'experimentDescription'
     * has the following description: An experiment in which this
     * interaction has been determined.
     * 
     * @return the value of field 'experimentDescription'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.ExperimentType getExperimentDescription()
    {
        return this._experimentDescription;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.ExperimentType getExperimentDescription()

    /**
     * Method getExperimentRefReturns the value of field
     * 'experimentRef'. The field 'experimentRef' has the following
     * description: References an experiment already present in
     * this entry.
     * 
     * @return the value of field 'experimentRef'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.RefType getExperimentRef()
    {
        return this._experimentRef;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.RefType getExperimentRef()

    /**
     * Method setExperimentDescriptionSets the value of field
     * 'experimentDescription'. The field 'experimentDescription'
     * has the following description: An experiment in which this
     * interaction has been determined.
     * 
     * @param experimentDescription the value of field
     * 'experimentDescription'.
     */
    public void setExperimentDescription(org.cytoscape.coreplugin.psi_mi.schema.mi1.ExperimentType experimentDescription)
    {
        this._experimentDescription = experimentDescription;
    } //-- void setExperimentDescription(org.cytoscape.coreplugin.psi_mi.schema.mi1.ExperimentType)

    /**
     * Method setExperimentRefSets the value of field
     * 'experimentRef'. The field 'experimentRef' has the following
     * description: References an experiment already present in
     * this entry.
     * 
     * @param experimentRef the value of field 'experimentRef'.
     */
    public void setExperimentRef(org.cytoscape.coreplugin.psi_mi.schema.mi1.RefType experimentRef)
    {
        this._experimentRef = experimentRef;
    } //-- void setExperimentRef(org.cytoscape.coreplugin.psi_mi.schema.mi1.RefType)

}
