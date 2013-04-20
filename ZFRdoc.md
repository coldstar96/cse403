## Clone, Build and Install
For Linux (and probably \*BSD, Mac OS X, and other UNIXy environments, but don't quote me on it):

1. Launch an instance of an Android Emulator
  * With `emulator -avd MyVirtualDevice` (or whatever you named it), for example.
2. Launch a terminal
3. `cd` into the parent directory in which you want to place the project folder.
4. `$ git clone https://github.com/coldstar96/cse403.git BudgetManager`
5. `$ cd BudgetManager`
6. `$ scripts/build-and-install.sh debug bin/MainActivity-debug.apk`
7. The app should now be installed on the emulator. Go run it. Have fun.

In addition, you can just build and run it from Eclipse.
## Data Access

### HTTP
You can access our current ZFR endpoint at ubudget.herokuapp.com/users.
It currently just returns the rows of a table as a JSON string.

This will be greatly expanded upon in the coming weeks and will offer more functionality.

### Direct Database Access
Since giving you that information involves giving out passwords, we won't put that here.
You can contact us privately if you really want this information.
