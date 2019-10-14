package main;

import java.util.Scanner;
import java.util.ArrayList;

public class Menu {
	private static Scanner in = new Scanner(System.in);
	
	static final String LD = "=============================================================",
						LS = "-------------------------------------------------------------";
	private String tit;
	private ArrayList<String> ops = new ArrayList<String>();
	
	public Menu(String tit) {
		this.tit = tit;
		
		if (tit.equals("PRINCIPAL")) {
			this.ops.add("Salir");
			this.ops.add("Productos");
			this.ops.add("Proveedores");
		} else if (tit.contains("MODIFICA PRODUCTO")){
			this.ops.add("Salir de modificar");
			this.ops.add("Nombre");
			this.ops.add("Precio Unitario");
			this.ops.add("NIF Proveedor");
		} else if (tit.contains("MODIFICA PROVEEDOR")) {
			this.ops.add("Salir de modificar");
			this.ops.add("NIF");
			this.ops.add("Nombre");
			this.ops.add("Dirección");		
		}
		else {
			this.ops.add("Volver al menú principal");
			this.ops.add("Añadir registro");
			this.ops.add("Ver registros");
			this.ops.add("Modificar registro");
			this.ops.add("Eliminar registro");
		}
		System.out.println();
		this.print();
	}
	
	public void print() {
		System.out.println(LD);
		System.out.println(this.tit);
		System.out.println(LS);
		for (int op = 0; op < this.ops.size(); op++) System.out.println("[" + op + "] " + this.ops.get(op));
		System.out.println(LD);
	}
	
	public int getOp() {
		int i = in.nextInt();
		
		if (!this.tit.equals("PRINCIPAL") && i == 0) return -1;
		else return i;
	}
	
	public void closeIn() {
		in.close();
	}
}
