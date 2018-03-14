package services;

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
import dataRW.TerritoriesRW;

@Path("territory")
public class TerritoryService {

	@Context
	private HttpServletRequest request;
	
	@Context
	private ServletContext context;
	
	@GET
	@Path("")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<Territory> getTerritories(){
		return getTerritoriesRW().getTerritories().values().stream().collect(Collectors.toList());
	}
	
	private TerritoriesRW getTerritoriesRW(){
		TerritoriesRW territories = (TerritoriesRW)context.getAttribute("territories");
		if(territories == null){
			territories = new TerritoriesRW();
			territories.readTerritories(context.getRealPath(""));
			context.setAttribute("territories", territories);
		}
		return territories;
	}
}
