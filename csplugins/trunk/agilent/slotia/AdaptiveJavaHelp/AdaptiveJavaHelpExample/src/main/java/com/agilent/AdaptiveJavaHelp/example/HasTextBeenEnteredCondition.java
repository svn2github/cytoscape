package com.agilent.AdaptiveJavaHelp.example;

import com.agilent.AdaptiveJavaHelp.Condition;

public class HasTextBeenEnteredCondition implements Condition
{
    public boolean isMet()
    {
	return ExampleMain.getText().equals("I heart AJH");
    }
}
