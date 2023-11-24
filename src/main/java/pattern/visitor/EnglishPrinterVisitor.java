package pattern.visitor;

public class EnglishPrinterVisitor implements Visitor {

	@Override
	public void visitCity(City city) {
		System.out.println("City: " + city.getName());
	}

	@Override
	public void visitBuilding(Building building) {
		System.out.println("	Building: " + building.getName());
	}

	@Override
	public void visitFloor(Floor floor) {
		System.out.println("		Floor: " + floor.getName());
	}

	@Override
	public void visitApartment(Apartment apartment) {
		System.out.println("			Apartment: " + apartment.getName());
	}
}
