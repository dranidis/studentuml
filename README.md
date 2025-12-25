# StudentUML

This repository contains the StudentUML software, code, files and folders necessary to make the app work. These are constantly being worked on by the Lead Developer.

For any issues regarding Documentation, please refer to the StudentUML-Doc repository.

### Package

```
mvn clean package
```

### Execute

```
java -jar target/studentuml-1.0.0-SNAPSHOT-jar-with-dependencies.jar
```

### How to (re)-create the local repository

```
mvn deploy:deploy-file -DgroupId=ubc.cs -DartifactId=jlogic -Dversion=1.0 -Durl=file:./local-maven-repo/ -DrepositoryId=local-maven-repo -DupdateReleaseInfo=true -Dfile=libs/jlogic.jar

mvn deploy:deploy-file -DgroupId=ubc.cs -DartifactId=builtinsLib -Dversion=1.0 -Durl=file:./local-maven-repo/ -DrepositoryId=local-maven-repo -DupdateReleaseInfo=true -Dfile=libs/builtinsLib.jar

mvn deploy:deploy-file -DgroupId=com.lipstikLF -DartifactId=lipstikLF -Dversion=1.0 -Durl=file:./local-maven-repo/ -DrepositoryId=local-maven-repo -DupdateReleaseInfo=true -Dfile=libs/lipstikLF-RC2.jar

mvn deploy:deploy-file -DgroupId=com.bulenkov -DartifactId=darcula -Dversion=2018.2 -Durl=file:./local-maven-repo/ -DrepositoryId=local-maven-repo -DupdateReleaseInfo=true -Dfile=libs/darcula.jar
```

### Settings are saved at:

```
~/.java/.userPrefs/edu/city/studentuml/util/prefs.xml
```

### Generate a PMD report

```
mvn jxr:jxr
mvn pmd:pmd
```

Open the `target/site/pmd.html` file in the browser.

### Generate a test coverage report

Run tests with coverage:

```
mvn clean test
```

Generate the coverage report:

```
mvn jacoco:report
```

Open the coverage report in your browser:

```
# Linux
xdg-open target/site/jacoco/index.html

# macOS
open target/site/jacoco/index.html

# Windows
start target/site/jacoco/index.html
```

The report shows:

-   **Overall coverage**: Percentage of instructions, branches, and methods covered
-   **Package-level coverage**: Detailed breakdown by package
-   **Class-level coverage**: Individual class coverage with line-by-line highlighting
-   **Color coding**: Green (covered), red (not covered), yellow (partially covered branches)

To run tests and generate the report in one command:

```
mvn clean test jacoco:report
```
