# JotMe
Note app similar to Memo with search feature. Implements Firebase Realtime Database.

## How To Use JotMe
### Initial Start-up
Upon initial start-up, user is prompted to provide an authentic email account and create a password. This information will be used to authenticate user for future sessions. User authentication is handled by FirebaseUI Authentication.

### Add A New Jot
Upon successful authentication, the list screen will be displayed. If the list is empty, meaning you have no saved Jots, a text will prompt the user to click the floating action button to create a new Jot. Upon clicking the floating action button, a screen will be displayed allowing the user to fill in Jot details. These details include a title, up to three tags, and a message.

**User can save Jot only after providing at least one tag. User can cancel changes to newly created Jot at any time and return to list screen.**

### Edit An Existing Jot
Upon successful authentication, the list screen will be displayed. If the list is not empty, the user selects a list item, which will launch a screen displaying the details of the selected Jot. Once the details of the selected Jot are displayed, the user will have the option to edit the Jot by clicking a floating action button.

**User can save Jot only after providing at least one tag. User can cancel changes to newly created Jot at any time and return to list screen. Save option will not be available if changes match original values for Jot details.**

#### Smaller Screens
The details of the selected Jot will be launched in a single pane, replacing the list screen. The available floating action buttons will be _edit_ and _delete_. Select _edit_ floating action button to edit selected Jot. The details screen will be replaced with an edit screen allowing the user to make changes to the selected Jot. Select _save_ to save changes, then list screen replaces edit screen.

#### Larger Screens
The list screen will remain displayed on the left side of the screen and the details of the selected Jot will be displayed on the right side of the screen. The available floating action buttons will be _edit_ and _delete_. Select _edit_ floating action button to edit selected Jot. The details screen will be replaced with an edit screen allowing the user to make changes to the selected Jot. Select _save_ to save changes, then details screen replaces edit screen.

### Delete An Existing Jot

The user can delete an existing Jot in two ways. The first is by swiping the list item left or right. The second is by selecting the _delete_ floating action button while the details screen is displayed.

**The delete function is unavailable while in search mode**.

### Search For An Existing Jot

The search feature, or search mode, searches the tags of existing Jots. To enable the search function, or search mode, select the magnifying glass icon on the toolbar. Once enabled, type the desired tag to be searched for. The list of Jots will be filtered to list all existing Jots that have tags that match the search terms.

**Editing and deleting functions will be unavailable while the search feature is enabled**. **Search mode is unavailable while editing an existing Jot and while adding a new Jot**.

### Sign Out Authenticated User
Selecting the sign out icon on the toolbar will sign out the authenticated user and then launch the FirebaseUI Authentication screen.

**Sign out is unavailable while editing an existing Jot and while adding a new Jot**.
