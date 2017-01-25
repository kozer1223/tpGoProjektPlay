/**
 * 
 */
package models.game;

/**
 * @author Kacper
 *
 */
public class InvalidMoveException extends Exception {


	private static final long serialVersionUID = -6687156793420175471L;

	/**
	 * @param message Invalid move explanation message.
	 */
	public InvalidMoveException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

}
