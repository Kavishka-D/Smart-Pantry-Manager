Smart Pantry Manager

Description: Smart Pantry Manager is a Java Android application that helps users
keep track of ingredients in their pantry and find recipes they can make using the 
ingredients they already have.

The application allows users to: 
- Add, edit, and delete pantry ingredients. 
- Store ingredient quantities, units, and optional expiry dates. 
- View all saved pantry ingredients in a RecyclerView. 
- View suggested recipes based on the current pantry contents. 
- View recipe ingredients and preparation steps. 
- Use an “Almost There” section for recipes that are missing exactly one ingredient. 
- Manage basic profile and settings information.

The main recipe-matching rule is strict: 
A recipe is shown as a suggested recipe only when all required ingredients
are available in the pantry in the required quantity. Ingredient names are also
normalised to handle simple differences such as capitalisation and singular/plural
forms.

Technology
Language: Java
Platform: Android
IDE: Android Studio
Database: SQLite
Database helper: SQLiteOpenHelper
Minimum SDK: 24
Target SDK: 37
Compile SDK: 37
Java compatibility: Java 11

Database: SQLite was chosen because the application stores the user’s pantry
information locally on the Android device. The database provides persistent storage 
and supports the required Create, Read, Update, and Delete (CRUD) operations.

The database contains: 
pantry: stores pantry ingredient information. 
recipes: stores recipe names and preparation steps. 
recipe_ingredients: stores the ingredients required by each recipe and links them to the recipes table.

The application also includes pre-loaded recipes in the database.

Project Structure
Important application classes include: 
MainActivity.java: Home screen and navigation. 
PantryActivity.java: Displays saved pantry ingredients. 
PantryAdapter.java: Displays pantry data in the RecyclerView. 
AddEditIngredientActivity.java: Adds and edits pantry ingredients. 
SuggestedRecipesActivity.java: Performs recipe matching and displays suggestions. 
RecipeDetailActivity.java: Displays recipe details. 
ProfileSetupActivity.java: Handles initial profile setup. 
SettingsActivity.java: Handles settings and profile information. 
BaseDrawerActivity.java: Provides the navigation drawer functionality. 
DatabaseHelper.java: Creates and manages the SQLite database.

Setup and Run Instructions:
1.  Install Android Studio.
2.  Extract or clone the Smart Pantry Manager project.
3.  Open the project folder in Android Studio.
4.  Allow Gradle to sync and download the required dependencies.
5.  Create or start an Android emulator, or connect a compatible Android device with USB debugging enabled.
6.  Select the “app” run configuration.
7.  Run the application from Android Studio.
8.  On first launch, complete the profile setup.
9.  Add pantry ingredients and use the Pantry and Suggested Recipes screens to test the application’s functionality.
