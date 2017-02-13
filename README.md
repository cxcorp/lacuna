# lacuna

[![Travis Build Status](https://travis-ci.org/cxcorp/lacuna.svg?branch=master&updatecachepls=2)](https://travis-ci.org/cxcorp/lacuna) [![Coverage Status](https://coveralls.io/repos/github/cxcorp/lacuna/badge.svg?branch=master&updatecachepls=2)](https://coveralls.io/github/cxcorp/lacuna?branch=master)

Program for reading and writing other programs' memory.
Coursework for [Javalabra-2017-3](https://github.com/javaLabra/Javalabra2017-3).

## Current state
Lacuna can currently dump the active process list and read a process' memory on both Windows and Linux. Reading supports common primitive data types as well as arbitrary byte amounts. Writing memory is supported on Windows.

![](http://i.imgur.com/PMrn1BA.png)

Tested platforms:
* Windows:
  - Microsoft Windows 8.1 Pro (Build 9600)
  - Microsoft Windows 10 Pro N (Build 14393)
* Linux:
  - Ubuntu 16.04.1 LTS (Kernel 4.4.0-59-generic x86_64 GNU/Linux)
  
## Links
* [**Javadoc**](https://htmlpreview.github.io/?https://github.com/cxcorp/lacuna/blob/master/javadoc/index.html)
* [**Documentation**](https://github.com/cxcorp/lacuna/tree/master/dokumentaatio) (spec, diagrams, reports)
* [**Trello**](https://trello.com/b/KGL8icHx/lacuna)

## Download
See [**Releases**](https://github.com/cxcorp/lacuna/releases) for ready jars. Run with `java -jar lacuna-<VERSION>.jar`

For compilation instructions, see below.

## Compiling
### Maven
1. Install [Apache Maven](https://maven.apache.org/install.html). Lacuna uses Maven to manage dependencies and lifecycle scripts.
2. Clone or download this repository.
3. Go to the project folder: `cd lacuna/lacuna`
4. Compile and package the project: `mvn clean package`. This step generates the .jar file in the target/ directory.
  - You may optionally choose to skip running unit tests: `mvn clean package -DskipTests`
5. Run Lacuna: `java -jar target/lacuna-<VERSION>.jar`

## License
Lacuna is licensed under the MIT License (Expat). See LICENSE.
Lacuna uses third party libraries that are distributed under their own terms (see LICENSE-3RD-PARTY).
