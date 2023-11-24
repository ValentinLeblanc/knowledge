package pattern.visitor;

import java.util.ArrayList;
import java.util.List;

public class Floor implements Visited {

	private String name;
	
	private List<Apartment> apartments = new ArrayList<>();
	
	public Floor(String name) {
		this.name = name;
	}
	
	public void setApartments(List<Apartment> apartments) {
		this.apartments = apartments;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public void accept(Visitor visitor) {
		visitor.visitFloor(this);
		for (Apartment apartment : apartments) {
			apartment.accept(visitor);
		}
	}
}