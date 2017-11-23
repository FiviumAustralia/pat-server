package fivium.pat.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PAT_DAO {

	private static Connection connection;
	private static Log logger = LogFactory.getLog(PAT_DAO.class);

	private static Connection getConnection() throws SQLException, ClassNotFoundException {
		if (connection == null) {
			Class.forName("org.mariadb.jdbc.Driver");
			// 2147483 seconds ~ 24 days (maximum for mysql run on windows)
			connection = DriverManager.getConnection(
					"jdbc:mariadb://localhost:3306/pat?user=root&password=root&sessionVariables=wait_timeout=2147483");
		}
		return connection;
	}

	public static Collection<Map<String, String>> executeStatement(String sql, Object[] args)
			throws SQLException, ClassNotFoundException {
		Collection<Map<String, String>> returnData = new HashSet<Map<String, String>>();
		PreparedStatement preparedStatement = getConnection().prepareStatement(sql);
		// insert args
		if (args != null) {
			for (int i = 0; i < args.length; i++) {
				preparedStatement.setObject(i + 1, args[i]);
			}
		}

		ResultSet resultSet = preparedStatement.executeQuery();
		ResultSetMetaData md = resultSet.getMetaData();
		int columns = md.getColumnCount();
		while (resultSet.next()) {
			HashMap<String, String> row = new HashMap<String, String>(columns);
			for (int i = 1; i <= columns; ++i) {
				row.put(md.getColumnName(i), String.valueOf(resultSet.getObject(i)));
			}
			returnData.add(row);
		}
		return returnData;
	}


	public static void executeSingleSQLStatementInBulk(String sql, Map<String, List<Object>> dataToBeStored) {
		logger.info("Entering executeBulkStatements...");
		try {
			Connection connection = getConnection();
			PreparedStatement statement = connection.prepareStatement(sql);
			Set<String> dataSet = dataToBeStored.keySet();
			for (String x : dataSet) {
				List<Object> aList = dataToBeStored.get(x);
				for(int i = 0; i<aList.size();i++) {
					statement.setString(i+1, aList.get(i).toString());
				}
				statement.addBatch();
			}
				statement.executeBatch(); // Execute every 1000 items.
		} catch (ClassNotFoundException | SQLException e) {
			logger.error("ClassNotFoundException | SQLException occured storing the data into the database... " + e);
		} catch (Exception e) {
			logger.error("An Exception occured storing the data into the database... " + e);
		}
	}
	
}
