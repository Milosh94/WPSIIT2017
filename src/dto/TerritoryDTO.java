package dto;

public class TerritoryDTO {

	private String name;
	
	private double surfaceArea;
	
	private int residentCount;
	
	public TerritoryDTO(){
		
	}

	public TerritoryDTO(String name, double surfaceArea, int residentCount) {
		super();
		this.name = name;
		this.surfaceArea = surfaceArea;
		this.residentCount = residentCount;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getSurfaceArea() {
		return surfaceArea;
	}

	public void setSurfaceArea(double surfaceArea) {
		this.surfaceArea = surfaceArea;
	}

	public int getResidentCount() {
		return residentCount;
	}

	public void setResidentCount(int residentCount) {
		this.residentCount = residentCount;
	}
}
