package assessment.parkinglot;

import assessment.parkinglot.api.dto.VehicleTypeRequest;
import assessment.parkinglot.configuration.ParkingLotConfigurationProperties;
import assessment.parkinglot.controller.ParkingOperationsApiDelegate;
import assessment.parkinglot.service.exception.NoFreeSpotsException;
import assessment.parkinglot.service.exception.VehicleIdentificationNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ParkingLotServiceApplicationTests {

	@Autowired
	private ParkingOperationsApiDelegate parkingService;

	@Autowired
	private ParkingLotConfigurationProperties parkingLotConfigurationProperties;

	private ResponseEntity<assessment.parkinglot.api.dto.ParkResponse> parkVehicle(VehicleTypeRequest type) throws NoFreeSpotsException {
		var parkVehicle = new assessment.parkinglot.api.dto.ParkVehicle();
		parkVehicle.setVehicleType(type);
		parkVehicle.setIdentification("AF111PS");
		return this.parkingService.parkVehicle(parkVehicle);
	}

	@Test
	void parkAMotorcycle() throws NoFreeSpotsException {
		var response = parkVehicle(VehicleTypeRequest.MOTORCYCLE);
		Assertions.assertNotNull(response);
	}

	@Test
	void parkACar() throws NoFreeSpotsException {
		var response = parkVehicle(VehicleTypeRequest.CAR);
		Assertions.assertNotNull(response);
	}

	@Test
	void parkAVan() throws NoFreeSpotsException {
		var response = parkVehicle(VehicleTypeRequest.VAN);
		Assertions.assertNotNull(response);
	}

	@Test
	void parkAVehicleWithNoFreeSpots() throws NoFreeSpotsException {

		for (int i = 0; i < parkingLotConfigurationProperties.getMotorcycle(); i++) {
			this.parkVehicle(VehicleTypeRequest.MOTORCYCLE);
		}

		for (int i = 0; i < parkingLotConfigurationProperties.getCompact() + parkingLotConfigurationProperties.getRegular(); i++) {
			this.parkVehicle(VehicleTypeRequest.CAR);
		}

		var parkVehicle = new assessment.parkinglot.api.dto.ParkVehicle();
		parkVehicle.setVehicleType(VehicleTypeRequest.VAN);
		parkVehicle.setIdentification("AF111PS");
		Assertions.assertThrows(NoFreeSpotsException.class, () -> this.parkingService.parkVehicle(parkVehicle));
	}

	@Test
	void motorcycleLeavesParkSpot() throws NoFreeSpotsException, VehicleIdentificationNotFoundException {
		var parkVehicle = new assessment.parkinglot.api.dto.ParkVehicle();
		parkVehicle.setVehicleType(VehicleTypeRequest.MOTORCYCLE);
		parkVehicle.setIdentification("AF111PS");
		var response = this.parkingService.parkVehicle(parkVehicle);

		var parkLeave = new assessment.parkinglot.api.dto.ParkLeave();
		parkLeave.setIdentification(parkVehicle.getIdentification());
		parkLeave.setSpotIds(response.getBody().getSpotIds());
		var parkVehicleLeave = this.parkingService.vehicleLeave(parkLeave).getBody();
		Assertions.assertNotNull(parkVehicleLeave);
		Assertions.assertEquals(parkVehicle.getIdentification(), parkVehicleLeave.getIdentification());
		Assertions.assertEquals(parkVehicle.getVehicleType(), parkVehicleLeave.getVehicleType());
	}

	@Test
	void carLeavesParkSpot() throws NoFreeSpotsException, VehicleIdentificationNotFoundException  {
		var parkVehicle = new assessment.parkinglot.api.dto.ParkVehicle();
		parkVehicle.setVehicleType(VehicleTypeRequest.CAR);
		parkVehicle.setIdentification("AF111PS");
		var response = this.parkingService.parkVehicle(parkVehicle);

		var parkLeave = new assessment.parkinglot.api.dto.ParkLeave();
		parkLeave.setIdentification(parkVehicle.getIdentification());
		parkLeave.setSpotIds(response.getBody().getSpotIds());
		var parkVehicleLeave = this.parkingService.vehicleLeave(parkLeave).getBody();
		Assertions.assertNotNull(parkVehicleLeave);
		Assertions.assertEquals(parkVehicle.getIdentification(), parkVehicleLeave.getIdentification());
		Assertions.assertEquals(parkVehicle.getVehicleType(), parkVehicleLeave.getVehicleType());
	}

	@Test
	void vanLeavesParkSpot() throws NoFreeSpotsException, VehicleIdentificationNotFoundException {
		var parkVehicle = new assessment.parkinglot.api.dto.ParkVehicle();
		parkVehicle.setVehicleType(VehicleTypeRequest.VAN);
		parkVehicle.setIdentification("AF111PS");
		var response = this.parkingService.parkVehicle(parkVehicle);

		var parkLeave = new assessment.parkinglot.api.dto.ParkLeave();
		parkLeave.setIdentification(parkVehicle.getIdentification());
		parkLeave.setSpotIds(response.getBody().getSpotIds());
		var parkVehicleLeave = this.parkingService.vehicleLeave(parkLeave).getBody();
		Assertions.assertNotNull(parkVehicleLeave);
		Assertions.assertEquals(parkVehicle.getIdentification(), parkVehicleLeave.getIdentification());
		Assertions.assertEquals(parkVehicle.getVehicleType(), parkVehicleLeave.getVehicleType());
	}

	@Test
	void remainingSpots() {
		var freeSpotsResponse = this.parkingService.remainingSpots();
		Assertions.assertEquals(25, freeSpotsResponse.getBody().getFreeSpots());
	}

	@Test
	void remainingSpotsAfterParkingVehicles() throws NoFreeSpotsException {
		for (int i = 0; i < parkingLotConfigurationProperties.getMotorcycle(); i++) {
			this.parkVehicle(VehicleTypeRequest.MOTORCYCLE);
		}

		for (int i = 0; i < parkingLotConfigurationProperties.getCompact(); i++) {
			this.parkVehicle(VehicleTypeRequest.CAR);
		}

		var freeSpotsResponse = this.parkingService.remainingSpots();
		Assertions.assertEquals(parkingLotConfigurationProperties.getRegular(), freeSpotsResponse.getBody().getFreeSpots());
	}

	@Test
	void remainingSpotsByVehicleType() {
		var freeSpotsMotorcycleResponse = this.parkingService.remainingSpotsByVehicleType(VehicleTypeRequest.MOTORCYCLE);
		Assertions.assertEquals(10, freeSpotsMotorcycleResponse.getBody().getFreeSpots());

		var freeSpotsCarResponse = this.parkingService.remainingSpotsByVehicleType(VehicleTypeRequest.CAR);
		Assertions.assertEquals(15, freeSpotsCarResponse.getBody().getFreeSpots());

		var freeSpotsVanResponse = this.parkingService.remainingSpotsByVehicleType(VehicleTypeRequest.VAN);
		Assertions.assertEquals(3, freeSpotsVanResponse.getBody().getFreeSpots());
	}

	@Test
	void remainingSpotsByVehicleTypeAfterParkingVehicles() throws NoFreeSpotsException {

		for (int i = 0; i < 5; i++) {
			this.parkVehicle(VehicleTypeRequest.MOTORCYCLE);
		}

		for (int i = 0; i < 10; i++) {
			this.parkVehicle(VehicleTypeRequest.CAR);
		}

		var freeSpotsMotorcycleResponse = this.parkingService.remainingSpotsByVehicleType(VehicleTypeRequest.MOTORCYCLE);
		Assertions.assertEquals(5, freeSpotsMotorcycleResponse.getBody().getFreeSpots());

		var freeSpotsCarResponse = this.parkingService.remainingSpotsByVehicleType(VehicleTypeRequest.CAR);
		Assertions.assertEquals(5, freeSpotsCarResponse.getBody().getFreeSpots());

		var freeSpotsVanResponse = this.parkingService.remainingSpotsByVehicleType(VehicleTypeRequest.VAN);
		Assertions.assertEquals(1, freeSpotsVanResponse.getBody().getFreeSpots());
	}

}