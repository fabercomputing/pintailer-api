FROM gradle:4.10.2 as pintailer-api-gradle-build
RUN mkdir -p ~/servercode
COPY --chown=gradle ./pintailer-api /home/gradle/servercode
WORKDIR /home/gradle/servercode
RUN ls -l
#RUN rm -rf /home/gradle/servercode/build/libs
RUN ["/usr/bin/gradle", "build"]

FROM java:8-jdk-alpine
RUN mkdir /runjar
RUN apk add --no-cache ttf-dejavu
WORKDIR /runjar
COPY --from=pintailer-api-gradle-build /home/gradle/servercode/build/libs/fw_test_mgmt-0.0.1-SNAPSHOT.jar .
RUN ls -l 
EXPOSE 8080
CMD ["java",  "-jar",  "/runjar/fw_test_mgmt-0.0.1-SNAPSHOT.jar"]