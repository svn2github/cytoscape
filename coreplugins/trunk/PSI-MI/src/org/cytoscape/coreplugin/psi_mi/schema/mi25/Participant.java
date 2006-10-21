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
 * Participant of the inferred interaction.
 * 
 * @version $Revision$ $Date$
 */
public class Participant implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _participantRef
     */
    private int _participantRef;

    /**
     * keeps track of state for field: _participantRef
     */
    private boolean _has_participantRef;

    /**
     * Field _participantFeatureRef
     */
    private int _participantFeatureRef;

    /**
     * keeps track of state for field: _participantFeatureRef
     */
    private boolean _has_participantFeatureRef;


      //----------------/
     //- Constructors -/
    //----------------/

    public Participant() {
        super();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.Participant()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method getParticipantFeatureRefReturns the value of field
     * 'participantFeatureRef'.
     * 
     * @return the value of field 'participantFeatureRef'.
     */
    public int getParticipantFeatureRef()
    {
        return this._participantFeatureRef;
    } //-- int getParticipantFeatureRef() 

    /**
     * Method getParticipantRefReturns the value of field
     * 'participantRef'.
     * 
     * @return the value of field 'participantRef'.
     */
    public int getParticipantRef()
    {
        return this._participantRef;
    } //-- int getParticipantRef() 

    /**
     * Method hasParticipantFeatureRef
     */
    public boolean hasParticipantFeatureRef()
    {
        return this._has_participantFeatureRef;
    } //-- boolean hasParticipantFeatureRef() 

    /**
     * Method hasParticipantRef
     */
    public boolean hasParticipantRef()
    {
        return this._has_participantRef;
    } //-- boolean hasParticipantRef() 

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
     * Method setParticipantFeatureRefSets the value of field
     * 'participantFeatureRef'.
     * 
     * @param participantFeatureRef the value of field
     * 'participantFeatureRef'.
     */
    public void setParticipantFeatureRef(int participantFeatureRef)
    {
        this._participantFeatureRef = participantFeatureRef;
        this._has_participantFeatureRef = true;
    } //-- void setParticipantFeatureRef(int) 

    /**
     * Method setParticipantRefSets the value of field
     * 'participantRef'.
     * 
     * @param participantRef the value of field 'participantRef'.
     */
    public void setParticipantRef(int participantRef)
    {
        this._participantRef = participantRef;
        this._has_participantRef = true;
    } //-- void setParticipantRef(int) 

    /**
     * Method unmarshalParticipant
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi25.Participant unmarshalParticipant(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.Participant) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi25.Participant.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.Participant unmarshalParticipant(java.io.Reader)

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
