## platform-specific
* dependency-inject memory functions
    - autodetect on first start: "Your platform has been autodetected to be XXX, change in settings"
    - platform in settings
* deploy testtarget for windows and linux, should behave the same on all platforms
* JNA takes like 11 seconds to initialize on first library load

## process listing
* pid, command
* order by launch time
    - icon?
* window listing?

## memory search
* boyer-moore?
* find string
    - default to utf-8, provide drop-down menu with ALL found encodings
    - utf-8 at top of dropdown list, then separator, then rest of the encoding schemes
* wildcard bytes

## testtarget
* returns global struct's pointer with values
    - prints caps hex without 0x (uses PRIXPTR)

## ui
* store user's bookmarked memory addresses relative to the image base

## hax
### heroes3 complete edition GOG version, GOG patch applied
* state struct info:
    - (max 7 players at once??), player is index 6 in struct pointer list
* state struct:

```
00  int32_t unk
...
90  int32_t wood
84  int32_t mercury
8C  int32_t stones
90  int32_t some_gold_powder
94  int32_t crystals
98  int32_T candy_shit
9C  int32_t money

size: 0xA0 = 160
```
