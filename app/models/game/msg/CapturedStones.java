package models.game.msg;

public class CapturedStones {
	
	private final int capturedStones1;
	private final int capturedStones2;

	public CapturedStones(int capturedStones1, int capturedStones2) {
		this.capturedStones1 = capturedStones1;
		this.capturedStones2 = capturedStones2;
	}

	public int getCapturedStones1() {
		return capturedStones1;
	}

	public int getCapturedStones2() {
		return capturedStones2;
	}

}
