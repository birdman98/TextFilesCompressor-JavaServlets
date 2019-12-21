package webtextfilescompressor.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import webtextfilescompressor.webmodel.*;

/**
 *
 * @author Piotr Matras
 * @version 1.0
 * Servlet which realizes compressing/decompressing file passed in request arguments
 */
public class ProcessFile extends HttpServlet {
    
    /**
     * mode - mode of file compressor (compress/decompress)
     */
    private String mode = "";
    /**
     * inputFileName - path to file which will be compressed/decompressed
     */
    private String inputFileName = "";
    /**
     * outputFileName - path to File in which compressed/decompressed input file will be saved
     */
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
        
        request.getSession(true);
        response.setContentType("text/html;charset=UTF-8");
        this.mode = request.getParameter("mode");
        this.inputFileName = request.getParameter("inputFile");
        this.outputFileName = request.getParameter("outputFile");
        
        PrintWriter out = response.getWriter();
        
        Cookie[] cookies = request.getCookies();
        String previousMode = "";
        if(cookies != null) {
            for(Cookie cookie : cookies) {
                if(cookie.getName().equals("prevMode")) {
                    previousMode = cookie.getValue();
                    break;
                }                
            }            
        }
                
        if(this.mode.length() == 0 || this.inputFileName.length() == 0 || this.outputFileName.length() == 0) {
            response.sendError(response.SC_BAD_REQUEST, "Wrong parameters passed!");
        } else {
            if(this.mode.equals(Mode.COMPRESS.toString().toLowerCase())) {
                WebFilesCompressor compressor = SingleInstanceOfWebModelGuard.getFilesCompressor(this.inputFileName, "", this.outputFileName);
                
                try {
                     if(compressor.compressFile()) {
                         out.println("<html>\n<body>\n<div>File <b>" + this.inputFileName + "</b> was successfuly compressed into file <b>"
                                 + this.outputFileName + "</b>.</div>\n</body>\n</html>");
                     }               
                } catch(WrongFilePassedException e) {
                       out.println("<html>\n<body>\n<div>File to compress not found: <b>"
                              + e.getMessage() + "</b>.</div>\n</body>\n</html>");                  
                } catch(IOException e) {
                      out.println("<html>\n<body>\n<div>Problem occured while compressing file: <b>"
                              + e.getMessage() + "</b>.</div>\n</body>\n</html>");    
                }               
            } else if(this.mode.equals(Mode.DECOMPRESS.toString().toLowerCase())) {
                WebFilesCompressor decompressor = SingleInstanceOfWebModelGuard.getFilesCompressor("", this.inputFileName, this.outputFileName);
                
                try {
                     if(decompressor.decompressFile()) {
                         out.println("<html>\n<body>\n<div>File <b>" + this.inputFileName + "</b> was successfuly decompressed into file <b>"
                                 + this.outputFileName + "</b>.</div>\n</body>\n</html>");
                     }               
                } catch(WrongFilePassedException e) {
                       out.println("<html>\n<body>\n<div>File to decompress not found: <b>"
                              + e.getMessage() + "</b>.</div>\n</body>\n</html>");                  
                } catch(IOException e) {
                      out.println("<html>\n<body>\n<div>Problem occured while decompressing file: <b>"
                              + e.getMessage() + "</b>.</div>\n</body>\n</html>");    
                }                    
            } else {
                out.println("<html>\n<body>\n<div>Wrong working mode passed! Try again...</div>\n</body>\n</html>");
            }
        }   
        
        if(!previousMode.equals("")) {
            out.println("<html><body><br><br><div> Mode of previous operation request was: <b>" + previousMode + "</b></div></body></html>");            
        }
        
        Cookie cookie = new Cookie("prevMode", this.mode.toLowerCase());
        cookie.setMaxAge(60 * 60);
        response.addCookie(cookie);
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
