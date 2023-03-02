/*
 *  This file is part of z/VM Performance Monitor.
 *
 *  z/VM Performance Monitor is free software: you can redistribute
 *  it and/or modify it under the terms of the GNU General Public License
 *  as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  z/VM Performance Monitor is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with z/VM Performance Monitor Daemon.
 *  If not, see <http://www.gnu.org/licenses/>.
*/

package zpm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Przemyslaw Kupisz
 */
public class CurrentEvents implements Runnable {
    private static boolean thread_is_on;
    /**
     * Define new thread
     */
    Thread t_cevent;
    
    /**
     *Define statements for new DB connection
     */
    Connection conn = null;
    Statement s = null;
    ResultSet rs = null;

    /**
     * Last id from each EVENT table
     * (for each Domain)
     */
    /**
     * Domain 1
     */
    int id1r1 = 0;
     /**
     * Domain 2
     */
    int id2r1 = 0;
    int id2r3 = 0;
    //=========
    int id3;
     /**
     * Domain 4
     */
    int id4r10 = 0;
    //========
    int id5;
    int id6;
    /**
     * Domain 7
     */
     int id7r1 = 0;
    /**
     * Domain 8
     */
    int id8;
    int id9;
    int id10;

/**
 * Creates new thread & goes to function run()
 */
    public CurrentEvents() {
        t_cevent = new Thread(this, "CurrentEvents");
        t_cevent.start();

    }
/**
 * Killing active thread
 */
    public static void stop() {
        thread_is_on = false;
    }

    /**
     * StartingValueSelectSQL() used for each Event Record
     */
    public int StartingValueSelectSQL(String rec) throws SQLException {
        int x = 0;
        rs = s.executeQuery("SELECT id FROM ("+rec+") ORDER BY id DESC LIMIT 1");
        while(rs.next()) {
        rs.last();
        x = rs.getInt(1);
        }
        rs.close();
        return x;
    }

    /**
     * Start "SELECT queries for records"
     * Returns formatted fields to jPanel "CurrentEvents"
     * Each record has it owns void function
     */
        private void Query_d1r1() throws SQLException {
        rs = s.executeQuery("SELECT * FROM d1r1 WHERE id > ("+id1r1+")");
        while (rs.next()) {
                     id1r1 = rs.getInt("id");
                     String cell2 = rs.getString("time");
                     String cell3 = rs.getString("mtrepr_config");
                     ZpmView.jTextArea1.append(cell2 + " |   " + "Event Domains state has changed  "
                     + "CONFIG time limit in seconds( " + cell3 + " )\n");
                          }
        rs.close();
    }
        private void Query_d2r1() throws SQLException {
        rs = s.executeQuery("SELECT * FROM d2r1 WHERE id > ("+id2r1+")");
        while (rs.next()) {
                     id2r1 = rs.getInt("id");
                     String cell2 = rs.getString("time");
                     String cell3 = rs.getString("sclrdb_vmduser");
                     String cell4 = "NOT TERMINAL";
                     if ((rs.getInt("sclrdb_calflags") & 128) == 128) {
                         cell4 = rs.getString("sclrdb_rdevsid");
                     }
                     ZpmView.jTextArea1.append(cell2 + " |   " + "Console READ has begun for User( "
                             + cell3 + " )  "
                     + "Real device subchannel id( " + cell4 + " )\n");
                          }
        rs.close();
    }
        private void Query_d2r3() throws SQLException {
        rs = s.executeQuery("SELECT * FROM d2r3 WHERE id > ("+id2r3+")");
        while (rs.next()) {
                     id2r3 = rs.getInt("id");
                     String cell2 = rs.getString("time");
                     String cell3 = rs.getString("sclwrr_vmduser");
                     String cell4 = "NOT TERMINAL";
                     if ((rs.getInt("sclwrr_calflags") & 128) == 128) {
                         cell4 = rs.getString("sclwrr_rdevsid");
                     }
                     ZpmView.jTextArea1.append(cell2 + " |   " + "Console WRITE has completed for User( "
                     + cell3 + " )  " + "Real device subchannel id( " + cell4 + " ) \n");
                          }
        rs.close();
    }
        private void Query_d4r10() throws SQLException {
        rs = s.executeQuery("SELECT * FROM d4r10 WHERE id > ("+id4r10+")");
        while (rs.next()) {
                     id4r10 = rs.getInt("id");
                     String cell2 = rs.getString("time");
                     String cell3 = rs.getString("useite_vmduser");
                     String cell4 = rs.getString("useite_vmdsvmid");
                     
                     ZpmView.jTextArea1.append(cell2 + " |   " + "Interaction at transaction ended for User( "
                     + cell3 + " )  " + "VMDBK name for the last successful IUCV, VMCF, APPC/VM( " + cell4 + " ) \n");
                          }
        rs.close();
    }

    private void Query_d7r1() throws SQLException {
        rs = s.executeQuery("SELECT * FROM d7r1 WHERE id > ("+id7r1+")");
        while (rs.next()) {
                     id7r1 = rs.getInt("id");
                     String cell2 = rs.getString("time");
                     String cell3 = rs.getString("seksek_vmduser");
                     String cell4 = rs.getString("seksek_iorposct"); 
                     ZpmView.jTextArea1.append(cell2 + " |   " + "SEEK I/O Operation  "
                     + "Requestor id( " + cell3 + " )  "
                     + "Numer of DASD access arm position changes( " + cell4 + " )\n");
                          }
        rs.close();
    }


    /**
     * Here starts new Thread (run())
     */
    @Override
    public void run() {

        try {
            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
            } catch (InstantiationException ex) {
                Logger.getLogger(CurrentEvents.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(CurrentEvents.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(CurrentEvents.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            conn = DriverManager.getConnection("jdbc:mysql://" + ZpmApp.ip + ":" + ZpmApp.port + "/" + ZpmApp.dbname, ZpmApp.user, ZpmApp.passwd);
        } catch (SQLException ex) {
            Logger.getLogger(CurrentEvents.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            s = conn.createStatement();
        } catch (SQLException ex) {
            Logger.getLogger(CurrentEvents.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            /** Sets Last (MAX) Value for each table
             * from DB for each Selected Domain
             */
            thread_is_on = true;
            if (ZpmApp.checkbox2 == true && thread_is_on == true) {
                id1r1 = StartingValueSelectSQL("d1r1");
            }
            if (ZpmApp.checkbox3 == true && thread_is_on == true) {
                id2r1 = StartingValueSelectSQL("d2r1");
            }
            if (ZpmApp.checkbox3 == true && thread_is_on == true) {
                id2r3 = StartingValueSelectSQL("d2r3");
            }
            if (ZpmApp.checkbox5 == true && thread_is_on == true) {
                id4r10 = StartingValueSelectSQL("d4r10");
            }
            if (ZpmApp.checkbox8 == true && thread_is_on == true) {
                id7r1 = StartingValueSelectSQL("d7r1");
            }





            
            /**
             * Loop while for in Real Time Monitoring
             */
            while(thread_is_on) {
                /**System
                 * Domain
                 */
                if (ZpmApp.checkbox1 == true && thread_is_on == true) {

                }
                /**Monitor
                 * Domain
                 */
                if (ZpmApp.checkbox2 == true && thread_is_on == true) {
                    Query_d1r1();
                }
                /**Sheduler
                 * Domain
                 */
                if (ZpmApp.checkbox3 == true && thread_is_on == true) {
                    Query_d2r1();
                    Query_d2r3();
                }
                /**Storage
                 * Domain
                 */
                if (ZpmApp.checkbox4 == true && thread_is_on == true) {

                }
                /**Users
                 * Domain
                 */
                if (ZpmApp.checkbox5 == true && thread_is_on == true) {
                    Query_d4r10();
                }
                /**Processor
                 * Domain
                 */
                if (ZpmApp.checkbox6 == true && thread_is_on == true) {

                }
                /**I/O
                 * Domain
                 */
                if (ZpmApp.checkbox7 == true && thread_is_on == true) {

                }
                /**SEEK
                 * Domain
                 */
                if (ZpmApp.checkbox8 == true && thread_is_on == true) {
                    Query_d7r1();
                }
                /**Virtual Network
                 * Domain
                 */
                if (ZpmApp.checkbox9 == true && thread_is_on == true) {

                }
                /**Appldata
                 * Domain
                 */
                if (ZpmApp.checkbox10 == true && thread_is_on == true) {

                }

                
                if (thread_is_on == true) Thread.sleep(ZpmApp.interval * 1000);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CurrentEvents.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(CurrentEvents.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            /**
             * Close DB connection
             */
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(CurrentEvents.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
