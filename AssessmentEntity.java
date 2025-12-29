package application;

import java.io.Serializable;

abstract class AssessmentEntity implements IGradable, Serializable {
	private static final long serialVersionUID = 1L;

	protected String name;
	protected double totalWeight;

	// Constructor
	public AssessmentEntity(String name, double totalWeight) {
		this.name = name;
		this.totalWeight = totalWeight;
	}

	// 4. OOP CONCEPT: OVERRIDING (Implementing Interface method)
	@Override
	public String getName() {
		return this.name;
	}

	public double getTotalWeight() {
		return totalWeight;
	}

	@Override
	public abstract double calculateContribution();

}