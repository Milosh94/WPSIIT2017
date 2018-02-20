package beans;

public class Territory {
	
	private int id;
	
	private String name;
	
	private double surfaceArea;

	private int residentCount;
	
	public Territory(){
		
	}

	public Territory(int id, String name, double surfaceArea, int residentCount) {
		super();
		this.id = id;
		this.name = name;
		this.surfaceArea = surfaceArea;
		this.residentCount = residentCount;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	@Override
	public String toString() {
		return "Territory [id=" + id + ", name=" + name + ", surfaceArea=" + surfaceArea + ", residentCount="
				+ residentCount + "]";
	}
	
}
