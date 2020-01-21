![](./assets/cbt-practice-logo.jpg)

# CBT Practice
CBT Practice helps undergraduate students of the University of Jos practice for their computer-based examinations. This repository houses the *Android* app. Asscoiated repositories are listed below. Please note that the second repository is private. If you would like me to make it open-source, kindly message me so that I can prioritize doing that.
* [CBT Practice Webapp](https://github.com/okibeogezi/cbt-practice-web)
* [CBT Practice Question Submitter](https://github.com/okibeogezi/cbt-practice-question-submitter) - This allows people with special access submit new questions to the question bank.
* CBT Practice PIN Generator - This contains CLI utilities that generates unique PINS that can be used to access the application and bundles them into a PDF document that can be printed, cut into pieces and sold

## Important note
To build and run the app yourself, please do the following:
* Create a Firebase project, initialize cloud messaging, firestore, auth, and cloud storage then download a `google-services.json` file and drop it in the `app/src` directory
* Set `facebook_application_id` in your strings.xml
* Set `fb_login_protocol_scheme` in your strings.xml
* Set `app_admob_id` in your strings.xml

## Get it here
[<img src="./assets/google-play.svg" width="400">](https://play.google.com/store/apps/details?id=com.makerloom.ujcbt&hl=en_US)

## Problem and Idea
While studying computer science at the University of Jos, I observed that although students had been taking computer-based exams for years, there was no convenient and cheap way for them to access the past questions. The best solution at the time was for students to make photocopies of the past questions. This was problematic for two reasons:
* Photocopying these past questions were fairly expensive as there were up to hundreds of pages in some cases.
* Students typically made these photocopies about a week to the day of the exam and discard them shortly afterwards. Since this app minimizes paper usage, I feel that it reduces paper [pollution](https://en.wikipedia.org/wiki/Environmental_impact_of_paper) and has a positive envoronmental impact .

## Solution
My solution, CBT Practice, allows students of the University to practice on their android mobile devices. The students had access to instand feedback on their performance as well as corrections after taking the practice exam.

## User access
Users are able to access the app's content for a fee of ₦200. This fee could either be paid online on the app itself or used to buy a paper PIN which looks like this.
If you would like me to waive the fee so you could test the app, kindly contact me.

![](./assets/sample-paper-pin.png)

## User support
Phone numbers of support personnel were provided within the app, but the most common way for users to receive support was the [CBT Practice WhatsApp group](https://chat.whatsapp.com/I9aDaWoaxydJJxcu9f7sQv).

## Bringing this to another school
This application currently only works in the [University of Jos](https://www.unijos.edu.ng/). If you would like to work with me to bring something like this to your school, send me an email.

## App screenshots
|Launching the app
|-----------------
|<img src="./assets/google-play.svg" width="400">
