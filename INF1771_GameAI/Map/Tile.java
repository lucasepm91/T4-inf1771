package INF1771_GameAI.Map;

import java.util.ArrayList;

public class Tile implements Comparable<Tile>{

	private int x;
	private int y;
	private boolean visited;
	private boolean verified;
	private int count = 0; // Número de vezes que foi visitada
	private String statusTile; // Vazia,bloqueada,etc
	private ArrayList<String> supposition = new ArrayList<String>();	
	private int reward = 0; // Valor acumulado de recompensa pelo movimento
	
	public Tile(){
		this.statusTile = "";		
		this.visited = false;
		this.verified = false;
	}
	
	public int getCount() {
		return count;
	}
	
	public String getStatus() {
		return statusTile;
	}
	
	public int getReward() {
		return reward;
	}
	
	public void incrementCount() {
		this.count++;
	}
	
	public void setStatus(String status) {
		this.statusTile = status;
	}
	
	public void updateReward(int value) {
		this.reward += value;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	public boolean isVisited() {
		return visited;
	}

	public void setVisited() {
		this.visited = true;
	}

	public boolean isVerified() {
		return verified;
	}
	
	public boolean isBlocked(){
		if(statusTile != null && statusTile.equalsIgnoreCase("blocked"))
			return true;
		
		return false;
	}

	public void setVerified() {
		this.verified = true;
	}

	public ArrayList<String> getSupposition() {
		return supposition;
	}
	
	public void addSupposition(String s) {
			
		if(supposition.contains(s.toLowerCase())){
			//removeSupposition(s);
			if(s.equalsIgnoreCase("breeze"))
				this.statusTile = "hole";
			else
				if(s.equalsIgnoreCase("flash"))
					this.statusTile = "vortex";
				else
					if(s.equalsIgnoreCase("steps"))
						this.statusTile = "enemy";					
		}
		else{
			if(!visited && !verified)
				supposition.add(s);			
		}
	}
	
	public void removeSupposition(String s) {
		
		int size = supposition.size();
		
		for(int i = 0; i < size;i++){
			if(supposition.get(i).equalsIgnoreCase(s)){
				supposition.remove(i);
				size--;
				i--;
			}			
		}
		
	}
	
	public void correctWrongStatus() {
		
		if(supposition.size() > 0){
			if(this.visited == true)
				this.statusTile = null;
			else{
				this.statusTile = null;				
				supposition.clear();
				setVerified();
			}			
		}
		else
			setVerified();		
	}
	
	public void blockedStatus(){
		this.verified = false;
		this.statusTile = "blocked";
	}

	@Override
	public int compareTo(Tile t) {
		
		String stts1 = this.statusTile;
		String stts2 = t.getStatus();
		
		if(stts1 == null && stts2 != null)
			return 1;
		else
			if(stts1 != null && stts2 == null)
				return -1;
		
		return Integer.compare(this.count,t.getCount());
	}
}
