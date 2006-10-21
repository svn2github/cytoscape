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
 * Class FeatureRangeList.
 * 
 * @version $Revision$ $Date$
 */
public class FeatureRangeList implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Location of the feature on the sequence of the interactor.
     * One feature may have more than one featureRange, used e.g.
     * for features which involve sequence positions close in the
     * folded, three-dimensional state of a protein, but
     * non-continuous along the sequence.
     */
    private java.util.Vector _featureRangeList;


      //----------------/
     //- Constructors -/
    //----------------/

    public FeatureRangeList() {
        super();
        _featureRangeList = new Vector();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.FeatureRangeList()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addFeatureRange
     * 
     * @param vFeatureRange
     */
    public void addFeatureRange(org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationType vFeatureRange)
        throws java.lang.IndexOutOfBoundsException
    {
        _featureRangeList.addElement(vFeatureRange);
    } //-- void addFeatureRange(org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationType)

    /**
     * Method addFeatureRange
     * 
     * @param index
     * @param vFeatureRange
     */
    public void addFeatureRange(int index, org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationType vFeatureRange)
        throws java.lang.IndexOutOfBoundsException
    {
        _featureRangeList.insertElementAt(vFeatureRange, index);
    } //-- void addFeatureRange(int, org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationType)

    /**
     * Method enumerateFeatureRange
     */
    public java.util.Enumeration enumerateFeatureRange()
    {
        return _featureRangeList.elements();
    } //-- java.util.Enumeration enumerateFeatureRange() 

    /**
     * Method getFeatureRange
     * 
     * @param index
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationType getFeatureRange(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _featureRangeList.size())) {
            throw new IndexOutOfBoundsException();
        }

        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationType) _featureRangeList.elementAt(index);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationType getFeatureRange(int)

    /**
     * Method getFeatureRange
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationType[] getFeatureRange()
    {
        int size = _featureRangeList.size();
        org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationType[] mArray = new org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationType[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationType) _featureRangeList.elementAt(index);
        }
        return mArray;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationType[] getFeatureRange()

    /**
     * Method getFeatureRangeCount
     */
    public int getFeatureRangeCount()
    {
        return _featureRangeList.size();
    } //-- int getFeatureRangeCount() 

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
     * Method removeAllFeatureRange
     */
    public void removeAllFeatureRange()
    {
        _featureRangeList.removeAllElements();
    } //-- void removeAllFeatureRange() 

    /**
     * Method removeFeatureRange
     * 
     * @param index
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationType removeFeatureRange(int index)
    {
        java.lang.Object obj = _featureRangeList.elementAt(index);
        _featureRangeList.removeElementAt(index);
        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationType) obj;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationType removeFeatureRange(int)

    /**
     * Method setFeatureRange
     * 
     * @param index
     * @param vFeatureRange
     */
    public void setFeatureRange(int index, org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationType vFeatureRange)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _featureRangeList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _featureRangeList.setElementAt(vFeatureRange, index);
    } //-- void setFeatureRange(int, org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationType)

    /**
     * Method setFeatureRange
     * 
     * @param featureRangeArray
     */
    public void setFeatureRange(org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationType[] featureRangeArray)
    {
        //-- copy array
        _featureRangeList.removeAllElements();
        for (int i = 0; i < featureRangeArray.length; i++) {
            _featureRangeList.addElement(featureRangeArray[i]);
        }
    } //-- void setFeatureRange(org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationType)

    /**
     * Method unmarshalFeatureRangeList
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi25.FeatureRangeList unmarshalFeatureRangeList(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.FeatureRangeList) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi25.FeatureRangeList.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.FeatureRangeList unmarshalFeatureRangeList(java.io.Reader)

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
