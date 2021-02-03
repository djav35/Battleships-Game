import java.util.ArrayList;
import java.util.Random;

/* 10x10 Grid: A-J x 1-10
 * 
 * A
 * B
 * C
 * D
 * E
 * F
 * G
 * H
 * I
 * J
 *   1 2 3 4 5 6 7 8 9 10
 *   
 *   
 * Aircraft Carrier AAAAA
 * Battleship       BBBB
 * Submarine        SSS
 * Cruiser          CCC
 * Destroyer        DD
 */

public class Engine {

	//Grids
	private Grid playerGrid;
	private Grid cpuGrid;
	private Grid shootingGrid;
	private Grid cpuShootingGrid;
	
	//Para elecciones aleatorias
	private Random seed;
	
	//Estrategias de disparo de cpu
	private boolean[] adjacentShot;
	private final int parity;
	
	public Engine() {
		playerGrid = new Grid();
		cpuGrid = new Grid();
		shootingGrid = new Grid();
		cpuShootingGrid = new Grid();	
		seed = new Random();
		adjacentShot = new boolean[5];
		for (int i = 0; i < 5; i++) adjacentShot[i] = false;
		parity = seed.nextInt(2);
		//Crear el grid aleatorio de la cpu
		buildRandomGrid(false);
	}

	public Grid getPlayerGrid() {
		return playerGrid;
	}

	public Grid getCpuGrid() {
		return cpuGrid;
	}

	public Grid getShootingGrid() {
		return shootingGrid;
	}

	public Grid getCpuShootingGrid() {
		return cpuShootingGrid;
	}

	private boolean fitsIn(Ship ship, Coordinates startShipCoords, Coordinates endShipCoords, Grid grid) {
		if (!startShipCoords.isValid() || !endShipCoords.isValid()) return false;
	
		//Asegurarnos que "van de menor (start) a mayor (end)"
		if (endShipCoords.getX() - startShipCoords.getX() < 0) {
			Coordinates.swapX(endShipCoords, startShipCoords);
		}
		if (endShipCoords.getY() - startShipCoords.getY() < 0) {
			Coordinates.swapY(endShipCoords, startShipCoords);	
		}
	
		int difX = endShipCoords.getX() - startShipCoords.getX();
		int difY = endShipCoords.getY() - startShipCoords.getY();
	
		//Si estan en diagonal
		if (difX != 0 && difY != 0) return false;
	
		//Si la longitud no es la correspondiente a la nave
		if (ship.getLength() != difX + 1  && ship.getLength() != difY + 1) return false;
	
		//Verificamos las casillas (funciona a la vez para vertical como horizontal)
		for (int i = startShipCoords.getX(); i <= difX + startShipCoords.getX(); i++) {
			for (int j = startShipCoords.getY(); j <= difY + startShipCoords.getY(); j++) {
				//(Si alguna casilla esta SHOT nos vale, devuelve true, para poder usar este metodo para el analisis probabilistico)
				if (!grid.getPos(i, j).equals(Ship_Enum.WATER) && !grid.getPos(i, j).equals(Ship_Enum.SHOT)) return false;
			}
		}
		return true;
	}

	private void placeShip(Ship ship, Coordinates startShipCoords, Coordinates endShipCoords, Grid grid) {		
		//Ponemos las coordenadas en el objeto nave
		ship.setCoords(startShipCoords, endShipCoords);	
		
		int difX = endShipCoords.getX() - startShipCoords.getX();
		int difY = endShipCoords.getY() - startShipCoords.getY();
		
		//Metemos la nave en el grid
		for (int i = startShipCoords.getX(); i <= difX + startShipCoords.getX(); i++) {
			for (int j = startShipCoords.getY(); j <= difY + startShipCoords.getY(); j++) {
				grid.setPos(i, j, ship.getId());
			}
		}
	}

	//Parametro true solo para el modo autoplay
	public void buildRandomGrid(boolean isPlayerGrid) {
		Grid grid = cpuGrid;
		if (isPlayerGrid) grid = playerGrid;
		
		ArrayList<Coordinates> pool = new ArrayList<Coordinates>();
		for (int i = 0; i < Grid.GRID_ROWS; i++) {
			for (int j = 0; j < Grid.GRID_COLS; j++) {
				//Evita empezar en posiciones centrales...
				if (!( (i == 4 || i == 5) && (j == 4 || j == 5) )) pool.add(new Coordinates(i,j));
			}
		}
		
		//Colocamos aleatoriamente las naves del jugador
		for (Ship ship : grid.getShips()) {
			boolean found = false;
			while (!found) {

				//Aleatorizar posicion inicial
				Coordinates startShipCoords = chooseRandomly(pool);
				//Coordinates startShipCoords = chooseWeightedRandomly(weightedPool);

				//Posibles posiciones finales
				Coordinates[] possibleEndShip  = startShipCoords.alignedAtDistance(ship.getLength() - 1);

				//Aleatorizar cual de ellas elegimos para empezar
				int n = seed.nextInt(possibleEndShip.length);
				Coordinates endShipCoords = possibleEndShip[n];
	
				//Recorremos todas las opciones
				for (int i = 0; i < possibleEndShip.length && !found; i++) {
					if (fitsIn(ship, startShipCoords, endShipCoords, grid)) {
						placeShip(ship, startShipCoords, endShipCoords, grid);
						found = true;
						//Pone a cero la probabilidad de elegir esa casilla
						pool.remove(startShipCoords);
					}
					n = (n + 1) % possibleEndShip.length;
					endShipCoords = possibleEndShip[n];			
				}
			}
		}
	}

	//True: sin errores   False: error
	public boolean buildGrid(Ship ship, String startShip, String endShip) {

		Coordinates startShipCoords = Coordinates.str2Coords(startShip);
		Coordinates endShipCoords = Coordinates.str2Coords(endShip);

		if (fitsIn(ship, startShipCoords, endShipCoords, playerGrid)) {
			placeShip(ship, startShipCoords, endShipCoords, playerGrid);
			return true;
		}
		return false;
	}

	//0: sin errores  1: disparar otra vez  2: victoria jugador  3: error
	public int playerShot(String input) {
		Coordinates shotCoords = Coordinates.str2Coords(input);

		//Mirar si esta dentro del grid y no se habia disparado a esa casilla
		if (!shotCoords.isValid() || shootingGrid.getPos(shotCoords.getX(), shotCoords.getY()).equals(Ship_Enum.SHOT)
				|| shootingGrid.getPos(shotCoords.getX(), shotCoords.getY()).equals(Ship_Enum.EMPTY)) return 3;

		System.out.print('\n');

		//Fallo
		if (cpuGrid.getPos(shotCoords.getX(), shotCoords.getY()).equals(Ship_Enum.WATER)) {
			GUI.printStyle("Miss!", 6);
			shootingGrid.setPos(shotCoords.getX(), shotCoords.getY(), Ship_Enum.EMPTY);
			return 0;

			//Acierto
		} else {
			switch (cpuGrid.getPos(shotCoords.getX(),shotCoords.getY())) {
			
			case AIRCRAFT_CARRIER:
				cpuGrid.getAircraftCarrier().decLifeLeft();
				shootingGrid.getAircraftCarrier().decLifeLeft();
				if (cpuGrid.getAircraftCarrier().isSunk()) GUI.printStyle("Hit and sunk!", 3);
				else GUI.printStyle("Hit!", 5);
				break;

			case BATTLESHIP:
				cpuGrid.getBattleship().decLifeLeft();
				shootingGrid.getBattleship().decLifeLeft();
				if (cpuGrid.getBattleship().isSunk()) GUI.printStyle("Hit and sunk!", 3);
				else GUI.printStyle("Hit!", 5);
				break;

			case CRUISER:
				cpuGrid.getCruiser().decLifeLeft();
				shootingGrid.getCruiser().decLifeLeft();
				if (cpuGrid.getCruiser().isSunk()) GUI.printStyle("Hit and sunk!", 3);
				else GUI.printStyle("Hit!", 5);
				break;

			case SUBMARINE:
				cpuGrid.getSubmarine().decLifeLeft();
				shootingGrid.getSubmarine().decLifeLeft();
				if (cpuGrid.getSubmarine().isSunk()) GUI.printStyle("Hit and sunk!", 3);
				else GUI.printStyle("Hit!", 5);
				break;

			case DESTROYER:
				cpuGrid.getDestroyer().decLifeLeft();
				shootingGrid.getDestroyer().decLifeLeft();
				if (cpuGrid.getDestroyer().isSunk()) GUI.printStyle("Hit and sunk!", 3);
				else GUI.printStyle("Hit!", 5);
				break;

			default:
				break;
			}
			
			shootingGrid.setPos(shotCoords.getX(), shotCoords.getY(), Ship_Enum.SHOT);

			//victoria del jugador
			if (cpuGrid.isDead()) return 2;
			//disparar otra vez
			else return 1;

		}
	}

	private ArrayList<Coordinates> maxChance(int[][] likelyhoodGrid) {
		ArrayList<Coordinates> res = new ArrayList<Coordinates>(); 
		int max = 0;
		//Encontrar el maximo
		for (int i = 0; i < likelyhoodGrid[0].length; i++) {
			for (int j = 0; j < likelyhoodGrid.length; j++) {
				if (likelyhoodGrid[i][j] > max) {
					max = likelyhoodGrid[i][j];
				}
			}
		}
		//Guardar todos los maximos
		for (int i = 0; i < likelyhoodGrid[0].length; i++) {
			for (int j = 0; j < likelyhoodGrid.length; j++) {
				if (likelyhoodGrid[i][j] == max) {
					res.add(new Coordinates(i, j));
				}
			}
		}
		return res;
	}

	private Coordinates chooseRandomly(ArrayList<Coordinates> pool) {
		int index = seed.nextInt(pool.size());
		return pool.get(index);
	}
	
	//Hundir nave en el grid
	private void sinkPanels(Ship ship) {
		int difX = ship.getEndShipCoords().getX() - ship.getStartShipCoords().getX();
		int difY = ship.getEndShipCoords().getY() - ship.getStartShipCoords().getY();
		for (int i = ship.getStartShipCoords().getX(); i <= difX + ship.getStartShipCoords().getX(); i++) {
			for (int j = ship.getStartShipCoords().getY(); j <= difY + ship.getStartShipCoords().getY(); j++) {
				playerGrid.setPos(i, j, Ship_Enum.SUNK);
				cpuShootingGrid.setPos(i, j, Ship_Enum.SUNK);
			}
		}
	}
	
	//Mira cuantas casillas SHOT hay en la trayectoria
	private int nShotIn(Coordinates startShipCoords, Coordinates endShipCoords, Grid grid) {
		int n = 0;
		int difX = endShipCoords.getX() - startShipCoords.getX();
		int difY = endShipCoords.getY() - startShipCoords.getY();
		
		for (int i = startShipCoords.getX(); i <= difX + startShipCoords.getX(); i++) {
			for (int j = startShipCoords.getY(); j <= difY + startShipCoords.getY(); j++) {
				if (grid.getPos(i, j).equals(Ship_Enum.SHOT)) n++;
			}
		}
		return n;
	}

	//Elimina las que no tengan la paridad determinada al inicio de la partida
	private void parityFilter(ArrayList<Coordinates> pool) {
		for (int i = pool.size() - 1; i >= 0; i--) {
			if (pool.size() < 1 && (pool.get(i).getX() + pool.get(i).getY()) % 2 != parity) pool.remove(i);
		}
	}

	//0: sin errores  1: disparar otra vez  2: victoria cpu
	public int cpuShot() {
		Coordinates shotCoords = null;

		//Analisis probabilistico
		int[][] likelyhoodGrid = new int[Grid.GRID_ROWS][Grid.GRID_COLS];
		Coordinates startShipCoords = null;
		Coordinates endShipCoords = null;

		//Mirar todas las colocaciones posibles de cada nave no hundida
		for (Ship ship : playerGrid.getShips()) {
			
			//En horizontal
			for (int i = 0; i < Grid.GRID_ROWS; i++) {
				for (int j = 0; j < Grid.GRID_COLS - (ship.getLength() - 1); j++) {
					startShipCoords = new Coordinates(i, j);
					endShipCoords = new Coordinates(i, j + (ship.getLength() - 1));

					if (fitsIn(ship, startShipCoords, endShipCoords, cpuShootingGrid)) {
						for (int k = j; k < j + ship.getLength(); k++) {
							// Probabilidad 0 si la casilla esta SHOT 
							if (!cpuShootingGrid.getPos(i, k).equals(Ship_Enum.SHOT)) {
								// +1 o +10 * numero de casillas SHOT por las que pasa 	
								likelyhoodGrid[i][k] = likelyhoodGrid[i][k] + 1 
										+ 10 * nShotIn(startShipCoords, endShipCoords, cpuShootingGrid);
							}
						}
					}
				}
			}

			//En vertical
			for (int i = 0; i < Grid.GRID_COLS; i++) {
				for (int j = 0; j < Grid.GRID_ROWS - (ship.getLength() - 1); j++) {
					startShipCoords = new Coordinates(j, i);
					endShipCoords = new Coordinates(j + (ship.getLength() - 1), i);

					if (fitsIn(ship, startShipCoords, endShipCoords, cpuShootingGrid)) {
						for (int k = j; k < j + ship.getLength(); k++) {	
							// Probabilidad 0 si la casilla esta SHOT 
							if (!cpuShootingGrid.getPos(k, i).equals(Ship_Enum.SHOT)) {	
								// +1 o +10 * numero de casillas SHOT por las que pasa	
								likelyhoodGrid[k][i] = likelyhoodGrid[k][i] + 1 
										+ 10 * nShotIn(startShipCoords, endShipCoords, cpuShootingGrid);;
							}
						}
					}
				}
			}
		}

		/*Imprime la probailidad de cada casilla
		 * for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				System.out.print(likelyhoodGrid[i][j] + "  ");
			}
			System.out.print('\n');
		}*/

		//Elegir una de entre las mas probables
		ArrayList<Coordinates> pool = maxChance(likelyhoodGrid);
		
		//Si no es un disparo adyacente, pasarle el filtro de paridad a los posibles disparos
		if (!adjacentShot[0] && !adjacentShot[1] && !adjacentShot[2] && !adjacentShot[3] && !adjacentShot[4]) parityFilter(pool);
		
		shotCoords = chooseRandomly(pool);

		GUI.printStyle("CPU shot at "+ shotCoords.coords2str(), 0);

		//Fallo
		if (playerGrid.getPos(shotCoords.getX(), shotCoords.getY()).equals(Ship_Enum.WATER)) {
			GUI.printStyle("CPU Missed!", 8);
			playerGrid.setPos(shotCoords.getX(), shotCoords.getY(), Ship_Enum.EMPTY);
			cpuShootingGrid.setPos(shotCoords.getX(), shotCoords.getY(), Ship_Enum.EMPTY);
			return 0;

			//Acierto
		} else {
			switch (playerGrid.getPos(shotCoords.getX(), shotCoords.getY())) {
			
			case AIRCRAFT_CARRIER:
	
				//Decrementar la vida de la nave
				playerGrid.getAircraftCarrier().decLifeLeft();
				cpuShootingGrid.getAircraftCarrier().decLifeLeft();
				
				//Si se ha hundido, cambiar a hundido todos las casillas de esa nave y no buscar disparo adyacente a esa nave
				if (playerGrid.getAircraftCarrier().isSunk()) {
					sinkPanels(playerGrid.getAircraftCarrier());
					adjacentShot[0] = false;
					GUI.printStyle("CPU Hit and sunk!", 9);
					
					//Si no se ha hundido, cambiar a SHOT la casilla a la que se ha disparado y buscar disparo adyacente a esa nave
				} else {
					playerGrid.setPos(shotCoords.getX(), shotCoords.getY(), Ship_Enum.SHOT);
					cpuShootingGrid.setPos(shotCoords.getX(), shotCoords.getY(), Ship_Enum.SHOT);
					adjacentShot[0] = true;
					GUI.printStyle("CPU Hit!", 7);
				}
				break;

			case BATTLESHIP:
				playerGrid.getBattleship().decLifeLeft();
				cpuShootingGrid.getBattleship().decLifeLeft();
				if (playerGrid.getBattleship().isSunk()) {
					sinkPanels(playerGrid.getBattleship());
					adjacentShot[1] = false;
					GUI.printStyle("CPU Hit and sunk!", 9);
				} else {
					playerGrid.setPos(shotCoords.getX(), shotCoords.getY(), Ship_Enum.SHOT);
					cpuShootingGrid.setPos(shotCoords.getX(), shotCoords.getY(), Ship_Enum.SHOT);
					adjacentShot[1] = true;
					GUI.printStyle("CPU Hit!", 7);
				}
				break;

			case CRUISER:
				playerGrid.getCruiser().decLifeLeft();
				cpuShootingGrid.getCruiser().decLifeLeft();
				if (playerGrid.getCruiser().isSunk()) {
					sinkPanels(playerGrid.getCruiser());
					adjacentShot[2] = false;
					GUI.printStyle("CPU Hit and sunk!", 9);
				} else {
					playerGrid.setPos(shotCoords.getX(), shotCoords.getY(), Ship_Enum.SHOT);
					cpuShootingGrid.setPos(shotCoords.getX(), shotCoords.getY(), Ship_Enum.SHOT);
					adjacentShot[2] = true;
					GUI.printStyle("CPU Hit!", 7);
				}
				break;

			case SUBMARINE:
				playerGrid.getSubmarine().decLifeLeft();
				cpuShootingGrid.getSubmarine().decLifeLeft();
				if (playerGrid.getSubmarine().isSunk()) {
					sinkPanels(playerGrid.getSubmarine());
					adjacentShot[3] = false;
					GUI.printStyle("CPU Hit and sunk!", 9);
				} else {
					playerGrid.setPos(shotCoords.getX(), shotCoords.getY(), Ship_Enum.SHOT);
					cpuShootingGrid.setPos(shotCoords.getX(), shotCoords.getY(), Ship_Enum.SHOT);
					adjacentShot[3] = true;
					GUI.printStyle("CPU Hit!", 7);
				}
				break;

			case DESTROYER:
				playerGrid.getDestroyer().decLifeLeft();
				cpuShootingGrid.getDestroyer().decLifeLeft();
				if (playerGrid.getDestroyer().isSunk()) {
					sinkPanels(playerGrid.getDestroyer());
					adjacentShot[4] = false;
					GUI.printStyle("CPU Hit and sunk!", 9);
				} else {
					playerGrid.setPos(shotCoords.getX(), shotCoords.getY(), Ship_Enum.SHOT);
					cpuShootingGrid.setPos(shotCoords.getX(), shotCoords.getY(), Ship_Enum.SHOT);
					adjacentShot[4] = true;
					GUI.printStyle("CPU Hit!", 7);
				}
				break;

			default:
				break;
			}

			//victoria de la cpu
			if (playerGrid.isDead()) return 2;
			//disparar otra vez
			else return 1;
		}
	}
}
