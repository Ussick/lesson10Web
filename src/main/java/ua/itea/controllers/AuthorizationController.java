package ua.itea.controllers;

import ua.itea.daoImpl.Authorizator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class AuthorizationController extends HttpServlet {
    private boolean showAForm = true;
    private boolean isAccessDenied;
    private boolean isBlocked;
    private long timeOfLock;
    private int countLogins;
    private long rez;
    public HttpSession session;

    public Authorizator getAuthorizator() {
        return new Authorizator();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("showAForm", showAForm);
        req.setAttribute("isAccessDenied", isAccessDenied);
        req.setAttribute("isBlocked", isBlocked);
        req.getRequestDispatcher("WEB-INF/views/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        session = req.getSession();
        if (req.getParameter("logout") != null) {
            session.setAttribute("authorized", null);
            isAccessDenied = true;
            showAForm = true;
            isBlocked = false;
        }else{
            String loginFromSession = (String) session.getAttribute("authorized");

            String login = req.getParameter("login");
            String password = req.getParameter("password");

            if ((login == null && password == null)) {
            } else {
                Authorizator auth = getAuthorizator();
                String userName = auth.isAuthorized(login, password);
                if ((userName != null)) {
                    timeOfLock = 0;
                    showAForm = false;
                    isAccessDenied = false;
                    isBlocked = false;
                    session.setAttribute("authorized", userName);
                    loginFromSession = userName;
                    req.setAttribute("result", "Access granted!");
                    req.setAttribute("loginFromSession", loginFromSession);
                } else {
                    isAccessDenied = true;
                    countLogins++;
                    if (countLogins == 3) {
                        timeOfLock = System.currentTimeMillis();
                    }
                    if (countLogins >= 3) {
                        rez = ((timeOfLock + 10000) - System.currentTimeMillis()) / 1000;
                        if (rez > 0) {
                            isBlocked = true;
                            showAForm = false;
                            String block = "You locked for " + rez + " seconds";
                            req.setAttribute("block", block);
                        } else {
                            timeOfLock = 0;
                            countLogins = 0;
                            rez = 0;
                            isBlocked = false;
                            showAForm = true;
                        }
                    }
                    req.setAttribute("accessDenied", "Access denied. countLogins: " + countLogins);
                }
            }
        }
        req.setAttribute("isBlocked", isBlocked);
        req.setAttribute("showAForm", showAForm);
        req.setAttribute("isAccessDenied", isAccessDenied);
        req.getRequestDispatcher("WEB-INF/views/login.jsp").forward(req, resp);
    }
}

