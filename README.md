# csc335-networked-gui

This is an example of using threads to (mostly) keep the event loop unblocked.

To run, clone and load up into eclipse. You may need to update the run configuration and/or library path to fix JavaFX.

Run the program twice (click the green play triangle twice). Move the windows so they don't overlap. In one window, click the server button. In the other, click the client button.

In the server, click anywhere in the window. A circle will appear in both windows. If you try to place another one, you'll find that you cannot - not because the UI is blocked, but because it's not "your turn". Click in the client window and see a circle appear in both.

The code is lightly commented but should give you what you need to make this work similarly for your Connect4 program.
