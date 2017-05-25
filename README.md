![Endeavour Logo](http://www.endeavourhealth.org/github/logo-text-left-cropped.png)

## Config Manager

A centralised configuration service for providing configuration to Endeavour Health services and components.


### Making Changes

To make changes...

* Increment the version number
    * On the maven window, run "common (root) -> Plugins -> release -> release:update-versions"
    * The prompt *SHOULD* default to the next version number but can be overriden
* Make your code changes, doing a standard (IntelliJ) compile!
* Once happy with your code, to use/test in other modules
    * Run the maven "Lifecycle -> Install"

        **Note:** This should add the new version to your local (.m2) repository

    * Update the version number in your external project, build and test.

* Once development is complete, commit your code changes!

