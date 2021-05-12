# Tick
A todo list that can sync anywhere.

For now, this project will target Android and uses git for syncing. In future, the git module will become multi-platform, allowing frontends on other platforms.

## Coding conventions
#### Redux - Dispatch
Tick is built atop Redux, with a single store powering the state management for the app. Redux requires a reference to a dispatch function (`(Any) -> Any`) to send actions upstream, mutating the state of the app.

The dispatch function in Tick is provided to the entire composition via a `CompositionLocal`. This makes functions "impure" in the sense that their dependencies aren't always provided
as parameters, however this has a number of benefits:
* The dispatch function is only referenced where it is needed
* No function signature pollution - simply pass in the data needed to render the screen, and actions bubble up through a separate channel

This makes programming within Tick much less cumbersome, but does have some drawbacks:
* Functions are not necessarily pure, which might make testing slightly harder
* A base definition of `LocalDispatch` is needed for feature modules to reference the same instance

#### Redux - Main logic loop
To make the Redux implementation in this app as simple and accessible as possible, I've chosen to only implement the absolute basic amount of logic 
needed for a To-do list app in the main loop. All additional functionality (such as saving to the local database) is a side-effect, and as such is implemented in middleware.

## Architecture diagram

<a href="https://lucid.app/lucidchart/3e63af74-4f02-42ca-9fe2-e46e0fd36e86/edit?beaconFlowId=8138934BA98B6805&page=0_0#" target="_blank">
  <img src="https://lucid.app/publicSegments/view/8a646562-c975-48f4-b500-e0a0c5761721/image.png" alt="TIck Architecture Diagram" border="10" />
</a>
