package main;

import java.sql.*;

public class MySQL extends DB {
	private String cadConexion = null;
	private String user = null;
	private String pass = null;
	
	public MySQL(String host, String port, String bd, String user, String pass) throws Exception {
		this.cadConexion = "jdbc:mysql://" + host + ":" + port + "/" + bd;
		this.user = user;
		this.pass = pass;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conexion = DriverManager.getConnection(cadConexion, this.user, this.pass);
		} catch (Exception e) {
			throw e;
		}
	}
}
