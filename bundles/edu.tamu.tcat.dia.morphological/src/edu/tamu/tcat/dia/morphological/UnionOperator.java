package edu.tamu.tcat.dia.morphological;

import edu.tamu.tcat.dia.binarization.BinaryImage;
import edu.tamu.tcat.dia.binarization.BooleanArrayBinaryImage;

public final class UnionOperator {

	private BinaryImage input1, input2;
	private int width;
	private int height;
	private BooleanArrayBinaryImage output;

	public UnionOperator(BinaryImage image1, BinaryImage image2)
			throws IllegalArgumentException {

		if ((image1.getHeight() != image2.getHeight())
				|| (image1.getWidth() != image2.getWidth()))
			throw new IllegalArgumentException(
					"Input image sizes not equal, cannot proceed");

		this.input1 = image1;
		this.input2 = image2;

		this.width = input1.getWidth();
		this.height = input1.getHeight();

		this.output = new BooleanArrayBinaryImage(width, height);
	}

	public UnionOperator(BinaryImage image1, BinaryImage image2,
			boolean padWithZeros) throws IllegalArgumentException {

		BooleanArrayBinaryImage newImage, newImage2;
		
		if (padWithZeros) {
			if (image1.getSize() != image2.getSize()) {
				System.out.println("Image sizes not equal, will proceed with padding appropriately");
				if (image1.getHeight() > image2.getHeight()) {
					System.out.println("image1 taller, padding image2 height");
					newImage = (BooleanArrayBinaryImage) padWithZeros(image2,
							0, image1.getHeight() - image2.getHeight());
					// compare width - image2 is in newImage now
					if (image1.getWidth() > newImage.getWidth()) {
						System.out.println("image1 wider, padding image2 width");
						newImage2 = (BooleanArrayBinaryImage) padWithZeros(
								newImage,
								image1.getWidth() - newImage.getWidth(), 0);
						this.input1 = image1;
						this.input2 = newImage2;
					} else if (newImage.getWidth() > image1.getWidth()) {
						System.out.println("image2 wider, padding image1 width");
						newImage2 = (BooleanArrayBinaryImage) padWithZeros(
								image1,
								newImage.getWidth() - image1.getWidth(), 0);
						this.input1 = newImage2;
						this.input2 = newImage;
					} else if (image1.getWidth() == newImage.getWidth()) {
						System.out.println("Widths equal");
						this.input1 = image1;
						this.input2 = newImage;
					}

				} else if (image2.getHeight() > image1.getHeight()) {
					System.out.println("image2 taller, padding image1 height");
					newImage = (BooleanArrayBinaryImage) padWithZeros(image1,
							0, image2.getHeight() - image1.getHeight());
					// check widths - image1 is now in newImage
					if (image2.getWidth() > newImage.getWidth()) {
						System.out.println("image2 wider, padding image1 width");
						newImage2 = (BooleanArrayBinaryImage) padWithZeros(
								newImage,
								image2.getWidth() - newImage.getWidth(), 0);
						this.input1 = newImage2;
						this.input2 = image2;
					} else if (newImage.getWidth() > image2.getWidth()) {
						System.out.println("image1 wider, padding image2 width");
						newImage2 = (BooleanArrayBinaryImage) padWithZeros(
								image2,
								newImage.getWidth() - image2.getWidth(), 0);
						this.input1 = newImage;
						this.input2 = newImage2;
					} else if (image2.getWidth() == newImage.getWidth()) {
						System.out.println("Widths equal");
						this.input1 = newImage;
						this.input2 = image2;
					}

				} else if (image2.getHeight() == image1.getHeight()) {
					System.out.println("heights equal, comparing widths");
					if (image1.getWidth() > image2.getWidth()) {
						System.out.println("image1 wider, padding image2 width");
						newImage = (BooleanArrayBinaryImage) padWithZeros(
								image2, image1.getWidth() - image2.getWidth(),
								0);
						this.input1 = image1;
						this.input2 = newImage;
					} else if (image2.getWidth() > image1.getWidth()) {
						System.out.println("image2 wider, padding image1 width");
						newImage = (BooleanArrayBinaryImage) padWithZeros(
								image1, image2.getWidth() - image1.getWidth(),
								0);
						this.input1 = newImage;
						this.input2 = image2;
					}
				}
			} else {
				System.out.println("Sizes equal, no padding necessary");
				this.input1 = image1;
				this.input2 = image2;

			}
		}else{
			System.out.println("padWithZeros false, creating UnionOperator");
			new UnionOperator(image1,image2);
		}

		this.width = input1.getWidth();
		this.height = input1.getHeight();

		this.output = new BooleanArrayBinaryImage(width, height);
	}

	public BinaryImage run() {

		for (int i = 0; i < input1.getSize(); i++) {
			if (input1.isForeground(i) || input2.isForeground(i))
				output.setForeground(i);
			else
				output.setBackground(i);
		}
		return output;

	}

	public BinaryImage addRows(BinaryImage source, int numRows) {

		BooleanArrayBinaryImage imageOut = new BooleanArrayBinaryImage(
				source.getWidth(), source.getHeight() + numRows);

		for (int i = 0; i < source.getSize(); i++) {
			if (source.isForeground(i))
				imageOut.setForeground(i);
			else
				imageOut.setBackground(i);
		}
		for (int i = source.getSize(); i < imageOut.getSize(); i++) {
			imageOut.setBackground(i);
		}
		return imageOut;

	}

	public BinaryImage padWithZeros(BinaryImage source, int padWidth,
			int padHeight) {

		BooleanArrayBinaryImage imageOut = new BooleanArrayBinaryImage(
				source.getWidth() + padWidth, source.getHeight() + padHeight);

		int scaledWidth = imageOut.getWidth();
		int offset = 0;
		for (int i = 0; i < source.getHeight(); i++) {
			for (int j = 0; j < source.getWidth(); j++) {
				if (source.isForeground(j, i))
					imageOut.setForeground(offset + j);
			}
			offset += scaledWidth;
		}
		return imageOut;

	}

}
