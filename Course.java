package application;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Course implements Serializable {
	// file version for serialization
	private static final long serialVersionUID = 1L;

	private String id;
	private String name;
	private String ownerUsername;

	// 5. OOP CONCEPT: POLYMORPHISM
	// The list holds 'EvaluationGroup' objects, but they are built upon the
	// abstract AssessmentEntity.
	private List<EvaluationGroup> evaluationGroups;

	public Course(String id, String name, String ownerUsername) {
		this.id = id;
		this.name = name;
		this.ownerUsername = ownerUsername;
		this.evaluationGroups = new ArrayList<>();
	}

	/**
	 * Adds a new evaluation group. Uses the "Best-of-N" logic constructor.
	 */
	public void addEvaluationGroup(String name, double totalWeight, int totalItems, int itemsToCount) {
		evaluationGroups.add(new EvaluationGroup(name, totalWeight, totalItems, itemsToCount));
	}

	/**
	 * 6. OOP CONCEPT: OVERLOADING Convenience method: Adds a group where ALL items
	 * count (itemsToCount = totalItems).
	 */
	public void addEvaluationGroup(String name, double totalWeight, int totalItems) {
		// Calls the main logic with default parameters
		addEvaluationGroup(name, totalWeight, totalItems, totalItems);
	}

	public List<EvaluationGroup> getEvaluationGroups() {
		return evaluationGroups;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getOwnerUsername() {
		return ownerUsername;
	}

	public double calculateCurrentGrade() {
		double totalWeightedScore = 0.0;
		for (EvaluationGroup group : evaluationGroups) {
			// Polymorphic call: uses EvaluationGroup's implementation of
			// calculateContribution
			totalWeightedScore += group.calculateContribution();
		}
		return totalWeightedScore * 100;
	}

	// --- Concrete Implementation ---

	/**
	 * 7. OOP CONCEPT: INHERITANCE EvaluationGroup "is-a" AssessmentEntity. It
	 * inherits fields (name, weight) and implements the abstract logic.
	 */
	public static class EvaluationGroup extends AssessmentEntity implements Serializable {
		private static final long serialVersionUID = 1L;

		private int totalItems;
		private int itemsToCount;
		private List<IndividualScore> individualScores;

		// Constructor 1: Full logic
		public EvaluationGroup(String name, double totalWeight, int totalItems, int itemsToCount) {
			super(name, totalWeight); // Calls Abstract Parent Constructor
			this.totalItems = totalItems;
			this.itemsToCount = itemsToCount;
			this.individualScores = new ArrayList<>();

			for (int i = 1; i <= totalItems; i++) {
				individualScores.add(new IndividualScore(name + " " + i));
			}
		}

		/**
		 * 8. OOP CONCEPT: OVERRIDING (Abstract Method Implementation) Provides the
		 * specific "Best-of-N" math required by the Abstract parent.
		 */
		@Override
		public double calculateContribution() {
			if (itemsToCount == 0 || individualScores.isEmpty())
				return 0.0;

			List<Double> normalizedScores = new ArrayList<>();
			// Gather normalized scores and handle ungraded items and assumption logic

			for (IndividualScore score : individualScores) {
				if (score.isGraded()) { // is graded comes from static class IndividualScore

					normalizedScores.add(score.getNormalizedScore());

				} else {
					normalizedScores.add(1.0); // Assumption logic
				}
			}

			normalizedScores.sort(Comparator.reverseOrder());

			double sumOfBestScores = 0.0;
			for (int i = 0; i < Math.min(itemsToCount, normalizedScores.size()); i++) {
				sumOfBestScores += normalizedScores.get(i);
			}

			double averageBestScore = sumOfBestScores / itemsToCount;

			// Uses 'totalWeight' inherited from abstract parent
			return averageBestScore * (this.totalWeight / 100.0);
		}

		// Getters needed for Controller access
		public int getTotalItems() {
			return totalItems;
		}

		public int getItemsToCount() {
			return itemsToCount;
		}

		public List<IndividualScore> getIndividualScores() {
			return individualScores;
		}

		public void updateScore(int index, double score, double maxPoints) {
			if (index >= 0 && index < individualScores.size()) {
				individualScores.get(index).setScore(score, maxPoints);
			}
		}
	}

	// --- Helper Class (No changes needed here) ---
	public static class IndividualScore implements Serializable {
		private static final long serialVersionUID = 1L;

		private String itemName;
		private double score;
		private double maxPoints;

		public IndividualScore(String itemName) {
			this.itemName = itemName;
			this.score = -1;
			this.maxPoints = 1;
		}

		public void setScore(double score, double maxPoints) {
			if (score < 0 || maxPoints <= 0) {
				this.score = -1;
				this.maxPoints = 1;
			} else {
				this.score = score;
				this.maxPoints = maxPoints;
			}
		}

		public double getNormalizedScore() {
			if (!isGraded())
				return 1.0;
			return score / maxPoints;
		}

		public boolean isGraded() {
			return score >= 0 && maxPoints > 0;
		}

		public String getItemName() {
			return itemName;
		}

		public double getScore() {
			return score;
		}

		public double getMaxPoints() {
			return maxPoints;
		}
	}
}