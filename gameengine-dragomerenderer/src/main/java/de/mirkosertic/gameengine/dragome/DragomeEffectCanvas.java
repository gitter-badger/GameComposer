package de.mirkosertic.gameengine.dragome;

import de.mirkosertic.gameengine.generic.CSSUtils;
import de.mirkosertic.gameengine.type.Color;
import de.mirkosertic.gameengine.type.EffectCanvas;
import de.mirkosertic.gameengine.type.Position;

import com.dragome.web.html.dom.html5canvas.interfaces.CanvasRenderingContext2D;

public class DragomeEffectCanvas implements EffectCanvas {

    private final CanvasRenderingContext2D renderingContext;

    public DragomeEffectCanvas(CanvasRenderingContext2D renderingContext) {
        this.renderingContext = renderingContext;
    }

    @Override
    public void setPaint(Color aColor) {
        String theColor = CSSUtils.toColor(aColor);
        renderingContext.setStrokeStyle(theColor);
    }

    @Override
    public void drawSingleDot(Position aPosition) {
        renderingContext.strokeRect(aPosition.x, aPosition.y, 1, 1);
    }
}
