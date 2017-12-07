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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



/**
 * Servlet Filter implementation class CORS_Filter
 */
public class CORS_Filter implements Filter {

	 private static Log logger = LogFactory.getLog(CORS_Filter.class);
    /**
     * Default constructor. 
     */
    public CORS_Filter() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
			HttpServletRequest httpServletRequest = (HttpServletRequest) request ;
	    HttpServletResponse httpServletResponse = (HttpServletResponse) response;
	    
	    httpServletResponse.addHeader("Access-Control-Allow-Origin","*");
	    httpServletResponse.addHeader("Access-Control-Allow-Methods","GET,POST");
	    httpServletResponse.addHeader("Access-Control-Allow-Headers","Origin, X-Requested-With, Content-Type, Accept, Authorization");
	    	    
	    // Just ACCEPT and REPLY OK if OPTIONS
	    if ( httpServletRequest.getMethod().equals("OPTIONS") ) {
	    	httpServletResponse.setStatus(HttpServletResponse.SC_OK);
	        return;
	    }
		
		// pass the request along the filter chain
		chain.doFilter(request, response);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
