package assessment.parkinglot.controller;

import assessment.parkinglot.api.dto.FreeSpotsResponse;
import assessment.parkinglot.api.dto.ParkLeave;
import assessment.parkinglot.api.dto.ParkResponse;
import assessment.parkinglot.api.dto.ParkVehicle;
import assessment.parkinglot.api.dto.VehicleTypeRequest;
import assessment.parkinglot.model.Spot;
import assessment.parkinglot.model.SpotType;
import assessment.parkinglot.model.VehicleType;
import assessment.parkinglot.provider.VehicleProvider;
import assessment.parkinglot.repository.SpotRepository;
import assessment.parkinglot.service.exception.NoFreeSpotsException;
import assessment.parkinglot.service.exception.VehicleIdentificationNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ParkingOperationsApiDelegate implements assessment.parkinglot.api.ParkingOperationsApiDelegate {

	private final SpotRepository spotRepository;
	private final VehicleProvider vehicleProvider;

	private List<SpotType> spotTypeByVehicleType(VehicleType vehicleType) {
		return switch (vehicleType) {
			case MOTORCYCLE -> List.of(SpotType.MOTORCYCLE);
			case CAR -> List.of(SpotType.COMPACT, SpotType.REGULAR);
			case VAN -> List.of(SpotType.REGULAR);
		};
	}


	@Override
	public ResponseEntity<ParkResponse> parkVehicle(ParkVehicle parkVehicle) throws NoFreeSpotsException {
		var vehicleType = VehicleType.valueOf(parkVehicle.getVehicleType().getValue());
		var spotTypes = this.spotTypeByVehicleType(vehicleType);
		List<Spot> spots = new ArrayList<>();

		if (VehicleType.VAN.equals(vehicleType)) {
			spots = this.spotRepository.findTop3ByVehicleIsNullAndTypeIn(spotTypes);
			if (spots.size() < 3) {
				throw new NoFreeSpotsException();
			}
		} else {
			var singleSpot = this.spotRepository
					.findFirstByVehicleIsNullAndTypeIn(spotTypes)
					.orElseThrow(NoFreeSpotsException::new);
			spots.add(singleSpot);
		}

		var vehicle = this.vehicleProvider.provideVehicle(vehicleType, parkVehicle.getIdentification());
		spots.forEach(spot -> spot.setVehicle(vehicle));
		spotRepository.saveAll(spots);

		var reponse = new ParkResponse();
		reponse.setSpotIds(spots.stream().map(Spot::getId).toList());
		return ResponseEntity.ok(reponse);
	}

	@Override
	public ResponseEntity<FreeSpotsResponse> remainingSpots() {
		var count = this.spotRepository.countByVehicleIsNull();

		var reponse = new FreeSpotsResponse();
		reponse.setFreeSpots(count);
		return ResponseEntity.ok(reponse);
	}

	@Override
	public ResponseEntity<FreeSpotsResponse> remainingSpotsByVehicleType(VehicleTypeRequest vehicleType) {
		var vehicleTypeModel = VehicleType.valueOf(vehicleType.getValue());
		var spotTypes = this.spotTypeByVehicleType(vehicleTypeModel);
		var count = this.spotRepository.countByVehicleIsNullAndTypeIn(spotTypes);


		if (VehicleType.VAN.equals(vehicleTypeModel)) {
			count = count / 3;
		}

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