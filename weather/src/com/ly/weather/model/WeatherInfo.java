package com.ly.weather.model;

public class WeatherInfo {
	

	public String getWeather() {
		return weather;
	}

	public void setWeather(String weather) {
		this.weather = weather;
	}

	public String getWind() {
		return wind;
	}

	public void setWind(String wind) {
		this.wind = wind;
	}

	public String getTemperature() {
		return temperature;
	}

	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}

	private String week;
	public String getWeek() {
		return week;
	}

	public void setWeek(String week) {
		this.week = week;
	}

	private String weather;
	private String wind;
	private String temperature;
}
