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

import java.util.Vector;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * The method(s) by which this participant has been determined. If
 * this element is present, its value supersedes
 * experimentDescription/ participantIdentificationMethod.
 * 
 * @version $Revision$ $Date$
 */
public class ParticipantIdentificationMethodList implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Experimental method to determine the interactors involved in
     * the interaction. This element is controlled by the PSI-MI
     * controlled vocabulary "participant identification method",
     * root term id MI:0002.
     */
    private java.util.Vector _participantIdentificationMethodList;


      //----------------/
     //- Constructors -/
    //----------------/

    public ParticipantIdentificationMethodList() {
        super();
        _participantIdentificationMethodList = new Vector();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantIdentificationMethodList()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addParticipantIdentificationMethod
     * 
     * @param vParticipantIdentificationMethod
     */
    public void addParticipantIdentificationMethod(org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantIdentificationMethod vParticipantIdentificationMethod)
        throws java.lang.IndexOutOfBoundsException
    {
        _participantIdentificationMethodList.addElement(vParticipantIdentificationMethod);
    } //-- void addParticipantIdentificationMethod(org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantIdentificationMethod)

    /**
     * Method addParticipantIdentificationMethod
     * 
     * @param index
     * @param vParticipantIdentificationMethod
     */
    public void addParticipantIdentificationMethod(int index, org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantIdentificationMethod vParticipantIdentificationMethod)
        throws java.lang.IndexOutOfBoundsException
    {
        _participantIdentificationMethodList.insertElementAt(vParticipantIdentificationMethod, index);
    } //-- void addParticipantIdentificationMethod(int, org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantIdentificationMethod)

    /**
     * Method enumerateParticipantIdentificationMethod
     */
    public java.util.Enumeration enumerateParticipantIdentificationMethod()
    {
        return _participantIdentificationMethodList.elements();
    } //-- java.util.Enumeration enumerateParticipantIdentificationMethod() 

    /**
     * Method getParticipantIdentificationMethod
     * 
     * @param index
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantIdentificationMethod getParticipantIdentificationMethod(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _participantIdentificationMethodList.size())) {
            throw new IndexOutOfBoundsException();
        }

        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantIdentificationMethod) _participantIdentificationMethodList.elementAt(index);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantIdentificationMethod getParticipantIdentificationMethod(int)

    /**
     * Method getParticipantIdentificationMethod
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantIdentificationMethod[] getParticipantIdentificationMethod()
    {
        int size = _participantIdentificationMethodList.size();
        org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantIdentificationMethod[] mArray = new org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantIdentificationMethod[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantIdentificationMethod) _participantIdentificationMethodList.elementAt(index);
        }
        return mArray;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantIdentificationMethod[] getParticipantIdentificationMethod()

    /**
     * Method getParticipantIdentificationMethodCount
     */
    public int getParticipantIdentificationMethodCount()
    {
        return _participantIdentificationMethodList.size();
    } //-- int getParticipantIdentificationMethodCount() 

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
     * Method removeAllParticipantIdentificationMethod
     */
    public void removeAllParticipantIdentificationMethod()
    {
        _participantIdentificationMethodList.removeAllElements();
    } //-- void removeAllParticipantIdentificationMethod() 

    /**
     * Method removeParticipantIdentificationMethod
     * 
     * @param index
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantIdentificationMethod removeParticipantIdentificationMethod(int index)
    {
        java.lang.Object obj = _participantIdentificationMethodList.elementAt(index);
        _participantIdentificationMethodList.removeElementAt(index);
        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantIdentificationMethod) obj;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantIdentificationMethod removeParticipantIdentificationMethod(int)

    /**
     * Method setParticipantIdentificationMethod
     * 
     * @param index
     * @param vParticipantIdentificationMethod
     */
    public void setParticipantIdentificationMethod(int index, org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantIdentificationMethod vParticipantIdentificationMethod)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _participantIdentificationMethodList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _participantIdentificationMethodList.setElementAt(vParticipantIdentificationMethod, index);
    } //-- void setParticipantIdentificationMethod(int, org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantIdentificationMethod)

    /**
     * Method setParticipantIdentificationMethod
     * 
     * @param participantIdentificationMethodArray
     */
    public void setParticipantIdentificationMethod(org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantIdentificationMethod[] participantIdentificationMethodArray)
    {
        //-- copy array
        _participantIdentificationMethodList.removeAllElements();
        for (int i = 0; i < participantIdentificationMethodArray.length; i++) {
            _participantIdentificationMethodList.addElement(participantIdentificationMethodArray[i]);
        }
    } //-- void setParticipantIdentificationMethod(org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantIdentificationMethod)

    /**
     * Method unmarshalParticipantIdentificationMethodList
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantIdentificationMethodList unmarshalParticipantIdentificationMethodList(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantIdentificationMethodList) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantIdentificationMethodList.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantIdentificationMethodList unmarshalParticipantIdentificationMethodList(java.io.Reader)

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
