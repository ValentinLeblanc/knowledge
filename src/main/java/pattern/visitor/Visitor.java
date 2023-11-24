package pattern.visitor;

public interface Visitor {
	void visitCity(City city);
	void visitBuilding(Building building);
	void visitFloor(Floor floor);
	void visitApartment(Apartment apartment);
}
