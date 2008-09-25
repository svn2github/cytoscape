
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

package org.cytoscape.work;

import java.lang.annotation.*;

/**
 * An annotation used to identifiy fields in an object that constitute values
 * which can be modified by a {@link org.cytoscape.work.tunable.TunableInterceptor}.  The name of the 
 * annotation is the name of the {@link java.lang.reflect.Field} being annotated.
 */
@Retention(RetentionPolicy.RUNTIME) // makes this availabe for reflection

@Target(ElementType.FIELD) // says we're just looking at fields (not methods or constructors)

public @interface Tunable {
/**
     * A brief (approximately one sentence) description of this {@link java.lang.reflect.Field}.  
     * The description should be suitable for presentation in a user interface.
     */
	public String description();
/**
     * The namespace of the Tunable.  All Tunables in a class should have the 
     * same namespace.  The namespace will be used to serialize the Tunable
     * to {@link java.util.Properties} files so should not use spaces or the '.' character.
     */
	public String namespace();
/**
     * The group can be specified to cluster Tunables within a class for more
     * coherent presentation within a user interface.
     */
	public String group() default "";
}
