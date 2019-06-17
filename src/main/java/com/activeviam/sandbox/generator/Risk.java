/*
 * (C) ActiveViam 2007-2019
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of Quartet Financial Systems Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.sandbox.generator;

import static com.activeviam.sandbox.generator.DataGenerator.CSV_SEPARATOR;

import java.io.PrintWriter;

/**
 * 
 * @author ActiveViam
 *
 */
public class Risk {

	protected final long tradeId;
	protected final double delta;
	protected final double gamma;
	protected final double vega;
	protected final double pnl;
	protected final double pnlDelta;
	protected final double pnlVega;

	
	public Risk(long tradeId, double delta, double gamma, double vega, double pnl, double pnlDelta, double pnlVega) {
		this.tradeId = tradeId;
		this.delta = delta;
		this.gamma = gamma;
		this.vega = vega;
		this.pnl = pnl;
		this.pnlDelta = pnlDelta;
		this.pnlVega = pnlVega;
	}

	public long getTradeId() {
		return tradeId;
	}
	public double getDelta() {
		return delta;
	}
	public double getGamma() {
		return gamma;
	}
	public double getVega() {
		return vega;
	}
	public double getPnl() {
		return pnl;
	}
	public double getPnlDelta() {
		return pnlDelta;
	}
	public double getPnlVega() {
		return pnlVega;
	}
	
	
	public static void appendCsvHeader(PrintWriter w) {
		w.append("TradeId");
		w.append(CSV_SEPARATOR).append("Delta");
		w.append(CSV_SEPARATOR).append("Gamma");
		w.append(CSV_SEPARATOR).append("Vega");
		w.append(CSV_SEPARATOR).append("Pnl");
		w.append(CSV_SEPARATOR).append("PnlDelta");
		w.append(CSV_SEPARATOR).append("PnlVega");
	}
	
	/**
	 * Append a CSV representation of this object into a writer.
	 */
	public void appendCsvRow(PrintWriter sb) {
		sb.print(getTradeId());
		sb.append(CSV_SEPARATOR).print(getDelta());
		sb.append(CSV_SEPARATOR).print(getGamma());
		sb.append(CSV_SEPARATOR).print(getVega());
		sb.append(CSV_SEPARATOR).print(getPnl());
		sb.append(CSV_SEPARATOR).print(getPnlDelta());
		sb.append(CSV_SEPARATOR).print(getPnlVega());
	}
}
