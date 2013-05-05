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

import java.io.IOException;
import javax.swing.JFrame;

/**
 *
 * @author Qingbao.Guo
 * @version Version 1.0
 */
public class JavaPE {

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws IOException, Exception {
      
                System.out.print("This is a simple Java PE reader!\nJava version: ");
                System.out.print(System.getProperty("java.version"));
                System.out.print("\nJava home: ");
                System.out.print(System.getProperty("java.home"));
                System.out.print("\nCurrent dir: ");
                System.out.println(System.getProperty("user.dir"));
                   
                CommandLine.printUsage();
                
                
               
       java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
               GUI layout = new GUI(); // create MenuFrame
               layout.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
               layout.setLocation(700, 100);
               layout.setSize( 800, 700 ); // set frame size
               layout.setVisible( true ); 
              
            }
        });
                
              
  }
}
