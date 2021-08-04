# Dosis-Connector

<img src="documentation/logo-aio.svg" align="right" alt="Agentschap Innovatie en Ondernemen" width="120">

## Wat is dosis-connector?

De dosis-connector is een vrijstaande applicatie die op gegevens afhaalt van een of 
meerdere dossierbeheersystemen, en vervolgens deze gegevens doorstuurt naar DOSIS. 
De frequentie waarmee gegevens worden afgehaald is instelbaar, alsook de verschillende
systemen waar dit moet gebeuren. Het project veronderstelt wel dat alle 
geconfigureerde systemen waarvan gegevens worden afgehaald behoren tot dezelfde
bron (d.w.z. ze hebben globaal een unieke id voor hun dossiers).

## Opbouw van de repository

* documentation: deze folder bevat de figuren van de documentatie
* dosis-connector: deze folder bevat de broncode van het dosis-connector project
* dbssim: deze folder bevat een simulator voor het dossierbeheersysteem welke kan
gebruikt worden om de dosis-connector mee te testen.

## Snel van start

### Dosis-Connector bouwen

```
cd dosis-connector
mvn clean install
```

Een ```mvn clean install``` is enkel nodig indien men de dossierbeheersysteem simulator 
nodig heeft. Indien niet volstaat een ```mvn clean package```.

Men kan de connector opstarten als volgt:
```
java -jar .\controller\target\dosis-connector-controller-0.0.1-SNAPSHOT.jar
```
Dit zal de connector opstarten op basis van de interne properties file. Commandline
kan een externe property file worden meegegeven:

### Dossierbeheersysteem simulator bouwen en uitvoeren
Om de simulator voor het dossierbeheersysteem te bouwen doet men vervolgens: 
```
cd dbssim
mvn clean package
```
Dit genereert een jar bestand onder target: *dbssim-0.0.1-SNAPSHOT.jar* dat kan worden
uitgevoerd met ```java -jar dbssim-0.0.1-SNAPSHOT.jar``` Dit start vervolgens de 
simulator op welke standaard op poort 8080 draait.

Er is tevens een dockerfile meegeleverd waarmee de simulator makkelijk in een docker
container worden opgestart:

```
cd dbssim
docker build -t vlaio/dbssim .
docker run -p 8080:8080 vlaio/dbssim
```

## Opbouw van de dosis-connector

Conceptueel wordt de dosis-connector voorgesteld door volgend schema:

<img src="documentation/ConnectorArchitectuur.png" alt="Architectuur Diagramma"/>

## Componenten
### De Poller
### De Controller
### De Pusher
### De Validator
### WorkInProgress & DiskStore
## Onderlinge afhankelijkheden
