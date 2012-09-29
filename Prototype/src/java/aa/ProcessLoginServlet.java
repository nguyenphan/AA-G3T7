package aa;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.sql.SQLException;

import Database.TraderDAO;
import Entity.Trader;

public class ProcessLoginServlet extends HttpServlet {

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("id").trim();

        try {
            
            //get trader from database
            TraderDAO traderDAO = new TraderDAO();
            Trader trader = traderDAO.getTraderWithUsername(username);

            //if trader does not exist, create a new one.
            if (trader == null) {
                trader = new Trader(username);
                traderDAO.add(trader);
            }

            //save trader in session
            HttpSession session = request.getSession();
            session.setAttribute("userId", username);
            session.setAttribute("authenticatedUser", true);
            response.sendRedirect("loginSuccess.jsp");
            
        } catch (SQLException e) {

            response.sendRedirect("login.jsp?error=");
            
        }
    }

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
