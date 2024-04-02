package assessment.parkinglot.controller;

import assessment.parkinglot.api.dto.FreeSpotsResponse;
import assessment.parkinglot.api.dto.ParkLeave;
import assessment.parkinglot.api.dto.ParkResponse;
import assessment.parkinglot.api.dto.ParkVehicle;
import assessment.parkinglot.api.dto.VehicleTypeRequest;
import assessment.parkinglot.model.Spot;
import assessment.parkinglot.model.VehicleType;
import assessment.parkinglot.provider.VehicleProvider;
import assessment.parkinglot.repository.SpotRepository;
import assessment.parkinglot.service.SpotService;
import assessment.parkinglot.service.exception.NoFreeSpotsException;
import assessment.parkinglot.service.exception.VehicleIdentificationNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ParkingOperationsApiDelegate implements assessment.parkinglot.api.ParkingOperationsApiDelegate {

	private final SpotRepository spotRepository;
	private final VehicleProvider vehicleProvider;
	private final List<SpotService> spotServiceList;

	@Override
	public ResponseEntity<ParkResponse> parkVehicle(ParkVehicle parkVehicle) throws NoFreeSpotsException {
		var vehicleType = VehicleType.valueOf(parkVehicle.getVehicleType().getValue());
		var spots = this.getSpotService(vehicleType).getFreeSpots(vehicleType);

		var vehicle = this.vehicleProvider.provideVehicle(vehicleType, parkVehicle.getIdentification());
		spots.forEach(spot -> spot.setVehicle(vehicle));
		spotRepository.saveAll(spots);

		var reponse = new ParkResponse();
		reponse.setSpotIds(spots.stream().map(Spot::getId).toList());
		return ResponseEntity.ok(reponse);
	}

	private SpotService getSpotService(VehicleType vehicleType) {
		return this.spotServiceList.stream()
				.filter(spotService -> spotService.supportVehicleType(vehicleType))
				.findFirst()
				.orElseThrow(InternalError::new);
	}

	@Override
	public ResponseEntity<FreeSpotsResponse> freeSpots() {
		var count = this.spotRepository.countByVehicleIsNull();

		var reponse = new FreeSpotsResponse();
		reponse.setFreeSpots(count);
		return ResponseEntity.ok(reponse);
	}

	@Override
	public ResponseEntity<FreeSpotsResponse> freeSpotsByVehicleType(VehicleTypeRequest vehicleType) {
		var vehicleTypeModel = VehicleType.valueOf(vehicleType.getValue());
		var count = this.getSpotService(vehicleTypeModel).countFreeSpots(vehicleTypeModel);
		var reponse = new FreeSpotsResponse();
		reponse.setFreeSpots(count);
		return ResponseEntity.ok(reponse);
	}

	@Override
	@Transactional
	public ResponseEntity<ParkVehicle> vehicleLeave(ParkLeave parkLeave) throws VehicleIdentificationNotFoundException {
		var spot = this.spotRepository
				.findFirstByVehicleIdentificationIs(parkLeave.getIdentification())
				.orElseThrow(VehicleIdentificationNotFoundException::new);

		var vehicle = spot.getVehicle();

		spot.setVehicle(null);
		this.spotRepository.save(spot);

		var reponse = new ParkVehicle();
		reponse.setIdentification(vehicle.getIdentification());
		var vehicleType = VehicleTypeRequest.fromValue(vehicle.getType().name());
		reponse.setVehicleType(vehicleType);
		return ResponseEntity.ok(reponse);
	}
}