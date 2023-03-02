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
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Przemyslaw Kupisz
 */


public class JDBCinit {

    public JDBCinit() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException{
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        ZpmApp.conn = (Connection) DriverManager.getConnection("jdbc:mysql://"
                + ZpmApp.ip + ":" + ZpmApp.port + "/" + ZpmApp.dbname, ZpmApp.user, ZpmApp.passwd);

        ZpmApp.s = (Statement) ZpmApp.conn.createStatement();
    }
    public JDBCinit(Connection conn, boolean m) throws SQLException {
        if (m == true) {
            conn.close();
            ZpmView.eDBConn.setEnabled(false);
            ZpmView.sDBConn.setEnabled(true);
        }
    }
}
