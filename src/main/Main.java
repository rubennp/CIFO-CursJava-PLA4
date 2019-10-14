package main;

import java.util.ArrayList;

public class Main {
	private static final int PRINCIPAL = -1, SALIR = 0;
	private static int tabla, opCRUD;
	private static ArrayList<String> resultado = new ArrayList<String>();
	
	public static void main(String[] args) throws Exception {
		try {
			MySQL db = new MySQL("localhost", "3306", "cifo_ex_pla4", "rubennp", "I5@5l97B");
			
			do {
				Menu menuPrincipal = new Menu("PRINCIPAL");
				tabla = menuPrincipal.getOp();
				if (tabla != SALIR) {
					do {
						Menu menuCRUD = new Menu((tabla == 1) ? "PRODUCTOS" : "PROVEEDORES");
						opCRUD = menuCRUD.getOp();
						if (opCRUD != PRINCIPAL) {
							resultado = db.consulta(opCRUD, tabla);
							for (String linea : resultado) System.out.println(linea);
						}
					} while (opCRUD != PRINCIPAL);
				}
			} while (tabla != SALIR);
			
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}