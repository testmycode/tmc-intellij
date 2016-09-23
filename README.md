[![Build Status](https://travis-ci.org/ohtu-intellij/tmc-intellij.svg?branch=master)](https://travis-ci.org/ohtu-intellij/tmc-intellij)

[![Coverage Status](https://coveralls.io/repos/github/ohtu-intellij/tmc-intellij/badge.svg?branch=master)](https://coveralls.io/github/ohtu-intellij/tmc-intellij?branch=master)

# TMC plugin for IntelliJ IDEA

TMC-IntelliJ is the IntelliJ IDEA plugin for University of Helsinki's TestMyCode framework. TestMyCode is used by various online programming courses for exercise testing and submitting.

The plugin is available to be downloaded through the Jetbrains plugin repository.
- [Instructions here](https://github.com/ohtu-intellij/tmc-intellij/wiki)

## If you want to develop the plugin yourself, please refer to the instructions below:

#### Requirements

* Java Runtime Environment 8
* Linux, Mac OS X or Microsoft Windows
  * Other Unix-like systems may work, but are not tested
* IntelliJ IDEA, Community or Ultimate version

#### Setting up the project locally

Once you have the code on your local environment:

* open the project in your IntelliJ IDEA (both Community and Ultimate versions do).
* open `File` -> `Project Structure`.
* set the Project SDK to be the IntelliJ Platform Plugin SDK (it might try to give you only an invalid SDK as an option at first, but in that case press the `New...` button to choose the Plugin SDK.
* in case you haven't set up the home directory for JDK earlier for IntelliJ IDEA, it will request that first. The plugin uses Java 8.
* after that IntelliJ should ask you to give the home directory for the Plugin SDK. Choose the directory where you installed IntelliJ IDEA.
*  now, the SDK should be set, but to be sure, also check in `Modules` section of `Project Structure` that the SDK is the same in both `tmc-intellij` and `tmc-plugin-intellij` modules (`Dependencies` tab).
* go to any Java class in the source files. At the bottom of IntelliJ IDEA there should be a request to import Maven dependencies. Accept that. If you don't see the request, open `View` -> `Tool Windows` -> `Event log`.

Now everything should be ready and all the code compilable.

#### How to run the plugin
*Note, again, this is just if you want to develop it. If you actually want to use the plugin to submit your course exercises, then you might want to refer to the instructions above about the alpha version.*

* choose `Run` -> `Run`. IntelliJ IDEA should open up a small window suggesting to edit configurations. Click that.
* add new configuratino by clicking the `+` icon at top left and choose Plugin.
* you might want to name the configuration as "Plugin" or something like that, but otherwise the default settings should be fine.
* press OK to finish and run the plugin.

A new window for IntelliJ IDEA will open up and the Plugin will be active in that window so you can test out the TMC functions as you wish.

---

## Deployment

New releases may be uploaded to: https://plugins.jetbrains.com/plugin/8551 

---


##Credits
This plugin was developed for RAGE team of the department of Computer Science in University of Helsinki during course Software Production Project, Summer 2016.

#### Original developers

* Samu Kauppinen ([Rubiini](https://github.com/Rubiini))
* Konsta Kutvonen ([Djiffit](https://github.com/Djiffit))
* Henri Manninen ([Melchan](https://github.com/Melchan))
* Miika Leinonen ([Denopia](https://github.com/Denopia))
* Tuomo Oila ([tuomokar](https://github.com/tuomokar))

#### Instructor

* Esa Harju

#### Clients

* Leo Leppänen ([ljleppan](https://github.com/ljleppan))
* Jarmo Isotalo ([jamox](https://github.com/jamox))
