package INF1771_GameAI;

import java.util.ArrayList;
import java.util.Collections;

public class AStar {

	ArrayList<Node> open;
	ArrayList<Node> close;
	ArrayList<Node> path;
	ArrayList<Node> provided;
	Node start;
	Node end;
	
	public AStar(ArrayList<Node> provided,Node start,Node end){
		
		open = new ArrayList<Node>();
		close = new ArrayList<Node>();
		path = new ArrayList<Node>();
		this.provided = provided;
		this.start = start;
		this.end = end;
	}
	
	public boolean search(){
		
		open.add(start);
		Node current;
		ArrayList<Node> neighbours = null;
		
		while(true){
			
			Collections.sort(open);
			current = open.get(0);
			
			close.add(current);
			open.remove(current);
			
			if(current.getX() == end.getX() && current.getY() == end.getY()){
				savePath();
				return true;
			}
			
			neighbours = availableNeighbours(current);
			
			if(neighbours != null){
				for(int i = 0;i < neighbours.size();i++){
					Node neighbour = neighbours.get(i);
					if(!close.contains(neighbour)){
						int gCost = current.getG() + 1;
						int hCost = manhattan(neighbour);
						
						if(!open.contains(neighbour)){
							neighbour.setParent(current);
							neighbour.setG(gCost);
							neighbour.setH(hCost);
							open.add(neighbour);
						}
						else
							if(neighbour.getG() > gCost){
								neighbour.setParent(current);
								neighbour.setG(gCost);
								neighbour.setH(hCost);
							}
					}
				}
				neighbours.clear();
				neighbours = null;
			}
			
			if(open.isEmpty())
			{				
				return false;
			}				
		}
		
	}
	
	public ArrayList<Node> availableNeighbours(Node n){
		
		int x = n.getX();
		int y = n.getY();
		int right = x + 1;
		int left = x - 1;
		int up = y + 1;
		int down = y - 1;
		ArrayList<Node> neighbours = new ArrayList<Node>();
		
		for(int i = 0;i < provided.size();i++){
			int xP = provided.get(i).getX();
			int yP = provided.get(i).getY();
			
			if(xP == right && yP == y)
				neighbours.add(provided.get(i));
			else
				if(xP == left && yP == y)
					neighbours.add(provided.get(i));
				else
					if(xP == x && yP == up)
						neighbours.add(provided.get(i));
					else
						if(xP == x && yP == down)
							neighbours.add(provided.get(i));
		}
		
		if(neighbours.size() > 0){			
			return neighbours;
		}			
		
		return null;
	}
	
	public int manhattan(Node n){
		
		int xi = n.getX();
    	int yi = n.getY();
    	int xf = end.getX();
    	int yf = end.getY();
    	
    	return Math.abs(xi - xf) + Math.abs(yi - yf);
	}
	
	public boolean savePath(){
		
		Node current = this.end;
		
		while(current != null){
			path.add(current);			
			current = current.getParent();				
		}
		
		if(path.size() > 0){
			Collections.reverse(path);
			return true;
		}			
		
		return false;
	}
	
	public ArrayList<Node> getPath() {
		return path;
	}
}
