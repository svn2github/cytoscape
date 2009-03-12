package org.cytoscape.webservice.psicquic.test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.cxf.frontend.ClientProxyFactoryBean;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.cytoscape.webservice.psicquic.PSICQUICReturnType;
import org.hupo.psi.mi.psicquic.DbRef;
import org.hupo.psi.mi.psicquic.PsicquicService;
import org.hupo.psi.mi.psicquic.QueryResponse;
import org.hupo.psi.mi.psicquic.RequestInfo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CXFClientTest {

	public static final String INTACT = "http://www.ebi.ac.uk/intact/psicquic/webservices/psicquic";
	public static final String IREFINDEX = "http://biotin.uio.no:8080/psicquic-ws-1/webservices/psicquic";
	public static final String MINT = "http://mint.bio.uniroma2.it/mint/psicquic/webservices/psicquic";
	public static final String MPIDB = "http://www.jcvi.org/mpidb/servlet/webservices/psicquic";
	public static final String BIOGRID = "http://tyerslab.bio.ed.ac.uk:8080/psicquic-ws/webservices/psicquic";

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCXF() throws Exception {

		List<ClientProxyFactoryBean> factoryList = new ArrayList<ClientProxyFactoryBean>();

		ClientProxyFactoryBean intactFactory = new JaxWsProxyFactoryBean();
		intactFactory.setServiceClass(PsicquicService.class);
		intactFactory.setAddress(INTACT);
		factoryList.add(intactFactory);

		ClientProxyFactoryBean irefFactory = new JaxWsProxyFactoryBean();
		irefFactory.setServiceClass(PsicquicService.class);
		irefFactory.setAddress(IREFINDEX);
		factoryList.add(irefFactory);

		ClientProxyFactoryBean mintFactory = new JaxWsProxyFactoryBean();
		mintFactory.setServiceClass(PsicquicService.class);
		mintFactory.setAddress(MINT);
		factoryList.add(mintFactory);

		ClientProxyFactoryBean mpidbFactory = new JaxWsProxyFactoryBean();
		mpidbFactory.setServiceClass(PsicquicService.class);
		mpidbFactory.setAddress(MPIDB);
		factoryList.add(mpidbFactory);
		
		ClientProxyFactoryBean biogridFactory = new JaxWsProxyFactoryBean();
		biogridFactory.setServiceClass(PsicquicService.class);
		biogridFactory.setAddress(BIOGRID);
		factoryList.add(biogridFactory);
		
		
		System.out.println("==================== Cleint proxy generated ===================");
		
		// Create query
		DbRef ref = new DbRef();
		ref.setId("p53");
		
		final RequestInfo info = new RequestInfo();

		info.setResultType(PSICQUICReturnType.MITAB25.getTypeName());
		info.setBlockSize(100);
		
		
		for (ClientProxyFactoryBean bean : factoryList) {
			PsicquicService port = (PsicquicService) bean.create();
			System.out.println(bean.getAddress());
			System.out.println(bean.getBindingId());
			System.out.println(bean.getEndpointName());
			System.out.println(bean.getWsdlLocation());
			System.out.println(bean.getServiceClass());
			
			QueryResponse res = port.getByInteractor(ref, info);
			res.getResultInfo().getTotalResults();
			System.out.println("number of result = " + res.getResultInfo().getTotalResults());
//			Method[] methods = port.getClass().getMethods();
//			for (Method m : methods) {
//				System.out.println(m.getName()
//						+ Arrays.toString(m.getParameterTypes()));
//			}
		}
	}

}
