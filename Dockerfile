FROM mcr.microsoft.com/openjdk/jdk:21-ubuntu
LABEL authors="gabriela"
ADD /build/libs/application.jar /application.jar
RUN mkdir "/data"
COPY src/main/resources /data
ENTRYPOINT ["java","-jar","/application.jar", "/test"]
