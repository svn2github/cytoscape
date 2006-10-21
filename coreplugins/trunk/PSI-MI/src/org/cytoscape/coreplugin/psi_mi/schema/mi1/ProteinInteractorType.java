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
 * Describes a protein interactor.
 * 
 * @version $Revision$ $Date$
 */
public class ProteinInteractorType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _id
     */
    private java.lang.String _id;

    /**
     * Protein name(s). The short label is typically a short name
     * that could appear as a label on a diagram.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi1.NamesType _names;

    /**
     * An interactor should have an xref whenever possible. If the
     * interactor is not available in external databases, it must
     * be characterised within this proteinInteractor object e.g.
     * by its sequence.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi1.XrefType _xref;

    /**
     * The normal source organism of the interactor. If a human
     * protein has been expressed in yeast, this attribute would
     * describe human.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi1.Organism _organism;

    /**
     * Amino acid sequence in uppercase
     */
    private java.lang.String _sequence;


      //----------------/
     //- Constructors -/
    //----------------/

    public ProteinInteractorType() {
        super();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinInteractorType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method getIdReturns the value of field 'id'.
     * 
     * @return the value of field 'id'.
     */
    public java.lang.String getId()
    {
        return this._id;
    } //-- java.lang.String getId() 

    /**
     * Method getNamesReturns the value of field 'names'. The field
     * 'names' has the following description: Protein name(s). The
     * short label is typically a short name that could appear as a
     * label on a diagram.
     * 
     * @return the value of field 'names'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.NamesType getNames()
    {
        return this._names;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.NamesType getNames()

    /**
     * Method getOrganismReturns the value of field 'organism'. The
     * field 'organism' has the following description: The normal
     * source organism of the interactor. If a human protein has
     * been expressed in yeast, this attribute would describe
     * human.
     * 
     * @return the value of field 'organism'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.Organism getOrganism()
    {
        return this._organism;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.Organism getOrganism()

    /**
     * Method getSequenceReturns the value of field 'sequence'. The
     * field 'sequence' has the following description: Amino acid
     * sequence in uppercase
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
     * within this proteinInteractor object e.g. by its sequence.
     * 
     * @return the value of field 'xref'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.XrefType getXref()
    {
        return this._xref;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.XrefType getXref()

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
     * Method setIdSets the value of field 'id'.
     * 
     * @param id the value of field 'id'.
     */
    public void setId(java.lang.String id)
    {
        this._id = id;
    } //-- void setId(java.lang.String) 

    /**
     * Method setNamesSets the value of field 'names'. The field
     * 'names' has the following description: Protein name(s). The
     * short label is typically a short name that could appear as a
     * label on a diagram.
     * 
     * @param names the value of field 'names'.
     */
    public void setNames(org.cytoscape.coreplugin.psi_mi.schema.mi1.NamesType names)
    {
        this._names = names;
    } //-- void setNames(org.cytoscape.coreplugin.psi_mi.schema.mi1.NamesType)

    /**
     * Method setOrganismSets the value of field 'organism'. The
     * field 'organism' has the following description: The normal
     * source organism of the interactor. If a human protein has
     * been expressed in yeast, this attribute would describe
     * human.
     * 
     * @param organism the value of field 'organism'.
     */
    public void setOrganism(org.cytoscape.coreplugin.psi_mi.schema.mi1.Organism organism)
    {
        this._organism = organism;
    } //-- void setOrganism(org.cytoscape.coreplugin.psi_mi.schema.mi1.Organism)

    /**
     * Method setSequenceSets the value of field 'sequence'. The
     * field 'sequence' has the following description: Amino acid
     * sequence in uppercase
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
     * within this proteinInteractor object e.g. by its sequence.
     * 
     * @param xref the value of field 'xref'.
     */
    public void setXref(org.cytoscape.coreplugin.psi_mi.schema.mi1.XrefType xref)
    {
        this._xref = xref;
    } //-- void setXref(org.cytoscape.coreplugin.psi_mi.schema.mi1.XrefType)

    /**
     * Method unmarshalProteinInteractorType
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinInteractorType unmarshalProteinInteractorType(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinInteractorType) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinInteractorType.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinInteractorType unmarshalProteinInteractorType(java.io.Reader)

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
