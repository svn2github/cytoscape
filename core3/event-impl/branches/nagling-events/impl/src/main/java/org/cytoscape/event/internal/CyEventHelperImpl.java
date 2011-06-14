/*
 Copyright (c) 2008, The Cytoscape Consortium (www.cytoscape.org)

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
package org.cytoscape.event.internal;

import org.cytoscape.event.CyEvent;
import org.cytoscape.event.CyPayloadEvent;
import org.cytoscape.event.CyEventHelper;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Constructor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CyEventHelperImpl implements CyEventHelper {

	private static final Logger logger = LoggerFactory.getLogger(CyEventHelperImpl.class);

	private final CyListenerAdapter normal;
	private final Map<Object,Map<Class<?>,PayloadAccumulator<?,?,?>>> sourceAccMap;
	private final ScheduledExecutorService payloadEventMonitor; 

	public CyEventHelperImpl(final CyListenerAdapter normal) {
		this.normal = normal;

		sourceAccMap = Collections.synchronizedMap( new HashMap<Object,Map<Class<?>,PayloadAccumulator<?,?,?>>>());

		payloadEventMonitor = Executors.newSingleThreadScheduledExecutor();

        final Runnable firingAgent = new Runnable() {
            public void run() {
                forceFirePayloadEvents();
            }
        };
        payloadEventMonitor.scheduleAtFixedRate(firingAgent, CyEventHelper.DEFAULT_PAYLOAD_WAIT_TIME_MILLIS, CyEventHelper.DEFAULT_PAYLOAD_WAIT_TIME_MILLIS, TimeUnit.MILLISECONDS);
	}	

	@Override 
	public <E extends CyEvent<?>> void fireEvent(final E event) {
		System.out.println("firing event: " + event);
		normal.fireEvent(event);
	}


	@Override 
	public void silenceEventSource(Object eventSource) {
		if ( eventSource == null )
			return;
		logger.info("silencing event source: " + eventSource.toString());
		normal.silenceEventSource(eventSource);
	}

	@Override 
	public void unsilenceEventSource(Object eventSource) {
		if ( eventSource == null )
			return;
		logger.info("unsilencing event source: " + eventSource.toString());
		normal.unsilenceEventSource(eventSource);
	}

	@Override 
	public <S,P,E extends CyPayloadEvent<S,P>> void addEventPayload(S source, P payload, Class<E> eventType) {
		System.out.println("addEventPayload: " + source + "  " + payload + "  " + eventType);
		if ( payload == null || source == null || eventType == null) {
			logger.warn("improperly specified payload event with source: " + source + 
			            "  with payload: " + payload + 
						"  with event type: " + eventType);
			return;
		}

		Map<Class<?>,PayloadAccumulator<?,?,?>> cmap = sourceAccMap.get(source);
		if ( cmap == null ) { 
			cmap = Collections.synchronizedMap(new HashMap<Class<?>,PayloadAccumulator<?,?,?>>());
			sourceAccMap.put(source,cmap);
			System.out.println("  adding source to map: " + source);
		}

		PayloadAccumulator<S,P,E> acc = (PayloadAccumulator<S,P,E>) cmap.get(eventType);

		if ( acc == null ) {
			try {
				acc = new PayloadAccumulator<S,P,E>(source, eventType);
				cmap.put(eventType,acc);
				System.out.println("  adding accumulator: " + source + " " + eventType);
			} catch (NoSuchMethodException nsme) {
				logger.warn("Unable to add payload to event, because of missing event constructor.", nsme);
				return;
			}
		}

		System.out.println("   addEventPayload: " + source + "  " + payload + "  " + eventType);
		acc.addPayload(payload);
	}

	public void forceFirePayloadEvents() {
//		System.out.println("forceFirePayloadEvents in thread: " + Thread.currentThread());
		for ( Object source : sourceAccMap.keySet() ) {
//			System.out.println("   examining source: " + source);
			for ( PayloadAccumulator<?,?,?> acc : sourceAccMap.get(source).values() ) {
//				System.out.println("      found accumulator: " + acc);
				try {
					CyPayloadEvent<?,?> event = acc.newEventInstance( source );
					if ( event != null ) {
						System.out.println("        -----------force firing event: " + event);
						fireEvent(event);
					}
				} catch (Exception ie) {
					logger.warn("Couldn't instantiate event for source: " + source, ie);
				}
			}
		}
	}
}

