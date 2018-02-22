package beans;

import java.util.Date;

import util.Utils;

public class Comment {

	private int id;

	private int textId;
	
	private String text;
	
	private Date dateTime;
	
	private User user;
	
	private EmergencySituation emergencySituation;
	
	public Comment(){
		
	}

	public Comment(int id, int textId, String text, Date dateTime, User user, EmergencySituation emergencySituation) {
		super();
		this.id = id;
		this.textId = textId;
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
	
	public int getTextId() {
		return textId;
	}

	public void setTextId(int textId) {
		this.textId = textId;
	}

	@Override
	public String toString() {
		return "Comment [id=" + id + ", textId=" + textId + ", text=" + text + ", dateTime=" + dateTime + ", user="
				+ user + ", emergencySituation=" + emergencySituation + "]";
	}

	public String toFile(){
		return this.id + "; " + this.textId + "; " + Utils.dateToString(this.dateTime) + "; " + this.user.getId() + "; " + this.emergencySituation.getId();
	}
}
