# Patrons de création

## Factory Method

**Problème**

L'instanciation de différents objets partageant la même interface peut se retrouver éparpillée dans le code, rendant difficile la maintenance et l'évolution, notamment lors de la suppression ou de l'ajout de nouvelle classes d'implémentation.

**Solution**

=> Déléguer la création de ces objets à des classes partageant une même interface.

**Exemple**

```java
public interface Transport {
	void travel();
}
```

```java
public class Car implements Transport {

	@Override
	public void travel() {
		System.out.println("I take the road!");
	}
}
```

```java
public class Boat implements Transport {

	@Override
	public void travel() {
		System.out.println("I take the sea!");
	}
}
```

```java
public interface TransportFactory {
	Transport createTransport();
}
```

```java
public class CarFactory implements TransportFactory {

	@Override
	public Transport createTransport() {
		return new Car();
	}
}
```

```java
public class BoatFactory implements TransportFactory {

	@Override
	public Transport createTransport() {
		return new Boat();
	}
}
```

```java
	public static void main(String[] args) {
		
		String transportType = args[0];
		int transportNumber = Integer.parseInt(args[1]);
		TransportFactory factory = null;
		
		if ("CAR".equals(transportType)) {
			factory = new CarFactory();
		} else if ("BOAT".equals(transportType)) {
			factory = new BoatFactory();
		}
		
		startTransports(factory, transportNumber);
	}

	private static void startTransports(TransportFactory factory, int transportNumber) {
		
		for (int i = 0; i < transportNumber; i++) {
			Transport transport = factory.createTransport();
			transport.travel();
		}
	}
```

## Builder

**Problème**

La création d'objets complexes avec beaucoup d'attributs peut se révéler fastidieuse. Les constructeurs sont alors nombreux et attendent beaucoup de paramètres.

**Solution**

Déplacer le code de construction de l'objet dans une classe dédiée appelée *Builder*.

**Exemple**

```java
public class Car {

	private String name;
	private String engineType;
	private String color;
	private double enginePower;
	private double size;
	private int numberOfSeats;
	private boolean isDiesel;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEngineType() {
		return engineType;
	}

	public void setEngineType(String engineType) {
		this.engineType = engineType;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public double getEnginePower() {
		return enginePower;
	}

	public void setEnginePower(double enginePower) {
		this.enginePower = enginePower;
	}

	public double getSize() {
		return size;
	}

	public void setSize(double size) {
		this.size = size;
	}

	public double getNumberOfSeats() {
		return numberOfSeats;
	}

	public void setNumberOfSeats(int numberOfSeats) {
		this.numberOfSeats = numberOfSeats;
	}

	public boolean isDiesel() {
		return isDiesel;
	}

	public void setDiesel(boolean isDiesel) {
		this.isDiesel = isDiesel;
	}
}
```

```java
public class CarBuilder {
	
	private Car car;
	
	public CarBuilder() {
		car = new Car();
	}

	public CarBuilder name(String name) {
		car.setName(name);
		return this;
	}
	
	public CarBuilder color(String color) {
		car.setColor(color);
		return this;
	}
	
	public CarBuilder engineType(String engineType) {
		car.setEngineType(engineType);
		return this;
	}
	
	public CarBuilder enginePower(double enginePower) {
		car.setEnginePower(enginePower);
		return this;
	}
	
	public CarBuilder size(double size) {
		car.setSize(size);
		return this;
	}
	
	public CarBuilder numberOfSeats(int numberOfSeats) {
		car.setNumberOfSeats(numberOfSeats);
		return this;
	}
	
	public CarBuilder setDiesel(boolean isDiesel) {
		car.setDiesel(isDiesel);
		return this;
	}
	
	public Car build() {
		return car;
	}
}
```

```java
public static void main(String[] args) {
		
	Car car1 = new CarBuilder().color("Green").setDiesel(true).enginePower(195).build();
	Car car2 = new CarBuilder().setDiesel(false).enginePower(235).engineType("4-T").numberOfSeats(5).build();
		
	System.out.println("car1 = " + car1);
	System.out.println("car2 = " + car2);
}
```

## Prototype

**Problème**

La copie d'un objet peut être compliquée, surtout qu'on ne sait pas quel est son type concret et lorsqu'il possède des attributs internes non accessibles.

**Solution**

Créer une interface commune aux classes des objets devant être copiés dans laquelle on crée une méthode clone().

**Exemple**

```java
public interface Copyable {
	Copyable copy();
}
```

```java
public class Car implements Copyable {

	private String name;
	private String color;
	private double enginePower;
	private double size;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public double getEnginePower() {
		return enginePower;
	}

	public void setEnginePower(double enginePower) {
		this.enginePower = enginePower;
	}

	public double getSize() {
		return size;
	}

	public void setSize(double size) {
		this.size = size;
	}

	@Override
	public Copyable copy() {
		Car copy = new Car();
		copy.name = name;
		copy.color = color;
		copy.enginePower = enginePower;
		copy.size = size;
		return copy;
	}
}
```

# Patrons structurels

## Adapteur

**Problème**

Il arrive parfois qu'un service ne puisse fournir un traitement que pour un seul type d'objet, or il faudrait qu'il puisse fonctionner avec d'autres types d'objets.

**Solution**

Créer un objet *Adapteur*, qui implémente l'interface cible, et qui contient un objet implémentant l'interface source. L'objectif est de faire l'objet source se comporter comme un objet cible.

**Exemple**

```java
public interface ExternalInterface {
	void doExternalThing();
}
```

```java
public class ExternalService {
	public void performExternalAction(ExternalInterface externalObject) {
		externalObject.doExternalThing();
	}
}
```

```java
public interface InternalInterface {
	void doInternalThing();
}
```

```java
public class ExternalInterfaceAdapteur implements ExternalInterface {

	private InternalInterface internalObject;
	
	public ExternalInterfaceAdapteur(InternalInterface internalObject) {
		this.internalObject = internalObject;
	}

	@Override
	public void doExternalThing() {
		System.out.println("Adaptation of the internal behaviour...");
		internalObject.doInternalThing();
	}
}
```

```java
public class Main {

	public static void main(String[] args) {
		
		InternalInterface internalObject = new InternalInterface() {
			@Override
			public void doInternalThing() {
				System.out.println("This is the thing that is done internally");
			}
		};
		
		ExternalService externalService = new ExternalService();
		externalService.performExternalAction(new ExternalInterfaceAdapteur(internalObject));
	}
}
```

## Bridge

**Problème**

Parfois des classes sont multidimensionnelles : la logique métier qu'elles contiennent peut être scindée selon plusieurs axes indépendants, mais tous sont présents dans la même classe. Ceci rend la classe trop compliquée à maintenir et à faire évoluer.

**Solution**

Il convient alors de séparer plusieurs axes de la logique métier via une hiérarchie de classes. Chaque niveau d'abstraction contient son propre niveau d'implémentation.

**Exemple**

```java
public interface Vehicule {
	void turnOn();
}
```

```java
public class Car implements Vehicule {

	private Engine engine;
	
	public Car(Engine engine) {
		this.engine = engine;
	}
	
	@Override
	public void turnOn() {
		System.out.println("Open the door, sit, close the door");
		engine.turnOn();
	}
}
```

```java
public class Motorcycle implements Vehicule {

private Engine engine;
	
	public Motorcycle(Engine engine) {
		this.engine = engine;
	}
	
	@Override
	public void turnOn() {
		System.out.println("Sit");
		engine.turnOn();
	}
}
```

```java
public interface Engine {
	void turnOn();
}
```

```java
public class ThermalEngine implements Engine {

	@Override
	public void turnOn() {
		System.out.println("This is so loud!");
	}
}
```

```java
public class ElectricalEngine implements Engine {

	@Override
	public void turnOn() {
		System.out.println("This is really silent...");
	}
}
```

```java
public class Main {

	public static void main(String[] args) {
		Vehicule car = new Car(new ThermalEngine());
		Vehicule motorcycle = new Motorcycle(new ElectricalEngine());
		
		car.turnOn();
		motorcycle.turnOn();
	}
}
```

## Composite

**Problème**

Lorsqu'on se retrouve face à une structure arborescente, il peut être difficile d'effectuer des traitements sur l'ensemble de ses éléments.

**Solution**

Créer une structure *Composite* qui permet de manipuler une arborescence comme un objet individuel.

**Exemple**

```java
public interface Product {
	double getPrice();
}
```

```java
public class Item implements Product {

	private double price;
	
	public Item(double price) {
		super();
		this.price = price;
	}

	@Override
	public double getPrice() {
		return price;
	}
}
```

```java
public class Box implements Product {

	private List<Product> products = new ArrayList<>();
    
    public void addProduct(Product product) {
        products.add(product);
    }
    
    public void removeProduct(Product product) {
        products.remove(product);
    }
    
    public double getPrice() {
        double totalPrice = 0;
        for (Product product : products) {
            totalPrice += product.getPrice();
        }
        return totalPrice;
    }
}
```

```java
public class Main {

	public static void main(String[] args) {
		
        Item item1 = new Item(25.99);
        Item item2 = new Item(39.99);
        
        Box smallBox = new Box();
        smallBox.addProduct(item1);
        smallBox.addProduct(item2);
        
        Item item3 = new Item(9.99);
        
        Box bigBox = new Box();
        bigBox.addProduct(item3);
        bigBox.addProduct(smallBox);
        
        double totalPrice = bigBox.getPrice();
        System.out.println("Total price: $" + totalPrice);
		
	}
}
```

