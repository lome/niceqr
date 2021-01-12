package org.lome.niceqr.zxing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/*
 * Copyright 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.google.zxing.qrcode.encoder.QRCode;

/**
 * This object renders a QR Code as a BitMatrix 2D array of greyscale values.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class QRCodeWriter {

	private static final int QUIET_ZONE_SIZE = 4;

	public QRCodeWithPositionals encode(String contents,
			BarcodeFormat format,
			int width,
			int height,
			Map<EncodeHintType,?> hints) throws WriterException {

		if (contents.isEmpty()) {
			throw new IllegalArgumentException("Found empty contents");
		}

		if (format != BarcodeFormat.QR_CODE) {
			throw new IllegalArgumentException("Can only encode QR_CODE, but got " + format);
		}

		if (width < 0 || height < 0) {
			throw new IllegalArgumentException("Requested dimensions are too small: " + width + 'x' +
					height);
		}

		ErrorCorrectionLevel errorCorrectionLevel = ErrorCorrectionLevel.L;
		int quietZone = QUIET_ZONE_SIZE;
		if (hints != null) {
			ErrorCorrectionLevel requestedECLevel = (ErrorCorrectionLevel) hints.get(EncodeHintType.ERROR_CORRECTION);
			if (requestedECLevel != null) {
				errorCorrectionLevel = requestedECLevel;
			}
			Integer quietZoneInt = (Integer) hints.get(EncodeHintType.MARGIN);
			if (quietZoneInt != null) {
				quietZone = quietZoneInt;
			}
		}
		
		List<PositionalSquare> positionals = new ArrayList<>();
		QRCode code = QREncoder.encode(contents, errorCorrectionLevel, hints, positionals);
		
		return renderResult(code, width, height, quietZone, positionals);
	}

	public static class QRCodeWithPositionals{
		public final QRCode code;
		public final BitMatrix rendered;
		public final List<PositionalSquare> positionals;
		public final BitMatrix dataMatrix;
		public final List<DataSquare> squares;
		public final List<DataSquare> dataSquares;
		public QRCodeWithPositionals(QRCode code, BitMatrix rendered, List<PositionalSquare> positionals,
				List<DataSquare> squares) {
			super();
			this.code = code;
			this.rendered = rendered;
			this.positionals = positionals;
			this.squares = squares;
			this.dataSquares = removePositionalsSquares();
			this.dataMatrix = removePositionals();
		}
		private List<DataSquare> removePositionalsSquares() {
			return squares.stream()
					.filter(s -> !positionalsContains(s.x, s.y))
					.collect(Collectors.toList());
		}
		private BitMatrix removePositionals() {
			BitMatrix matrix = new BitMatrix(rendered.getWidth(), rendered.getHeight());
			for (int x=0; x < rendered.getWidth(); x++) {
				for (int y=0; y < rendered.getHeight(); y++) {
					if (rendered.get(x, y) && !positionalsContains(x,y)) matrix.set(x, y);
				}
			}
			return matrix;
		}
		private boolean positionalsContains(int x, int y) {
			return positionals.stream()
					.filter(p -> p.left <= x)
					.filter(p -> (p.left+p.size) >= x)
					.filter(p -> p.top <= y)
					.filter(p -> (p.top+p.size) >= y)
					.count() > 0L;
		}
	}

	// Note that the input matrix uses 0 == white, 1 == black, while the output matrix uses
	// 0 == black, 255 == white (i.e. an 8 bit greyscale bitmap).
	private static QRCodeWithPositionals renderResult(QRCode code, int width, int height, int quietZone, 
			List<PositionalSquare> positionals) {
		ByteMatrix input = code.getMatrix();
		if (input == null) {
			throw new IllegalStateException();
		}
		int inputWidth = input.getWidth();
		int inputHeight = input.getHeight();
		int qrWidth = inputWidth + (quietZone * 2);
		int qrHeight = inputHeight + (quietZone * 2);
		int outputWidth = Math.max(width, qrWidth);
		int outputHeight = Math.max(height, qrHeight);

		int multiple = Math.min(outputWidth / qrWidth, outputHeight / qrHeight);
		// Padding includes both the quiet zone and the extra white pixels to accommodate the requested
		// dimensions. For example, if input is 25x25 the QR will be 33x33 including the quiet zone.
		// If the requested size is 200x160, the multiple will be 4, for a QR of 132x132. These will
		// handle all the padding from 100x100 (the actual QR) up to 200x160.
		int leftPadding = (outputWidth - (inputWidth * multiple)) / 2;
		int topPadding = (outputHeight - (inputHeight * multiple)) / 2;

		BitMatrix output = new BitMatrix(outputWidth, outputHeight);
		List<DataSquare> squares = new ArrayList<>();

		for (int inputY = 0, outputY = topPadding; inputY < inputHeight; inputY++, outputY += multiple) {
			// Write the contents of this row of the barcode
			for (int inputX = 0, outputX = leftPadding; inputX < inputWidth; inputX++, outputX += multiple) {
				if (input.get(inputX, inputY) == 1) {
					output.setRegion(outputX, outputY, multiple, multiple);
					squares.add(new DataSquare(true, outputX, outputY, multiple));
				}else {
					squares.add(new DataSquare(false, outputX, outputY, multiple));
				}
			}
		}
		
		positionals = positionals.stream()
		.map(p -> 
			new PositionalSquare(
					p.top*multiple+topPadding, 
					p.left*multiple+leftPadding, 
					p.size*multiple, 
					p.blackBorder*multiple, 
					p.whiteBorder*multiple))
		.collect(Collectors.toList());

		return new QRCodeWithPositionals(code,output,positionals,squares);
	}
	
	public static class DataSquare{
		final public boolean black;
		final public int x;
		final public int y;
		final public int size;
		public DataSquare(boolean black, int x, int y, int size) {
			super();
			this.black = black;
			this.x = x;
			this.y = y;
			this.size = size;
		}
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("DataSquare [black=");
			builder.append(black);
			builder.append(", x=");
			builder.append(x);
			builder.append(", y=");
			builder.append(y);
			builder.append(", size=");
			builder.append(size);
			builder.append("]");
			return builder.toString();
		}
	}

}

