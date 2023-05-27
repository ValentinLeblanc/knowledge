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

## Adaptateur

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

## Pont

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

## Décorateur

**Problème**

Lorsque l'on souhaite enrichir le comportement d'une classe, on peut le faire via un héritage, mais cela implique de le faire de façon statique (peu flexible) et une seule fois : il devient compliqué d'associer plusieurs modifications au comportement initial sans créer une floraison de classes filles et une complexité grandissante.

**Solution**

Créer un *décorateur* pour ce type d'objet : cela permet de placer l'objet de base à l'intérieur d'une autre classe implémentant la même interface, et déléguant son comportement à l'objet qu'elle contient, tout en enrichissant le comportement. Il est possible alors de créer autant de décorateurs que l'on souhaite pour additionner leurs comportements respectifs.

**Exemple**

```java
public interface TextGenerator {
	String generateText();
}
```

```java
public class BasicTextGenerator implements TextGenerator {
	@Override
	public String generateText() {
		return "Basic text generated!";
	}
}
```

```java
public class BaseTextGeneratorDecorator implements TextGenerator {

	private TextGenerator wrappedTextGenerator;
	
	public BaseTextGeneratorDecorator(TextGenerator wrappedTextGenerator) {
		this.wrappedTextGenerator = wrappedTextGenerator;
	}
	
	@Override
	public String generateText() {
		return wrappedTextGenerator.generateText();
	}
}
```

```java
public class ToUpperCaseTextGeneratorDecorator extends BaseTextGeneratorDecorator {

	public ToUpperCaseTextGeneratorDecorator(TextGenerator wrappedTextGenerator) {
		super(wrappedTextGenerator);
	}
	
	@Override
	public String generateText() {
		return super.generateText().toUpperCase();
	}
}
```

```java
public class BeginEndTextGeneratorDecorator extends BaseTextGeneratorDecorator {

	public BeginEndTextGeneratorDecorator(TextGenerator wrappedTextGenerator) {
		super(wrappedTextGenerator);
	}
	
	@Override
	public String generateText() {
		return "begin: " + super.generateText() + ", end";
	}
}
```

```java
public class Main {
	
	public static void main(String[] args) {
		TextGenerator textGenerator = new BeginEndTextGeneratorDecorator(new ToUpperCaseTextGeneratorDecorator(new BasicTextGenerator()));
		System.out.println(textGenerator.generateText());
	}
}
```

Résultat :

```
begin: BASIC TEXT GENERATED!, end
```

## Proxy

**Problème**

Lorsqu'il est fastidieux de faire des appels à un service car il demande beaucoup de ressources, ou bien qu'il faille appliquer certains droits avant d'effectuer telle ou telle opération vers ce service, ou bien qu'il faille logger les appels faits vers ce service... Cela demande de dupliquer du code à chaque appel au service, ou bien cela prend du temps.

**Solution**

Créer un objet *Proxy* qui va encapsuler notre service et effectuer les opérations nécessaires avant de lui déléguer le travail.

**Exemple**

```java
public interface DownloadService {
	String download(String url);
}
```

```java
public class FileDownloadService implements DownloadService {
	@Override
	public String download(String url) {
		System.out.println("Downloading from url...");
		String downloadResult = "Download result";
		return downloadResult;
	}
}
```

```java
public class FileDownloadServiceProxy implements DownloadService {

	private Map<String, String> resultCache = new HashMap<>();
	private FileDownloadService fileDownloadService;
	
	public FileDownloadServiceProxy() {
		fileDownloadService = new FileDownloadService();
	}
	
	@Override
	public String download(String url) {
		return resultCache.computeIfAbsent(url, k -> fileDownloadService.download(k));
	}
}
```

# Patrons comportementaux

## Chaîne de responsabilité

**Problème**

Lorsque l'on a besoin de lancer plusieurs étapes de traitement dans un ordre bien précis, que certains retours de traitement peuvent annuler l'exécution d'autres traitements, il est difficile de maintenir le code existant lors de l'ajout de nouveaux traitements.

**Solution**

Créer une chaine de traitements qui permet de faire circuler une demande tout au long d'une chaîne de handlers sans coupler la classe du client au différents classes de traitement. L'ordre d'exécution peut être défini lors de l'exécution.

**Exemple**

```java
public interface CarChainHandler {
	void setNextHandler(CarChainHandler nextHandler);
	boolean start(Car car);
	boolean process(Car car);
}
```

```java
public abstract class CarBaseChainHandler implements CarChainHandler {

	private CarChainHandler nextHandler;
	
	public static CarChainHandler link(CarChainHandler first, CarChainHandler... handlers) {
		CarChainHandler head = first;
		for (CarChainHandler next : handlers) {
			head.setNextHandler(next);
			head = next;
		}
		
		return first;
	}
	
	@Override
	public void setNextHandler(CarChainHandler nextHandler) {
		this.nextHandler = nextHandler;
	}
	
	@Override
	public boolean start(Car car) {
		
		if (process(car) && nextHandler != null) {
			return nextHandler.start(car);
		}
		
		return false;
	}
	
	@Override
	public boolean process(Car car) {
		return true;
	}
}
```

```java
public class NameCheckCarChainHandler extends CarBaseChainHandler {

	@Override
	public boolean process(Car car) {
		
		if (car.getName() == null || car.getName().isBlank()) {
			System.out.println("This car has no name!");
			return false;
		}
		System.out.println("OK, this name is fine");
		return true;
	}
}
```

```java
public class NumberOfSeatsCarChainHandler extends CarBaseChainHandler {
	
	@Override
	public boolean process(Car car) {
		if (car.getNumberOfSeats() < 2) {
			System.out.println("This car has less than 2 seats!");
			return false;
		}
		System.out.println("OK, we cant sit in this car");
		return true;
	}
}
```

```java
public class EngineCarChainHandler extends CarBaseChainHandler {
	
	@Override
	public boolean process(Car car) {
		if (car.getEnginePower() < 410.0) {
			System.out.println("This car is a snail!");
			return false;
		}
		System.out.println("OK, we can ride this car");
		return true;
	}
}
```

```java
public static void main(String[] args) {
		CarChainHandler chain = CarBaseChainHandler.link(new NameCheckCarChainHandler(),
                                                         new NumberOfSeatsCarChainHandler(),
                                                         new EngineCarChainHandler());
		Car car = new Car();
		car.setName("CAR_001");
		car.setNumberOfSeats(4);
		car.setEnginePower(435);
		chain.start(car);
	}
```

## Commande

**Problème**

Le traitement d'une action nécessite parfois d'être exécuté de façon asynchrone, d'être placé dans une queue, de pouvoir être rétabli... Il est ardu de répondre à tous ces besoins sans utiliser le patron Commande.

**Solution**

Convertir les demandes ou les traitements simples en objets avec paramètres.

**Exemple**

```java
public abstract class MoneyCommand {

	protected Wallet wallet;
	private Double backup;
	
	protected MoneyCommand(Wallet wallet) {
		this.wallet = wallet;
	}
	
	public Wallet getWallet() {
		return wallet;
	}
	
	protected void backup() {
		this.backup = wallet.getMoney();
	}
	
	public abstract boolean execute();
	
	public void undo() {
		wallet.setMoney(backup);
	}
}
```

```java
public class IncrementMoneyCommand extends MoneyCommand {

	protected IncrementMoneyCommand(Wallet wallet) {
		super(wallet);
	}

	@Override
	public boolean execute() {
		backup();
		wallet.setMoney(wallet.getMoney() + 1);
		return true;
	}
}
```

```java
public class DoubleMoneyCommand extends MoneyCommand {

	protected DoubleMoneyCommand(Wallet wallet) {
		super(wallet);
	}

	@Override
	public boolean execute() {
		backup();
		wallet.setMoney(wallet.getMoney() * 2);
		return true;
	}
}
```

```java
public class ClearMoneyCommand extends MoneyCommand {

	protected ClearMoneyCommand(Wallet wallet) {
		super(wallet);
	}

	@Override
	public boolean execute() {
		backup();
		wallet.setMoney(0.0);
		return true;
	}
}
```

```java
public class Wallet {

	private Double money;
	
	public Wallet(Double money) {
		this.money = money;
	}

	public Double getMoney() {
		return money;
	}

	public void setMoney(Double moneyAmount) {
		this.money = moneyAmount;
	}
}
```

```java
import java.util.Deque;
import java.util.LinkedList;

public class MoneyCommandHistory {

	private Deque<MoneyCommand> history = new LinkedList<>();
	
	public void push(MoneyCommand command) {
		history.push(command);
	}
	
	public MoneyCommand pop() {
		return history.pop();
	}
	
	public boolean isEmpty() {
		return history.isEmpty();
	}
}
```

```java
private static MoneyCommandHistory history = new MoneyCommandHistory();
	
	public static void main(String[] args) {
		Wallet wallet = new Wallet(12.0);
		
		System.out.println("Start: wallet has currently " + wallet.getMoney() + " €");
		
		executeCommand(new DoubleMoneyCommand(wallet));
		executeCommand(new IncrementMoneyCommand(wallet));
		executeCommand(new DoubleMoneyCommand(wallet));
		executeCommand(new IncrementMoneyCommand(wallet));
		executeCommand(new ClearMoneyCommand(wallet));
		undo();
		undo();
		undo();
		undo();
		undo();
	}
	
	private static void executeCommand(MoneyCommand command) {
		if (command.execute()) {
			history.push(command);
			System.out.println("Command executed: wallet has currently " + command.getWallet().getMoney() + " €");
		}
	}
	
	private static void undo() {
		if (!history.isEmpty()) {
			MoneyCommand lastCommand = history.pop();
			lastCommand.undo();
			System.out.println("Undo command: wallet has currently " + lastCommand.getWallet().getMoney() + " €");
		}
	}
```

```
Start: wallet has currently 12.0 €
Command executed: wallet has currently 24.0 €
Command executed: wallet has currently 25.0 €
Command executed: wallet has currently 50.0 €
Command executed: wallet has currently 51.0 €
Command executed: wallet has currently 0.0 €
Undo command: wallet has currently 51.0 €
Undo command: wallet has currently 50.0 €
Undo command: wallet has currently 25.0 €
Undo command: wallet has currently 24.0 €
Undo command: wallet has currently 12.0 €
```

## Itérateur

**Objectif**

Parcourir une structure de données complexe de façon séquentielle sans exposer ses détails internes.

**Problème**

Lorsque l'on manipule des collections complexes, comme des arbres ou des tableaux, et qu'une logique de parcours de ces collections doit être respectée, le détail de parcours peut polluer le code client. De plus, il est compliqué de maintenir un tel code et de le faire évoluer sans perturber le code client.

**Solution**

Utiliser une interface *Iterator* qui encapsule la collection et qui permet de réaliser des opérations telles que : élément suivant, élément précédent, fin de du parcours...

**Exemple**

```java
public interface CarPowerIterator {
	boolean hasNext();
	Car getNext();
	void reset();
}
```

```java
public class BMWCarPowerIterator implements CarPowerIterator {

	private List<Car> internalList;
	private int currentPosition = 0;
	
	public BMWCarPowerIterator(List<Car> carList, String carType) {
		this.internalList = carList.stream().filter(car -> "BMW".equals(car.getBrand()) && car.getType().equals(carType)).sorted((c1, c2) -> c1.getPower().compareTo(c2.getPower())).toList();
	}

	@Override
	public boolean hasNext() {
		return currentPosition < internalList.size();
	}

	@Override
	public Car getNext() {
		if(!hasNext()) {
			return null;
		}
		return internalList.get(currentPosition++);
	}

	@Override
	public void reset() {
		currentPosition = 0;
	}
}
```

```java
public class AudiCarPowerIterator implements CarPowerIterator {

	private List<Car> internalList;
	private int currentPosition = 0;
	
	public AudiCarPowerIterator(List<Car> carList, String carType) {
		this.internalList = carList.stream().filter(car -> "Audi".equals(car.getBrand()) && car.getType().equals(carType)).sorted((c1, c2) -> c1.getPower().compareTo(c2.getPower())).toList();
	}

	@Override
	public boolean hasNext() {
		return currentPosition < internalList.size();
	}

	@Override
	public Car getNext() {
		if(!hasNext()) {
			return null;
		}
		return internalList.get(currentPosition++);
	}

	@Override
	public void reset() {
		currentPosition = 0;
	}
}
```

```java
	public static void main(String[] args) {
		List<Car> carList = List.of(new Car("BMW", "diesel", 150), new Car("BMW", "fuel", 120),
				new Car("Audi", "diesel", 170), new Car("BMW", "diesel", 188), new Car("BMW", "diesel", 105),
				new Car("Audi", "diesel", 180), new Car("BMW", "fuel", 166), new Car("Audi", "fuel", 80),
				new Car("Audi", "fuel", 133), new Car("BMW", "diesel", 147), new Car("Audi", "fuel", 143),
				new Car("BMW", "fuel", 110), new Car("Audi", "diesel", 126), new Car("BMW", "fuel", 135));
		
		System.out.println("Printing all BMW diesel:");
		printIteratorList(new BMWCarPowerIterator(carList, "diesel"));
		System.out.println("Printing all BMW fuel:");
		printIteratorList(new BMWCarPowerIterator(carList, "fuel"));
		System.out.println("Printing all Audi diesel:");
		printIteratorList(new AudiCarPowerIterator(carList, "diesel"));
		System.out.println("Printing all Audi fuel:");
		printIteratorList(new AudiCarPowerIterator(carList, "fuel"));
		
	}
	
	private static void printIteratorList(CarPowerIterator iterator) {
		while (iterator.hasNext()) {
			System.out.println(iterator.getNext());
		}
	}
```

Résultat :

```
Printing all BMW diesel:
Car [brand=BMW, type=diesel, power=105]
Car [brand=BMW, type=diesel, power=147]
Car [brand=BMW, type=diesel, power=150]
Car [brand=BMW, type=diesel, power=188]
Printing all BMW fuel:
Car [brand=BMW, type=fuel, power=110]
Car [brand=BMW, type=fuel, power=120]
Car [brand=BMW, type=fuel, power=135]
Car [brand=BMW, type=fuel, power=166]
Printing all Audi diesel:
Car [brand=Audi, type=diesel, power=126]
Car [brand=Audi, type=diesel, power=170]
Car [brand=Audi, type=diesel, power=180]
Printing all Audi fuel:
Car [brand=Audi, type=fuel, power=80]
Car [brand=Audi, type=fuel, power=133]
Car [brand=Audi, type=fuel, power=143]
```

