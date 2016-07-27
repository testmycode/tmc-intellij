[![Build Status](https://travis-ci.org/ohtu-intellij/tmc-intellij.svg?branch=master)](https://travis-ci.org/ohtu-intellij/tmc-intellij)

[![Coverage Status](https://coveralls.io/repos/github/ohtu-intellij/tmc-intellij/badge.svg?branch=master)](https://coveralls.io/github/ohtu-intellij/tmc-intellij?branch=master)

# TMC plugin for IntelliJ IDEA

TMC-IntelliJ is the IntelliJ IDEA plugin for University of Helsinki's TestMyCode framework. TestMyCode is used by various online programming courses for exercise testing and submitting.

The plugin is not yet published at the plugin repository of Jetbrains, but feel free to clone the repository if you can want to see it in action.

### Requirements

* Java Runtime Environment 7
* Linux, Mac OS X or Microsoft Windows
  * Currently only limited support for Windows
  * Other Unix-like systems may work, but are not tested
* IntelliJ IDEA, Community or Ultimate version

### Setting up the project locally

Once you have the code on your local environment:
* open the project in your IntelliJ IDEA (both Community and Ultimate versions do).
* open `File` -> `Project Structure`.
* set the Project SDK to be the IntelliJ Platform Plugin SDK (it might try to give you only an invalid SDK as an option at first, but in that case press the `New...` button to choose the Plugin SDK.
* in case you haven't set up the home directory for JDK earlier for IntelliJ IDEA, it will request that first. The plugin actually uses Java 7, but Java 8 is fine to choose there.
* after that IntelliJ should ask you to give the home directory for the Plugin SDK. Choose the directory where you installed IntelliJ IDEA.
*  now, the SDK should be set, but to be sure, also check in `Modules` section of `Project Structure` that the SDK is the same in both `tmc-intellij` and `tmc-plugin-intellij` modules (`Dependencies` tab).
* go to any Java class in the source files. At the bottom of IntelliJ IDEA there should be a request to import Maven dependencies. Accept that.

Now everything should be ready and all the code compilable.

### How to run the plugin

* choose `Run` -> `Run`. IntelliJ IDEA should open up a small window suggesting to edit configurations. Click that.
* add new configuratino by clicking the `+` icon at top left and choose Plugin.
* you might want to name the configuration as "Plugin" or something like that, but otherwise the default settings should be fine.
* press OK to finish and run the plugin.
* a new window for IntelliJ IDEA will open up and the Plugin will be active in that window so you can test out the TMC functions as you wish.

