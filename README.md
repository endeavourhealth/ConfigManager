![Endeavour Logo](http://www.endeavourhealth.org/github/logo-text-left-cropped.png)

## Config Manager
![Version](https://s3.eu-west-2.amazonaws.com/endeavour-codebuild/Common-Config/badges/version.svg)
![Build Status](https://codebuild.eu-west-2.amazonaws.com/badges?uuid=eyJlbmNyeXB0ZWREYXRhIjoibU1zNDFESDFJSnVQVW5uQmI3NXpIMnozQjlDazNheEVDNFFEa3hkNmZVRW5UTE43bGJLMHVKak0wWWUyVHFJTDFZUWFXMkh5dmpDU293b0QxRGIyTkQ0PSIsIml2UGFyYW1ldGVyU3BlYyI6IkZvcFJSTm44OWMzTGl3a3UiLCJtYXRlcmlhbFNldFNlcmlhbCI6MX0%3D&branch=master)
![Unit Tests](https://s3.eu-west-2.amazonaws.com/endeavour-codebuild/Common-Config/badges/unit-test.svg)

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

