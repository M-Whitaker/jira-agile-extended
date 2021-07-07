package uk.co.mattwhitaker.atlassian.jiracloudagileextended.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Issue {
    private long id;
    private String key;
    private Fields fields;
}
