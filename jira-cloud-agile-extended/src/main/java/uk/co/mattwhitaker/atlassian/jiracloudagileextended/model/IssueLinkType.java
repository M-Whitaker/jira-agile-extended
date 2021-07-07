package uk.co.mattwhitaker.atlassian.jiracloudagileextended.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class IssueLinkType {
    private long id;
    private String name;
    private String outwardName;
    private String inwardName;
    private Boolean isSubTaskLinkType;
    private Boolean isSystemLinkType;
}
