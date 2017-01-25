package models.game.msg;

public class LabeledBoard {
	
	final int[][] labeledBoard;

	public LabeledBoard(int[][] labeledBoard) {
		this.labeledBoard = labeledBoard;
	}

	public int[][] getLabeledBoard() {
		return labeledBoard;
	}

}
