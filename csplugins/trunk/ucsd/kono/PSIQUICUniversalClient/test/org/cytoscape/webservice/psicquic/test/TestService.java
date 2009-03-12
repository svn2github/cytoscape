package org.cytoscape.webservice.psicquic.test;

import java.util.ArrayList;
import java.util.List;

import org.cytoscape.webservice.psicquic.PSICQUICReturnType;
import org.hupo.psi.mi.psicquic.DbRef;
import org.hupo.psi.mi.psicquic.QueryResponse;
import org.hupo.psi.mi.psicquic.RequestInfo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import psi.hupo.org.mi.psicquic.PsicquicService_Service;
import uk.ac.ebi.intact.psicquic.ws.IndexBasedPsicquicServiceService;
import uk.ac.ebi.intact.psicquic.ws.PsicquicService;

public class TestService {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testIntAct() throws Exception {
		PsicquicService_Service service = new PsicquicService_Service();
		IndexBasedPsicquicServiceService iref = new IndexBasedPsicquicServiceService();
		
		System.out.println(service.getServiceName());
		System.out.println(service.toString());

		psi.hupo.org.mi.psicquic.PsicquicService pt = service.getIntactPsicquicServicePort();
		PsicquicService pt2 = iref.getIndexBasedPsicquicServicePort();
		String ver = pt.getVersion();

		System.out.println("Version ===========> " + ver + ", "
				+ service.getWSDLDocumentLocation());

		RequestInfo info = new RequestInfo();
		info.setResultType(PSICQUICReturnType.MITAB25.getTypeName());
		info.setBlockSize(50);

		DbRef dbRef1 = new DbRef();
		dbRef1.setId("brca1");
		DbRef dbRef2 = new DbRef();
		dbRef2.setId("brca2");
		DbRef dbRef3 = new DbRef();
		dbRef3.setId("yap1");

		List<DbRef> interactorList = new ArrayList<DbRef>();
		interactorList.add(dbRef1);
		interactorList.add(dbRef2);
		interactorList.add(dbRef3);
		
		final QueryResponse queryRes = pt.getByQuery("brca*", info);
		
//		EntrySet xmlRes = queryRes.getResultSet().getEntrySet();
//		List<Entry> ent = xmlRes.getEntry();
//		
//		for(Entry entry: ent) {
//		}
//		
		
		System.out.println(queryRes.getResultSet().getMitab());
		System.out.println(queryRes.getResultInfo().getTotalResults());
		
		
//		final QueryResponse queryResponse = pt.getByInteractorList(interactorList, info, "OR");
		final QueryResponse queryResponseIref = pt2.getByInteractorList(interactorList, info, "OR");
//
//		System.out.println(queryResponse.getResultSet().getEntrySet());
//		System.out.println(queryResponse.getResultInfo().getTotalResults());
//		
		System.out.println(queryResponseIref.getResultSet().getMitab());
		System.out.println(queryResponseIref.getResultInfo().getTotalResults());
//
//		// printing the results
//		// EntrySet mitab = queryResponse.getResultSet().getEntrySet();
//		//
//		// System.out.println(mitab.getEntry().size());

	}

}
