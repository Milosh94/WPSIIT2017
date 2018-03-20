package dto;

public class ReportDTO {

	private String situationName;
	
	private String district;
	
	private String description;
	
	private String urgencyLevel;
	
	private int territory;
	
	private String location;
	
	private String locationCoordinates;
	
	private String streetNumber;
	
	public ReportDTO(){
		
	}

	public ReportDTO(String situationName, String district, String description, String urgencyLevel, int territory,
			String location, String locationCoordinates, String streetNumber) {
		super();
		this.situationName = situationName;
		this.district = district;
		this.description = description;
		this.urgencyLevel = urgencyLevel;
		this.territory = territory;
		this.location = location;
		this.locationCoordinates = locationCoordinates;
		this.streetNumber = streetNumber;
	}

	public String getSituationName() {
		return situationName;
	}

	public void setSituationName(String situationName) {
		this.situationName = situationName;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrgencyLevel() {
		return urgencyLevel;
	}

	public void setUrgencyLevel(String urgencyLevel) {
		this.urgencyLevel = urgencyLevel;
	}

	public int getTerritory() {
		return territory;
	}

	public void setTerritory(int territory) {
		this.territory = territory;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getLocationCoordinates() {
		return locationCoordinates;
	}

	public void setLocationCoordinates(String locationCoordinates) {
		this.locationCoordinates = locationCoordinates;
	}

	public String getStreetNumber() {
		return streetNumber;
	}

	public void setStreetNumber(String streetNumber) {
		this.streetNumber = streetNumber;
	}
}
