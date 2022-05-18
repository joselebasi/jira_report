package com.unifin.jirareports.model.jira;

public class IssueDTO {

    @Override
    public String toString() {
        return getTimeSpent() + "," + getKey() + "," + getId() + "," + getName() + "," + getNameProject() + ","
                + getdCreated()+ ","+getDStarted()+ ","+getHistoryPoints();
    }

    private String id;
    private String key;
    private String summary;
    private String name;
    private String timeSpent;
    private String dCreated;
    private String dStarted;
    private String nameProject;
    private String commentWl;
    private String historyPoints;

    public String getHistoryPoints() {
        return this.historyPoints;
    }

    public void setHistoryPoints(String historyPoints) {
        this.historyPoints = historyPoints;
    }

    public String getDStarted() {
        return this.dStarted;
    }

    public void setDStarted(String dStarted) {
        this.dStarted = dStarted;
    }

    public String getDCreated() {
        return this.dCreated;
    }

    public void setDCreated(String dCreated) {
        this.dCreated = dCreated;
    }

    public String getCommentWl() {
        return this.commentWl;
    }

    public void setCommentWl(String commentWl) {
        this.commentWl = commentWl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(String timeSpent) {
        this.timeSpent = timeSpent;
    }

    public String getdCreated() {
        return dCreated;
    }

    public void setdCreated(String dCreated) {
        this.dCreated = dCreated;
    }

    public String getNameProject() {
        return nameProject;
    }

    public void setNameProject(String nameProject) {
        this.nameProject = nameProject;
    }

}
