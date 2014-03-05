package zingaya.chess.matan;

public class Pos {
	public final int x,y;

	public Pos(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		return "Pos{" +
				"x=" + x +
				", y=" + y +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Pos)) return false;

		Pos pos = (Pos) o;

		if (x != pos.x) return false;
		if (y != pos.y) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = x;
		result = 31 * result + y;
		return result;
	}
}
