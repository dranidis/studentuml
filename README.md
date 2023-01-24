# StudentUML

This repository contains the StudentUML software, code, files and folders necessary to make the app work. These are constantly being worked on by the Lead Developer.

For any issues regarding Documentation, please refer to the StudentUML-Doc repository. 

### Package
```
mvn clean package -DskipTests 
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


