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
 * The host organism(s) in which the protein has been expressed. If
 * not given, it is assumed to be the native species of the
 * protein.
 * 
 * @version $Revision$ $Date$
 */
public class HostOrganismList implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _hostOrganismList
     */
    private java.util.Vector _hostOrganismList;


      //----------------/
     //- Constructors -/
    //----------------/

    public HostOrganismList() {
        super();
        _hostOrganismList = new Vector();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.HostOrganismList()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addHostOrganism
     * 
     * @param vHostOrganism
     */
    public void addHostOrganism(org.cytoscape.coreplugin.psi_mi.schema.mi25.HostOrganism vHostOrganism)
        throws java.lang.IndexOutOfBoundsException
    {
        _hostOrganismList.addElement(vHostOrganism);
    } //-- void addHostOrganism(org.cytoscape.coreplugin.psi_mi.schema.mi25.HostOrganism)

    /**
     * Method addHostOrganism
     * 
     * @param index
     * @param vHostOrganism
     */
    public void addHostOrganism(int index, org.cytoscape.coreplugin.psi_mi.schema.mi25.HostOrganism vHostOrganism)
        throws java.lang.IndexOutOfBoundsException
    {
        _hostOrganismList.insertElementAt(vHostOrganism, index);
    } //-- void addHostOrganism(int, org.cytoscape.coreplugin.psi_mi.schema.mi25.HostOrganism)

    /**
     * Method enumerateHostOrganism
     */
    public java.util.Enumeration enumerateHostOrganism()
    {
        return _hostOrganismList.elements();
    } //-- java.util.Enumeration enumerateHostOrganism() 

    /**
     * Method getHostOrganism
     * 
     * @param index
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.HostOrganism getHostOrganism(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _hostOrganismList.size())) {
            throw new IndexOutOfBoundsException();
        }

        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.HostOrganism) _hostOrganismList.elementAt(index);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.HostOrganism getHostOrganism(int)

    /**
     * Method getHostOrganism
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.HostOrganism[] getHostOrganism()
    {
        int size = _hostOrganismList.size();
        org.cytoscape.coreplugin.psi_mi.schema.mi25.HostOrganism[] mArray = new org.cytoscape.coreplugin.psi_mi.schema.mi25.HostOrganism[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (org.cytoscape.coreplugin.psi_mi.schema.mi25.HostOrganism) _hostOrganismList.elementAt(index);
        }
        return mArray;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.HostOrganism[] getHostOrganism()

    /**
     * Method getHostOrganismCount
     */
    public int getHostOrganismCount()
    {
        return _hostOrganismList.size();
    } //-- int getHostOrganismCount() 

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
     * Method removeAllHostOrganism
     */
    public void removeAllHostOrganism()
    {
        _hostOrganismList.removeAllElements();
    } //-- void removeAllHostOrganism() 

    /**
     * Method removeHostOrganism
     * 
     * @param index
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.HostOrganism removeHostOrganism(int index)
    {
        java.lang.Object obj = _hostOrganismList.elementAt(index);
        _hostOrganismList.removeElementAt(index);
        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.HostOrganism) obj;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.HostOrganism removeHostOrganism(int)

    /**
     * Method setHostOrganism
     * 
     * @param index
     * @param vHostOrganism
     */
    public void setHostOrganism(int index, org.cytoscape.coreplugin.psi_mi.schema.mi25.HostOrganism vHostOrganism)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _hostOrganismList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _hostOrganismList.setElementAt(vHostOrganism, index);
    } //-- void setHostOrganism(int, org.cytoscape.coreplugin.psi_mi.schema.mi25.HostOrganism)

    /**
     * Method setHostOrganism
     * 
     * @param hostOrganismArray
     */
    public void setHostOrganism(org.cytoscape.coreplugin.psi_mi.schema.mi25.HostOrganism[] hostOrganismArray)
    {
        //-- copy array
        _hostOrganismList.removeAllElements();
        for (int i = 0; i < hostOrganismArray.length; i++) {
            _hostOrganismList.addElement(hostOrganismArray[i]);
        }
    } //-- void setHostOrganism(org.cytoscape.coreplugin.psi_mi.schema.mi25.HostOrganism)

    /**
     * Method unmarshalHostOrganismList
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi25.HostOrganismList unmarshalHostOrganismList(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.HostOrganismList) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi25.HostOrganismList.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.HostOrganismList unmarshalHostOrganismList(java.io.Reader)

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
