/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.4.3</a>, using an XML
 * Schema.
 * $Id$
 */

package org.cytoscape.coreplugin.psi_mi.schema.mi1.types;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.util.Hashtable;

/**
 * Class RoleType.
 * 
 * @version $Revision$ $Date$
 */
public class RoleType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * The bait type
     */
    public static final int BAIT_TYPE = 0;

    /**
     * The instance of the bait type
     */
    public static final RoleType BAIT = new RoleType(BAIT_TYPE, "bait");

    /**
     * The prey type
     */
    public static final int PREY_TYPE = 1;

    /**
     * The instance of the prey type
     */
    public static final RoleType PREY = new RoleType(PREY_TYPE, "prey");

    /**
     * The neutral type
     */
    public static final int NEUTRAL_TYPE = 2;

    /**
     * The instance of the neutral type
     */
    public static final RoleType NEUTRAL = new RoleType(NEUTRAL_TYPE, "neutral");

    /**
     * The unspecified type
     */
    public static final int UNSPECIFIED_TYPE = 3;

    /**
     * The instance of the unspecified type
     */
    public static final RoleType UNSPECIFIED = new RoleType(UNSPECIFIED_TYPE, "unspecified");

    /**
     * Field _memberTable
     */
    private static java.util.Hashtable _memberTable = init();

    /**
     * Field type
     */
    private int type = -1;

    /**
     * Field stringValue
     */
    private java.lang.String stringValue = null;


      //----------------/
     //- Constructors -/
    //----------------/

    private RoleType(int type, java.lang.String value) {
        super();
        this.type = type;
        this.stringValue = value;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.types.RoleType(int, java.lang.String)


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method enumerateReturns an enumeration of all possible
     * instances of RoleType
     */
    public static java.util.Enumeration enumerate()
    {
        return _memberTable.elements();
    } //-- java.util.Enumeration enumerate() 

    /**
     * Method getTypeReturns the type of this RoleType
     */
    public int getType()
    {
        return this.type;
    } //-- int getType() 

    /**
     * Method init
     */
    private static java.util.Hashtable init()
    {
        Hashtable members = new Hashtable();
        members.put("bait", BAIT);
        members.put("prey", PREY);
        members.put("neutral", NEUTRAL);
        members.put("unspecified", UNSPECIFIED);
        return members;
    } //-- java.util.Hashtable init() 

    /**
     * Method toStringReturns the String representation of this
     * RoleType
     */
    public java.lang.String toString()
    {
        return this.stringValue;
    } //-- java.lang.String toString() 

    /**
     * Method valueOfReturns a new RoleType based on the given
     * String value.
     * 
     * @param string
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi1.types.RoleType valueOf(java.lang.String string)
    {
        java.lang.Object obj = null;
        if (string != null) obj = _memberTable.get(string);
        if (obj == null) {
            String err = "'" + string + "' is not a valid RoleType";
            throw new IllegalArgumentException(err);
        }
        return (RoleType) obj;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.types.RoleType valueOf(java.lang.String)

}
