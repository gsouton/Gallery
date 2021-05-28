FROM maven:3.6.0-jdk-11-slim as build

COPY pom.xml /home/app/backend/

RUN mvn -f /home/app/backend/pom.xml dependency:go-offline -B


COPY src /home/app/backend/src

RUN rm -rf /home/app/backend/src/test

RUN mvn -f /home/app/backend/pom.xml  package -DskipTests

FROM openjdk:11-jre-slim as gallery-application

COPY --from=build /home/app/backend/target/backend-0.0.1-SNAPSHOT.jar /usr/local/lib/gallery.jar

COPY wait-for-it.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/wait-for-it.sh


EXPOSE 8080

CMD ["./usr/local/bin/wait-for-it.sh", "mysql-db:3306", "--timeout=120", "--strict", "--", "java", "-jar", "/usr/local/lib/gallery.jar"]

#ENTRYPOINT [ "java", "-jar", "/usr/local/lib/gallery.jar" ]

