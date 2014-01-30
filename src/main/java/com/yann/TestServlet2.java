package com.yann;

import javax.servlet.*;
import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = "/upload2", asyncSupported = true)
@WebListener
public class TestServlet2 extends HttpServlet implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

    @Override
    protected void service(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws ServletException, IOException {
        System.out.println("start service 2");

        AsyncContext asyncContext = servletRequest.startAsync();
        asyncContext.setTimeout(-1);

        ServletInputStream inputStream = servletRequest.getInputStream();
        final ReadListenerImpl2 readListener = new ReadListenerImpl2(inputStream, asyncContext);
        inputStream.setReadListener(readListener);

        AsyncListener asyncListener = new AsyncListener() {
            @Override
            public void onStartAsync(AsyncEvent event) throws IOException {
                System.out.println("start async");
            }

            @Override
            public void onComplete(AsyncEvent event) throws IOException {
                event.getSuppliedResponse().getOutputStream().print("Complete. Read " + readListener.getRead());
            }

            @Override
            public void onTimeout(AsyncEvent event) throws IOException {
                System.out.println(event.getThrowable());
            }

            @Override
            public void onError(AsyncEvent event) throws IOException {
                System.out.println(event.getThrowable());
            }
        };
        asyncContext.addListener(asyncListener);
    }
}
