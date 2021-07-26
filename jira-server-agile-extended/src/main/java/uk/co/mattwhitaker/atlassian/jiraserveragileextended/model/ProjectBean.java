package uk.co.mattwhitaker.atlassian.jiraserveragileextended.model;

import java.io.IOException;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class ProjectBean {

  @JsonProperty
  private Long id;
  @JsonProperty
  private String name;
  @JsonProperty
  private Boolean selected;

  public ProjectBean(String jsonString) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    Map<String, Object> map = objectMapper.readValue(jsonString,
        new TypeReference<Map<String, Object>>() {
        });
    this.id = Long.valueOf((Integer) map.get("id"));
    this.selected = (Boolean) map.get("selected");
  }

  public ProjectBean(Integer id) {
    this.id = Long.valueOf(id);
    this.selected = true;
  }
}
