package pattern.visitor;

import java.util.List;

public class VisitorPattern {

	public static void main(String[] args) {
		
		City city1 = new City("City1");
        Building building1 = new Building("Building1");
        Floor floor1 = new Floor("Floor1");
        Apartment apartment1 = new Apartment("Apartment1");
        Apartment apartment2 = new Apartment("Apartment2");

        floor1.setApartments(List.of(apartment1, apartment2));
        building1.setFloors(List.of(floor1));

        Building building2 = new Building("Building2");
        Floor floor2 = new Floor("Floor2");
        Apartment apartment3 = new Apartment("Apartment3");
        floor2.setApartments(List.of(apartment3));
        building2.setFloors(List.of(floor2));
        city1.setBuildings(List.of(building1, building2));
        
        Visitor englishPrinterVisitor = new EnglishPrinterVisitor();
        city1.accept(englishPrinterVisitor);
        
        Visitor frenchPrinterVisitor = new FrenchPrinterVisitor();
        city1.accept(frenchPrinterVisitor);
		
	}
}
