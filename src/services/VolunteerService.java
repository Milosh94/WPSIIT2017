package services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import beans.User;
import dataRW.UsersRW;
import dto.EmergencySituationSimpleDTO;
import dto.UserDTO;
import util.Utils;

@Path("")
public class VolunteerService {

	@Context
	private HttpServletRequest request;
	
	@Context
	private ServletContext context;
	
	/*
	 * Get all volunteers
	 */
	@GET
	@Path("/volunteers")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getVolunteers(){
		User user = (User)request.getSession().getAttribute("user");
		if(user == null || user.isAdmin() == false){
			return Response.status(Status.FORBIDDEN).build();
		}
		UsersRW usersRW = Utils.getUsersRW(context);
		List<UserDTO> volunteers = usersRW.getUsers().values()
				.stream()
				.filter(u -> u.isAdmin() == false)
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
	 * Get logged volunteer's emergency situations
	 */
	@GET
	@Path("/volunteer-emergency-situations")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getVolunteerEmergencySituations(){
		User user = (User)request.getSession().getAttribute("user");
		if(user == null || user.isAdmin() == true || user.isBlocked() == true){
			return Response.status(Status.FORBIDDEN).build();
		}
		List<EmergencySituationSimpleDTO> situations = Utils.getEmergencySituationsRW(context).getEmergencySituations().values()
				.stream()
				.filter(e -> e.getStatus() == 1 && e.getVolunteer() != null && e.getVolunteer().getId() == user.getId())
				.map(e -> new EmergencySituationSimpleDTO(
						e.getId(), 
						e.getName(), 
						e.getDistrict(), 
						e.getDateTime(), 
						e.getTerritory().getName(),
						e.getUrgentLevel().toString(), 
						e.getVolunteer() != null ? e.getVolunteer().getFirstName() + " " + e.getVolunteer().getLastName() : null, 
						e.getVolunteer() != null ? e.getVolunteer().getUsername() : null))
				.sorted((a, b) -> b.getDateTime().compareTo(a.getDateTime()))
				.collect(Collectors.toList());
		return Response.status(Status.OK).entity(situations).build();
	}
	
	/*
	 * Search volunteers
	 */
	@GET
	@Path("/search-volunteers")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response searchVolunteers(@QueryParam("search") String search){
		User user = (User)request.getSession().getAttribute("user");
		if(user == null || user.isAdmin() == false){
			return Response.status(Status.FORBIDDEN).build();
		}
		if(Utils.checkReportFieldString(search) == true){
			return Response.status(Status.BAD_REQUEST).build();
		}
		UsersRW usersRW = Utils.getUsersRW(context);
		List<UserDTO> volunteers = usersRW.getUsers().values()
				.stream()
				.filter(u -> {
					if(u.isAdmin() == true){
						return false;
					}
					if(u.getUsername().toLowerCase().contains(search.trim().toLowerCase()) == false){
						List<String> words = new ArrayList<String>(Arrays.asList(u.getFirstName().split(" ")));
						words.addAll(Arrays.asList(u.getLastName().split(" ")));
						List<String> searchWords = Arrays.asList(search.trim().replaceAll("\\s+", " ").split(" "));
						boolean next = false;
						for(String searchWord : searchWords){
							boolean contains = false;
							for(int i = 0; i < words.size(); i++){
								if(words.get(i).toLowerCase().contains(searchWord.toLowerCase())){
									contains = true;
									words.remove(i);
									break;
								}
							}
							if(contains == false){
								next = true;
								break;
							}
						}
						if(next == true){
							return false;
						}
						else{
							return true;
						}
					}
					else{
						return true;
					}
				})
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
	 * Admin blocking/unblocking volunteer
	 */
	@PUT
	@Path("/block-unblock")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response blockUnblock(@QueryParam("username") String username, @QueryParam("block") boolean block){
		User user = (User)request.getSession().getAttribute("user");
		if(user == null || user.isAdmin() == false){
			return Response.status(Status.FORBIDDEN).build();
		}
		User volunteer = Utils.getUsersRW(context).getUsers().values().stream().filter(u -> u.getUsername().equals(username)).findFirst().orElse(null);
		if(volunteer == null){
			return Response.status(Status.BAD_REQUEST).build();
		}
		volunteer.setBlocked(block);
		Utils.getUsersRW(context).writeUsers(context.getRealPath(""));
		UserDTO userDTO = new UserDTO(
				volunteer.getUsername(), 
				volunteer.getFirstName(),
				volunteer.getLastName(),
				volunteer.getPhone(), 
				volunteer.getEmail(), 
				volunteer.getTerritory(), 
				volunteer.getPicture(),
				volunteer.isBlocked(),
				volunteer.isAdmin());
		return Response.status(Status.OK).entity(userDTO).build();
	}
}
