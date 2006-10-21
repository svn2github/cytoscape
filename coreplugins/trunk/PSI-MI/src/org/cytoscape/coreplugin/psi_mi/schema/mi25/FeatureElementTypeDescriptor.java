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

import org.exolab.castor.xml.validators.*;

/**
 * Class FeatureElementTypeDescriptor.
 * 
 * @version $Revision$ $Date$
 */
public class FeatureElementTypeDescriptor extends org.exolab.castor.xml.util.XMLClassDescriptorImpl {


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

    public FeatureElementTypeDescriptor() {
        super();
        nsURI = "net:sf:psidev:mi";
        xmlName = "featureElementType";
        
        //-- set grouping compositor
        setCompositorAsSequence();
        org.exolab.castor.xml.util.XMLFieldDescriptorImpl  desc           = null;
        org.exolab.castor.xml.XMLFieldHandler              handler        = null;
        org.exolab.castor.xml.FieldValidator               fieldValidator = null;
        //-- initialize attribute descriptors
        
        //-- _id
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(java.lang.Integer.TYPE, "_id", "id", org.exolab.castor.xml.NodeType.Attribute);
        handler = (new org.exolab.castor.xml.XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                FeatureElementType target = (FeatureElementType) object;
                if(!target.hasId())
                    return null;
                return new Integer(target.getId());
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    FeatureElementType target = (FeatureElementType) object;
                    // ignore null values for non optional primitives
                    if (value == null) return;
                    
                    target.setId( ((Integer)value).intValue());
                }
                catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return null;
            }
        } );
        desc.setHandler(handler);
        desc.setRequired(true);
        addFieldDescriptor(desc);
        
        //-- validation code for: _id
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        fieldValidator.setMinOccurs(1);
        { //-- local scope
            IntegerValidator typeValidator= new IntegerValidator();
            fieldValidator.setValidator(typeValidator);
        }
        desc.setValidator(fieldValidator);
        //-- initialize element descriptors
        
        //-- _names
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(org.cytoscape.coreplugin.psi_mi.schema.mi25.NamesType.class, "_names", "names", org.exolab.castor.xml.NodeType.Element);
        handler = (new org.exolab.castor.xml.XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                FeatureElementType target = (FeatureElementType) object;
                return target.getNames();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    FeatureElementType target = (FeatureElementType) object;
                    target.setNames( (org.cytoscape.coreplugin.psi_mi.schema.mi25.NamesType) value);
                }
                catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new org.cytoscape.coreplugin.psi_mi.schema.mi25.NamesType();
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
        //-- _xref
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(org.cytoscape.coreplugin.psi_mi.schema.mi25.XrefType.class, "_xref", "xref", org.exolab.castor.xml.NodeType.Element);
        handler = (new org.exolab.castor.xml.XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                FeatureElementType target = (FeatureElementType) object;
                return target.getXref();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    FeatureElementType target = (FeatureElementType) object;
                    target.setXref( (org.cytoscape.coreplugin.psi_mi.schema.mi25.XrefType) value);
                }
                catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new org.cytoscape.coreplugin.psi_mi.schema.mi25.XrefType();
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("net:sf:psidev:mi");
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _xref
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- _featureType
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType.class, "_featureType", "featureType", org.exolab.castor.xml.NodeType.Element);
        handler = (new org.exolab.castor.xml.XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                FeatureElementType target = (FeatureElementType) object;
                return target.getFeatureType();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    FeatureElementType target = (FeatureElementType) object;
                    target.setFeatureType( (org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType) value);
                }
                catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType();
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("net:sf:psidev:mi");
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _featureType
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- _featureDetectionMethod
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType.class, "_featureDetectionMethod", "featureDetectionMethod", org.exolab.castor.xml.NodeType.Element);
        handler = (new org.exolab.castor.xml.XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                FeatureElementType target = (FeatureElementType) object;
                return target.getFeatureDetectionMethod();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    FeatureElementType target = (FeatureElementType) object;
                    target.setFeatureDetectionMethod( (org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType) value);
                }
                catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType();
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("net:sf:psidev:mi");
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _featureDetectionMethod
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- _experimentRefList
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentRefListType.class, "_experimentRefList", "experimentRefList", org.exolab.castor.xml.NodeType.Element);
        handler = (new org.exolab.castor.xml.XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                FeatureElementType target = (FeatureElementType) object;
                return target.getExperimentRefList();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    FeatureElementType target = (FeatureElementType) object;
                    target.setExperimentRefList( (org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentRefListType) value);
                }
                catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentRefListType();
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("net:sf:psidev:mi");
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _experimentRefList
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- _featureRangeList
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(org.cytoscape.coreplugin.psi_mi.schema.mi25.FeatureRangeList.class, "_featureRangeList", "featureRangeList", org.exolab.castor.xml.NodeType.Element);
        handler = (new org.exolab.castor.xml.XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                FeatureElementType target = (FeatureElementType) object;
                return target.getFeatureRangeList();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    FeatureElementType target = (FeatureElementType) object;
                    target.setFeatureRangeList( (org.cytoscape.coreplugin.psi_mi.schema.mi25.FeatureRangeList) value);
                }
                catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new org.cytoscape.coreplugin.psi_mi.schema.mi25.FeatureRangeList();
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("net:sf:psidev:mi");
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _featureRangeList
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        fieldValidator.setMinOccurs(1);
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- _attributeList
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(org.cytoscape.coreplugin.psi_mi.schema.mi25.AttributeListType.class, "_attributeList", "attributeList", org.exolab.castor.xml.NodeType.Element);
        handler = (new org.exolab.castor.xml.XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                FeatureElementType target = (FeatureElementType) object;
                return target.getAttributeList();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    FeatureElementType target = (FeatureElementType) object;
                    target.setAttributeList( (org.cytoscape.coreplugin.psi_mi.schema.mi25.AttributeListType) value);
                }
                catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new org.cytoscape.coreplugin.psi_mi.schema.mi25.AttributeListType();
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("net:sf:psidev:mi");
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _attributeList
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.FeatureElementTypeDescriptor()


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
        return org.cytoscape.coreplugin.psi_mi.schema.mi25.FeatureElementType.class;
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
