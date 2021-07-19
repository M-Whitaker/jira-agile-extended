package uk.co.mattwhitaker.atlassian.jiraserveragileextended.admin;

import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import java.io.IOException;
import java.net.URI;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AdminUtils {

  /**
   * Check if user is a system admin.
   * @param request HTTP request.
   * @return true if user is authorized else false
   */
  public static Boolean checkAuthorized(HttpServletRequest request, UserManager userManager) {
    UserKey userKey = userManager.getRemoteUserKey(request);
    if (userKey == null || !userManager.isSystemAdmin(userKey)) {
      return false;
    } else {
      return true;
    }
  }

  public static void redirectToLogin(HttpServletRequest request, HttpServletResponse response, LoginUriProvider loginUriProvider)
      throws IOException {
    response.sendRedirect(loginUriProvider.getLoginUri(getUri(request)).toASCIIString());
  }

  public static URI getUri(HttpServletRequest request) {
    StringBuffer builder = request.getRequestURL();
    if (request.getQueryString() != null) {
      builder.append("?");
      builder.append(request.getQueryString());
    }
    return URI.create(builder.toString());
  }

}
