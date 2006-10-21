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

/**
 * Class BioSourceTypeDescriptor.
 * 
 * @version $Revision$ $Date$
 */
public class BioSourceTypeDescriptor extends org.exolab.castor.xml.util.XMLClassDescriptorImpl {


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

    public BioSourceTypeDescriptor() {
        super();
        nsURI = "net:sf:psidev:mi";
        xmlName = "bioSourceType";

        //-- set grouping compositor
        setCompositorAsSequence();
        org.exolab.castor.xml.util.XMLFieldDescriptorImpl  desc           = null;
        org.exolab.castor.xml.XMLFieldHandler              handler        = null;
        org.exolab.castor.xml.FieldValidator               fieldValidator = null;
        //-- initialize attribute descriptors

        //-- initialize element descriptors

        //-- _names
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(org.cytoscape.coreplugin.psi_mi.schema.mi1.NamesType.class, "_names", "names", org.exolab.castor.xml.NodeType.Element);
        handler = (new org.exolab.castor.xml.XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object )
                throws IllegalStateException
            {
                BioSourceType target = (BioSourceType) object;
                return target.getNames();
            }
            public void setValue( java.lang.Object object, java.lang.Object value)
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    BioSourceType target = (BioSourceType) object;
                    target.setNames( (org.cytoscape.coreplugin.psi_mi.schema.mi1.NamesType) value);
                }
                catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new org.cytoscape.coreplugin.psi_mi.schema.mi1.NamesType();
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("net:sf:psidev:mi");
        desc.setMultivalued(false);
        addFieldDescriptor(desc);

        //-- validation code for: _names
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- _cellType
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(org.cytoscape.coreplugin.psi_mi.schema.mi1.OpenCvType.class, "_cellType", "cellType", org.exolab.castor.xml.NodeType.Element);
        handler = (new org.exolab.castor.xml.XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object )
                throws IllegalStateException
            {
                BioSourceType target = (BioSourceType) object;
                return target.getCellType();
            }
            public void setValue( java.lang.Object object, java.lang.Object value)
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    BioSourceType target = (BioSourceType) object;
                    target.setCellType( (org.cytoscape.coreplugin.psi_mi.schema.mi1.OpenCvType) value);
                }
                catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new org.cytoscape.coreplugin.psi_mi.schema.mi1.OpenCvType();
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("net:sf:psidev:mi");
        desc.setMultivalued(false);
        addFieldDescriptor(desc);

        //-- validation code for: _cellType
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- _compartment
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(org.cytoscape.coreplugin.psi_mi.schema.mi1.OpenCvType.class, "_compartment", "compartment", org.exolab.castor.xml.NodeType.Element);
        handler = (new org.exolab.castor.xml.XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object )
                throws IllegalStateException
            {
                BioSourceType target = (BioSourceType) object;
                return target.getCompartment();
            }
            public void setValue( java.lang.Object object, java.lang.Object value)
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    BioSourceType target = (BioSourceType) object;
                    target.setCompartment( (org.cytoscape.coreplugin.psi_mi.schema.mi1.OpenCvType) value);
                }
                catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new org.cytoscape.coreplugin.psi_mi.schema.mi1.OpenCvType();
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("net:sf:psidev:mi");
        desc.setMultivalued(false);
        addFieldDescriptor(desc);

        //-- validation code for: _compartment
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- _tissue
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(org.cytoscape.coreplugin.psi_mi.schema.mi1.OpenCvType.class, "_tissue", "tissue", org.exolab.castor.xml.NodeType.Element);
        handler = (new org.exolab.castor.xml.XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object )
                throws IllegalStateException
            {
                BioSourceType target = (BioSourceType) object;
                return target.getTissue();
            }
            public void setValue( java.lang.Object object, java.lang.Object value)
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    BioSourceType target = (BioSourceType) object;
                    target.setTissue( (org.cytoscape.coreplugin.psi_mi.schema.mi1.OpenCvType) value);
                }
                catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new org.cytoscape.coreplugin.psi_mi.schema.mi1.OpenCvType();
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("net:sf:psidev:mi");
        desc.setMultivalued(false);
        addFieldDescriptor(desc);

        //-- validation code for: _tissue
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.BioSourceTypeDescriptor()


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
        return org.cytoscape.coreplugin.psi_mi.schema.mi1.BioSourceType.class;
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
