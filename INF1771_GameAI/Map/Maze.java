package INF1771_GameAI.Map;

import java.util.ArrayList;
import java.util.Collections;

public class Maze {

	private static Maze maze = null;
	private Tile[][] map;
	private ArrayList<Tile> bestMove;
	private ArrayList<Tile> possiblePowerup;
	
	private Maze(){
		map = new Tile[34][59];
		bestMove = new ArrayList<Tile>();
		possiblePowerup = new ArrayList<Tile>();
		addTileToBest();		
	}
	
	public static Maze getInstance(){
		if(maze == null)
			maze = new Maze();
		
		return maze;
	}
	
	public Tile[][] getMap() {
		return map;
	}
	
	public ArrayList<Tile> getPossiblePowerup() {
		return possiblePowerup;
	}
	
	public void addPowerupTile(Tile t){
		
		if(!possiblePowerup.contains(t))
			possiblePowerup.add(t);
	}

	private void addTileToBest() {
		for(int i = 0;i < 34;i++)
			for(int j = 0;j < 59;j++){
				map[i][j] = new Tile();
				map[i][j].setX(j);
				map[i][j].setY(i);
				bestMove.add(map[i][j]);				
			}			
	}
	
	public void removeTiletoBest(){
		
		int size = bestMove.size();
		
		for(int i = 0;i < size;i++){
			if(bestMove.get(i).getStatus().equalsIgnoreCase("blocked")){
				bestMove.remove(i);
				i--;
				size--;
			}				
		}
	}
	
	public Tile findBestTile(Position p){
		
		Collections.sort(bestMove);
		Tile best = bestMove.get(0);
		
		if(best.getX() != p.x || best.getY() != p.y )
			return best;
		
		return bestMove.get(1);
	}	
	
	public Position closestPwrUp(Position p){
		Position pwr = new Position();
		int close = 0;	// indice do menor na lista
		int dist = 9999;	// menor distancia		
		int ret = 0;	// guarda retorno da heuristica
				
		for(int i = 0;i < possiblePowerup.size();i++){
			ret = manhattanPwr(p,possiblePowerup.get(i));
			if(ret < dist && !possiblePowerup.get(i).getStatus().equals("nothing")){
				close = i;
				dist = ret;
			}			
		}
		
		if(dist != 9999){
			pwr.x = possiblePowerup.get(close).getX();
			pwr.y = possiblePowerup.get(close).getY();
		}
		else{
			if(possiblePowerup.size() > 0){
				for(int i = 0;i < possiblePowerup.size();i++){
					if(possiblePowerup.get(i).getX() != p.x &&
							possiblePowerup.get(i).getY() != p.y){

						pwr.x = possiblePowerup.get(i).getX();
						pwr.y = possiblePowerup.get(i).getY();
					}				
				}
			}
		}				
		
		return pwr;
	}
	
	private int manhattanPwr(Position p,Tile t){
		return Math.abs(p.x - t.getX()) + Math.abs(p.y - t.getY());
	}
	
}
