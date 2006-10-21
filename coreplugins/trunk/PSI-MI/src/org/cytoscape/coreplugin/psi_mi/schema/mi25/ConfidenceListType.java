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
 * A list of confidence values.
 * 
 * @version $Revision$ $Date$
 */
public class ConfidenceListType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _confidenceList
     */
    private java.util.Vector _confidenceList;


      //----------------/
     //- Constructors -/
    //----------------/

    public ConfidenceListType() {
        super();
        _confidenceList = new Vector();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ConfidenceListType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addConfidence
     * 
     * @param vConfidence
     */
    public void addConfidence(org.cytoscape.coreplugin.psi_mi.schema.mi25.Confidence vConfidence)
        throws java.lang.IndexOutOfBoundsException
    {
        _confidenceList.addElement(vConfidence);
    } //-- void addConfidence(org.cytoscape.coreplugin.psi_mi.schema.mi25.Confidence)

    /**
     * Method addConfidence
     * 
     * @param index
     * @param vConfidence
     */
    public void addConfidence(int index, org.cytoscape.coreplugin.psi_mi.schema.mi25.Confidence vConfidence)
        throws java.lang.IndexOutOfBoundsException
    {
        _confidenceList.insertElementAt(vConfidence, index);
    } //-- void addConfidence(int, org.cytoscape.coreplugin.psi_mi.schema.mi25.Confidence)

    /**
     * Method enumerateConfidence
     */
    public java.util.Enumeration enumerateConfidence()
    {
        return _confidenceList.elements();
    } //-- java.util.Enumeration enumerateConfidence() 

    /**
     * Method getConfidence
     * 
     * @param index
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.Confidence getConfidence(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _confidenceList.size())) {
            throw new IndexOutOfBoundsException();
        }

        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.Confidence) _confidenceList.elementAt(index);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.Confidence getConfidence(int)

    /**
     * Method getConfidence
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.Confidence[] getConfidence()
    {
        int size = _confidenceList.size();
        org.cytoscape.coreplugin.psi_mi.schema.mi25.Confidence[] mArray = new org.cytoscape.coreplugin.psi_mi.schema.mi25.Confidence[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (org.cytoscape.coreplugin.psi_mi.schema.mi25.Confidence) _confidenceList.elementAt(index);
        }
        return mArray;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.Confidence[] getConfidence()

    /**
     * Method getConfidenceCount
     */
    public int getConfidenceCount()
    {
        return _confidenceList.size();
    } //-- int getConfidenceCount() 

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
     * Method removeAllConfidence
     */
    public void removeAllConfidence()
    {
        _confidenceList.removeAllElements();
    } //-- void removeAllConfidence() 

    /**
     * Method removeConfidence
     * 
     * @param index
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.Confidence removeConfidence(int index)
    {
        java.lang.Object obj = _confidenceList.elementAt(index);
        _confidenceList.removeElementAt(index);
        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.Confidence) obj;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.Confidence removeConfidence(int)

    /**
     * Method setConfidence
     * 
     * @param index
     * @param vConfidence
     */
    public void setConfidence(int index, org.cytoscape.coreplugin.psi_mi.schema.mi25.Confidence vConfidence)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _confidenceList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _confidenceList.setElementAt(vConfidence, index);
    } //-- void setConfidence(int, org.cytoscape.coreplugin.psi_mi.schema.mi25.Confidence)

    /**
     * Method setConfidence
     * 
     * @param confidenceArray
     */
    public void setConfidence(org.cytoscape.coreplugin.psi_mi.schema.mi25.Confidence[] confidenceArray)
    {
        //-- copy array
        _confidenceList.removeAllElements();
        for (int i = 0; i < confidenceArray.length; i++) {
            _confidenceList.addElement(confidenceArray[i]);
        }
    } //-- void setConfidence(org.cytoscape.coreplugin.psi_mi.schema.mi25.Confidence)

    /**
     * Method unmarshalConfidenceListType
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi25.ConfidenceListType unmarshalConfidenceListType(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.ConfidenceListType) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi25.ConfidenceListType.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ConfidenceListType unmarshalConfidenceListType(java.io.Reader)

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
