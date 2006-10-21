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
 * Class ExperimentTypeDescriptor.
 * 
 * @version $Revision$ $Date$
 */
public class ExperimentTypeDescriptor extends org.exolab.castor.xml.util.XMLClassDescriptorImpl {


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

    public ExperimentTypeDescriptor() {
        super();
        nsURI = "net:sf:psidev:mi";
        xmlName = "experimentType";

        //-- set grouping compositor
        setCompositorAsSequence();
        org.exolab.castor.xml.util.XMLFieldDescriptorImpl  desc           = null;
        org.exolab.castor.xml.XMLFieldHandler              handler        = null;
        org.exolab.castor.xml.FieldValidator               fieldValidator = null;
        //-- initialize attribute descriptors

        //-- _id
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(java.lang.String.class, "_id", "id", org.exolab.castor.xml.NodeType.Attribute);
        this.identity = desc;
        handler = (new org.exolab.castor.xml.XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object )
                throws IllegalStateException
            {
                ExperimentType target = (ExperimentType) object;
                return target.getId();
            }
            public void setValue( java.lang.Object object, java.lang.Object value)
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    ExperimentType target = (ExperimentType) object;
                    target.setId( (java.lang.String) value);
                }
                catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new java.lang.String();
            }
        } );
        desc.setHandler(handler);
        desc.setRequired(true);
        addFieldDescriptor(desc);

        //-- validation code for: _id
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        fieldValidator.setMinOccurs(1);
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- initialize element descriptors

        //-- _names
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(org.cytoscape.coreplugin.psi_mi.schema.mi1.NamesType.class, "_names", "names", org.exolab.castor.xml.NodeType.Element);
        handler = (new org.exolab.castor.xml.XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object )
                throws IllegalStateException
            {
                ExperimentType target = (ExperimentType) object;
                return target.getNames();
            }
            public void setValue( java.lang.Object object, java.lang.Object value)
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    ExperimentType target = (ExperimentType) object;
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
        //-- _bibref
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(org.cytoscape.coreplugin.psi_mi.schema.mi1.BibrefType.class, "_bibref", "bibref", org.exolab.castor.xml.NodeType.Element);
        handler = (new org.exolab.castor.xml.XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object )
                throws IllegalStateException
            {
                ExperimentType target = (ExperimentType) object;
                return target.getBibref();
            }
            public void setValue( java.lang.Object object, java.lang.Object value)
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    ExperimentType target = (ExperimentType) object;
                    target.setBibref( (org.cytoscape.coreplugin.psi_mi.schema.mi1.BibrefType) value);
                }
                catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new org.cytoscape.coreplugin.psi_mi.schema.mi1.BibrefType();
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("net:sf:psidev:mi");
        desc.setMultivalued(false);
        addFieldDescriptor(desc);

        //-- validation code for: _bibref
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- _xref
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(org.cytoscape.coreplugin.psi_mi.schema.mi1.XrefType.class, "_xref", "xref", org.exolab.castor.xml.NodeType.Element);
        handler = (new org.exolab.castor.xml.XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object )
                throws IllegalStateException
            {
                ExperimentType target = (ExperimentType) object;
                return target.getXref();
            }
            public void setValue( java.lang.Object object, java.lang.Object value)
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    ExperimentType target = (ExperimentType) object;
                    target.setXref( (org.cytoscape.coreplugin.psi_mi.schema.mi1.XrefType) value);
                }
                catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new org.cytoscape.coreplugin.psi_mi.schema.mi1.XrefType();
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
        //-- _hostOrganism
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(org.cytoscape.coreplugin.psi_mi.schema.mi1.HostOrganism.class, "_hostOrganism", "hostOrganism", org.exolab.castor.xml.NodeType.Element);
        handler = (new org.exolab.castor.xml.XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object )
                throws IllegalStateException
            {
                ExperimentType target = (ExperimentType) object;
                return target.getHostOrganism();
            }
            public void setValue( java.lang.Object object, java.lang.Object value)
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    ExperimentType target = (ExperimentType) object;
                    target.setHostOrganism( (org.cytoscape.coreplugin.psi_mi.schema.mi1.HostOrganism) value);
                }
                catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new org.cytoscape.coreplugin.psi_mi.schema.mi1.HostOrganism();
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("net:sf:psidev:mi");
        desc.setMultivalued(false);
        addFieldDescriptor(desc);

        //-- validation code for: _hostOrganism
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- _interactionDetection
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(org.cytoscape.coreplugin.psi_mi.schema.mi1.CvType.class, "_interactionDetection", "interactionDetection", org.exolab.castor.xml.NodeType.Element);
        handler = (new org.exolab.castor.xml.XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object )
                throws IllegalStateException
            {
                ExperimentType target = (ExperimentType) object;
                return target.getInteractionDetection();
            }
            public void setValue( java.lang.Object object, java.lang.Object value)
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    ExperimentType target = (ExperimentType) object;
                    target.setInteractionDetection( (org.cytoscape.coreplugin.psi_mi.schema.mi1.CvType) value);
                }
                catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new org.cytoscape.coreplugin.psi_mi.schema.mi1.CvType();
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("net:sf:psidev:mi");
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);

        //-- validation code for: _interactionDetection
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        fieldValidator.setMinOccurs(1);
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- _participantDetection
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(org.cytoscape.coreplugin.psi_mi.schema.mi1.CvType.class, "_participantDetection", "participantDetection", org.exolab.castor.xml.NodeType.Element);
        handler = (new org.exolab.castor.xml.XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object )
                throws IllegalStateException
            {
                ExperimentType target = (ExperimentType) object;
                return target.getParticipantDetection();
            }
            public void setValue( java.lang.Object object, java.lang.Object value)
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    ExperimentType target = (ExperimentType) object;
                    target.setParticipantDetection( (org.cytoscape.coreplugin.psi_mi.schema.mi1.CvType) value);
                }
                catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new org.cytoscape.coreplugin.psi_mi.schema.mi1.CvType();
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("net:sf:psidev:mi");
        desc.setMultivalued(false);
        addFieldDescriptor(desc);

        //-- validation code for: _participantDetection
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- _featureDetection
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(org.cytoscape.coreplugin.psi_mi.schema.mi1.CvType.class, "_featureDetection", "featureDetection", org.exolab.castor.xml.NodeType.Element);
        handler = (new org.exolab.castor.xml.XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object )
                throws IllegalStateException
            {
                ExperimentType target = (ExperimentType) object;
                return target.getFeatureDetection();
            }
            public void setValue( java.lang.Object object, java.lang.Object value)
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    ExperimentType target = (ExperimentType) object;
                    target.setFeatureDetection( (org.cytoscape.coreplugin.psi_mi.schema.mi1.CvType) value);
                }
                catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new org.cytoscape.coreplugin.psi_mi.schema.mi1.CvType();
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("net:sf:psidev:mi");
        desc.setMultivalued(false);
        addFieldDescriptor(desc);

        //-- validation code for: _featureDetection
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- _confidence
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(org.cytoscape.coreplugin.psi_mi.schema.mi1.Confidence.class, "_confidence", "confidence", org.exolab.castor.xml.NodeType.Element);
        handler = (new org.exolab.castor.xml.XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object )
                throws IllegalStateException
            {
                ExperimentType target = (ExperimentType) object;
                return target.getConfidence();
            }
            public void setValue( java.lang.Object object, java.lang.Object value)
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    ExperimentType target = (ExperimentType) object;
                    target.setConfidence( (org.cytoscape.coreplugin.psi_mi.schema.mi1.Confidence) value);
                }
                catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new org.cytoscape.coreplugin.psi_mi.schema.mi1.Confidence();
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("net:sf:psidev:mi");
        desc.setMultivalued(false);
        addFieldDescriptor(desc);

        //-- validation code for: _confidence
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- _attributeList
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(org.cytoscape.coreplugin.psi_mi.schema.mi1.AttributeListType.class, "_attributeList", "attributeList", org.exolab.castor.xml.NodeType.Element);
        handler = (new org.exolab.castor.xml.XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object )
                throws IllegalStateException
            {
                ExperimentType target = (ExperimentType) object;
                return target.getAttributeList();
            }
            public void setValue( java.lang.Object object, java.lang.Object value)
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    ExperimentType target = (ExperimentType) object;
                    target.setAttributeList( (org.cytoscape.coreplugin.psi_mi.schema.mi1.AttributeListType) value);
                }
                catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new org.cytoscape.coreplugin.psi_mi.schema.mi1.AttributeListType();
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
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.ExperimentTypeDescriptor()


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
        return org.cytoscape.coreplugin.psi_mi.schema.mi1.ExperimentType.class;
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
