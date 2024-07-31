FROM mcr.microsoft.com/openjdk/jdk:21-ubuntu
LABEL authors="gabriela"
ADD /build/libs/application.jar /application.jar
RUN mkdir /data
ENTRYPOINT ["java","-jar","/application.jar", "/data"]
