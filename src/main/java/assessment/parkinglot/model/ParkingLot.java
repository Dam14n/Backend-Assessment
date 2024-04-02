package assessment.parkinglot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;

@Setter
@Getter
@Entity
@Table(name = "parkings")
public class ParkingLot {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private long id;

	@OneToMany(fetch = LAZY, mappedBy = "parking", orphanRemoval = true, cascade = ALL)
	private List<Spot> spots;

	public List<Spot> getSpots() {
		if (Objects.isNull(this.spots)) {
			this.spots = new ArrayList<>();
		}
		return this.spots;
	}

	public boolean addSpot(Spot spot) {
		return this.getSpots().add(spot);
	}
}