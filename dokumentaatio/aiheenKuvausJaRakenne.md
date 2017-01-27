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
| byte | 8 | byte
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
![](http://i.imgur.com/OBtz63v.png)
