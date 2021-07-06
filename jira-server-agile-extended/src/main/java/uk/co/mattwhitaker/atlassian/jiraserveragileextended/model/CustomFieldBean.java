package uk.co.mattwhitaker.atlassian.jiraserveragileextended.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonProperty;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public final class CustomFieldBean {
  @JsonProperty
  private String id;
  @JsonProperty
  private Long idAsLong;
  @JsonProperty
  private String name;
}
