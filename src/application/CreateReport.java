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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;



/**
 *
 * @author Qingbao.Guo
 * @version 1.0
 */
public class CreateReport {
    
    
    
     public CreateReport(){}
     
     
 /**
 * Create a XML file of the result
 */
    public void CreateXML(String filename) throws Exception{
                
             // Create a empty XML document
            Document doc ;
            DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbfactory.newDocumentBuilder();
            doc = docBuilder.newDocument();           
            
            // Create the XML tree with a root element 
            Element root = doc.createElement("Report");          
            doc.appendChild(root);           
            //
            //create a comment and put it in the root element
            Comment comment = doc.createComment("This is a XML just for saving the PE informantin");
            root.appendChild(comment);
            
            
            ReadPEfile pe = new ReadPEfile(filename);
            pe.open();
            String h = pe.getHeader();
            String o = pe.getOptionalHeater();
            String s = pe.getSection();
            String i = pe.getIconInfo();
            pe.close();
           
            //
            // Create child elements and add to xml tree
            Element fstChild = doc.createElement("Filename");
            root.appendChild(fstChild);     
            Text date = doc.createTextNode(filename + "  "+new java.util.Date().toString());
            fstChild.appendChild(date);
            
            Element secChild = doc.createElement("HeaderInformation");
            root.appendChild(secChild);
            Text header = doc.createTextNode(h);
            secChild.appendChild(header);
            
            Element thirdChild = doc.createElement("OptionalHeaderInformation");
            root.appendChild(thirdChild);
            Text optional = doc.createTextNode(o);
            thirdChild.appendChild(optional);
            
            Element fourChild = doc.createElement("SectionHeaderInformation");
            root.appendChild(fourChild);
            Text section = doc.createTextNode(s);
            fourChild.appendChild(section);
            
            Element fiveChild = doc.createElement("IconHeaderInformation");
            root.appendChild(fiveChild);
            Text icon = doc.createTextNode(i);
            fiveChild.appendChild(icon);
                    
            
            //
            // Display the XMl tree with the use of a transformer
            TransformerFactory transfac = TransformerFactory.newInstance();
            Transformer trans = transfac.newTransformer();
           
            trans.setOutputProperty(OutputKeys.METHOD, "XML");
            trans.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            trans.setOutputProperty(OutputKeys.INDENT, "YES");
       
             //
            // Prepare the DOM document for writing
            DOMSource source = new DOMSource(doc);
          
               
            // Prepare the output file 
            File file = new File(filename+".xml");
            Result fileResult = new StreamResult(file);                  
            
            // Write the DOM document tree to file    
            trans.transform(source, fileResult);
                    
           }
                
    
/**
 * Create a PDF file of the result
 */
    public void CreatePDF(String filename) throws Exception {
       
    
    }
    
    
 /**
 * Create a HTML file of the result
 */ 
    public void CreateHTML(String filename) throws Exception{
        
            ReadPEfile pe = new ReadPEfile(filename);
            pe.open();
            String h = pe.getHeader();
            String o = pe.getOptionalHeater();
            String s = pe.getSection();
            String i = pe.getIconInfo();
            pe.close();
        
        FileWriter fWriter = null;
        BufferedWriter writer = null;
        try {
            fWriter = new FileWriter(filename+".html");
            writer = new BufferedWriter(fWriter);
            writer.write("<h1>Report</h1>");
            writer.newLine();
            writer.write("<p>Filename: "+filename+"</p>");
            writer.newLine();
            writer.write("<p>Header information: "+h+"</p>");
            writer.newLine();
            writer.write("<p>Optional header information: "+o+"</p>");
            writer.newLine();
            writer.write("<p>Section header information: "+s+"</p>");
            writer.newLine();
            writer.write("<p>Icon header information: "+i+"</p>");
            writer.newLine();
            
            writer.close(); 
        } catch (Exception e) {
          //catch any exceptions here
        }
   
    
    }
    
    
    
  
    
}