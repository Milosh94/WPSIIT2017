package services;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import beans.EmergencySituation;
import beans.Territory;
import beans.User;
import dataRW.EmergencySituationsRW;
import dto.EmergencySituationSimpleDTO;
import dto.ReportDTO;
import dto.SearchDTO;
import util.UrgentLevel;
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
			if(s.getStatus() != 1){
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
	
	/*
	 * Report situation
	 */
	@POST
	@Path("/report")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response report(@FormDataParam("file") InputStream uploadedInputStream, @FormDataParam("file") FormDataContentDisposition fileDetails, 
			@FormDataParam("report") FormDataBodyPart jsonPart) throws UnsupportedEncodingException{
		jsonPart.setMediaType(MediaType.APPLICATION_JSON_TYPE);
		ReportDTO reportDTO = jsonPart.getValueAs(ReportDTO.class);
		if(Utils.checkString(reportDTO.getSituationName()) || Utils.checkString(reportDTO.getDistrict()) || Utils.checkString(reportDTO.getUrgencyLevel())
				|| Utils.checkReportFieldString(reportDTO.getDescription()) || Utils.checkReportFieldString(reportDTO.getLocation()) 
				|| Utils.checkReportFieldString(reportDTO.getLocationCoordinates()) || Utils.checkReportFieldString(reportDTO.getStreetNumber())){
			return Response.status(Status.BAD_REQUEST).build();
		}
		boolean exists = false;
		UrgentLevel urgencyLevel = null;
		for(int i = 0; i < UrgentLevel.values().length; i++){
			if(UrgentLevel.values()[i].toString().equalsIgnoreCase(reportDTO.getUrgencyLevel())){
				exists = true;
				urgencyLevel = UrgentLevel.values()[i];
				break;
			}
		}
		if(exists == false){
			return Response.status(Status.BAD_REQUEST).build();
		}
		Territory t = Utils.getTerritoriesRW(context).getTerritories().get(reportDTO.getTerritory());
		if(t == null){
			return Response.status(Status.BAD_REQUEST).build();
		}
		EmergencySituation situation = new EmergencySituation();
		situation.setId(Utils.getEmergencySituationsRW(context).getEmergencySituations().size() + 1);
		situation.setName(reportDTO.getSituationName().trim().replaceAll("\\s+", " "));
		situation.setDescription(reportDTO.getDescription().trim().replaceAll("\\s+", " "));
		situation.setDistrict(reportDTO.getDistrict().trim().replaceAll("\\s+", " "));
		situation.setLocation(reportDTO.getLocation().trim().replaceAll("\\s+", " "));
		situation.setLocationCoordinates(reportDTO.getLocationCoordinates());
		situation.setStreetNumber(reportDTO.getStreetNumber());
		situation.setStatus(0);
		situation.setUrgentLevel(urgencyLevel);
		situation.setDateTime(new Date());
		situation.setTerritory(t);
		situation.setVolunteer(null);
		String fileName = fileDetails.getFileName();
		if(fileName != null && !fileName.trim().equals("")){
			long filestamp = (new Date()).getTime();
			fileName = filestamp + "_" + fileDetails.getFileName();
			situation.setPicture(fileName);
			String uploadedFileLocation = Utils.getSituationsImagesFilePath(context.getRealPath("")) + fileName;
			Utils.saveToFile(uploadedInputStream, uploadedFileLocation);
		}
		else{
			situation.setPicture("");
		}
		EmergencySituationsRW emergencySituationsRW = Utils.getEmergencySituationsRW(context);
		emergencySituationsRW.getEmergencySituations().put(situation.getId(), situation);
		emergencySituationsRW.writeEmergencySituations(context.getRealPath(""));
		context.setAttribute("situations", emergencySituationsRW);
		return Response.status(Status.OK).build();
	}
}
