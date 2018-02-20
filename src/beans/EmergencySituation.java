package beans;

import java.util.Date;

import util.UrgentLevel;

public class EmergencySituation {

	private int id;
	
	private String name;
	
	private String district;
	
	private String description;
	
	private Date dateTime;
	
	private String location;
	
	private Territory territory;
	
	private UrgentLevel urgentLevel;
	
	private String picture;
	
	private boolean status;
	
	private User volunteer;
	
	public EmergencySituation(){
		
	}

	public EmergencySituation(int id, String name, String district, String description, Date dateTime, String location,
			Territory territory, UrgentLevel urgentLevel, String picture, boolean status, User volunteer) {
		super();
		this.id = id;
		this.name = name;
		this.district = district;
		this.description = description;
		this.dateTime = dateTime;
		this.location = location;
		this.territory = territory;
		this.urgentLevel = urgentLevel;
		this.picture = picture;
		this.status = status;
		this.volunteer = volunteer;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Territory getTerritory() {
		return territory;
	}

	public void setTerritory(Territory territory) {
		this.territory = territory;
	}

	public UrgentLevel getUrgentLevel() {
		return urgentLevel;
	}

	public void setUrgentLevel(UrgentLevel urgentLevel) {
		this.urgentLevel = urgentLevel;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public User getVolunteer() {
		return volunteer;
	}

	public void setVolunteer(User volunteer) {
		this.volunteer = volunteer;
	}

	@Override
	public String toString() {
		return "EmergencySituation [id=" + id + ", name=" + name + ", district=" + district + ", description="
				+ description + ", dateTime=" + dateTime + ", location=" + location + ", territory=" + territory
				+ ", urgentLevel=" + urgentLevel + ", picture=" + picture + ", status=" + status + ", volunteer="
				+ volunteer + "]";
	}
	
}
