clear
host

list
of
collocations

loc
get
content
path
c
users
vanminh
documents
netbeansprojects
simplifyeng
generatescores
assets
data
corpus
simple
simple
txt


output
c
users
vanminh
documents
netbeansprojects
simplifyeng
generatescores
assets
data
corpus
simple
annotated


enfiles
get
path
c
users
vanminh
documents
netbeansprojects
simplifyeng
generatescores
assets
data
corpus
simple


pattern
a
za
z

foreach
utfile
in
enfiles




currentoutput
output
utfile
name

utcontent
get
content
utfile
n


write
host
utfile


utcontent
utcontent
tolower


utcontent
utcontent
replace
pattern


utcontent
utcontent
replace


utcontent
utcontent
split


potentialcoll




i



while
i
lt
utcontent
length




word
utcontent
i


if
word
eq
or
word
eq




i
i




else



potentialcoll
utcontent
i
utcontent
i


if
loc
contains
potentialcoll




i
i


word
collocation
potentialcoll
collocation


write
host
word



else



i
i




add
content
currentoutput
word








