# Membership System
This repository contains the code for the Membership System. 

## User instructions
Prerequisites:
-	Command line knowledge
-	Java
-	Maven
-   Postman
-	Git (optional)

## Building and deploying to Open Liberty

To build the application and deploy it to Open Liberty, please follow the instructions below:

1. Unzip and download the folder ‘Synoptic Project Membership System’ into your Desktop. Alternatively, clone my Github repository to your machine. Use your Terminal to navigate to your Desktop, and run the command 
    ```sh
    git clone https://github.com/jadecarino/membership-system.git
    ```


2. Use your Terminal to navigate to your Desktop. Once you are in your Desktop, run this command to navigate to the root of the project:
    ```sh
    cd Synoptic\ Project\ Membership\ System/
    ```


3. Navigate to the `login` directory and deploy the Login API to Open Liberty, using the commands
    ```sh
    cd login
    mvn liberty:run
    ```


4. Open another Terminal tab and navigate to the `membership-system` directory and deploy the Membership System to Open Liberty, using the commands
    ```sh
    cd membership-system
    mvn liberty:run
    ```

**The first time running the servers may take several minutes.** After you see the following message in both Terminal sessions, both of your servers are ready:
```sh
The defaultServer server is ready to run a smarter planet.
```

You will have two Open Liberty server instances: the web application 'login' will be available on https://localhost:6080/membership-system/ and the web application 'membership-system' will be available on http://localhost:9080/membership-system/




## Postman 

Now, you can use Postman to test out the application. 

**IMPORTANT: if you are using the Postman web client, you will need to download the Postman Desktop Agent.**


1. Press the button below (or click the link underneath) to fork and use the collection:

[![Run in Postman](https://run.pstmn.io/button.svg)](https://app.getpostman.com/run-collection/15899001-aaa15db3-9cec-44a6-86a1-c4aaacfbcaa1?action=collection%2Ffork&collection-url=entityId%3D15899001-aaa15db3-9cec-44a6-86a1-c4aaacfbcaa1%26entityType%3Dcollection%26workspaceId%3D8432e8fd-7e6c-4072-8615-65887d41d744)

https://app.getpostman.com/run-collection/15899001-aaa15db3-9cec-44a6-86a1-c4aaacfbcaa1?action=collection%2Ffork&collection-url=entityId%3D15899001-aaa15db3-9cec-44a6-86a1-c4aaacfbcaa1%26entityType%3Dcollection%26workspaceId%3D8432e8fd-7e6c-4072-8615-65887d41d744

If you don't already have a Postman account, please login using this temporary account.

| Email                         | Password      |
|-------------------------------|---------------|
| synopticprojecttemp@gmail.com | Firebrand123  |


2. Fork the collection and save it to a Workspace (the steps should look as below):
![Fork](./docs-assets/Fork.png)


3. Once you have forked the collection and can see a workspace, expand all of the folders inside the Membership System collection to see all of the requests:

![Sample data request](./docs-assets/Sample-data-request.png)


4. As the database will be empty, to load some sample data into the database, use the **Load** request in the Load sample data folder. This request requires no authentication as it is not part of the Membership System, just a helpful way to load data in to test the application. It will enter three Employee records into the database and open Accounts for them.

![Postman](./docs-assets/Postman.png)


5. You will need to authenticate all other requests on the Membership System with a Json Web Token, as I have secured the application. Use the **Login** request in the Login folder to login with any of the credentials below and get a Json Web Token back:

| Username | Password | Role        | 
|----------|----------|-------------|
| jade     | jadepwd  | admin, user |
| frank    | frankpwd | user        |
| lydia    | lydiapwd | user        |

**IMPORTANT: The following requests are restricted to just Admins (jade), so if you try to access them as a User (frank or lydia), you will get a 401 Unauthorized.**
- Get all employees
- Delete all employees

![Login API](./docs-assets/Login-API.png)


6. Copy the Json Web Token from the response of the **Login** request (excluding the quotation marks) to your clipboard. You can now paste it into the Bearer token of all other requests in the Membership System collection to authenticate them

![Copy JWT from response](./docs-assets/Copy-JWT-from-response.png)

![JWT in Bearer](./docs-assets/JWT-in-Bearer.png)


7. Use the **Get all employees** request in the Employees folder to view the sample data that was loaded into the system by the **Load** request:

![Get request](./docs-assets/Get-request.png)


8. **Now try it out yourself!** Use any of the other requests I have saved in the Postman collection: you can view information about an individual employee, update their details, create a new employee, view their account balance using their card number, top up their balance or pay for an item!

9.	To stop the servers, press CTRL+C in the command line session where the server is.


## How to write requests:
The requests require a mix of path and query parameters. I have already filled in some parameters in the some of the requests so you can just press send from Postman, but please feel free to add your own parameters.

I have left placeholders in some requests _{employeeId}_ and _{cardNumber}_ as you will need to fill these in yourself with data from the database (**TIP:** use the employeeIds and cardNumbers from employees you created in the **Load** request).

1.	Path parameters are for identifying a resource, they come after a ‘/’. The path below means you want to do something with the employee with employeeId ‘1234’.

localhost:9080/membership-system/employees/{employeeId}

localhost:9080/membership-system/employees/1234


2.	Query parameters are for entering information, they come after a ‘?’, and after the parameter name. The path below means you are filling in information, and the ‘name’ you want to provide is ‘Jade’.

localhost:9080/membership-system/employees?name={name}

localhost:9080/membership-system/employees?name=Jade


## Thanks for using my application!