package org.lome.niceqr.zxing;

public class PositionalSquare {
	final public int top;
	final public int left;
	final public int size;
	final public int blackBorder;
	final public int whiteBorder;
	public PositionalSquare(int top, int left, int size, int blackBorder, int whiteBorder) {
		super();
		this.top = top;
		this.left = left;
		this.size = size;
		this.blackBorder = blackBorder;
		this.whiteBorder = whiteBorder;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PositionalSquare [top=");
		builder.append(top);
		builder.append(", left=");
		builder.append(left);
		builder.append(", size=");
		builder.append(size);
		builder.append(", blackBorder=");
		builder.append(blackBorder);
		builder.append(", whiteBorder=");
		builder.append(whiteBorder);
		builder.append("]");
		return builder.toString();
	}
}
