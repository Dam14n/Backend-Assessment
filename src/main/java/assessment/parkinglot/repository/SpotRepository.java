package assessment.parkinglot.repository;

import assessment.parkinglot.model.Spot;
import assessment.parkinglot.model.SpotType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpotRepository extends JpaRepository<Spot, Long>, VanSpotRepository {

	Optional<Spot> findFirstByVehicleIsNullAndTypeIn(List<SpotType> spotTypeList);

	Optional<Spot> findFirstByVehicleIdentificationIsAndIdIn(String identification, List<Long> spotIdList);

	long countByVehicleIsNull();

	long countByVehicleIsNullAndTypeIn(List<SpotType> spotTypeList);
}