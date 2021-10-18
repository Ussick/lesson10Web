package ua.itea.controllers;

import ua.itea.daoImpl.ProductDbService;
import ua.itea.model.Product;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;


public class CartController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("WEB-INF/views/cart.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String operation = req.getParameter("operation");
        String quantityString = req.getParameter("quantity");
        String productId = req.getParameter("id");
        Integer quantity = Integer.parseInt(quantityString);
        HttpSession session = req.getSession();
        ProductDbService pds = new ProductDbService();
        Product product = pds.getProductById(productId);
        Object productsCartMap = session.getAttribute("productsCartMap");
        if (Objects.equals(operation, "InCart")) {
            if (productId != null && !productId.isEmpty()) {
                if (productsCartMap == null) {
                    productsCartMap = new HashMap<Product, Integer>();
                }
                Integer count = ((HashMap<Product, Integer>) productsCartMap).get(product);
                if (count == null) {
                    ((HashMap<Product, Integer>) productsCartMap).put(product, quantity);
                } else {
                    count = count + quantity;
                    ((HashMap<Product, Integer>) productsCartMap).put(product, count);
                }
            }
        } else {
            if (productId != null && !productId.isEmpty()) {
                if (productsCartMap == null) {
                    resp.sendRedirect("cart");
                } else {
                    if (((HashMap<Product, Integer>) productsCartMap).get(product) == null) {
                        resp.sendRedirect("cart");
                    } else {
                        if (quantity > 0) {
                            ((HashMap<Product, Integer>) productsCartMap).put(product, quantity);
                        } else {
                            ((HashMap<Product, Integer>) productsCartMap).remove(product);
                        }
                    }
                }
            }
        }
        int productsCartMapSize=((HashMap<Product, Integer>) productsCartMap).entrySet()
                .stream().mapToInt(x -> x.getValue()).sum();
        int total=((HashMap<Product, Integer>) productsCartMap).entrySet()
                .stream().mapToInt(x -> x.getKey().getPrice() * x.getValue()).sum();
        session.setAttribute("productsCartMap", productsCartMap);
        session.setAttribute("productsCartMapSize",productsCartMapSize);
        session.setAttribute("total",  total);
        resp.getWriter().write(productsCartMapSize+","+total);
    }
}
