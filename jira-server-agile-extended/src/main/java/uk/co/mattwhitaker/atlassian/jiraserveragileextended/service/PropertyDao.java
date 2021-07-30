package uk.co.mattwhitaker.atlassian.jiraserveragileextended.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("jae-propertyDao")
public class PropertyDao {

  public static final String SERVICE = "jae-propertyDao";
  private static final String KEY_JAE_PROPS = "JiraAgileExtended.properties";
  private static final long GLOBAL_ENTITY_ID = 1L;
  private final PersistenceService persistenceService;

  public PropertyDao(@Autowired PersistenceService persistenceService) {
    this.persistenceService = persistenceService;
  }

  public Long getLongProperty(String key) {
    return persistenceService.getLong(KEY_JAE_PROPS, 1L, key);
  }

  public void setLongProperty(String key, Long value) {
    persistenceService.setLong(KEY_JAE_PROPS, 1L, key, value);
  }

  public void deleteProperty(String key) {
    persistenceService.delete(KEY_JAE_PROPS, 1L, key);
  }

  public Boolean getBooleanProperty(String key) {
    return persistenceService.getBoolean(KEY_JAE_PROPS, 1L, key);
  }

  public void setBooleanProperty(String key, Boolean value) {
    persistenceService.setBoolean(KEY_JAE_PROPS, 1L, key, value);
  }

  public String getStringProperty(String key) {
    return persistenceService.getString(KEY_JAE_PROPS, 1L, key);
  }

  public void setStringProperty(String key, String value) {
    persistenceService.setString(KEY_JAE_PROPS, 1L, key, value);
  }

  public Object getPropertyAsType(String key) {
    return persistenceService.getPropertyAsType(KEY_JAE_PROPS, 1L, key);
  }
}
