# software-engineering-1
This repository is dedicated to projects of Software-Engineering1 which is a course in my fifth term of cumputer jurney in Univesity of Zanjan.(Mehr 1405))

## finalproject_ap — University Library Management System

A console-based library management system written in Java (tested with JDK 8).

### Storage
All data (students, employees, books, loans, loan requests, blacklist) is persisted
in an **SQLite** database file `library.db`, which is created automatically on the
first run and rewritten on every clean exit. The SQLite JDBC driver is bundled in
`finalproject_ap/lib/`.

### How to run (Windows)
Double-click `finalproject_ap\run.bat`, or run manually:

```
cd finalproject_ap
javac -encoding UTF-8 -d out *.java
java -cp "out;lib\sqlite-jdbc-3.45.1.0.jar;lib\slf4j-api-1.7.36.jar;lib\slf4j-nop-1.7.36.jar" ap.exercises.finalproject_ap.LibrarySystem
```

Default headmaster login: username `Saba1404`, password `s14B04`.

