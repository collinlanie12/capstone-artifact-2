# Capstone-Artifact-2

# TrackIt - Event Tracking Mobile Application

**TrackIt** is an event tracking mobile application to help users manage and track upcoming events such as meetings, concerts, and appointments. It includes features like adding, editing, and deleting events, with SMS notifications and clean UI. This artifact is from the course CS-360 Mobile Architecture and Programming. The requirements for the mobile application was a sign-up and login function, a database to store user credentials and event information, and SMS notifications. Once a user added a new list/event item, they would receive an SMS message about their planned event. The planned enhancement was for the **Algorithms and Data Structures** category. 

You can observe the difference between the original and enhanced artifacts through my pull request: [Pull Request: Enhanced TrackIt Android Mobile Application (Capstone Artifact 2)](https://github.com/collinlanie12/capstone-artifact-2/pull/1)

---

## Code Enhancements
- Refactored all comments and documentation to follow industry standards.
- Removed unused and redundant code.
- Validated user inputs.
- Implemented proper error handling and logging.
- Improved structure for better maintainability and readability.

## Algorithms
- Implemented Merge Sort to arrange events in chronological order (year, month, day, hour, minute).
- Users will now see their event list in correct sequence.

## Data Structures
- Priority Queue added to determine and display the upcoming event efficiently.
- This is displayed in a new banner at the top of the RecyclerView for the event list.

---

## Technologies Used
- Java
- Android Studio
- SQLite
- XML
- Gradle

---

## Getting Started

Follow these steps to setup and run the TrackIt mobile application on you local machine using Android Studio.

1. **Clone the repository**
   ```bash
   git clone https://github.com/collinlanie12/capstone-artifact-2.git
   cd capstone-artifact-2
   ```
   
2. Open in Android Studio
   - Launch Android Studio.
   - Click on "Open" and navigate to the cloned repository folder.
   - Wait for Android Studio to sync and build the Gradle project. **This may take a few moments.**

3. Configure Emulator
   - Make sure you have an emulator set up (Pixel 5, API 34)
   - Click Run to build and launch the app.

   

## Screenshots

### Before Enhancement 
![Original Artifact](screenshots/original_android_artifact.png)

### After Enhancement
![Enhanced Artifact](screenshots/enhanced_android_artifact.png)

## Author
Collin Lanier

Southern New Hampshire University

CS-499 Capstone Project - Artifact 2
