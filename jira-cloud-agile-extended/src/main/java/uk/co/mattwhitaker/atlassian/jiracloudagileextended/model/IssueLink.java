package uk.co.mattwhitaker.atlassian.jiracloudagileextended.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class IssueLink {
    private long id;
    private long sourceIssueId;
    private long destinationIssueId;
    private IssueLinkType issueLinkType;
    private Boolean systemLink;
}
