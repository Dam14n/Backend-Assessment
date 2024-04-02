package assessment.parkinglot.service;

import assessment.parkinglot.model.Spot;
import assessment.parkinglot.model.VehicleType;
import assessment.parkinglot.repository.SpotRepository;
import assessment.parkinglot.service.exception.NoFreeSpotsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class SingleSpotService implements SpotService {

	private final SpotRepository spotRepository;

	@Override
	public List<Spot> getFreeSpots(VehicleType vehicleType) throws NoFreeSpotsException {
		var spotTypes = this.spotTypeByVehicleType(vehicleType);
		var singleSpot = this.spotRepository
				.findFirstByVehicleIsNullAndTypeIn(spotTypes)
				.orElseThrow(NoFreeSpotsException::new);
		return List.of(singleSpot);
	}

	@Override
	public boolean supportVehicleType(VehicleType vehicleType) {
		var supportedVehicleTypes = List.of(VehicleType.CAR, VehicleType.MOTORCYCLE);
		return supportedVehicleTypes.contains(vehicleType);
	}

	@Override
	public long countFreeSpots(VehicleType vehicleType) {
		var spotTypes = this.spotTypeByVehicleType(vehicleType);
		return this.spotRepository.countByVehicleIsNullAndTypeIn(spotTypes);
	}
}