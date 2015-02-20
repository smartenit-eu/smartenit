/**
 * Copyright (C) 2014 The SmartenIT consortium (http://www.smartenit.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.smartenit.unada.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The UserSessionFilter class. It filters all incoming requests and checks 
 * whether current session is active, otherwise it redirects them to login page.
 * 
 * @author George Petropoulos
 * @version 2.0
 * 
 */
public class UserSessionFilter implements Filter {
	
	private static final Logger logger = LoggerFactory.getLogger(UserSessionFilter.class);

	public void init(FilterConfig filterConfig) throws ServletException {

	}

	/**
	 * The method that executes the session filtering.
	 * If the session is not valid and the request page is not the login one, 
	 * then the user is redirected back to login page.
	 * 
	 * @param servletRequest The servlet request to be filtered.
	 * @param servletResponse The servlet response to be redirected to.
	 * @param filterChain The filter chain.
	 * 
	 * @throws IOException
	 * @throws ServletException
	 */
	public void doFilter(ServletRequest servletRequest,
			ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		
		HttpServletRequest req = (HttpServletRequest) servletRequest;
		HttpServletResponse resp = (HttpServletResponse) servletResponse;
		HttpSession session = req.getSession(false);
		String page = req.getRequestURL().toString();
        if (page.contains("login.xhtml")) {
            filterChain.doFilter(servletRequest, servletResponse);
        }
		else if (session == null && req.getRequestedSessionId() != null
				&& !req.isRequestedSessionIdValid()) {
			logger.info("Current session has expired, redirecting to login page.");
			resp.sendRedirect("login.xhtml");
		} else {
			filterChain.doFilter(servletRequest, servletResponse);
		}
	}

	public void destroy() {

	}
}
