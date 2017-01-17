# Aiheen kuvaus ja rakenne

## Aihe
*Prosessimuistinlukija ja -kirjoittaja*

Toteutetaan kirjasto, jolla voidaan kirjoittaa ja lukea toisen, samalla 
tietokoneella pyörivän prosessin muistia. Kirjaston rinnalle toteutetaan
graafinen käyttöliittymä. 

Kirjaston tulee tukea Windows 10 ja Ubuntu 16.** -käyttöjärjestelmiä.
Ohjelman toimintaa ei määritellä muilla käyttöjärjestelmillä, eikä
alustoilla, jotka eivät käytä little-endian -tavujärjestystä ja kahden
komplementtijärjestelmää.

Kirjastolla tulee pystyä selaamaan tietokoneen prosessilistausta, sekä
manipuloimaan tietyn käyttäjätilassa pyörivän prosessin muistia. Kirjastolla
tulee pystyä kirjoittamaan ja lukemaan yleisimpien tietotyyppien lisäksi
mielivaltaisen pituisia tavumääriä prosessin muistin rajoissa.

Yleiset tietotyypit määritellään seuraavanlaisesti:

Nimi | Lukukoko biteissä | Kirjaston tyyppi
--------------------
bool | 8 | boolean
char | 8 | char
byte | 8 | byte
wchar | 16 | char
short | 16 | short
int | 32 | int
float | 32 | float
long | 64 | long
double | 64 | double

Käyttöliittymän tulee tarjota käyttäjälle tapa lukea ja tallentaa omia
"kirjanmerkki"-muistiosotteita, esimerkiksi XML- tai JSON-tiedostoon. Muistiosoitteita
ei käsitellä pysyvyystarkoituksissa absoluuttisina, vaan suhteessa
prosessin kuvan pohjaan (esim. kun kuvan pohjaosoite on 0x400000 ja käyttäjän
kirjanmerkki on 0x49FDAA, tallennetaan osoite muodossa 0x9FDAA, eikä 0x49FDAA.

## Käyttäjät
Käyttäjät

## Toiminnot

