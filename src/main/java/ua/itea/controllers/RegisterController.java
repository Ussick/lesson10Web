package ua.itea.controllers;

import ua.itea.dao.UserDao;
import ua.itea.daoImpl.DaoFactory;
import ua.itea.model.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import java.util.regex.Pattern;


public class RegisterController extends HttpServlet {
    private boolean showForm = true;
    private boolean isError = false;
    private User user=new User();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.setAttribute("user", user);
        req.setAttribute("isError", isError);
        req.setAttribute("showform", showForm);
        req.getRequestDispatcher("WEB-INF/views/registration.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        isError=false;

        String regLog = "^([a-zA-Z0-9_]{2,}[\\.])*[a-zA-Z0-9_]{2,}[@][a-zA-Z]{2,}[\\.a-zA-Z]{3,}$";
        String reg1 = "^[\\w\\W]{8,}$";
        String reg2 = "^[\\w\\W]*[A-ZА-Я]+[\\w\\W]*$";
        String reg3 = "^[\\w\\W]*[0-9]+[\\w\\W]*$";
        String errorText = "<ul>";


        user.setLogin(req.getParameter("login"));
        user.setName(req.getParameter("name"));
        user.setPassword(req.getParameter("password"));
        user.setPasswordRepeat( req.getParameter("passwordRepeat"));
        user.setGender(req.getParameter("gender"));
        user.setRegion(req.getParameter("region"));
        user.setComment(req.getParameter("comment"));

        String login = user.getLogin();
        String name = user.getName();
        String password = user.getPassword();
        String passwordRepeat = user.getPasswordRepeat();
        String gender = user.getGender();
        String region = user.getRegion();
        String comment = user.getComment();
        String browser = req.getParameter("browser");

        if (login == null || login.isEmpty()) {
            isError = true;
            errorText += "<li>Login is empty!</li>";
        } else {
            if (Pattern.matches(regLog, login)) {
            } else {
                isError = true;
                errorText += "<li>Login is not valid!</li>";
            }
        }

        if (name == null || name.isEmpty()) {
            isError = true;
            errorText += "<li>Name is empty!</li>";
        }

        if (password == null || password.isEmpty()) {
            isError = true;
            errorText += "<li>Password is empty!</li>";
        } else {
            if (Objects.equals(password, passwordRepeat)) {
                if (Pattern.matches(reg1, password) && Pattern.matches(reg2, password) && Pattern.matches(reg3, password)) {
                } else {
                    isError = true;
                    errorText += "<li>Password have to be more then 8 symbols with minimum 1 capital letter and 1 number !</li>";
                }
            } else {
                isError = true;
                errorText += "<li>Password and RepeatPassword are not equal!</li>";
            }
        }

        if (gender == null || gender.isEmpty()) {
            isError = true;
            errorText += "<li>Choose your gender!</li>";
        }

        if (comment == null || comment.isEmpty()) {
            isError = true;
            errorText += "<li>Fill in your comment!</li>";
        }

        if (browser == null || browser.isEmpty()) {
            isError = true;
            errorText += "<li>Amigo Browser has to be chosen!</li>";
        }

        errorText += "</ul>";

        if (!isError) {
            UserDao de = DaoFactory.getInstance().getUserDAO();

            de.addUser(user);
            showForm = false;
            req.setAttribute("showform", showForm);
            req.setAttribute("isError", isError);
            req.setAttribute("result", "Registration succeeded!");
            req.getRequestDispatcher("WEB-INF/views/registration.jsp").forward(req, resp);

            showForm = true;
            isError = false;
        } else {
            req.setAttribute("isError", isError);
            req.setAttribute("result", errorText);
            doGet(req, resp);
        }
    }
}
