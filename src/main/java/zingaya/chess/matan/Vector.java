package zingaya.chess.matan;

public abstract class Vector {

	public final int c1;
	public final int c2;

	public Vector(int x1, int x2) {
		if (x1 > x2){
			this.c1 = x2;
			this.c2 = x1;
		}
		else {
			this.c1 = x1;
			this.c2 = x2;
		}
	}

	public boolean isStill(){
		return c1 == c2;
	}

	public abstract boolean contains(Pos p);
}
