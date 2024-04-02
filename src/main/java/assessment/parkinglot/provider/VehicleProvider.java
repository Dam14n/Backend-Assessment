package assessment.parkinglot.provider;

import assessment.parkinglot.model.Car;
import assessment.parkinglot.model.Motorcycle;
import assessment.parkinglot.model.Van;
import assessment.parkinglot.model.Vehicle;
import assessment.parkinglot.model.VehicleType;
import org.springframework.stereotype.Component;

@Component
public class VehicleProvider {

	/***
	 * Generate a new Vehicle instance
	 *
	 * @param type of the new Vehicle
	 * @param identification of the new Vehicle
	 * @return the generated instance
	 */
	public Vehicle provideVehicle(VehicleType type, String identification) {
		return switch (type) {
			case CAR -> new Car(identification);
			case MOTORCYCLE -> new Motorcycle(identification);
			case VAN -> new Van(identification);
		};
	}
}