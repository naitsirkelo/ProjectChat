# Co-Living
## IMT3673 Project

![Build Status](https://img.shields.io/badge/build-running-green.svg)

## Project members

- Ole Larsen
- Marius Håkonsen

## App Idea

## Features

## Firebase

The list below will explain how the data is structured in Firebase. No data will be deleted when a user clicks a remove or delete button, but rather hidden from view. This is to keep a log of events by the administrator in case it is needed.  

- messages:
    - id: from_to, Child: Randomly generated key
        - message, time, user

- rooms:
    - id: Minimum 6 letters/numbers
        - owner (Email + phone)
        - rules (Created house rules, by users)
            - rule (id), user (who submitted), hidden (0/1)
        - tasks (Created by users)
            - task, area, user (who submitted), donBy (if completed), completed (0/1), hidden (0/1), time (timestamp with date of submission)

- users:
    - id: Login username
        - password
        - room id (Last used)
        - showName (Displayed to other users)

## Installation and testing


### Tools
* Android Studio
* Firebase
* Java

### Assignment Text

.
.
.
