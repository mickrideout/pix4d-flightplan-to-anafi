# anafi-flightplan-utils

Converts drone flight plans gerneated by Pix4D to Parrot Anafi format.

## Installation

Clojure Leiningen is needed to build this project: https://leiningen.org/#install

Build the uberjar which is built to the target directory `target/uberjar`:
```
lein uberjar
```

## Usage
```
java -jar anafi-flightplan-utils-0.1.0-standalone.jar 

Usage: anafi-flightplan-utils [options]

Options:
  -i, --input FILE                      Input file
  -o, --output FILE                     Output file
  -s, --speed SPEED         5           Speed m/s
  -p, --period SECS         2           Image capture period secs
  -t, --title TITLE         2022-09-19  Title of the flightplan
  -x, --homeLatitude LAT                latitude to return to
  -y, --homeLongitude LONG              longitude to return to
  -a, --homeAltitude ALT    50          Altitude to return to for home
  -h, --help                            Help

```
### Example
```
java -jar target/uberjar/anafi-flightplan-utils-0.1.0-SNAPSHOT-standalone.jar -i details.pix4dcapture-mission -o savedPlan.json --speed 1 --period 1.5
```

## License

Copyright © 2020 FIXME

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
