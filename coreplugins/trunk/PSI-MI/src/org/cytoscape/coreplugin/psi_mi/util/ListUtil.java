package org.cytoscape.coreplugin.psi_mi.util;

import org.cytoscape.coreplugin.psi_mi.schema.mi25.EntrySet;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: local_admin
 * Date: 15-Mar-2006
 * Time: 10:08:06
 * To change this template use File | Settings | File Templates.
 */
public class ListUtil {
    static int totalInteractors;
    static String level;
    static String version;
    static int fileCount;
    private static EntrySet entrySet;

    public static org.cytoscape.coreplugin.psi_mi.schema.mi1.EntrySet getPsiOneEntrySet() {
        return psiOneEntrySet;
    }

    public static void setPsiOneEntrySet(org.cytoscape.coreplugin.psi_mi.schema.mi1.EntrySet psiOneEntrySet) {
        ListUtil.psiOneEntrySet = psiOneEntrySet;
    }

    private static org.cytoscape.coreplugin.psi_mi.schema.mi1.EntrySet psiOneEntrySet;


    public static Map getInteractionMap() {
        return interactionMap;
    }

    public static void setInteractionMap(Map interactionMap) {
        ListUtil.interactionMap = interactionMap;
    }

    private static Map interactionMap;

    public static  EntrySet getEntrySet()
    {
        return entrySet;
    }

    public static void setEntrySet(EntrySet eSet)
    {
        entrySet = eSet;
    }
    public static void setInteractorCount(int interactorCount)
    {
         totalInteractors   = interactorCount;
    }
    public static int getInteractorCount()
    {
        return   totalInteractors;
    }
    public static void setLevel(String l)
    {
         level   = l;
    }
    public static String getLevel()
    {
        return   level;
    }

    public static void setVersion(String v)
    {
         version   = v;
    }
    public static String getVersion()
    {
        return   version;
    }

    public static void setFileEntryCount(int count)
    {
        fileCount = count;
    }

    public static int getFileEntryCount()
    {
        return fileCount;
    }
}
