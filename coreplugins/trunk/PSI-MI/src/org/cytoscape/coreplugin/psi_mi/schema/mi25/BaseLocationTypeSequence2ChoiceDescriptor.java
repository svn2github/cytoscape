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

/**
 * Class BaseLocationTypeSequence2ChoiceDescriptor.
 * 
 * @version $Revision$ $Date$
 */
public class BaseLocationTypeSequence2ChoiceDescriptor extends org.exolab.castor.xml.util.XMLClassDescriptorImpl {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field nsPrefix
     */
    private java.lang.String nsPrefix;

    /**
     * Field nsURI
     */
    private java.lang.String nsURI;

    /**
     * Field xmlName
     */
    private java.lang.String xmlName;

    /**
     * Field identity
     */
    private org.exolab.castor.xml.XMLFieldDescriptor identity;


      //----------------/
     //- Constructors -/
    //----------------/

    public BaseLocationTypeSequence2ChoiceDescriptor() {
        super();
        nsURI = "net:sf:psidev:mi";

        //-- set grouping compositor
        setCompositorAsChoice();
        org.exolab.castor.xml.util.XMLFieldDescriptorImpl  desc           = null;
        org.exolab.castor.xml.XMLFieldHandler              handler        = null;
        org.exolab.castor.xml.FieldValidator               fieldValidator = null;
        //-- initialize attribute descriptors

        //-- initialize element descriptors

        //-- _end
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(org.cytoscape.coreplugin.psi_mi.schema.mi25.PositionType.class, "_end", "end", org.exolab.castor.xml.NodeType.Element);
        handler = (new org.exolab.castor.xml.XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object )
                throws IllegalStateException
            {
                BaseLocationTypeSequence2Choice target = (BaseLocationTypeSequence2Choice) object;
                return target.getEnd();
            }
            public void setValue( java.lang.Object object, java.lang.Object value)
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    BaseLocationTypeSequence2Choice target = (BaseLocationTypeSequence2Choice) object;
                    target.setEnd( (org.cytoscape.coreplugin.psi_mi.schema.mi25.PositionType) value);
                }
                catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new org.cytoscape.coreplugin.psi_mi.schema.mi25.PositionType();
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("net:sf:psidev:mi");
        desc.setMultivalued(false);
        addFieldDescriptor(desc);

        //-- validation code for: _end
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- _endInterval
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(org.cytoscape.coreplugin.psi_mi.schema.mi25.IntervalType.class, "_endInterval", "endInterval", org.exolab.castor.xml.NodeType.Element);
        handler = (new org.exolab.castor.xml.XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object )
                throws IllegalStateException
            {
                BaseLocationTypeSequence2Choice target = (BaseLocationTypeSequence2Choice) object;
                return target.getEndInterval();
            }
            public void setValue( java.lang.Object object, java.lang.Object value)
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    BaseLocationTypeSequence2Choice target = (BaseLocationTypeSequence2Choice) object;
                    target.setEndInterval( (org.cytoscape.coreplugin.psi_mi.schema.mi25.IntervalType) value);
                }
                catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new org.cytoscape.coreplugin.psi_mi.schema.mi25.IntervalType();
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("net:sf:psidev:mi");
        desc.setMultivalued(false);
        addFieldDescriptor(desc);

        //-- validation code for: _endInterval
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationTypeSequence2ChoiceDescriptor()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method getAccessMode
     */
    public org.exolab.castor.mapping.AccessMode getAccessMode()
    {
        return null;
    } //-- org.exolab.castor.mapping.AccessMode getAccessMode() 

    /**
     * Method getExtends
     */
    public org.exolab.castor.mapping.ClassDescriptor getExtends()
    {
        return null;
    } //-- org.exolab.castor.mapping.ClassDescriptor getExtends() 

    /**
     * Method getIdentity
     */
    public org.exolab.castor.mapping.FieldDescriptor getIdentity()
    {
        return identity;
    } //-- org.exolab.castor.mapping.FieldDescriptor getIdentity() 

    /**
     * Method getJavaClass
     */
    public java.lang.Class getJavaClass()
    {
        return org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationTypeSequence2Choice.class;
    } //-- java.lang.Class getJavaClass() 

    /**
     * Method getNameSpacePrefix
     */
    public java.lang.String getNameSpacePrefix()
    {
        return nsPrefix;
    } //-- java.lang.String getNameSpacePrefix() 

    /**
     * Method getNameSpaceURI
     */
    public java.lang.String getNameSpaceURI()
    {
        return nsURI;
    } //-- java.lang.String getNameSpaceURI() 

    /**
     * Method getValidator
     */
    public org.exolab.castor.xml.TypeValidator getValidator()
    {
        return this;
    } //-- org.exolab.castor.xml.TypeValidator getValidator() 

    /**
     * Method getXMLName
     */
    public java.lang.String getXMLName()
    {
        return xmlName;
    } //-- java.lang.String getXMLName() 

}
