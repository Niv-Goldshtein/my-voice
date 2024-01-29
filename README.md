# MyVoice Android App

MyVoice is an Android application designed to provide a hands-free, voice-controlled experience, primarily in Hebrew.

## Overview

The main goal of the app is to help users interact without any physical touch, filling the language gap for Hebrew speakers, as Bixby primarily supports English.

## Features

- **Authentication**: Users can sign in or sign up on the login page, with input validation and user existence checks and can change their password in the sign in page.
- **Main Page**: Upon logging in, the main page immediately starts listening to user input using SpeechRecognizer, executing actions based on recognized instructions.
- **Menu Options**:
  - Logout: Allows users to log out of their account.
  - History Activity: Displays past activities with details and a thumbnail image. Users can delete entries using a dialog popup.

## Voice Commands

The app recognizes the following voice commands:

- **"Call to"**: Initiates a call to the specified contact if available; otherwise, a toast notifies the user of the absence of the contact.
- **"Search"**: Conducts a Google search based on the provided query.
- **"Send message to"**: Sends a message to the specified contact if available; otherwise, prompts the user to speak again for the message or initiates the calling process.
- **"Go to"**: Opens Waze with the specified destination.
- **Default**: If the app hears anything else, it prompts the user to speak again. If no input is detected, a toast message informs the user.

## Additional Features

- **Shake to Open**: Implemented using service and BroadcastReceiver, shaking the phone opens the app.(currently not working due to version changes in android)
- **Bixby Integration**: The app can be opened by telling Bixby to open it.
- **WiFi Check**: A BroadcastReceiver checks for WiFi; if absent, the user can only log out and view history.

## Permissions

The app requires the following permissions:

- Contacts: For accessing and using contact information.
- Phone: For making calls.
- Microphone: For voice input using SpeechRecognizer.
- SMS: For sending messages.

If permissions are not granted, the app will request them and display a toast message instructing the user to grant the necessary permissions. Users can also provide access via device app settings.

## App Requirements

- Android API: At least API 23.
- Internet or WiFi Connection: Required to save history.
- Voice Input: Device must support the option of listening to what the user says.

## Installation Instructions

1. Download the APK file(app-debug.apk) that is attached above.

## Potential Issues

- **No WiFi**: Without WiFi, users can only log out and view history.
- **Permissions**: Grant necessary permissions when prompted or through device app settings therwise most of the app features will not work.

## Note

  - Opening the app by shaking your phone is currently not working due to changes in android studio, android version update and the time this app was made.
