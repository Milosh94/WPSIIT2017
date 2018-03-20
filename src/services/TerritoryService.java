package services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import beans.Territory;
import dto.CountryDTO;
import util.Utils;

@Path("territory")
public class TerritoryService {

	@Context
	private HttpServletRequest request;
	
	@Context
	private ServletContext context;
	
	/*
	 * Get all territories
	 */
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<Territory> getTerritories(){
		return Utils.getTerritoriesRW(context).getTerritories().values().stream().collect(Collectors.toList());
	}
	
	/*
	 * List of states and their codes
	 */
	@GET
	@Path("/states")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<CountryDTO> stateCodes(){
		List<CountryDTO> countries = new ArrayList<CountryDTO>();
		String csvFile = context.getRealPath("") + File.separator + "dataRW" + File.separator + "statecodes.csv";
        String line = "";
        String cvsSplitBy = ",";
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                String[] row = line.split(cvsSplitBy);
                String countryName = row[0];
                for(int i = 1; i < row.length - 1; i++){
                	countryName = countryName + ", " + row[i];
                }
                String countryCode = row[row.length - 1];
                countries.add(new CountryDTO(countryName.replaceAll("\"", ""), countryCode));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
		return countries;
	}
}
