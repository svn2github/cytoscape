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
 * Class BaseLocationTypeSequence2.
 * 
 * @version $Revision$ $Date$
 */
public class BaseLocationTypeSequence2 implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Attribute of the end positions, e.g. "certain" or "c-terminal
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType _endStatus;

    /**
     * Field _baseLocationTypeSequence2Choice
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationTypeSequence2Choice _baseLocationTypeSequence2Choice;


      //----------------/
     //- Constructors -/
    //----------------/

    public BaseLocationTypeSequence2() {
        super();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationTypeSequence2()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method getBaseLocationTypeSequence2ChoiceReturns the value
     * of field 'baseLocationTypeSequence2Choice'.
     * 
     * @return the value of field 'baseLocationTypeSequence2Choice'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationTypeSequence2Choice getBaseLocationTypeSequence2Choice()
    {
        return this._baseLocationTypeSequence2Choice;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationTypeSequence2Choice getBaseLocationTypeSequence2Choice()

    /**
     * Method getEndStatusReturns the value of field 'endStatus'.
     * The field 'endStatus' has the following description:
     * Attribute of the end positions, e.g. "certain" or
     * "c-terminal"
     * 
     * @return the value of field 'endStatus'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType getEndStatus()
    {
        return this._endStatus;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType getEndStatus()

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
     * Method setBaseLocationTypeSequence2ChoiceSets the value of
     * field 'baseLocationTypeSequence2Choice'.
     * 
     * @param baseLocationTypeSequence2Choice the value of field
     * 'baseLocationTypeSequence2Choice'.
     */
    public void setBaseLocationTypeSequence2Choice(org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationTypeSequence2Choice baseLocationTypeSequence2Choice)
    {
        this._baseLocationTypeSequence2Choice = baseLocationTypeSequence2Choice;
    } //-- void setBaseLocationTypeSequence2Choice(org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationTypeSequence2Choice)

    /**
     * Method setEndStatusSets the value of field 'endStatus'. The
     * field 'endStatus' has the following description: Attribute
     * of the end positions, e.g. "certain" or "c-terminal"
     * 
     * @param endStatus the value of field 'endStatus'.
     */
    public void setEndStatus(org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType endStatus)
    {
        this._endStatus = endStatus;
    } //-- void setEndStatus(org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType)

    /**
     * Method unmarshalBaseLocationTypeSequence2
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationTypeSequence2 unmarshalBaseLocationTypeSequence2(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationTypeSequence2) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationTypeSequence2.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationTypeSequence2 unmarshalBaseLocationTypeSequence2(java.io.Reader)

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
