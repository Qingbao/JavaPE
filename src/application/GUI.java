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

import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import javax.swing.*;

/**
 *
 * @author Qingbao Guo
 * @version Version 1.0
 */

public class GUI  extends JFrame implements ActionListener,MouseListener,ItemListener{ 
      
      private JMenuBar menubar;
      private JMenu filemenu,helpmenu;
      private JMenuItem exitItem,aboutItem,openItem; 
      private FileDialog openFileDialog;
      
      //TabbedPanes
      private JTabbedPane tabbedPane;
      private JComponent panel1,panel2,panel3,panel4,panel5;
      
      //Icons
      private  ImageIcon icon_header,icon_optional,icon_section,icon_showicon,icon_report,
                                 icon_right,icon_warning,icon_unknow;
      
      //image icon from a PE file
      private  ImageIcon image_icon;
     
      //Text area show PE file info
      private JTextArea header,optional_header,pe_section,show_icon_info;
      
      //Check box
      private JCheckBox makePDF,makeXML,makeHTML;
     
      //Button
      private JButton create;
      
      //opened file name
      private String file;
      
      //check box index
      private int index = 0;
      
      /**
       * Create the GUI
       */
     public GUI(){
           super("Java PE reader");
         try{     
         //add menu bar    
         menubar = new JMenuBar();
         filemenu = new JMenu("File");
         helpmenu = new JMenu("Help");  
         exitItem = new JMenuItem("Exit"); 
         openItem= new JMenuItem("Open file"); 
         aboutItem = new JMenuItem("About..");
         
        
         //add listener
         exitItem.addActionListener(this);
         aboutItem.addActionListener(this);
         openItem.addActionListener(this);
         // add to layout        
         filemenu.add(openItem);
         filemenu.add(exitItem);   
         helpmenu.add(aboutItem);
         menubar.add(filemenu);
         menubar.add(helpmenu);
         openFileDialog = new FileDialog(this,"Open File",FileDialog.LOAD);
          //setttings
         this.setJMenuBar(menubar);
         this.setFont(new Font("Times New Roman",Font.PLAIN,12));      
      
         
         //icons
          icon_header = createImageIcon("images/home.png");
          icon_optional = createImageIcon("images/info.png");
          icon_section = createImageIcon("images/section.png");
          icon_showicon = createImageIcon("images/showicon.png");
          icon_report = createImageIcon("images/report.png");
          image_icon = createImageIcon("images/no.png");
          
          icon_right = createImageIcon("images/check.png");
          icon_warning = createImageIcon("images/warning.png");
          icon_unknow = createImageIcon("images/unknow.png");
          
          //set text area with edit disable
           header = new JTextArea();
           optional_header = new JTextArea();
           pe_section= new JTextArea();
           show_icon_info= new JTextArea();
           
           header.setEditable(false);
           optional_header.setEditable(false);
           pe_section.setEditable(false);
           show_icon_info.setEditable(false);
           
           //set font
           header.setFont(new Font("Times New Roman",Font.BOLD,14));
           optional_header.setFont(new Font("Times New Roman",Font.BOLD,14));
           pe_section.setFont(new Font("Times New Roman",Font.BOLD,14));
           show_icon_info.setFont(new Font("Times New Roman",Font.BOLD,14));
           
           //initial checkbox and button
            makePDF= new JCheckBox("Create PDF file");
            makePDF.setMnemonic(KeyEvent.VK_C);
            makePDF.setSelected(false);
            makePDF.addItemListener(this);
                   
            makeXML= new JCheckBox("Create XML file");
            makeXML.setMnemonic(KeyEvent.VK_C);
            makeXML.setSelected(false);
            makeXML.addItemListener(this);
            
            makeHTML= new JCheckBox("Create HTML file");
            makeHTML.setMnemonic(KeyEvent.VK_C);
            makeHTML.setSelected(false);
            makeHTML.addItemListener(this);
            
            create = new JButton("Create");
            create.addMouseListener(this);
           
            //add components to tab panels
            panel1= makeTextPanel1("PE header information");
            panel2= makeTextPanel2("PE optional header information");
            panel3= makeTextPanel3("PE section information");
            panel4= makeIconPanel("PE icon information");
            panel5= makeReportPanel("Choose the format of the report file");
       
            //Tabbedpanes
            tabbedPane = new JTabbedPane();
               
            tabbedPane.addTab("PE header", icon_header, panel1, "");
            tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
            
            tabbedPane.addTab("Optional header", icon_optional, panel2, "");
            tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
          
            tabbedPane.addTab("PE section", icon_section, panel3,"");
            tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);
        
            tabbedPane.addTab("Icon", icon_showicon, panel4, "");
            tabbedPane.setMnemonicAt(3, KeyEvent.VK_4);
        
            tabbedPane.addTab("Creat report", icon_report, panel5, "");
            tabbedPane.setMnemonicAt(4, KeyEvent.VK_5);
        
            add(tabbedPane);
            tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
         
         }catch(Exception e){}
     }

    
   
     /** Listens to the menu item events. */
    @Override
    public void actionPerformed(ActionEvent e) {
          JMenuItem menuItem = (JMenuItem)e.getSource();
          if(menuItem == exitItem){
                System.exit(0);
          }
          else if(menuItem == aboutItem){
               JOptionPane.showMessageDialog(this, "Java PE reader v1.0.");
          }
          else if(menuItem == openItem){
              openFileDialog.setVisible(true);
              String filename = openFileDialog.getDirectory() + openFileDialog.getFile();
              if(filename != null){
                 //Reset the screen
                  clear();
                  file = openFileDialog.getFile();
                  //Append the infromation of PE on lables
                  try{
                        ReadPEfile pe = new ReadPEfile(filename);
                        pe.open();
                        
                        String h = pe.getHeader();
                        header.setText(h);
                        
                        String o = pe.getOptionalHeater();
                        optional_header.setText(o);
                        
                        String s = pe.getSection();
                        pe_section.setText(s);
                        
                        String i = pe.getIconInfo();
                        show_icon_info.setText(i);
                        
                        pe.loadIcon();
                               
                        pe.close();
                       
        }
            catch(IOException ex){
                    JOptionPane.showMessageDialog(this, "Error for opening the file!");
            }
              }
          }
           
    }

    
     /** Listens to the mouse clieck events. */
    @Override
    public void mouseClicked(MouseEvent e) {
          
            CreateReport cr = new CreateReport();
        
            if(index==1){try {
                cr.CreatePDF(file);
                System.out.println("The PDF report has created successfully!");
            } catch (Exception ex) {}
        }
            
            else if (index ==2){
            try {
                cr.CreateXML(file);
                System.out.println("The XML report has created successfully!");
            } catch (Exception ex) {}
        } 
            
            else if (index ==3){
            try {
                cr.CreateHTML(file);
                System.out.println("The HTML report has created successfully!");   
            } catch (Exception ex){}
         }
            
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        
    }

    @Override
    public void mouseReleased(MouseEvent e) {
      
    }

    @Override
    public void mouseEntered(MouseEvent e) {
       
    }

    @Override
    public void mouseExited(MouseEvent e) {
       
    }
   
    
    /**read a file from hardisk
     * 
     */
   private void readFile(String filename) {
             
//              PEFile pefile = new PEFile(new File(f)); 
//              File outfile = new File(f+".log");
//              PrintStream out = new PrintStream(outfile);
//              pefile.open();         
//              pefile.header.dump(out);
//              
//               FileReader readIn = new FileReader(outfile);
//               int size = (int)outfile.length();
//               int charsRead = 0;
//               char[]  content = new char[size];
//               while(readIn.ready()){
//                   charsRead += readIn.read(content,charsRead,size-charsRead);
//                   header.setText(new String(content,0,charsRead));
//               }
   }
   
   /**clear the lables
     * 
     */
   private void clear(){
       header.setText("");
       optional_header.setText("");
       pe_section.setText("");
       show_icon_info.setText("");   
   }
   

    
     /** 
    * @return 
    * @param path of the image file
    * Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = JavaPE.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
    
    
    /** 
     * @return 
     * @param text
     * Returns a JPanel for showing the header information, that contains some sub components. */
    protected JComponent makeTextPanel1(String text) {
        JPanel panel = new JPanel();
        JLabel filler = new JLabel(text); 
        panel.setLayout(new BorderLayout());
        panel.add(filler,BorderLayout.NORTH);
        panel.add(new JScrollPane(header),BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        return panel;
    }
    
     /** Returns a JPanel for showing the optiinal header information, that contains some sub components. */
    protected JComponent makeTextPanel2(String text) {
        JPanel panel = new JPanel();
        JLabel filler = new JLabel(text); 
        panel.setLayout(new BorderLayout());
        panel.add(filler,BorderLayout.NORTH);
        panel.add(new JScrollPane(optional_header),BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        return panel;
    }
    
     /** Returns a JPanel for showing the header section information, that contains some sub components. */
    protected JComponent makeTextPanel3(String text) {
        JPanel panel = new JPanel();
        JLabel filler = new JLabel(text); 
        panel.setLayout(new BorderLayout());
        panel.add(filler,BorderLayout.NORTH);
        panel.add(new JScrollPane(pe_section),BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        return panel;
    }
    
    /** Returns a JPanel for showing the PE file icon, that contains some sub components. */
    protected JComponent makeIconPanel(String text) {
        JPanel panel = new JPanel();
        JLabel filler = new JLabel(text);
        JLabel icon = new JLabel();
        icon.setIcon(image_icon);
        panel.setLayout(new BorderLayout());
        panel.add(filler,BorderLayout.NORTH);
        panel.add(new JScrollPane(show_icon_info),BorderLayout.CENTER);
        panel.add(icon,BorderLayout.EAST);
        panel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        return panel;
    }
    
     /** Returns a JPanel for making a report, that contains some sub components. */
    protected JComponent makeReportPanel(String text) {
        JPanel panel = new JPanel();
        JLabel filler = new JLabel(text); 
        JPanel checkPanel = new JPanel(new GridLayout(0, 1));
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(makePDF);
        buttonGroup.add(makeXML);
        buttonGroup.add(makeHTML);
        checkPanel.add(makePDF);
        checkPanel.add(makeXML);
        checkPanel.add(makeHTML);
        panel.setLayout(new BorderLayout());
        panel.add(checkPanel, BorderLayout.LINE_START);
        panel.add(filler,BorderLayout.NORTH);
        panel.add(create,BorderLayout.SOUTH);
        panel.setBorder(BorderFactory.createEmptyBorder(20,300,20,280));
        return panel;
    }

     /** Listens to the check boxes state change. */
    @Override
    public void itemStateChanged(ItemEvent e) {
          
         if (e.getItemSelectable() == makePDF){        
             index = 1;
            //System.out.println("index="+index);
         }      
         
         else  if (e.getItemSelectable() == makeXML){
             index = 2;
             //System.out.println("index="+index);          
         }  
         
         else  if (e.getItemSelectable() == makeHTML){
             index = 3;
             //System.out.println("index="+index);     
         }       
         
         if (e.getStateChange() == ItemEvent.DESELECTED) {          
           index = 0; 
           //System.out.println("index="+index);
        }

    }
}
