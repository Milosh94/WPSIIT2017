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
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import beans.Territory;
import beans.User;
import dataRW.EmergencySituationsRW;
import dataRW.TerritoriesRW;
import dataRW.UsersRW;
import dto.CountryDTO;
import dto.TerritoryDTO;
import dto.UserDTO;
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
	public Response getTerritories(){
		List<Territory> territories = Utils.getTerritoriesRW(context).getTerritories().values().stream().filter(t -> t.isStatus() == true).collect(Collectors.toList());
		return Response.status(Status.OK).entity(territories).build();
	}
	
	/*
	 * List of states and their codes
	 */
	@GET
	@Path("/states")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response stateCodes(){
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
		return Response.status(Status.OK).entity(countries).build();
	}
	
	/*
	 * Get volunteers from specific territory
	 */
	@GET
	@Path("/{id}/volunteers")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTerritoryVolunteers(@PathParam("id") int territoryId){
		User user = (User)request.getSession().getAttribute("user");
		if(user == null || user.isAdmin() == false){
			return Response.status(Status.FORBIDDEN).build();
		}
		TerritoriesRW territoriesRW = Utils.getTerritoriesRW(context);
		Territory territory = territoriesRW.getTerritories().values().stream().filter(t -> t.getId() == territoryId).findFirst().orElse(null);
		if(territory == null || territory.isStatus() == false){
			return Response.status(Status.BAD_REQUEST).build();
		}
		UsersRW usersRW = Utils.getUsersRW(context);
		List<UserDTO> volunteers = usersRW.getUsers().values()
				.stream()
				.filter(u -> u.isAdmin() == false && (u.getTerritory() == null ? false : u.getTerritory().getId() == territoryId))
				.map(u -> new UserDTO(
						u.getUsername(), 
						u.getFirstName(), 
						u.getLastName(), 
						u.getPhone(), 
						u.getEmail(), 
						u.getTerritory(), 
						u.getPicture(), 
						u.isBlocked(), 
						u.isAdmin()
					))
				.collect(Collectors.toList());
		return Response.status(Status.OK).entity(volunteers).build();
	}
	
	/*
	 * Check if territory already exists
	 */
	@GET
	@Path("/exists")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response territoryExists(@QueryParam("name") String name){
		User user = (User)request.getSession().getAttribute("user");
		if(user == null || user.isAdmin() == false){
			return Response.status(Status.FORBIDDEN).build();
		}
		if(name == null || name.trim().equals("")){
			return Response.status(Status.BAD_REQUEST).build();
		}
		TerritoriesRW territoriesRW = Utils.getTerritoriesRW(context);
		Territory exists = territoriesRW.getTerritories().values().stream().filter(t -> t.getName().equalsIgnoreCase(name.trim()) && t.isStatus() == true).findFirst().orElse(null);
		if(exists == null){
			return Response.status(Status.OK).entity("{\"exists\": false}").build();
		}
		else{
			return Response.status(Status.OK).entity("{\"exists\": true}").build();
		}
	}
	
	/*
	 * Search territories by name
	 */
	@GET
	@Path("/search")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response searchTerritories(@QueryParam("search") String search){
		User user = (User)request.getSession().getAttribute("user");
		if(user == null || user.isAdmin() == false){
			return Response.status(Status.FORBIDDEN).build();
		}
		if(Utils.checkReportFieldString(search) == true){
			return Response.status(Status.BAD_REQUEST).build();
		}
		TerritoriesRW territoriesRW = Utils.getTerritoriesRW(context);
		List<Territory> territories = territoriesRW.getTerritories().values()
				.stream()
				.filter(t -> {
					if(t.isStatus() == false){
						return false;
					}
					else{
						if(t.getName().toLowerCase().contains(search.toLowerCase().trim().replaceAll("\\s+", " "))){
							return true;
						}
						else{
							return false;
						}
					}
				})
				.collect(Collectors.toList());
		return Response.status(Status.OK).entity(territories).build();
	}
	
	/*
	 * Add new territory
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addTerritory(TerritoryDTO territoryDTO){
		User user = (User)request.getSession().getAttribute("user");
		if(user == null || user.isAdmin() == false){
			return Response.status(Status.FORBIDDEN).build();
		}
		if(territoryDTO == null || Utils.checkString(territoryDTO.getName()) || territoryDTO.getSurfaceArea() <= 0 || territoryDTO.getResidentCount() < 0){
			return Response.status(Status.BAD_REQUEST).build();
		}
		TerritoriesRW territoriesRW = Utils.getTerritoriesRW(context);
		Territory exists = territoriesRW.getTerritories().values().stream().filter(t -> t.getName().equalsIgnoreCase(territoryDTO.getName().trim()) && t.isStatus() == true).findFirst().orElse(null);
		if(exists != null){
			return Response.status(Status.BAD_REQUEST).build();
		}
		int territoryId = territoriesRW.getTerritories().size() + 1;
		Territory territory = new Territory(territoryId, territoryDTO.getName(), territoryDTO.getSurfaceArea(), territoryDTO.getResidentCount(), true);
		territoriesRW.getTerritories().put(territoryId, territory);
		territoriesRW.writeTerritories(context.getRealPath(""));
		return Response.status(Status.OK).build();
	}
	
	/*
	 * Update territory
	 */
	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateTerritory(@PathParam("id") int id, Territory territory){
		User user = (User)request.getSession().getAttribute("user");
		if(user == null || user.isAdmin() == false){
			return Response.status(Status.FORBIDDEN).build();
		}
		if(territory == null || Utils.checkString(territory.getName()) || territory.getSurfaceArea() <= 0 || territory.getResidentCount() < 0){
			return Response.status(Status.BAD_REQUEST).build();
		}
		TerritoriesRW territoriesRW = Utils.getTerritoriesRW(context);
		Territory exists = territoriesRW.getTerritories().values().stream().filter(t -> t.getId() == id && t.getName().equalsIgnoreCase(territory.getName().trim()) && t.isStatus() == true).findFirst().orElse(null);
		if(exists == null){
			return Response.status(Status.BAD_REQUEST).build();
		}
		exists.setSurfaceArea(territory.getSurfaceArea());
		exists.setResidentCount(territory.getResidentCount());
		territoriesRW.writeTerritories(context.getRealPath(""));
		return Response.status(Status.OK).build();
	}
	
	/*
	 * Delete territory
	 */
	@DELETE
	@Path("/{id}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteTerritory(@PathParam("id") int id){
		User user = (User)request.getSession().getAttribute("user");
		if(user == null || user.isAdmin() == false){
			return Response.status(Status.FORBIDDEN).build();
		}
		TerritoriesRW territoriesRW = Utils.getTerritoriesRW(context);
		Territory territory = territoriesRW.getTerritories().values().stream().filter(t -> t.getId() == id).findFirst().orElse(null);
		if(territory == null){
			return Response.status(Status.BAD_REQUEST).build();
		}
		territory.setStatus(false);
		territoriesRW.writeTerritories(context.getRealPath(""));
		UsersRW usersRW = Utils.getUsersRW(context);
		usersRW.getUsers().values()
			.stream()
			.filter(u -> u.getTerritory() != null && u.getTerritory().getId() == id)
			.forEach(u -> u.setTerritory(null));
		usersRW.writeUsers(context.getRealPath(""));
		EmergencySituationsRW emergencySituationsRW = Utils.getEmergencySituationsRW(context);
		emergencySituationsRW.getEmergencySituations().values()
			.stream()
			.filter(e -> e.getTerritory() != null && e.getTerritory().getId() == id)
			.forEach(e -> {
				e.setTerritory(null);
				e.setVolunteer(null);
			});
		emergencySituationsRW.writeEmergencySituations(context.getRealPath(""));
		return Response.status(Status.OK).build();
	}
}
