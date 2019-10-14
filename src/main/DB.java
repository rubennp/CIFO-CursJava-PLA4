package main;

import java.util.Scanner;
import java.util.ArrayList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class DB {
	private static final int CREATE = 1, READ = 2, UPDATE = 3, DELETE = 4;			// para tipo
	private static final int SALIRMODIFICAR = -1, PRODUCTOS = 1, PROVEEDORES = 2;	// para tabla o menús
	private static final int uNombrePd = 1, uPrecioUnitario = 2, uNIFProveedor = 3;	// para update producto
	private static final int uNIF = 1, uNombrePv = 2, uDireccion = 3;				// para update proveedor
	
	static Connection conexion;
	static Statement stmt;
	static PreparedStatement pstmt;
	static ResultSet rs;
	
	/**
	 * Realiza las consultas a la base de datos
	 * @param tipo es el tipo de consulta (create, read, update o delete)
	 * @param tabla es la tabla en la que se realiza la consulta
	 * @return el resultado de la consulta
	 * @throws Exception
	 */
	public ArrayList<String> consulta(int tipo, int tabla) throws Exception {
		ArrayList<String> resultado = new ArrayList<String>();
		
		switch(tipo) {
		case CREATE:
			System.out.println("-> NUEVO " + ((tabla == PRODUCTOS) ? "PRODUCTO" : "PROVEEDOR"));
			
			switch (tabla) {
			case PRODUCTOS:
				System.out.println("* Introduce un nombre:");
				String nombre = extracted().nextLine();
				System.out.println("* Introduce el precio unitario:");
				double precio = 0;
				try {
					precio = extracted().nextDouble();
				} catch (Exception e) {
					resultado.add("-> Precio Unitario: valor mal introducido!");
					resultado.add("-> No se ha podido añadir el producto");
					break;
				}
				System.out.println("* Introduce el NIF del proveedor:");
				String nif = extracted().nextLine();
				stmt = conexion.createStatement();
				rs = stmt.executeQuery("SELECT NIF FROM PROVEEDOR WHERE NIF = \"" + nif + "\"");
				if (!rs.first()) {
					resultado.add("-> No se ha encontrado el proveedor!");
				} else {
					pstmt = conexion.prepareStatement("INSERT INTO PRODUCTO (Nombre, PrecioUnitario, NIFProveedor) VALUES(?,?,?)");
					pstmt.setString(1, nombre);
					pstmt.setDouble(2, precio);
					pstmt.setString(3, nif);
					try {
						pstmt.execute();
					} catch (Exception e) {
						resultado.add("-> " + e);
						break;
					}
					resultado.add("-> Producto creado correctamente.");
				}
				break;
				
			case PROVEEDORES:
				System.out.println("* Introduce un NIF:");
				nif = extracted().nextLine();
				stmt = conexion.createStatement();
				rs = stmt.executeQuery("SELECT NIF FROM PROVEEDOR WHERE NIF = \"" + nif + "\"");
				if (rs.first()) {
					System.out.println("-> Ya existe un proveedor con este NIF!");
				} else {
					System.out.println("* Introduce un nombre:");
					nombre = extracted().nextLine();
					System.out.println("* Introduce una dirección:");
					String direccion = extracted().nextLine();
					pstmt = conexion.prepareStatement("INSERT INTO PROVEEDOR (NIF, Nombre, Direccion) VALUES (?,?,?)");
					pstmt.setString(1, nif);
					pstmt.setString(2, nombre);
					pstmt.setString(3, direccion);
					try {
						pstmt.execute();
					} catch (Exception e) {
						resultado.add("-> " + e);
						break;
					}
					resultado.add("-> Proveedor insertado correctamente.");
				}
			}
			break;
			
		case READ:
			stmt = conexion.createStatement();
			rs = stmt.executeQuery("SELECT * FROM " + ((tabla == PRODUCTOS) ? "PRODUCTO" : "PROVEEDOR"));
			switch(tabla) {
			case PRODUCTOS:
				while(rs.next()) resultado.add(rs.getInt("id") + " " + rs.getString("Nombre") + " " + rs.getDouble("PrecioUnitario") + " " + rs.getString("NIFProveedor"));
				break;
			case PROVEEDORES:
				while(rs.next()) resultado.add(rs.getString("NIF") + " " + rs.getString("Nombre") + " " + rs.getString("Direccion"));
				break;
			}
			break;
			
		case UPDATE:
			switch(tabla) {
			case PRODUCTOS:
				System.out.println("* Introduce el 'id' del producto que quieras modificar:");
				int id = -1;
				try {
					id = extracted().nextInt();
				} catch (Exception e) {
					resultado.add("-> El 'id' introducido no es un número válido");
					break;
				}
				stmt = conexion.createStatement();
				rs = stmt.executeQuery("SELECT * FROM PRODUCTO WHERE id=" + Integer.toString(id));
				if (rs.first()) {
					int op;
					do {
						Menu menuUpdateProducto = new Menu("MODIFICA PRODUCTO \n(selecciona campo a modificar):");
						op = menuUpdateProducto.getOp();
						switch(op) {
						case uNombrePd:
							System.out.println("* Introduce un nuevo Nombre:");
							String nombre = extracted().nextLine();
							pstmt = conexion.prepareStatement("UPDATE PRODUCTO SET Nombre=? WHERE id=?");
							pstmt.setString(1, nombre);
							pstmt.setInt(2, id);
							try {
								pstmt.execute();
							} catch (Exception e) {
								System.out.println("-> " + e);
								break;
							}
							System.out.println("-> 'Nombre' modificado correctamente.");
							break;
						case uPrecioUnitario:
							System.out.println("* Introduce un nuevo Precio Unitario");
							double precio;
							try {
								precio = extracted().nextDouble();
							} catch (Exception e) {
								System.out.println("-> Precio Unitario: valor mal introducido!"); 
								System.out.println("-> No se ha podido modificar el producto");
								op = SALIRMODIFICAR;
								break;
							}
							pstmt = conexion.prepareStatement("UPDATE PRODUCTO SET PrecioUnitario=? WHERE id=?");
							pstmt.setDouble(1, precio);
							pstmt.setInt(2,id);
							try {
								pstmt.execute();
							} catch (Exception e){
								System.out.println("-> " + e);
								break;
							}
							System.out.println("-> 'Precio Unitario' modificado correctamente.");
							break;
						case uNIFProveedor:
							System.out.println("* Selecciona un nuevo proveedor: ");
							stmt = conexion.createStatement();
							rs = stmt.executeQuery("SELECT * FROM PROVEEDOR");
							int n = 1;
							while (rs.next()) {
								System.out.println("["+ n +"] " + rs.getString("NIF") + " " + rs.getString("Nombre"));
								n++;
							}
							int proveedor = extracted().nextInt();
							pstmt = conexion.prepareStatement("UPDATE PRODUCTO SET NIFProveedor=? WHERE id=?");
							rs.first();
							rs.absolute(proveedor);
							pstmt.setString(1, rs.getString("NIF"));
							pstmt.setInt(2, id);
							try {
								pstmt.execute();
								System.out.println("-> Proveedor de producto cambiado con éxito.");
							} catch (Exception e) {
								System.out.println("-> " + e);
							}
							break;
						}
					} while(op != SALIRMODIFICAR);
					break;
				} else {
					resultado.add("-> No se ha encontrado el registro!");
					break;
				}
			case PROVEEDORES:
				System.out.println("* Introduce el 'NIF' del proveedor que quieras modificar:");
				String oldNIF = extracted().nextLine();
				stmt = conexion.createStatement();
				rs = stmt.executeQuery("SELECT * FROM PROVEEDOR WHERE NIF=\"" + oldNIF + "\"");
				if (rs.first()) {
					int op;
					do {
						Menu menuUpdateProveedores = new Menu("MODIFICA PROVEEDOR\n(selecciona campo a modificar):");
						op = menuUpdateProveedores.getOp();
						switch(op) {
						case uNIF:
							System.out.println("* Introduce un nuevo NIF:");
							String newNIF = extracted().nextLine();
							stmt = conexion.createStatement();
							rs = stmt.executeQuery("SELECT * FROM PROVEEDOR WHERE NIF=\"" + newNIF + "\"");
							if (!rs.first()) {
								pstmt = conexion.prepareStatement("UPDATE PROVEEDOR SET NIF=? WHERE NIF=?");
								pstmt.setString(1, newNIF);
								pstmt.setString(2, oldNIF);
								try {
									pstmt.execute();
									oldNIF = newNIF;
									System.out.println("-> NIF modificado correctamente.");
								} catch (Exception e) {
									System.out.println("-> " + e);
									break;
								}
								break;
							} else {
								System.out.println("-> Ya existe un Proveedor con este NIF, debe usar otro!");
								break;
							}
						case uNombrePv:
							System.out.println("* Introduce un nuevo Nombre:");
							String nombre = extracted().nextLine();
							pstmt = conexion.prepareStatement("UPDATE PROVEEDOR SET Nombre=? WHERE NIF=?");
							pstmt.setString(1, nombre);
							pstmt.setString(2, oldNIF);
							try {
								pstmt.execute();
								System.out.println("-> Nombre modificado correctamente.");
							} catch (Exception e) {
								System.out.println("-> " + e);
								break;
							}
							break;
						case uDireccion:
							System.out.println("* Introduce una nueva Dirección:");
							String direccion = extracted().nextLine();
							pstmt = conexion.prepareStatement("UPDATE PROVEEDOR SET Direccion=? WHERE NIF=?");
							pstmt.setString(1, direccion);
							pstmt.setString(2, oldNIF);
							try {
								pstmt.execute();
								System.out.println("-> Dirección modificada correctamente.");
							} catch (Exception e) {
								System.out.println("-> " + e);
							}
							break;
						}
					} while (op != SALIRMODIFICAR);
				} else {
					resultado.add("-> No se ha encontrado al proveedor!");
					break;
				}
			}
			break;
			
		case DELETE:
			switch(tabla) {
			case PRODUCTOS:
				System.out.println("* Introduce la 'id' del producto que quieras eliminar:");
				int id = -1;
				try {
					id = extracted().nextInt();
				} catch (Exception e) {
					resultado.add("-> El 'id' introducido no es un número válido");
				}
				stmt = conexion.createStatement();
				rs = stmt.executeQuery("SELECT * FROM PRODUCTO WHERE id=" + Integer.toString(id));
				if (rs.first()) {
					pstmt = conexion.prepareStatement("DELETE FROM PRODUCTO WHERE id=?");
					pstmt.setInt(1, id);
					try {
						pstmt.execute();
					} catch (Exception e) {
						resultado.add("-> " + e);
						break;
					}
					resultado.add("-> Registro con id = " + id + " borrado correctamente.");
				} else {
					resultado.add("-> No se ha encontrado el registro!");
				}
				break;
			case PROVEEDORES:
				System.out.println("* Introduce el 'NIF' del proveedor que quieras eliminar:");
				String nif = extracted().nextLine();
				stmt = conexion.createStatement();
				rs = stmt.executeQuery("SELECT * FROM PROVEEDOR WHERE NIF=\"" + nif + "\"");
				if (rs.first()) {
					pstmt = conexion.prepareStatement("DELETE FROM PROVEEDOR WHERE NIF=?");
					pstmt.setString(1, nif);
					try {
						pstmt.execute();
					} catch(Exception e) {
						resultado.add("-> " + e);
						break;
					}
					resultado.add("-> Registro con NIF = " + nif + " borrado correctamente.");
				} else {
					resultado.add("-> No se ha encontrado el NIF introducido!");
				}
				break;
			}
		}
		
		return resultado;
	}

	/**
	 * Entrada por teclado
	 * @return la entrada por teclado introducida.
	 */
	private Scanner extracted() {
		return new Scanner(System.in);
	}
	
	/**
	 * Cierra la conexion con la BD
	 * @throws Exception
	 */
	public void close() throws Exception {
		conexion.close();
	}
}
