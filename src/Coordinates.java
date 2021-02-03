
public class Coordinates {
	private int x;
	private int y;
	
	public Coordinates(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setX(int newX) {
		x = newX;
	}
	
	public void setY(int newY) {
		y = newY;
	}
	
	public void setCoords(Coordinates newCoords) {
		this.setX(newCoords.getX());
		this.setY(newCoords.getY());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Coordinates other = (Coordinates) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

	public boolean isValid() {
		return !(x < 0 || x >= Grid.GRID_ROWS || y < 0 || y >= Grid.GRID_COLS);
	}
	
	//Ej.: C7 -> (3,7) -> (2,6) para ponerlo en 0-9 x 0-9
	public static Coordinates str2Coords(String input) {
		int x = -1 , y = -1;
		x = input.toUpperCase().charAt(0) - 'A';
		//Por si es un formato no valido
		try {
			y = Integer.parseInt(input.substring(1)) - 1;
		} catch (Exception e) {}
		return new Coordinates(x,y);
	}
	
	public String coords2str() {
		return (char)('A' + getX()) + Integer.toString(getY()+1);
	}

	public static void swapX(Coordinates coord1, Coordinates coord2) {
		int tmp = coord1.getX();
		coord1.setX(coord2.getX());
		coord2.setX(tmp);
	}
	
	public static void swapY(Coordinates coord1, Coordinates coord2) {
		int tmp = coord1.getY();
		coord1.setY(coord2.getY());
		coord2.setY(tmp);
	}

	//Para elegir coordenadas finales a partir de iniciales con la longitud de la nave
	public Coordinates[] alignedAtDistance(int offset) {
		Coordinates[] adjacentCoords = {
				new Coordinates(getX() + offset, getY()), new Coordinates(getX() - offset, getY()),
				new Coordinates(getX(), getY() + offset), new Coordinates(getX(), getY() - offset)
		};
		return adjacentCoords;
	}
}
