package br.com.bovbi.embed.authenticated;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.util.StringUtils;

import br.com.bovbi.embed.EmbedSecurityConstants;
import br.com.bovbi.embed.Utils;
import br.com.bovbi.embed.logger.EmbedLogger;

public class EmbedAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final static EmbedLogger logger = EmbedLogger.Get(EmbedAuthenticationSuccessHandler.class);

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws ServletException, IOException {

		String targetUrlParameter = getTargetUrlParameter();
		String url = request.getParameter(EmbedSecurityConstants.Parameter.URL);
		clearAuthenticationAttributes(request);
		// test if the parameter url is not empty
		if (!Utils.isNullOrEmpty(url)) 
			targetUrlParameter = url;

		this.logger.debug("Redirecting to URL: " + targetUrlParameter);
		getRedirectStrategy().sendRedirect(request, response, targetUrlParameter);
		
        
	}

}