package fivium.pat.servlets;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fivium.pat.utils.PatAuthUtils;
import fivium.pat.utils.PatUtils;

public class SecurityFilter implements Filter {

	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest servletRequest = (HttpServletRequest) request;
		String jwt_Token = servletRequest.getHeader("Authorization");
		if (PatAuthUtils.isValidJWT(jwt_Token, servletRequest.getServletPath())) {
			chain.doFilter(request, response);
		} else {
			PatUtils.set400Reponse((HttpServletResponse) response, "Invalid Request.");
			return;
		}
	}
	public void destroy() {
		// TODO Auto-generated method stub
	}
}
