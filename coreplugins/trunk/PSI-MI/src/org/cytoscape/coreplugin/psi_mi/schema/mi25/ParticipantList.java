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
 * A list of molecules participating in this interaction. An
 * interaction has one (intramolecular), two (binary), or more
 * (n-ary, complexes) participants.
 * 
 * @version $Revision$ $Date$
 */
public class ParticipantList implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _participantList
     */
    private java.util.Vector _participantList;


      //----------------/
     //- Constructors -/
    //----------------/

    public ParticipantList() {
        super();
        _participantList = new Vector();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantList()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addParticipant
     * 
     * @param vParticipant
     */
    public void addParticipant(org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantType vParticipant)
        throws java.lang.IndexOutOfBoundsException
    {
        _participantList.addElement(vParticipant);
    } //-- void addParticipant(org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantType)

    /**
     * Method addParticipant
     * 
     * @param index
     * @param vParticipant
     */
    public void addParticipant(int index, org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantType vParticipant)
        throws java.lang.IndexOutOfBoundsException
    {
        _participantList.insertElementAt(vParticipant, index);
    } //-- void addParticipant(int, org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantType)

    /**
     * Method enumerateParticipant
     */
    public java.util.Enumeration enumerateParticipant()
    {
        return _participantList.elements();
    } //-- java.util.Enumeration enumerateParticipant() 

    /**
     * Method getParticipant
     * 
     * @param index
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantType getParticipant(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _participantList.size())) {
            throw new IndexOutOfBoundsException();
        }

        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantType) _participantList.elementAt(index);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantType getParticipant(int)

    /**
     * Method getParticipant
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantType[] getParticipant()
    {
        int size = _participantList.size();
        org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantType[] mArray = new org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantType[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantType) _participantList.elementAt(index);
        }
        return mArray;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantType[] getParticipant()

    /**
     * Method getParticipantCount
     */
    public int getParticipantCount()
    {
        return _participantList.size();
    } //-- int getParticipantCount() 

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
     * Method removeAllParticipant
     */
    public void removeAllParticipant()
    {
        _participantList.removeAllElements();
    } //-- void removeAllParticipant() 

    /**
     * Method removeParticipant
     * 
     * @param index
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantType removeParticipant(int index)
    {
        java.lang.Object obj = _participantList.elementAt(index);
        _participantList.removeElementAt(index);
        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantType) obj;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantType removeParticipant(int)

    /**
     * Method setParticipant
     * 
     * @param index
     * @param vParticipant
     */
    public void setParticipant(int index, org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantType vParticipant)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _participantList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _participantList.setElementAt(vParticipant, index);
    } //-- void setParticipant(int, org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantType)

    /**
     * Method setParticipant
     * 
     * @param participantArray
     */
    public void setParticipant(org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantType[] participantArray)
    {
        //-- copy array
        _participantList.removeAllElements();
        for (int i = 0; i < participantArray.length; i++) {
            _participantList.addElement(participantArray[i]);
        }
    } //-- void setParticipant(org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantType)

    /**
     * Method unmarshalParticipantList
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantList unmarshalParticipantList(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantList) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantList.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantList unmarshalParticipantList(java.io.Reader)

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
