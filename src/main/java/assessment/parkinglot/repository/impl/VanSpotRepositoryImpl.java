package assessment.parkinglot.repository.impl;

import assessment.parkinglot.model.QSpot;
import assessment.parkinglot.model.Spot;
import assessment.parkinglot.model.SpotType;
import assessment.parkinglot.repository.VanSpotRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class VanSpotRepositoryImpl implements VanSpotRepository {

	private final EntityManager em;

	@Override
	public List<Spot> findFreeSpotsForVanVehicle() {
		var qSpot = QSpot.spot;

		var previousPositionExistsPredicate = this.getPreviousPositionExistsPredicate(qSpot, 1L);
		var previous2PositionExistsPredicate = this.getPreviousPositionExistsPredicate(qSpot, 2L);
		var nextPositionExistsPredicate = this.getNextPositionExistsPredicate(qSpot, 1L);
		var next2PositionExistsPredicate = this.getNextPositionExistsPredicate(qSpot, 2L);

		return new JPAQuery<Spot>(this.em)
				.from(qSpot)
				.where(qSpot.vehicle.isNull().and(qSpot.type.eq(SpotType.REGULAR)))
				.where(previousPositionExistsPredicate.and(nextPositionExistsPredicate)
						.or(nextPositionExistsPredicate.and(next2PositionExistsPredicate))
						.or(previousPositionExistsPredicate.and(previous2PositionExistsPredicate)))
				.fetch();
	}

	private BooleanExpression getPreviousPositionExistsPredicate(QSpot qSpot, long previousPosition) {
		var existsQSpot = new QSpot("exists");
		return JPAExpressions.selectOne()
				.from(existsQSpot)
				.where(existsQSpot.vehicle.isNull().and(existsQSpot.type.eq(SpotType.REGULAR)))
				.where(existsQSpot.id.eq(qSpot.id.subtract(previousPosition)))
				.exists();
	}

	private BooleanExpression getNextPositionExistsPredicate(QSpot qSpot, long nextPosition) {
		var existsQSpot = new QSpot("exists");
		return JPAExpressions.selectOne()
				.from(existsQSpot)
				.where(existsQSpot.vehicle.isNull().and(existsQSpot.type.eq(SpotType.REGULAR)))
				.where(existsQSpot.id.eq(qSpot.id.add(nextPosition)))
				.exists();
	}
}