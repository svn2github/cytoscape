/**
 * Copyright 2006 Andrea Splendiani
 * Released under GPL license
 */

/*
 * Created on Jul 7, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package fr.pasteur.sysbio.rdfscape.knowledge;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;

import javax.swing.Action;

/**
 * @author andrea
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class InfRuleObject {
	private String rule=null;
	private String name=null;
	private boolean isActive=true;
	private Hashtable myParams=null;
	
	public void setParam(String key, String value) {
		myParams.put(key,value);
	}
	public String getParam(String key) {
		String answer=(String)myParams.get(key);
		if(answer==null) answer="";
		return answer;
	}
	
	/**
	 * @return Returns the isActive.
	 */
	public boolean isActive() {
		return isActive;
	}
	/**
	 * @param isActive The isActive to set.
	 */
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	
	
	public InfRuleObject(String s) {
		System.out.println("Read rule "+s);
		rule=s;
		name="rule";
		myParams=new Hashtable();
	}
	/**
	 * 
	 */
	public InfRuleObject() {
		name="rule";
		myParams=new Hashtable();
		rule=new String("");
	}
	/**
	 * @return Returns the name.
	 */
	public String getName() {
		int begin=0;
		int end=0;
		begin=rule.indexOf('[');
		end=rule.indexOf(':');
		return rule.substring(begin+1,end).trim();
	}
	/**
	 * @return Returns the rule.
	 */
	public String getRule() {
		return rule;
	}

	/**
	 * @param rule The rule to set.
	 */
	public void setRule(String rule) {
		this.rule = rule;
	}
	/**
	 * 
	 */
	public void invertSelected() {
		isActive=!isActive;
		System.out.println("->"+isActive);
		
	}
	
	
}
