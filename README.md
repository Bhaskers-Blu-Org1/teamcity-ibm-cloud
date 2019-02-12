## TeamCity IBM Cloud plugin

This plugin allows TeamCity users to manage and run builds on IBM Cloud VSIs.

To get the latest version of the plugin, clone this repo locally and build it using [Gradle](https://gradle.org). Gradle can be installed using [Homebrew](https://brew.sh):

```
$ brew install gradle
```

After you've cloned this repo and installed Gradle, run it in the repo's root directory.

```
$ gradle build
```

Automated tests will be run. To silence some verbose output, you can do

```
$ gradle build | grep -v Attempt
```

Gradle will create a zip file in `./ibm-cloud-server/build/distributions`.

```
$ cd ibm-cloud-agent/build/distributions
```

The zip file can be uploaded to the TeamCity server to install the plugin.

Detailed user documentation can be found [here](https://ibm.box.com/s/gxrj20cy71swn2ymh9ph0fjlwgb1wl4k).

We welcome and encourage community contributions to this project. Check out [CONTRIBUTING.md](CONTRIBUTING.md) for information about how to contribute.
