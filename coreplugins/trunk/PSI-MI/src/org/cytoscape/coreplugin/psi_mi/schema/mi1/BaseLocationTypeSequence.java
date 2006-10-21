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
 * Class BaseLocationTypeSequence.
 * 
 * @version $Revision$ $Date$
 */
public class BaseLocationTypeSequence implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _baseLocationTypeSequenceChoice
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi1.BaseLocationTypeSequenceChoice _baseLocationTypeSequenceChoice;

    /**
     * Field _baseLocationTypeSequenceChoice2
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi1.BaseLocationTypeSequenceChoice2 _baseLocationTypeSequenceChoice2;


      //----------------/
     //- Constructors -/
    //----------------/

    public BaseLocationTypeSequence() {
        super();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.BaseLocationTypeSequence()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method getBaseLocationTypeSequenceChoiceReturns the value of
     * field 'baseLocationTypeSequenceChoice'.
     * 
     * @return the value of field 'baseLocationTypeSequenceChoice'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.BaseLocationTypeSequenceChoice getBaseLocationTypeSequenceChoice()
    {
        return this._baseLocationTypeSequenceChoice;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.BaseLocationTypeSequenceChoice getBaseLocationTypeSequenceChoice()

    /**
     * Method getBaseLocationTypeSequenceChoice2Returns the value
     * of field 'baseLocationTypeSequenceChoice2'.
     * 
     * @return the value of field 'baseLocationTypeSequenceChoice2'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.BaseLocationTypeSequenceChoice2 getBaseLocationTypeSequenceChoice2()
    {
        return this._baseLocationTypeSequenceChoice2;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.BaseLocationTypeSequenceChoice2 getBaseLocationTypeSequenceChoice2()

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
    public void setBaseLocationTypeSequenceChoice(org.cytoscape.coreplugin.psi_mi.schema.mi1.BaseLocationTypeSequenceChoice baseLocationTypeSequenceChoice)
    {
        this._baseLocationTypeSequenceChoice = baseLocationTypeSequenceChoice;
    } //-- void setBaseLocationTypeSequenceChoice(org.cytoscape.coreplugin.psi_mi.schema.mi1.BaseLocationTypeSequenceChoice)

    /**
     * Method setBaseLocationTypeSequenceChoice2Sets the value of
     * field 'baseLocationTypeSequenceChoice2'.
     * 
     * @param baseLocationTypeSequenceChoice2 the value of field
     * 'baseLocationTypeSequenceChoice2'.
     */
    public void setBaseLocationTypeSequenceChoice2(org.cytoscape.coreplugin.psi_mi.schema.mi1.BaseLocationTypeSequenceChoice2 baseLocationTypeSequenceChoice2)
    {
        this._baseLocationTypeSequenceChoice2 = baseLocationTypeSequenceChoice2;
    } //-- void setBaseLocationTypeSequenceChoice2(org.cytoscape.coreplugin.psi_mi.schema.mi1.BaseLocationTypeSequenceChoice2)

    /**
     * Method unmarshalBaseLocationTypeSequence
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi1.BaseLocationTypeSequence unmarshalBaseLocationTypeSequence(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi1.BaseLocationTypeSequence) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi1.BaseLocationTypeSequence.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.BaseLocationTypeSequence unmarshalBaseLocationTypeSequence(java.io.Reader)

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
