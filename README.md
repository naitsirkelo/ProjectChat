# Co-Living
## IMT3673 Project

![Build Status](https://img.shields.io/badge/build-running-green.svg)

## Project members

- Ole Larsen
- Marius Håkonsen

## App Idea

The idea is to make a single platform for organizing a household. Users can join a room/home, and will be connected to that room thanks to a unique ID of every room.

The app will help with:
- delegating responsibilities in the home, such as cleaning and taking out the trash
- booking/reserving common areas for events, such as movie nights
- having a common shopping list for items used by the fellowship
- storing contact info for landlord and view your rights as a tenant
- communicating with your cohabitants
- deciding the house rules to follow

## Usage

Registration:
When you open the application for the first time, press "Register here" to create an account. Enter display name, account name and a password. Press "Create account".

Log in:
Enter your recently created account name and password, followed by "login".

Joining a room:
If no one has already created a room, press "create here". Otherwise enter the households existing room id.
Enter your desired room id in the text field. Press "create room". Now enter the recently created room id and press "join".

Creating tasks:
After logging in you are in the tasks tab. To add a task press the blue Floating Action Button with a plus icon in it. Enter your desired task description and area for the task. To go back to the tasks tab you can press the FAB with the red cross in it. To add the task press the FAB with a green plus sign.

Adding items to shopping list:
From the burger menu, navigate to Shop tab. Press FAB containing a plus sign. Enter name of item to be bought followed by selecting if the shop request is for you or for everyone in the household. When you are ready, press the plus FAB to add item. Press red cross FAB to go back to Shop tab.


## Implemented and future features

When a new room is created, the user is set to be an admin of that room. This allows the user the admin privileges, which is to remove users, transfer the admin privileges (if they are moving out) and updating the landlord contact information.

Currently implemented features:

- Add and remove items from shoppinglist
- Create and delete events
- Create avatar/profile picture
- Create user, log in, join room, log out
- Language settings
- One to one chat
- Admin settings
- Create and delete house rules
- Bored button, for asking/notifying people to hang out/do something
- English and Norwegian versions
- Direct link to relevant information for tenants (english and norwegian)

Future features/changes:
- Local cache storage of rules/info/tasks
- Make tasks recursive on a set time frame
- Poll/vote for house rules, comment for suggested changes
- Approval of finished tasks by cohabitants
- Way of managing cost of items in shopping list, debt, integrating payment service (Vipps integration)
- Group chat
- Link to online stored renting contract


## Firebase Realtime Database

The list below will explain how the data is structured in Firebase. No data will be deleted when a user clicks a remove or delete button, but rather hidden from view. This is to keep a log of events by the administrator in case it is needed.  

- messages:
    - id: from_to, Child: Randomly generated key
        - message, time, user

- rooms:
    - id: Minimum 6 letters/numbers
        - events
        - admin (user id)
        - blockedList (user id(s))
        - owner (Email + phone)
        - rules (Created house rules, by users)
            - rule (id), user (who submitted), hidden (0/1)
        - tasks (Created by users)
            - task, area, user (who submitted), doneBy (if completed), completed (0/1), hidden (0/1), time (timestamp with date of submission)
        - shoppingList (Created by users)
            - item (id), forWho (for the fellowship or private request), itemName, hidden (0/1)

- users:
    - id: Login username
        - password
        - room id (Last used)
        - showName (Displayed to other users)
        - admin
        - blocked

## Installation
Clone the repo and install app by building it through Android Studio, either to an emulator or physical device.
The app connects to the Firebase database automatically. A connection to the internet is required to see the correct lists in real-time.

### Tools

* Android Studio
* Firebase
* Java
