# Wordle

This is a clone of the New York Times game WORDLE created for android using the Kotlin programming language. 

Link to the original: https://www.nytimes.com/games/wordle/index.html

=======================================================================================================================================

I created this for the purpose of learning and impoving my development skills and to demonstrate my ability to potential employers.

I welcome and greatly appreciate any and all forms of constructive critisism, suggestions, bug fixes, requests or the like.

=======================================================================================================================================

This app demonstrates the use of android architecture components like:
- ViewModel
- Activities and intents (implicit and explicit)
- Fragments and the fragment lifecycle
- Jetpack Navigation Component
- Databinding
- Live Data

Other important components used include:
- Motion Layouts (animating views on screen)
- Shared Preferences (for data persistence)
    - user settings
    - user statistics
    - saved games
- Work Manager (scheduling notifications in the background)
- Logging (for debugging)
- User selectable Themes (light, dark, or system default)
- Dialog Fragments

=======================================================================================================================================

The premise of the game is that there is a secret 5 letter word that you must guess in six tries or less.

Each time you enter a valid word each of the letters will change to one of three colors:
- GREEN = the letter is in the word and in the correct spot
- YELLOW = the letter is in the word but in the wrong spot
- GREY = the letter is not in the word


