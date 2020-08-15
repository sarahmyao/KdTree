import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdDraw;

import java.util.Stack;

public class PointSET {
    private SET<Point2D> pts;

    public PointSET() {
        pts = new SET<Point2D>();
    }                              // construct an empty set of points

    public boolean isEmpty() {
        return pts.isEmpty();
    }                     // is the set empty?

    public int size() {
        return pts.size();
    }                 // number of points in the set

    public void insert(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("Null argument");
        }
        if (!pts.contains(p))
            pts.add(p);
    }         // add the point to the set (if it is not already in the set)

    public boolean contains(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("Null argument");
        }
        return pts.contains(p);
    }         // does the set contain point p?

    public void draw() {
        StdDraw.setXscale(0, 1);
        StdDraw.setYscale(0, 1);
        for (Point2D p : pts) {
            p.draw();
        }
    }     // draw all points to standard draw

    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new IllegalArgumentException("Null argument");
        }
        Stack<Point2D> pointStack = new Stack<Point2D>();
        if (pts.isEmpty()) {
            return pointStack;
        }
        for (Point2D p : pts) {
            if (rect.contains(p)) {
                pointStack.push(p);
            }
        }
        return pointStack;

    }           // all points that are inside the rectangle (or on the boundary)

    public Point2D nearest(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("Null argument");
        }
        double minDist = Double.POSITIVE_INFINITY;
        Point2D min = null;
        for (Point2D thisPt : pts) {
            if (thisPt.distanceSquaredTo(p) < minDist) {
                min = thisPt;
                minDist = thisPt.distanceSquaredTo(p);
            }
        }
        return min;

    }         // a nearest neighbor in the set to point p; null if the set is empty

    public static void main(String[] args) {
        PointSET myPts = new PointSET();
        Point2D p1 = new Point2D(1.0, 1.0);
        Point2D p2 = new Point2D(2.0, 2.0);
        Point2D p3 = new Point2D(3.0, 3.0);
        Point2D p4 = new Point2D(4.0, 4.0);
        Point2D p5 = new Point2D(4.0, 5.0);
        System.out.println(myPts.range(new RectHV(0, 0, 1, 1)));
        myPts.insert(p1);
        myPts.insert(p2);
        myPts.insert(p3);
        myPts.insert(p4);
        System.out.println("Is it empty: " + myPts.isEmpty());
        System.out.println("Size: " + myPts.size());
        System.out.println("Does it contain point p2: " + myPts.contains(p2));
        myPts.draw();
        RectHV myRect = new RectHV(0.0, 0.0, 5.0, 5.0);
        System.out.println("Which points are in the rectangle: " + myPts.range(myRect));
        System.out.println("Which point is closest to p5: " + myPts.nearest(p5));
    }               // unit testing of the methods (optional)
}
