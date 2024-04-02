package assessment.parkinglot.model;

import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Van extends Vehicle {

	public Van(String identification) {
		super(identification);
	}

	@Override
	public VehicleType getType() {
		return VehicleType.VAN;
	}

}