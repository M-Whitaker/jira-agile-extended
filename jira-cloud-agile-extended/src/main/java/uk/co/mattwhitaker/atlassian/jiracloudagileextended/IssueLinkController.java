package uk.co.mattwhitaker.atlassian.jiracloudagileextended;

import com.atlassian.connect.spring.AtlassianHostRestClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import uk.co.mattwhitaker.atlassian.jiracloudagileextended.model.Issue;
import uk.co.mattwhitaker.atlassian.jiracloudagileextended.model.IssueLinkEvent;

@RestController
public class IssueLinkController {

  private final AtlassianHostRestClients atlassianHostRestClients;

  @Autowired
  public IssueLinkController(AtlassianHostRestClients atlassianHostRestClients) {
    this.atlassianHostRestClients = atlassianHostRestClients;
  }

  private Issue getIssue(long issueId) {
    RestTemplate restTemplate = atlassianHostRestClients.authenticatedAsAddon();
    ResponseEntity<Issue> response =
            restTemplate.getForEntity("/rest/agile/1.0/issue/" + issueId, Issue.class);
    System.out.format("status code: %d\n", response.getStatusCode().value());
    System.out.println(response.getStatusCode());
    return response.getBody();
  }

  @PostMapping("/create_link")
  void create(@RequestBody IssueLinkEvent issueLinkEvent) {
      System.out.format("Creating link for issue: %s\n", issueLinkEvent.getIssueLink().getSourceIssueId());
      System.out.println(getIssue(issueLinkEvent.getIssueLink().getSourceIssueId()));
  }

  @PostMapping("/delete_link")
  void delete(@RequestBody IssueLinkEvent issueLinkEvent) {
    System.out.format("Deleting link for issue: %s\n", issueLinkEvent.getIssueLink().getSourceIssueId());
    System.out.println(getIssue(issueLinkEvent.getIssueLink().getSourceIssueId()));
  }
}
