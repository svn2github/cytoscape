/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */

package csplugins.id.mapping.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import java.net.URL;
import java.net.URLConnection;


/**
 *
 * @author gjj
 */
public class BridgeRestUtil {
    private BridgeRestUtil(){}

    public static String defaultBaseUrl = "http://webservice.bridgedb.org";

    public static List<String> supportedOrganisms(String baseUrl) {
        return supportedOrganisms(baseUrl, -1);
    }

    public static List<String> supportedOrganismsNr(String baseUrl) {
        return supportedOrganisms(baseUrl, 0);
    }

    public static List<String> supportedOrganismsLatin(String baseUrl) {
        return supportedOrganisms(baseUrl, 1);
    }

    private static List<String> supportedOrganisms(String baseUrl, int column) {
        String contentUrl = baseUrl + "/contents";
        List<String> lines = readUrl(contentUrl);

        List<String> ret = new ArrayList<String>(lines.size());
        for (String line : lines) {
            String[] strs = line.split("\t");
            if (column>=0)
                ret.add(strs[strs.length>column?column:0]);
            else
                ret.addAll(Arrays.asList(strs));
        }
        return ret;
    }

    private static List<String> readUrl(final String strUrl) {
        final List<String> ret = new ArrayList<String>();
        
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            public void run() {
                try {
                    URL url = new URL(strUrl);
                    URLConnection yc = url.openConnection();
                    BufferedReader in = new BufferedReader(
                                            new InputStreamReader(
                                            yc.getInputStream()));

                    String inputLine;
                    while ((inputLine = in.readLine()) != null)
                        ret.add(inputLine);
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                System.err.println("Failed to connect to "+strUrl);
                executor.shutdown();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }
}
