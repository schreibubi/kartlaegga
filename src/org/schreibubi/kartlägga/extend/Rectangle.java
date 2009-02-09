/**
 * Copyright (C) 2009 joerg <schreibubi@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.schreibubi.kartlägga.extend;

public class Rectangle {

	public double x;
	public double y;
	public double width;
	public double height;

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public Rectangle(double x, double y, double width, double height) {
		setBounds(x,y,width,height);
	}

	public double getCenterX() {
		return x + width / 2;
	}

	public double getCenterY() {
		return y + height / 2;
	}

	public boolean contains(Rectangle rect) {
		if ((rect.getX() > x) && (rect.getY() > y)
				&& (rect.getX() + rect.getWidth() < x + width)
				&& (rect.getY() + rect.getHeight() < y + height))
			return true;
		else
			return false;
	}

	public Rectangle intersection(Rectangle r) {
		double x1 = Math.max(x, r.x);
		double y1 = Math.max(y, r.y);
		double x2 = Math.min(x + width, r.x + r.width);
		double y2 = Math.min(y + height, r.y + r.height);
		return new Rectangle(x1, y1, x2 - x1, y2 - y1);
	}

	public boolean intersects(Rectangle r) {
		return !intersection(r).isEmpty();
	}

	public boolean isEmpty() {
		return width <= 0 || height <= 0;
	}

	public double getMaxY() {
		return y + height;
	}

	public double getMaxX() {
		return x + height;
	}
    public void add(double px, double py) {
        double x1 = Math.min(x, px);
        double x2 = Math.max(x + width, px);
        double y1 = Math.min(y, py);
        double y2 = Math.max(y + height, py);
        setBounds(x1, y1, x2 - x1, y2 - y1);
    }

    public void add(Point2D p) {
        add(p.x, p.y);
    }

    public void add(Rectangle r) {
        double x1 = Math.min(x, r.x);
        double x2 = Math.max(x + width, r.x + r.width);
        double y1 = Math.min(y, r.y);
        double y2 = Math.max(y + height, r.y + r.height);
        setBounds(x1, y1, x2 - x1, y2 - y1);
    }
    public void setBounds(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
    }

}
