/*
*  z/VM Performance Monitor
*  Copyright (C) 2008, 2009  Przemyslaw Kupisz
*
*  This program is free software; you can redistribute it and/or
*  modify it under the terms of the GNU General Public License
*  as published by the Free Software Foundation; either version 2
*  of the License, or (at your option) any later version.
*
*  This program is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*  GNU General Public License for more details.
*
*  You should have received a copy of the GNU General Public License
*  along with this program; if not, write to the Free Software
*  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

/*
 * ZpmApp.java
 */

package zpm;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class ZpmApp extends SingleFrameApplication {

    static Connection conn = null;
    static String ip = "127.0.0.1";
    static String dbname = null;
    static String user = null;
    static String passwd = null;
    static String port = "3306";

    static Statement s = null;
    static ResultSet rs = null;

    //MenuEvents Begin
    static byte toggleButtonSet = 0;
    static int interval = 20;
    
    static boolean checkbox1 = false; //System
    static boolean checkbox2 = false; //Monitor
    static boolean checkbox3 = false; //Sheduler
    static boolean checkbox4 = false; //Storage
    static boolean checkbox5 = false; //Users
    static boolean checkbox6 = false; //Processor
    static boolean checkbox7 = false; //I/O
    static boolean checkbox8 = false; //Seek
    static boolean checkbox9 = false; //Virtual Network
    static boolean checkbox10 = false; //Appldata
    //MenuEvents End

    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
        show(new ZpmView(this));
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of ZpmApp
     */
    public static ZpmApp getApplication() {
        return Application.getInstance(ZpmApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(ZpmApp.class, args);
    }
}
