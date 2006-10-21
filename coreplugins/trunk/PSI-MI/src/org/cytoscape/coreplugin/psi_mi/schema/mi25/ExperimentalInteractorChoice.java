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
 * Either refer to an already defined protein interactor in this
 * entry or insert description.
 * 
 * @version $Revision$ $Date$
 */
public class ExperimentalInteractorChoice implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * References an interactor described in the interactorList of
     * the entry
     */
    private int _interactorRef;

    /**
     * keeps track of state for field: _interactorRef
     */
    private boolean _has_interactorRef;

    /**
     * Fully describes an interactor
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi25.InteractorElementType _interactor;


      //----------------/
     //- Constructors -/
    //----------------/

    public ExperimentalInteractorChoice() {
        super();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalInteractorChoice()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method getInteractorReturns the value of field 'interactor'.
     * The field 'interactor' has the following description: Fully
     * describes an interactor
     * 
     * @return the value of field 'interactor'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.InteractorElementType getInteractor()
    {
        return this._interactor;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.InteractorElementType getInteractor()

    /**
     * Method getInteractorRefReturns the value of field
     * 'interactorRef'. The field 'interactorRef' has the following
     * description: References an interactor described in the
     * interactorList of the entry
     * 
     * @return the value of field 'interactorRef'.
     */
    public int getInteractorRef()
    {
        return this._interactorRef;
    } //-- int getInteractorRef() 

    /**
     * Method hasInteractorRef
     */
    public boolean hasInteractorRef()
    {
        return this._has_interactorRef;
    } //-- boolean hasInteractorRef() 

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
     * Method setInteractorSets the value of field 'interactor'.
     * The field 'interactor' has the following description: Fully
     * describes an interactor
     * 
     * @param interactor the value of field 'interactor'.
     */
    public void setInteractor(org.cytoscape.coreplugin.psi_mi.schema.mi25.InteractorElementType interactor)
    {
        this._interactor = interactor;
    } //-- void setInteractor(org.cytoscape.coreplugin.psi_mi.schema.mi25.InteractorElementType)

    /**
     * Method setInteractorRefSets the value of field
     * 'interactorRef'. The field 'interactorRef' has the following
     * description: References an interactor described in the
     * interactorList of the entry
     * 
     * @param interactorRef the value of field 'interactorRef'.
     */
    public void setInteractorRef(int interactorRef)
    {
        this._interactorRef = interactorRef;
        this._has_interactorRef = true;
    } //-- void setInteractorRef(int) 

    /**
     * Method unmarshalExperimentalInteractorChoice
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalInteractorChoice unmarshalExperimentalInteractorChoice(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalInteractorChoice) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalInteractorChoice.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalInteractorChoice unmarshalExperimentalInteractorChoice(java.io.Reader)

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
