package frontend.ui.views;

import backend.models.Arete;
import backend.models.Ligne;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class ArreteView extends Line {
	private static final Color DEFAULT_COLOR = Color.BLACK;
	private static final double DEFAULT_WIDTH = 2.0;
	private Color color=DEFAULT_COLOR;
	private static final Color HIGHLIGHT_WIDTH_COLOR = Color.web("#FFC400");
	private static final double HIGHLIGHT_WIDTH = 4.0;

	private final Arete arete;

	public ArreteView(Arete arete, double x1, double y1, double x2, double y2) {
		super(x1, y1, x2, y2);
		this.arete = arete;
		setStroke(color);
		setStrokeWidth(DEFAULT_WIDTH);
		getStrokeDashArray().addAll(10.0, 6.0);

	}

	public ArreteView(Arete arete, double x1, double y1, double x2, double y2, Ligne ligne) {
		super(x1, y1, x2, y2);
		this.arete = arete;
		color = Color.web(ligne.getCouleur());
		setStroke(color);
		setStrokeWidth(DEFAULT_WIDTH);
		getStrokeDashArray().addAll(10.0, 6.0);
	}

	public void setHighlighted(boolean highlighted) {
		setStroke(highlighted ? HIGHLIGHT_WIDTH_COLOR : color);
		setStrokeWidth(highlighted ? HIGHLIGHT_WIDTH : DEFAULT_WIDTH);
		if(highlighted) getStrokeDashArray().clear();
		else getStrokeDashArray().addAll(10.0, 6.0);
	}

	public Arete getArete() {
		return arete;
	}
}
