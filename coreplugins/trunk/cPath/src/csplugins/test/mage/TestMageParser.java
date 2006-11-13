/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package csplugins.test.mage;

import csplugins.dataviewer.mage.MageData;
import csplugins.dataviewer.mage.MageParser;
import junit.framework.TestCase;

import java.io.File;
import java.util.List;

/**
 * Tests the MAGE-ML Paser Utility Class.
 *
 * @author Ethan Cerami
 */
public class TestMageParser extends TestCase {

    /**
     * Basic Mage-ML Test with sample NCI-60 MAGE-ML File.
     *
     * @throws Exception All Errors.
     */
    public void testMageMlParser() throws Exception {
        File file = new File("testData/mage/NCI-60_U95.xml");
        MageParser parser = new MageParser();
        MageData mageData = parser.parseFile(file);

        //  Validate Experiment Description
        List expDescriptionList = mageData.getExperimentDescriptionList();
        assertEquals(1, expDescriptionList.size());
        String expDesc0 = (String) expDescriptionList.get(0);
        assertTrue(expDesc0.startsWith("Comparison between cell lines"));

        //  Validate Organizational Contacts
        List orgList = mageData.getOrganizationContactList();
        assertEquals(3, orgList.size());
        String org0 = (String) orgList.get(0);
        assertEquals("NCI/NIH", org0);
        String org1 = (String) orgList.get(1);
        assertEquals("LMP", org1);
        String org2 = (String) orgList.get(2);
        assertEquals("NCI", org2);

        //  Validate External File List
        List fileList = mageData.getFileList();
        assertEquals(5, fileList.size());
        for (int i = 0; i < fileList.size(); i++) {
            String fileName = (String) fileList.get(i);
            switch (i) {
                case 0:
                    assertEquals("nci60_hgu95A_combined.txt", fileName);
                    break;
                case 4:
                    assertEquals("nci60_hgu95E_combined.txt", fileName);
                    break;
            }
        }
    }
}
