# ASIP BNF

This document will describe the Bacchus-Naur form of the newly developed
protocol ASIP.

## Basiscs

```javascript
letter                  = 'A' | 'B' | 'C' | 'D' | 'E' | 'F' | 'G'
                        | 'H' | 'I' | 'J' | 'K' | 'L' | 'M' | 'N'
                        | 'O' | 'P' | 'Q' | 'R' | 'S' | 'T' | 'U'
                        | 'V' | 'W' | 'X' | 'Y' | 'Z' | 'a' | 'b'
                        | 'c' | 'd' | 'e' | 'f' | 'g' | 'h' | 'i'
                        | 'j' | 'k' | 'l' | 'm' | 'n' | 'o' | 'p'
                        | 'q' | 'r' | 's' | 't' | 'u' | 'v' | 'w'
                        | 'x' | 'y' | 'z' ;
digit                   = '0' | '1' | '2' | '3' | '4'
                        | '5' | '6' | '7' | '8' | '9' ;
number                  = digit {digit} ;
floatNumber             = number '.' number
character               = letter | digit ;
text                    = character {character} ;
bool                    = 'Y' | 'N' ;
void                    = '' ;
```

## ASIP-Message

```javascript
cmd | message | unit    = '{'
                            '"version":' floatNumber ','
                            '"format":' text ','
                            '"encryptedKey":' text | void ','
                            '"sender":' peerSemanticTag ','
                            '"receiver":' '[' optionalSemanticTags ']' ','
                            '"signed":' bool ','
                            '"ttl":' number','
                            '"content":' content
                          '}' signature ;
optionalSemanticTag     = peerSemanticTag | spatialSemanticTag | timeSemanticTag ;
optionalSemanticTags    = optionalSemanticTag { ',' optionalSemanticTag } ;
content                 = '{'
                            '"logSender":' logicalSender ','
                            '"signed":' bool ','
                            insert | expose | raw
                          '}' signature ;
signature               = text ;
insert                  = '"insert":' '[' {knowledges} ']' ;
expose                  = '"expose":' '[' {interests} ']' ;
raw                     = '"raw":' text ;
logicalSender           = peerSemanticTag ;

```

## ASIP-SemanticTag

```javascript

semanticTagName         = '"name":' name ;
semanticTagSI           = '"sis":' '[' {subjectIdentifiers} ']' ;
semanticTag             = '{'
                            semanticTagName ','
                            semanticTagSI
                          '}' ;
semanticTags            = semanticTag { ',' semanticTag } ;
peerSemanticTag         = '{'
                            semanticTagName ','
                            semanticTagSI ','
                            '"addresses":' '[' {addresses} ']'
                          '}' ;
peerSemanticTags        = peerSemanticTag { ',' peerSemanticTag } ;
spatialSemanticTag      = '{'
                            semanticTagName ','
                            semanticTagSI ','
                            '"locations":' '[' {locations} ']'
                          '}' ;
spatialSemanticTags     = spatialSemanticTag { ',' spatialSemanticTag } ;
timeSemanticTag         = '{'
                            semanticTagName ','
                            semanticTagSI ','
                            '"times":' '[' {times} ']'
                          '}' ;
timeSemanticTags        = timeSemanticTag { ',' timeSemanticTag } ;
subjectIdentifiers      = subjectIdentifier { ',' subjectIdentifier } ;
subjectIdentifier       = uri ;
uri                     = '"uri":' http://www.w3.org/Addressing/URL/5_URI_BNF.html ;
address                 = '{'
                            '"address":' gcf
                          '}' ;
addresses               = address | { ',' address } ;
gcf                     = tcpEndpoint | httpEndpoint | mailEndpoint ;
endPoint                = ( character | '.' | '-' ) { ( character | '.' | '-' ) } ;
port                    = number ;
tcpEndpoint             = 'tcp' '://' endPoint [ ':' port ] ;
httpEndpoint            = 'http' '://' endPoint [ ':' port ] ;
mailEndpoint            = 'mail' '://' user '@' endPoint [ ';' mbSize ] ;
user                    = text
mbSize                  = number ;
locations               = location { ',' location } ;
location                = '{'
                            '"location":' ewkt
                          '}' ;
ewkt                    = defined at:
                          http://docs.opengeospatial.org/is/12-063r5/12-063r5.html ;
time                    = '{'
                            '"from":' utcTime ',' unixTime ',' sharkTime ','
                            '"duration":' number
                          '}' ;
utcTime                 = '"utcTime":' https://www.ietf.org/rfc/rfc3339.txt Part 5.6 ;
unixTime                = '"unixTime":' number ;
sharkTime               = '"sharkTime":' void ;
name                    = text ;
```

## ASIP-Commands

### Insert

```javascript
knowledge               = '{'
                            '"vocabulary":' vocabulary ','
                            '"infoData":' '[' infoDatas ']' ','
                            infoContent
                          '}' ;
knowledges              = knowledge { ',' knowledge } ;
infoData                = '{'
                            '"infoSpace":' infoSpace ','
                            '"infoMetaData":' '[' [infoMetaDatas] ']'
                          '}' ;
infoDatas               = infoData { ',' infoData } ;
infoMetaData            = '{'
                            '"name": ' text ','
                            '"offset":' number ','
                            '"length":' number
                          '}' ;
infoMetaDatas           = infoMetaData { ',' infoMetaData } ;              
infoContent             = '{'
                            '"byteStream":' text
                          '}' ;
infoSpace               = '{'
                            '"topics":' [ semanticTags ] ','
                            '"types":' [ semanticTags ] ','
                            '"approvers":' [ peerSemanticTags ] ','
                            '"sender":' [ peerSemanticTag ]','
                            '"recipients":' [ peerSemanticTags ] ','
                            '"locations":' [ spatialSemanticTags ] ','
                            '"times":' [ timeSemanticTags ]
                          '}' ;

// TODO: Check Vocabulary
vocabulary              = '{'
                            topicDim ','
                            typeDim ','
                            peerDim ','
                            locationDim ','
                            timeDim
                          '}' ;
topicDim                = semanticNet ;
typeDim                 = semanticNet ;
peerDim                 = peerSemanticTagNet ;
locationDim             = spatialSemanticTagNet ;
timeDim                 = timeSemanticTagNet ;

// TODO: Check SemanticNets
semanticNet             = '{'
                            '"stTable":' '['
                              { semanticTagId ',' semanticTag } ','
                            '"relations":' '[' {property} ']'
                          '}' ;
peerSemanticNet         = '{'
                            '"stTable":' '['
                              { semanticTagId ',' peerSemanticTag }
                             ']' ','
                            '"relations":' '[' { property} ']'
                          '}' ;
spatialSemanticNet      = '{'
                            '"stTable":' '['
                              { semanticTagId ',' spatialSemanticTag }
                            ']' ','
                            '"relations":' '[' {property} ']'
                          '}' ;
timeSemanticNet         = '{'
                            '"stTable":' '['
                              { semanticTagId ',' timeSemanticTag }
                            ']' ','
                            '"relations":' '[' { property} ']'
                          '}' ;
semanticTagId           = '{' '"id":' text '}' ;
property                = '{'
                            '"name":' text
                            '"sourceId":' text
                            '"targetId":' text
                          '}' ;
```

### Expose
```javascript

interest                = '{'
                            '"topics":' '[' {semanticTags} | void ']' ','
                            '"types":' '[' {semanticTags} | void ']' ','
                            '"approvers":' '[' {peerSemanticTags} | void ']' ','
                            '"sender":' peerSemanticTag | void ','
                            '"recipients":' '[' {peerSemanticTags} | void ']' ','
                            '"locations":' '[' {spatialSemanticTags} | void ']' ','
                            '"times":' '[' {timeSemanticTags} | void ']' ','
                            '"direction":' 'IN' | 'OUT' | 'INOUT' | 'NO' '}'
                          '}' ;
interests               = interest { ',' interest } ;

```
