# Patrons de création

## Factory Method

### Problème

L'instanciation de différents objets partageant la même interface peut se retrouver éparpillée dans le code, rendant difficile la maintenance et l'évolution, notamment lors de la suppression ou de l'ajout de nouvelle classes d'implémentation.

### Solution

=> Déléguer la création de ces objets à des classes partageant une même interface.

### Exemple

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

### Problème

La création d'objets complexes avec beaucoup d'attributs peut se révéler fastidieuse. Les constructeurs sont alors nombreux et attendent beaucoup de paramètres.

### Solution

Déplacer le code de construction de l'objet dans une classe dédiée appelée *Builder*.

### Exemple

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

