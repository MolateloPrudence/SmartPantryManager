# Smart Pantry Manager

An Android application built in Java that helps users reduce food waste by suggesting recipes based strictly on the leftover ingredients in their pantry.

## Features
- *Pantry Management:* Full CRUD (Create, Read, Update, Delete) functionality for ingredients using SQLite.
- *Strict Recipe Matching:* Suggests recipes ONLY if every single required ingredient is present in the correct quantity.
- *"Almost There" Bonus:* A separate tab showing recipes that are missing only one ingredient.
- *Custom UI:* A sleek black-and-white theme with custom animations and fonts.

## Database Choice
I chose *SQLite* using SQLiteOpenHelper because it is local, requires no internet connection, and is perfect for a personal pantry app. It allows for fast, offline data persistence.

## How to Run
1. Clone this repository.
2. Open the project in Android Studio.
3. Click *Run* and select your emulator or connected physical device.
