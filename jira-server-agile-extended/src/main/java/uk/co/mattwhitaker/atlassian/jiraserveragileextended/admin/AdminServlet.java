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
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AdminServlet extends HttpServlet {

  private static final Logger log = LoggerFactory.getLogger(AdminServlet.class);

  private final UserManager userManager;
  private final LoginUriProvider loginUriProvider;
  private final TemplateRenderer renderer;

  @Autowired
  public AdminServlet(@ComponentImport UserManager userManager, @ComponentImport LoginUriProvider loginUriProvider,
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
    UserKey userKey = userManager.getRemoteUserKey(req);
    if (userKey == null || !userManager.isSystemAdmin(userKey)) {
      redirectToLogin(req, resp);
    }
    Map<String, Object> paramMap = new HashMap<>();
    resp.setContentType("text/html;charset=utf-8");
    renderer.render("/templates/admin.vm", paramMap, resp.getWriter());
  }

  private void redirectToLogin(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    response.sendRedirect(loginUriProvider.getLoginUri(getUri(request)).toASCIIString());
  }

  private URI getUri(HttpServletRequest request) {
    StringBuffer builder = request.getRequestURL();
    if (request.getQueryString() != null) {
      builder.append("?");
      builder.append(request.getQueryString());
    }
    return URI.create(builder.toString());
  }

}