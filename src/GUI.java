
public class GUI {

	public static void printGrid(Grid grid, String name) {
		Ship_Enum[][] panels = grid.getPanels();
		
		System.out.println("\n     "+name+"'s Grid\n");
		for (int i = 0; i < 10; i++) {
			//Primera columna para añadir la letra
			System.out.print((char)('A'+i)+"   |  "+panels[i][0].getId()+" | ");
			for (int j = 1; j < 10; j++) {
				System.out.print(" "+panels[i][j].getId()+" | ");
			}
			System.out.println('\n');
		}
		//Ultima fila para añadir los numeros
		System.out.print("      1    ");
		for (int n = 2; n <= 10; n++) {
			System.out.print(n+"    ");
		}
		System.out.println('\n');
	}
	
	public static void print2Grids(Grid grid1, String name1, Grid grid2, String name2) {
		Ship_Enum[][] panels1 = grid1.getPanels();
		Ship_Enum[][] panels2 = grid2.getPanels();
		
		System.out.println("\n      "+name1+"'s Grid                                                              "+name2+"'s Grid\n");
		
		for (int i = 0; i < 10; i++) { 
			//Primer grid
			System.out.print(" "+(char)('A'+i)+"   |  "+panels1[i][0].getId()+" | ");
			for (int j = 1; j < 10; j++) {
				System.out.print(" "+panels1[i][j].getId()+" | ");
			}
			//Segundo Grid
			System.out.print("               "+(char)('A'+i)+"   |  "+panels2[i][0].getId()+" | ");
			for (int j = 1; j < 10; j++) {
				System.out.print(" "+panels2[i][j].getId()+" | ");
			}
			System.out.println('\n');
		}
		
		//1-10
		//Primer Grid
		System.out.print("       1    ");
		for (int n = 2; n <= 10; n++) {
			System.out.print(n+"    ");
		}
		//Segudno Grid
		System.out.print("                    1    ");
		for (int n = 2; n <= 10; n++) {
			System.out.print(n+"    ");
		}
		System.out.println('\n');
		
		//Tipo y numero de naves, desaparecen cuando se hunden
		System.out.print((!grid1.getAircraftCarrier().isSunk() ? "A.Carrier:" + grid1.getAircraftCarrier().getLifeLeft() : "           ")
			+ (!grid1.getBattleship().isSunk() ? "  Battleship:" + grid1.getBattleship().getLifeLeft() : "              ")
			+ (!grid1.getCruiser().isSunk() ? "  Cruiser:" + grid1.getCruiser().getLifeLeft() : "           ")
			+ (!grid1.getSubmarine().isSunk() ? "  Submarine:" + grid1.getSubmarine().getLifeLeft() : "             ")
			+ (!grid1.getDestroyer().isSunk() ? "  Destroyer:" + grid1.getDestroyer().getLifeLeft() : "             "));
		System.out.print("            " + (!grid2.getAircraftCarrier().isSunk() ? "A.Carrier  " : "           ")
				+ (!grid2.getBattleship().isSunk() ? "Battleship  " : "            ")
				+ (!grid2.getCruiser().isSunk() ? "Cruiser  " : "         ")
				+ (!grid2.getSubmarine().isSunk() ? "Submarine  " : "           ")
				+ (!grid2.getDestroyer().isSunk() ? "Destroyer  " : "           "));
		System.out.println('\n');
	}

	public static void printStyle(String text, int style) {
		switch (style) {
		
		// text
		case 0:
		default:
			System.out.println(text);
			break;
			
		//  --------
        // || text ||
        //  --------   
		case 1:
			System.out.print(" ");
			for (int i = 0; i < text.length()+4; i++) System.out.print("-");
			System.out.println("\n|| "+text+" ||");
			System.out.print(" ");
			for (int i = 0; i < text.length()+4; i++) System.out.print("-");
			break;
		
		// ::::::::::
		// :: text ::
		// ::::::::::
		case 2:
			for (int i = 0; i < text.length()+6; i++) System.out.print(":");
			System.out.println("\n:: "+text+" ::");
			for (int i = 0; i < text.length()+6; i++) System.out.print(":");
			break;
			
		//   ////////////
	    //  //  text  //
        // ////////////	
	    case 3:
	    	System.out.print("  ");
		    for (int i = 0; i < text.length()+8; i++) System.out.print("/");
			System.out.println("\n //  "+text+"  //");
			for (int i = 0; i < text.length()+8; i++) System.out.print("/");
			break;
			 
		//  //======\\
		// ||  text  ||
		//  \\======//
		case 4:
			System.out.print(" //");
			for (int i = 0; i < text.length()+2; i++) System.out.print("=");
			System.out.print("\\\\");
			System.out.println("\n||  "+text+"  ||");
			System.out.print(" \\\\");
			for (int i = 0; i < text.length()+2; i++) System.out.print("=");
			System.out.print("//");
			break;
			
		// >>>>>>>>>>>
		//  >>  text  >>
		// >>>>>>>>>>>
		case 5:
			for (int i = 0; i < text.length()+7; i++) System.out.print(">");
			System.out.println("\n >>  "+text+"  >>");
			for (int i = 0; i < text.length()+7; i++) System.out.print(">");
			break;
			
		// ++++++++++
		// +  text  +
		// ++++++++++
		case 6:
			for (int i = 0; i < text.length()+6; i++) System.out.print("+");
			System.out.println("\n+  "+text+"  +");
			for (int i = 0; i < text.length()+6; i++) System.out.print("+");
			break;
			
		//   <<<<<<<<<<<
		// <<  text  <<
		//   <<<<<<<<<<<
		case 7:
			System.out.print("  ");
			for (int i = 0; i < text.length()+7; i++) System.out.print("<");
			System.out.println("\n<<  "+text+"  << ");
			System.out.print("  ");
			for (int i = 0; i < text.length()+7; i++) System.out.print("<");
			break;
			
		// ----------
		// -  text  -
		// ----------
		case 8:
			for (int i = 0; i < text.length()+6; i++) System.out.print("-");
			System.out.println("\n-  "+text+"  -");
			for (int i = 0; i < text.length()+6; i++) System.out.print("-");
			break;
			
		// \\\\\\\\\\\\
		//  \\  text  \\
		//   \\\\\\\\\\\\	
		case 9:
			for (int i = 0; i < text.length()+8; i++) System.out.print("\\");
			System.out.println("\n \\\\  "+text+"  \\\\");
			System.out.print("  ");
			for (int i = 0; i < text.length()+8; i++) System.out.print("\\");
			break;
        
		
		//               __		
		//              /  |  text won the game !!
		//             / / |   	
		//            / /| |    :::::: :::::::
		//           /_/ | |   :::        ::
		//               | |    :::::     ::
		//	           __| |_      :::    ::
		//            |______| ::::::     ::
			case 10:
			System.out.println("              __");
			System.out.println("             /  |  " + text + " WON THE GAME !!");
			System.out.println("            / / |");
			System.out.println("           / /| |    :::::: :::::::");
			System.out.println("          /_/ | |   :::        ::");
			System.out.println("              | |    :::::     ::");
			System.out.println("            __| |_      :::    ::");
			System.out.println("           |______| ::::::     ::");
			break;
		}
		System.out.println('\n');
	}      
}

