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
 * Lists parameters which are relevant for the Interaction, e.g.
 * kinetics.
 * 
 * @version $Revision$ $Date$
 */
public class ParameterList implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _parameterList
     */
    private java.util.Vector _parameterList;


      //----------------/
     //- Constructors -/
    //----------------/

    public ParameterList() {
        super();
        _parameterList = new Vector();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ParameterList()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addParameter
     * 
     * @param vParameter
     */
    public void addParameter(org.cytoscape.coreplugin.psi_mi.schema.mi25.Parameter vParameter)
        throws java.lang.IndexOutOfBoundsException
    {
        _parameterList.addElement(vParameter);
    } //-- void addParameter(org.cytoscape.coreplugin.psi_mi.schema.mi25.Parameter)

    /**
     * Method addParameter
     * 
     * @param index
     * @param vParameter
     */
    public void addParameter(int index, org.cytoscape.coreplugin.psi_mi.schema.mi25.Parameter vParameter)
        throws java.lang.IndexOutOfBoundsException
    {
        _parameterList.insertElementAt(vParameter, index);
    } //-- void addParameter(int, org.cytoscape.coreplugin.psi_mi.schema.mi25.Parameter)

    /**
     * Method enumerateParameter
     */
    public java.util.Enumeration enumerateParameter()
    {
        return _parameterList.elements();
    } //-- java.util.Enumeration enumerateParameter() 

    /**
     * Method getParameter
     * 
     * @param index
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.Parameter getParameter(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _parameterList.size())) {
            throw new IndexOutOfBoundsException();
        }

        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.Parameter) _parameterList.elementAt(index);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.Parameter getParameter(int)

    /**
     * Method getParameter
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.Parameter[] getParameter()
    {
        int size = _parameterList.size();
        org.cytoscape.coreplugin.psi_mi.schema.mi25.Parameter[] mArray = new org.cytoscape.coreplugin.psi_mi.schema.mi25.Parameter[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (org.cytoscape.coreplugin.psi_mi.schema.mi25.Parameter) _parameterList.elementAt(index);
        }
        return mArray;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.Parameter[] getParameter()

    /**
     * Method getParameterCount
     */
    public int getParameterCount()
    {
        return _parameterList.size();
    } //-- int getParameterCount() 

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
     * Method removeAllParameter
     */
    public void removeAllParameter()
    {
        _parameterList.removeAllElements();
    } //-- void removeAllParameter() 

    /**
     * Method removeParameter
     * 
     * @param index
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.Parameter removeParameter(int index)
    {
        java.lang.Object obj = _parameterList.elementAt(index);
        _parameterList.removeElementAt(index);
        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.Parameter) obj;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.Parameter removeParameter(int)

    /**
     * Method setParameter
     * 
     * @param index
     * @param vParameter
     */
    public void setParameter(int index, org.cytoscape.coreplugin.psi_mi.schema.mi25.Parameter vParameter)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _parameterList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _parameterList.setElementAt(vParameter, index);
    } //-- void setParameter(int, org.cytoscape.coreplugin.psi_mi.schema.mi25.Parameter)

    /**
     * Method setParameter
     * 
     * @param parameterArray
     */
    public void setParameter(org.cytoscape.coreplugin.psi_mi.schema.mi25.Parameter[] parameterArray)
    {
        //-- copy array
        _parameterList.removeAllElements();
        for (int i = 0; i < parameterArray.length; i++) {
            _parameterList.addElement(parameterArray[i]);
        }
    } //-- void setParameter(org.cytoscape.coreplugin.psi_mi.schema.mi25.Parameter)

    /**
     * Method unmarshalParameterList
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi25.ParameterList unmarshalParameterList(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.ParameterList) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi25.ParameterList.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ParameterList unmarshalParameterList(java.io.Reader)

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
