package org.cytoscape.webservice.psicquic;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.cxf.frontend.ClientProxyFactoryBean;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.hupo.psi.mi.psicquic.DbRef;
import org.hupo.psi.mi.psicquic.PsicquicService;
import org.hupo.psi.mi.psicquic.QueryResponse;
import org.hupo.psi.mi.psicquic.RequestInfo;

import cytoscape.data.webservice.CyWebServiceEvent;
import cytoscape.data.webservice.CyWebServiceEventListener;
import cytoscape.data.webservice.CyWebServiceException;
import cytoscape.data.webservice.WebServiceClientManager;
import cytoscape.data.webservice.CyWebServiceEvent.WSEventType;
import cytoscape.logger.CyLogger;

public class PSICQUICServiceRegistory {
	
	private static RegistryManager manager;

	private enum OperationType {
		GET_COUNT, IMPORT;
	}


	private static Map<URI, PsicquicService> services;
	private static Map<URI, String> serviceNames;
	private static List<PsicquicService> ports;

	/*
	 * Initialize services. In 3.0, this will be done by Spring DM.
	 */
	static {
		services = new HashMap<URI, PsicquicService>();
		serviceNames = new HashMap<URI, String>();
		ports = new ArrayList<PsicquicService>();

		try {
			// Human-readable database name should be taken from PSI-MI 2.5
			// ontology.
			
			manager = new RegistryManager();
			
			for (String serviceName: manager.getRegistry().keySet()) {
				final ClientProxyFactoryBean factory = new JaxWsProxyFactoryBean();
				factory.setServiceClass(PsicquicService.class);
				factory.setAddress(manager.getRegistry().get(serviceName));
				serviceNames.put(new URI(manager.getRegistry().get(serviceName)), serviceName);
				final PsicquicService port = (PsicquicService) factory.create();
				ports.add(port);
				services.put(new URI(manager.getRegistry().get(serviceName)), port);
			}
		} catch (Exception e) {
			CyLogger.getLogger().error("Could not initialize PSICQUIC ports.",
					e);
		}
	}

	public Map<URI, String> getServiceNames() {
		return serviceNames;
	}

	/**
	 * Get number of hits in the
	 * 
	 * @param interactors
	 * @param reqInfo
	 * @param operator
	 * @return
	 * @throws CyWebServiceException
	 */
	public Map<URI, QueryResponse> getCount(List<DbRef> interactors,
			RequestInfo reqInfo, String operator) throws CyWebServiceException {
		final Map<URI, QueryResponse> result = new ConcurrentHashMap<URI, QueryResponse>();

		System.out.println("===== Search Start ====");
		final ExecutorService exe = Executors.newCachedThreadPool();
		final long startTime = System.currentTimeMillis();

		final CompletionService<List<Object>> cs = new ExecutorCompletionService<List<Object>>(
				exe);

		Future<List<Object>> res = null;

		// Submit tasks
		Object port;
		for (URI key : services.keySet()) {
			port = services.get(key);
			cs.submit(new PSICQUICRemoteTask(interactors, reqInfo, operator,
					port, key, OperationType.GET_COUNT));
			System.out.println("Submit search query to " + key);
		}

		try {
			for (int i = 0; i < services.size(); i++) {
				res = cs.take();
				URI name = (URI) res.get().get(0);
				QueryResponse qr = (QueryResponse) res.get().get(1);
				result.put(name, qr);
				System.out.println("End: " + name + " ---> "
						+ qr.getResultInfo().getTotalResults());
			}

			long endTime = System.currentTimeMillis();
			double sec = (endTime - startTime) / (1000.0);
			System.out.println("PSICQUIC DB search finished in " + sec
					+ " msec.");
		} catch (Exception ee) {
			ee.printStackTrace();
			CyLogger.getLogger().fatal(
					"Could not complete PSICQUIC search task.", ee);
			throw new CyWebServiceException(
					CyWebServiceException.WSErrorCode.REMOTE_EXEC_FAILED);

		} finally {
			// res.cancel(true);
			exe.shutdown();
		}

		return result;
	}

	public Map<URI, List<QueryResponse>> getByInteractorList(
			List<DbRef> interactors, RequestInfo reqInfo, String operator)
			throws CyWebServiceException {

		final Map<URI, List<QueryResponse>> result = new ConcurrentHashMap<URI, List<QueryResponse>>();

		System.out.println("===== Import Start ====");
		final ExecutorService exe = Executors.newCachedThreadPool();
		final long startTime = System.currentTimeMillis();

		final CompletionService<List<Object>> cs = new ExecutorCompletionService<List<Object>>(
				exe);

		Future<List<Object>> res = null;

		// Submit tasks
		Object port;
		for (URI key : services.keySet()) {
			port = services.get(key);
			cs.submit(new PSICQUICRemoteTask(interactors, reqInfo, operator,
					port, key, OperationType.IMPORT));
			System.out.println("Submit search query to " + key);
		}

		try {

			for (int i = 0; i < services.size(); i++) {
				res = cs.take();
				URI name = (URI) res.get().get(0);
				final List<QueryResponse> qrList = new ArrayList<QueryResponse>();
				for (int j = 1; j < res.get().size(); j++) {
					qrList.add((QueryResponse) res.get().get(j));
				}
				result.put(name, qrList);
				System.out.println("End: " + name);
			}

			long endTime = System.currentTimeMillis();
			double sec = (endTime - startTime) / (1000.0);
			System.out.println("PSICQUIC DB search finished in " + sec
					+ " msec.");
		} catch (Exception ee) {
			ee.printStackTrace();
			CyLogger.getLogger().fatal(
					"Could not complete PSICQUIC search task.", ee);
			throw new CyWebServiceException(
					CyWebServiceException.WSErrorCode.REMOTE_EXEC_FAILED);

		} finally {
			// res.cancel(true);
			exe.shutdown();
		}

		return result;
	}

	class PSICQUICRemoteTask implements Callable<List<Object>>,
			CyWebServiceEventListener {

		List<DbRef> interactorList;
		RequestInfo reqInfo;
		String operator;

		Object port;
		URI portName;

		OperationType type;

		public PSICQUICRemoteTask(List<DbRef> interactorList,
				RequestInfo reqInfo, String operator, Object port,
				URI portName, OperationType type) {
			this.interactorList = interactorList;
			this.reqInfo = reqInfo;
			this.operator = operator;
			this.port = port;
			this.portName = portName;
			this.type = type;

			WebServiceClientManager.getCyWebServiceEventSupport()
					.addCyWebServiceEventListener(this);
		}

		public List<Object> call() throws CyWebServiceException {

			final List<Object> result = new ArrayList<Object>();
			result.add(portName);

			if (type.equals(OperationType.IMPORT)) {

				QueryResponse firstRes = callService();
				result.add(firstRes);
				int total = firstRes.getResultInfo().getTotalResults();
				if (total > reqInfo.getBlockSize()) {
					int index = reqInfo.getBlockSize();
					while (total > index) {
						reqInfo.setFirstResult(index);
						result.add(callService());
						index = index + reqInfo.getBlockSize();
					}
				}

			} else {
				// This is a count operation
				result.add(callService());
			}
			return result;
		}

		private QueryResponse callService() {

			QueryResponse ret = null;

			// Use reflection to import all results.

			Method method;
			try {
				method = port.getClass().getMethod(
						"getByInteractorList",
						new Class[] { List.class, RequestInfo.class,
								String.class });
			} catch (SecurityException e2) {
				e2.printStackTrace();
				return null;
			} catch (NoSuchMethodException e2) {
				e2.printStackTrace();
				return null;
			}

			try {
				ret = (QueryResponse) method.invoke(port, new Object[] {
						interactorList, reqInfo, operator });
			} catch (IllegalArgumentException e3) {
				System.err.println("IllegalArgumentException !!");
				e3.printStackTrace();
				return null;
			} catch (IllegalAccessException e3) {
				System.err.println("IllegalAccessException !!");
				e3.printStackTrace();
				return null;
			} catch (InvocationTargetException e3) {
				System.err.println("InvocationTargetException !!");
				e3.getCause().printStackTrace();
				return null;
			}

			return ret;
		}

		public void executeService(CyWebServiceEvent event)
				throws CyWebServiceException {

			if (event.getEventType().equals(WSEventType.CANCEL)) {
				throw new CyWebServiceException(
						CyWebServiceException.WSErrorCode.REMOTE_EXEC_FAILED);
			}
		}
	}
}
