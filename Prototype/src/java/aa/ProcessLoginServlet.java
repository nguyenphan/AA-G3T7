package aa;

import Database.ConnectionFactory;
import Database.TraderDAO;
import Entity.Trader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ProcessLoginServlet extends HttpServlet {

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException{

        String username = request.getParameter("id").trim();

        Connection conn = null;
        
        try {
            
            conn = ConnectionFactory.getInstance().getConnection();
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);  //enable range locks
            conn.setAutoCommit(false);
            
            //get trader from database
            TraderDAO traderDAO = new TraderDAO();
            Trader trader = traderDAO.getTraderWithUsername(conn, username);    //note that trader, whether existant or not, will be locked
            conn.commit();  //release lock, otherwise existant trader cannot be updated later, like for updating credits
            
            //if trader does not exist, create a new one.
            if (trader == null) {
                
                trader = new Trader(username);
                traderDAO.getTraderWithUsername(conn, username);    //select again to get range lock
                traderDAO.add(conn,trader); //add
                conn.commit();  //release lock
                
            }
            
            //save trader in session
            HttpSession session = request.getSession();
            session.setAttribute("userId", username);
            session.setAttribute("authenticatedUser", true);
            response.sendRedirect("loginSuccess.jsp");
            
            
        } catch (SQLException e) {
            
            //rollback and display error
            e.printStackTrace();
            if (conn!=null) conn.rollback();
            System.err.print("Transaction is rolled back.");
            response.sendRedirect("login.jsp?error=");
            
        }finally{
            
            //release connection
            if(conn!=null) conn.close();
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
       
        try{
            processRequest(request, response);
        }catch(SQLException e){
            //TODO: Do something
        }
    }
}
