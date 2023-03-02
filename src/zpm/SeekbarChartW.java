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
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.jdbc.JDBCCategoryDataset;

/**
 *
 * @author Przemyslaw Kupisz
 */
public class SeekbarChartW implements Runnable{
     /**
     * Define new thread
     */
    Thread t_hevent;
     /**
     *Define statements for new DB connection
     */
    Connection conn = null;
    Statement s = null;
    ResultSet rs = null;
 /**
 * Creates new thread & goes to function fun()
 */
    public SeekbarChartW() {
        t_hevent = new Thread(this, "SeekbarChartW");
        t_hevent.start();
    }

    @Override
        public void run() {
        try {
            String X = "Device number";
            String Y = "WRITE count";
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection("jdbc:mysql://" + ZpmApp.ip + ":" + ZpmApp.port + "/" + ZpmApp.dbname, ZpmApp.user, ZpmApp.passwd);
            s = conn.createStatement();

            String query1 = "select seksek_rvdevdev, count(seksek_calflags) as seksek_calflags from d7r1 where seksek_calflags=0 "
                    + "&& time > DATE_SUB(current_date, interval 1 month) group by seksek_rvdevdev";
            CategoryDataset barDataset = new JDBCCategoryDataset(conn, query1);
            JFreeChart barChart = ChartFactory.createBarChart3D("Number of WRITE channel program from last month by specific DASD"
                    , X, Y, barDataset, PlotOrientation.VERTICAL, true, true, false);

            JFrame frame = new JFrame("SEEK Domain");
            frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

            BufferedImage image = barChart.createBufferedImage(1024, 768);
            JLabel label = new JLabel();
            label.setIcon(new ImageIcon(image));
            frame.getContentPane().add(label);
            frame.pack();
            frame.setVisible(true);

            rs.close();
            conn.close();

        } catch (InstantiationException ex) {
            Logger.getLogger(SeekbarChartW.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(SeekbarChartW.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(SeekbarChartW.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SeekbarChartW.class.getName()).log(Level.SEVERE, null, ex);
        }
        }
}
