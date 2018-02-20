package beans;

import java.util.Date;

public class Comment {

	private int id;
	
	private String text;
	
	private Date dateTime;
	
	private User user;
	
	private EmergencySituation emergencySituation;
	
	public Comment(){
		
	}

	public Comment(int id, String text, Date dateTime, User user, EmergencySituation emergencySituation) {
		super();
		this.id = id;
		this.text = text;
		this.dateTime = dateTime;
		this.user = user;
		this.emergencySituation = emergencySituation;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public EmergencySituation getEmergencySituation() {
		return emergencySituation;
	}

	public void setEmergencySituation(EmergencySituation emergencySituation) {
		this.emergencySituation = emergencySituation;
	}

	@Override
	public String toString() {
		return "Comment [id=" + id + ", text=" + text + ", dateTime=" + dateTime + ", user=" + user
				+ ", emergencySituation=" + emergencySituation + "]";
	}
	
}
