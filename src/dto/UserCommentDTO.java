package dto;

public class UserCommentDTO {

	private String comment;
	
	public UserCommentDTO(){
		
	}

	public UserCommentDTO(String comment) {
		super();
		this.comment = comment;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}
