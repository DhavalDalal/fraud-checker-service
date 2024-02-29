package com.tsys.fraud_checker.web.filters;

import org.springframework.context.annotation.Profile;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

//@Component
@Profile("development")
public class StubResponseFilter  implements Filter {

//    @Autowired
//    private VerificationService verificationService;
    private static final Logger LOG = Logger.getLogger(StubResponseFilter.class.getName());

    public StubResponseFilter() {
        LOG.info("StubResponseFilter.StubResponseFilter");
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOG.info("StubResponseFilter.init");
        LOG.info("filterConfig = " + filterConfig);
        final ServletContext servletContext = filterConfig.getServletContext();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        LOG.info("StubResponseFilter.doFilter");
//        System.out.println("verificationService = " + verificationService);
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        LOG.info(httpRequest.getRequestURI());
        LOG.info("StubResponseFilter.doFilter before chain.doFilter(...)");
        LOG.info("httpResponse.isCommitted() = " + httpResponse.isCommitted());
        chain.doFilter(httpRequest, httpResponse);
        LOG.info("StubResponseFilter.doFilter after chain.doFilter(...)");
        LOG.info("httpResponse.isCommitted() = " + httpResponse.isCommitted());
    }
}
