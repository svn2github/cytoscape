package org.cytoscape.bamevent;

import java.util.List;

import org.cytoscape.bamevent.impl.EventFactoryImpl;
/**
 * 
 * Factory for creating instances of Event related objects.
 *
 */
public interface EventFactory {
    /**
     * Obtain the singleton EventManager.
     */
    public EventManager getEventManager ();

    /**
     * Create an EventAction.
     */
    EventAction createEventAction (String eventAction);
    /**
     * Return a simple EventNotification that doesn't contain any supporting information.
     * 
     */
    <T>EventNotification<T> createEventNotification(Notifier notifier, T context, EventAction eventAction);

    /**
     * Return an EventNotification what contains one supporting piece of information.
     * @param <T> the type of the context
     * @param <S> the type of the supportingInfo
     *
     */
    public <T,S>EventNotification<T> createEventNotification (Notifier notifier, T context, EventAction eventAction, S supportingInfo);

    /**
     * Return an EventNotification what contains one supporting piece of information.
     * @param <T> the type of the context
     * @param <S1> the type of the first supportingInfo argument
     * @param <S2> the type of the second supportingInfo argument
     *
     */

    public <T,S1,S2>EventNotification<T> createEventNotification (Notifier notifier, T context, EventAction eventAction, S1 supportingArg1, S2 supportingArg2);

    /**
     * Return an EventNotification what contains one supporting piece of information.
     * @param <T> the type of the context
     * @param <S1> the type of the first supportingInfo argument
     * @param <S2> the type of the second supportingInfo argument
     * @param <S3> the type of the third supportingInfo argument
     *
     */
    public <T,S1,S2,S3>EventNotification<T> createEventNotification (Notifier notifier, T context, EventAction eventAction, S1 supportingArg1, S2 supportingArg2, S3 supportingArg3);


    /**
     * Return an EventNotification that contains an arbitrary list of supporting information.
     * 
     */
    <T>EventNotification<T> createEventNotification(Notifier notifier, T context, EventAction eventAction, List<?> supportingInfo);

    /**
     * Creates a new default EventConditions with the given EventAction.
     * @return a new EventConditions where 
     *         deliveryTiming=DeliveryTiming.CAN_BE_DELAYED,
     *         isSupressingDuplicates=false,
     *         deliveryMethod=DeliveryMethod.SYNCHRONOUS, and no
     *         EventFilter is used.
     */
    EventConditions createEventConditions (EventAction action);
    /**
     * Creates a new EventConditions with the given parameters.
     * @param action the EventAction a Listener is interested in receiving.
     *               One special EventAction is EventAction.ANY, which
     *               specifies that any EventAction will match this condition.
     * @param deliveryTiming when the Listener wishes to receive
     *                       EventNotifications.
     * @param collapseNotifications when true, specifies that when a
     *                          notification is batched, if matching
     *                          notifications occur in the batch, they
     *                          can be collapsed so that only one
     *                          notification is fired. Information
     *                          about the other notifications is made
     *                          available by chaining Notifications
     *                          from the one notification to the
     *                          others.  collapseNotifications is
     *                          useful for improving efficiency for
     *                          Listeners that perform the same
     *                          operations, such as refreshing a GUI
     *                          display.  When false, notifications
     *                          will not be collapsed.
     *                          collapseNotifications only makes sense
     *                          when the DeliveryTiming is CAN_BE_DELAYED.
     * @param deliveryMethod the manner in which a Listener
     *                       should be notified.
     * @param filter the EventFilter to be executed to test if a possible
     *               EventNotification should be given to a Listener.
     * @return EventConditions based on the values of the given
     * parameters.
     */
    EventConditions createEventConditions (EventAction action,
					   DeliveryTiming deliveryTiming,
					   boolean collapseNotifications,
					   DeliveryMethod deliveryMethod,
					   EventFilter filter);

    /**
     * Creates a new EventConditions with the given parameters.
     * @param action the EventAction a Listener is interested in receiving.
     *               One special EventAction is EventAction.ANY, which
     *               specifies that any EventAction will match this condition.
     * @param deliveryOptions The deliveryOptions object defining how
     *                        EventNotifications should be delivered
     *                        to a Listener.
     * @param filter the EventFilter to be executed to test if a possible
     *               EventNotification should be given to a Listener.
     * @return EventConditions based on the values of the given
     * parameters.
     */

    EventConditions createEventConditions (EventAction action,
					   DeliveryOptions deliveryOptions,
					   EventFilter filter);

    /**
     * Creates a new default DeliveryOptions.
     * @return DeliveryOptions with deliveryTiming=DeliveryTiming.CAN_BE_DELAYED,
     *         isSupressingDuplicates=false, and
     *         deliveryMethod=DeliveryMethod.SYNCHRONOUS.
     */
    DeliveryOptions createDeliveryOptions ();

    /**
     * Creates a new DeliveryOptions with the given information.
     * @param deliveryTiming when the Listener wishes to receive
     *                       EventNotifications.
     * @param collapseNotifications when true, specifies that when a
     *                          notification is batched, if matching
     *                          notifications occur in the batch, they
     *                          can be collapsed so that only one
     *                          notification is fired. Information
     *                          about the other notifications is made
     *                          available by chaining Notifications
     *                          from the one notification to the
     *                          others.  collapseNotifications is
     *                          useful for improving efficiency for
     *                          Listeners that perform the same
     *                          operations, such as refreshing a GUI
     *                          display.  When false, notifications
     *                          will not be collapsed.
     *                          collapseNotifications only makes sense
     *                          when the DeliveryTiming is CAN_BE_DELAYED.
     * @param deliveryMethod the manner in which a Listener
     *                       should be notified.
     */    
    DeliveryOptions createDeliveryOptions (DeliveryTiming deliveryTiming,
					   boolean collapseNotifications,
					   DeliveryMethod deliveryMethod);

}
