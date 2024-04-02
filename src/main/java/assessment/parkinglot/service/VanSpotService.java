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
public class VanSpotService implements SpotService {

	private final SpotRepository spotRepository;

	@Override
	public List<Spot> getFreeSpots(VehicleType vehicleType) throws NoFreeSpotsException {
		var spotTypes = this.spotTypeByVehicleType(vehicleType);
		var spots = this.spotRepository.findTop3ByVehicleIsNullAndTypeIn(spotTypes);
		if (spots.size() < vehicleType.getSpotsSize()) {
			throw new NoFreeSpotsException();
		}
		return spots;
	}

	@Override
	public boolean supportVehicleType(VehicleType vehicleType) {
		var supportedVehicleTypes = List.of(VehicleType.VAN);
		return supportedVehicleTypes.contains(vehicleType);
	}

	@Override
	public long countFreeSpots(VehicleType vehicleType) {
		var spotTypes = this.spotTypeByVehicleType(vehicleType);
		return this.spotRepository.countByVehicleIsNullAndTypeIn(spotTypes) / vehicleType.getSpotsSize();
	}
}