package com.cloudkibo.kiboengage.model;

/**
 * The Class GroupItem is a Java Bean class that can be used for representing a
 * Chat conversation item.
 */
public class GroupItem
{

	/** The name. */
	private String name;
	private String uniqueid;

	public GroupItem(String name, String uniqueid) {
		this.name = name;
		this.uniqueid = uniqueid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUniqueid() {
		return uniqueid;
	}

	public void setUniqueid(String uniqueid) {
		this.uniqueid = uniqueid;
	}
}
