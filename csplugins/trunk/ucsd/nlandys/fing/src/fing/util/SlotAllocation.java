package fing.util;

/**
 * The purpose of this class is to handle the logic of keeping a pool of
 * free integer 'slots'.  In reality, the definition of this
 * class was motivated by the implementation fing.model.FRootGraph, which
 * provides index-to-node mappings via array of nodes.  We want to find
 * empty slots in the array in constant time during the create node
 * operation.
 *
 * While it is possible to increase the number of total slots (free and
 * reserved), it is not possible to decrease the number of total slots.
 */
public class SlotAllocation
{

  /**
   * Creates this object such that free slots after construction are
   * 0 through size-1, inclusive.
   */
  public SlotAllocation(int size) { }

  /**
   * The range of values returned by this method is 0 through getSize()-1,
   * inclusive.
   * @exception OutOfSlotsException if all slots have been reserved; you can
   *   do a pre-check for this condition by calling getNumFreeSlots().
   * @return the slot that has been reserved as a result of calling this
   *   method.
   */
  public int reserveFreeSlot() { return -1; }

  /**
   * Returns a slot to the pool of free slots.
   * It is an error to return a slot that is already free - such an action
   * is not checked and will cause serious data corruption.
   * @exception IllegalArgumentException if slot < 0 or if slot >= getSize().
   */
  public void freeReservedSlot(int slot) { }

  /**
   * Has an affect on getSize() as such:  if N is getSize() before calling
   * this method, then getSize() will return N + sizeIncrease after this
   * method returns.
   * @exception IllegalArgumentException if sizeIncrease is not a positive
   *   integer or if sizeIncrease + getSize() > Integer.MAX_VALUE.
   */
  public void increaseSizeBy(int sizeIncrease) { }

  /**
   * Defines the range of values returned by reserveFreeSlot().
   */
  public int getSize() { return -1; }

  /**
   * Returns an non-negative integer representing the number of times that
   * reserveFreeSlot() can be called in succession without throwing a
   * OutOfSlotsException.
   */
  public int getNumFreeSlots() { return -1; }

  public static class OutOfSlotsException extends RuntimeException {}

}
