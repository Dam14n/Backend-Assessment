package assessment.parkinglot.model;

import lombok.Getter;

@Getter
public enum VehicleType {
	MOTORCYCLE(1),
	CAR(1),
	VAN(3);

	private final int spotsSize;

	VehicleType(int spotsSize) {
		this.spotsSize = spotsSize;
	}
}