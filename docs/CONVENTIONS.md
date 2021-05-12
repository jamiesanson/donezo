# Coding conventions
### Redux - Dispatch
Tick is built atop Redux, with a single store powering the state management for the app. Redux requires a reference to a dispatch function (`(Any) -> Any`) to send actions upstream, mutating the state of the app.

The dispatch function in Tick is provided to the entire composition via a `CompositionLocal`. This makes functions "impure" in the sense that their dependencies aren't always provided
as parameters, however this has a number of benefits:
* The dispatch function is only referenced where it is needed
* No function signature pollution - simply pass in the data needed to render the screen, and actions bubble up through a separate channel

This makes programming within Tick much less cumbersome, but does have some drawbacks:
* Functions are not necessarily pure, which might make testing slightly harder
* A base definition of `LocalDispatch` is needed for feature modules to reference the same instance

### Redux - Main logic loop
To make the Redux implementation in this app as simple and accessible as possible, I've chosen to only implement the absolute basic amount of logic
needed for a To-do list app in the main loop. All additional functionality (such as saving to the local database) is a side-effect, and as such is implemented in middleware.
