package assessment.parkinglot.repository;

import assessment.parkinglot.model.Spot;
import assessment.parkinglot.model.SpotType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpotRepository extends JpaRepository<Spot, Long> {

	List<Spot> findTop3ByVehicleIsNullAndTypeIn(List<SpotType> spotTypeList);

	Optional<Spot> findFirstByVehicleIsNullAndTypeIn(List<SpotType> spotTypeList);


	Optional<Spot> findFirstByVehicleIdentificationIs(String identification);

	long countByVehicleIsNull();

	long countByVehicleIsNullAndTypeIn(List<SpotType> spotTypeList);
}