package ua.itea.controllers;

import ua.itea.daoImpl.ProductDbService;
import ua.itea.model.Product;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class ProductController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String categoryId = req.getParameter("category");
        String productId=req.getParameter("id");
        RequestDispatcher rd;
        ProductDbService pds=new ProductDbService();
        if (productId!=null&&!productId.isEmpty()){
            rd=req.getRequestDispatcher("WEB-INF/views/product.jsp");
            Product product= pds.getProductById(productId);
            req.setAttribute("product", product);
        }else {
            rd=req.getRequestDispatcher("WEB-INF/views/products.jsp");
            List<Product> products = pds.getProductsByCategoryId(categoryId);
            req.setAttribute("productList", products);
        }
        rd.forward(req, resp);
    }
}
