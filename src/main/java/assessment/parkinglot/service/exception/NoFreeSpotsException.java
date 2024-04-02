package assessment.parkinglot.service.exception;

public class NoFreeSpotsException extends Exception {

	public NoFreeSpotsException() {
		super("No free spots available");
	}
}