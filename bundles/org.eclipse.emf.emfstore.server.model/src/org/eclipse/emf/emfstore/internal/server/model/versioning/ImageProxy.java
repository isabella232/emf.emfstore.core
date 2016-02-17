/*******************************************************************************
 * Copyright (c) 2015 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server.model.versioning;

/**
 * Proxy for an ImageData object in order to avoid depdendency to SWT.
 *
 * @author emueller
 *
 */
public final class ImageProxy {

  public static class RGB {
    private int red;
    private int green;
    private int blue;

    public RGB(int red, int green, int blue) {
      this.red = red;
      this.green = green;
      this.blue = blue;
    }

    public int getRed() {
      return red;
    }

    public int getGreen() {
      return green;
    }

    public int getBlue() {
      return blue;
    }

  }

  private int width;
  private int height;
  private int depth;
  private int scanlinePad;
  private byte[] data;
  private int redMask;
  private int greenMask;
  private int blueMask;
  private boolean direct = true;
  private RGB[] paletteColors = null;

  private ImageProxy() {

  }

  /**
   * Creates an image proxy.
   *
   * @return the created image proxy
   */
  public static ImageProxy create() {
    return new ImageProxy();
  }

  /**
   * Sets the width of an image.
   *
   * @param width the width
   * @return this {@link ImageProxy}
   */
  public ImageProxy setWitdh(int width) {
    this.width = width;
    return this;
  }

  /**
   * Sets the height of an image.
   *
   * @param height the height
   * @return this {@link ImageProxy}
   */
  public ImageProxy setHeight(int height) {
    this.height = height;
    return this;
  }

  /**
   * Sets the color depth.
   *
   * @param depth the color depth
   * @return this {@link ImageProxy}
   */
  public ImageProxy setDepth(int depth) {
    this.depth = depth;
    return this;
  }

  /**
   * Sets the scanline pad.
   *
   * @param scanlinePad the scanline pad
   * @return this {@link ImageProxy}
   */
  public ImageProxy setScanlinePad(int scanlinePad) {
    this.scanlinePad = scanlinePad;
    return this;
  }

  /**
   * Sets the actual image data.
   *
   * @param data the image data as a byte array
   * @return this {@link ImageProxy}
   */
  public ImageProxy setData(byte[] data) {
    this.data = data;
    return this;
  }

  /**
   * @return the width
   */
  public int getWidth() {
    return width;
  }

  /**
   * @param width the width to set
   */
  public void setWidth(int width) {
    this.width = width;
  }

  /**
   * @return the height
   */
  public int getHeight() {
    return height;
  }

  /**
   * @return the data
   */
  public byte[] getData() {
    return data;
  }

  /**
   * @return the depth
   */
  public int getDepth() {
    return depth;
  }

  /**
   * @return the scanlinePad
   */
  public int getScanlinePad() {
    return scanlinePad;
  }

  /**
   * Returns the red mask for a {@code Palette}.
   *
   * @return the red mask
   */
  public int getRedMask() {
    return redMask;
  }

  /**
   * Returns the green mask for a {@code Palette}.
   *
   * @return the green mask
   */
  public int getGreenMask() {
    return greenMask;
  }

  /**
   * Returns the blue mask for a {@code Palette}.
   *
   * @return the blue mask
   */
  public int getBlueMask() {
    return blueMask;
  }

  /**
   * Sets the red mask for a {@code Palette}.
   *
   * @param mask the red mask
   * @return this {@link ImageProxy}
   */
  public ImageProxy setRedMask(int mask) {
    redMask = mask;
    return this;
  }

  /**
   * Sets the green mask for a {@code Palette}.
   *
   * @param mask the green mask
   * @return this {@link ImageProxy}
   */
  public ImageProxy setGreenMask(int mask) {
    greenMask = mask;
    return this;
  }

  /**
   * Sets the blue mask for a {@code Palette}.
   *
   * @param mask the blue mask
   * @return this {@link ImageProxy}
   */
  public ImageProxy setBlueMask(int mask) {
    blueMask = mask;
    return this;
  }

  /**
   * set palette colors, the palette direct flag is set to false
   *
   * @param colors colors to set
   * @return this {@link ImageProxy}
   */
  public ImageProxy setPaletteColors(RGB[] colors) {
    paletteColors = colors;
    direct = false;
    return this;
  }

  public RGB[] getPaletteColors() {
    return paletteColors;
  }

  public boolean isDirect() {
    return direct;
  }

}
