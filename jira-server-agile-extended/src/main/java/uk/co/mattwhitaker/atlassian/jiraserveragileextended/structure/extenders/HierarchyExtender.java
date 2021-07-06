package uk.co.mattwhitaker.atlassian.jiraserveragileextended.structure.extenders;

import com.almworks.jira.structure.api.forest.item.ItemForestBuilder;
import com.almworks.jira.structure.api.generator.ActionEffect;
import com.almworks.jira.structure.api.generator.ActionHandler;
import com.almworks.jira.structure.api.generator.ActionHandler.ExtenderActionHandler;
import com.almworks.jira.structure.api.generator.GeneratorUnavailableException;
import com.almworks.jira.structure.api.generator.StructurePosition;
import com.almworks.jira.structure.api.generator.util.AbstractGenerator;
import com.almworks.jira.structure.api.generator.util.BasicItemChangeFilter;
import com.almworks.jira.structure.api.generator.util.RecordingItemChangeFilter;
import com.almworks.jira.structure.api.item.CoreIdentities;
import com.almworks.jira.structure.api.row.StructureRow;
import com.almworks.jira.structure.api.util.StructureUtil;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.link.IssueLinkType;
import com.atlassian.jira.issue.link.IssueLinkTypeManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.util.ErrorCollection;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.service.HierarchyLinkManager;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.issuelink.HierarchyIssueLinkType;

public class HierarchyExtender extends AbstractGenerator.Extender {
  private static final String ISSUE_LINK_PARAMETER = "hierarchyLinkTypes";

  private final HierarchyLinkManager hierarchyLinkManager;
  private final JiraAuthenticationContext jiraAuthenticationContext;
  private final IssueLinkTypeManager issueLinkTypeManager;

  public HierarchyExtender(
      @Autowired HierarchyLinkManager hierarchyLinkManager,
      @ComponentImport JiraAuthenticationContext jiraAuthenticationContext,
      @ComponentImport IssueLinkTypeManager issueLinkTypeManager) {
    this.hierarchyLinkManager = hierarchyLinkManager;
    this.jiraAuthenticationContext = jiraAuthenticationContext;
    this.issueLinkTypeManager = issueLinkTypeManager;
  }

  @Nullable
  @Override
  public ExtenderFunction getExtenderFunction(
      @Nonnull Map<String, Object> map, @Nonnull GenerationContext generationContext) {

    String linkType = StructureUtil.getSingleParameter(map, ISSUE_LINK_PARAMETER);

    RecordingItemChangeFilter itemChangeFilter = BasicItemChangeFilter.createRecording();
    generationContext.addItemChangeFilter(itemChangeFilter);


    // And this is the main function that does the generation.
    return new HierarchyExtenderFunction(itemChangeFilter, linkType);
  }

  @Nullable
  @Override
  public ExtenderActionHandler createActionHandler(@NotNull Map<String, Object> parameters) {
    String linkType = StructureUtil.getSingleParameter(parameters, ISSUE_LINK_PARAMETER);
    return new HierarchyExtenderActionHandler(linkType);
  }

  @Override
  public void addDefaultFormParameters(@NotNull Map<String, Object> form)
      throws GeneratorUnavailableException {
    List<IssueLinkType> issueLinkTypes = new ArrayList<>(
        issueLinkTypeManager.getIssueLinkTypesByStyle(HierarchyIssueLinkType.LINK_STYLE));
    form.put(ISSUE_LINK_PARAMETER, issueLinkTypes);
  }

  @Override
  public void addParametersToForm(
      @NotNull Map<String, Object> parameters, @NotNull Map<String, Object> form)
      throws GeneratorUnavailableException {
    List<IssueLinkType> issueLinkTypes = new ArrayList<>(
        issueLinkTypeManager.getIssueLinkTypesByStyle(HierarchyIssueLinkType.LINK_STYLE));
    form.put(ISSUE_LINK_PARAMETER, issueLinkTypes);
  }

  @NotNull
  @Override
  public Map<String, Object> buildParametersFromForm(
      @NotNull Map<String, Object> form, @NotNull ErrorCollection errors)
      throws GeneratorUnavailableException {
    return ImmutableMap.of(ISSUE_LINK_PARAMETER,
        Objects.requireNonNull(StructureUtil.getSingleParameter(form, ISSUE_LINK_PARAMETER)));
  }

  private class HierarchyExtenderActionHandler implements ActionHandler.ExtenderActionHandler {

    private final String linkType;

    public HierarchyExtenderActionHandler(String linkType) {
      this.linkType = linkType;
    }

    @Override
    public void moveExtension(
        @Nonnull StructureRow structureRow,
        @Nullable StructurePosition structurePosition,
        @Nullable StructurePosition structurePosition1,
        @Nonnull HandlingContext handlingContext) {
    }

    @Override
    public void reorderExtension(
        @Nonnull StructureRow structureRow,
        @Nonnull StructurePosition structurePosition,
        @Nonnull StructurePosition structurePosition1,
        @Nonnull HandlingContext handlingContext) {
    }

    @Nonnull
    @Override
    public String getAddOptionDescription() {
      return "getAddOptionDescription";
    }

    @Nonnull
    @Override
    public String getMoveOptionDescription() {
      return "getMoveOptionDescription";
    }

    @Nonnull
    @Override
    public String getReorderOptionDescription() {
      return "getReorderOptionDescription";
    }
  }

  private class ChangeHierarchyEffect implements ActionEffect {

    @Override
    public void apply(EffectContext context) {

    }
  }

  private class HierarchyExtenderFunction implements ExtenderFunction {

    private final RecordingItemChangeFilter recordingItemChangeFilter;
    private final String linkType;

    public HierarchyExtenderFunction(RecordingItemChangeFilter recordingItemChangeFilter, String linkType) {
      this.recordingItemChangeFilter = recordingItemChangeFilter;
      this.linkType = linkType;
    }

    @Override
    public void extend(
        @Nonnull StructureRow structureRow, @Nonnull ItemForestBuilder itemForestBuilder) {
      Issue issue = structureRow.getItem(Issue.class);
      if (issue != null) {
        recordingItemChangeFilter.recordItem(structureRow);
        // TODO: Add selection of link types
        System.out.println("Getting links for: " + linkType);
        List<Issue> childIssues =
            hierarchyLinkManager.getIssueLinksForIssue(
                issue,
                false,
                HierarchyLinkManager.LinkDirection.INWARD,
                jiraAuthenticationContext.getLoggedInUser());

        for (Issue childIssue : childIssues) {
          itemForestBuilder.nextRow(CoreIdentities.issue(childIssue.getId()));
        }
      }
    }

    @Override
    public boolean isApplicableTo(StructureRow structureRow) {
      return CoreIdentities.isIssue(structureRow.getItemId())
          && structureRow.getItem(Issue.class) != null;
    }
  }
}
