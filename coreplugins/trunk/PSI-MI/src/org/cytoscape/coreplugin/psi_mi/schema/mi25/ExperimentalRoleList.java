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
 * The role(s) of the participant in the interaction, e.g. bait. 
 * 
 * @version $Revision$ $Date$
 */
public class ExperimentalRoleList implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * This element is controlled by the PSI-MI controlled
     * vocabulary "experimentalRole", root term id MI:0495.
     */
    private java.util.Vector _experimentalRoleList;


      //----------------/
     //- Constructors -/
    //----------------/

    public ExperimentalRoleList() {
        super();
        _experimentalRoleList = new Vector();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalRoleList()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addExperimentalRole
     * 
     * @param vExperimentalRole
     */
    public void addExperimentalRole(org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalRole vExperimentalRole)
        throws java.lang.IndexOutOfBoundsException
    {
        _experimentalRoleList.addElement(vExperimentalRole);
    } //-- void addExperimentalRole(org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalRole)

    /**
     * Method addExperimentalRole
     * 
     * @param index
     * @param vExperimentalRole
     */
    public void addExperimentalRole(int index, org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalRole vExperimentalRole)
        throws java.lang.IndexOutOfBoundsException
    {
        _experimentalRoleList.insertElementAt(vExperimentalRole, index);
    } //-- void addExperimentalRole(int, org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalRole)

    /**
     * Method enumerateExperimentalRole
     */
    public java.util.Enumeration enumerateExperimentalRole()
    {
        return _experimentalRoleList.elements();
    } //-- java.util.Enumeration enumerateExperimentalRole() 

    /**
     * Method getExperimentalRole
     * 
     * @param index
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalRole getExperimentalRole(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _experimentalRoleList.size())) {
            throw new IndexOutOfBoundsException();
        }

        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalRole) _experimentalRoleList.elementAt(index);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalRole getExperimentalRole(int)

    /**
     * Method getExperimentalRole
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalRole[] getExperimentalRole()
    {
        int size = _experimentalRoleList.size();
        org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalRole[] mArray = new org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalRole[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalRole) _experimentalRoleList.elementAt(index);
        }
        return mArray;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalRole[] getExperimentalRole()

    /**
     * Method getExperimentalRoleCount
     */
    public int getExperimentalRoleCount()
    {
        return _experimentalRoleList.size();
    } //-- int getExperimentalRoleCount() 

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
     * Method removeAllExperimentalRole
     */
    public void removeAllExperimentalRole()
    {
        _experimentalRoleList.removeAllElements();
    } //-- void removeAllExperimentalRole() 

    /**
     * Method removeExperimentalRole
     * 
     * @param index
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalRole removeExperimentalRole(int index)
    {
        java.lang.Object obj = _experimentalRoleList.elementAt(index);
        _experimentalRoleList.removeElementAt(index);
        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalRole) obj;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalRole removeExperimentalRole(int)

    /**
     * Method setExperimentalRole
     * 
     * @param index
     * @param vExperimentalRole
     */
    public void setExperimentalRole(int index, org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalRole vExperimentalRole)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _experimentalRoleList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _experimentalRoleList.setElementAt(vExperimentalRole, index);
    } //-- void setExperimentalRole(int, org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalRole)

    /**
     * Method setExperimentalRole
     * 
     * @param experimentalRoleArray
     */
    public void setExperimentalRole(org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalRole[] experimentalRoleArray)
    {
        //-- copy array
        _experimentalRoleList.removeAllElements();
        for (int i = 0; i < experimentalRoleArray.length; i++) {
            _experimentalRoleList.addElement(experimentalRoleArray[i]);
        }
    } //-- void setExperimentalRole(org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalRole)

    /**
     * Method unmarshalExperimentalRoleList
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalRoleList unmarshalExperimentalRoleList(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalRoleList) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalRoleList.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalRoleList unmarshalExperimentalRoleList(java.io.Reader)

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
