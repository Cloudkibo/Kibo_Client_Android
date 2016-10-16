package com.cloudkibo.kiboengage.model;

/**
 * Created by sojharo on 16/10/2016.
 */
public class ChannelItem {

    /** The name. */
    private String name;

    /**
     * Instantiates a new chat item.
     *
     * @param name
     *            the name
     */
    public ChannelItem(String name)
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
