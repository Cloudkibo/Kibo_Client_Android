package com.cloudkibo.kiboengage.model;

/**
 * Created by sojharo on 16/10/2016.
 */
public class ChannelItem {

    /** The name. */
    private String name;
    private String description;
    private String companyId;
    private String groupId;
    private String createdBy;
    private String creationDate;
    private String activeStatus;
    private String channelId;

    public ChannelItem(String name, String description, String companyId, String groupId, String createdBy, String creationDate, String activeStatus, String channelId) {
        this.name = name;
        this.description = description;
        this.companyId = companyId;
        this.groupId = groupId;
        this.createdBy = createdBy;
        this.creationDate = creationDate;
        this.activeStatus = activeStatus;
        this.channelId = channelId;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(String activeStatus) {
        this.activeStatus = activeStatus;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

}
