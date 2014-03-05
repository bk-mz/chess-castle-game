package zingaya.chess.matan;

public class VertVector extends Vector {
	public final int x;

	public VertVector(int y1, int y2, int x) {
		super(y1, y2);
		this.x = x;
	}

	public boolean contains(Pos p) {
		return p.x == x && c1 <= p.y && p.y <= c2;
	}

	@Override
	public String toString() {
		return "Vertical Move {" +
				"x=" + x +
				" y1=" + c1 +
				" y2=" + c2 +
				"} ";
	}
}
