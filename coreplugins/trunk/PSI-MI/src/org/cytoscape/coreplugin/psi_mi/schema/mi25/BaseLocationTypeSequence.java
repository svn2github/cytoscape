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
 * Class BaseLocationTypeSequence.
 * 
 * @version $Revision$ $Date$
 */
public class BaseLocationTypeSequence implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Attribute of the start positions, e.g. "certain" or
     * "n-terminal"
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType _startStatus;

    /**
     * Field _baseLocationTypeSequenceChoice
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationTypeSequenceChoice _baseLocationTypeSequenceChoice;


      //----------------/
     //- Constructors -/
    //----------------/

    public BaseLocationTypeSequence() {
        super();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationTypeSequence()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method getBaseLocationTypeSequenceChoiceReturns the value of
     * field 'baseLocationTypeSequenceChoice'.
     * 
     * @return the value of field 'baseLocationTypeSequenceChoice'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationTypeSequenceChoice getBaseLocationTypeSequenceChoice()
    {
        return this._baseLocationTypeSequenceChoice;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationTypeSequenceChoice getBaseLocationTypeSequenceChoice()

    /**
     * Method getStartStatusReturns the value of field
     * 'startStatus'. The field 'startStatus' has the following
     * description: Attribute of the start positions, e.g.
     * "certain" or "n-terminal"
     * 
     * @return the value of field 'startStatus'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType getStartStatus()
    {
        return this._startStatus;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType getStartStatus()

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
     * Method setBaseLocationTypeSequenceChoiceSets the value of
     * field 'baseLocationTypeSequenceChoice'.
     * 
     * @param baseLocationTypeSequenceChoice the value of field
     * 'baseLocationTypeSequenceChoice'.
     */
    public void setBaseLocationTypeSequenceChoice(org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationTypeSequenceChoice baseLocationTypeSequenceChoice)
    {
        this._baseLocationTypeSequenceChoice = baseLocationTypeSequenceChoice;
    } //-- void setBaseLocationTypeSequenceChoice(org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationTypeSequenceChoice)

    /**
     * Method setStartStatusSets the value of field 'startStatus'.
     * The field 'startStatus' has the following description:
     * Attribute of the start positions, e.g. "certain" or
     * "n-terminal"
     * 
     * @param startStatus the value of field 'startStatus'.
     */
    public void setStartStatus(org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType startStatus)
    {
        this._startStatus = startStatus;
    } //-- void setStartStatus(org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType)

    /**
     * Method unmarshalBaseLocationTypeSequence
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationTypeSequence unmarshalBaseLocationTypeSequence(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationTypeSequence) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationTypeSequence.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationTypeSequence unmarshalBaseLocationTypeSequence(java.io.Reader)

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
