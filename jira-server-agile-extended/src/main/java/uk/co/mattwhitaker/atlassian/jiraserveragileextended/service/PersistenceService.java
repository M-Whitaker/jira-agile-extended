package uk.co.mattwhitaker.atlassian.jiraserveragileextended.service;

import com.atlassian.jira.propertyset.JiraPropertySetFactory;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.opensymphony.module.propertyset.PropertyException;
import com.opensymphony.module.propertyset.PropertySet;
import com.thoughtworks.xstream.XStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.Validate;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("gh-persistenceService")
public class PersistenceService {

  private final Logger log = Logger.getLogger(this.getClass());
  private final XStream xstream = new XStream();
  private final JiraPropertySetFactory jiraPropertySetFactory;

  @Autowired
  public PersistenceService(@ComponentImport JiraPropertySetFactory jiraPropertySetFactory) {
    this.jiraPropertySetFactory = jiraPropertySetFactory;
  }

  public void setLong(String entityName, Long entityId, String key, Long value) {
    Validate.notNull(entityName);
    Validate.notNull(entityId);
    Validate.notNull(key);
    Validate.notNull(value);
    this.getPropertySet(entityName, entityId).setLong(key, value);
  }

  public Long getLong(String entityName, Long entityId, String key) {
    Validate.notNull(entityName);
    Validate.notNull(entityId);
    Validate.notNull(key);
    PropertySet propertySet = this.getPropertySet(entityName, entityId);
    return this.exists(propertySet, key) ? propertySet.getLong(key) : null;
  }

  public void setDouble(String entityName, Long entityId, String key, Double value) {
    Validate.notNull(entityName);
    Validate.notNull(entityId);
    Validate.notNull(key);
    Validate.notNull(value);
    this.getPropertySet(entityName, entityId).setDouble(key, value);
  }

  public Double getDouble(String entityName, Long entityId, String key) {
    Validate.notNull(entityName);
    Validate.notNull(entityId);
    Validate.notNull(key);
    PropertySet propertySet = this.getPropertySet(entityName, entityId);
    return this.exists(propertySet, key) ? propertySet.getDouble(key) : null;
  }

  public void setBoolean(String entityName, Long entityId, String key, Boolean value) {
    Validate.notNull(entityName);
    Validate.notNull(entityId);
    Validate.notNull(key);
    Validate.notNull(value);
    this.getPropertySet(entityName, entityId).setBoolean(key, value);
  }

  public Boolean getBoolean(String entityName, Long entityId, String key) {
    Validate.notNull(entityName);
    Validate.notNull(entityId);
    Validate.notNull(key);
    PropertySet propertySet = this.getPropertySet(entityName, entityId);
    return this.exists(propertySet, key) ? propertySet.getBoolean(key) : null;
  }

  public void setString(@Nonnull String entityName, @Nonnull Long entityId, @Nonnull String key,
      @Nonnull String value) {
    Validate.notNull(entityName);
    Validate.notNull(entityId);
    Validate.notNull(key);
    Validate.notNull(value);
    this.getPropertySet(entityName, entityId).setString(key, value);
  }

  @Nullable
  public String getString(@Nonnull String entityName, @Nonnull Long entityId, @Nonnull String key) {
    Validate.notNull(entityName);
    Validate.notNull(entityId);
    Validate.notNull(key);
    PropertySet propertySet = this.getPropertySet(entityName, entityId);
    return this.exists(propertySet, key) ? propertySet.getString(key) : null;
  }

  public Map<String, Object> getData(String entityName, Long entityId, String key) {
    Validate.notNull(entityName);
    Validate.notNull(entityId);
    Validate.notNull(key);
    String serializedData = this.getPropertySet(entityName, entityId).getText(key);
    return serializedData == null ? null : (Map) this.xstream.fromXML(serializedData);
  }

  public void setData(String entityName, Long entityId, String key, Map<String, Object> data) {
    Validate.notNull(entityName);
    Validate.notNull(entityId);
    Validate.notNull(key);
    Validate.notNull(data);
    String serializedData = this.xstream.toXML(data);
    this.getPropertySet(entityName, entityId).setText(key, serializedData);
  }

  public List<Object> getListData(String entityName, Long entityId, String key) {
    Validate.notNull(entityName, "entityName must not be null");
    Validate.notNull(entityId, "entityId must not be null");
    Validate.notNull(key, "key must not be null");
    String serializedData = this.getPropertySet(entityName, entityId).getText(key);
    return serializedData == null ? null : (List) this.xstream.fromXML(serializedData);
  }

  public void setListData(String entityName, Long entityId, String key, List<Object> data) {
    Validate.notNull(entityName, "entityName must not be null");
    Validate.notNull(entityId, "entityId must not be null");
    Validate.notNull(key, "key must not be null");
    Validate.notNull(data, "data must not be null");
    String serializedData = this.xstream.toXML(data);
    if (this.log.isDebugEnabled()) {
      this.log.debug(
          "Storing list data in property set: " + entityName + ":" + entityId + " => " + key + ":"
              + serializedData);
    }

    this.getPropertySet(entityName, entityId).setText(key, serializedData);
  }

  public Set<String> getKeys(String entityName, Long entityId) {
    Validate.notNull(entityName);
    Validate.notNull(entityId);
    Collection<String> keys = this.getPropertySet(entityName, entityId).getKeys();
    if (keys.isEmpty()) {
      return Collections.emptySet();
    } else {
      Set<String> keySet = new HashSet();
      Iterator var5 = keys.iterator();

      while (var5.hasNext()) {
        String key = (String) var5.next();
        keySet.add(key);
      }

      return keySet;
    }
  }

  public boolean exists(String entityName, Long entityId, String key) {
    return this.exists(this.getPropertySet(entityName, entityId), key);
  }

  private boolean exists(PropertySet propertySet, String key) {
    return propertySet.exists(key);
  }

  public Object getPropertyAsType(String entityName, Long entityId, String key) {
    PropertySet propertySet = this.getPropertySet(entityName, entityId);
    return !this.exists(propertySet, key) ? null : propertySet.getAsActualType(key);
  }

  public void delete(String entityName, Long entityId, String key) {
    Validate.notNull(entityName);
    Validate.notNull(entityId);
    Validate.notNull(key);

    try {
      this.getPropertySet(entityName, entityId).remove(key);
    } catch (PropertyException var5) {
      this.log.warn(var5, var5);
    }

  }

  public void deleteAll(String entityName, Long entityId) {
    Validate.notNull(entityName);
    Validate.notNull(entityId);

    try {
      this.getPropertySet(entityName, entityId).remove();
    } catch (PropertyException var4) {
      this.log.warn(var4, var4);
    }

  }

  private PropertySet getPropertySet(String entityName, Long entityId) {
    Validate.notNull(entityName);
    Validate.notNull(entityId);
    return this.jiraPropertySetFactory.buildCachingPropertySet(entityName, entityId);
  }
}
