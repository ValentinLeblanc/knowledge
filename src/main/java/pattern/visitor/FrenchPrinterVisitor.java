package pattern.visitor;

public class FrenchPrinterVisitor implements Visitor {

	@Override
	public void visitCity(City city) {
		System.out.println("Ville: " + city.getName());
	}

	@Override
	public void visitBuilding(Building building) {
		System.out.println("	Immeuble: " + building.getName());
	}

	@Override
	public void visitFloor(Floor floor) {
		System.out.println("		Etage: " + floor.getName());
	}

	@Override
	public void visitApartment(Apartment apartment) {
		System.out.println("			Appartement: " + apartment.getName());
	}
}
