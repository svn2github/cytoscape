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

import java.util.Vector;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * Data availability statements, for example copyrights
 * 
 * @version $Revision$ $Date$
 */
public class AvailabilityList implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Describes data availability, e.g. through a copyright
     * statement. If no availability is given, the data is assumed
     * to be freely available.
     */
    private java.util.Vector _availabilityList = new Vector();


      //----------------/
     //- Constructors -/
    //----------------/

    public AvailabilityList() {
        super();
        _availabilityList = new Vector();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.AvailabilityList()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addAvailability
     * 
     * @param vAvailability
     */
    public void addAvailability(org.cytoscape.coreplugin.psi_mi.schema.mi1.AvailabilityType vAvailability)
        throws java.lang.IndexOutOfBoundsException
    {
        _availabilityList.addElement(vAvailability);
    } //-- void addAvailability(org.cytoscape.coreplugin.psi_mi.schema.mi1.AvailabilityType)

    /**
     * Method addAvailability
     * 
     * @param index
     * @param vAvailability
     */
    public void addAvailability(int index, org.cytoscape.coreplugin.psi_mi.schema.mi1.AvailabilityType vAvailability)
        throws java.lang.IndexOutOfBoundsException
    {
        _availabilityList.insertElementAt(vAvailability, index);
    } //-- void addAvailability(int, org.cytoscape.coreplugin.psi_mi.schema.mi1.AvailabilityType)

    /**
     * Method enumerateAvailability
     */
    public java.util.Enumeration enumerateAvailability()
    {
        return _availabilityList.elements();
    } //-- java.util.Enumeration enumerateAvailability() 

    /**
     * Method getAvailability
     * 
     * @param index
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.AvailabilityType getAvailability(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _availabilityList.size())) {
            throw new IndexOutOfBoundsException();
        }

        return (org.cytoscape.coreplugin.psi_mi.schema.mi1.AvailabilityType) _availabilityList.elementAt(index);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.AvailabilityType getAvailability(int)

    /**
     * Method getAvailability
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.AvailabilityType[] getAvailability()
    {
        int size = _availabilityList.size();
        org.cytoscape.coreplugin.psi_mi.schema.mi1.AvailabilityType[] mArray = new org.cytoscape.coreplugin.psi_mi.schema.mi1.AvailabilityType[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (org.cytoscape.coreplugin.psi_mi.schema.mi1.AvailabilityType) _availabilityList.elementAt(index);
        }
        return mArray;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.AvailabilityType[] getAvailability()

    /**
     * Method getAvailabilityCount
     */
    public int getAvailabilityCount()
    {
        return _availabilityList.size();
    } //-- int getAvailabilityCount() 

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
     * Method removeAllAvailability
     */
    public void removeAllAvailability()
    {
        _availabilityList.removeAllElements();
    } //-- void removeAllAvailability() 

    /**
     * Method removeAvailability
     * 
     * @param index
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.AvailabilityType removeAvailability(int index)
    {
        java.lang.Object obj = _availabilityList.elementAt(index);
        _availabilityList.removeElementAt(index);
        return (org.cytoscape.coreplugin.psi_mi.schema.mi1.AvailabilityType) obj;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.AvailabilityType removeAvailability(int)

    /**
     * Method setAvailability
     * 
     * @param index
     * @param vAvailability
     */
    public void setAvailability(int index, org.cytoscape.coreplugin.psi_mi.schema.mi1.AvailabilityType vAvailability)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _availabilityList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _availabilityList.setElementAt(vAvailability, index);
    } //-- void setAvailability(int, org.cytoscape.coreplugin.psi_mi.schema.mi1.AvailabilityType)

    /**
     * Method setAvailability
     * 
     * @param availabilityArray
     */
    public void setAvailability(org.cytoscape.coreplugin.psi_mi.schema.mi1.AvailabilityType[] availabilityArray)
    {
        //-- copy array
        _availabilityList.removeAllElements();
        for (int i = 0; i < availabilityArray.length; i++) {
            _availabilityList.addElement(availabilityArray[i]);
        }
    } //-- void setAvailability(org.cytoscape.coreplugin.psi_mi.schema.mi1.AvailabilityType)

    /**
     * Method unmarshalAvailabilityList
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi1.AvailabilityList unmarshalAvailabilityList(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi1.AvailabilityList) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi1.AvailabilityList.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.AvailabilityList unmarshalAvailabilityList(java.io.Reader)

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
