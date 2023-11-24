package pattern.visitor;

public class Apartment implements Visited {

	private String name;
	
	public String getName() {
		return name;
	}
	
	public Apartment(String name) {
		this.name = name;
	}
	
	@Override
	public void accept(Visitor visitor) {
		visitor.visitApartment(this);
	}
}
