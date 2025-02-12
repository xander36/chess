# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```


Stuff actually added by Alex
Phase 2 link
https://sequencediagram.org/index.html#initialData=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