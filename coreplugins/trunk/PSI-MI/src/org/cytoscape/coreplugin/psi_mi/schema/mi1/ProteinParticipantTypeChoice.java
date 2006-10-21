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
 * Either refer to an already defined protein interactor in this
 * entry or insert description.
 * 
 * @version $Revision$ $Date$
 */
public class ProteinParticipantTypeChoice implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * References an interactor described in the interactorList of
     * the entry
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi1.RefType _proteinInteractorRef;

    /**
     * Fully describes an interactor
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinInteractorType _proteinInteractor;


      //----------------/
     //- Constructors -/
    //----------------/

    public ProteinParticipantTypeChoice() {
        super();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinParticipantTypeChoice()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method getProteinInteractorReturns the value of field
     * 'proteinInteractor'. The field 'proteinInteractor' has the
     * following description: Fully describes an interactor
     * 
     * @return the value of field 'proteinInteractor'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinInteractorType getProteinInteractor()
    {
        return this._proteinInteractor;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinInteractorType getProteinInteractor()

    /**
     * Method getProteinInteractorRefReturns the value of field
     * 'proteinInteractorRef'. The field 'proteinInteractorRef' has
     * the following description: References an interactor
     * described in the interactorList of the entry
     * 
     * @return the value of field 'proteinInteractorRef'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.RefType getProteinInteractorRef()
    {
        return this._proteinInteractorRef;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.RefType getProteinInteractorRef()

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
     * Method setProteinInteractorSets the value of field
     * 'proteinInteractor'. The field 'proteinInteractor' has the
     * following description: Fully describes an interactor
     * 
     * @param proteinInteractor the value of field
     * 'proteinInteractor'.
     */
    public void setProteinInteractor(org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinInteractorType proteinInteractor)
    {
        this._proteinInteractor = proteinInteractor;
    } //-- void setProteinInteractor(org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinInteractorType)

    /**
     * Method setProteinInteractorRefSets the value of field
     * 'proteinInteractorRef'. The field 'proteinInteractorRef' has
     * the following description: References an interactor
     * described in the interactorList of the entry
     * 
     * @param proteinInteractorRef the value of field
     * 'proteinInteractorRef'.
     */
    public void setProteinInteractorRef(org.cytoscape.coreplugin.psi_mi.schema.mi1.RefType proteinInteractorRef)
    {
        this._proteinInteractorRef = proteinInteractorRef;
    } //-- void setProteinInteractorRef(org.cytoscape.coreplugin.psi_mi.schema.mi1.RefType)

    /**
     * Method unmarshalProteinParticipantTypeChoice
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinParticipantTypeChoice unmarshalProteinParticipantTypeChoice(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinParticipantTypeChoice) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinParticipantTypeChoice.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinParticipantTypeChoice unmarshalProteinParticipantTypeChoice(java.io.Reader)

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
