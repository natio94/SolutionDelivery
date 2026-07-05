package frontend.ui.views;

import backend.models.Arete;
import backend.models.Ligne;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class AreteView extends Line {
	private static final Color DEFAULT_COLOR = Color.BLACK;
	private static final double DEFAULT_WIDTH = 2.0;
	private Color color=DEFAULT_COLOR;
	private static final double HIGHLIGHT_WIDTH = 6.0;

	private final Arete arete;

	public AreteView(Arete arete, double x1, double y1, double x2, double y2) {
		super(x1, y1, x2, y2);
		this.arete = arete;
		setStroke(color);
		setStrokeWidth(DEFAULT_WIDTH);
		getStrokeDashArray().addAll(10.0, 6.0);

	}

	public AreteView(Arete arete, double x1, double y1, double x2, double y2, Ligne ligne) {
		super(x1, y1, x2, y2);
		this.arete = arete;
		color = Color.web(ligne.getCouleur(),0.25);
		setStroke(color);
		setStrokeWidth(DEFAULT_WIDTH);
		getStrokeDashArray().addAll(10.0, 6.0);
	}

	public AreteView(Arete arete, double x1, double y1, double x2, double y2, boolean pied) {
		super(x1, y1, x2, y2);
		this.arete = arete;
		color = Color.web("#666666", 0.8);
		setStroke(color);
		setStrokeWidth(1.5);
		getStrokeDashArray().addAll(4.0, 4.0);
	}


	public void setHighlighted(boolean highlighted) {
		setStroke(highlighted ? color.deriveColor(1,1,1,4) : color);
		setStrokeWidth(highlighted ? HIGHLIGHT_WIDTH : DEFAULT_WIDTH);
		if(highlighted) getStrokeDashArray().clear();
		else getStrokeDashArray().addAll(10.0, 6.0);
	}

	public Arete getArete() {
		return arete;
	}
}
