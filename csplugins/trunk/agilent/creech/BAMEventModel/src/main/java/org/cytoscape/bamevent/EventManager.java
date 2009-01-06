package org.cytoscape.bamevent;

import java.util.List;


    /**
     * Manages the association of objects that want to receive
     * notifications with objects that produce notifications. Also
     * dispatches events. 
     *
     * <H4>Assumptions</H4>
     * We must:
     * <OL>
     *  <LI> Handle both course grained and fine grained events.</LI>
     *     <ol type=a>
     *     <li> allow for class-based listening (if any instance fires a notification).</li>
     *     <li> allow for instance-based listening (just one instance fires a notification).</li>
     *     </ol>
     *  <LI> Handle synchronous and asynchronous events.</LI>
     *  <LI> Handle batch and individual events.</LI>
     *  <LI> Isolate all data access by placing in an object versus  accessing data thru method parameters.</LI>
     *  <LI> Decouple Notifiers from Listeners as much as possible.</LI>
     *    <OL type=a>
     *    <LI> Reduce needed methods. </LI>
     *    <LI> Reduce needed management of Listeners within Notifiers.</LI></LI>
     *  </OL>
     * </OL>
     * <H4>Terminology</H4>
     *  <DL>
     *    <DT>notifier</DT>
     *    <DD>the object that fired the event (that is doing the notification).</DD>
     *    <DT>context</DT>
     *        <DD>the object this notification is about (such as a Network).</DD>
     *    <DT>event action</DT>
     *         <DD>what happened to the context object (e.g., added a Node)</DD>
     *    <DT>supporting info</DT>
     *         <DD>more detailed information about the event (e.g., a node that was added).
     *    <DT>listener</DT>
     *         <DD>the object that is notified of an event.</DD>
     *  </DL>
     *
     * <H4>Examples</H4>
     * Here are some examples of events and what would need to be passed to registerInterest() and dispatch() methods for a Listener
     * to be notified of the event.
     * <OL>
     *     <LI>  Network net1 was destroyed.
     *     <BR>   registerInterest:
     *                 <BLOCKQUOTE>
     *               notifier=Cytoscape.class, EventConditions: [DeliveryTiming.DELIVER_IMMEDIATELY, DeliveryMethod.SYNCHRONOUS, EventFilter=NO_FILTER, EventAction: "NETWORK.DESTROYED"];
     *                 </BLOCKQUOTE>
     *       dispatch:
     *                 <BLOCKQUOTE>
     *               notifier=Cytoscape.class, EventNotification: [context=net1, EventAction: "NETWORK.DESTROYED", supportingInfo: null]
     *                 </BLOCKQUOTE>
     *     <LI>  A node node1 was added to Network net1
     *     <BR>   registerInterest:
     *                 <BLOCKQUOTE>
     *               notifier=net1, EventConditions: [DeliveryTiming.DELIVER_IMMEDIATELY, DeliveryMethod.SYNCHRONOUS, EventFilter=NO_FILTER, EventAction: "NODE.ADDED"]
     *                 </BLOCKQUOTE>
     *        dispatch:
     *                 <BLOCKQUOTE>
     *               notifier=net1, EventNotification: [context=net1, EventAction="NODE.ADDED", supportingInfo=(node1)]
     *                 </BLOCKQUOTE>
     *     <LI>  A node (node1) was added to some Network
     *     <BR>   registerInterest:
     *                 <BLOCKQUOTE>
     *               notifier=CyNetwork.class, EventConditions: [DeliveryTiming.DELIVER_IMMEDIATELY, DeliveryMethod.SYNCHRONOUS, EventFilter=NO_FILTER, EventAction: "NODE.ADDED"]
     *                 </BLOCKQUOTE>
     *        dispatch:
     *                 <BLOCKQUOTE>
     *               notifier=netn, EventNotification: [context=netn, EventAction="NODE.ADDED", supportingInfo=(node1)]
     *                 </BLOCKQUOTE>
     *     <LI>  Some sort of Network operation occurred in net1
     *     <BR>   registerInterest:
     *                 <BLOCKQUOTE>
     *               notifier=net1, EventConditions: [DeliveryTiming.DELIVER_IMMEDIATELY, DeliveryMethod.SYNCHRONOUS, EventFilter=NO_FILTER, EventAction: EventAction.ANY]
     *                 </BLOCKQUOTE>
     *        dispatch:
     *                 <BLOCKQUOTE>
     *               notifier=netn, EventNotification: [context=net1, EventAction=<various>, supportingInfo=(<various>)]
     *                 </BLOCKQUOTE>
     *     <LI>  Edge edge1's 'canonicalName' attribute changed from "htz1(pp)MPS3" to "special edge" [assume C3 attributes]
     *     <BR>   registerInterest:
     *                 <BLOCKQUOTE>
     *               notifier=edge1, EventConditions: [DeliveryTiming.DELIVER_IMMEDIATELY, DeliveryMethod.SYNCHRONOUS, EventFilter=NO_FILTER, EventAction: "ATTRIBUTE.CHANGED"]
     *                 </BLOCKQUOTE>
     *        dispatch:
     *                 <BLOCKQUOTE>
     *               notifier=edge1 EventNotification: [context=edge1, EventAction="ATTRIBUTE.CHANGED", supportingInfo=("canonicalName"
     *                                    "htz1(pp)MPS3")]
     *                 </BLOCKQUOTE>
     *     <LI>  CyDataTable table1 had 'attribute6' change from value "oldValue" to "newValue"
     *     <BR>   registerInterest:
     *                 <BLOCKQUOTE>
     *               notifier=table1, EventConditions: [DeliveryTiming.DELIVER_IMMEDIATELY, DeliveryMethod.SYNCHRONOUS, EventFilter=JustAttribute6Filter,
     *                                                  EventAction: "ATTRIBUTE.CHANGED"]
     * <PRE>
     * private class JustAttribute6Filter implements EventFilter {
     *    public boolean includeEvent (EventNotification note) {
     *        // ASSUME: Non-null first argument is a String:
     *        String attributeName = (String)(note.getSupportingInfo().get(0));
     *        return "attribute6".equals (attributeName); }
     * }
     * </PRE>
     *                 </BLOCKQUOTE>
     *    <BR>    dispatch:
     *                 <BLOCKQUOTE>
     *               notifier=table1 EventNotification: [context=table1, EventAction="ATTRIBUTE.CHANGED", supportingInfo=("attribute6"
     *                                    "oldValue")]
     *                 </BLOCKQUOTE>
     *     <LI>  The plugin state can now be saved.
     *     <BR>       registerInterest:
     *                 <BLOCKQUOTE>
     *               notifier=Cytoscape.class, EventConditions: [DeliveryTiming.DELIVER_IMMEDIATELY, DeliveryMethod.SYNCHRONOUS, EventFilter=NO_FILTER, EventAction: "SAVE_PLUGIN_STATE"];
     *                 </BLOCKQUOTE>
     *            dispatch:
     *                 <BLOCKQUOTE>
     *               notifier=Cytoscape.class, EventNotification: [context=cytoscape.class, EventAction: "SAVE_PLUGIN_STATE", supportingInfo: null]
     *                 </BLOCKQUOTE>
     *     <LI> A Network 'net1' was loaded.
     *     <BR>      registerInterest:
     *                 <BLOCKQUOTE>
     *               notifier=Cytoscape.class, EventConditions: [DeliveryTiming.DELIVER_IMMEDIATELY, DeliveryMethod.SYNCHRONOUS, EventFilter=NO_FILTER, EventAction: "NETWORK.LOADED"];
     *                 </BLOCKQUOTE>
     *               dispatch:
     *                 <BLOCKQUOTE>
     *               notifier=Cytoscape.class, EventNotification: [context=cytoscape.class, EventAction: "NETWORK.LOADED", supportingInfo: (net1)]
     *                 </BLOCKQUOTE>
     * </OL>
     * <H4>Questions</H4>
     * <OL>
     * <LI> What would be some good examples of batch notifications?
     * <OL type=a>
     * <LI>  Will the existing scheme work where notifications can be delayed on a
     *      Notifier basis until dispatchDelayed() is called?</LI>
     * </OL>
     * <P><LI> What should we do for EventNotification on complex attribute
     *    changes?</P>
     * <P>   Doesn't seem like there is much we can do to specify what
     *    changed in a complex attribute (e.g., a List of Maps)
     *    without quickly getting into a bunch of complexity. We could
     *    possibly deep copy the attribute value and pass that to the
     *    Listener, but this may be very inefficient.</P></LI>
     * </OL>
     *
     */

public interface EventManager {
    /**
     * a Listener specifies interest in receiving notifications from a given Notifier.
     *
     * <H4>Questions</H4>
     * <OL>
     * <LI> Is a two-level key (Notifier,EventAction) sufficient for
     *      all events?
     * <P> There is one case where a three-level key
     *         would be useful--following events on a given
     *         CyDataTable entry.  In this case you would want to
     *         register interest in an event that deals with a given
     *         CyDataTable, has a given EventAction (e.g., ATTRIBUTE.CHANGED),
     *         and deals with given attribute key (e.g., Entry15). The way
     *         this would have to handled using a two-level key is to
     *         use an EventFilter. The filter would only allow the
     *         EventReciever to be called when the appropriate
     *         CyDataTable entry key was part of the
     *         EventNotification.</P>
     * </LI></OL>
     * @param notifier the Notifier object for which notified 
     *                 wishes to receive events of interest.  The
     *                 notifier should clearly define what an
     *                 Listener will find in an EventNotification
     *                 from a dispatch().
     * @param notified the object that wishes to receive the events of interest.
     * @param conditions details on the conditions under which notified wishes to receive
     *                   notification.
     *
     * @see EventManager#dispatch
     */
    void registerInterest (Notifier notifier, Listener notified, EventConditions conditions);


    /**
     * a Listener specifies interest in receiving notifications from any Notifiers of a  given class.
     * @param notifierClass the class of the Notifier objects that notified
     *                 wish to receive notifications from.  This is
     *                 commonly used for receiving notifications about
     *                 created objects.  notifierClass must implement
     *                 Notifier, or a IllegalArgumentException will be
     *                 thrown.  The notifier should clearly define
     *                 what a Listener will find in an
     *                 EventNotification from a dispatch().
     * @param notified the object that wishes to receive the events of interest.
     * @param conditions details on the conditions under which notified wishes to receive
     *                   notification.
     */

    void registerInterest (Class<? extends Notifier> notifierClass, Listener notified, EventConditions conditions);    

    /**
     * 
     * A given Listener removes interest in receiving events
     * from a given Notifier. The given information is used to
     * specify the details of the association to remove.
     * @param notifier the Notifier that notified
     *                 wishes to no longer receive notifications about.
     * @param notified the object that wishes to non longer receive the events of interest.
     * @param conditions details on the conditions under which notified wishes to no
     *                   longer receive notification.
     * @return true if the given association was found and removed. Return false otherwise.
     */
    boolean unregisterInterest (Notifier notifier, Listener notified, EventConditions conditions);

    /**
     * 
     * A given Listener removes interest in receiving events
     * from all Notifiers of a given class.
     * @param notifierClass the class of the Notifiers that notified
     *                 wishes to stop receiving notifications about.
     *                 This is commonly used for receiving
     *                 notifications about created objects.
     *                 notifierClass must implement Notifier, or a
     *                 IllegalArgumentException will be thrown.
     * @param notified the object that wishes to non longer receive the events of interest.
     * @param conditions details on the conditions under which notified wishes to no
     *                   longer receive notification.
     * @return true if the given association was found and removed. Return false otherwise.
     */
    boolean unregisterInterest (Class<? extends Notifier> notifierClass, Listener notified, EventConditions conditions);

    
    /**
     * Begin accumulating
     * dispatched notifications in a notification queue.  The notifications
     * accumulated are all those where the Listener does not wish
     * immediate notification.  This occurs when the Listener
     * specified isImmediate=false in its EventConditions.
     * Accumulation is nested in the sense that multiple
     * startAccumulating() calls will only stop accumulating when the
     * last corresponding dispatchAccumulated() is called.
     *
     * <H4>Questions</H4>
     * <OL>
     * <LI>Is nested accumulation too complex?
     * <P>Accumulation seems to require nesting since a method that does accumulation might be used by yet a
     * higher-level method that is also accumulating. Some problems with the nesting are:
     * <OL TYPE=a>
     * <LI>What happens if a user doesn't complete the startAccumulating()-dispatchAccumulated() correctly?
     * <LI>What happens when error handling throws an exception?
     * </OL>
     * </OL>
     * @see EventManager#dispatchAccumulated
     * 
     */
    void  startAccumulating ();

    /**
     * Fire a notification to all interested Listeners
     * handing them the given EventNotification. This will cause
     * all Listeners that qualify to have their
     * receivedNotification() method called with the given
     * EventNotification.
     * Note that since the EventNotification is handed to the
     * Listeners without modification, subclasses of
     * EventNotification can be created and used.
     * @param notifier the Notifier that is generating this notification.
     * @param notification the EventNotification containing the details of the event.
     * @see EventManager#registerInterest
     */
    void dispatch (Notifier notifier, EventNotification<?> notification);

    /**
     * Obtain the queue of accumulated EventNotifications
     * and dispatch these notifications when necessary. Notifications are only
     * dispatched when we are not nested in higher-level accumulations (only one startAccumulating() is unprocessed).
     * If we are nested, no action is performed.
     * @return true iff at least one accumulated EventNotification was dispatched.
     * @see EventManager#startAccumulating
     */
    boolean dispatchAccumulated ();

    /**
     * Are there Listeners for a given Notifier under given EventConditions?
     * @param notifier the Notifier we want to check for Listeners.
     * @param notified the object that wishes to receive the events of interest.
     * @param conditions details on the conditions under which notified wishes to receive
     *                   notification.
     * @return true iff there are one or more Listeners for
     *         the given Notifier based on the given
     *         EventConditions. This method can be very helpful
     *         for complex notifications by helping Notifiers
     *         avoid generating unnecessary EventNotifications
     *         when there are no Listeners.
     */
    boolean hasListeners (Notifier notifier, Listener notified, EventConditions conditions);
    /**
     * Get the Listeners for a given Notifier for the given EventConditions.
     * Returns a safely modifiable List of the Listeners for
     *         the given Notifier based on the given
     *         EventConditions.
     * @param notifier the Notifier we want to check for Listeners.
     * @param notified the object that wishes to receive the events of interest.
     * @param conditions details on the conditions under which notified wishes to receive
     *                   notification.
     */
    List<Listener> getListeners (Notifier notifier, Listener notified, EventConditions conditions);
}
