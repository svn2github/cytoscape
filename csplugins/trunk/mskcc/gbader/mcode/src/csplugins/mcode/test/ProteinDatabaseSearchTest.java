package org.mskcc.csplugins.PDIDDyNet.test;

import junit.framework.TestCase;
import org.biojava.bio.BioException;
import org.mskcc.csplugins.PDIDDyNet.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Copyright (c) 2005 Memorial Sloan-Kettering Cancer Center
 * *
 * * Code written by: Gary Bader
 * * Authors: Gary Bader, Chris Sander
 * *
 * * This library is free software; you can redistribute it and/or modify it
 * * under the terms of the GNU Lesser General Public License as published
 * * by the Free Software Foundation; either version 2.1 of the License, or
 * * any later version.
 * *
 * * This library is distributed in the hope that it will be useful, but
 * * WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 * * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 * * documentation provided hereunder is on an "as is" basis, and
 * * Memorial Sloan-Kettering Cancer Center
 * * has no obligations to provide maintenance, support,
 * * updates, enhancements or modifications.  In no event shall the
 * * Memorial Sloan-Kettering Cancer Center
 * * be liable to any party for direct, indirect, special,
 * * incidental or consequential damages, including lost profits, arising
 * * out of the use of this software and its documentation, even if
 * * Memorial Sloan-Kettering Cancer Center
 * * has been advised of the possibility of such damage.  See
 * * the GNU Lesser General Public License for more details.
 * *
 * * You should have received a copy of the GNU Lesser General Public License
 * * along with this library; if not, write to the Free Software Foundation,
 * * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 * *
 * * User: Gary Bader
 * * Date: Mar 28.2005
 * * Time: 11:57:55 AM
 * * Description  JUnit testing for profile search
 */

/**
 * Test for the protein profile search algorithm
 */
public class ProteinDatabaseSearchTest extends TestCase {
    ProteinDatabaseSearch search = null;
    ProteinProfile profile = null;
    ProteinDatabaseSearchParams params = null;
    BindingPeptideList peptideList = null;

    /**
     * Set up a few things for this test set
     *
     * @throws Exception
     */
    public void setUp() throws Exception {
        search = new ProteinDatabaseSearch("testData" + File.separator + "smallDB.fasta", "FASTA");
    }

    /**
     * Test the profile searching code
     */
    public void testProfileSingleHit() throws BioException, IOException {
        peptideList = new BindingPeptideList();
        peptideList.read("testData" + File.separator + "SH3_pep8.txt");
        assertEquals(8, peptideList.getMaxPeptideLength());
        assertEquals(8, peptideList.getMinPeptideLength());
        profile = new ProteinProfile(peptideList.getSequenceIteratorByLength(4, ProteinTerminus.C), 1, new String("testProfile"));
        assertEquals(8, profile.getNumSequences());

        params = new ProteinDatabaseSearchParams(false);

        SequenceSearchResultSet results = search.profileSearchDB(profile, 1.0, params);
        assertEquals(115, results.getNumberOfHits());
        assertEquals(115, results.getNumberSequencesHit());
        assertEquals(6702, results.getNumberOfSequencesSearched());
    }

    public void testProfileMultipleHits() throws BioException, IOException {
        peptideList = new BindingPeptideList();
        peptideList.read("testData" + File.separator + "pep4List.txt");
        assertEquals(4, peptideList.getMaxPeptideLength());
        assertEquals(4, peptideList.getMinPeptideLength());
        profile = new ProteinProfile(peptideList.getSequenceIterator(), 1, new String("testProfile"));
        assertEquals(8, profile.getNumSequences());

        params = new ProteinDatabaseSearchParams(true);

        SequenceSearchResultSet results = search.profileSearchDB(profile, 1.0, params);
        //Multi hit cases: make sure there are more hits than sequences hits
        assertEquals(249, results.getNumberOfHits());
        assertEquals(238, results.getNumberSequencesHit());
        assertEquals(6702, results.getNumberOfSequencesSearched());
    }

    public void testProfileSingleHitFromCTerminus() throws BioException, IOException {
        peptideList = new BindingPeptideList();
        peptideList.read("testData" + File.separator + "pep4List.txt");
        assertEquals(4, peptideList.getMaxPeptideLength());
        assertEquals(4, peptideList.getMinPeptideLength());
        profile = new ProteinProfile(peptideList.getSequenceIteratorByLength(3, ProteinTerminus.C), 1, new String("testProfile"));
        assertEquals(8, profile.getNumSequences());

        params = new ProteinDatabaseSearchParams(ProteinTerminus.C);

        SequenceSearchResultSet results = search.profileSearchDB(profile, 1.0, params);
        assertEquals(9, results.getNumberOfHits());
        assertEquals(9, results.getNumberSequencesHit());
        List list = results.getBestHits();
        Hit hit = (Hit) list.get(0);
        //assertEquals(0.4519605906801301, hit.getScore().doubleValue(), 0.00000000000001);
        assertEquals(6702, results.getNumberOfSequencesSearched());
        System.out.println(results.toString());
    }

    public void testProfileSingleHitFromNTerminus() throws BioException, IOException {
        peptideList = new BindingPeptideList();
        peptideList.read("testData" + File.separator + "Nterm-pep3List.txt");
        assertEquals(3, peptideList.getMaxPeptideLength());
        assertEquals(3, peptideList.getMinPeptideLength());
        profile = new ProteinProfile(peptideList.getSequenceIterator(), 1, new String("testProfile"));
        assertEquals(8, profile.getNumSequences());

        params = new ProteinDatabaseSearchParams(ProteinTerminus.N);

        SequenceSearchResultSet results = search.profileSearchDB(profile, 1.0, params);
        assertEquals(120, results.getNumberOfHits());
        assertEquals(120, results.getNumberSequencesHit());
        List list = results.getBestHits();
        Hit hit = (Hit) list.get(0);
        assertEquals(0.39431902248861483, hit.getScore().doubleValue(), 0.00000000000001);
        assertEquals(6702, results.getNumberOfSequencesSearched());
    }

    public void testProfileMultiHitFromCTerminus() throws BioException, IOException {
        peptideList = new BindingPeptideList();
        peptideList.read("testData" + File.separator + "pep4List.txt");
        assertEquals(4, peptideList.getMaxPeptideLength());
        assertEquals(4, peptideList.getMinPeptideLength());
        profile = new ProteinProfile(peptideList.getSequenceIteratorByLength(3, ProteinTerminus.C), 1, new String("testProfile"));
        assertEquals(8, profile.getNumSequences());

        params = new ProteinDatabaseSearchParams(ProteinTerminus.C, 100);

        SequenceSearchResultSet results = search.profileSearchDB(profile, 1.0, params);
        //Multi hit cases: make sure there are more hits than sequences hits
        assertEquals(1391, results.getNumberOfHits());
        assertEquals(927, results.getNumberSequencesHit());
        List list = results.getBestHits();
        Hit hit = (Hit) list.get(0);
        assertEquals(0.4519605906801301, hit.getScore().doubleValue(), 0.00000000000001);
        assertEquals(6702, results.getNumberOfSequencesSearched());
    }

    public void testProfileMultiHitFromNTerminus() throws BioException, IOException {
        peptideList = new BindingPeptideList();
        peptideList.read("testData" + File.separator + "Nterm-pep3List.txt");
        assertEquals(3, peptideList.getMaxPeptideLength());
        assertEquals(3, peptideList.getMinPeptideLength());
        profile = new ProteinProfile(peptideList.getSequenceIterator(), 1, new String("testProfile"));
        assertEquals(8, profile.getNumSequences());

        params = new ProteinDatabaseSearchParams(ProteinTerminus.N, 100);

        SequenceSearchResultSet results = search.profileSearchDB(profile, 1.0, params);
        //Multi hit cases: make sure there are more hits than sequences hits
        assertEquals(297, results.getNumberOfHits());
        assertEquals(287, results.getNumberSequencesHit());
        List list = results.getBestHits();
        Hit hit = (Hit) list.get(0);
        assertEquals(0.39431902248861483, hit.getScore().doubleValue(), 0.00000000000001);
        assertEquals(6702, results.getNumberOfSequencesSearched());
    }

    public void testProfileSingleHitFromCTerminusLength() throws BioException, IOException {
        peptideList = new BindingPeptideList();
        peptideList.read("testData" + File.separator + "pep4List.txt");
        assertEquals(4, peptideList.getMaxPeptideLength());
        assertEquals(4, peptideList.getMinPeptideLength());
        profile = new ProteinProfile(peptideList.getSequenceIteratorByLength(3, ProteinTerminus.C), 1, new String("testProfile"));
        assertEquals(8, profile.getNumSequences());

        params = new ProteinDatabaseSearchParams(ProteinTerminus.C, 100);
        params.setMultipleHits(false);

        SequenceSearchResultSet results = search.profileSearchDB(profile, 1.0, params);
        assertEquals(927, results.getNumberOfHits());
        assertEquals(927, results.getNumberSequencesHit());
        List list = results.getBestHits();
        Hit hit = (Hit) list.get(0);
        assertEquals(0.4519605906801301, hit.getScore().doubleValue(), 0.00000000000001);
        assertEquals(6702, results.getNumberOfSequencesSearched());
    }

    public void testProfileSingleHitFromNTerminusLength() throws BioException, IOException {
        peptideList = new BindingPeptideList();
        peptideList.read("testData" + File.separator + "Nterm-pep3List.txt");
        assertEquals(3, peptideList.getMaxPeptideLength());
        assertEquals(3, peptideList.getMinPeptideLength());
        profile = new ProteinProfile(peptideList.getSequenceIterator(), 1, new String("testProfile"));
        assertEquals(8, profile.getNumSequences());

        params = new ProteinDatabaseSearchParams(ProteinTerminus.N, 100);
        params.setMultipleHits(false);

        SequenceSearchResultSet results = search.profileSearchDB(profile, 1.0, params);
        assertEquals(287, results.getNumberOfHits());
        assertEquals(287, results.getNumberSequencesHit());
        List list = results.getBestHits();
        Hit hit = (Hit) list.get(0);
        assertEquals(0.39431902248861483, hit.getScore().doubleValue(), 0.00000000000001);
        assertEquals(6702, results.getNumberOfSequencesSearched());
    }

    public void testProfileSingleHitNormalized() throws BioException, IOException {
        peptideList = new BindingPeptideList();
        peptideList.read("testData" + File.separator + "pep4List.txt");
        assertEquals(4, peptideList.getMaxPeptideLength());
        assertEquals(4, peptideList.getMinPeptideLength());
        profile = new ProteinProfile(peptideList.getSequenceIterator(), 1, new String("testProfile"));
        assertEquals(8, profile.getNumSequences());

        params = new ProteinDatabaseSearchParams(false);
        params.setNormalized(true);

        SequenceSearchResultSet results = search.profileSearchDB(profile, 1.0, params);
        assertEquals(389, results.getNumberOfHits());
        assertEquals(389, results.getNumberSequencesHit());
        List list = results.getBestHits();
        Hit hit = (Hit) list.get(0);
        assertEquals(0.0, hit.getScore().doubleValue(), 0.00000000000001);
        assertEquals(6702, results.getNumberOfSequencesSearched());
    }

    /**
     * Test the regex searching code
     */
    public void testRegexSingleHit() throws BioException {
        params = new ProteinDatabaseSearchParams(false);
        SequenceSearchResultSet results = search.regexSearchDB("VEEE", params);
        assertEquals(75, results.getNumberOfHits());
        assertEquals(75, results.getNumberSequencesHit());
        assertEquals(6702, results.getNumberOfSequencesSearched());
    }

    public void testRegexMultipleHits() throws BioException {
        params = new ProteinDatabaseSearchParams(true);
        SequenceSearchResultSet results = search.regexSearchDB("VEEE", params);
        //Multi hit cases: make sure there are more hits than sequences hits
        assertEquals(76, results.getNumberOfHits());
        assertEquals(75, results.getNumberSequencesHit());
        assertEquals(6702, results.getNumberOfSequencesSearched());
    }

    public void testRegexSingleHitFromCTerminus() throws BioException {
        params = new ProteinDatabaseSearchParams(ProteinTerminus.C);
        SequenceSearchResultSet results = search.regexSearchDB("YEST", params);
        assertEquals(1, results.getNumberOfHits());
        assertEquals(1, results.getNumberSequencesHit());
        assertEquals(6702, results.getNumberOfSequencesSearched());
    }

    public void testRegexSingleHitFromNTerminus() throws BioException {
        params = new ProteinDatabaseSearchParams(ProteinTerminus.N);
        SequenceSearchResultSet results = search.regexSearchDB("MVLT", params);
        assertEquals(3, results.getNumberOfHits());
        assertEquals(3, results.getNumberSequencesHit());
        assertEquals(6702, results.getNumberOfSequencesSearched());
    }

    public void testRegexMultiHitFromCTerminus() throws BioException {
        params = new ProteinDatabaseSearchParams(ProteinTerminus.C, 100);
        SequenceSearchResultSet results = search.regexSearchDB("EST", params);
        //Multi hit cases: make sure there are more hits than sequences hits
        assertEquals(196, results.getNumberOfHits());
        assertEquals(193, results.getNumberSequencesHit());
        assertEquals(6702, results.getNumberOfSequencesSearched());
    }

    public void testRegexMultiHitFromNTerminus() throws BioException {
        params = new ProteinDatabaseSearchParams(ProteinTerminus.N, 100);
        SequenceSearchResultSet results = search.regexSearchDB("MVLT", params);
        //Multi hit cases: make sure there are more hits than sequences hits
        assertEquals(7, results.getNumberOfHits());
        assertEquals(6, results.getNumberSequencesHit());
        assertEquals(6702, results.getNumberOfSequencesSearched());
    }

    public void testRegexSingleHitFromCTerminusLength() throws BioException {
        params = new ProteinDatabaseSearchParams(ProteinTerminus.C, 100);
        params.setMultipleHits(false);
        SequenceSearchResultSet results = search.regexSearchDB("YEST", params);
        assertEquals(6, results.getNumberOfHits());
        assertEquals(6, results.getNumberSequencesHit());
        assertEquals(6702, results.getNumberOfSequencesSearched());
    }

    public void testRegexSingleHitFromNTerminusLength() throws BioException {
        params = new ProteinDatabaseSearchParams(ProteinTerminus.N, 100);
        params.setMultipleHits(false);
        SequenceSearchResultSet results = search.regexSearchDB("MVLT", params);
        assertEquals(6, results.getNumberOfHits());
        assertEquals(6, results.getNumberSequencesHit());
        assertEquals(6702, results.getNumberOfSequencesSearched());
    }
}
