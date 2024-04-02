package assessment.parkinglot.service.exception;

public class VehicleIdentificationNotFoundException extends Exception {

	public VehicleIdentificationNotFoundException() {
		super("Vehicle Identification Not Found");
	}
}