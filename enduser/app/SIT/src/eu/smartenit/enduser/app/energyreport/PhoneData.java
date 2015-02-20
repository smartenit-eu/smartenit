package eu.smartenit.enduser.app.energyreport;

public class PhoneData {
	private Measurements[] measurements;

	private String identifier;

	public PhoneData(String identifier) {
		this.identifier = identifier;
	}

	public Measurements[] getMeasurements() {
		return measurements;
	}

	public void setMeasurements(Measurements[] measurements) {
		this.measurements = measurements;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
}
