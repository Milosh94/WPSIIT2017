package dataRW;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.StringTokenizer;

import beans.Territory;
import beans.User;

public class UsersRW {
	
	private HashMap<Integer, User> users = new HashMap<Integer, User>();

	private HashMap<Integer, Territory> territories = new HashMap<Integer, Territory>();
	
	public UsersRW(HashMap<Integer, Territory> territories){
		this.territories = territories;
	}

	public HashMap<Integer, User> getUsers() {
		return users;
	}

	public void setUsers(HashMap<Integer, User> users) {
		this.users = users;
	}
	
	public HashMap<Integer, Territory> getTerritories() {
		return territories;
	}

	public void setTerritories(HashMap<Integer, Territory> territories) {
		this.territories = territories;
	}

	public HashMap<Integer, User> readUsers(String path){
		BufferedReader in = null;
		try{
			File file = new File(path +  File.separator + "dataRW" + File.separator + "users.txt");
			in = new BufferedReader(new FileReader(file));
			StringTokenizer st;
			String line;
			this.users = new HashMap<Integer, User>();
			while((line = in.readLine()) != null){
				line = line.trim();
				if(line.equals("") || line.indexOf("#") == 0){
					continue;
				}
				st = new StringTokenizer(line, ";");
				User user = new User();
				int territoryId = -1;
				int id = -1;
				while(st.hasMoreTokens()){
					id = Integer.parseInt(st.nextToken());
					user.setId(id);
					user.setUsername(st.nextToken().trim());
					user.setPassword(st.nextToken().trim());
					user.setFirstName(st.nextToken().trim());
					user.setLastName(st.nextToken().trim());
					user.setPhone(st.nextToken().trim());
					user.setEmail(st.nextToken().trim());
					territoryId = Integer.parseInt(st.nextToken());
					user.setPicture(st.nextToken().trim());
					user.setBlocked(Boolean.parseBoolean(st.nextToken()));
					user.setAdmin(Boolean.parseBoolean(st.nextToken()));
				}
				user.setTerritory(this.territories.get(territoryId));
				this.users.put(id, user);
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		finally{
			if(in != null){
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return this.users;
	}
	
	public void writeUsers(String path){
		String filePath = path +  File.separator + "dataRW" + File.separator + "users.txt";
		File file = new File(filePath);
		if(!file.exists()){
			file.getParentFile().mkdirs();
		}
		PrintWriter out;
		try {
			out = new PrintWriter(new FileWriter(filePath));
			for(User u : users.values()){
				out.println(u.toFile());
			}
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
