package models.game.msg;

public class Score {
	
	private final double score1;
	private final double score2;

	public Score(double score1, double score2) {
		this.score1 = score1;
		this.score2 = score2;
	}

	public double getScore1() {
		return score1;
	}

	public double getScore2() {
		return score2;
	}

}
