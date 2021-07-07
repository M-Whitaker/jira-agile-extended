package uk.co.mattwhitaker.atlassian.jiracloudagileextended.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class IssueLinkEvent {
    private long timestamp;
    private String webhookEvent;
    private IssueLink issueLink;
}
