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
package csplugins.dataviewer.mage;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Bare Bones Parser for MAGE-ML XML File.
 *
 * @author Ethan Cerami.
 */
public class MageParser {
    private MageData mageData;
    private File mageFile;

    /**
     * Parses the specified MAGE-ML File.
     *
     * @param file MAGE-ML File.
     * @return MageData Object.
     * @throws IOException   IO Error.
     * @throws JDOMException XML Error.
     */
    public MageData parseFile(File file) throws IOException, JDOMException {
        this.mageFile = file;
        mageData = new MageData();
        SAXBuilder builder = new SAXBuilder(false);
        builder.setEntityResolver(new MageEntityResolver());
        Document doc = builder.build(file);
        Element root = doc.getRootElement();
        extractExperimentDescription(root);
        extractOrganizationalContact(root);
        extractExternalDataFiles(root);
        mageData.setFile(file);
        return mageData;
    }

    private void extractExperimentDescription(Element root) throws JDOMException {
        XPath xpath = XPath.newInstance("//Experiment/*/Description");
        List expDescriptionList = xpath.selectNodes(root);
        ArrayList list = new ArrayList();
        for (int i = 0; i < expDescriptionList.size(); i++) {
            Element expDescription = (Element) expDescriptionList.get(i);
            Attribute text = expDescription.getAttribute("text");
            if (text != null && text.getValue() != null
                    && text.getValue().length() > 0) {
                list.add(text.getValue());
            }
        }
        mageData.setExperimentDescriptionList(list);
    }

    private void extractOrganizationalContact(Element root) throws JDOMException {
        XPath xpath = XPath.newInstance("//Organization");
        List orgList = xpath.selectNodes(root);
        ArrayList list = new ArrayList();
        for (int i = 0; i < orgList.size(); i++) {
            Element org = (Element) orgList.get(i);
            Attribute name = org.getAttribute("name");
            if (name != null && name.getValue() != null
                    && name.getValue().length() > 0) {
                list.add(name.getValue());
            }
        }
        mageData.setOrganizationContactList(list);
    }

    private void extractExternalDataFiles(Element root) throws JDOMException {
        XPath xpath = XPath.newInstance("//DerivedBioAssayData//DataExternal");
        List fileList = xpath.selectNodes(root);
        ArrayList list = new ArrayList();
        for (int i = 0; i < fileList.size(); i++) {
            Element file = (Element) fileList.get(i);
            Attribute fileName = file.getAttribute("filenameURI");
            if (fileName != null && fileName.getValue() != null
                    && fileName.getValue().length() > 0) {
                Attribute dataFormat = file.getAttribute("dataFormat");
                if (dataFormat != null && dataFormat.getValue() != null
                        && dataFormat.getValue().equals("tab delimited")) {
                    boolean flag = filePassesFilterChecks(fileName.getValue());
                    if (flag) {
                        list.add(fileName.getValue());
                    }
                }
            }
        }
        mageData.setFileList(list);
    }

    private boolean filePassesFilterChecks(String fileName) {

        //  Filter 1:  File is not a URL
        if (fileName.startsWith("http") || fileName.startsWith("ftp")) {
            return false;
        }

        //  Filter 2:  File exists on local fileName system
        File file = new File(mageFile.getParentFile(), fileName);
        if (!file.exists()) {
            return false;
        }

        //  Filter 3:  Filter out all CHP, CEL, DAT and RPT Files
        if (file.getName().endsWith("CHP")
                || file.getName().endsWith("CEL")
                || file.getName().endsWith("DAT")
                || file.getName().endsWith("RPT")) {
            return false;
        }

        //  Filter 4:  Filter out any files with meta data
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(file));
            String line = in.readLine();
            int lineCounter = 0;

            //  Read first five lines.  If any of these lines begins with [
            //  e.g. [CEL], filter the file out.
            while (lineCounter++ < 5 && line != null) {
                line = line.trim();
                if (line.startsWith("[")) {
                    return false;
                }
                line = in.readLine();
            }
        } catch (IOException e) {
            return false;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
            }
        }
        return true;
    }
}

/**
 * Entity Resolver.
 *
 * @author Ethan Cerami
 */
class MageEntityResolver implements EntityResolver {

    /**
     * No Op.
     *
     * @param publicId  Public ID.
     * @param systemId  System ID.
     * @return Empty InputSource.
     */
    public InputSource resolveEntity(String publicId, String systemId) {
        return new InputSource(new StringReader(""));
    }
}
