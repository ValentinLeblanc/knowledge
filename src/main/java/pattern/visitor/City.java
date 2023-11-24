package pattern.visitor;

import java.util.ArrayList;
import java.util.List;

public class City implements Visited {

	private String name;
	
	private List<Building> buildings = new ArrayList<>();
	
	public City(String name) {
		this.name = name;
	}
	
	public void setBuildings(List<Building> buildings) {
		this.buildings = buildings;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public void accept(Visitor visitor) {
		visitor.visitCity(this);
		for (Building building : buildings) {
			building.accept(visitor);
		}
	}
	
}