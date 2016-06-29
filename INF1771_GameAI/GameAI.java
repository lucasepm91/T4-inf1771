package INF1771_GameAI;
import INF1771_GameAI.Map.*;
import java.util.ArrayList;
import java.util.List;


public class GameAI
{
    Position player = new Position();
    String state = "ready";
    String dir = "north";
    long score = 0;
    int energy = 0;
    String action = "walk";
    int dist = -1;
    int huntCount = 0;    
    ArrayList<String> actionList = new ArrayList<String>();
    
    /**
     * Refresh player status
     * @param x			player position x
     * @param y			player position y
     * @param dir		player direction
     * @param state		player state
     * @param score		player score
     * @param energy	player energy
     */
    public void SetStatus(int x, int y, String dir, String state, long score, int energy)
    {
        player.x = x;
        player.y = y;
        this.dir = dir.toLowerCase();

        this.state = state;
        this.score = score;
        this.energy = energy;
    }

    /**
     * Get list of observable adjacent positions
     * @return List of observable adjacent positions 
     */
    public List<Position> GetObservableAdjacentPositions()
    {
        List<Position> ret = new ArrayList<Position>();

        if(player.x > 0)
        	ret.add(new Position(player.x - 1, player.y));
        if(player.x < 58)
        	ret.add(new Position(player.x + 1, player.y));
        if(player.y > 0)
        	ret.add(new Position(player.x, player.y - 1));
        if(player.y < 33)
        	ret.add(new Position(player.x, player.y + 1));

        return ret;
    }

    /**
     * Get list of all adjacent positions (including diagonal)
     * @return List of all adjacent positions (including diagonal)
     */
    public List<Position> GetAllAdjacentPositions()
    {
        List<Position> ret = new ArrayList<Position>();

        ret.add(new Position(player.x - 1, player.y - 1));
        ret.add(new Position(player.x, player.y - 1));
        ret.add(new Position(player.x + 1, player.y - 1));

        ret.add(new Position(player.x - 1, player.y));
        ret.add(new Position(player.x + 1, player.y));

        ret.add(new Position(player.x - 1, player.y + 1));
        ret.add(new Position(player.x, player.y + 1));
        ret.add(new Position(player.x + 1, player.y + 1));

        return ret;
    }

    /**
     * Get next forward position
     * @return next forward position
     */
    public Position NextPosition()
    {
        Position ret = null;
        if(dir.equals("north"))
                ret = new Position(player.x, player.y - 1);
        else if(dir.equals("east"))
                ret = new Position(player.x + 1, player.y);
        else if(dir.equals("south"))
                ret = new Position(player.x, player.y + 1);
        else if(dir.equals("west"))
                ret = new Position(player.x - 1, player.y);

        return ret;
    }

    /**
     * Player position
     * @return player position
     */
    public Position GetPlayerPosition()
    {
        return player;
    }
    
    /**
     * Set player position
     * @param x		x position
     * @param y		y position
     */
    public void SetPlayerPosition(int x, int y)
    {
        player.x = x;
        player.y = y;

    }

    /**
     * Observations received
     * @param o	 list of observations
     */
    public void GetObservations(List<String> o)
    {

    	int posX = this.player.x;
        int posY = this.player.y;        
        Tile[][] map = Maze.getInstance().getMap();
        List<Position> neighbours = GetObservableAdjacentPositions();
        String[] enemyDist;
        boolean markedAction = false;       
    	        
        for (String s : o)
        {
            if(s.equals("blocked")){
            	int nextX = NextPosition().x;
            	int nextY = NextPosition().y;
            	     
            	if(nextX > -1 && nextX < 59 && nextY > -1 && nextY < 34)
            		map[nextY][nextX].blockedStatus(); 
            	
            } else if(s.equals("steps")){            	
            	map[posY][posX].setVisited();
            	if(action.equals("hunt") && actionList.size() == 0)
            		this.action = "walk";
            	else{
            		if(huntCount == 0){
            			this.action = "hunt";
            			huntCount++;
            		}
            	}            		
            	markedAction = true;

            } else if(s.equals("breeze")){
            	map[posY][posX].setVisited();
            	for(int i = 0;i < neighbours.size();i++){
            		posX = neighbours.get(i).x;
            		posY = neighbours.get(i).y;
            		
            		map[posY][posX].addSupposition("breeze");
            	}

            } else if(s.equals("flash")){
            	map[posY][posX].setVisited();
            	for(int i = 0;i < neighbours.size();i++){
            		posX = neighbours.get(i).x;
            		posY = neighbours.get(i).y;
            		
            		map[posY][posX].addSupposition("flash");
            	}

            } else if(s.equals("redLight")){ 
            	this.action = "pickEnergy";
            	actionList.clear();
            	Maze.getInstance().addPowerupTile(map[posY][posX]);
            	map[posY][posX].setStatus("nothing"); 
            	map[posY][posX].setVisited();
            	huntCount = 0; 
            	markedAction = true;

            } else if(s.equals("blueLight")){ 
            	this.action = "pickGold";
            	actionList.clear();
            	map[posY][posX].setVisited();
            	markedAction = true;

            } else if(s.equals("greenLight")){            	

            } else if(s.equals("damage")){
            	if(!action.equalsIgnoreCase("escape"))
            		actionList.clear();
            	
            	this.action = "escape";
            	map[posY][posX].setVisited();
            	markedAction = true;
            	
            } else{
            	enemyDist = s.split("#");
            	if(enemyDist.length == 2 && enemyDist[0].equalsIgnoreCase("enemy")){
            		if(!action.equalsIgnoreCase(enemyDist[0]))
                		actionList.clear();
            		
            		if(energy > 60)
            			this.action = "attack";
            		else
            			this.action = "escape";
            		dist = Integer.parseInt(enemyDist[1]);
            		map[posY][posX].setVisited();
            		markedAction = true;
            	}
            }
            map[posY][posX].setVisited();
        }
        
        if(action.equalsIgnoreCase("walk")){
        	ArrayList<Tile> pwrUp = Maze.getInstance().getPossiblePowerup();
        	if(energy < 80 && pwrUp.size() > 0){
        		if(!action.equalsIgnoreCase("searchEnergy"))
            		actionList.clear();
        		
        		action = "searchEnergy";
        		markedAction = true;
        	}
        }
        else
        	if(!action.equalsIgnoreCase("hunt") &&
        		!action.equalsIgnoreCase("searchEnergy") &&
        		!action.equalsIgnoreCase("escape") &&
        		markedAction == false){
        		
        		if(!action.equalsIgnoreCase("walk"))
            		actionList.clear();
        		
        		action = "walk";
        	}
        if(action.equalsIgnoreCase("searchEnergy")){
        	if(actionList.isEmpty())
        		map[posY][posX].setStatus("nothing"); 
        }     
        
        
        
    }

    /**
     * No observations received
     */
    public void GetObservationsClean()
    {
    	int posX = this.player.x;
    	int posY = this.player.y;
    	Tile[][] map = Maze.getInstance().getMap();
    	List<Position> neighbours = GetObservableAdjacentPositions();
    	
    	map[posY][posX].setVisited();

    	for(int i = 0;i < neighbours.size();i++){
    		posX = neighbours.get(i).x;
    		posY = neighbours.get(i).y;

    		map[posY][posX].correctWrongStatus();
    	}

    	if(this.action.equalsIgnoreCase("attack") ||
    		this.action.equalsIgnoreCase("hunt")){
    		actionList.clear();
    		action = "walk";
    		dist = -1;
    	}
    }

    /**
     * Get Decision
     * @return command string to new decision
     */
    public String GetDecision()
    {       
    	
    	if(action.equalsIgnoreCase("escape")){
    		String res = escape();
    		return res;
    	}
    	else
    		if(action.equalsIgnoreCase("attack")){
    			return "atacar";
    		}
    		else
        		if(action.equalsIgnoreCase("walk")){
        			// Decidir se vai virar ou andar.
        			String res = walk();        			
        			return res;
        		}
        		else
            		if(action.equalsIgnoreCase("pickEnergy")){
            			action = "walk";
            			return "pegar_powerup";
            		}
            		else
                		if(action.equalsIgnoreCase("pickGold")){
                			action = "walk";
                			return "pegar_ouro";
                		}  
                		else
                    		if(action.equalsIgnoreCase("hunt")){                    			
                    			String res = hunt();
                    			return res;
                    		}  
                    		else
                        		if(action.equalsIgnoreCase("searchEnergy")){
                        			// Usar A* para energia
                        			String res = searchEnergy();
                        			return res;
                        		}          

    	return "atacar";
    }
    
    private String searchEnergy(){
    	
    	PathFinder pf = PathFinder.getInstance();
    	Node n;
    	
    	if(actionList.size() == 0){
    		if(pf.getEnergyPath().size() == 0){
    			boolean ret = pf.findEnergyPath(player);
    			if(ret == false){
    				System.out.println("Problema A* energia!");
    			} 
    		}

    		n = pf.getEnergyPath().get(0);
    		String turn = checkTurn(n);
    		if(turn.equalsIgnoreCase(dir)){
    			pf.previous = player;
    			return "andar";
    		}    			
    		else{
    			// Chamar função para verificar o giro
    			turnCount(turn);
    			actionList.add("andar");
    			String first = actionList.get(0);
    			actionList.remove(0);
    			return first;
    		}
    	}
    	else{
    		String first = actionList.get(0);
			actionList.remove(0);
			if(first.equals("andar"))
				pf.previous = player;
			
			return first;
    	}
    		
    }
    
    private String hunt(){
    	
    	if(actionList.size() == 0){
    		actionList.add("virar_direita");
    		actionList.add("virar_direita");    		
    		return "virar_direita";
    	}
    	else{
    		String ret = actionList.get(0);
    		actionList.remove(0);    		
    		return ret;
    	}
    	
    }
    
    private String walk(){
    	
    	PathFinder pf = PathFinder.getInstance();
    	String res;    	
    	
    	if(actionList.size() == 0){

    		res = pf.walkDecision(player,dir);
    		
    		if(res.equalsIgnoreCase("dir") || res == dir){
    			pf.previous = player;
    			return "andar";
    		}    		
    		else if(res.equalsIgnoreCase("north") && dir.equals("east")){
    			actionList.add("andar");
    			return "virar_direita";

    		} else if(res.equalsIgnoreCase("north") && dir.equals("west")){
    			actionList.add("andar");
    			return "virar_esquerda";

    		} else if(res.equalsIgnoreCase("north") && dir.equals("south")){
    			actionList.add("virar_direita");
    			actionList.add("andar");
    			return "virar_direita";

    		} else if(res.equalsIgnoreCase("south") && dir.equals("east")){
    			actionList.add("andar");
    			return "virar_esquerda";

    		} else if(res.equalsIgnoreCase("south") && dir.equals("west")){
    			actionList.add("andar");
    			return "virar_direita";

    		} else if(res.equalsIgnoreCase("south") && dir.equals("north")){
    			actionList.add("virar_direita");
    			actionList.add("andar");
    			return "virar_direita";

    		} else if(res.equalsIgnoreCase("east") && dir.equals("north")){
    			actionList.add("andar");
    			return "virar_esquerda";

    		} else if(res.equalsIgnoreCase("east") && dir.equals("south")){
    			actionList.add("andar");
    			return "virar_direita";

    		} else if(res.equalsIgnoreCase("east") && dir.equals("west")){
    			actionList.add("virar_esquerda");
    			actionList.add("andar");
    			return "virar_esquerda";

    		} else if(res.equalsIgnoreCase("west") && dir.equals("north")){
    			actionList.add("andar");
    			return "virar_direita";

    		} else if(res.equalsIgnoreCase("west") && dir.equals("south")){
    			actionList.add("andar");
    			return "virar_esquerda";

    		} else if(res.equalsIgnoreCase("west") && dir.equals("east")){
    			actionList.add("virar_esquerda");
    			actionList.add("andar");
    			return "virar_esquerda";

    		}
    	}
    	else{
    		String ret = actionList.get(0);
    		actionList.remove(0);   
    		if(ret.equalsIgnoreCase("andar"))
    			pf.previous = player;
    		return ret;
    	}
    		
    	return "";
    }
    
    private String escape(){
    	
    	PathFinder pf = PathFinder.getInstance(); 
    	
    	if(actionList.size() == 0){
    		
    		Position up = new Position();
    		Position right = new Position();
    		Position down = new Position();
    		Position left = new Position();
    		Position upLeft = new Position();
    		Position upRight = new Position();
    		Position downLeft = new Position();
    		Position downRight = new Position();

    		up.y = player.y - 1;
    		up.x = player.x;

    		down.y = player.y + 1;
    		down.x = player.x;

    		left.y = player.y;
    		left.x = player.x - 1;

    		right.y = player.y;
    		right.x = player.x + 1;

    		upLeft.y = player.y - 1;
    		upLeft.x = player.x - 1;

    		upRight.y = player.y - 1;
    		upRight.x = player.x + 1;

    		downLeft.y = player.y + 1;
    		downLeft.x = player.x - 1;

    		downRight.y = player.y + 1;
    		downRight.x = player.x + 1;

    		if(dir.equalsIgnoreCase("north")){
    			if(pf.escapeRoute(up,upRight)){
    				actionList.add("virar_direita");
    				actionList.add("andar");
    				pf.previous = player;
    				return "andar";

    			} else if(pf.escapeRoute(up,upLeft)){
    				actionList.add("virar_esquerda");
    				actionList.add("andar");
    				pf.previous = player;
    				return "andar";

    			} else if(pf.escapeRoute(down,downLeft)){
    				actionList.add("virar_esquerda");
    				actionList.add("andar");
    				pf.previous = player;
    				return "andar_re";

    			}
    			else if(pf.escapeRoute(down,downRight)){
    				actionList.add("virar_direita");
    				actionList.add("andar");
    				pf.previous = player;
    				return "andar_re";

    			}
    		} else if(dir.equalsIgnoreCase("south")){
    			if(pf.escapeRoute(down,downLeft)){
    				actionList.add("virar_direita");
    				actionList.add("andar");
    				pf.previous = player;
    				return "andar";

    			} else if(pf.escapeRoute(down,downRight)){
    				actionList.add("virar_esquerda");
    				actionList.add("andar");
    				pf.previous = player;
    				return "andar";

    			} else if(pf.escapeRoute(up,upRight)){
    				actionList.add("virar_esquerda");
    				actionList.add("andar");
    				pf.previous = player;
    				return "andar_re";

    			} else if(pf.escapeRoute(up,upLeft)){
    				actionList.add("virar_direita");
    				actionList.add("andar");
    				pf.previous = player;
    				return "andar_re";

    			}
    		} else if(dir.equalsIgnoreCase("east")){
    			if(pf.escapeRoute(right, downRight)){
    				actionList.add("virar_direita");
    				actionList.add("andar");
    				pf.previous = player;
    				return "andar";

    			} else if(pf.escapeRoute(right, upRight)){
    				actionList.add("virar_esquerda");
    				actionList.add("andar");
    				pf.previous = player;
    				return "andar";

    			} else if(pf.escapeRoute(left, upLeft)){
    				actionList.add("virar_esquerda");
    				actionList.add("andar");
    				pf.previous = player;
    				return "andar_re";

    			} else if(pf.escapeRoute(left, downLeft)){
    				actionList.add("virar_direita");
    				actionList.add("andar");
    				pf.previous = player;
    				return "andar_re";

    			}
    		} else if(dir.equalsIgnoreCase("west")){
    			if(pf.escapeRoute(left, upLeft)){
    				actionList.add("virar_direita");
    				actionList.add("andar");
    				pf.previous = player;
    				return "andar";

    			} else if(pf.escapeRoute(left, downLeft)){
    				actionList.add("virar_esquerda");
    				actionList.add("andar");
    				pf.previous = player;
    				return "andar";

    			} else if(pf.escapeRoute(right, downRight)){
    				actionList.add("virar_esquerda");
    				actionList.add("andar");
    				pf.previous = player;
    				return "andar";

    			} else if(pf.escapeRoute(right, upRight)){
    				actionList.add("virar_direita");
    				actionList.add("andar");
    				pf.previous = player;
    				return "andar";

    			}
    		}  
    	}
    	else{
    		String ret = actionList.get(0);
    		actionList.remove(0);    		
    		if(ret.equalsIgnoreCase("andar"))
    			pf.previous = player;
    		
    		return ret;
    	}
    	
    	return "andar";
    }
    
    private String checkTurn(Node n){    	
    	
    	int dif = 0;
    	
    	if(player.x == n.getX()){
    		dif = player.y - n.getY();
    		
    		if(dif > 0)
    			return "south";
    		else
    			return "north";
    	}
    	else{
    		dif = player.x - n.getX();
    		
    		if(dif > 0)
    			return "west";
    		else
    			return "east";
    	}
    }
    
    private void turnCount(String turn){
    	
    	if(turn.equals("north") && dir.equals("east")){
    		actionList.add("virar_direita");
    		
    	} else if(turn.equals("north") && dir.equals("west")){
    		actionList.add("virar_esquerda");
    		
    	} else if(turn.equals("south") && dir.equals("east")){
    		actionList.add("virar_esquerda");
    		
    	} else if(turn.equals("south") && dir.equals("west")){
    		actionList.add("virar_direita");
    		
    	} else if(turn.equals("east") && dir.equals("north")){
    		actionList.add("virar_esquerda");
    		
    	} else if(turn.equals("east") && dir.equals("south")){
    		actionList.add("virar_direita");
    		
    	} else if(turn.equals("west") && dir.equals("north")){
    		actionList.add("virar_direita");
    		
    	} else if(turn.equals("west") && dir.equals("south")){
    		actionList.add("virar_esquerda");
    		
    	} else if(turn.equals("north") && dir.equals("south")){
    		actionList.add("virar_direita");
    		actionList.add("virar_direita");
    		
    	} else if(turn.equals("south") && dir.equals("north")){
    		actionList.add("virar_direita");
    		actionList.add("virar_direita");
    		
    	} else if(turn.equals("east") && dir.equals("west")){
    		actionList.add("virar_esquerda");
    		actionList.add("virar_esquerda");
    		
    	} else if(turn.equals("west") && dir.equals("east")){
    		actionList.add("virar_esquerda");
    		actionList.add("virar_esquerda");
    	}    	
    	
    }
}
