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

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * Describes one or more interactions as a self-contained unit.
 * Multiple entries from different files can be concatenated into a
 * single entrySet.
 * 
 * @version $Revision$ $Date$
 */
public class Entry implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Desciption of the source of the entry, usually an organisatio
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi1.Source _source;

    /**
     * Data availability statements, for example copyrights
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi1.AvailabilityList _availabilityList;

    /**
     * All experiments in which the interactions of this entry have
     * been determined
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi1.ExperimentList1 _experimentList1;

    /**
     * List of all interactors occurring in the entry
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi1.InteractorList _interactorList;

    /**
     * List of interactions
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi1.InteractionList _interactionList;

    /**
     * Semi-structured additional description of the data contained
     * in the entry.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi1.AttributeListType _attributeList;


      //----------------/
     //- Constructors -/
    //----------------/

    public Entry() {
        super();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.Entry()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method getAttributeListReturns the value of field
     * 'attributeList'. The field 'attributeList' has the following
     * description: Semi-structured additional description of the
     * data contained in the entry.
     * 
     * @return the value of field 'attributeList'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.AttributeListType getAttributeList()
    {
        return this._attributeList;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.AttributeListType getAttributeList()

    /**
     * Method getAvailabilityListReturns the value of field
     * 'availabilityList'. The field 'availabilityList' has the
     * following description: Data availability statements, for
     * example copyrights
     * 
     * @return the value of field 'availabilityList'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.AvailabilityList getAvailabilityList()
    {
        return this._availabilityList;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.AvailabilityList getAvailabilityList()

    /**
     * Method getExperimentList1Returns the value of field
     * 'experimentList1'. The field 'experimentList1' has the
     * following description: All experiments in which the
     * interactions of this entry have been determined
     * 
     * @return the value of field 'experimentList1'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.ExperimentList1 getExperimentList1()
    {
        return this._experimentList1;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.ExperimentList1 getExperimentList1()

    /**
     * Method getInteractionListReturns the value of field
     * 'interactionList'. The field 'interactionList' has the
     * following description: List of interactions
     * 
     * @return the value of field 'interactionList'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.InteractionList getInteractionList()
    {
        return this._interactionList;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.InteractionList getInteractionList()

    /**
     * Method getInteractorListReturns the value of field
     * 'interactorList'. The field 'interactorList' has the
     * following description: List of all interactors occurring in
     * the entry
     * 
     * @return the value of field 'interactorList'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.InteractorList getInteractorList()
    {
        return this._interactorList;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.InteractorList getInteractorList()

    /**
     * Method getSourceReturns the value of field 'source'. The
     * field 'source' has the following description: Desciption of
     * the source of the entry, usually an organisation
     * 
     * @return the value of field 'source'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.Source getSource()
    {
        return this._source;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.Source getSource()

    /**
     * Method isValid
     */
    public boolean isValid()
    {
        try {
            validate();
        }
        catch (org.exolab.castor.xml.ValidationException vex) {
            return false;
        }
        return true;
    } //-- boolean isValid() 

    /**
     * Method marshal
     * 
     * @param out
     */
    public void marshal(java.io.Writer out)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {

        Marshaller.marshal(this, out);
    } //-- void marshal(java.io.Writer) 

    /**
     * Method marshal
     * 
     * @param handler
     */
    public void marshal(org.xml.sax.ContentHandler handler)
        throws java.io.IOException, org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {

        Marshaller.marshal(this, handler);
    } //-- void marshal(org.xml.sax.ContentHandler) 

    /**
     * Method setAttributeListSets the value of field
     * 'attributeList'. The field 'attributeList' has the following
     * description: Semi-structured additional description of the
     * data contained in the entry.
     * 
     * @param attributeList the value of field 'attributeList'.
     */
    public void setAttributeList(org.cytoscape.coreplugin.psi_mi.schema.mi1.AttributeListType attributeList)
    {
        this._attributeList = attributeList;
    } //-- void setAttributeList(org.cytoscape.coreplugin.psi_mi.schema.mi1.AttributeListType)

    /**
     * Method setAvailabilityListSets the value of field
     * 'availabilityList'. The field 'availabilityList' has the
     * following description: Data availability statements, for
     * example copyrights
     * 
     * @param availabilityList the value of field 'availabilityList'
     */
    public void setAvailabilityList(org.cytoscape.coreplugin.psi_mi.schema.mi1.AvailabilityList availabilityList)
    {
        this._availabilityList = availabilityList;
    } //-- void setAvailabilityList(org.cytoscape.coreplugin.psi_mi.schema.mi1.AvailabilityList)

    /**
     * Method setExperimentList1Sets the value of field
     * 'experimentList1'. The field 'experimentList1' has the
     * following description: All experiments in which the
     * interactions of this entry have been determined
     * 
     * @param experimentList1 the value of field 'experimentList1'.
     */
    public void setExperimentList1(org.cytoscape.coreplugin.psi_mi.schema.mi1.ExperimentList1 experimentList1)
    {
        this._experimentList1 = experimentList1;
    } //-- void setExperimentList1(org.cytoscape.coreplugin.psi_mi.schema.mi1.ExperimentList1)

    /**
     * Method setInteractionListSets the value of field
     * 'interactionList'. The field 'interactionList' has the
     * following description: List of interactions
     * 
     * @param interactionList the value of field 'interactionList'.
     */
    public void setInteractionList(org.cytoscape.coreplugin.psi_mi.schema.mi1.InteractionList interactionList)
    {
        this._interactionList = interactionList;
    } //-- void setInteractionList(org.cytoscape.coreplugin.psi_mi.schema.mi1.InteractionList)

    /**
     * Method setInteractorListSets the value of field
     * 'interactorList'. The field 'interactorList' has the
     * following description: List of all interactors occurring in
     * the entry
     * 
     * @param interactorList the value of field 'interactorList'.
     */
    public void setInteractorList(org.cytoscape.coreplugin.psi_mi.schema.mi1.InteractorList interactorList)
    {
        this._interactorList = interactorList;
    } //-- void setInteractorList(org.cytoscape.coreplugin.psi_mi.schema.mi1.InteractorList)

    /**
     * Method setSourceSets the value of field 'source'. The field
     * 'source' has the following description: Desciption of the
     * source of the entry, usually an organisation
     * 
     * @param source the value of field 'source'.
     */
    public void setSource(org.cytoscape.coreplugin.psi_mi.schema.mi1.Source source)
    {
        this._source = source;
    } //-- void setSource(org.cytoscape.coreplugin.psi_mi.schema.mi1.Source)

    /**
     * Method unmarshalEntry
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi1.Entry unmarshalEntry(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi1.Entry) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi1.Entry.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.Entry unmarshalEntry(java.io.Reader)

    /**
     * Method validate
     */
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
