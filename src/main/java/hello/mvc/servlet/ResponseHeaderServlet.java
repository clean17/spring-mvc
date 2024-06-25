package hello.mvc.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name ="responseHeaderServlet", urlPatterns = "/response-header")
public class ResponseHeaderServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

        resp.setHeader("Content-Type", "text/plain;charset=utf-8");
        resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        resp.setHeader("Pragma", "no-cache");
        resp.setHeader("custom-header", "servlet");

        PrintWriter writer = resp.getWriter();
        writer.println("ok");

        redirect(resp);
    }

    /**
     * 자바는 객체타입을 파라미터로 넣으면 복사한 객체를 넣는다
     * 이때 참조값을 복사한 객체를 변경하면 기존 객체도 변경된다
     * 객체의 참조는 call by value로 전달되지만 참조를 통한 객체는 동일하므로 원본이 변경된다
     * 반면에 primative 타입은 지역변수로 사용된다
     * @param response
     */
    private void cenntent(HttpServletResponse response) {
        response.setHeader("Content-Type", "text/plain;charset=utf-8");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("custom-header", "servlet");
    }

    private void cookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("myCookie", "good");
        cookie.setMaxAge(600); // 600초
        response.addCookie(cookie);
    }

    private void redirect(HttpServletResponse response) throws IOException {
        response.sendRedirect("/basic.html");
    }
}
