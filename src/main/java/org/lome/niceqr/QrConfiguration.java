package org.lome.niceqr;

import java.awt.Color;

public class QrConfiguration {
	
	// Final image size, multiple of 4 required
	int size = 200;
	// Dark Color
	Color darkColor = Color.black;
	// Light Color
	Color lightColor = Color.white;
	// Positionals Inner Color
	Color positionalsColor = Color.black;
	// Relative logo size. Keep it under .25
	double relativeLogoSize = .2;
	//Relative border size
	double relativeBorderSize = .05;
	//Relative border round
	double relativeBorderRound = .25;
	//Use circular positional markers
	boolean circularPositionals = false;
	//Relative Positional round
	double relativePositionalsRound = .5;
	
	private QrConfiguration() {
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public Color getDarkColor() {
		return darkColor;
	}

	public void setDarkColor(Color darkColor) {
		this.darkColor = darkColor;
	}

	public Color getLightColor() {
		return lightColor;
	}

	public void setLightColor(Color lightColor) {
		this.lightColor = lightColor;
	}

	public double getRelativeLogoSize() {
		return relativeLogoSize;
	}

	public void setRelativeLogoSize(double relativeLogoSize) {
		this.relativeLogoSize = relativeLogoSize;
	}

	public double getRelativeBorderSize() {
		return relativeBorderSize;
	}

	public void setRelativeBorderSize(double relativeBorderSize) {
		this.relativeBorderSize = relativeBorderSize;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public double getRelativeBorderRound() {
		return relativeBorderRound;
	}

	public void setRelativeBorderRound(double relativeBorderRound) {
		this.relativeBorderRound = relativeBorderRound;
	}

	public boolean isCircularPositionals() {
		return circularPositionals;
	}

	public void setCircularPositionals(boolean circularPositionals) {
		this.circularPositionals = circularPositionals;
	}

	public double getRelativePositionalsRound() {
		return relativePositionalsRound;
	}

	public void setRelativePositionalsRound(double relativePositionalsRound) {
		this.relativePositionalsRound = relativePositionalsRound;
	}
	
	public static class Builder{
		QrConfiguration _conf;
		public Builder() {
			this._conf = new QrConfiguration();
		}
		
		public QrConfiguration build() {
			return _conf;
		}
		
		public Builder withSize(int size) {
			_conf.size = size;
			return this;
		}

		public Builder withDarkColor(Color darkColor) {
			_conf.darkColor = darkColor;
			return this;
		}

		public Builder withLightColor(Color lightColor) {
			_conf.lightColor = lightColor;
			return this;
		}

		public Builder withRelativeLogoSize(double relativeLogoSize) {
			_conf.relativeLogoSize = relativeLogoSize;
			return this;
		}

		public Builder withRelativeBorderSize(double relativeBorderSize) {
			_conf.relativeBorderSize = relativeBorderSize;
			return this;
		}
		
		public Builder withRelativeBorderRound(double relativeBorderRound) {
			_conf.relativeBorderRound = relativeBorderRound;
			return this;
		}

		public Builder withCircularPositionals(boolean circularPositionals) {
			_conf.circularPositionals = circularPositionals;
			return this;
		}

		public Builder withRelativePositionalsRound(double relativePositionalsRound) {
			_conf.relativePositionalsRound = relativePositionalsRound;
			return this;
		}
		
		public Builder withPositionalsColor(Color positionalsColor) {
			_conf.positionalsColor = positionalsColor;
			return this;
		}
	}
	
	@SuppressWarnings("serial")
	public static class InvalidConfigurationException extends Exception{

		public InvalidConfigurationException(String message) {
			super(message);
		}
		
	}

	public Color getPositionalsColor() {
		return positionalsColor;
	}

	public void setPositionalsColor(Color positionalsColor) {
		this.positionalsColor = positionalsColor;
	}

}
