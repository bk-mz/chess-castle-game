package zingaya.chess.matan;

public class HorzVector extends Vector {
	public final int y;

	public HorzVector(int x1, int x2, int y) {
		super(x1, x2);
		this.y = y;
	}

	public boolean contains(Pos p) {
		return p.y == y && c1 <= p.x && p.x <= c2;
	}

	@Override
	public String toString() {
		return "Horizontal Move {" +
				"y=" + y +
				" x1=" + c1 +
				" x2=" + c2 +
				"} ";
	}
}
