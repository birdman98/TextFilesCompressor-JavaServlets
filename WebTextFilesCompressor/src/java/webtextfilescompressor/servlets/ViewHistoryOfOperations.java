package webtextfilescompressor.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import webtextfilescompressor.webmodel.SingleInstanceOfWebModelGuard;
import webtextfilescompressor.webmodel.WebFilesCompressor;

/**
 *
 * @author Piotr Matras
 * @version 1.0
 */
public class ViewHistoryOfOperations extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.getSession(true);
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        WebFilesCompressor compressor = SingleInstanceOfWebModelGuard.getFilesCompressor("", "", "");
        
        List<List<String>> historyOfOperations = compressor.getHistoryOfOperations();
        
        out.println("<hmtl>\n<body>\n");
        
        int i = 1;
        int index = 0;
        
        out.println("<h3> History of operations:</h3>");
        if(historyOfOperations.size() == 0) {
            out.println("<br><br><b> No data of previous operations!</b>");
        }
                
        for(List<String> history : historyOfOperations) {
            out.println("<div><b>" + i + ":</b></div>");
            out.println("<div><b> mode:</b> " + history.get(index) + "</div>");
            out.println("<div><b> input file:</b> " + history.get(++index) + "</div>");
            out.println("<div><b> output file:</b> " + history.get(++index) + "</div>");
            out.println("<br><br>");
            
            ++i;
            index = 0;
        }
        out.println("</body>\n</html>");
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
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

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "History of operations from WebFilesCompressor";
    }

}
