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

public class TerritoriesRW {
	
	private HashMap<Integer, Territory> territories = new HashMap<Integer, Territory>();

	public HashMap<Integer, Territory> getTerritories() {
		return territories;
	}

	public void setTerritories(HashMap<Integer, Territory> territories) {
		this.territories = territories;
	}
	
	public TerritoriesRW(){
		
	}

	public TerritoriesRW(HashMap<Integer, Territory> territories) {
		super();
		this.territories = territories;
	}

	public HashMap<Integer, Territory> readTerritories(String path){
		BufferedReader in = null;
		try {
			File file = new File(path + File.separator + "dataRW" + File.separator + "territories.txt");
			in = new BufferedReader(new FileReader(file));
			StringTokenizer st;
			String line;
			this.territories = new HashMap<Integer, Territory>();
			while((line = in.readLine()) != null){
				line = line.trim();
				if(line.equals("") || line.indexOf("#") == 0){
					continue;
				}
				st = new StringTokenizer(line, ";");
				Territory territory = new Territory();
				int id = -1;
				while(st.hasMoreTokens()){
					id = Integer.parseInt(st.nextToken().trim());
					territory.setId(id);
					territory.setName(st.nextToken().trim());
					territory.setSurfaceArea(Double.parseDouble(st.nextToken().trim()));
					territory.setResidentCount(Integer.parseInt(st.nextToken().trim()));
					territory.setStatus(Boolean.parseBoolean(st.nextToken().trim()));
				}
				this.territories.put(id, territory);
			}
		} catch (Exception e) {
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
		return this.territories;
	}
	
	public void writeTerritories(String path){
		String filePath = path +  File.separator + "dataRW" + File.separator + "territories.txt";
		File file = new File(filePath);
		if(!file.exists()){
			file.getParentFile().mkdirs();
		}
		PrintWriter out;
		try {
			out = new PrintWriter(new FileWriter(file));
			for(Territory territory : this.territories.values()){
				out.println(territory.toFile());
			}
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
