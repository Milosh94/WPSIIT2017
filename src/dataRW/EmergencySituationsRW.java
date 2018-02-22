package dataRW;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.StringTokenizer;

import beans.EmergencySituation;
import beans.Territory;
import beans.User;
import util.UrgentLevel;
import util.Utils;

public class EmergencySituationsRW {

	private HashMap<Integer, EmergencySituation> emergencySituations = new HashMap<Integer, EmergencySituation>();
	
	private HashMap<Integer, User> users = new HashMap<Integer, User>();
	
	private HashMap<Integer, Territory> territories = new HashMap<Integer, Territory>();
	
	public EmergencySituationsRW(){
		
	}

	public EmergencySituationsRW(HashMap<Integer, User> users,
			HashMap<Integer, Territory> territories) {
		super();
		this.users = users;
		this.territories = territories;
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

	public HashMap<Integer, Territory> getTerritories() {
		return territories;
	}

	public void setTerritories(HashMap<Integer, Territory> territories) {
		this.territories = territories;
	}
	/*
	 * this.id + "; " + this.name + "; " + this.district + "; " + this.description + "; " + Utils.dateToString(this.dateTime) + 
				"; " + this.location + "; " + this.territory.getId() + "; " + this.urgentLevel.toString() + 
				"; " + this.picture + "; " +this.status + "; " + this.volunteer.getId();
	 */
	public HashMap<Integer, EmergencySituation> readEmergencySituations(String path){
		BufferedReader in = null;
		try{
			File file = new File(path +  File.separator + "dataRW" + File.separator + "emergencySituations.txt");
			in = new BufferedReader(new FileReader(file));
			StringTokenizer st;
			String line;
			this.emergencySituations = new HashMap<Integer, EmergencySituation>();
			while((line = in.readLine()) != null){
				line = line.trim();
				if(line.equals("") || line.indexOf("#") == 0){
					continue;
				}
				st = new StringTokenizer(line, ";");
				EmergencySituation emergencySituation = new EmergencySituation();
				int id = -1;
				int territoryId = -1;
				int userId = -1;
				while(st.hasMoreTokens()){
					id = Integer.parseInt(st.nextToken());
					emergencySituation.setId(id);
					emergencySituation.setDistrict(st.nextToken().trim());
					emergencySituation.setDescription(st.nextToken().trim());
					emergencySituation.setDateTime(Utils.stringToDate(st.nextToken()));
					emergencySituation.setLocation(st.nextToken().trim());
					territoryId = Integer.parseInt(st.nextToken());
					emergencySituation.setUrgentLevel(UrgentLevel.valueOf(st.nextToken().trim()));
					emergencySituation.setPicture(st.nextToken().trim());
					emergencySituation.setStatus(Boolean.parseBoolean(st.nextToken()));
					userId = Integer.parseInt(st.nextToken());
				}
				emergencySituation.setTerritory(territories.get(territoryId));
				emergencySituation.setVolunteer(users.get(userId));
				this.emergencySituations.put(id, emergencySituation);
			}
		}catch(Exception e){
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
		return this.emergencySituations;
	}
	
	public void writeEmergencySituations(String path){
		String filePath = path +  File.separator + "dataRW" + File.separator + "emergencySituations.txt";
		File file = new File(filePath);
		if(!file.exists()){
			file.getParentFile().mkdirs();
		}
		PrintWriter out;
		try {
			out = new PrintWriter(new FileWriter(file));
			for(EmergencySituation emergencySituation : this.emergencySituations.values()){
				out.println(emergencySituation.toFile());
			}
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
