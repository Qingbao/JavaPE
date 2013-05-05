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

import PEfile.BinaryInputStream;
import PEfile.IcoCodec.*;
import PEfile.PEFile;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileSystemView;



/**
 *
 * @author Qingbao.Guo
 * @version Version 1.0
 */
public  class ReadPEfile {
    
          private String filename;
          private PEFile peFile;
          private String header;
          private String optional_header;
          private String section;
          private String icon_info;
    
    
  /** Constructor
     * Internal constructor for already-normalized pathname strings.
     * The parameter order is used to disambiguate this method from the
     * public(File, String) constructor.
     */         
        public  ReadPEfile (String filename) {
             this.filename = filename;
     
    }
        /**
         *  open a PE file
         * 
         */
        public void open() throws IOException{
              peFile = new PEFile(new File(filename)); 
              peFile.open();
    }
    
         /**
         *  close a PE file
         * 
         */
        public void close() throws IOException{
              peFile.close();                                   
    }
        
        /**
         * @return header information as string
         * 
         */
        public String getHeader() throws UnsupportedEncodingException{
            
            System.out.println("PE header information:");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(bos);
            peFile.header.dumpHeader(ps);
            header = bos.toString("UTF-8");
            System.out.println(header);
            return header;
        
        }
        
        
        /**
         * @return optional header information as string
         * 
         */
        public String getOptionalHeater() throws UnsupportedEncodingException{
        
            System.out.println("PE optional header information:");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(bos);
            peFile.header.dumpOptionalHeader(ps);
            optional_header = bos.toString("UTF-8");
            System.out.println(optional_header);
            return optional_header;
        
        }
        
        
        /**
         * @return section information as string
         * 
         */
        public String getSection() throws UnsupportedEncodingException{
            
            StringBuilder sb = new StringBuilder();
                  
            sb.append("PE section information: Sections: \n");
                  
              for (int i=0; i<peFile.getSections().size();i++){               
                  sb.append(peFile.getSections().get(i).getName());    
                  sb.append("\n");
              }    
              
                 sb.append("\n");
                 
              for (int i=0; i<peFile.getSections().size();i++){               
                  ByteArrayOutputStream bos = new ByteArrayOutputStream();
                  PrintStream ps = new PrintStream(bos);
                  peFile.getSections().get(i).dumpSectionInfo(ps);
                  sb.append(bos);
              }                
           section = sb.toString();  
           System.out.println(section);
           return section;
        
        }
        
        
        /**
         * @return icon information as string
         * 
         */
        public String getIconInfo() throws IOException{
        
                    InputStream istream = new FileInputStream(filename);
                    BufferedInputStream buffin = new BufferedInputStream(istream);
                    BinaryInputStream in = new BinaryInputStream(buffin);
                    IconDir dir = new IconDir(in);
                    IconEntry entry = new IconEntry(in);
                    IconHeader iconHeader = new IconHeader(in);
                   
                    StringBuilder sb = new StringBuilder();
                    sb.append("Icon header information: \n");
                    sb.append(dir.toString()).append("\n");
                    sb.append(entry.toString()).append("\n");
                    sb.append(iconHeader.toString()).append("\n");
      
                    icon_info = sb.toString();
                    System.out.println(icon_info);
                    return icon_info;
        
        }
        
         /**
         * Load icon from the pe file
         * 
         */
        public void loadIcon() throws FileNotFoundException{
            
             //smaller icon 
             ImageIcon icon = (ImageIcon) FileSystemView.getFileSystemView().getSystemIcon(new File(filename));
        
            //bigger icon (ShellFolder from the OpenJDK, maybe obsoleted in the future)
            //ImageIcon icon = new ImageIcon();
            //ShellFolder shellFolder = ShellFolder.getShellFolder(new File(filename));
            //icon.setImage(shellFolder.getIcon(true));
                 
            javax.swing.JFrame jf = new javax.swing.JFrame("Icon");
            javax.swing.JLabel label = new javax.swing.JLabel(icon);
            
            jf.getContentPane().add(label);
            jf.setLocation(500, 200);
            jf.setSize(150, 150);
            jf.setVisible(true);
        
        }
               
}
