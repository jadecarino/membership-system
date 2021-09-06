# Membership System
This repository contains the code for the Membership System. 

## User instructions
Prerequisites:
-	Command line knowledge
-	Java
-	Maven
-	Git (optional)

To try out the application for yourself, please follow the instructions below:

1. Clone this Github repository to your machine. Use your Terminal to navigate to the directory you want to place the repo, and run the command 
    ```sh
    git clone https://github.com/jadecarino/membership-system.git
    ```
1. Navigate to the root of the repository, using the command
    ```sh
    cd Synoptic\ Project\ Membership\ System
    ```
1. Run the shell script file to build and deploy the application to Open Liberty, using the command 
    ```sh
    bash script.sh
    ```
    This will start two Open Liberty server instances: the web application login will be available on https://localhost:6080/membership-system/ and the web application membership-system will be available on http://localhost:9080/membership-system/

Now you can use Postman to test out the application:
[![Run in Postman](https://run.pstmn.io/button.svg)](https://app.getpostman.com/run-collection/15899001-aaa15db3-9cec-44a6-86a1-c4aaacfbcaa1?action=collection%2Ffork&collection-url=entityId%3D15899001-aaa15db3-9cec-44a6-86a1-c4aaacfbcaa1%26entityType%3Dcollection%26workspaceId%3D8432e8fd-7e6c-4072-8615-65887d41d744)

1. As the database will be empty, to load some sample data into the database, use the Sample Data request [http://localhost:9080/membership-system/sampledata/load](http://localhost:9080/membership-system/sampledata/load). This will enter three Employee records into the database and open Accounts for them.
1. You will need to authenticate all other requests with a Json Web Token. Use the Login API [https://localhost:6080/membership-system/login](https://localhost:6080/membership-system/login) to login with any of the credentials below:
| Username | Password | Role        | 
|----------|----------|-------------|
| jade     | jadepwd  | admin, user |
| frank    | frankpwd | user        |
| lydia    | lydiapwd | user        |
1. Copy and paste the Json Web Token from the response (excluding the quotation marks) and paste it into the Bearer token to make requests on the Membership System.
1. Use the Get all employees request [http://localhost:9080/membership-system/employees](http://localhost:9080/membership-system/employees) to view the sample data that was just loaded into the system.
1. Use any of the other requests in Postman: view information about an individual employee, update their details, create a new employee, view an employee’s account balance using their card number, top up their balance or pay for an item.
1. To stop the servers, press CTRL+C in the Terminal session where the server is.