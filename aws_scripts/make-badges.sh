#!/bin/bash

mkdir badges

# Version
version=$( xmllint --xpath "/*[local-name() = 'project']/*[local-name() = 'version']/text()" pom.xml )
version=${version/-/--}

echo "https://img.shields.io/badge/Version-$version-green.svg"
curl -s "https://img.shields.io/badge/Version-$version-green.svg" > badges/version.svg

# Unit tests
failures=$( xmllint --xpath 'string(//testsuite/@failures) + string(//testsuite/@errors)' API/target/surefire-reports/TEST-*.xml )

if [ "$failures" -gt "0" ] ; then
        badge_status=failing
        badge_colour=red
else
        badge_status=passing
        badge_colour=green
fi

echo "Generating badge 'https://img.shields.io/badge/Unit_Tests-$badge_status-$badge_colour.svg'"
curl -s "https://img.shields.io/badge/Unit_Tests-$badge_status-$badge_colour.svg" > badges/unit-test.svg