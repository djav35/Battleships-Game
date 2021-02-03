import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Scanner;


//TODO
/*
 * Mejorar colocación de naves de la AI: evitar adyacencias y evitar el centro
 * Mejorar la GUI
 */
 
public class Launcher {

	public static void main (String[] args) {
		//Autoplay para ver cuanto tarda la cpu en resolver, se puede activar desde consola de comandos
		boolean autoplay = false;
		if (args.length > 0) autoplay = args[0].equalsIgnoreCase("autoplay");

		String playerName = null;
		Scanner sc = null;
		if (!autoplay) {
			sc = new Scanner(System.in);
			System.out.print('\n');
			GUI.printStyle("Type your nickname", 1);
			playerName = sc.next();
		}

		//Se inicializa el juego
		Engine game = new Engine();
		int shotsPlayer = 0;
		int shotsCPU = 0;

		//Introducir naves
		if (!autoplay) {
			GUI.printGrid(game.getPlayerGrid(), playerName);
			for(Ship ship : game.getPlayerGrid().getShips()) {
				GUI.printStyle("Place your "+ship.getId().name()+", which has length "+ship.getLength(), 2);

				String startShip = sc.next();
				String endShip = sc.next();
				while (!game.buildGrid(ship, startShip, endShip)) {
					System.err.println("Error. Try Again");
					startShip = sc.next();
					endShip = sc.next();
				}

				GUI.printGrid(game.getPlayerGrid(), playerName);
			}
		} else game.buildRandomGrid(true);

		//Juego
		while(true) {

			if (!autoplay) {
				//Turno del jugador
				//0: sin errores  1: disparar otra vez  2: victoria jugador  3: error
				int playerState = 0;
				GUI.print2Grids(game.getPlayerGrid(), playerName, game.getShootingGrid(), "CPU");
				do {
					GUI.printStyle("Choose where to shoot", 4);

					String shot = sc.next();
					while ((playerState = game.playerShot(shot)) == 3) {
						System.err.println("Error. Try Again");
						shot = sc.next();
					}
					shotsPlayer++;

					GUI.print2Grids(game.getPlayerGrid(), playerName, game.getShootingGrid(), "CPU");

					//Victoria del jugador, se guarda el numero de disparos
					if (playerState == 2) {
						GUI.printStyle(playerName, 10);
						GUI.printStyle("Game won in " + shotsPlayer + " shots. Score saved.", 0);
						try (FileWriter writer = new FileWriter("scores.txt", true);
								BufferedWriter bw = new BufferedWriter(writer)) { 
							bw.write("Player: "+ playerName + "  Shots: " + shotsPlayer 
									+ "  Date: " + new Timestamp(System.currentTimeMillis()) + '\n');
						} catch (IOException e) {};
						System.exit(0);
					}

				} while (playerState == 1);
			}

			//Turno de la CPU
			//0: sin errores  1: disparar otra vez  2: victoria cpu
			int cpuState = 0;
			do {
				if (!autoplay) {
					GUI.printStyle("CPU is going to shoot", 4);
					//Pausa
					sc.next();
				}

				cpuState = game.cpuShot();
				shotsCPU++;
				if (cpuState != 0 && !autoplay) GUI.print2Grids(game.getPlayerGrid(), playerName, game.getShootingGrid(), "CPU");

				//Victoria de la maquina, se guarda el numero de disparos
				if (cpuState == 2) {
					GUI.printStyle("CPU", 10);
					GUI.printStyle("Game won in " + shotsCPU + " shots. Score saved.", 0);
					try (FileWriter writer = new FileWriter("scores.txt", true);
							BufferedWriter bw = new BufferedWriter(writer)) { 
						bw.write("Player: CPU  Shots: " + shotsCPU + "  Date: " + new Timestamp(System.currentTimeMillis()) +'\n');
					} catch (IOException e) {};
					System.exit(0);
				}

			} while (cpuState == 1);
		}
	}
}
