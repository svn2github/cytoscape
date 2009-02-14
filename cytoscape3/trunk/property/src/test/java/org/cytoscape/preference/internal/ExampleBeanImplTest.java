package org.cytoscape.preference.internal;

import junit.framework.TestCase;

import org.cytoscape.property.ExampleBean;
import org.cytoscape.property.internal.ExampleBeanImpl;

public class ExampleBeanImplTest extends TestCase
{
    public void testBeanIsABean()
    {
        ExampleBean aBean = new ExampleBeanImpl();
        assertTrue( aBean.isABean() );
    }
}
