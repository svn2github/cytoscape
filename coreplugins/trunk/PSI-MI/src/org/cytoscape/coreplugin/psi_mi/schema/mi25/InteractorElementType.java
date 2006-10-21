/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.4.3</a>, using an XML
 * Schema.
 * $Id$
 */

package org.cytoscape.coreplugin.psi_mi.schema.mi25;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * Describes a molecular interactor.
 * 
 * @version $Revision$ $Date$
 */
public class InteractorElementType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * All major objects have a numerical id which is unique to
     * that object within a PSI MI file. The object may be
     * repeated, though, e.g. in the denormalised representation.
     */
    private int _id;

    /**
     * keeps track of state for field: _id
     */
    private boolean _has_id;

    /**
     * Name(s). The short label is typically a short name that
     * could appear as a label on a diagram.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi25.NamesType _names;

    /**
     * An interactor should have an xref whenever possible. If the
     * interactor is not available in external databases, it must
     * be characterised within this object e.g. by its sequence.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi25.XrefType _xref;

    /**
     * The molecule type of the participant, e.g. protein. This
     * element is controlled by the PSI-MI controlled vocabulary
     * "interactorType", root term id MI:0313.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType _interactorType;

    /**
     * The normal source organism of the interactor.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi25.Organism _organism;

    /**
     * Sequence in uppercase
     */
    private java.lang.String _sequence;

    /**
     * Allows semi-structured additional annotation of the
     * interactor.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi25.AttributeListType _attributeList;


      //----------------/
     //- Constructors -/
    //----------------/

    public InteractorElementType() {
        super();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.InteractorElementType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method getAttributeListReturns the value of field
     * 'attributeList'. The field 'attributeList' has the following
     * description: Allows semi-structured additional annotation of
     * the interactor.
     * 
     * @return the value of field 'attributeList'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.AttributeListType getAttributeList()
    {
        return this._attributeList;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.AttributeListType getAttributeList()

    /**
     * Method getIdReturns the value of field 'id'. The field 'id'
     * has the following description: All major objects have a
     * numerical id which is unique to that object within a PSI MI
     * file. The object may be repeated, though, e.g. in the
     * denormalised representation.
     * 
     * @return the value of field 'id'.
     */
    public int getId()
    {
        return this._id;
    } //-- int getId() 

    /**
     * Method getInteractorTypeReturns the value of field
     * 'interactorType'. The field 'interactorType' has the
     * following description: The molecule type of the participant,
     * e.g. protein. This element is controlled by the PSI-MI
     * controlled vocabulary "interactorType", root term id
     * MI:0313.
     * 
     * @return the value of field 'interactorType'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType getInteractorType()
    {
        return this._interactorType;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType getInteractorType()

    /**
     * Method getNamesReturns the value of field 'names'. The field
     * 'names' has the following description: Name(s). The short
     * label is typically a short name that could appear as a label
     * on a diagram.
     * 
     * @return the value of field 'names'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.NamesType getNames()
    {
        return this._names;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.NamesType getNames()

    /**
     * Method getOrganismReturns the value of field 'organism'. The
     * field 'organism' has the following description: The normal
     * source organism of the interactor.
     * 
     * @return the value of field 'organism'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.Organism getOrganism()
    {
        return this._organism;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.Organism getOrganism()

    /**
     * Method getSequenceReturns the value of field 'sequence'. The
     * field 'sequence' has the following description: Sequence in
     * uppercase
     * 
     * @return the value of field 'sequence'.
     */
    public java.lang.String getSequence()
    {
        return this._sequence;
    } //-- java.lang.String getSequence() 

    /**
     * Method getXrefReturns the value of field 'xref'. The field
     * 'xref' has the following description: An interactor should
     * have an xref whenever possible. If the interactor is not
     * available in external databases, it must be characterised
     * within this object e.g. by its sequence.
     * 
     * @return the value of field 'xref'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.XrefType getXref()
    {
        return this._xref;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.XrefType getXref()

    /**
     * Method hasId
     */
    public boolean hasId()
    {
        return this._has_id;
    } //-- boolean hasId() 

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
     * description: Allows semi-structured additional annotation of
     * the interactor.
     * 
     * @param attributeList the value of field 'attributeList'.
     */
    public void setAttributeList(org.cytoscape.coreplugin.psi_mi.schema.mi25.AttributeListType attributeList)
    {
        this._attributeList = attributeList;
    } //-- void setAttributeList(org.cytoscape.coreplugin.psi_mi.schema.mi25.AttributeListType)

    /**
     * Method setIdSets the value of field 'id'. The field 'id' has
     * the following description: All major objects have a
     * numerical id which is unique to that object within a PSI MI
     * file. The object may be repeated, though, e.g. in the
     * denormalised representation.
     * 
     * @param id the value of field 'id'.
     */
    public void setId(int id)
    {
        this._id = id;
        this._has_id = true;
    } //-- void setId(int) 

    /**
     * Method setInteractorTypeSets the value of field
     * 'interactorType'. The field 'interactorType' has the
     * following description: The molecule type of the participant,
     * e.g. protein. This element is controlled by the PSI-MI
     * controlled vocabulary "interactorType", root term id
     * MI:0313.
     * 
     * @param interactorType the value of field 'interactorType'.
     */
    public void setInteractorType(org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType interactorType)
    {
        this._interactorType = interactorType;
    } //-- void setInteractorType(org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType)

    /**
     * Method setNamesSets the value of field 'names'. The field
     * 'names' has the following description: Name(s). The short
     * label is typically a short name that could appear as a label
     * on a diagram.
     * 
     * @param names the value of field 'names'.
     */
    public void setNames(org.cytoscape.coreplugin.psi_mi.schema.mi25.NamesType names)
    {
        this._names = names;
    } //-- void setNames(org.cytoscape.coreplugin.psi_mi.schema.mi25.NamesType)

    /**
     * Method setOrganismSets the value of field 'organism'. The
     * field 'organism' has the following description: The normal
     * source organism of the interactor.
     * 
     * @param organism the value of field 'organism'.
     */
    public void setOrganism(org.cytoscape.coreplugin.psi_mi.schema.mi25.Organism organism)
    {
        this._organism = organism;
    } //-- void setOrganism(org.cytoscape.coreplugin.psi_mi.schema.mi25.Organism)

    /**
     * Method setSequenceSets the value of field 'sequence'. The
     * field 'sequence' has the following description: Sequence in
     * uppercase
     * 
     * @param sequence the value of field 'sequence'.
     */
    public void setSequence(java.lang.String sequence)
    {
        this._sequence = sequence;
    } //-- void setSequence(java.lang.String) 

    /**
     * Method setXrefSets the value of field 'xref'. The field
     * 'xref' has the following description: An interactor should
     * have an xref whenever possible. If the interactor is not
     * available in external databases, it must be characterised
     * within this object e.g. by its sequence.
     * 
     * @param xref the value of field 'xref'.
     */
    public void setXref(org.cytoscape.coreplugin.psi_mi.schema.mi25.XrefType xref)
    {
        this._xref = xref;
    } //-- void setXref(org.cytoscape.coreplugin.psi_mi.schema.mi25.XrefType)

    /**
     * Method unmarshalInteractorElementType
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi25.InteractorElementType unmarshalInteractorElementType(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.InteractorElementType) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi25.InteractorElementType.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.InteractorElementType unmarshalInteractorElementType(java.io.Reader)

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
