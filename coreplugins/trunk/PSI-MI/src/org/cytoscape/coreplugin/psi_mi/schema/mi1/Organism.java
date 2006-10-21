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
 * The normal source organism of the interactor. If a human protein
 * has been expressed in yeast, this attribute would describe
 * human.
 * 
 * @version $Revision$ $Date$
 */
public class Organism extends org.cytoscape.coreplugin.psi_mi.schema.mi1.BioSourceType
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _ncbiTaxId
     */
    private int _ncbiTaxId;

    /**
     * keeps track of state for field: _ncbiTaxId
     */
    private boolean _has_ncbiTaxId;


      //----------------/
     //- Constructors -/
    //----------------/

    public Organism() {
        super();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.Organism()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method getNcbiTaxIdReturns the value of field 'ncbiTaxId'.
     * 
     * @return the value of field 'ncbiTaxId'.
     */
    public int getNcbiTaxId()
    {
        return this._ncbiTaxId;
    } //-- int getNcbiTaxId() 

    /**
     * Method hasNcbiTaxId
     */
    public boolean hasNcbiTaxId()
    {
        return this._has_ncbiTaxId;
    } //-- boolean hasNcbiTaxId() 

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
     * Method setNcbiTaxIdSets the value of field 'ncbiTaxId'.
     * 
     * @param ncbiTaxId the value of field 'ncbiTaxId'.
     */
    public void setNcbiTaxId(int ncbiTaxId)
    {
        this._ncbiTaxId = ncbiTaxId;
        this._has_ncbiTaxId = true;
    } //-- void setNcbiTaxId(int) 

    /**
     * Method unmarshalOrganism
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi1.Organism unmarshalOrganism(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi1.Organism) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi1.Organism.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.Organism unmarshalOrganism(java.io.Reader)

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
