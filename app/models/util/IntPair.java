package models.util;

/**
 * @author Kacper
 *
 */
public class IntPair {

	public final int x;
	public final int y;

	public IntPair(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof IntPair) {
			return equals((IntPair) o);
		} else {
			return super.equals(o);
		}
	}

	public boolean equals(IntPair pair) {
		return (x == pair.x && y == pair.y);
	}

	@Override
	public int hashCode() {
		return 257 * x + y; // dla celow gry bezkolizyjny
	}
}
