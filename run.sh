
rm -r out
shopt -s globstar

javac src/**/*.java -d out
cp -r src/resources out
java -cp out JChess