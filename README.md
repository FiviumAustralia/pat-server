# rns-java-backend
Backend source for RNS
[![Build Status](https://travis-ci.com/FiviumAustralia/rns-java-backend.svg?token=2xoKEZRZZcSoW7ymB1Yt&branch=development)](https://travis-ci.com/FiviumAustralia/rns-java-backend)

Setup Instructions

1. Download and install Eclipse IDE: https://www.eclipse.org/downloads/?
2. Download and install Tomcat: http://tomcat.apache.org/
3. Follow Tomcat setup for Eclipse: https://www.eclipse.org/webtools/jst/components/ws/1.0/tutorials/InstallTomcat/InstallTomcat.html
4. In the eclipse IDE, open the server properties (Double click the Tomcat server item under the Servers tab) and select: Server Locations -> Use Tomcat Installation (takes control of Tomcat Installation)
5. Import the rns-java-backend project into eclipse (using the import option "Existing Maven Projects")
6. Start your Tomcat server and deploy the rns-java-backend project to the server

You should now have the backend running at http://localhost:8080/rns-java-backend-war/RNS_Backend
if you open this address in your browser you should see a line of text saying: "Invalid Request, expected JSON postdata was missing."
How to Make valid requests will be explained shortly.

Database Setup

Before making requests to this service, you will need to first set up a database.  To do this follow these steps:

7. Download and install mariadb https://downloads.mariadb.org/
8. Download and install HeidiSQL https://www.heidisql.com/download.php
9. Open HeidiSQL and run the latest database creation query: https://github.com/FiviumAustralia/rns-java-backend/raw/master/integration/create_rns_db.sql

Testing the Backend

The easiest way to test the backend is to use Postman (Google Chrome App).

10. Install and run Postman
11. Set the URL to http://localhost:8080/rns-java-backend-war/RNS_Backend
12. Set the HTTP request type to POST
13. Select the Headers tab and enter the following: Content-Type:application/json
14. Select the Body tab and select raw
15. Enter the body content then click Send

Example body content

{"graphQL_Query":"{ AuthenticatePatient(p_id: \"batman\" ) { jwt_token } }"}

Mac Mini Deployment Instructions
1.  Download the backend source
2.  Download and install Maven
3.  Download Tomcat v9.x (.zip core) from http://tomcat.apache.org/download-90.cgi
4.  Extract the .zip file somewhere convenient
5.  Open $TOMCAT_HOME/conf/tomcat-users.xml and add the following 2 lines inside the ``<tomcat-users>`` section:
```
<role rolename="manager-gui"/>
<user username="admin" password="admin" roles="manager-gui"/>
```
6.  Open a terminal and execute startup.sh to start the server (located at: $TOMCAT_HOME\bin\startup.sh)
7.  Open a terminal at the java backend's root directory (which contains the pom.xml file) and run 'mvn clean install'
8.  After the build succeeds, open a web browser and navigate to: http://localhost:8080/manager/html (login with: username: 'admin', password: 'admin')
9.  Use the UI to deploy the .war file that has been created in the target directory (which is located in the backend's root directory)


Request properties file{
  "async": true,
  "crossDomain": true,
  "url": "http://localhost:8000/rns-java-backend-war/RNS_Backend",
  "method": "POST",
  "headers": {
    "content-type": "application/json",
    "authorization": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJiYXRtYW4ifQ.edQyAXK65KoAY-6_4RbCidMGhbrCaZ_-RgtFpuR9Q0TlIEbcvZUns3Pqg4pShlvLqwTG9lJy6qrZYVOSwkGNEw",
    "cache-control": "no-cache",
    "postman-token": "4f01b451-1c67-4886-c46b-320bf9da7c04"
  },
  "processData": false,
  "data": "{\"graphQL_Query\":\"{RetrieveData( user:\\\"superuser\\\", password:\\\"password12\\\") {data} }\"}"
}

Push newly generated patient id
{
  "async": true,
  "crossDomain": true,
  "url": "http://localhost:8000/rns-java-backend-war/RNS_Backend",
  "method": "POST",
  "headers": {
    "content-type": "application/json",
    "authorization": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJiYXRtYW4ifQ.edQyAXK65KoAY-6_4RbCidMGhbrCaZ_-RgtFpuR9Q0TlIEbcvZUns3Pqg4pShlvLqwTG9lJy6qrZYVOSwkGNEw",
    "cache-control": "no-cache",
    "postman-token": "78dbbd0b-2c0a-7624-99f8-587a01e5dfeb"
  },
  "processData": false,
  "data": "{\"graphQL_Query\":\"{CreatePatientID(p_id: \\\"6\\\", user:\\\"superuser\\\", password:\\\"password12\\\") {result} }\"}"
}
