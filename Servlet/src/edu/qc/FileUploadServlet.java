//package net.techsuite.SIPPA_HealthTech;
//package com.journaldev.servlet;
package edu.qc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.*;
 
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
 
@WebServlet("/FileUploadServlet")
@MultipartConfig(fileSizeThreshold=1024*1024*10, 	// 10 MB 
                 maxFileSize=1024*1024*50,      	// 50 MB
                 maxRequestSize=1024*1024*100)   	// 100 MB
public class FileUploadServlet extends HttpServlet {
	
	
 
    private static final long serialVersionUID = 205242440643911308L;
	
    /**
     * Directory where uploaded files will be saved, its relative to
     * the web application directory.
     */
    private static final String UPLOAD_DIR = "uploads";
   
     
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        // gets absolute path of the web application
        String applicationPath = request.getServletContext().getRealPath("");
        // constructs path of the directory to save uploaded file
        String uploadFilePath = applicationPath + File.separator + UPLOAD_DIR;
         
        // creates the save directory if it does not exists
        File fileSaveDir = new File(uploadFilePath);
        if (!fileSaveDir.exists()) {
            fileSaveDir.mkdirs();
        }
        
        ora_DBTest_Demo od = new ora_DBTest_Demo();
        Connection connection = null;
       
     
        System.out.println("Upload File Directory="+fileSaveDir.getAbsolutePath());
        
        
        
        String fileName = "";
        //Get all the parts from request and write it to the file on server
        for (Part part : request.getParts()) {
            fileName = getFileName(part);
            part.write(uploadFilePath + File.separator + fileName);
        }
        
        Part filePart = request.getPart("file");
        
        try {
        	connection = od.testconnection_mysql();
        	
        	if(connection != null) {
        		
        		
        		 String query1 = "CREATE TABLE IF NOT EXISTS `files`("
     	    	  		+ "`id` INT NOT NULL AUTO_INCREMENT,"
     	    	  		+ "`file_name` VARCHAR(255) NOT NULL,"
     	    	  		+ "`update_time` VARCHAR(55) NOT NULL,"
     	    	  		+ "`file` LONGBLOB,"
     	    	  		+ "PRIMARY KEY(`id`))";
        		 
        		 String query2 = "INSERT INTO files (file_name, update_time, file) values(?, ?, ?)";
        		 
        		 
        		 Statement stmt = null;
        		 
        		 try {
   	    		  
   	    	      stmt = connection.createStatement();
   	    	      
   	    	      int ct = stmt.executeUpdate(query1);
   	    	      
   	    	      if(ct > 0)
   	    	    	  System.out.println("Table created");
   	    	      	    	      
   	    	      PreparedStatement st = connection.prepareStatement(query2);
   	    	      
   	    	      
   	    	     
   	    	      InputStream fs = filePart.getInputStream();
   	    	      
   	    	      String curTime = ora_DBTest_Demo.currentTime(connection);
   	    	      
   	    	      st.setString(1, fileName);
   	    	      st.setString(2, curTime);
   	    	      
   	    	      
   	    	      
   	    	      if(fs != null)
   	    	    	  st.setBlob(3, fs);
   	    	      else
   	    	    	  fs = null;
   	    	       	    	      
   	    	     
   	    	      int r = st.executeUpdate();
 	    	         	    	      
   	    	      if(r > 0)
   	    	    	  System.out.println("Data updated");
   	    	      else
   	    	    	  System.out.println("Not updated");
   	    	      
   	    	      
   	    	      String query4 = "select max(id) from files";
   	    	      
   	    	      PreparedStatement id = connection.prepareStatement(query4);
   	    	      
   	    	      ResultSet r2 = id.executeQuery();
   	    	      
   	    	      int i = 0;
   	    	      
   	    	      if(r2.next()) {
   	    	    	  i = Integer.parseInt(r2.getString(1));
   	    	      }
   	    	      
   	    	      /**
   	    	       * TO GET THE CONTENT OF THE UPLOADED BLOB FILE, UNCOMMENT THE FOLLOWING PORTION OF CODE
   	    	       */
   	    	     
   	    	     /* String query3 = "SELECT CONVERT(file USING utf8) FROM files where ID = " + i;
      		 
   	    	   	  PreparedStatement back = connection.prepareStatement(query3);
      		 
   	    	   	  ResultSet r1 = back.executeQuery();
      		 
	      		  if(r1.next()) {
	      		 	 String st1 = r1.getString(1);
	      			 System.out.println(st1);
	      		  }*/
   	    	    } catch (SQLException e) {
   	    	      e.printStackTrace();
   	    	    }
        	}
        	
        }catch(Exception e) {
        	System.out.println("Database Not connected");
        }
        
 
        request.setAttribute("message", "File uploaded successfully!");
        getServletContext().getRequestDispatcher("/response.jsp").forward(
                request, response);
        
        //Below is added for parsing EHR
//		DecodeCCDA parsed = new DecodeCCDA(uploadFilePath + File.separator + fileName);

		
//		writeToResponse(response, parsed.getjson());
    }
 
    /**
     * Utility method to get file name from HTTP header content-disposition
     */
    private String getFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        System.out.println("content-disposition header= "+contentDisp);
        String[] tokens = contentDisp.split(";");
        for (String token : tokens) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf("=") + 2, token.length()-1);
            }
        }
        return "";
    }
    
    
	private void writeToResponse(HttpServletResponse resp, String results) throws IOException {
		PrintWriter writer = new PrintWriter(resp.getOutputStream());
		resp.setContentType("text/plain");

		if (results.isEmpty()) {
			writer.write("No results found.");
		} else {
			writer.write(results);
		}
		writer.close();
	}	
	
	
}