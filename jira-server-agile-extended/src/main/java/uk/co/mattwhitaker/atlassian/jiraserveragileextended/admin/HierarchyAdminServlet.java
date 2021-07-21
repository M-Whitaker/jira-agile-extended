package uk.co.mattwhitaker.atlassian.jiraserveragileextended.admin;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HierarchyAdminServlet extends HttpServlet {

  private static final Logger log = LoggerFactory.getLogger(HierarchyAdminServlet.class);

  private final UserManager userManager;
  private final LoginUriProvider loginUriProvider;
  private final TemplateRenderer renderer;

  @Autowired
  public HierarchyAdminServlet(@ComponentImport UserManager userManager, @ComponentImport LoginUriProvider loginUriProvider,
      @ComponentImport TemplateRenderer renderer) {
    this.userManager = userManager;
    this.loginUriProvider = loginUriProvider;
    this.renderer = renderer;
  }

  /**
   * Renders the velocity template on get request.
   * @param req HTTP request information.
   * @param resp HTTP response information.
   * @throws IOException when render can't find velocity template provided.
   */
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    if (!AdminUtils.checkAuthorized(req, userManager)) {
      AdminUtils.redirectToLogin(req, resp, loginUriProvider);
    }
    Map<String, Object> paramMap = new HashMap<>();
    resp.setContentType("text/html;charset=utf-8");
    renderer.render("/templates/admin/hierarchy.vm", paramMap, resp.getWriter());
  }
}