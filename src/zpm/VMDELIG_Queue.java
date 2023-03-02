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

import java.awt.image.BufferedImage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.util.TableOrder;

/**
 *
 * @author Przemyslaw Kupisz
 */
public class VMDELIG_Queue implements Runnable {
     /**
     * Define new thread
     */
    Thread t_VMDELIG;
    /**
     *Define statements for new DB connection
     */
    Connection conn = null;
    Statement s = null;
    ResultSet rs = null;
    /**
    * Creates new thread & goes to function run()
    */
    public VMDELIG_Queue() {
        t_VMDELIG = new Thread(this, "VMDELIG_Queue");
        t_VMDELIG.start();
    }

    @Override
    public void run() {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection("jdbc:mysql://" + ZpmApp.ip + ":" + ZpmApp.port + "/" + ZpmApp.dbname, ZpmApp.user, ZpmApp.passwd);
            s = conn.createStatement();
        //==========
        DefaultCategoryDataset dataset2 = new DefaultCategoryDataset();
        try {
        rs = s.executeQuery("SELECT DISTINCT useite_vmduser,USEITE_HFELIG0,USEITE_HFELIG1,USEITE_HFELIG2,USEITE_HFELIG3 FROM d4r10 GROUP BY useite_vmduser");

        while (rs.next()) {
        dataset2.addValue(rs.getBigDecimal("USEITE_HFELIG0"),"E0", rs.getString("USEITE_VMDUSER"));
        dataset2.addValue(rs.getBigDecimal("USEITE_HFELIG1"), "E1", rs.getString("USEITE_VMDUSER"));
        dataset2.addValue(rs.getBigDecimal("USEITE_HFELIG2"), "E2", rs.getString("USEITE_VMDUSER"));
        dataset2.addValue(rs.getBigDecimal("USEITE_HFELIG3"), "E3", rs.getString("USEITE_VMDUSER"));
        }
         rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(ZpmView.class.getName()).log(Level.SEVERE, null, ex);
        }
            
            /**
             * Close DB connection
             */
            conn.close();
            
           // now exist & ready dataset
           JFreeChart Chart = ChartFactory.createMultiplePieChart3D(
           "Number of times the VMDBK was in E0-E3 Queues. (if blank then none)",
                   dataset2, TableOrder.BY_COLUMN, true, true, false);

           JFrame frame = new JFrame("VMDELIG Queue");
           frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

           BufferedImage image = Chart.createBufferedImage(1024, 768);
           JLabel label = new JLabel();
           label.setIcon(new ImageIcon(image));
           frame.getContentPane().add(label);
           frame.pack();
           frame.setVisible(true);
   
        } catch (InstantiationException ex) {
            Logger.getLogger(VMDELIG_Queue.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(VMDELIG_Queue.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(VMDELIG_Queue.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(VMDELIG_Queue.class.getName()).log(Level.SEVERE, null, ex);
        }
 }
}