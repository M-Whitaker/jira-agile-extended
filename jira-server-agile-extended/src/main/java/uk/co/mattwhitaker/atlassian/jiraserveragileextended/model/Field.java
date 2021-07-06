package uk.co.mattwhitaker.atlassian.jiraserveragileextended.model;

public class Field {

  private String name;
  private String value;

  public Field(String name, String value) {
    this.name = name;
    this.value = value;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
