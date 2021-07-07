package uk.co.mattwhitaker.atlassian.jiracloudagileextended.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Fields {
    @JsonProperty("issuetype")
    private IssueType issueType;
}
