# lacuna

Program for reading and writing other programs' memory.
Coursework for [Javalabra-2017-3](https://github.com/javaLabra/Javalabra2017-3).

## Current state
Lacuna can currently dump the active process list on both Windows and Linux. Memory reading is supported on Windows via the `WindowsMemoryReader` class.

Tested platforms:
* Windows:
  - Microsoft Windows 10 Pro N (Build 14393)
* Linux:
  - Ubuntu 16.04.1 LTS (Kernel 4.4.0-59-generic x86_64 GNU/Linux)

## Download
See [Releases](https://github.com/cxcorp/lacuna/releases) for ready jars. See **Compiling** for compilation instructions.

## Compiling
### Maven
1. Install [Apache Maven](https://maven.apache.org/install.html). Lacuna uses Maven to manage dependencies and manage lifecycle scripts.
2. Clone or download this repository.
3. Go to the project folder: `cd lacuna/lacuna`
4. Compile and package the project: `mvn clean package`. This step generates the .jar file in the target/ directory.
  - You may optionally choose to skip running unit tests: `mvn clean package -DskipTests`
5. Run Lacuna: `java -jar target/lacuna-<VERSION>.jar`

### Sample output
#### Linux
![](http://i.imgur.com/mhiWzFU.png)

#### Windows
![](http://i.imgur.com/IoEvW2d.png)

## Links
* [**Documentation**](https://github.com/cxcorp/lacuna/tree/master/dokumentaatio)
* [**Trello**](https://trello.com/b/KGL8icHx/lacuna)

## License
Lacuna is licensed under the MIT License (Expat).
See LICENSE.
