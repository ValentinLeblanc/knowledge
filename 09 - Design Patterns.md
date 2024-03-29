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

**Objectif**

Faire circuler une demande tout au long d'une chaîne de handlers, jusqu'à ce que l'un ou plusieurs d'entre eux la traite.

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

**Objectif**

Convertir des demandes ou des traitements simples en objets.

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

## Médiateur

**Objectif**

Diminuer le couplage entre les composants d'un programme en les faisant communiquer indirectement via un objet spécial appelé Médiateur.

**Problème**

Lorsque des composants doivent communiquer entre eux, et que chaque composant doit transmettre des messages à beaucoup d'autres composants, la logique communicative peut venir polluer la logique interne du composant.

**Solution**

Créer un objet Médiateur référencé dans chaque composant qui aura pour fonction de transmettre les messages entre chaque composant.

**Exemple**

```java
public interface PersonMediator {
	void registerPerson(Person person);
	void sendMessage(Person sender, String message);
}
```

```java
public class LanguagePersonMediator implements PersonMediator {

	private List<Person> persons = new ArrayList<>();
	
	@Override
	public void registerPerson(Person person) {
		persons.add(person);
	}
	
	@Override
	public void sendMessage(Person sender, String message) {
		persons.stream()
			.filter(person -> person != sender && person.getLanguage().equals(sender.getLanguage()))
			.forEach(person -> System.out.println(sender.getName() + " says to " + person.getName() + ": '" + message + "'"));
	}
}
```

```java
public class Person {

	private String name;
	private String language;
	private PersonMediator mediator;
	
	public Person(String name, String language, PersonMediator mediator) {
		this.name = name;
		this.language = language;
		this.mediator = mediator;
		mediator.registerPerson(this);
	}
	public void sendMessage(String message) {
		mediator.sendMessage(this, message);
	}
}
```

```java
public static void main(String[] args) {
    PersonMediator mediator = new LanguagePersonMediator();

    Person david = new Person("David", "French", mediator);
    Person davido = new Person("Davido", "Spanish", mediator);
    Person davidish = new Person("Davidish", "English", mediator);
    Person jacques = new Person("Jacques", "French", mediator);
    Person jaco = new Person("Jaco", "Spanish", mediator);
    Person jack = new Person("Jack", "English", mediator);
    Person charles = new Person("Charles", "French", mediator);
    Person carlos = new Person("Carlos", "Spanish", mediator);
    Person charly = new Person("Charly", "English", mediator);

    david.sendMessage("Bonjour, je m'appelle David!");
    jack.sendMessage("Hi, how are you?");
    carlos.sendMessage("Tengo la camisa negra.");
}
```

Résultat :

```
David says to Jacques: 'Bonjour, je m'appelle David!'
David says to Charles: 'Bonjour, je m'appelle David!'
Jack says to Davidish: 'Hi, how are you?'
Jack says to Charly: 'Hi, how are you?'
Carlos says to Davido: 'Tengo la camisa negra.'
Carlos says to Jaco: 'Tengo la camisa negra.'
```

## Mémento

**Objectif**

Prendre des instantanés de l'état d'un objet pour pouvoir les restaurer plus tard.

**Problème**

Lorsque l'on souhaite créer une photo de l'état d'un objet depuis l'extérieur, il faut que tous ses attributs soient exposés. Or, cela est contraire au principe d'encapsulation.

**Solution**

Créer un objet Memento qui va contenir l'état de l'objet et pouvoir le restaurer si nécessaire via un CareTaker.

**Exemple**

```java
public class Document {

	public Document(String title, String content, String author) {
		super();
		this.title = title;
		this.content = content;
		this.author = author;
	}

	private String title;
	
	private String content;
	
	private String author;
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public String getAuthor() {
		return author;
	}
	
	public void setAuthor(String author) {
		this.author = author;
	}
	
	public void appendContent(String content) {
		this.content += content;
	}
	
	public void restore(DocumentMemento memento) {
		this.title = memento.title;
		this.content = memento.content;
		this.author = memento.author;
	}
	
	public DocumentMemento save() {
		return new DocumentMemento(this.title, this.content, this.author);
	}
	
	@Override
	public String toString() {
		return "Document [title=" + title + ", content=" + content + ", author=" + author + "]";
	}
	
	public class DocumentMemento {
		
		private String title;
		private String content;
		private String author;
		
		public DocumentMemento(String title, String content, String author) {
			super();
			this.title = title;
			this.content = content;
			this.author = author;
		}
	}
}
```

```java
public class DocumentCareTaker {

	private Deque<DocumentMemento> history = new LinkedList<>();
	
	private Document originator;
	
	public DocumentCareTaker(Document originator) {
		this.originator = originator;
	}
	
	public void undo() {
		DocumentMemento memento = history.pop();
		if (memento != null) {
			originator.restore(memento);
		}
	}
	
	public void save() {
		history.push(originator.save());
	}
}
```

```java
	public static void main(String[] args) {
		
		Document doc = new Document("First document", "This is the first content.", "Valentin");
		
		DocumentCareTaker careTaker = new DocumentCareTaker(doc);
		careTaker.save();
		
		System.out.println(doc.toString());
		
		doc.appendContent(" This is an update");
		
		System.out.println(doc.toString());
		
		careTaker.undo();
		
		System.out.println(doc.toString());
	}
```

Résultat :

```
Document [title=First document, content=This is the first content., author=Valentin]
Document [title=First document, content=This is the first content. This is an update, author=Valentin]
Document [title=First document, content=This is the first content., author=Valentin]
```

## Observateur

**Objectif**

Permettre à certains objets d'envoyer des notifications concernant leur état à d'autres objets.

**Problème**

Lorsqu'un objet A souhaite connaître le changement d'état d'un autre objet B, il peut faire des appels vers cet objet B jusqu'à ce que l'état soit changé. Ou bien alors, l'objet B pourrait notifier tous les objets de son changement d'état, mais il pourrait alors notifier des objets qui ne sont pas intéressés par ce changement.

**Solution**

Créer une interface de souscription permettant de s'inscrire ou de se désinscrire à des évènements liés à un changement d'état d'un objet.

**Exemple**

```java
interface Subject {
    void registerObserver(BookObserver observer);
    void removeObserver(BookObserver observer);
    void notifyObservers();
}
```

```java
class BookStore implements Subject {
    private List<Book> books;
    private List<BookObserver> observers;

    public BookStore() {
        books = new ArrayList<>();
        observers = new ArrayList<>();
    }

    public void addBook(Book book) {
        books.add(book);
        notifyObservers();
    }

    public void removeBook(Book book) {
        books.remove(book);
        notifyObservers();
    }

    @Override
    public void registerObserver(BookObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(BookObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (BookObserver observer : observers) {
            observer.update(books);
        }
    }
}
```

```java
public interface BookObserver {
    void update(List<Book> books);
}
```

```java
class Customer implements BookObserver {
    private String name;

    public Customer(String name) {
        this.name = name;
    }

    @Override
    public void update(List<Book> books) {
        System.out.println(name + ": Book inventory updated. Total books available: " + books.size());
    }
}
```

```java
        BookStore bookStore = new BookStore();
        Customer customer1 = new Customer("John");
        Customer customer2 = new Customer("Emily");

        bookStore.registerObserver(customer1);
        bookStore.registerObserver(customer2);

        Book book1 = new Book("Java Programming");
        Book book2 = new Book("Design Patterns");

        bookStore.addBook(book1); // Notifies both customers
        bookStore.addBook(book2); // Notifies both customers

        bookStore.removeBook(book1); // Notifies both customers
```

Résultat :

```
John: Book inventory updated. Total books available: 1
Emily: Book inventory updated. Total books available: 1
John: Book inventory updated. Total books available: 2
Emily: Book inventory updated. Total books available: 2
John: Book inventory updated. Total books available: 1
Emily: Book inventory updated. Total books available: 1
```

## Etat

**Objectif**

Permettre de modifier le comportement d'un objet lorsque son état interne change, simulant ainsi un "changement de classe" de cet objet.

**Problème**

Il arrive qu'objet puisse avoir beaucoup d'états différents, impliquant alors un comportement différent pour une même action donnée selon son état.

La logique comportementale de transition d'état peut alors devenir très compliquée à mesure que le nombre d'états augmente et que les comportements se complexifient. Maintenir une telle logique comportementale dans l'objet devient alors impossible.

**Solution**

La solution revient à déléguer cette logique spécifique à chaque état dans des classes représentant les états de l'objet. Cela permet de cloisonner le comportement ainsi que la logique de transition d'état, appliquant ainsi les principes de responsabilité unique et ouvert/fermé.

**Exemple**

```java
public abstract class State {

	protected Player player;
	
	protected State(Player player) {
		this.player = player;
	}
	
	public abstract String onLock();
	public abstract String onPlay();
	public abstract String onNext();
	public abstract String onPrevious();
}
```

```java
public class ReadyState extends State {

    public ReadyState(Player player) {
        super(player);
    }

    @Override
    public String onLock() {
        player.changeState(new LockedState(player));
        return "Locked...";
    }

    @Override
    public String onPlay() {
        String action = player.startPlayback();
        player.changeState(new PlayingState(player));
        return action;
    }

    @Override
    public String onNext() {
        return "Locked...";
    }

    @Override
    public String onPrevious() {
        return "Locked...";
    }
}
```

```java
public class PlayingState extends State {

    PlayingState(Player player) {
        super(player);
    }

    @Override
    public String onLock() {
        player.changeState(new LockedState(player));
        player.setCurrentTrackAfterStop();
        return "Stop playing";
    }

    @Override
    public String onPlay() {
        player.changeState(new ReadyState(player));
        return "Paused...";
    }

    @Override
    public String onNext() {
        return player.nextTrack();
    }

    @Override
    public String onPrevious() {
        return player.previousTrack();
    }
}
```

```java
public class LockedState extends State {

    LockedState(Player player) {
        super(player);
        player.setPlaying(false);
    }

    @Override
    public String onLock() {
        if (player.isPlaying()) {
            player.changeState(new ReadyState(player));
            return "Stop playing";
        } else {
            return "Locked...";
        }
    }

    @Override
    public String onPlay() {
        player.changeState(new ReadyState(player));
        return "Ready";
    }

    @Override
    public String onNext() {
        return "Locked...";
    }

    @Override
    public String onPrevious() {
        return "Locked...";
    }
}
```

```java
public class Player {
    private State state;
    private boolean playing = false;
    private List<String> playlist = new ArrayList<>();
    private int currentTrack = 0;

    public Player() {
        this.state = new ReadyState(this);
        setPlaying(true);
        for (int i = 1; i <= 12; i++) {
            playlist.add("Track " + i);
        }
    }

    public void changeState(State state) {
        this.state = state;
    }

    public State getState() {
        return state;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public boolean isPlaying() {
        return playing;
    }

    public String startPlayback() {
        return "Playing " + playlist.get(currentTrack);
    }

    public String nextTrack() {
        currentTrack++;
        if (currentTrack > playlist.size() - 1) {
            currentTrack = 0;
        }
        return "Playing " + playlist.get(currentTrack);
    }

    public String previousTrack() {
        currentTrack--;
        if (currentTrack < 0) {
            currentTrack = playlist.size() - 1;
        }
        return "Playing " + playlist.get(currentTrack);
    }

    public void setCurrentTrackAfterStop() {
        this.currentTrack = 0;
    }
}
```

```java
public class UI {
    private Player player;
    private static JTextField textField = new JTextField();

    public UI(Player player) {
        this.player = player;
    }

    public void init() {
        JFrame frame = new JFrame("Test player");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel context = new JPanel();
        context.setLayout(new BoxLayout(context, BoxLayout.Y_AXIS));
        frame.getContentPane().add(context);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        context.add(textField);
        context.add(buttons);

        // Context delegates handling user's input to a state object. Naturally,
        // the outcome will depend on what state is currently active, since all
        // states can handle the input differently.
        JButton play = new JButton("Play");
        play.addActionListener(e -> textField.setText(player.getState().onPlay()));
        JButton stop = new JButton("Stop");
        stop.addActionListener(e -> textField.setText(player.getState().onLock()));
        JButton next = new JButton("Next");
        next.addActionListener(e -> textField.setText(player.getState().onNext()));
        JButton prev = new JButton("Prev");
        prev.addActionListener(e -> textField.setText(player.getState().onPrevious()));
        frame.setVisible(true);
        frame.setSize(300, 100);
        buttons.add(play);
        buttons.add(stop);
        buttons.add(next);
        buttons.add(prev);
    }
}
```

```java
public class StatePattern {
    public static void main(String[] args) {
        Player player = new Player();
        UI ui = new UI(player);
        ui.init();
    }
}
```

## Visiteur

**Objectif**

Permettre d'effectuer des traitements sur des structures complexes sans les associer aux classes du modèle.

**Problème**

Lorsque notre objet métier est complexe (notamment s'il s'agit d'un arbre ou d'un graphe), effectuer un traitement sur ce modèle peut s'avérer compliqué sans perturber les classes le représentant. De plus, l'ajout de nouveaux traitements augmente la complexité de ces objets.

**Solution**

Les traitements sont séparés des objets, les objets implémentent alors une interface commune ***Visited*** contenant une méthode **accept(Visitor v)**, et ***Visitor*** est une interface contenant une méthode **visit(Visited v)**. Chaque objet propage l'objet Visiteur à ses sous-objets.

**Exemple**

```java
public interface Visitor {
	void visitCity(City city);
	void visitBuilding(Building building);
	void visitFloor(Floor floor);
	void visitApartment(Apartment apartment);
}
```

```java
public interface Visited {
	void accept(Visitor visitor);
}
```

```java
public class City implements Visited {

	private String name;
	
	private List<Building> buildings = new ArrayList<>();
	
	public City(String name) {
		this.name = name;
	}
	
	public void setBuildings(List<Building> buildings) {
		this.buildings = buildings;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public void accept(Visitor visitor) {
		visitor.visitCity(this);
		for (Building building : buildings) {
			building.accept(visitor);
		}
	}
}
```

```java
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
```

```java
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
```

```java
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
```

```java
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
```

```java
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
```

```java
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
```

Résultat :

```
City: City1
	Building: Building1
		Floor: Floor1
			Apartment: Apartment1
			Apartment: Apartment2
	Building: Building2
		Floor: Floor2
			Apartment: Apartment3
Ville: City1
	Immeuble: Building1
		Etage: Floor1
			Appartement: Apartment1
			Appartement: Apartment2
	Immeuble: Building2
		Etage: Floor2
			Appartement: Apartment3
```

