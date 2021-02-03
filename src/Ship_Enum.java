
public enum Ship_Enum {
	AIRCRAFT_CARRIER('A', 5), 
	BATTLESHIP('B', 4), 
	SUBMARINE('S', 3), 
	CRUISER('C', 3), 
	DESTROYER('D', 2),
	
	WATER(' ', 1),
	SHOT('X', 1),
	SUNK('X', 1),
	EMPTY('O', 1);
	
	private final char id;
	private final int length;
	
	private Ship_Enum(char id, int length) {
		this.id = id;
		this.length = length;
	}
	
	public int getLength() {
		return length;
	}
	
	public char getId() {
		return id;
	}
}


