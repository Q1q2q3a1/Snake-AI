import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import za.ac.wits.snake.DevelopmentAgent;

public class MyAgent extends DevelopmentAgent {

    public static void main(String args[]) {
        MyAgent agent = new MyAgent();
        MyAgent.start(agent, args);
    }

    @Override
    public void run() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            String initString = br.readLine();
            String[] temp = initString.split(" ");
            //Getting the number of snake
            int nSnakes = Integer.parseInt(temp[0]);

            //Creating a snakes array, it will carry the snakes, including the zombies
            List<String> snakes = new LinkedList<>();

            //snake to add to the snakes list
            String snake;

            //my snakes's head
            String sourceC = null;
            String sourceR = null;

            //random counter
            int p = 0;


            while (true) {
                snakes.clear();
                //making the play area to use and make the values '.' for places that can be moved into
                char playarea[][] = new char[Grid.rows][Grid.columns];
                for (int i = 0; i < playarea.length; i++) {
                    for (int j = 0; j < playarea[0].length; j++) {
                        playarea[i][j] = '.';
                    }
                }
                //incrementing the random counter
                p++;

                //Getting the first line
                String line = br.readLine();

                //if game is over
                if (line.contains("Game Over")) {
                    break;
                }
                //getting apple
                int appleC = Integer.parseInt(line.substring(0, line.indexOf(" ")));
                int appleR = Integer.parseInt(line.substring(line.indexOf(" ") + 1));

                //Create appple HOLDER
                Holder apple = new Holder(appleR, appleC);

                //do stuff with apples
                for (int zombie = 0; zombie < 6; zombie++) {
                    String zombieLine = br.readLine();
                    //Getting the individual zombie snakes to put into the snakes list
                    snakes.add(zombieLine);
                }
                //Getting my snake's number
                int mySnakeNum = Integer.parseInt(br.readLine());

                //adding the alive snakes into the snakes list
                for (int i = 0; i < nSnakes; i++) {
                    String snakeLine = br.readLine();
                    String[] getCoordinates = snakeLine.split(" ", 4);

                    //if the snake it dead add it to the list but as nothing
                    if (getCoordinates[0].equals("dead")) {
                        snake = "";
                    } else {
                        snake = getCoordinates[3];
                    }

                    //add snake to snakes list
                    snakes.add(snake);
                    if (i == mySnakeNum) {
                        //hey! That's me :)
                        //getting my head
                        String[] sourceNotReady = getCoordinates[3].split(" ");
                        String[] sourceReady = sourceNotReady[0].split(",");
                        sourceR = sourceReady[1];
                        sourceC = sourceReady[0];
                    }
                    //do stuff with other snakes
                }

                //finished reading, calculate move:
                //Drawing the snakes and creating a matrix with all snakes included
                for (int i = 0; i < snakes.size(); i++) {
                    boolean isZombie,isS = false;
                    //getting my snake and not giving it 3 more heads
                    if (mySnakeNum+6 == i){
                        isS=true;
                    }
                    else{
                        isS=false;
                    }
                    if (i < 6) {
                        isZombie = true;
                    } else {
                        isZombie = false;
                    }
                    if (snakes.get(i)!="") {
                        //drawing that snake on the matrix if it is not a dead snake
                        playarea = drawSnake(snakes.get(i), playarea, isZombie,isS);
                    }
                }

                //Getiing the source coordinates
                int sR = Integer.parseInt(sourceR);
                int sC = Integer.parseInt(sourceC);

                //setting the head of the snake and the apple in the playarea
                playarea[appleR][appleC] = 'A';
                playarea[sR][sC] = 'S';

                //initialising my move to make
                Holder target = new Holder(0, 0);

                //Creating my queue
                Queue<Holder> q = new LinkedList<Holder>();

                //apple not eaten yet
                boolean appleEaten = false;

                //possible moves
                int[] rm = {+1, 0, 0, -1};
                int[] cm = {0, +1, -1, 0};

                //initialising my snake head
                Holder head = new Holder(sR, sC);

                //initialising my visited grid
                Boolean[][] visited = new Boolean[Grid.rows][Grid.columns];
                for (int i = 0; i < Grid.rows; i++) {
                    for (int j = 0; j < Grid.columns; j++) {
                        visited[i][j] = false;
                    }
                }

                //adding the head into the queue
                q.add(head);

                //setting the visited of the head to true
                visited[head.r][head.c] = true;

                //Creating my removed from queue Holder
                Holder removed;

                //initialising my parent/previousHolder grid
                Holder[][] prev = new Holder[Grid.rows][Grid.columns];
                for (int i = 0; i < Grid.rows; i++) {
                    for (int j = 0; j < Grid.columns; j++) {
                        prev[i][j] = null;
                    }
                }

                //continue until my q is empty and the apple has not been eaten
                while (!q.isEmpty() && !visited[appleR][appleC]) {
                    removed = q.poll();

                    int newrm = 0;
                    int newcm = 0;
                    for (int i = 0; i < 4; i++) {
                        //new move
                        newrm = removed.r + rm[i];
                        newcm = removed.c + cm[i];

                        //skipping moves out of the grid
                        if (newrm < 0 || newcm < 0) {
                            continue;
                        }
                        if (newrm >= Grid.rows || newcm >= Grid.columns) {
                            continue;
                        }
                        //skipping moves that have been visited and those that are blocked
                        if (visited[newrm][newcm]) {
                            continue;
                        }
                        if (playarea[newrm][newcm] == 'O') {
                            continue;
                        }

                        //now we take the move since it is valid
                        Holder moveMade = new Holder(newrm, newcm);

                        //Add it to queue
                        q.add(moveMade);

                        //mark that coordinate as visited
                        visited[moveMade.r][moveMade.c] = true;

                        //Set its parent/prevHolder
                        prev[moveMade.r][moveMade.c] = removed;

                        //checking if holders are storing their parents/previous
                        //System.err.println(String.valueOf(moveMade.r) +","+ String.valueOf(moveMade.c)+" its prev: "+String.valueOf(removed.r) +","+ String.valueOf(removed.c));

                        //if that move was a apple eating move,good
                        if (visited[appleR][appleC]) {
                            appleEaten = true;
                            q.clear();

                            //break from directions loop
                            break;
                        }
                    }
                }
                //getting the next move depending on the first item in the queue
                int move = 0;

                if (appleEaten) {
                    //create path
                    List<Holder> path = new ArrayList<Holder>();
                    for (Holder at = apple; at != null; at = prev[at.r][at.c]) {
                        path.add(at);
                    }
                    //reverse that path
                    Collections.reverse(path);

                    //Setting the move to make
                    if (path.get(0).r < path.get(1).r) {
                        //go down
                        //System.out.println("log down");
                        move = 1;
                    } else if (path.get(0).r > path.get(1).r) {
                        //go up
                        //System.out.println("log up");
                        move = 0;
                    }
                    if (path.get(0).c < path.get(1).c) {
                        //go right
                        //System.out.println("log right");
                        move = 3;
                    } else if (path.get(0).c > path.get(1).c) {
                        //go left
                        //System.out.println("log left");
                        move = 2;
                    }
                    //clearing this path since it has been used already
                    path.clear();
                } else {
                    move =5;
                }
                //making move
                System.out.println(move);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static char[][] drawSnake(String snake, char[][] play, boolean isZombie, boolean isS) {
        //Splitting the snake into the corners to take
        //adding the 3 adjacent points to head as snake body on the grid , all snakes except my own
        if (snake != "" && isS==false) {
            //getting the head
            String head = snake.substring(0, snake.indexOf(" "));

            //creating the first two points to make the new line on the snake
            String point1 = String.valueOf((Integer.parseInt(head.substring(0, head.indexOf(","))) + 1) + "," + (Integer.parseInt(head.substring(head.indexOf(",") + 1))));
            String point2 = String.valueOf((Integer.parseInt(head.substring(0, head.indexOf(","))) - 1) + "," + (Integer.parseInt(head.substring(head.indexOf(",") + 1))));

            //adding that point to the snake
            snake = snake + " " + point1 + " " + point2;

            //creating the other two points to make the new line on the snake
            point1 = String.valueOf((Integer.parseInt(head.substring(0, head.indexOf(",")))) + "," + (Integer.parseInt(head.substring(head.indexOf(",") + 1)) + 1));
            point2 = String.valueOf((Integer.parseInt(head.substring(0, head.indexOf(",")))) + "," + (Integer.parseInt(head.substring(head.indexOf(",") + 1)) - 1));

            //adding that point to the snake
            snake = snake + " " + point1 + " " + point2;

            //output test
            //System.out.println("log " + snake);
        }

        //getting snake corners
        String[] snk = snake.split(" ");

        // Creating an array and storing the newly created lines in it
        char[][] ans = new char[play.length][play.length];
        for (int i = 0; i < snk.length - 1; i++) {
            //if the two coordinates have no same r or c coordiantes then skip it, its probably the newly added body parts
            if (!(snk[i].substring(0, snk[i].indexOf(",")).equals(snk[i + 1].substring(0, snk[i + 1].indexOf(","))) ||
                    snk[i].substring(snk[i].indexOf(",") + 1).equals(snk[i + 1].substring(snk[i + 1].indexOf(",") + 1)))) {
                continue;
            }

            //getting a grid with a line drawn on
            ans = drawLine(play, snk[i], snk[i + 1]);
        }
        //returning the array with the entire new snake
        return ans;
    }

    public static char[][] drawLine(char[][] playarea, String pone, String ptwo) {
        //Splitting the two points to be single digits
        String[] firstp = pone.split(",");
        String[] secp = ptwo.split(",");

        //Check with axis of the two point match and if they match we increment by the other axis.
        //Setting unmovable coordinates to 0
        if (Math.min(Integer.parseInt(firstp[0]), Integer.parseInt(secp[0])) == Math.max(Integer.parseInt(firstp[0]), Integer.parseInt(secp[0]))) {
            for (int i = Math.min(Integer.parseInt(firstp[1]), Integer.parseInt(secp[1])); i < Math.max(Integer.parseInt(firstp[1]), Integer.parseInt(secp[1])) + 1; i++) {
                //if the point is out of bounds move to next one
                if (Math.min(Integer.valueOf(firstp[0]), Integer.valueOf(secp[0])) >= Grid.columns || i >= Grid.columns || Math.min(Integer.valueOf(firstp[0]), Integer.valueOf(secp[0])) < 0 || i < 0) {
                    continue;
                }
                //set point to snake bodypart
                playarea[i][Math.min(Integer.parseInt(firstp[0]), Integer.parseInt(secp[0]))] = 'O';
            }
        }

        if (Math.min(Integer.parseInt(firstp[1]), Integer.parseInt(secp[1])) == Math.max(Integer.parseInt(firstp[1]), Integer.parseInt(secp[1]))) {
            for (int i = Math.min(Integer.parseInt(firstp[0]), Integer.parseInt(secp[0])); i < Math.max(Integer.parseInt(firstp[0]), Integer.parseInt(secp[0])) + 1; i++) {
                //if the point is out of bounds move to next one
                if (Math.min(Integer.valueOf(firstp[1]), Integer.valueOf(secp[1])) >= Grid.columns || i >= Grid.columns || Math.min(Integer.valueOf(firstp[1]), Integer.valueOf(secp[1])) < 0 || i < 0) {
                    continue;
                }
                //set point to snake bodypart
                playarea[Math.min(Integer.valueOf(firstp[1]), Integer.valueOf(secp[1]))][i] = 'O';
            }
        }
        //returning the new playarea
        return playarea;
    }

    public static void printBoard(char[][] board) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (j != board.length - 1) {
                    System.err.print(board[i][j] + " ");
                } else {
                    System.err.print(board[i][j]);
                }
            }
            System.err.println();
        }
    }

        class Grid {
            public static int rows = 50;
            public static int columns = 50;
        }
        class Holder {
            int r;
            int c;

            Holder(int row, int col) {
                //Initialising the Holder variables
                r = row;
                c = col;
            }
        }
    }

    /*
    private List<Holder> path(char[][] fmat, int sX,int sY ){      //formatted matrix
        Holder head = new Holder(sX,sY);
        Holder removed;
        Queue<Holder>  q = new LinkedList<Holder>();
        List<Holder> neighbours = null;

        q.add(head);
        while (!q.isEmpty()){
            removed = q.poll();
            //if where i am is the apple, then location changed is nothing
            if (fmat[removed.x][removed.y] == 'A'){
                neighbours.add(removed);
                return neighbours;
            }else {
                fmat[removed.x][removed.y] = '0';
                neighbours = addNeighbours(removed,fmat);
                q.addAll(neighbours);
            }
        }
        //return a list here
        return neighbours;
    }
    private List<Holder> addNeighbours(Holder removed, char[][] fmat){
        List<Holder> list = new LinkedList<Holder>();
        if ((removed.x - 1 >= 0 && removed.x - 1 < Grid.rows) && fmat[removed.x - 1][removed.y] != '0'){
            list.add(new Holder(removed.x - 1, removed.y));
        }
        if((removed.x + 1 >= 0 && removed.x+1 < Grid.rows) && fmat[removed.x + 1][removed.y] != '0') {
            list.add(new Holder(removed.x + 1, removed.y));
        }
        if((removed.y - 1 >= 0 && removed.y-1 < Grid.columns) && fmat[removed.x][removed.y-1] != '0') {
            list.add(new Holder(removed.x, removed.y - 1));
        }
        if((removed.y + 1 >= 0 && removed.y + 1 < Grid.columns) && fmat[removed.x][removed.y + 1] != '0') {
            list.add(new Holder(removed.x, removed.y + 1));
        }
        return list;
    }

     */

/*
                    if( p==1 || p==2 ){
                        //for(Holder coo:path) {
                          //  System.err.println(String.valueOf(coo.r) +","+ String.valueOf(coo.c) + " pathno:"+p);
                        //}
                        //System.err.println(snakes.get(6));
                        //printBoard(playarea);
                        //break;


                    }



                    //if(p==55){
                        //printBoard(playarea);
                        //break;
                    //}
                    //printBoard(playarea);

                     */



