package assessment.parkinglot.model;

import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static assessment.parkinglot.model.VehicleType.CAR;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Car extends Vehicle {

	public Car(String identification) {
		super(identification);
	}

	@Override
	public VehicleType getType() {
		return CAR;
	}
}