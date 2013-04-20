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

## Data Access

### HTTP
You can access our current endpoint at [http://students.washington.edu/clinger/script.php].
This is subject to change. For example, a move to Heroku will likely be happening in the near future.
### Direct Database Access
Maybe for your birthday. Or if you ask nicely.
