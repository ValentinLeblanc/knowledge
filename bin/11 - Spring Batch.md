# Spring Batch

## Présentation

Il s'agit d'une infrastructure qui permet de faire des développements par lots (batch) légère et complète. Il est utilisé lorsqu'un grand volume de données doit être traité de façon automatique, donc lorsqu'on doit exécuter un **Batch Processing**.

**Un programme Batch :**

- lit un grand nombre de données depuis une BDD, un fichier ou une queue
- traite les données
- écrit les données sous une forme modifiée

Spring Batch est l'un des rares Framework open source offrant une solution robuste pour ce type de processing.

**Exemple d'utilisations métier :**

- une entreprise doit effectuer le virement des salaires aux employés
- le traitement des bulletins de salaire à la fin de chaque mois
- envoie de courriels de communication de masse
- générer des rapports automatisés sur une base quotidienne, hebdo ou mensuelle
- exécution automatique d'un flux de travail sans intervention humaine
- soumettre un traitement par lots de façon périodique
- faire un traitement par lots de façon massive en parallèle

**Exemple d'utilisations techniques :**

- exécuter automatiquement des tests unitaires périodiquement
- effectuer des mises à jour automatiques de bases de données
- définir un système de file d'attente pour traiter un grand nombre de traitements
- être utilisé avec des API pour faire un contrôle d'intégrité du serveur ou de l'appli, générer des données factices...

## Architecture

C'est une architecture à 3 couches :

- **Application**

  contient tous les traitements par lots et le code personnalisé écrit par le développeur

- **Batch Core**

  c'est le noyau qui contient les classes d'exécution principales au lancement et au contrôle d'un lancement de batch (**JobLauncher**, **Job** et **Step**)

- **Batch Infrastructure**

  Application et Batch Core sont tous les deux construits sur une infrastructure commune. Elle contient des **Readers**, des **Writers** et des **services de traitement courants**

## Concepts fondamentaux

Un traitement par lot est défini par :

- un **Job** qui se compose de plusieurs **Step**
- chaque **Step** a un seul **ItemReader**, **ItemProcessor** et **ItemWriter**
- un **Job** est exécuté par un **JobLauncher**
- les métadonnées relatives aux traitements configurés et exécutés sont stockées dans un **JobRepository**

## Job

Un Job est une entité qui encapsule un processus de traitement par lots complet.

Comme avec les autres projets de Spring, un Job est associé à :

- un fichier de configuration XML

  OU

- une classe de configuration Java

Un **Job** est un conteneur pour les instances **Step**. Chaque Job peut être associé à plusieurs **JobInstance**, chacune étant définie de manière unique par ses **JobParameters**

Chaque exécution d'une **JobInstance** est appelée **JobExecution**.

Chaque JobExecution suit ce qui s'est passé lors d'une exécution comme les statuts actuels, de sortie, les heures de début, de fin...

## Step

C'est une phase spécifique indépendante d'un travail par lots.

Chaque **Step** utilise :

- un **ItemReader** permettant de lire et désérialiser les enregistrements Batch à partir d'une entrée
- un **ItemProcessor** permettant de traiter les items
- un **ItemWriter** permettant d'écrire les enregistrements traités par ItemProcesor dans une sortie quelconque

## StepConfiguration

Spring Batch utilise le modèle **Chunk-oriented processing** : les données sont lues une par une et des morceaux sont créés avant d'être écrits dans une transaction.

Un élément est lu par un **ItemReader**, confié à un **ItemProcessor** et agrégé.

Lorsque le nombre d'éléments agrégés est égal à l'intervalle de validation, l'élément complet est écrit par un **ItemWriter** puis la transaction est validée

## ExecutionContext

C'est un ensemble de paires de clé/valeur contenant des informations relatives à **StepExecution** et **JobExecution**.

## JobRepository

C'est un mécanisme qui rend toute persistance possible. Il fournit des opérations CRUD pour les instanciations de **JobLauncher**, **Job** et **Step**.

## JobLauncher

Il permet de démarrer un **Job** en lui associant des **JobParameters**.

## Exemple

```java
@Configuration
public class SpringBatchConfig {

	@Value("${inputFile}")
	private Resource inputFile;

	@Bean
	public Job bankJob(JobRepository jobRepository, ItemWriter<BankTransaction> bankTransactionItemWriter,
			PlatformTransactionManager transactionManager,
			ItemProcessor<BankTransaction, BankTransaction> bankTransactionItemProcessor) {
		Step step = new StepBuilder("step-load-data", jobRepository)
				.<BankTransaction, BankTransaction>chunk(100, transactionManager).reader(itemReader())
				.processor(bankTransactionItemProcessor).writer(bankTransactionItemWriter).build();

		return new JobBuilder("bank-data-loader-job", jobRepository).start(step).build();
	}

	@Bean
	public ItemReader<BankTransaction> itemReader() {
		FlatFileItemReader<BankTransaction> flatFileItemReader = new FlatFileItemReader<>();
		flatFileItemReader.setName("CSV-READER");
		flatFileItemReader.setResource(inputFile);
		flatFileItemReader.setLinesToSkip(1);
		flatFileItemReader.setLineMapper(lineMapper());
		return flatFileItemReader;
	}

	@Bean
	public LineMapper<BankTransaction> lineMapper() {
		DefaultLineMapper<BankTransaction> lineMapper = new DefaultLineMapper<>();
		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
		lineTokenizer.setDelimiter(",");
		lineTokenizer.setStrict(false);
		lineTokenizer.setNames("id", "accountID", "strTransactionDate", "transactionType", "amount");
		lineMapper.setLineTokenizer(lineTokenizer);
		BeanWrapperFieldSetMapper<BankTransaction> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
		fieldSetMapper.setTargetType(BankTransaction.class);
		lineMapper.setFieldSetMapper(fieldSetMapper);
		return lineMapper;
	}

}
```

```java
@RestController
public class JobRestController {

	@Autowired
	private JobLauncher jobLauncher;
	
	@Autowired
	private Job job;
	
	@GetMapping("/startJob")
	public BatchStatus load() throws Exception {
		
		Map<String, JobParameter<?>> parameters = new HashMap<>();
		parameters.put("time", new JobParameter<>(System.currentTimeMillis(), Long.class));
		JobParameters jobParameters = new JobParameters(parameters);
		
		JobExecution jobExecution = jobLauncher.run(job, jobParameters);
		
		while (jobExecution.isRunning()) {
			System.out.println("...");
		}
		
		return jobExecution.getStatus();
	}
}
```

```java
@Entity
public class BankTransaction {

	@Id
	private Long id;
	private Long accountID;
	private Date transactionDate;
	@Transient
	private String strTransactionDate;
	private String transactionType;
	private double amount;
    
    // getters, setters
}
```

```java
@Component
public class BankTransactionItemProcessor implements ItemProcessor<BankTransaction, BankTransaction> {

	private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy-HH:mm");
	
	@Override
	public BankTransaction process(BankTransaction bankTransaction) throws Exception {
		bankTransaction.setTransactionDate(dateFormat.parse(bankTransaction.getStrTransactionDate()));
		return bankTransaction;
	}
}
```

```java
@Component
public class BankTransactionItemWriter implements ItemWriter<BankTransaction> {

	@Autowired
	private BankTransactionRepository bankTransactionRepository;
	
	@Override
	public void write(Chunk<? extends BankTransaction> chunk) throws Exception {
		bankTransactionRepository.saveAll(chunk);
	}
}
```

```java
public interface BankTransactionRepository extends JpaRepository<BankTransaction, Long> {
}
```

Fichier **application.properties** :

```properties
inputFile=classpath:/data.csv
spring.batch.job.enabled=false
```

Fichier **data.csv** : (dans ***src/main/resources***)

```css
transaction_id,account_number,transaction_date,transaction_type,transaction_amount
540300,10025436,17/10/2018-09:44,D,10000.570
545004,48541165,18/10/2018-12:55,D,1645.23
840407,64829873,18/10/2018-14:31,D,6987.41
140321,22629137,19/10/2018-23:02,D,110.22
```

