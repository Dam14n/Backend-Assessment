package assessment.parkinglot;

import assessment.parkinglot.configuration.ParkingLotConfigurationProperties;
import assessment.parkinglot.model.ParkingLot;
import assessment.parkinglot.model.Spot;
import assessment.parkinglot.model.SpotType;
import assessment.parkinglot.repository.ParkingLotRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ParkingLotServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ParkingLotServiceApplication.class, args);
	}


	@Bean
	ApplicationRunner init(ParkingLotConfigurationProperties parkingLotConfigurationProperties, ParkingLotRepository parkingLotRepository) {
		return args -> {
			var parkingLot = new ParkingLot();

			for (int i = 0; i < parkingLotConfigurationProperties.getMotorcycle(); i++) {
				var spot = new Spot();
				spot.setParking(parkingLot);
				spot.setType(SpotType.MOTORCYCLE);
				parkingLot.addSpot(spot);
			}

			for (int i = 0; i < parkingLotConfigurationProperties.getCompact(); i++) {
				var spot = new Spot();
				spot.setParking(parkingLot);
				spot.setType(SpotType.COMPACT);
				parkingLot.addSpot(spot);
			}

			for (int i = 0; i < parkingLotConfigurationProperties.getRegular(); i++) {
				var spot = new Spot();
				spot.setParking(parkingLot);
				spot.setType(SpotType.REGULAR);
				parkingLot.addSpot(spot);
			}

			parkingLotRepository.saveAndFlush(parkingLot);
		};
	}

}