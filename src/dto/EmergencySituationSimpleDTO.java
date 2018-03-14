package dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class EmergencySituationSimpleDTO {

	private int id;
	
	private String name;
	
	private String district;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private Date dateTime;
	
	private String territoryName;
	
	private String urgentLevel;
	
	private String volunteerName;
	
	private String volunteerUsername;
	
	public EmergencySituationSimpleDTO(){
		
	}

	public EmergencySituationSimpleDTO(int id, String name, String district, Date dateTime, String territoryName,
			String urgentLevel, String volunteerName, String volunteerUsername) {
		super();
		this.id = id;
		this.name = name;
		this.district = district;
		this.dateTime = dateTime;
		this.territoryName = territoryName;
		this.urgentLevel = urgentLevel;
		this.volunteerName = volunteerName;
		this.volunteerUsername = volunteerUsername;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	public String getTerritoryName() {
		return territoryName;
	}

	public void setTerritoryName(String territoryName) {
		this.territoryName = territoryName;
	}

	public String getUrgentLevel() {
		return urgentLevel;
	}

	public void setUrgentLevel(String urgentLevel) {
		this.urgentLevel = urgentLevel;
	}

	public String getVolunteerName() {
		return volunteerName;
	}

	public void setVolunteerName(String volunteerName) {
		this.volunteerName = volunteerName;
	}

	public String getVolunteerUsername() {
		return volunteerUsername;
	}

	public void setVolunteerUsername(String volunteerUsername) {
		this.volunteerUsername = volunteerUsername;
	}
}
