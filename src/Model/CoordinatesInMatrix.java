package Model;

import java.io.Serializable;

/**
 * Class implementing locations inside PlayerGrid matrix
 * @author Maia
 *
 */
public class CoordinatesInMatrix implements Comparable<CoordinatesInMatrix>, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int line = -1;
	private int column = -1;
	
	public CoordinatesInMatrix(int line, int column) {
		this.line = line;
		this.column = column;
	}
	
	@Override
	public String toString() {
		return "(" + line + ", " + column + ")";
	}

	public int getLine() {
		return line;
	}

	public int getColumn() {
		return column;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + column;
		result = prime * result + line;
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
		CoordinatesInMatrix other = (CoordinatesInMatrix) obj;
		if (column != other.column)
			return false;
		if (line != other.line)
			return false;
		return true;
	}

	/**
	 * Compares coordinates, lower => (0,0)
	 */
	@Override
	public int compareTo(CoordinatesInMatrix o) {
		
		if (o.getLine() == this.getLine() && o.getColumn() < this.getColumn()) {
			return 1;
		} else if (o.getColumn() == this.getColumn() && o.getLine() < this.getLine()) {
			return 1;
		} else if (o.getLine() < this.getLine() && o.getColumn() < this.getColumn()) {
			return 1;
		} else {
			return -1;
		}
	}

}
