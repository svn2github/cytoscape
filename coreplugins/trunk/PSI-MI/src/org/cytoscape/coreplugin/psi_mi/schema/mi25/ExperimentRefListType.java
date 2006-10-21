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
 * Refers to a list of experiments within the same entry.
 * 
 * @version $Revision$ $Date$
 */
public class ExperimentRefListType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * References an experiment already present in this entry.
     */
    private java.util.Vector _experimentRefList;


      //----------------/
     //- Constructors -/
    //----------------/

    public ExperimentRefListType() {
        super();
        _experimentRefList = new Vector();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentRefListType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addExperimentRef
     * 
     * @param vExperimentRef
     */
    public void addExperimentRef(int vExperimentRef)
        throws java.lang.IndexOutOfBoundsException
    {
        _experimentRefList.addElement(new Integer(vExperimentRef));
    } //-- void addExperimentRef(int) 

    /**
     * Method addExperimentRef
     * 
     * @param index
     * @param vExperimentRef
     */
    public void addExperimentRef(int index, int vExperimentRef)
        throws java.lang.IndexOutOfBoundsException
    {
        _experimentRefList.insertElementAt(new Integer(vExperimentRef), index);
    } //-- void addExperimentRef(int, int) 

    /**
     * Method enumerateExperimentRef
     */
    public java.util.Enumeration enumerateExperimentRef()
    {
        return _experimentRefList.elements();
    } //-- java.util.Enumeration enumerateExperimentRef() 

    /**
     * Method getExperimentRef
     * 
     * @param index
     */
    public int getExperimentRef(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _experimentRefList.size())) {
            throw new IndexOutOfBoundsException();
        }

        return ((Integer)_experimentRefList.elementAt(index)).intValue();
    } //-- int getExperimentRef(int) 

    /**
     * Method getExperimentRef
     */
    public int[] getExperimentRef()
    {
        int size = _experimentRefList.size();
        int[] mArray = new int[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = ((Integer)_experimentRefList.elementAt(index)).intValue();
        }
        return mArray;
    } //-- int[] getExperimentRef() 

    /**
     * Method getExperimentRefCount
     */
    public int getExperimentRefCount()
    {
        return _experimentRefList.size();
    } //-- int getExperimentRefCount() 

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
     * Method removeAllExperimentRef
     */
    public void removeAllExperimentRef()
    {
        _experimentRefList.removeAllElements();
    } //-- void removeAllExperimentRef() 

    /**
     * Method removeExperimentRef
     * 
     * @param index
     */
    public int removeExperimentRef(int index)
    {
        java.lang.Object obj = _experimentRefList.elementAt(index);
        _experimentRefList.removeElementAt(index);
        return ((Integer)obj).intValue();
    } //-- int removeExperimentRef(int) 

    /**
     * Method setExperimentRef
     * 
     * @param index
     * @param vExperimentRef
     */
    public void setExperimentRef(int index, int vExperimentRef)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _experimentRefList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _experimentRefList.setElementAt(new Integer(vExperimentRef), index);
    } //-- void setExperimentRef(int, int) 

    /**
     * Method setExperimentRef
     * 
     * @param experimentRefArray
     */
    public void setExperimentRef(int[] experimentRefArray)
    {
        //-- copy array
        _experimentRefList.removeAllElements();
        for (int i = 0; i < experimentRefArray.length; i++) {
            _experimentRefList.addElement(new Integer(experimentRefArray[i]));
        }
    } //-- void setExperimentRef(int) 

    /**
     * Method unmarshalExperimentRefListType
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentRefListType unmarshalExperimentRefListType(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentRefListType) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentRefListType.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentRefListType unmarshalExperimentRefListType(java.io.Reader)

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
