package webtextfilescompressor.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import webtextfilescompressor.webmodel.*;

/**
 *
 * @author Piotr Matras
 * @version 1.0
 */
public class ProcessFile extends HttpServlet {
    
    private String mode = "";
    private String inputFileName = "";
    private String outputFileName = "";

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
        
        response.setContentType("text/html;charset=UTF-8");
        this.mode = request.getParameter("mode");
        this.inputFileName = request.getParameter("inputFile");
        this.outputFileName = request.getParameter("outputFile");
        
        PrintWriter out = response.getWriter();
                
        if(this.mode.length() == 0 || this.inputFileName.length() == 0 || this.outputFileName.length() == 0) {
            response.sendError(response.SC_BAD_REQUEST, "Wrong parameters passed!");
        } else {
            if(this.mode.equals(Mode.COMPRESS.toString().toLowerCase())) {
                WebFilesCompressor compressor = SingleInstanceOfWebModelGuard.getFilesCompressor(this.inputFileName, "", this.outputFileName);
                
                try {
                     if(compressor.compressFile()) {
                         out.println("<html>\n<body>\n<div>File <b>" + this.inputFileName + "</b> was successfuly compressed into file <b>"
                                 + this.outputFileName + "</b>.</div>\n");
                     }               
                } catch(WrongFilePassedException e) {
                       out.println("<html>\n<body>\n<div>File to compress not found: <b>"
                              + e.getMessage() + "</b>.</div>\n");                  
                } catch(IOException e) {
                      out.println("<html>\n<body>\n<div>Problem occured while compressing file: <b>"
                              + e.getMessage() + "</b>.</div>\n");    
                }               
            } 
        }        
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
        return "WebTextFilesCompressor";
    }

}
