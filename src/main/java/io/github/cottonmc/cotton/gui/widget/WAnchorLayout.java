package io.github.cottonmc.cotton.gui.widget;

import java.util.concurrent.ConcurrentHashMap;

public class WAnchorLayout {
  public class AnchorException extends Exception {
    private static final long serialVersionUID = -4344321727684528775L;

    public AnchorException(AnchorSide side1, String what1, AnchorSide side2, String what2, String why) {
      super("Unable to link " + side1.toString() + " of " + what1 + " to " + side2.toString() + " of " + what2 + ": " + why);
    }
  }

  public enum AnchorSide {
    TOP,
    RIGHT,
    BOTTOM,
    LEFT,
  }

  public class Anchor {
    protected final String anchorName;
    protected final AnchorSide anchorSide;
    protected String anchorTarget = null;
    protected AnchorSide anchorTargetSide = null;
    protected AnchoredWidget linkedAnchorTarget = null;

    public Anchor(String _anchorName, AnchorSide _anchorSide) {
      anchorName = _anchorName;
      anchorSide = _anchorSide;
    }

    public void bind(String _anchorTarget, AnchorSide _anchorTargetSide) {
      anchorTarget = _anchorTarget;
      anchorTargetSide = _anchorTargetSide;
    }

    public void link(WAnchorLayout layout) throws AnchorException {
      if (anchorTarget == null) {
        return;
      }
      AnchoredWidget w = layout.getWidgetByName(anchorTarget);
      if (w == null) {
        throw new AnchorException(anchorSide, anchorName, anchorTargetSide, anchorTarget, "No such anchor: " + anchorTarget);
      }
      linkedAnchorTarget = w;
    }
  }
  
  public class AnchoredWidget {
    protected WWidget widget;
    protected final String name;
    protected Anchor top, right, bottom, left;

    public AnchoredWidget(String _name, WWidget _widget) {
      widget = _widget;
      name = _name;
      top = new Anchor(name, AnchorSide.TOP);
      right = new Anchor(name, AnchorSide.RIGHT);
      bottom = new Anchor(name, AnchorSide.BOTTOM);
      left = new Anchor(name, AnchorSide.LEFT);
    }

    public void link(WAnchorLayout layout) throws AnchorException {
      if (top    != null) top.link(layout);
      if (right  != null) right.link(layout);
      if (bottom != null) bottom.link(layout);
      if (left   != null) left.link(layout);
    }

    public AnchoredWidget bind(AnchorSide side, AnchorSide toSide, String ofWhat) {
      Anchor a = null;
      if (side == null) {
        return this;
      } else if (side == AnchorSide.TOP) {
        a = top;
      } else if (side == AnchorSide.RIGHT) {
        a = right;
      } else if (side == AnchorSide.BOTTOM) {
        a = bottom;
      } else if (side == AnchorSide.LEFT) {
        a = left;
      }
      // a.anchorSide
      return this;
    }
  }

  protected ConcurrentHashMap<String, AnchoredWidget> children;

  void add(String name, AnchoredWidget widget) {
    children.put(name, widget);
  }

  AnchoredWidget getWidgetByName(String name) {
    return children.get(name);
  }

  void link() {
    children.forEach((k, w) -> {
      try {
        w.link(this);
      } catch (AnchorException e) {
        e.printStackTrace();
      }
    });
  }
}