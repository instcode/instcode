package me.instcode.undo.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

public class ArrowIcon implements Icon, SwingConstants {
	static final int DEFAULT_SIZE = 4;

	Color black = new Color(0, 0, 0);

	Color shadow = null;

	Color highlight = null;

	int size;

	int m_direction;

	/**
	 * Contruct an arrow icon
	 * @param direction : use SwingConstants
	 */
	public ArrowIcon(int direction) {
		this(direction, DEFAULT_SIZE);
	}

	public ArrowIcon(int direction, int size) {
		this.m_direction = direction;
		shadow = UIManager.getColor("controlShadow");
		highlight = UIManager.getColor("controlLtHighlight");
		setSize(size);
	}

	public void setSize(int nSize) {
		this.size = nSize;
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
		paintTriangle(g, x, y, m_direction, c.isEnabled());
	}

	public int getIconWidth() {
		return size;
	}

	public int getIconHeight() {
		return size;
	}

	private void paintTriangle(Graphics g, int x, int y, int direction,
			boolean isEnabled) {
		Color oldColor = g.getColor();
		int mid, i, j;

		j = 0;
		size = Math.max(size, 2);
		mid = (size / 2) - 1;

		g.translate(x, y);
		if (isEnabled)
			g.setColor(black);
		else
			g.setColor(shadow);

		switch (direction) {
		case NORTH:
			for (i = 0; i < size; i++) {
				g.drawLine(mid - i, i, mid + i, i);
			}
			if (!isEnabled) {
				g.setColor(highlight);
				g.drawLine(mid - i + 2, i, mid + i, i);
			}
			break;
		case SOUTH:
			if (!isEnabled) {
				g.translate(1, 1);
				g.setColor(highlight);
				for (i = size - 1; i >= 0; i--) {
					g.drawLine(mid - i, j, mid + i, j);
					j++;
				}
				g.translate(-1, -1);
				g.setColor(shadow);
			}

			j = 0;
			for (i = size - 1; i >= 0; i--) {
				g.drawLine(mid - i, j, mid + i, j);
				j++;
			}
			break;
		case WEST:
			for (i = 0; i < size; i++) {
				g.drawLine(i, mid - i, i, mid + i);
			}
			if (!isEnabled) {
				g.setColor(highlight);
				g.drawLine(i, mid - i + 2, i, mid + i);
			}
			break;
		case EAST:
			if (!isEnabled) {
				g.translate(1, 1);
				g.setColor(highlight);
				for (i = size - 1; i >= 0; i--) {
					g.drawLine(j, mid - i, j, mid + i);
					j++;
				}
				g.translate(-1, -1);
				g.setColor(shadow);
			}

			j = 0;
			for (i = size - 1; i >= 0; i--) {
				g.drawLine(j, mid - i, j, mid + i);
				j++;
			}
			break;
		}
		g.translate(-x, -y);
		g.setColor(oldColor);
	}
}