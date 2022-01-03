package com.whitearl.stockpredictor.application.model;

import java.util.Date;

public class StockCandle implements Comparable<StockCandle> {

	private static final String COMMA = ",";

	private Double closePrice;
	private Double highPrice;
	private Double lowPrice;
	private Double openPrice;
	private Long   timestamp;
	private Long   volume;

	public StockCandle(Double openPrice, Double closePrice, Double highPrice, Double lowPrice, Long timestamp, Long volumes) {

		this.closePrice = closePrice;
		this.highPrice  = highPrice;
		this.lowPrice   = lowPrice;
		this.openPrice  = openPrice;
		this.timestamp  = timestamp;
		this.volume     = volumes;
	}
	
	public StockCandle(Double closePrice, Long timestamp) {
		this.timestamp  = timestamp;
		this.closePrice = closePrice;
	}

	public String getCSV(StringBuilder sb, boolean newLine) {
		String csv = "";
		if (sb != null) {
			sb.append(timestamp.toString() + COMMA);
			sb.append(volume.toString() + COMMA);
			sb.append(highPrice.toString() + COMMA);
			sb.append(lowPrice.toString() + COMMA);
			sb.append(openPrice.toString() + COMMA);
			sb.append(closePrice.toString());
			
			if (newLine) {sb.append(System.lineSeparator());}
			csv = sb.toString();
			sb.setLength(0);
		}
				
		return csv;
	}

	public Date getDateFromTimestamp() {
		return new Date(this.getTimestamp() * 1000L);
	}
	
	public String getDateStringFromTimestamp() {
		return getDateFromTimestamp().toString();
	}
	
	@Override
	public int compareTo(StockCandle o) {
		if (o instanceof StockCandle) {
			return compare(this.timestamp, o.getTimestamp());
		} else {
			return -1;
		}
	}

	private int compare(long a, long b) {
		int comparison = 0;
		if (a > b) {
			comparison = -1;
		} else if (a < b) {
			comparison = 1;
		}

		// If the timestamps are the same then return 0 by default
		return comparison;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof StockCandle) {
			return this.getTimestamp().equals(((StockCandle) obj).getTimestamp());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return timestamp.hashCode();
	}

	public Double getClosePrice() {
		return closePrice;
	}

	public void setClosePrice(Double closePrice) {
		this.closePrice = closePrice;
	}

	public Double getHighPrice() {
		return highPrice;
	}

	public void setHighPrice(Double highPrice) {
		this.highPrice = highPrice;
	}

	public Double getLowPrice() {
		return lowPrice;
	}

	public void setLowPrice(Double lowPrice) {
		this.lowPrice = lowPrice;
	}

	public Double getOpenPrice() {
		return openPrice;
	}

	public void setOpenPrice(Double openPrice) {
		this.openPrice = openPrice;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public Long getVolume() {
		return volume;
	}

	public void setVolume(Long volume) {
		this.volume = volume;
	}

}