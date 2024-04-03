package assessment.parkinglot.repository;

import assessment.parkinglot.model.Spot;

import java.util.List;

public interface VanSpotRepository {

	List<Spot> findFreeSpotsForVanVehicle();
}