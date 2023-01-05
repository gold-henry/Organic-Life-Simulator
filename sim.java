import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import com.jwetherell.algorithms.data_structures.QuadTree;
import com.jwetherell.algorithms.data_structures.QuadTree.PointRegionQuadTree;
import com.jwetherell.algorithms.data_structures.QuadTree.XYPoint;

public class sim {
	
	static int SCREEN_WIDTH = 1200;
	static int SCREEN_HEIGHT = 800;
	
	private static DrawingPanel DP = new DrawingPanel(SCREEN_WIDTH, SCREEN_HEIGHT);
	private static Graphics G = DP.getGraphics();
	
	public static PointRegionQuadTree<XYPoint> white = create(Color.white, 300);
    public static PointRegionQuadTree<XYPoint> red = create(Color.red, 1200);
    public static PointRegionQuadTree<XYPoint> green = create(Color.green, 1200);
    public static PointRegionQuadTree<XYPoint> yellow = create(Color.yellow, 1200);
	
	public static int forceBorder = 30;
	public static int forceBorderStrength = 1;
	
	public static void main(String[] args) {

		try {
			runSim();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private static PointRegionQuadTree<XYPoint> create(Color c, int n) {
		PointRegionQuadTree<XYPoint> l = new PointRegionQuadTree<XYPoint>(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
		genRandomParticles(l, n, c);
		return l;
	}
	
	private static void runSim() throws InterruptedException {
		boolean running = true;
		while (running) {
			update();
		}
	}
	
	private static void update() throws InterruptedException {
			Thread.sleep(100);
			
			
			rule(white, white, -0.1, 20, 100);
			rule(white, white, 3, 0, 20);
			
 			rule(white, white, -0.1, 40, 120);
			
			rule(red, white, -0.4, 40, 80);
			rule(red, white, 0.5, 0, 30);
			
			rule(red, red, 0.1, 0, 20);
			
			rule(green, green, 0.1, 0, 20);
			
			rule(green, white, -0.4, 40, 80);
			rule(green, white, 0.5, 0, 30);
			
			rule(red, green, 0.1, 0, 10);
			rule(green, red, 0.1, 0, 10);
			
			rule(white, green, 0.1, 0, 45);
			
			rule(white, yellow, 0.6, 0, 28);
			rule(yellow, red, 0.6, 0, 20);
			rule(red, yellow, -0.6, 0, 60);
			
			rule(yellow, yellow, -0.2, 0, 40);
			
			rule(white, yellow, -0.2, 0, 50);
			rule(yellow, white, -0.2, 0, 50);
			
			rule(green, yellow, 0.3, 0, 50);
			
			/*rule(red, white, -0.3, 0, 100);
			rule(white, red, 0.3, 0, 100);
			
			rule(green, red, -0.4, 0, 120);
			rule(red, green, 0.4, 0, 120);*/
		
			G.clearRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
			drawParticles(white);
			
			drawParticles(red);
			drawParticles(green);
			drawParticles(yellow);
	}
	
	private static void drawParticles(PointRegionQuadTree<XYPoint> t) {
		Collection<XYPoint> l = t.queryRange(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
		for (XYPoint p : l) {
			p.draw(G);	
		}
	}
	
	private static void rule(PointRegionQuadTree<XYPoint> particles1Tree, PointRegionQuadTree<XYPoint> particles2Tree, double f, double innerRange, double outerRange) {

		Collection<XYPoint> particles1 = particles1Tree.queryRange(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
		Collection<XYPoint> particles2 = particles2Tree.queryRange(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
		
		// for all in first collection
		for(XYPoint a : particles1){
			double fx = 0;
			double fy = 0;
			
			// for all in second collection within range
			Collection<XYPoint> particles2Range = particles2Tree.queryRange(a.getX() - outerRange, a.getY() - outerRange, outerRange*2, outerRange*2);
			for(XYPoint b : particles2Range) {
				
				// calculate distance
				double dx = a.getX()-b.getX();
				double dy = a.getY()-b.getY();
				double d = Math.sqrt(dx*dx + dy*dy);
				
				// check distance
				if(d > innerRange && d < outerRange){
					double F = f * 1/d;
					fx += (F * dx);
					fy += (F * dy);
				}
			}
			a.vx = (a.vx + fx) * 0.6;
			a.vy = (a.vy + fy) * 0.6;
			
			a.set( a.getX() + a.vx,  a.getY() + a.vy);
			
			// bounds
			if(a.getX() <= 0 ) {
				a.set( 0, a.getY());
			}
			if(a.getX() >= SCREEN_WIDTH) {
				a.set( SCREEN_WIDTH, a.getY());
			}
			if(a.getY() <= 0 ) {
				a.set( a.getX(), 0);
			}
			if(a.getY() >= SCREEN_HEIGHT) {
				a.set( a.getX(), SCREEN_HEIGHT);
			}
			
			if(a.getX() - forceBorder <= 0 || a.getX() + forceBorder >= SCREEN_WIDTH){
				a.vx *=-forceBorderStrength;
			}
			if(a.getY() - forceBorder <= 0 || a.getY() + forceBorder >= SCREEN_HEIGHT){
				a.vy *=-forceBorderStrength; 
			}
		}
	}
	
	private static void genRandomParticles(PointRegionQuadTree<XYPoint> l, int n, Color c) {
		Random R = new Random();
		for (int i = 0; i < n; i++) {
			l.insert(R.nextDouble() * SCREEN_WIDTH, R.nextDouble() * SCREEN_HEIGHT, c);
		}
	}
}
