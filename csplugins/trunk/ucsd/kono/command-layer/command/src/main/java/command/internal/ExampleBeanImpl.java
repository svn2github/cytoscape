package command.internal;

import command.ExampleBean;

/**
 * Internal implementation of our example Spring Bean
 */
public class ExampleBeanImpl
    implements ExampleBean
{
    public boolean isABean()
    {
        return true;
    }
}
