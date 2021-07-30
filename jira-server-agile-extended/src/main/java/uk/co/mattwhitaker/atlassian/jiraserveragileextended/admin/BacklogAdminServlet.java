package uk.co.mattwhitaker.atlassian.jiraserveragileextended.admin;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.mattwhitaker.atlassian.jiraserveragileextended.service.PropertyDao;

@Component
public class BacklogAdminServlet extends HttpServlet {

  private static final Logger log = LoggerFactory.getLogger(HierarchyAdminServlet.class);
  public static final String KEY_DEFAULT_BACKLOG_CLIENT_REFRESH = "JiraAgileExtended.BacklogPositionClientRefresh.Default";

  private final UserManager userManager;
  private final LoginUriProvider loginUriProvider;
  private final TemplateRenderer renderer;
  private final PropertyDao propertyDao;

  @Autowired
  public BacklogAdminServlet(@ComponentImport UserManager userManager,
      @ComponentImport LoginUriProvider loginUriProvider,
      @ComponentImport TemplateRenderer renderer, @Autowired PropertyDao propertyDao) {
    this.userManager = userManager;
    this.loginUriProvider = loginUriProvider;
    this.renderer = renderer;
    this.propertyDao = propertyDao;
  }

  /**
   * Renders the velocity template on get request.
   *
   * @param req  HTTP request information.
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
    Long clientRefresh = propertyDao.getLongProperty(KEY_DEFAULT_BACKLOG_CLIENT_REFRESH);
    paramMap.put("clientRefresh", clientRefresh != null ? clientRefresh : 10);
    resp.setContentType("text/html;charset=utf-8");
    renderer.render("/templates/admin/backlog.vm", paramMap, resp.getWriter());
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    if (!AdminUtils.checkAuthorized(req, userManager)) {
      AdminUtils.redirectToLogin(req, resp, loginUriProvider);
    }

    String clientRefresh = req.getParameter("client-refresh-freq");

    if (clientRefresh != null) {
      propertyDao.setLongProperty(KEY_DEFAULT_BACKLOG_CLIENT_REFRESH, Long.valueOf(clientRefresh));
      resp.sendRedirect(req.getRequestURI());
    } else {
      resp.setStatus(Status.BAD_REQUEST.getStatusCode());
    }
  }
}
