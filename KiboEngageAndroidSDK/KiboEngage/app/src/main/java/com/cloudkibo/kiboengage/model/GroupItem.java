package com.cloudkibo.kiboengage.model;

/**
 * The Class GroupItem is a Java Bean class that can be used for representing a
 * Chat conversation item.
 */
public class GroupItem
{

	/** The name. */
	private String name;

	/**
	 * Instantiates a new chat item.
	 * 
	 * @param name
	 *            the name
	 */
	public GroupItem(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name.
	 * 
	 * @param name
	 *            the new name
	 */
	public void setName(String name)
	{
		this.name = name;
	}


}
