/*
	
	MultiLevelLayoutPlugin for Cytoscape (http://www.cytoscape.org/) 
	Copyright (C) 2007 Pekka Salmela

	This program is free software; you can redistribute it and/or
	modify it under the terms of the GNU General Public License
	as published by the Free Software Foundation; either version 2
	of the License, or (at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.
	
	You should have received a copy of the GNU General Public License
	along with this program; if not, write to the Free Software
	Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
	
 */

package multilevelLayoutPlugin;

import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Container class used to store node positions. Each node is referred by 
 * its unique root graph index number.
 * @author Pekka Salmela
 *
 */
public class NodePositionManager {
	
	/**
	 * Mapping from root graph indices to node positions.
	 */
	public Map<Integer, Point2D> positions;
	
	/**
	 * Class constructor.
	 *
	 */
	public NodePositionManager(){
		positions = Collections.synchronizedMap(new HashMap<Integer, Point2D>());
	}
	
	/**
	 * Constructor with parameter for the initial capacity for
	 * the container. Throws <code>IllegalArgumentException</code> if
	 * <code>initialCapacity</code> is negative.
	 * @param initialCapacity
	 */
	public NodePositionManager(int initialCapacity) throws IllegalArgumentException{
		positions = Collections.synchronizedMap(new HashMap<Integer, Point2D>(initialCapacity));
	}
	
	/**
	 * Adds a new node position to the container.
	 * @param id Root graph index of the node.
	 * @param x X-coordinate of the node.
	 * @param y Y-coordinate of the node.
	 */
	public void addNode(int id, double x, double y) throws IllegalStateException{
		if(x == Double.NaN  || y == Double.NaN) throw new IllegalStateException();
		positions.put(new Integer(id), new Point2D.Double(x, y));
		//System.out.println("Added node with index " + id);
	}
	
	/**
	 * Set the position of a node. Throws a 
	 * <code>NullPointeException</code> if there is no node mapped using
	 * the given root graph index.
	 * @param id Root graph index of the node.
	 * @param x X-coordinate of the node.
	 * @param y Y-coordinate of the node.
	 */
	public void setPosition(int id, double x, double y) throws NullPointerException, IllegalStateException{
		if(x == Double.NaN || y == Double.NaN) throw new IllegalStateException();
		positions.get(new Integer(id)).setLocation(x, y);
	}
	
	/**
	 * Set the x-coordinate of a node. Throws a 
	 * <code>NullPointeException</code> if there is no node mapped using
	 * the given root graph index.
	 * @param id Root graph index of the node.
	 * @param x X-coordinate of the node.
	 */
	public void setX(int id, double x) throws NullPointerException, IllegalStateException{
		if(x == Double.NaN) throw new IllegalStateException();
		Point2D node = positions.get(new Integer(id));
		node.setLocation(x, node.getY());
	}
	
	/**
	 * Set the y-coordinate of a node. Throws a 
	 * <code>NullPointeException</code> if there is no node mapped using
	 * the given root graph index.
	 * @param id Root graph index of the node.
	 * @param x Y-coordinate of the node.
	 */
	public void setY(int id, double y) throws NullPointerException, IllegalStateException{
		if(y == Double.NaN) throw new IllegalStateException();
		Point2D node = positions.get(new Integer(id));
		node.setLocation(node.getX(), y);
	}
	
	/**
	 * Get the x-coordinate of a node. Throws a 
	 * <code>NullPointeException</code> if there is no node mapped using 
	 * the given root graph index.
	 * @param id Root graph index of the node.
	 * @return X-coordinate of the node with the given root graph index.
	 */
	public double getX(int id) throws NullPointerException{
		if (!positions.containsKey(new Integer(id))){
			System.out.println("Wtf, ei löytynyt nodea ID:llä " + id);
			positions.put(id, new Point2D.Double(0, 0));
		}
		return positions.get(new Integer(id)).getX();
	}
	
	/**
	 * Get the y-coordinate of a node. Throws a 
	 * <code>NullPointeException</code> if there is no node mapped using
	 * @param id Root graph index of the node.
	 * @return Y-coordinate of the node with the given root graph index.
	 */
	public double getY(int id) throws NullPointerException{
		return positions.get(new Integer(id)).getY();
	}
	
	/**
	 * Removes a node with given id (if present) from the container. 
	 * @param id Root graph index of the node to be removed.
	 */
	public void removeNode(int id){
		positions.remove(new Integer(id));
	}
}