package pattern.visitor;

import java.util.ArrayList;
import java.util.List;

public class Building implements Visited {

	private String name;
	
	private List<Floor> floors = new ArrayList<>();
	
	public Building(String name) {
		this.name = name;
	}
	
	public void setFloors(List<Floor> floors) {
		this.floors = floors;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public void accept(Visitor visitor) {
		visitor.visitBuilding(this);
		for (Floor floor : floors) {
			floor.accept(visitor);
		}
	}
}