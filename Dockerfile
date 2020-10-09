FROM openjdk:11

COPY target/fluree-events.standalone.jar /home/fluree-events.jar
COPY logback.xml /home/logback.xml

EXPOSE 8080

ENV FLUREE_SERVER=http://localhost:8080 \
    FLUREE_LEDGER=my/ledger

CMD ["java", "-Xms2g", "-Xmx2g", "-jar", "/home/fluree-events.jar"]
