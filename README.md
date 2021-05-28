# Projet Gallery

University, project where we had to develop an application, that let you store images and apply filter to them.
The backend follow the REST API implemented with Spring Framework,
The database used to store images, and users is MySQL.
The frontend is using Vue 3.

## Build
To build this project you can use Docker
To be able to use Docker if not already installed you need to:

1. [Install Docker](https://docs.docker.com/get-docker/)
2. [If not installed docker-compose](https://docs.docker.com/compose/install/)

#### Build with docker-compose
To build the project in the root of the project (where the docker-compose.yml is located)
To build the project:
```bash
docker-compose build
```
Then to run the application:
```bash
docker-compoes up
```

If everything executed correctly you can now use the browser to connect to [http://localhost:8089](http://localhost:8089)

## Application
This application is an image gallery where you can store your images and apply different filters to them.
A simple authentifcation system using JWT is implemented, allowing you to authentificate.

Each user then own their own gallery on the server.

When connecting to the application while not authentificated it's possible to test some functionality of the server,
* Apply algorithms to images.
* Download images.

To be able able to use the application completly you need to authentificate: Create an account and log in.
Once logged in you are able to :
* Add your own images.
* Delete images.
* Apply algorithms and save those images.


## Backend
The backend end server implement a REST API, allowing the user to make request to perform different operations.
JWT is used to authenticate users.



## MySQL

The project is using MySQL to store, images and users.
You can retrieve or store images using the REST API,
The database follow a simple implementation that can be found on this diagram.

[Diagram (In a browser)](https://drawsql.app/pdl/diagrams/pdl/embed)

## Notes
Some aspect are not optimised or polished due to the time limit of the project.

## Authors

**Gilles Souton**
**Bastien Soucasse** https://github.com/bastiensoucasse
**Tony Wolff** https://github.com/tony-wolff


