package dto;

import java.util.List;

import javax.ws.rs.QueryParam;

public class SearchDTO {

	@QueryParam("emergencySituationName")
	private String emergencySituationName;
	
	@QueryParam("districtName")
	private String districtName;
	
	@QueryParam("description")
	private String description;
	
	@QueryParam("volunteer")
	private String volunteer;
	
	@QueryParam("withoutVolunteer")
	private boolean withoutVolunteer;
	
	@QueryParam("newest")
	private boolean newest;
	
	@QueryParam("urgencyLevels")
	private List<String> urgencyLevels;
	
	@QueryParam("territories")
	private List<Integer> territories;
	
	public SearchDTO(){
		
	}

	public SearchDTO(String emergencySituationName, String districtName, String description, String volunteer,
			boolean withoutVolunteer, boolean newest, List<String> urgencyLevels, List<Integer> territories) {
		super();
		this.emergencySituationName = emergencySituationName;
		this.districtName = districtName;
		this.description = description;
		this.volunteer = volunteer;
		this.withoutVolunteer = withoutVolunteer;
		this.newest = newest;
		this.urgencyLevels = urgencyLevels;
		this.territories = territories;
	}

	public String getEmergencySituationName() {
		return emergencySituationName;
	}

	public void setEmergencySituationName(String emergencySituationName) {
		this.emergencySituationName = emergencySituationName;
	}

	public String getDistrictName() {
		return districtName;
	}

	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getVolunteer() {
		return volunteer;
	}

	public void setVolunteer(String volunteer) {
		this.volunteer = volunteer;
	}

	public boolean isWithoutVolunteer() {
		return withoutVolunteer;
	}

	public void setWithoutVolunteer(boolean withoutVolunteer) {
		this.withoutVolunteer = withoutVolunteer;
	}

	public boolean isNewest() {
		return newest;
	}

	public void setNewest(boolean newest) {
		this.newest = newest;
	}

	public List<String> getUrgencyLevels() {
		return urgencyLevels;
	}

	public void setUrgencyLevels(List<String> urgencyLevels) {
		this.urgencyLevels = urgencyLevels;
	}

	public List<Integer> getTerritories() {
		return territories;
	}

	public void setTerritories(List<Integer> territories) {
		this.territories = territories;
	}
}
