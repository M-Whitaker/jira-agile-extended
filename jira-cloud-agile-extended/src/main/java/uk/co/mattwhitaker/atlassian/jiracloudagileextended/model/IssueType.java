package uk.co.mattwhitaker.atlassian.jiracloudagileextended.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class IssueType {
    private long id;
    private String description;
    private String iconUrl;
    private String name;
    private Boolean subtask;
    private int hierarchyLevel;
}
