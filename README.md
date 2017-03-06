# lacuna

[![Travis Build Status](https://travis-ci.org/cxcorp/lacuna.svg?branch=master&updatecachepls=2)](https://travis-ci.org/cxcorp/lacuna)

Program for reading and writing other programs' memory.
Coursework for [Javalabra-2017-3](https://github.com/javaLabra/Javalabra2017-3).

## Current state
The Lacuna library (`lacuna-core`) is mostly feature-complete. It has classes for enumerating processes, and reading and writing a process's memory on both Windows and Linux. Common primitive datatypes are supported in addition to raw bytes. The library is quite inefficient currently, as it opens and closes a process handle on _each read or write_ ([#14](https://github.com/cxcorp/lacuna/issues/14)), and throws exceptions to signify failed reads ([#12](https://github.com/cxcorp/lacuna/issues/12)).

The GUI (`lacuna-ui`) can currently enumerate the processes, and read and write raw bytes with a hex editor component. Common data types can be written with a data inspector gadget.

![](http://i.imgur.com/7nx3zf8.png)
![](http://i.imgur.com/ESrx0Us.png)

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
See [**Releases**](https://github.com/cxcorp/lacuna/releases) for ready jars. Run with `java -jar lacuna-ui-<VERSION>.jar`

For compilation instructions, see below.

## Compiling
### Maven
1. Install [Apache Maven](https://maven.apache.org/install.html). Lacuna uses Maven to manage dependencies and lifecycle scripts.
2. Clone or download this repository.
3. Go to the project folder: `cd lacuna/lacuna`
4. Compile and package the project: `mvn clean install package`. This step generates the .jar files in each of the modules' target/ directories.
  - You may optionally choose to skip running unit tests: `mvn clean install package -DskipTests`
  - Note: the `install` phase is needed so that `lacuna-ui` finds `lacuna-core`.
5. Run the Lacuna UI: `java -jar lacuna-ui/target/lacuna-ui-<VERSION>.jar`

## License
Lacuna is licensed under the MIT License (Expat). See LICENSE.
Lacuna uses third party libraries that are distributed under their own terms (see LICENSE-3RD-PARTY).
