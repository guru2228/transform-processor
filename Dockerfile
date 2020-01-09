FROM fabric8/java-jboss-openjdk8-jdk:1.5.4
ENV JAVA_APP_DIR=/deployments
EXPOSE 8080 8778 9779
COPY target/*.jar /deployments/
VOLUME ["/tmp"]
CMD ["/deployments/run-java.sh"]