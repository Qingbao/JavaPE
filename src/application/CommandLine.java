/*
  JavaPE: a simple Java PE reader
  Copyright (C) 2013 Qingbao Guo <qingbao.guo@hig.no>

  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation; either version 2 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

*/

package application;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author Qingbao.Guo
 * @version Version 1.0
 */
public class CommandLine {
    
    
    /**
     * 
     * Print the command usage
     */
    public static void printUsage() throws IOException, Exception{
        
                    //print usage
                    System.out.println("Usage: open <filename> ");
                    System.out.println("       createXML <filename>");
                    System.out.println("       createPDF <filename>");
                    System.out.println("       createHTML <filename>");
                    System.out.println("       GUI (go to GUI model)");
                    
            while(true){   
                    //get user input
                   Scanner inputReader = new Scanner(System.in);
                   String input = inputReader.nextLine();
                   
                   //go to GUI model
                   if(input.equals("GUI")){
                       break;
                   }else{        
                   
                       String filename[] = input.split(" ");
                       File file = new File(filename[1]);
                   
                    //read file
                   if(file.exists()&&filename[0].equals("open")){
                    ReadPEfile pe = new ReadPEfile(filename[1]);
                    pe.open();
                    pe.getHeader();
                    pe.getOptionalHeater();
                    pe.getSection();
                    pe.getIconInfo();
                    pe.close();
                   }else{System.out.println("No such file");}
                   
                   
                   //Create report
                    if(file.exists()&&filename[0].equals("createXML")){
                        
                        CreateReport cr = new CreateReport();
                        cr.CreateXML(filename[1]);
                        System.out.println("The XML report has created successfully!");
                        
                   }//else{System.out.println("ERROR!");}
                
                    if(file.exists()&&filename[0].equals("createPDF")){
                        
                        CreateReport cr = new CreateReport();
                        cr.CreatePDF(filename[1]);
                        System.out.println("The PDF report has created successfully!");
                   
                   }//else{System.out.println("ERROR!");}
                    
                     if(file.exists()&&filename[0].equals("createHTML")){
                         
                        CreateReport cr = new CreateReport();
                        cr.CreateHTML(filename[1]);
                        System.out.println("The HTML report has created successfully!");
                   
                   }//else{System.out.println("ERROR!");}
                   
                   
               }
          }
    }
    
}
