package com.tsys.fraud_checker.web.interceptors;

import org.springframework.context.annotation.Profile;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * {@link HandlerMapping}  maps a method to a URL, so that the DispatcherServlet
 * will be able to invoke it when processing a request.
 * <p>
 * {@link DispatcherServlet} uses the HandlerAdapter to actually invoke the method.
 * <p>
 * This is where the handler interceptor comes in. We use the {@link HandlerInterceptor}
 * to perform actions before handling, after handling, or after completion
 * (when the view is rendered), of a request.
 * <p>
 * Requests can be intercepted for several reasons for Logging, filtering requests
 * on per country basis or optimizing image files before they are permanently saved,
 * log the origin of the request, modify the header file and append it into it the
 * desired things, or for changing globally used parameters in Spring model etc...
 * interceptors are needed.  Thus they can be used for cross-cutting concerns and
 * to avoid repetitive handler code.
 */

@Profile("development")
public class FraudInterceptor implements HandlerInterceptor {
    private static final Logger LOG = Logger.getLogger(FraudInterceptor.class.getName());

    public FraudInterceptor() {
        LOG.info("Creating FraudInterceptor...");
    }

    // called before the actual handler is executed, but the view is not generated yet
    // If returns true, the control would be passed to the next handler or
    // next designated interceptor (i.e. postHandle)
    // If returns false, then it is assumed that no further handling is needed
    // and written object is responded to the client (postHandle and
    // afterCompletion are not invoked).
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        LOG.info(() -> "FraudCheckerControllerInterceptor.preHandle");
        LOG.info(() -> "request = " + request);
        LOG.info(() -> "response = " + response);
        LOG.info(() -> "handler = " + handler);
        LOG.info(() -> "handler.getClass() = " + handler.getClass());
        LOG.info("response.isCommitted() = " + response.isCommitted());
        return true;
    }

    // called just before data is rendered to the client.
    // You can add headers to the response object.
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        LOG.info("FraudCheckerControllerInterceptor.postHandle");
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        LOG.info("modelAndView = " + modelAndView);
        LOG.info("response.isCommitted() = " + response.isCommitted());
    }

    // called after the complete request has finished and view was generated.
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        LOG.info("FraudCheckerControllerInterceptor.afterCompletion");
        LOG.info("response.isCommitted() = " + response.isCommitted());
        LOG.info("ex = " + ex);
    }
}
