import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

import java.util.Stack;


public class KdTree {

    private static class KdTreeNode {
        private Point2D value;
        private KdTreeNode leftBottom;
        private KdTreeNode rightTop;
        private boolean isVertical;
        private RectHV rect;

        public KdTreeNode(Point2D value, boolean isVertical, RectHV rect) {
            this.value = value;
            this.leftBottom = null;
            this.rightTop = null;
            this.isVertical = isVertical;
            this.rect = rect;
        }

    }

    private KdTreeNode root;
    private int size;
    private double minDist;
    private Stack<Point2D> allPts;
    private Point2D nearestPt;

    public KdTree() {
        root = null;
        size = 0;
        nearestPt = null;
        allPts = new Stack<Point2D>();
    }                           // construct an empty set of points

    public boolean isEmpty() {
        return root == null;
    }                // is the set empty?

    public int size() {
        return size;
    }          // number of points in the set

    private RectHV findRect(KdTreeNode parent, Point2D p) {
        RectHV parentRect = parent.rect;
        double xmin = 0.0;
        double xmax = 0.0;
        double ymin = 1.0;
        double ymax = 1.0;
        if (parent.isVertical && p.x() < parent.value.x()) {
            xmin = parentRect.xmin();
            xmax = parent.value.x();
            ymin = parentRect.ymin();
            ymax = parentRect.ymax();
        } else if (parent.isVertical) {
            xmin = parent.value.x();
            xmax = parentRect.xmax();
            ymin = parentRect.ymin();
            ymax = parentRect.ymax();
        } else if (p.y() < parent.value.y()) {
            xmin = parentRect.xmin();
            xmax = parentRect.xmax();
            ymin = parentRect.ymin();
            ymax = parent.value.y();
        } else {
            xmin = parentRect.xmin();
            xmax = parentRect.xmax();
            ymin = parent.value.y();
            ymax = parentRect.ymax();
        }

        return new RectHV(xmin, ymin, xmax, ymax);
    }

    public void insert(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("Null argument");
        }
        if (root == null) {
            RectHV newRect = new RectHV(0.0, 0.0, 1.0, 1.0);
            root = new KdTreeNode(p, true, newRect);
            size++;
            return;
        }
        KdTreeNode current = root;
        boolean done = false;
        boolean isLeftBottom = true;
        while (!done && !current.value.equals(p)) {
            if (current.isVertical) {
                if (p.x() < current.value.x()) {
                    if (current.leftBottom == null) {
                        done = true;
                    } else current = current.leftBottom;
                } else {
                    if (current.rightTop == null) {
                        isLeftBottom = false;
                        done = true;
                    } else current = current.rightTop;
                }
            } else {
                if (p.y() < current.value.y()) {
                    if (current.leftBottom == null) {
                        done = true;
                    } else current = current.leftBottom;
                } else {
                    if (current.rightTop == null) {
                        isLeftBottom = false;
                        done = true;
                    } else current = current.rightTop;
                }
            }
        }
        if (!current.value.equals(p)) {
            RectHV rect = findRect(current, p);
            KdTreeNode newNode = new KdTreeNode(p, !current.isVertical, rect);
            if (isLeftBottom) {
                current.leftBottom = newNode;
            } else {
                current.rightTop = newNode;
            }
            size++;
        }

    }        // add the point to the set (if it is not already in the set)

    public boolean contains(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("Null argument");
        }
        KdTreeNode current = root;
        while (current != null) {
            if (current.value.equals(p)) {
                return true;
            } else {
                if (current.isVertical) {
                    if (p.x() < current.value.x()) {
                        current = current.leftBottom;
                    } else current = current.rightTop;
                } else {
                    if (p.y() < current.value.y()) {
                        current = current.leftBottom;
                    } else current = current.rightTop;
                }
            }
        }
        return false;
    }       // does the set contain point p?

    public void draw() {
        draw(root);
    }               // draw all points to standard draw

    private void draw(KdTreeNode node) {
        if (node != null) {
            StdDraw.setXscale(0, 1);
            StdDraw.setYscale(0, 1);
            StdDraw.setPenColor(StdDraw.BLACK);
            node.value.draw();
            StdDraw.show();
            double xmin = 0.0;
            double xmax = 0.0;
            double ymin = 0.0;
            double ymax = 0.0;
            if (node.isVertical) {
                xmin = node.value.x();
                ymin = node.rect.ymin();
                ymax = node.rect.ymax();
                Point2D p = new Point2D(xmin, ymin);
                StdDraw.setPenColor(StdDraw.RED);
                p.drawTo(new Point2D(xmin, ymax));
                StdDraw.show();
            } else {
                xmin = node.rect.xmin();
                xmax = node.rect.xmax();
                ymin = node.value.y();
                Point2D p = new Point2D(xmin, ymin);
                StdDraw.setPenColor(StdDraw.BLUE);
                p.drawTo(new Point2D(xmax, ymin));
                StdDraw.show();
            }
            draw(node.leftBottom);
            draw(node.rightTop);
        }
    }

    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new IllegalArgumentException("Null argument");
        }
        if (this.root == null) {
            return allPts;
        } else {
            allPts.clear();
            return range(root, rect);
        }

    }          // all points that are inside the rectangle (or on the boundary)

    private Iterable<Point2D> range(KdTreeNode current, RectHV rect) {
        if (current.rect.intersects(rect)) {
            if (rect.contains(current.value)) {
                allPts.push(current.value);
            }
            if (current.isVertical) {
                if (rect.xmax() < current.value.x() && current.leftBottom != null) {
                    range(current.leftBottom, rect);
                } else if (rect.xmin() > current.value.x() && current.rightTop != null) {
                    range(current.rightTop, rect);
                } else {
                    if (current.leftBottom != null) {
                        range(current.leftBottom, rect);
                    }
                    if (current.rightTop != null) {
                        range(current.rightTop, rect);
                    }
                }
            } else {
                if (rect.ymax() < current.value.y() && current.leftBottom != null) {
                    range(current.leftBottom, rect);
                } else if (rect.ymin() > current.value.y() && current.rightTop != null) {
                    range(current.rightTop, rect);
                } else {
                    if (current.leftBottom != null) {
                        range(current.leftBottom, rect);
                    }
                    if (current.rightTop != null) {
                        range(current.rightTop, rect);
                    }
                }
            }
        }
        return allPts;
    }

    public Point2D nearest(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("Null argument");
        }
        if (root == null) {
            return null;
        } else {
            minDist = Double.POSITIVE_INFINITY;
            return nearest(root, p);
        }
    }       // a nearest neighbor in the set to point p; null if the set is empty

    private Point2D nearest(KdTreeNode current, Point2D p) {
        if (current == null) {
            return null;
        }
        if (current.value.distanceSquaredTo(p) < minDist) {
            nearestPt = current.value;
            minDist = current.value.distanceSquaredTo(p);
        }
        if (current.isVertical) {
            if (p.x() < current.value.x()) {
                nearest(current.leftBottom, p);
                if (current.rightTop != null && current.rightTop.rect.distanceSquaredTo(p) < minDist) {
                    nearest(current.rightTop, p);
                }
            } else {
                nearest(current.rightTop, p);
                if (current.leftBottom != null && current.leftBottom.rect.distanceSquaredTo(p) < minDist) {
                    nearest(current.leftBottom, p);
                }
            }
        } else {
            if (p.y() < current.value.y()) {
                nearest(current.leftBottom, p);
                if (current.rightTop != null && current.rightTop.rect.distanceSquaredTo(p) < minDist) {
                    nearest(current.rightTop, p);
                }
            } else {
                nearest(current.rightTop, p);
                if (current.leftBottom != null && current.leftBottom.rect.distanceSquaredTo(p) < minDist) {
                    nearest(current.leftBottom, p);
                }

            }

        }

        return nearestPt;
    }

    public static void main(String[] args) {
        String filename = args[0];
        In in = new In(filename);
        KdTree kdtree = new KdTree();

        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            kdtree.insert(p);
        }

        // draw the points
        StdDraw.clear();
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        kdtree.draw();
        StdDraw.show();
    }              // unit testing of the methods (optional)
}
