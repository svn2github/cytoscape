package com.agilent.AdaptiveJavaHelp.example;

import com.agilent.AdaptiveJavaHelp.Condition;

public class HasButtonBeenClickedCondition implements Condition
{
    public boolean isMet()
    {
	return ExampleMain.hasButtonBeenClicked();
    }
}
