package INF1771_GameAI;

import java.util.ArrayList;

import INF1771_GameAI.Map.Maze;
import INF1771_GameAI.Map.Position;
import INF1771_GameAI.Map.Tile;

public class PathFinder {

	private static PathFinder pf = null;
	private ArrayList<Node> energyPath = null;	
	public Position previous = null;	
	
	private PathFinder(){
		energyPath = new ArrayList<Node>();		
	}
	
	public static PathFinder getInstance(){
		if(pf == null)
			pf = new PathFinder();
		
		return pf;
	}
	
	public boolean findEnergyPath(Position p){
		
		Maze maze = Maze.getInstance();
		Tile[][] map = maze.getMap();
		ArrayList <Node> tiles = new ArrayList<Node>();
		Node start = new Node(p.x,p.y);
		Position dest = maze.closestPwrUp(p);
		Node end = new Node(dest.x,dest.y);
		AStar astar;
				
		tiles.add(start);
		
		for(int i = 0;i < 34;i++){
			for(int j = 0;j < 59;j++){
				if(map[i][j].getSupposition().size() == 0){
					String s = map[i][j].getStatus();
					if(!s.equals("hole") && !s.equals("vortex")){
						Node n = new Node(map[i][j].getX(),map[i][j].getY());
						tiles.add(n);
					}
				}
			}
		}
		
		if(tiles.size() < 2)
			return false;
		
		astar = new AStar(tiles,start,end);
		
		boolean res = astar.search();
		
		if(res == false)
			return false;
		
		astar.getPath().remove(0);	// Tirar o inicial		
		
		energyPath = astar.getPath();	
		return true;
	}
	
	public String walkDecision(Position p,String dir){
						
		Position aux = null;		
			
		try{
			aux = sameDirectionNotVisited(p,dir);		
			if(aux != null){				
				return dir;
			}

			aux = otherDirectionNotVisited(p,dir);		
			if(aux != null){				
				return findDirection(p,aux);
			}

			aux = sameDirectionVisited(p,dir);		
			if(aux != null){				
				return dir;
			}

			aux = otherDirectionVisitedNotPrevious(p,dir);		
			if(aux != null){				
				return findDirection(p,aux);
			}		

			aux = otherDirectionVisited(p,dir);		
			if(aux != null){				
				return findDirection(p,aux);
			}	
		}
		catch(Exception e){
			if(dir.equals("north"))
				return "south";
			else
				if(dir.equals("south"))
					return "north";
				else
					if(dir.equals("east"))
						return "west";
					else
						if(dir.equals("west"))
							return "east";
		}
		
		return null;
	}
	
	public boolean escapeRoute(Position adj,Position dest){
		
		Maze maze = Maze.getInstance();
		Tile[][] map = maze.getMap();
		
		if(adj.x < 0 || adj.x > 58 || adj.y < 0 || adj.y > 33)
			return false;
		
		if(dest.x < 0 || dest.x > 58 || dest.y < 0 || dest.y > 33)
			return false;
			
		
		if(map[adj.y][adj.x].isVisited() && map[dest.y][dest.x].isVisited())
			return true;
		else
			if(!map[adj.y][adj.x].isBlocked() && !map[dest.y][dest.x].isBlocked()){
				if(map[adj.y][adj.x].getSupposition().size() == 0
					&& map[dest.y][dest.x].getSupposition().size() == 0)
					return true;
			}
		
		return false;
	}
	
	private String findDirection(Position p,Position next){
		
		int resX = p.x - next.x;
		int resY = p.y - next.y;
		
		if(resX == 0){
			if(resY > 0)
				return "north";
			else
				return "south";
		}
		else
			if(resY == 0){
				if(resX > 0)
					return "west";
				else
					return "east";
			}
		
		return "";
	}
	
	private Position sameDirectionNotVisited(Position p,String dir){
		
		Position ret = new Position();
		Maze maze = Maze.getInstance();
		Tile[][] map = maze.getMap();
		int coord;
		
		if(dir.equalsIgnoreCase("north")){
			coord = p.y - 1;
			if(coord > -1 && !map[coord][p.x].isVisited()){
				if(!map[coord][p.x].isBlocked()){					
					if(map[coord][p.x].getSupposition().size() == 0 ||
							map[coord][p.x].isVerified()){
						
						ret.x = p.x;
						ret.y = coord;						
						return ret;
					}
				}				
			}
		} else if(dir.equalsIgnoreCase("south")){
			coord = p.y + 1;
			if(coord < 34 && !map[coord][p.x].isVisited()){
				if(!map[coord][p.x].isBlocked()){
					if(map[coord][p.x].getSupposition().size() == 0 ||
							map[coord][p.x].isVerified()){
						
						ret.x = p.x;
						ret.y = coord;
						return ret;
					}
				}
			}
		} else if(dir.equalsIgnoreCase("east")){
			coord = p.x + 1;
			if(coord < 59 && !map[p.y][coord].isVisited()){
				if(!map[p.y][coord].isBlocked()){
					if(map[p.y][coord].getSupposition().size() == 0 ||
							map[p.y][coord].isVerified()){
						
						ret.x = coord;
						ret.y = p.y;						
						return ret;
					}
				}				
			}
		} else if(dir.equalsIgnoreCase("west")){
			coord = p.x - 1;
			if(coord > -1 && !map[p.y][coord].isVisited()){
				if(!map[p.y][coord].isBlocked()){
					if(map[p.y][coord].getSupposition().size() == 0 ||
							map[p.y][coord].isVerified()){
						
						ret.x = coord;
						ret.y = p.y;
						return ret;
					}
				}				
			}
		}		
		
		return null;
	}
	
	private Position otherDirectionNotVisited(Position p,String dir){
		
		Position ret = new Position();
		Maze maze = Maze.getInstance();
		Tile[][] map = maze.getMap();
		int coord;
		
		if(dir.equalsIgnoreCase("north")){
			// Go to east
			coord = p.x + 1;
			if(coord < 58 && !map[p.y][coord].isVisited()){
				if(!map[p.y][coord].isBlocked()){
					if(map[p.y][coord].getSupposition().size() == 0 ||
							map[p.y][coord].isVerified()){
						
						ret.x = coord;
						ret.y = p.y;
						return ret;
					}
				}				
			}
			
			// Go to west
			coord = p.x - 1;
			if(coord > 0 && !map[p.y][coord].isVisited()){
				if(!map[p.y][coord].isBlocked()){
					if(map[p.y][coord].getSupposition().size() == 0 ||
							map[p.y][coord].isVerified()){
						
						ret.x = coord;
						ret.y = p.y;
						return ret;
					}
				}			
			}
			
			// Go to south
			coord = p.y + 1;
			if(coord < 33 && !map[coord][p.x].isVisited()){
				if(!map[coord][p.x].isBlocked()){
					if(map[coord][p.x].getSupposition().size() == 0 ||
							map[coord][p.x].isVerified()){
						
						ret.x = p.x;
						ret.y = coord;
						return ret;
					}
				}
			}			
			
		} else if(dir.equalsIgnoreCase("south")){
			// Go to west
			coord = p.x - 1;
			if(coord > 0 && !map[p.y][coord].isVisited()){
				if(!map[p.y][coord].isBlocked()){
					if(map[p.y][coord].getSupposition().size() == 0 ||
							map[p.y][coord].isVerified()){

						ret.x = coord;
						ret.y = p.y;
						return ret;
					}
				}		
			}

			// Go to east
			coord = p.x + 1;
			if(coord < 58 && !map[p.y][coord].isVisited()){
				if(!map[p.y][coord].isBlocked()){
					if(map[p.y][coord].getSupposition().size() == 0 ||
							map[p.y][coord].isVerified()){

						ret.x = coord;
						ret.y = p.y;
						return ret;
					}
				}			
			}			

			// Go to north
			coord = p.y - 1;
			if(coord > 0 && !map[coord][p.x].isVisited()){
				if(!map[coord][p.x].isBlocked()){
					if(map[coord][p.x].getSupposition().size() == 0 ||
							map[coord][p.x].isVerified()){
						
						ret.x = p.x;
						ret.y = coord;
						return ret;
					}
				}				
			}
			
		} else if(dir.equalsIgnoreCase("east")){
			// Go to south
			coord = p.y + 1;
			if(coord < 33 && !map[coord][p.x].isVisited()){
				if(!map[coord][p.x].isBlocked()){
					if(map[coord][p.x].getSupposition().size() == 0 ||
							map[coord][p.x].isVerified()){

						ret.x = p.x;
						ret.y = coord;
						return ret;
					}
				}
			}		

			// Go to north
			coord = p.y - 1;
			if(coord > 0 && !map[coord][p.x].isVisited()){
				if(!map[coord][p.x].isBlocked()){
					if(map[coord][p.x].getSupposition().size() == 0 ||
							map[coord][p.x].isVerified()){

						ret.x = p.x;
						ret.y = coord;
						return ret;
					}
				}				
			}			

			// Go to west
			coord = p.x - 1;
			if(coord > 0 && !map[p.y][coord].isVisited()){
				if(!map[p.y][coord].isBlocked()){
					if(map[p.y][coord].getSupposition().size() == 0 ||
							map[p.y][coord].isVerified()){
						
						ret.x = coord;
						ret.y = p.y;
						return ret;
					}
				}		
			}			
						
		} else if(dir.equalsIgnoreCase("west")){
			// Go to south
			coord = p.y + 1;
			if(coord < 33 && !map[coord][p.x].isVisited()){
				if(!map[coord][p.x].isBlocked()){
					if(map[coord][p.x].getSupposition().size() == 0 ||
							map[coord][p.x].isVerified()){

						ret.x = p.x;
						ret.y = coord;
						return ret;
					}
				}
			}		


			// Go to north
			coord = p.y - 1;
			if(coord > 0 && !map[coord][p.x].isVisited()){
				if(!map[coord][p.x].isBlocked()){
					if(map[coord][p.x].getSupposition().size() == 0 ||
							map[coord][p.x].isVerified()){

						ret.x = p.x;
						ret.y = coord;
						return ret;
					}
				}			
			}			

			// Go to east
			coord = p.x + 1;
			if(coord < 58 && !map[p.y][coord].isVisited()){
				if(!map[p.y][coord].isBlocked()){
					if(map[p.y][coord].getSupposition().size() == 0 ||
							map[p.y][coord].isVerified()){
						
						ret.x = coord;
						ret.y = p.y;
						return ret;
					}
				}			
			}	
			
		}		
		
		return null;
	}
	
	private Position sameDirectionVisited(Position p,String dir){
		
		Position ret = new Position();
		Maze maze = Maze.getInstance();
		Tile[][] map = maze.getMap();
		int coord;
		
		if(dir.equalsIgnoreCase("north")){
			coord = p.y - 1;
			if(coord > -1 && map[coord][p.x].isVisited()){
				ret.x = p.x;
				ret.y = coord;
				return ret;
			}
		} else if(dir.equalsIgnoreCase("south")){
			coord = p.y + 1;
			if(coord < 34 && map[coord][p.x].isVisited()){
				ret.x = p.x;
				ret.y = coord;
				return ret;
			}
		} else if(dir.equalsIgnoreCase("east")){
			coord = p.x + 1;
			if(coord < 59 && map[p.y][coord].isVisited()){
				ret.x = coord;
				ret.y = p.y;
				return ret;
			}
		} else if(dir.equalsIgnoreCase("west")){
			coord = p.x - 1;
			if(coord > -1 && map[p.y][coord].isVisited()){
				ret.x = coord;
				ret.y = p.y;
				return ret;
			}
		}		
		
		return null;
	}
	
	private Position otherDirectionVisitedNotPrevious(Position p,String dir){
		
		Position ret = new Position();
		Maze maze = Maze.getInstance();
		Tile[][] map = maze.getMap();
		int coord;
		Tile prev = new Tile();
		
		prev.setX(previous.x);
		prev.setY(previous.y);
		
		if(dir.equalsIgnoreCase("north")){
			// Go to west
			coord = p.x - 1;
			if(coord > 0 && map[p.y][coord].isVisited() && (map[p.y][coord].getX() != prev.getX() || map[p.y][coord].getY() != prev.getY())){
				ret.x = coord;
				ret.y = p.y;
				return ret;
			}

			// Go to east
			coord = p.x + 1;
			if(coord < 58 && map[p.y][coord].isVisited() && (map[p.y][coord].getX() != prev.getX() || map[p.y][coord].getY() != prev.getY())){
				ret.x = coord;
				ret.y = p.y;
				return ret;
			}
						
			// Go to south
			coord = p.y + 1;
			if(coord < 33 && map[coord][p.x].isVisited() && (map[p.y][coord].getX() != prev.getX() || map[p.y][coord].getY() != prev.getY())){
				ret.x = p.x;
				ret.y = coord;
				return ret;
			}			
			
		} else if(dir.equalsIgnoreCase("south")){
			// Go to east
			coord = p.x + 1;
			if(coord < 58 && map[p.y][coord].isVisited() && (map[p.y][coord].getX() != prev.getX() || map[p.y][coord].getY() != prev.getY())){
				ret.x = coord;
				ret.y = p.y;
				return ret;
			}

			// Go to west
			coord = p.x - 1;
			if(coord > 0 && !map[p.y][coord].isVisited() && (map[p.y][coord].getX() != prev.getX() || map[p.y][coord].getY() != prev.getY())){
				ret.x = coord;
				ret.y = p.y;
				return ret;
			}

			// Go to north
			coord = p.y - 1;
			if(coord > 0 && map[coord][p.x].isVisited() && (map[p.y][coord].getX() != prev.getX() || map[p.y][coord].getY() != prev.getY())){
				ret.x = p.x;
				ret.y = coord;
				return ret;
			}
			
		} else if(dir.equalsIgnoreCase("east")){
			// Go to north
			coord = p.y - 1;
			if(coord > 0 && map[coord][p.x].isVisited() && (map[p.y][coord].getX() != prev.getX() || map[p.y][coord].getY() != prev.getY())){
				ret.x = p.x;
				ret.y = coord;
				return ret;
			}

			// Go to south
			coord = p.y + 1;
			if(coord < 33 && map[coord][p.x].isVisited() && (map[p.y][coord].getX() != prev.getX() || map[p.y][coord].getY() != prev.getY())){
				ret.x = p.x;
				ret.y = coord;
				return ret;
			}		

			// Go to west
			coord = p.x - 1;
			if(coord > 0 && map[p.y][coord].isVisited() && (map[p.y][coord].getX() != prev.getX() || map[p.y][coord].getY() != prev.getY())){
				ret.x = coord;
				ret.y = p.y;
				return ret;
			}			
						
		} else if(dir.equalsIgnoreCase("west")){
			// Go to south
			coord = p.y + 1;
			if(coord < 33 && map[coord][p.x].isVisited() && (map[p.y][coord].getX() != prev.getX() || map[p.y][coord].getY() != prev.getY())){
				ret.x = p.x;
				ret.y = coord;
				return ret;
			}		

			// Go to north
			coord = p.y - 1;
			if(coord > 0 && map[coord][p.x].isVisited() && (map[p.y][coord].getX() != prev.getX() || map[p.y][coord].getY() != prev.getY())){
				ret.x = p.x;
				ret.y = coord;
				return ret;
			}

			// Go to east
			coord = p.x + 1;
			if(coord < 58 && map[p.y][coord].isVisited() && (map[p.y][coord].getX() != prev.getX() || map[p.y][coord].getY() != prev.getY())){
				ret.x = coord;
				ret.y = p.y;
				return ret;
			}
			
		}		
		
		return null;
	}
	
	private Position otherDirectionVisited(Position p,String dir){
		
		Position ret = new Position();
		Maze maze = Maze.getInstance();
		Tile[][] map = maze.getMap();
		int coord;
		Tile prev = new Tile();
		
		prev.setX(previous.x);
		prev.setY(previous.y);
		
		if(dir.equalsIgnoreCase("north")){
			// Go to east
			coord = p.x + 1;
			if(coord < 58 && map[p.y][coord].isVisited()){
				ret.x = coord;
				ret.y = p.y;
				return ret;
			}
			
			// Go to south
			coord = p.y + 1;
			if(coord < 33 && map[coord][p.x].isVisited()){
				ret.x = p.x;
				ret.y = coord;
				return ret;
			}			

			// Go to west
			coord = p.x - 1;
			if(coord > 0 && map[p.y][coord].isVisited()){
				ret.x = coord;
				ret.y = p.y;
				return ret;
			}		
			
			
		} else if(dir.equalsIgnoreCase("south")){
			// Go to east
			coord = p.x + 1;
			if(coord < 58 && map[p.y][coord].isVisited()){
				ret.x = coord;
				ret.y = p.y;
				return ret;
			}

			// Go to west
			coord = p.x - 1;
			if(coord > 0 && !map[p.y][coord].isVisited()){
				ret.x = coord;
				ret.y = p.y;
				return ret;
			}

			// Go to north
			coord = p.y - 1;
			if(coord > 0 && map[coord][p.x].isVisited()){
				ret.x = p.x;
				ret.y = coord;
				return ret;
			}
			
		} else if(dir.equalsIgnoreCase("east")){
			// Go to south
			coord = p.y + 1;
			if(coord < 33 && map[coord][p.x].isVisited()){
				ret.x = p.x;
				ret.y = coord;
				return ret;
			}		

			// Go to north
			coord = p.y - 1;
			if(coord > 0 && map[coord][p.x].isVisited()){
				ret.x = p.x;
				ret.y = coord;
				return ret;
			}			

			// Go to west
			coord = p.x - 1;
			if(coord > 0 && map[p.y][coord].isVisited()){
				ret.x = coord;
				ret.y = p.y;
				return ret;
			}			
						
		} else if(dir.equalsIgnoreCase("west")){
			// Go to north
			coord = p.y - 1;
			if(coord > 0 && map[coord][p.x].isVisited()){
				ret.x = p.x;
				ret.y = coord;
				return ret;
			}
			
			// Go to east
			coord = p.x + 1;
			if(coord < 58 && map[p.y][coord].isVisited()){
				ret.x = coord;
				ret.y = p.y;
				return ret;
			}

			// Go to south
			coord = p.y + 1;
			if(coord < 33 && map[coord][p.x].isVisited()){
				ret.x = p.x;
				ret.y = coord;
				return ret;
			}			
			
		}		
		
		return null;
	}
	
	public ArrayList<Node> getEnergyPath() {
		return energyPath;
	}
	
	public void resetEnergyPath(){
		energyPath.clear();		
	}	
	
}
