# Aiheen kuvaus ja rakenne

## Aihe
*Prosessimuistinlukija ja -kirjoittaja*

Toteutetaan kirjasto, jolla voidaan kirjoittaa ja lukea toisen, samalla 
tietokoneella pyörivän prosessin muistia. Kirjaston rinnalle toteutetaan
graafinen käyttöliittymä. 

Kirjaston tulee tukea Windows 10 ja Ubuntu 16.XX -käyttöjärjestelmiä.
Ohjelman toimintaa ei määritellä muilla käyttöjärjestelmillä, eikä
alustoilla, jotka eivät käytä little-endian -tavujärjestystä tai kahden
komplementtijärjestelmää.

Kirjastolla tulee pystyä selaamaan tietokoneen prosessilistausta, sekä
manipuloimaan tietyn käyttäjätilassa pyörivän prosessin muistia. Kirjastolla
tulee pystyä kirjoittamaan ja lukemaan yleisimpien tietotyyppien lisäksi
mielivaltaisen pituisia tavumääriä prosessin muistin rajoissa.

Yleiset tietotyypit määritellään seuraavanlaisesti:

| Nimi | Lukukoko biteissä | Kirjaston tyyppi
|------|-------------------|-----------------
| bool | 8 | boolean
| char | 8 | char
| wchar | 16 | char
| short | 16 | short
| int | 32 | int
| float | 32 | float
| long | 64 | long
| double | 64 | double

Käyttöliittymän tulee tarjota käyttäjälle tapa lukea ja tallentaa omia
"kirjanmerkki"-muistiosotteita, esimerkiksi XML- tai JSON-tiedostoon.

Vahvistamattomia ideoita:
* muistista haku, eri tietotyypit, patterns (e.g. 2f 5d ?? ?? 90 dd)
* laajemman muistialueen esitys? reaaliaikainen (esim. pollaus joka 500ms) päivitys?
* konekäskyjen luku ja kirjoitus
* muistisegmenttien listaus
* muistisektorien listaus
* ladattujen moduulien listaus
* ladattujen moduulien pohjaosoitteiden ja pituuksien listaus
* moduulien imports/exports

## Käyttäjät
Käyttäjä

## Toiminnot
### Käyttäjä
* prosessien listaus
* prosessin valinta listasta
* prosessin valinta käyttäjän määrittämästä PIDistä
* prosessilistasta haku
* muistin luku, yleiset tietotyypit
* muistin kirjoitus, yleiset tietotyypit
* muistin luku, mielivaltainen määrä tavuja
* muistin kirjoitus, mielivaltainen määrä tavuja
* alustan valinta (Windows/Linux) asetuksista
* osoitekirjanmerkkien hallinta
* osoitekirjanmerkkien vienti
* osoitekirjanmerkkien tuonti
* ...

## Diagrammeja
![](https://raw.githubusercontent.com/cxcorp/lacuna/master/dokumentaatio/luokkakaavio.png)

# Rakennekuvaus
## core

Core-kirjaston ns. entry-luokka on [`LacunaBootstrap`](https://htmlpreview.github.io/?https://github.com/cxcorp/lacuna/blob/master/javadoc/index.html), joka tarjoaa staattiset metodit kirjaston alustamiselle eri alustoille. `LacunaBoostrap`-instanssi tarjoilee muut kirjaston päärajapinnat: `NativeProcessEnumerator` prosessien listaukselle, `NativeProcessCollector` yksittäisen prosessin tietojen hakemiselle, sekä `MemoryWriter` ja `MemoryReader` prosessin muistin luku- ja kirjoitusoperaatioille.

Kirjaston cross-platform-tuki perustuu neljään rajapintaan: `NativeProcessCollector`, `RawMemoryReader`, `RawMemoryWriter` ja `PidEnumerator`. Kukin alusta (`core.linux` ja `core.windows`) tarjoaa implementaatiot kyseisille rajapinnoille. `MemoryReaderImpl` ja `MemoryWriterImpl` kykenevät käsittelemään ns. common data types kääntämällä raa'at tavut kyseisiksi tietotyypeiksi. `NativeProcessEnumeratorImpl` toimii ensin listaamalla kaikki prosessit `PidEnumerator`-rajapinnalla, sitten noutamalla prosessien yksityiskohdat `NativeProcessCollector`-rajapinnalla.

Linux-alustan toiminnallisuus pyörii `/proc` kansion ympärillä, kun taas Windows-alustan toiminnallisuus riippuu täysin Windows API:sta. Windows API-tuki on toteutettu käyttämällä natiivikirjastoja (esim. Kernel32.dll, Advapi32.dll) JNA-kirjaston avulla.

## ui

Tämänhetkinen Swing-käyttöliittymä noudattaa pääosin Model-View-Presenter-patternia. Muistimanipulaatio on omassa Swing-komponentissaan, joka tällä hetkellä tarjoaa vain hex editor -tyyppisen näkymän `DeltaHex`-kirjaston avulla.
