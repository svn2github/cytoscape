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
 * Description of the Interactor. Refers to an already defined
 * interactor in this entry, fully describes an interactor, or
 * references another interaction defined in this entry, to allow
 * the hierarchical building up of complexes from subunits. 
 * 
 * @version $Revision$ $Date$
 */
public class ParticipantTypeChoice implements java.io.Serializable {


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

    /**
     * References an interaction described in this entry. Used for
     * the hierarchical buildup of complexes.
     */
    private int _interactionRef;

    /**
     * keeps track of state for field: _interactionRef
     */
    private boolean _has_interactionRef;


      //----------------/
     //- Constructors -/
    //----------------/

    public ParticipantTypeChoice() {
        super();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantTypeChoice()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method getInteractionRefReturns the value of field
     * 'interactionRef'. The field 'interactionRef' has the
     * following description: References an interaction described
     * in this entry. Used for the hierarchical buildup of
     * complexes.
     * 
     * @return the value of field 'interactionRef'.
     */
    public int getInteractionRef()
    {
        return this._interactionRef;
    } //-- int getInteractionRef() 

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
     * Method hasInteractionRef
     */
    public boolean hasInteractionRef()
    {
        return this._has_interactionRef;
    } //-- boolean hasInteractionRef() 

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
     * Method setInteractionRefSets the value of field
     * 'interactionRef'. The field 'interactionRef' has the
     * following description: References an interaction described
     * in this entry. Used for the hierarchical buildup of
     * complexes.
     * 
     * @param interactionRef the value of field 'interactionRef'.
     */
    public void setInteractionRef(int interactionRef)
    {
        this._interactionRef = interactionRef;
        this._has_interactionRef = true;
    } //-- void setInteractionRef(int) 

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
     * Method unmarshalParticipantTypeChoice
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantTypeChoice unmarshalParticipantTypeChoice(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantTypeChoice) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantTypeChoice.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantTypeChoice unmarshalParticipantTypeChoice(java.io.Reader)

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
