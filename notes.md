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
* KMP for search?
* find string
    - default to utf-8, provide drop-down menu with ALL found encodings
    - utf-8 at top of dropdown list, then separator, then rest of the encoding schemes
* wildcard bytes

## testtarget
* returns global struct's pointer with values
    - prints caps hex without 0x (uses PRIXPTR)

## ui
* store user's bookmarked memory addresses relative to the image base
