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
 * Either refer to an already defined availability statement in
 * this entry or insert description.
 * 
 * @version $Revision$ $Date$
 */
public class InteractionElementTypeChoice implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * References an availability statement already present in this
     * entry.
     */
    private int _availabilityRef;

    /**
     * keeps track of state for field: _availabilityRef
     */
    private boolean _has_availabilityRef;

    /**
     * Describes the availability of the interaction data. If no
     * availability is given, the data is assumed to be freely
     * available.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi25.AvailabilityType _availability;


      //----------------/
     //- Constructors -/
    //----------------/

    public InteractionElementTypeChoice() {
        super();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.InteractionElementTypeChoice()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method deleteAvailabilityRef
     */
    public void deleteAvailabilityRef()
    {
        this._has_availabilityRef= false;
    } //-- void deleteAvailabilityRef() 

    /**
     * Method getAvailabilityReturns the value of field
     * 'availability'. The field 'availability' has the following
     * description: Describes the availability of the interaction
     * data. If no availability is given, the data is assumed to be
     * freely available.
     * 
     * @return the value of field 'availability'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.AvailabilityType getAvailability()
    {
        return this._availability;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.AvailabilityType getAvailability()

    /**
     * Method getAvailabilityRefReturns the value of field
     * 'availabilityRef'. The field 'availabilityRef' has the
     * following description: References an availability statement
     * already present in this entry.
     * 
     * @return the value of field 'availabilityRef'.
     */
    public int getAvailabilityRef()
    {
        return this._availabilityRef;
    } //-- int getAvailabilityRef() 

    /**
     * Method hasAvailabilityRef
     */
    public boolean hasAvailabilityRef()
    {
        return this._has_availabilityRef;
    } //-- boolean hasAvailabilityRef() 

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
     * Method setAvailabilitySets the value of field
     * 'availability'. The field 'availability' has the following
     * description: Describes the availability of the interaction
     * data. If no availability is given, the data is assumed to be
     * freely available.
     * 
     * @param availability the value of field 'availability'.
     */
    public void setAvailability(org.cytoscape.coreplugin.psi_mi.schema.mi25.AvailabilityType availability)
    {
        this._availability = availability;
    } //-- void setAvailability(org.cytoscape.coreplugin.psi_mi.schema.mi25.AvailabilityType)

    /**
     * Method setAvailabilityRefSets the value of field
     * 'availabilityRef'. The field 'availabilityRef' has the
     * following description: References an availability statement
     * already present in this entry.
     * 
     * @param availabilityRef the value of field 'availabilityRef'.
     */
    public void setAvailabilityRef(int availabilityRef)
    {
        this._availabilityRef = availabilityRef;
        this._has_availabilityRef = true;
    } //-- void setAvailabilityRef(int) 

    /**
     * Method unmarshalInteractionElementTypeChoice
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi25.InteractionElementTypeChoice unmarshalInteractionElementTypeChoice(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.InteractionElementTypeChoice) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi25.InteractionElementTypeChoice.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.InteractionElementTypeChoice unmarshalInteractionElementTypeChoice(java.io.Reader)

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
