
public class Ship {

	private Coordinates startShipCoords;
	private Coordinates endShipCoords;
	private int length;
	private int lifeLeft;
	private Ship_Enum id;
	
	public Ship(Ship_Enum ship) {
		this.id = ship; 
		this.length = ship.getLength();
		this.lifeLeft = length;
		this.startShipCoords = new Coordinates(-1, -1);
		this.endShipCoords = new Coordinates(-1, -1);
	}

	public Ship_Enum getId() {
		return id;
	}
	
	public int getLength() {
		return length;
	}
	
	public int getLifeLeft() {
		return lifeLeft;
	}
	
	public void decLifeLeft() {
		lifeLeft--;
	}
	
	public boolean isSunk() {
		return lifeLeft == 0;
	}
	
	public void setCoords(Coordinates startShipCoords, Coordinates endShipCoords) {
		this.startShipCoords.setCoords(startShipCoords);
		this.endShipCoords.setCoords(endShipCoords);
	}
	
	public Coordinates getStartShipCoords() {
		return startShipCoords;
	}

	public Coordinates getEndShipCoords() {
		return endShipCoords;
	}
}
