
public class Grid {

	final static int GRID_ROWS = 10;
	final static int GRID_COLS = 10;
	
	private Ship_Enum[][] panels;
 
	private Ship AircraftCarrier;
	private Ship Battleship;
	private Ship Cruiser;
	private Ship Submarine;
	private Ship Destroyer;
	
	public Grid() {
		panels = new Ship_Enum[GRID_ROWS][GRID_COLS];
		//Llenar con WATER
		for(int i = 0; i < GRID_ROWS; i++) {
			for(int j = 0; j < GRID_COLS; j++) {
				panels[i][j] = Ship_Enum.WATER;
			}
		}
		
		AircraftCarrier = new Ship(Ship_Enum.AIRCRAFT_CARRIER);
		Battleship = new Ship(Ship_Enum.BATTLESHIP);
		Cruiser = new Ship(Ship_Enum.CRUISER);
		Submarine = new Ship(Ship_Enum.SUBMARINE);
		Destroyer = new Ship(Ship_Enum.DESTROYER);
	}

	public Ship getAircraftCarrier() {
		return AircraftCarrier;
	}

	public Ship getBattleship() {
		return Battleship;
	}

	public Ship getCruiser() {
		return Cruiser;
	}

	public Ship getSubmarine() {
		return Submarine;
	}

	public Ship getDestroyer() {
		return Destroyer;
	}
	
	//Devuelve las no hundidas
	public Ship[] getShips() {
		int length = (AircraftCarrier.isSunk() ? 0 : 1) + (Battleship.isSunk() ? 0 : 1) + (Cruiser.isSunk() ? 0 : 1) + 
				(Submarine.isSunk() ? 0 : 1) + (Destroyer.isSunk() ? 0 : 1);
		int pos = 0;
		Ship[] res = new Ship[length];
		if (!AircraftCarrier.isSunk()) {
			res[pos] = AircraftCarrier;
			pos++;
		}
		if (!Battleship.isSunk()) {
			res[pos] = Battleship;
			pos++;
		}
		if (!Cruiser.isSunk()) {
			res[pos] = Cruiser;
			pos++;
		}
		if (!Submarine.isSunk()) {
			res[pos] = Submarine;
			pos++;
		}
		if (!Destroyer.isSunk()) {
			res[pos] = Destroyer;
		}
		return res;
	}

	//Para la GUI
	public Ship_Enum[][] getPanels() {
		return panels;
	}

	public void setPos(int x, int y, Ship_Enum newShip) {
		panels[x][y] = newShip;
	}

	public Ship_Enum getPos(int x, int y) {
		return panels[x][y];
	}
	
	public boolean isDead() {
		return AircraftCarrier.isSunk() && Battleship.isSunk() && Cruiser.isSunk() && Submarine.isSunk() && Destroyer.isSunk();
	}

}
