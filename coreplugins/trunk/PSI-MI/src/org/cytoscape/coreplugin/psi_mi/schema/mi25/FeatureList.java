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
 * Sequence features relevant for the interaction, for example
 * binding domains, and experimental modifications, e.g. protein
 * tags. 
 * 
 * @version $Revision$ $Date$
 */
public class FeatureList implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _featureList
     */
    private java.util.Vector _featureList;


      //----------------/
     //- Constructors -/
    //----------------/

    public FeatureList() {
        super();
        _featureList = new Vector();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.FeatureList()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addFeature
     * 
     * @param vFeature
     */
    public void addFeature(org.cytoscape.coreplugin.psi_mi.schema.mi25.FeatureElementType vFeature)
        throws java.lang.IndexOutOfBoundsException
    {
        _featureList.addElement(vFeature);
    } //-- void addFeature(org.cytoscape.coreplugin.psi_mi.schema.mi25.FeatureElementType)

    /**
     * Method addFeature
     * 
     * @param index
     * @param vFeature
     */
    public void addFeature(int index, org.cytoscape.coreplugin.psi_mi.schema.mi25.FeatureElementType vFeature)
        throws java.lang.IndexOutOfBoundsException
    {
        _featureList.insertElementAt(vFeature, index);
    } //-- void addFeature(int, org.cytoscape.coreplugin.psi_mi.schema.mi25.FeatureElementType)

    /**
     * Method enumerateFeature
     */
    public java.util.Enumeration enumerateFeature()
    {
        return _featureList.elements();
    } //-- java.util.Enumeration enumerateFeature() 

    /**
     * Method getFeature
     * 
     * @param index
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.FeatureElementType getFeature(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _featureList.size())) {
            throw new IndexOutOfBoundsException();
        }

        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.FeatureElementType) _featureList.elementAt(index);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.FeatureElementType getFeature(int)

    /**
     * Method getFeature
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.FeatureElementType[] getFeature()
    {
        int size = _featureList.size();
        org.cytoscape.coreplugin.psi_mi.schema.mi25.FeatureElementType[] mArray = new org.cytoscape.coreplugin.psi_mi.schema.mi25.FeatureElementType[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (org.cytoscape.coreplugin.psi_mi.schema.mi25.FeatureElementType) _featureList.elementAt(index);
        }
        return mArray;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.FeatureElementType[] getFeature()

    /**
     * Method getFeatureCount
     */
    public int getFeatureCount()
    {
        return _featureList.size();
    } //-- int getFeatureCount() 

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
     * Method removeAllFeature
     */
    public void removeAllFeature()
    {
        _featureList.removeAllElements();
    } //-- void removeAllFeature() 

    /**
     * Method removeFeature
     * 
     * @param index
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.FeatureElementType removeFeature(int index)
    {
        java.lang.Object obj = _featureList.elementAt(index);
        _featureList.removeElementAt(index);
        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.FeatureElementType) obj;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.FeatureElementType removeFeature(int)

    /**
     * Method setFeature
     * 
     * @param index
     * @param vFeature
     */
    public void setFeature(int index, org.cytoscape.coreplugin.psi_mi.schema.mi25.FeatureElementType vFeature)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _featureList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _featureList.setElementAt(vFeature, index);
    } //-- void setFeature(int, org.cytoscape.coreplugin.psi_mi.schema.mi25.FeatureElementType)

    /**
     * Method setFeature
     * 
     * @param featureArray
     */
    public void setFeature(org.cytoscape.coreplugin.psi_mi.schema.mi25.FeatureElementType[] featureArray)
    {
        //-- copy array
        _featureList.removeAllElements();
        for (int i = 0; i < featureArray.length; i++) {
            _featureList.addElement(featureArray[i]);
        }
    } //-- void setFeature(org.cytoscape.coreplugin.psi_mi.schema.mi25.FeatureElementType)

    /**
     * Method unmarshalFeatureList
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi25.FeatureList unmarshalFeatureList(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.FeatureList) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi25.FeatureList.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.FeatureList unmarshalFeatureList(java.io.Reader)

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
