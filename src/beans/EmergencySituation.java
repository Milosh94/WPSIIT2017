package beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import util.UrgentLevel;
import util.Utils;

public class EmergencySituation {

	private int id;
	
	private String name;
	
	private String district;
	
	private String description;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private Date dateTime;
	
	private String location;
	
	private String streetNumber;
	
	private String locationCoordinates;
	
	private Territory territory;
	
	private UrgentLevel urgentLevel;
	
	private String picture;
	
	private int status;
	
	private User volunteer;
	
	private List<Comment> comments;
	
	public EmergencySituation(){
		this.comments = new ArrayList<Comment>();
	}

	public EmergencySituation(int id, String name, String district, String description, Date dateTime, String location,
			String streetNumber, String locationCoordinates, Territory territory, UrgentLevel urgentLevel,
			String picture, int status, User volunteer, List <Comment> comments) {
		super();
		this.id = id;
		this.name = name;
		this.district = district;
		this.description = description;
		this.dateTime = dateTime;
		this.location = location;
		this.streetNumber = streetNumber;
		this.locationCoordinates = locationCoordinates;
		this.territory = territory;
		this.urgentLevel = urgentLevel;
		this.picture = picture;
		this.status = status;
		this.volunteer = volunteer;
		this.comments = comments;
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

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public User getVolunteer() {
		return volunteer;
	}

	public void setVolunteer(User volunteer) {
		this.volunteer = volunteer;
	}

	public String getStreetNumber() {
		return streetNumber;
	}

	public void setStreetNumber(String streetNumber) {
		this.streetNumber = streetNumber;
	}

	public String getLocationCoordinates() {
		return locationCoordinates;
	}

	public void setLocationCoordinates(String locationCoordinates) {
		this.locationCoordinates = locationCoordinates;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	@Override
	public String toString() {
		return "EmergencySituation [id=" + id + ", name=" + name + ", district=" + district + ", description="
				+ description + ", dateTime=" + dateTime + ", location=" + location + ", territory=" + territory
				+ ", urgentLevel=" + urgentLevel + ", picture=" + picture + ", status=" + status + ", volunteer="
				+ volunteer + "]";
	}
	
	public String toFile(){
		return this.id + "; " + this.name + "; " + this.district + "; " + this.description + "; " + Utils.dateToString(this.dateTime) + 
				"; " + this.location + "; " + this.streetNumber + "; " + this.locationCoordinates + "; " + this.territory.getId() + "; " + this.urgentLevel.toString() + 
				"; " + this.picture + "; " +this.status + "; " + (this.volunteer != null ? this.volunteer.getId() : -1);
	}
}
