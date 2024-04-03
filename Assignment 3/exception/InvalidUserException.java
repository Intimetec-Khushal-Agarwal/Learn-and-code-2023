package exception;

public class InvalidUserException extends RuntimeException {

	private static final long serialVersionUID = 264793259077740058L;
	
	public InvalidUserException(String message) {
		super(message);
	}
}
