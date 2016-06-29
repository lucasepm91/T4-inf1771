package INF1771_GameAI;

public class Node implements Comparable<Node>{

	private int x;
	private int y;
	private int g;
	private int h;	
	private Node parent;
	
	public Node(int x,int y){
		
		this.x = x;
		this.y = y;
		g = 0;
		h = 0;
		parent = null;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getG() {
		return g;
	}
	
	public int getH() {
		return h;
	}
	
	public void setG(int g) {
		this.g = g;
	}
	
	public void setH(int h) {
		this.h = h;
	}
	
	public Node getParent() {
		return parent;
	}
	
	public void setParent(Node parent) {
		this.parent = parent;
	}
	
	public int getF(){
		return this.g + this.h;
	}

	@Override
	public int compareTo(Node n) {		
		return Integer.compare(this.getF(),n.getF());
	}
	
}
