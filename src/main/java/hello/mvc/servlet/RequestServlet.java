package hello.mvc.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "requsetServlet", urlPatterns = "/requestHeader")
public class RequestServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        printHeader(req);
    }

    private void printHeader(HttpServletRequest req) {
        req.getHeaderNames().asIterator().forEachRemaining(headerName -> System.out.println("headerName = " + headerName));
        /**
         * headerName = host
         * headerName = connection
         * headerName = sec-ch-ua
         * headerName = sec-ch-ua-mobile
         * headerName = sec-ch-ua-platform
         * headerName = upgrade-insecure-requests
         * headerName = user-agent
         * headerName = accept
         * headerName = sec-fetch-site
         * headerName = sec-fetch-mode
         * headerName = sec-fetch-user
         * headerName = sec-fetch-dest
         * headerName = accept-encoding
         * headerName = accept-language
         * headerName = cookie
         */
    }
}
