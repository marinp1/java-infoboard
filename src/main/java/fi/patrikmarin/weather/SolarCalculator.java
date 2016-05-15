package fi.patrikmarin.weather;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.TimeZone;

public class SolarCalculator {
	
	public Double getJD() {

		LocalDateTime date = LocalDateTime.now();
		
		int month = date.getMonthValue();
		int day = date.getDayOfMonth();
		int year = date.getYear();

		if (month <=  2) {
			year -= 1;
			month += 12;
		}
		int A = (int) Math.floor(year / 100);
		int B = 2 - A + (int) Math.floor(A / 4);
		Double JD = (double) Math.floor(365.25*(year + 4716)) + Math.floor(30.6001*(month+1)) + day + B - 1524.5;
		return JD;
	}
	
	public Double getLocalTime() {
		LocalDateTime date = LocalDateTime.now();
		
		int hour = date.getHour();
		int min = date.getMinute();
		int sec = date.getSecond();

		if (isDST()) {
			hour -= 1;
		}
		
		Double mins = hour * 60 + min + sec/60.0;
		return mins;
	}
	
	public Double getTimezone() {
		TimeZone tz = TimeZone.getDefault();
		return (double) tz.getRawOffset() / 1000.0 / 60.0 / 60.0;
	}
	
	public Boolean isDST() {
		TimeZone tz = TimeZone.getDefault();
		
		Calendar c = Calendar.getInstance(tz);
		int DST = c.get(Calendar.DST_OFFSET);
		
		return DST != 0;
	}
	
	public Double calcTimeJulianCent(Double jd) {
		Double T = (jd - 2451545.0)/36525.0;
		return T;
	}
	
	Boolean isLeapYear(Integer yr) 
	{
	  return ((yr % 4 == 0 && yr % 100 != 0) || yr % 400 == 0);
	}
	
	Double  calcDoyFromJD(Double jd)
	{
	  Double z = Math.floor(jd + 0.5);
	  Double f = (jd + 0.5) - z;
	  Double A;
	  if (z < 2299161) {
	    A = z;
	  } else {
	    Double alpha = Math.floor((z - 1867216.25)/36524.25);
	    A = z + 1 + alpha - Math.floor(alpha/4);
	  }
	  Double B = A + 1524;
	  Double C = Math.floor((B - 122.1)/365.25);
	  Double D = Math.floor(365.25 * C);
	  Double E = Math.floor((B - D)/30.6001);
	  Integer day = (int) (B - D - Math.floor(30.6001 * E) + f);
	  Integer month = (int)((E < 14) ? E - 1 : E - 13);
	  Integer year = (int) ((month > 2) ? C - 4716 : C - 4715);

	  Integer k = (isLeapYear(year) ? 1 : 2);
	  Double doy = Math.floor((275 * month)/9) - k * Math.floor((month + 9)/12) + day -30;
	  return doy;
	}
	
	Double radToDeg(Double angleRad) {
	  return (180.0 * angleRad / Math.PI);
	}

	Double degToRad(Double angleDeg) {
	  return (Math.PI * angleDeg / 180.0);
	}
	
	String zeroPad(Double m, int digits) {
		String n = String.valueOf(m.intValue());
		while (n.length() < digits) {
			n = '0' + n;
		}
		return n;
	}

	
	String dayString(Double jd, Boolean next)
	{
	// returns a string in the form DDMMMYYYY
		
		String output = "";
	  if ( (jd < 900000) || (jd > 2817000) ) {
		  output = "error";
	  } else {
		  Double z = Math.floor(jd + 0.5);
		  Double f = (jd + 0.5) - z;
		  Double A;
		  if (z < 2299161) {
			  A = z;
		  } else {
			Double alpha = Math.floor((z - 1867216.25)/36524.25);
			A = z + 1 + alpha - Math.floor(alpha/4);
		  }
		  Double  B = A + 1524;
		  Double  C = Math.floor((B - 122.1)/365.25);
		  Double  D = Math.floor(365.25 * C);
		  Double  E = Math.floor((B - D)/30.6001);
		  Double day = B - D - Math.floor(30.6001 * E) + f;
		  Double month = (E < 14) ? E - 1 : E - 13;
		  Integer year = (int) ((month > 2) ? C - 4716 : C - 4715);
		  
		  output = zeroPad(day,2) + zeroPad(month, 2) + year.toString();
	  
	  }
	  return output;
	}

	String timeDateString(Double JD, Double minutes)
	{
	  String output = dayString(JD, false) + " " + timeString(minutes);
	  return output;
	}

	String timeString(Double minutes)
	// timeString returns a zero-padded string (HH:MM:SS) given time in minutes
	// flag=2 for HH:MM, 3 for HH:MM:SS
	{
		String output = "";
	  if ( (minutes >= 0) && (minutes < 1440) ) {
	    Double floatHour = minutes / 60.0;
	    Double hour = Math.floor(floatHour);
	    Double floatMinute = 60.0 * (floatHour - Math.floor(floatHour));
	    Double minute = Math.floor(floatMinute);
	    Double floatSec = 60.0 * (floatMinute - Math.floor(floatMinute));
	    Double second = Math.floor(floatSec + 0.5);
	    if (second > 59) {
	      second = 0.0;
	      minute += 1;
	    }
	    if (second >= 30) minute++;
	    if (minute > 59) {
	      minute = 0.0;
	      hour += 1;
	    }
	    output = zeroPad(hour,2) + ":" + zeroPad(minute,2);
	  } else { 
	    output = "error";
	  }
	  return output;
	}
	
	Double calcGeomMeanLongSun(Double t) {
	  Double L0 = 280.46646 + t * (36000.76983 + t*(0.0003032));
	  
	  while(L0 > 360.0) {
	    L0 -= 360.0;
	  }
	  while(L0 < 0.0) {
	    L0 += 360.0;
	  }
	  return L0;	// in degrees
	}

	Double calcGeomMeanAnomalySun(Double t) {
	  Double M = 357.52911 + t * (35999.05029 - 0.0001537 * t);
	  return M;		// in degrees
	}

	Double calcEccentricityEarthOrbit(Double t) {
	  Double e = 0.016708634 - t * (0.000042037 + 0.0000001267 * t);
	  return e;		// unitless
	}
	
	Double calcSunEqOfCenter(Double t)
	{
	  Double m = calcGeomMeanAnomalySun(t);
	  Double mrad = degToRad(m);
	  Double sinm = Math.sin(mrad);
	  Double sin2m = Math.sin(mrad+mrad);
	  Double sin3m = Math.sin(mrad+mrad+mrad);
	  Double C = sinm * (1.914602 - t * (0.004817 + 0.000014 * t)) + sin2m * (0.019993 - 0.000101 * t) + sin3m * 0.000289;
	  return C;		// in degrees
	}

	Double calcSunTrueLong(Double t) {
	  Double l0 = calcGeomMeanLongSun(t);
	  Double c = calcSunEqOfCenter(t);
	  Double O = l0 + c;
	  return O;		// in degrees
	}

	Double calcSunTrueAnomaly(Double t) {
	  Double m = calcGeomMeanAnomalySun(t);
	  Double c = calcSunEqOfCenter(t);
	  Double v = m + c;
	  return v;		// in degrees
	}

	Double calcSunRadVector(Double t) {
		Double v = calcSunTrueAnomaly(t);
		Double e = calcEccentricityEarthOrbit(t);
		Double R = (1.000001018 * (1 - e * e)) / (1 + e * Math.cos(degToRad(v)));
	  return R;		// in AUs
	}

	Double calcSunApparentLong(Double t)
	{
	  Double o = calcSunTrueLong(t);
	  Double omega = 125.04 - 1934.136 * t;
	  Double lambda = o - 0.00569 - 0.00478 * Math.sin(degToRad(omega));
	  return lambda;		// in degrees
	}

	Double calcMeanObliquityOfEcliptic(Double t)
	{
	  Double seconds = 21.448 - t*(46.8150 + t*(0.00059 - t*(0.001813)));
	  Double e0 = 23.0 + (26.0 + (seconds/60.0))/60.0;
	  return e0;		// in degrees
	}

	Double calcObliquityCorrection(Double t)
	{
	  Double e0 = calcMeanObliquityOfEcliptic(t);
	  Double omega = 125.04 - 1934.136 * t;
	  Double e = e0 + 0.00256 * Math.cos(degToRad(omega));
	  return e;		// in degrees
	}

	Double calcSunRtAscension(Double t)
	{
	  Double e = calcObliquityCorrection(t);
	  Double lambda = calcSunApparentLong(t);
	  Double tananum = (Math.cos(degToRad(e)) * Math.sin(degToRad(lambda)));
	  Double tanadenom = (Math.cos(degToRad(lambda)));
	  Double alpha = radToDeg(Math.atan2(tananum, tanadenom));
	  return alpha;		// in degrees
	}

	Double calcSunDeclination(Double t) {
		Double e = calcObliquityCorrection(t);
		Double lambda = calcSunApparentLong(t);
		Double sint = Math.sin(degToRad(e)) * Math.sin(degToRad(lambda));
		Double theta = radToDeg(Math.asin(sint));
		return theta;		// in degrees
	}
	
	Double calcEquationOfTime(Double t) {
	  Double epsilon = calcObliquityCorrection(t);
	  Double l0 = calcGeomMeanLongSun(t);
	  Double e = calcEccentricityEarthOrbit(t);
	  Double m = calcGeomMeanAnomalySun(t);

	  Double y = Math.tan(degToRad(epsilon)/2.0);
	  y *= y;

	  Double sin2l0 = Math.sin(2.0 * degToRad(l0));
	  Double sinm   = Math.sin(degToRad(m));
	  Double cos2l0 = Math.cos(2.0 * degToRad(l0));
	  Double sin4l0 = Math.sin(4.0 * degToRad(l0));
	  Double sin2m  = Math.sin(2.0 * degToRad(m));

	  Double Etime = y * sin2l0 - 2.0 * e * sinm + 4.0 * e * y * sinm * cos2l0 - 0.5 * y * y * sin4l0 - 1.25 * e * e * sin2m;
	  return radToDeg(Etime)*4.0;	// in minutes of time
	}
	
	Double calcHourAngleSunrise(Double lat, Double solarDec)
	{
	  Double latRad = degToRad(lat);
	  Double sdRad  = degToRad(solarDec);
	  Double HAarg = (Math.cos(degToRad(90.833))/(Math.cos(latRad)*Math.cos(sdRad))-Math.tan(latRad) * Math.tan(sdRad));
	  Double HA = Math.acos(HAarg);
	  return HA;		// in radians (for sunset, use -HA)
	}
	
	public Double calcSunriseSetUTC(Boolean rise, Double JD, Double latitude, Double longitude) {
	  Double t = calcTimeJulianCent(JD);
	  Double eqTime = calcEquationOfTime(t);
	  Double solarDec = calcSunDeclination(t);
	  Double hourAngle = calcHourAngleSunrise(latitude, solarDec);
	  //alert("HA = " + radToDeg(hourAngle));
	  if (!rise) hourAngle = -hourAngle;
	  Double delta = longitude + radToDeg(hourAngle);
	  Double timeUTC = 720 - (4.0 * delta) - eqTime;	// in minutes
	  return timeUTC;
	}
	
	public String calcSunriseSet(Boolean rise, Double JD, Double latitude, Double longitude, Double timezone, Boolean dst) {
		
		String result = "";
		
		Double timeUTC = calcSunriseSetUTC(rise, JD, latitude, longitude);
		Double newTimeUTC = calcSunriseSetUTC(rise, JD + timeUTC/1440.0, latitude, longitude); 

		Double timeLocal = newTimeUTC + (timezone * 60.0);
    	Double jdy;

		if (!Double.isNaN(newTimeUTC)) {
		    timeLocal += ((dst) ? 60.0 : 0.0);
		    if ( (timeLocal >= 0.0) && (timeLocal < 1440.0) ) {
			  Double jday = JD;
		      result = timeDateString(jday,timeLocal);
		    } else  {
		      Double jday = JD;
		      Integer increment = ((timeLocal < 0) ? 1 : -1);
		      while ((timeLocal < 0.0)||(timeLocal >= 1440.0)) {
		        timeLocal += increment * 1440.0;
		        jday -= increment;
		      }
		      result = timeDateString(jday,timeLocal);
		    }
		  } else { // no sunrise/set found
		    Double doy = calcDoyFromJD(JD);
		    if ( ((latitude > 66.4) && (doy > 79) && (doy < 267)) ||
			((latitude < -66.4) && ((doy < 83) || (doy > 263))) )
		    {   //previous sunrise/next sunset
		      if (rise) { // find previous sunrise
		        jdy = calcJDofNextPrevRiseSet(false, rise, JD, latitude, longitude, timezone, dst);
		      } else { // find next sunset
		        jdy = calcJDofNextPrevRiseSet(true, rise, JD, latitude, longitude, timezone, dst);
		      }
		      result = dayString(jdy, false);
		    } else {   //previous sunset/next sunrise

		      if (rise) { // find previous sunrise
		        jdy = calcJDofNextPrevRiseSet(true, rise, JD, latitude, longitude, timezone, dst);
		      } else { // find next sunset
		        jdy = calcJDofNextPrevRiseSet(false, rise, JD, latitude, longitude, timezone, dst);
		      }
		      result = dayString(jdy, false);
		    }
		  }
		
		return result;
	}
	
	Double calcJDofNextPrevRiseSet(Boolean next, Boolean rise, Double JD, Double latitude, Double longitude, Double tz, Boolean dst)
	{
	  Double julianday = JD;
	  Double increment = ((next) ? 1.0 : -1.0);

	  Double time = calcSunriseSetUTC(rise, julianday, latitude, longitude);
	  while(Double.isNaN(time)){
	    julianday += increment;
	    time = calcSunriseSetUTC(rise, julianday, latitude, longitude);
	  }
	  Double timeLocal = time + tz * 60.0 + ((dst) ? 60.0 : 0.0);
	  while ((timeLocal < 0.0) || (timeLocal >= 1440.0))
	  {
	    Integer incr = ((timeLocal < 0) ? 1 : -1);
	    timeLocal += (incr * 1440.0);
	    julianday -= incr;
	  }
	  return julianday;
	}
	
	public String calculate(Double lat, Double lng, Boolean sunrise) {
		Double JD = getJD();
		Double tz = getTimezone();
		Boolean DST = isDST();
		
//		Double tl = getLocalTime();
//		Double total = JD + tl/1440.0 - tz/24.0;
//		Double T = calcTimeJulianCent(total);
//		calcAzEl(1, T, tl, lat, lng, tz);
//		calcSolNoon(JD, lng, tz, DST);

		String rise = calcSunriseSet(true, JD, lat, lng, tz, DST);
		String set  = calcSunriseSet(false, JD, lat, lng, tz, DST);
		
		if (sunrise) {
			return rise;
		} else {
			return set;
		}
		
	}
}
