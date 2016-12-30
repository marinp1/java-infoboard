# java-infoboard
A Java application that displays current time, weather forecast, calendar etc. to use as an information display.
The application is running on a Raspberry Pi and uses an old computer monitor as a display.

## Setup

File API_KEYS.properties must be created to the root directory of the application.
At the moment all API keys must be defined or the program crashes.

```
FMI_API_KEY=
GOOGLE_CLIENT_ID=
GOOGLE_CLIENT_SECRET=
MEISTERTASK_API_KEY=
```

## Features

### Date and time
Applications displays time, date and week number.

### Weather forecast
Weather forecast is fetched using Finnish Meteorological Institute Weather API with specified coordinates.

Currently available data:
* Temperature
* Weather type (cloudy / rainy / clear etc.)
* Pressure
* Wind speed
* Sunrise / sunset

### Sensors
Currently only indoor temperature is displayed using a sensor connected to the Pi.
In the future the screen should shut down automatically when it's nighttime and turn back on when it's day.

### Calendar block
In the calendar block is displayed data from Google calendar, Google tasks and MeisterTask projects.
All events are displayed which contain a datetime, so for example MeisterTask tasks without a due date are not displayed.

### Android application
Next feature to be done is a simple android application that could be used as a controller for the infoboard.
