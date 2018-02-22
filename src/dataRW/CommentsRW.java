package dataRW;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.StringTokenizer;

import beans.Comment;
import beans.EmergencySituation;
import beans.User;
import util.Utils;

public class CommentsRW {
	
	private HashMap<Integer, Comment> comments = new HashMap<Integer, Comment>();
	
	private HashMap<Integer, EmergencySituation> emergencySituations = new HashMap<Integer, EmergencySituation>();
	
	private HashMap<Integer, User> users = new HashMap<Integer, User>();
	
	public CommentsRW(){
		
	}

	public CommentsRW(HashMap<Integer, EmergencySituation> emergencySituations, HashMap<Integer, User> users) {
		super();
		this.emergencySituations = emergencySituations;
		this.users = users;
	}

	public HashMap<Integer, Comment> getComments() {
		return comments;
	}

	public void setComments(HashMap<Integer, Comment> comments) {
		this.comments = comments;
	}

	public HashMap<Integer, EmergencySituation> getEmergencySituations() {
		return emergencySituations;
	}

	public void setEmergencySituations(HashMap<Integer, EmergencySituation> emergencySituations) {
		this.emergencySituations = emergencySituations;
	}

	public HashMap<Integer, User> getUsers() {
		return users;
	}

	public void setUsers(HashMap<Integer, User> users) {
		this.users = users;
	}

	/*
	 *  this.id + "; " + this.textId + "; " + Utils.dateToString(this.dateTime) + "; " + this.user.getId() + "; " + this.emergencySituation;
	 */
	public HashMap<Integer, Comment> readComments(String path){
		BufferedReader in = null;
		try{
			File file = new File(path +  File.separator + "dataRW" + File.separator + "comments.txt");
			in = new BufferedReader(new FileReader(file));
			StringTokenizer st;
			String line;
			this.comments = new HashMap<Integer, Comment>();
			while((line = in.readLine()) != null){
				line = line.trim();
				if(line.equals("") || line.indexOf("#") == 0){
					continue;
				}
				st = new StringTokenizer(line, ";");
				Comment comment = new Comment();
				int id = -1;
				int textId = -1;
				int emergencySituationId = -1;
				int userId = -1;
				while(st.hasMoreTokens()){
					id = Integer.parseInt(st.nextToken());
					comment.setId(id);
					textId = Integer.parseInt(st.nextToken());
					comment.setTextId(textId);
					comment.setDateTime(Utils.stringToDate(st.nextToken()));
					userId = Integer.parseInt(st.nextToken());
					emergencySituationId = Integer.parseInt(st.nextToken());
				}
				comment.setUser(users.get(userId));
				comment.setEmergencySituation(emergencySituations.get(emergencySituationId));
				comment.setText(getComment(id, path));
				this.comments.put(id, comment);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			if ( in != null ) {
				try {
					in.close();
				}
				catch (Exception e) { }
			}
		}
		return this.comments;
	}
	
	public void writeComments(String path){
		String filePath = path +  File.separator + "dataRW" + File.separator + "comments.txt";
	    File file = new File(filePath);
	    if (!file.exists()){
	    	file.getParentFile().mkdirs();
	    }
	    PrintWriter out;
		try {
			out = new PrintWriter(new FileWriter(filePath));
			for(Comment c : this.comments.values()){
				out.println(c.toFile());
			}
	        out.flush();
	        out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeComment(Comment c, String path){
		String filePath = path  + File.separator + "dataRW" + File.separator + "commentsText" + File.separator + "comment_" + c.getId() + ".txt";
	    File file = new File(filePath);
	    if (!file.exists()){
	    	file.getParentFile().mkdirs();
	    }
	    PrintWriter out;
		try {
			out = new PrintWriter(new FileWriter(filePath));
			out.print(c.getText());
	        out.flush();
	        out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void deleteComment(Comment c, String path){
		try{
			String filePath = path  + File.separator + "dataRW" + File.separator + "commentsText" + File.separator + "comment_" + c.getId() + ".txt";
    		File file = new File(filePath);
    		if(file.delete()){
    			System.out.println(file.getName() + " is deleted!");
    		}else{
    			System.out.println("Delete operation is failed.");
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
	}
	
	private String getComment(int commentId, String path) throws IOException {
		String filePath = path  + File.separator + "dataRW" + File.separator + "commentsText" + File.separator + "comment_" + commentId + ".txt";
		String comment = Utils.readFile(filePath);
		return comment;
	}
}
