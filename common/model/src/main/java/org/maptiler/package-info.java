/*
 * ##############################################################################
 *  $Id$
 *
 *  Project:  GDAL2Tiles, Google Summer of Code 2007 & 2008
 *            Global Map Tiles Classes
 *  Purpose:  Convert a raster into TMS tiles, create KML SuperOverlay EPSG:4326,
 *            generate a simple HTML viewers based on Google Maps and OpenLayers
 *  Author:   Klokan Petr Pridal, klokan at klokan dot cz
 *  Web:      http://www.klokan.cz/projects/gdal2tiles/
 *
 * ##############################################################################
 *  Copyright (c) 2008 Klokan Petr Pridal. All rights reserved.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 *  THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 *  DEALINGS IN THE SOFTWARE.
 * ##############################################################################
 */
/**
 * Modifications:
 * <ul>
 *     <li>Full conversion from Python to Java</li>
 *     <li>Fix random typos in comments</li>
 *     <li>Added Objects to replace Pyhton's tuples</li>
 *     <li>Added {@link org.maptiler.GlobalMercator#TilePixelsBounds(int, int)} method and usage example</li>
 * </ul>
 * The only thing I'm uncertain about is how {@link org.maptiler.GlobalMercator#ZoomForPixelSize(int)} should work,
 * when the for loop doesn't find a match.
 *
 * I cross-referenced all the changes with the map's info windows in the article.
 * <code>javac org/maptiler/*.java && java org.maptiler.globalmaptiles 10 51.5 -0.17</code>
 * (Central London, zoom 10: including Richmond, Wembley and Isle of Dogs)
 *
 * @see <a href="http://www.maptiler.org/google-maps-coordinates-tile-bounds-projection/">
 *     Tiles à la Google Maps: Coordinates, Tile Bounds and Projection</a>
 * @see <a href="http://www.maptiler.org/google-maps-coordinates-tile-bounds-projection/globalmaptiles.py">
 *     Original Python source code</a>
 */
package org.maptiler;
