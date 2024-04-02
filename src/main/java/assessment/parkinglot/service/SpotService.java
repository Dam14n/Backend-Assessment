package assessment.parkinglot.service;

import assessment.parkinglot.model.Spot;
import assessment.parkinglot.model.SpotType;
import assessment.parkinglot.model.VehicleType;
import assessment.parkinglot.service.exception.NoFreeSpotsException;

import java.util.List;

public interface SpotService {

	List<Spot> getFreeSpots(VehicleType vehicleType) throws NoFreeSpotsException;

	boolean supportVehicleType(VehicleType vehicleType);

	default List<SpotType> spotTypeByVehicleType(VehicleType vehicleType) {
		return switch (vehicleType) {
			case MOTORCYCLE -> List.of(SpotType.MOTORCYCLE);
			case CAR -> List.of(SpotType.COMPACT, SpotType.REGULAR);
			case VAN -> List.of(SpotType.REGULAR);
		};
	}

	long countFreeSpots(VehicleType vehicleTypeModel);
}