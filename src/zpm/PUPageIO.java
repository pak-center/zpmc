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
import java.math.BigDecimal;
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
import org.jfree.data.category.DefaultCategoryDataset;

/**
 *
 * @author Przemyslaw Kupisz
 */
public class PUPageIO implements Runnable {
    /**
     * Define new thread
     */
    Thread t_PUPageIO;

    /**
     *Define statements for new DB connection
     */
    Connection conn = null;
    Statement s = null;
    ResultSet rs = null;

    /**
    * Creates new thread & goes to function run()
    */
    public PUPageIO() {
        t_PUPageIO = new Thread(this, "PUPageIO");
        t_PUPageIO.start();

    }

    @Override
    public void run() {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection("jdbc:mysql://" + ZpmApp.ip + ":" + ZpmApp.port + "/" + ZpmApp.dbname, ZpmApp.user, ZpmApp.passwd);
            s = conn.createStatement();
            //create dataset
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            BigDecimal read,write;
            String[]cpu = new String[100]; //99 PU
            int i=0;

            rs = s.executeQuery("SELECT sytsyp_pfxcpuad FROM d0r1 GROUP BY sytsyp_pfxcpuad;");
            while (rs.next()) {
            cpu[++i]=""+rs.getString("sytsyp_pfxcpuad");
            cpu[0] =""+i;  //total processors <-string
            }
            rs.close();

            //==============
            int e;

            for(e=1;e<Integer.decode(cpu[0])+1;e++)
            {
                rs = s.executeQuery(" SELECT sytsyp_plspiopr,sytsyp_plspiopw  FROM d0r1 WHERE sytsyp_pfxcpuad='"
                        +cpu[e]+"' ORDER BY ID DESC LIMIT 2");
                rs.next();
                read = rs.getBigDecimal("sytsyp_plspiopr");
                write = rs.getBigDecimal("sytsyp_plspiopw");
                rs.next();
                read =read.subtract(rs.getBigDecimal("sytsyp_plspiopr"));
                write = write.subtract(rs.getBigDecimal("sytsyp_plspiopw"));
                rs.close();
                dataset.addValue(read,"Read",cpu[e]);
                dataset.addValue(write, "Write", cpu[e]);
            }

            conn.close();
            //dataset READY
            JFreeChart Chart = ChartFactory.createBarChart(
            "I/O operations for system paging by specific processor address",
            "PU ADDRESS", "Value", dataset, PlotOrientation.VERTICAL, true, true, false);

            JFrame frame = new JFrame("PU PAGING I/O");
            frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

            BufferedImage image = Chart.createBufferedImage(1024, 768);
            JLabel label = new JLabel();
            label.setIcon(new ImageIcon(image));
            frame.getContentPane().add(label);
            frame.pack();
            frame.setVisible(true);

        } catch (SQLException ex) {
            Logger.getLogger(PUPageIO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(PUPageIO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(PUPageIO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(PUPageIO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
