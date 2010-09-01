/*
 Copyright (c) 2010, The Cytoscape Consortium (www.cytoscape.org)

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


import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;


public class TunableHandlerTest {
	private final HasAnnotatedField hasAnnotatedField = new HasAnnotatedField();
	private final HasAnnotatedSetterAndGetterMethods hasAnnotatedSetterAndGetterMethods = new HasAnnotatedSetterAndGetterMethods();
	private TunableHandler fieldHandler;
	private TunableHandler getterAndSetterHandler;

	@Before
	public final void init() throws Exception {
		final Field annotatedIntField = hasAnnotatedField.getClass().getField("annotatedInt");
		final Tunable annotatedIntTunable = annotatedIntField.getAnnotation(Tunable.class);
		fieldHandler = new SimpleTunableHandler(annotatedIntField, hasAnnotatedField, annotatedIntTunable);

		final Method getterMethod = HasAnnotatedSetterAndGetterMethods.class.getMethod("getPrivateInt");
		final Method setterMethod = HasAnnotatedSetterAndGetterMethods.class.getMethod("setPrivateInt", int.class);
		final Tunable getterAndSetterTunable = getterMethod.getAnnotation(Tunable.class);
		getterAndSetterHandler = new SimpleTunableHandler(getterMethod, setterMethod,
		                                                  hasAnnotatedSetterAndGetterMethods,
		                                                  getterAndSetterTunable);
	}

	@Test
	public final void testSetAndGetValue() throws Exception {
		final Integer newIntValue = Integer.valueOf(-3);
		fieldHandler.setValue(newIntValue);
		assertEquals("Value set on an annotated field is not as expected!", newIntValue, fieldHandler.getValue());

		getterAndSetterHandler.setValue(newIntValue);
		assertEquals("Value set on an annotated getter/setter pair is not as expected!", newIntValue, getterAndSetterHandler.getValue());
	}

	@Test
	public final void testGetDescription() {
		assertEquals("Description on an annotated field is not as expected!", "An annotated field", fieldHandler.getDescription());
		assertEquals("Description on an annotated getter/setter pair is not as expected!",
		             "Annotated setters and getters", getterAndSetterHandler.getDescription());
	}

	@Test
	public final void testGetFlags() {
		assertArrayEquals("The flags property of the tunable of an annotated field is not as expected!",
		                  new Tunable.Param[] { }, fieldHandler.getFlags());
		assertArrayEquals("The flags property of the tunable of an annotated getter/setter pair is not as expected!",
		                  new Tunable.Param[] { }, getterAndSetterHandler.getFlags());
	}

	@Test
	public final void testGetAlignments() {
		assertArrayEquals("The alignments property of the tunable of an annotated field is not as expected!",
		                  new Tunable.Param[] { }, fieldHandler.getAlignments());
		assertArrayEquals("The alignments property of the tunable of an annotated getter/setter pair is not as expected!",
		                  new Tunable.Param[] { }, getterAndSetterHandler.getAlignments());
	}

	@Test
	public final void testGetGroups() {
		assertArrayEquals("The groups property of the tunable of an annotated field is not as expected!",
		                  new String[] { "group1" }, fieldHandler.getGroups());
		assertArrayEquals("The groups property of the tunable of an annotated getter/setter pair is not as expected!",
		                  new String[] { "group2" }, getterAndSetterHandler.getGroups());
	}

	@Test
	public final void testGetGroupTitleFlags() {
		assertArrayEquals("The group title flags property of the tunable of an annotated field is not as expected!",
		                  new Tunable.Param[] { }, fieldHandler.getGroupTitleFlags());
		assertArrayEquals("The group title flags property of the tunable of an annotated getter/setter pair is not as expected!",
		                  new Tunable.Param[] { }, getterAndSetterHandler.getGroupTitleFlags());
	}

	@Test
	public final void testControlsMutuallyExclusiveNestedChildren() {
		assertEquals("The controls-mutually-exclusive-nexted-children state of the tunable of an annotated field is not as expected!",
		             false, fieldHandler.controlsMutuallyExclusiveNestedChildren());
		assertEquals("The controls-mutually-exclusive-nexted-children state of the tunable of annotated getter/setter pair is not as expected!",
		             false, getterAndSetterHandler.controlsMutuallyExclusiveNestedChildren());
	}

	@Test
	public final void testGetChildKey() {
		assertEquals("The child-key property of an annotated field is not as expected!",
		             "", fieldHandler.getChildKey());
		assertEquals("The child-key property of an annotated getter/setter pair is not as expected!",
		             "", getterAndSetterHandler.getChildKey());
	}

	@Test
	public final void testGetName() {
		assertEquals("Name of an annotated field is not as expected!", "annotatedInt", fieldHandler.getName());
		assertEquals("Name of an annotated getter/setter pair is not as expected!", "PrivateInt", getterAndSetterHandler.getName());
	}

	@Test
	public final void testGetDependsOn() {
		assertEquals("The depends-on property of an annotated field is not as expected!",
		             "Fred", fieldHandler.dependsOn());
		assertEquals("The depends-on property of an annotated getter/setter pair is not as expected!",
		             "Bob", getterAndSetterHandler.dependsOn());
	}

	@Test
	public final void testGetQualifiedName() {
		assertEquals("Qualified name of an annotated field is not as expected!",
		             "HasAnnotatedField.annotatedInt", fieldHandler.getQualifiedName());
		assertEquals("Qualified name of an annotated getter/setter pair is not as expected!",
		             "HasAnnotatedSetterAndGetterMethods.PrivateInt", getterAndSetterHandler.getQualifiedName());
	}
}


class HasAnnotatedField {
	@Tunable(description="An annotated field", groups={"group1"}, dependsOn="Fred")
	public int annotatedInt;

	public int getAnnotatedInt() { return annotatedInt; }
}


class HasAnnotatedSetterAndGetterMethods {
	private int privateInt;

	public void setPrivateInt(final int newValue) {
		privateInt = newValue;
	}

	@Tunable(description="Annotated setters and getters", groups={"group2"}, dependsOn="Bob")
	public int getPrivateInt() {
		return privateInt;
	}
}


class SimpleTunableHandler extends AbstractTunableHandler {
	public SimpleTunableHandler(final Field field, final Object instance, final Tunable tunable) {
		super(field, instance, tunable);
	}

	public SimpleTunableHandler(final Method getter, final Method setter, final Object instance, final Tunable tunable) {
		super(getter, setter, instance, tunable);
	}
}
