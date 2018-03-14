package services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import beans.EmergencySituation;
import beans.User;
import dto.EmergencySituationSimpleDTO;
import dto.SearchDTO;
import util.Utils;

@Path("")
public class EmergencySituationService {
	
	@Context
	private HttpServletRequest request;
	
	@Context
	private ServletContext context;
	
	/*
	 * Search emergency situations
	 */
	@GET
	@Path("/search")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response search(@BeanParam SearchDTO search){
		User loggedUser = (User)context.getAttribute("user");
		if(loggedUser != null){
			if(loggedUser.isAdmin() == false){
				search.setWithoutVolunteer(false);
			}
		}
		else{
			search.setWithoutVolunteer(false);
		}
		List<EmergencySituation> result = new ArrayList<EmergencySituation>();
		for(EmergencySituation s : Utils.getEmergencySituationsRW(context).getEmergencySituations().values()){
			if(s.isStatus() == false){
				continue;
			}
			if(search.isWithoutVolunteer() == true){
				if(s.getVolunteer() != null){
					continue;
				}
			}
			if(Utils.checkString(search.getEmergencySituationName()) == false){
				if(s.getName().toLowerCase().contains(search.getEmergencySituationName().toLowerCase()) == false){
					continue;
				}
			}
			if(Utils.checkString(search.getDistrictName()) == false){
				if(s.getDistrict().toLowerCase().contains(search.getDistrictName().toLowerCase()) == false){
					continue;
				}
			}
			if(Utils.checkString(search.getDescription()) == false){
				if(s.getDescription().toLowerCase().contains(search.getDescription().toLowerCase()) == false){
					continue;
				}
			}
			if(Utils.checkString(search.getVolunteer()) == false){
				if(s.getVolunteer().getUsername().toLowerCase().contains(search.getVolunteer().trim().toLowerCase()) == false){
					List<String> words = new ArrayList<String>(Arrays.asList(s.getVolunteer().getFirstName().split(" ")));
					words.addAll(Arrays.asList(s.getVolunteer().getLastName().split(" ")));
					List<String> searchWords = Arrays.asList(search.getVolunteer().trim().replaceAll("\\s+", " ").split(" "));
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
						continue;
					}
				}
			}
			if(search.getUrgencyLevels().size() != 0){
				boolean exists = false;
				for(String level : search.getUrgencyLevels()){
					if(level.equalsIgnoreCase(s.getUrgentLevel().toString())){
						exists = true;
						break;
					}
				}
				if(exists == false){
					continue;
				}
			}
			if(search.getTerritories().size() != 0){
				boolean exists = false;
				for(int territory : search.getTerritories()){
					if(s.getTerritory().getId() == territory){
						exists = true;
						break;
					}
				}
				if(exists == false){
					continue;
				}
			}
			result.add(s);
		}
		if(search.isNewest() == true){
			result = result.stream().sorted((a, b) -> b.getDateTime().compareTo(a.getDateTime())).collect(Collectors.toList());
		}
		List<EmergencySituationSimpleDTO> resultTransformed = result.stream().map(s -> {
			return new EmergencySituationSimpleDTO(
					s.getId(), 
					s.getName(), 
					s.getDistrict(), 
					s.getDateTime(), 
					s.getTerritory().getName(), 
					s.getUrgentLevel().toString(), 
					s.getVolunteer() != null ? s.getVolunteer().getFirstName() + " " + s.getVolunteer().getLastName() : null, 
					s.getVolunteer() != null ? s.getVolunteer().getUsername() : null
			);
		}).collect(Collectors.toList());
		return Response.status(Status.OK).entity(resultTransformed).build();
	}
}
