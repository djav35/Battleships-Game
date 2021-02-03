/*
import java.util.ArrayList;
import java.util.Random;

 10x10 Grid: A-J x 1-10
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
  

public class BadOldEngine {

	//Grids
	private Grid playerGrid;
	private Grid cpuGrid;
	private Grid shootingGrid;

	//Para la cpu elegir donde disparar
	private ArrayList<Coordinates> pool;
	private ArrayList<Coordinates> improvedPool;

	//Para ayudar a la cpu  a disparar a una casilla adyacente
	private Coordinates prevHit;
	private ArrayList<Coordinates> adjacentPool;

	public BadOldEngine() {
		playerGrid = new Grid();
		cpuGrid = new Grid();
		shootingGrid = new Grid();

		pool = new ArrayList<Coordinates>();
		for (int i = 0; i < Grid.GRID_ROWS; i++) {
			for (int j = 0; j < Grid.GRID_COLS; j++) {
				pool.add(new Coordinates(i,j));
			}
		}
		
		//Elegimos unos de los 2 posibles patrones mejorados de disparo 
		Random rand = new Random();
		int choose = rand.nextInt(2);
		improvedPool = new ArrayList<Coordinates>();
		for (int i = 0; i < Grid.GRID_ROWS; i++) {
			for (int j = 0; j < Grid.GRID_COLS; j++) {
				if ((i + j) % 2 == choose) improvedPool.add(new Coordinates(i,j));
			}
		}

		//-1 indica que no hubo Hit en el disparo anterior
		prevHit = new Coordinates(-1,-1);
		adjacentPool = new ArrayList<Coordinates>();

		//Colocamos aleatoriamente las naves de la cpu
		for (Ship_Enum ship : Ship_Enum.values()) {
			if (!ship.equals(Ship_Enum.WATER) && !ship.equals(Ship_Enum.SHOT) && !ship.equals(Ship_Enum.EMPTY)) {

				boolean found = false;
				while (!found) {

					//Aleatorizar posicion inicial
					int index = rand.nextInt(pool.size());
					Coordinates startShipCoords = pool.get(index);

					//Posibles posiciones finales
					Coordinates[] possibleEndShip  = startShipCoords.adjacents(ship.getLength());

					//Aleatorizar cual de ellas elegimos para empezar
					int n = rand.nextInt(possibleEndShip.length);
					Coordinates endShipCoords = possibleEndShip[n];

					//Recorremos todas las opciones
					for (int i = 0; i < possibleEndShip.length && !found; i++) {
						found = placeShipIfPossible(ship, startShipCoords, endShipCoords, cpuGrid);
						n = (n + 1) % possibleEndShip.length;
						endShipCoords = possibleEndShip[n];
					}
				}
			}
		}
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


	//True: sin errores   False: error
	public boolean buildGrid(Ship_Enum ship, String startShip, String endShip) {

		Coordinates startShipCoords = Coordinates.str2Coords(startShip);
		Coordinates endShipCoords = Coordinates.str2Coords(endShip);

		return placeShipIfPossible(ship, startShipCoords, endShipCoords, playerGrid);	
	}

	public boolean placeShipIfPossible(Ship_Enum ship, Coordinates startShipCoords, Coordinates endShipCoords, Grid grid) {

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
				if (!grid.getPos(i, j).equals(Ship_Enum.WATER)) return false;
			}
		}

		//Metemos la nave
		for (int i = startShipCoords.getX(); i <= difX + startShipCoords.getX(); i++) {
			for (int j = startShipCoords.getY(); j <= difY + startShipCoords.getY(); j++) {
				grid.setPos(i, j, ship);
			}
		}
		return true;
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
				cpuGrid.decnAircraftCarrier();
				shootingGrid.decnAircraftCarrier();
				if (cpuGrid.getnAircraftCarrier() == 0) GUI.printStyle("Hit and sunk!", 3);
				else GUI.printStyle("Hit!", 5);
				break;

			case BATTLESHIP:
				cpuGrid.decnBattleship();
				shootingGrid.decnBattleship();
				if (cpuGrid.getnBattleship() == 0) GUI.printStyle("Hit and sunk!", 3);
				else GUI.printStyle("Hit!", 5);
				break;

			case CRUISER:
				cpuGrid.decnCruiser();
				shootingGrid.decnCruiser();
				if (cpuGrid.getnCruiser() == 0) GUI.printStyle("Hit and sunk!", 3);
				else GUI.printStyle("Hit!", 5);
				break;

			case SUBMARINE:
				cpuGrid.decnSubmarine();
				shootingGrid.decnSubmarine();
				if (cpuGrid.getnSubmarine() == 0)	GUI.printStyle("Hit and sunk!", 3);
				else GUI.printStyle("Hit!", 5);
				break;

			case DESTROYER:
				cpuGrid.decnDestroyer();
				shootingGrid.decnDestroyer();
				if (cpuGrid.getnDestroyer() == 0) GUI.printStyle("Hit and sunk!", 3);
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

	//0: sin errores  1: disparar otra vez  2: victoria cpu
	public int cpuShot() {

		Random rand = new Random();
		//Para coger del pool
		int index = 0;
		Coordinates shotCoords = null;
		//Determinar si va a hacerse un tiro adyacente o no
		boolean adjacentShot = false;

		//Si es disparo consecutivo busca casillas adyacentes		
		if (prevHit.isValid()) {
	
			//Aleatorizar cual se elige de las opciones
			int n = rand.nextInt(adjacentPool.size());
			for (int i = 0; i < adjacentPool.size(); i++) {
				if (pool.contains(adjacentPool.get(n))) {
					index = pool.indexOf(adjacentPool.get(n));
					shotCoords = pool.get(index);
					adjacentShot = true;
					break;
					//Eliminar del adjacentPool las coordenadas no validas
				} else {
					pool.remove(adjacentPool.get(n));
				}
				n = (n+1) % adjacentPool.size();
			}
		}

		if (!adjacentShot) {
			//Puede quedarse vacio sin golpear a todas si el mecanismo de golpeo adyacente no ha funcionado 
			//(ej: naves puestas unas al lado de otras)
			if (improvedPool.isEmpty()) {
				index = rand.nextInt(pool.size());
				shotCoords = pool.get(index);
			} else {
				index = rand.nextInt(improvedPool.size());
				shotCoords = improvedPool.get(index);
			}
			prevHit.invalidate();
		}

		GUI.printStyle("CPU shot at "+ shotCoords.coords2str(), 0);

		//No volver a disparar al mismo sitio
		improvedPool.remove(shotCoords);
		pool.remove(shotCoords);

		//Fallo
		if (playerGrid.getPos(shotCoords.getX(),shotCoords.getY()).equals(Ship_Enum.WATER)) {
			GUI.printStyle("CPU Missed!", 8);
			playerGrid.setPos(shotCoords.getX(), shotCoords.getY(), Ship_Enum.EMPTY);
			return 0;

			//Acierto
		} else {

			//Tomar adyacentes con direccion correcta para proximos disparos
			if (prevHit.isValid()) {
				
				//Eliminar del adjacentPool los que ya no estan en direccion correcta
				for (int i = adjacentPool.size() - 1 ; i >= 0; i--) {
					if (!adjacentPool.get(i).aligned(prevHit, shotCoords)) adjacentPool.remove(i);	
				}
				
				//Meter las adyacentes con direccion correcta del nuevo disparo
				for (Coordinates coords : shotCoords.adjacents()) {
					if (coords.aligned(prevHit, shotCoords) && !coords.equals(prevHit) && !coords.equals(shotCoords)) 
						adjacentPool.add(coords);
				}

				//Tomar adyacentes para proximos disparos
			} else {
				adjacentPool.clear();
				for (Coordinates adjacent : shotCoords.adjacents()) adjacentPool.add(adjacent);
			}

			prevHit.setCoords(shotCoords);

			switch (playerGrid.getPos(shotCoords.getX(), shotCoords.getY())) {
			case AIRCRAFT_CARRIER:
				playerGrid.decnAircraftCarrier();
				if (playerGrid.getnAircraftCarrier() == 0) {
					prevHit.invalidate();
					GUI.printStyle("CPU Hit and sunk!", 9);
				} else {
					GUI.printStyle("CPU Hit!", 7);
				}
				break;

			case BATTLESHIP:
				playerGrid.decnBattleship();
				if (playerGrid.getnBattleship() == 0) {
					prevHit.invalidate();
					GUI.printStyle("CPU Hit and sunk!", 9);
				} else {
					GUI.printStyle("CPU Hit!", 7);
				}
				break;

			case CRUISER:
				playerGrid.decnCruiser();
				if (playerGrid.getnCruiser() == 0) {
					GUI.printStyle("CPU Hit and sunk!", 9);
					prevHit.invalidate();
				} else {
					GUI.printStyle("CPU Hit!", 7);
				}
				break;

			case SUBMARINE:
				playerGrid.decnSubmarine();
				if (playerGrid.getnSubmarine() == 0) {
					GUI.printStyle("CPU Hit and sunk!", 9);
					prevHit.invalidate();
				} else {
					GUI.printStyle("CPU Hit!", 7);
				}
				break;

			case DESTROYER:
				playerGrid.decnDestroyer();
				if (playerGrid.getnDestroyer() == 0) {
					GUI.printStyle("CPU Hit and sunk!", 9);
					prevHit.invalidate();
				} else {
					GUI.printStyle("CPU Hit!", 7);
				}
				break;

			default:
				break;
			}

			playerGrid.setPos(shotCoords.getX(), shotCoords.getY(), Ship_Enum.SHOT);

			//victoria de la cpu
			if (playerGrid.isDead()) return 2;
			//disparar otra vez
			else return 1;
		}
	}
	
	
}
*/
