package ua.itea.daoImpl;

public class Authorizator {
    public String isAuthorized(String login, String password) {
        DbExecutor de = new DbExecutor();
        return de.checkLogin(login, password);
    }
}
