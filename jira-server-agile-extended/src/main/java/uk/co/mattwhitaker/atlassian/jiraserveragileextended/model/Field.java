package uk.co.mattwhitaker.atlassian.jiraserveragileextended.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Field {
  private String name;
  private String value;
}
