import com.sun.deploy.util.StringUtils;

import java.util.*;

public class FOA_EfficientTrainTurnout
{

    public enum Direction
    {
        LEFT("L"), RIGHT("R"), FORWARD("F");

        private final String text;

        Direction(final String text)
        {
            this.text = text;
        }

        @Override
        public String toString()
        {
            return text;
        }
    }

    static String[][] data = new String[][]
            {
                    { "S", "-", "F", "-", "F", "-", "X", "*", "*"},
                    { "*", "*", "|", "*", "*", "*", "*", "*", "*"},
                    { "X", "-", "R", "-", "R", "-", "R", "-", "R"},
                    { "*", "*", "|", "*", "*", "*", "|", "*", "|"},
                    { "X", "-", "L", "-", "X", "*", "X", "*", "X"},
                    { "*", "*", "|", "*", "*", "*", "*", "*", "*"},
                    { "X", "-", "R", "-", "X", "*", "*", "*", "*"},
                    { "*", "*", "|", "*", "*", "*", "*", "*", "*"},
                    { "*", "*", "L", "-", "F", "-", "F", "-", "X"}
            };

    static int[] destination = new int[]{4,0};

    public static class outputNodeValue
    {
        int count = -1;
        String direction = "";

        outputNodeValue(int count, String direction)
        {
            this.count = count;
            this.direction = direction;
        }
    }


    static outputNodeValue[][] output = new outputNodeValue[9][9];

    public static void main(String[] args)
    {
        initializeOutputData();

        Queue<String> queue = new LinkedList<String>();
        queue.add("0.0");

        //Start from [0,0] which is the source and assign the count to 0.
        output[0][0] = new outputNodeValue(0,"S");
        String currentElement = null;
        String[] xyValue = null;
        outputNodeValue currentNode = null;
        List surroundingCoOrds = null;

        //Do until all the nodes in the matrix are processed and assigned a count value
        while (!queue.isEmpty())
        {
            currentElement = queue.remove();
            xyValue = StringUtils.splitString(currentElement, ".");
            int x = Integer.parseInt(xyValue[0]);
            int y = Integer.parseInt(xyValue[1]);

            currentNode = output[x][y];

            surroundingCoOrds = surroudingCoOrdinates(x,y);

            for (int m = 0; m < 4; m++)
            {
                int xcor = ((int[])surroundingCoOrds.get(0))[m];
                int ycor = ((int[])surroundingCoOrds.get(1))[m];

                if (xcor >= 0 && xcor < 9 && ycor >= 0 && ycor < 9)
                {
                    if (data[xcor][ycor] != "*")
                    {
                        outputNodeValue isValidNode = output[xcor][ycor];
                        if (isValidNode.count == -1)
                        {
                            output[xcor][ycor] = new outputNodeValue(currentNode.count + 1, data[xcor][ycor]);
                            queue.add(xcor + "." + ycor);
                        }
                    }
                    else
                    {
                        output[xcor][ycor] = new outputNodeValue(-1,data[xcor][ycor]);
                    }
                }
            }
        }

        System.out.println("Before trains are started.");

        for(int i =0 ; i < 9; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                System.out.print(data[i][j] + " ");
            }
            System.out.println();
        }

        System.out.println();

        //To check the output matrix with assigned count values.
/*        for(int q =0 ; q < 9; q++)
        {
            for (int r = 0; r < 9; r++)
            {
                outputNodeValue cur = output[q][r];
                if(null != cur)
                {
                    System.out.print(cur.count+cur.direction);
                    System.out.print("\t\t");
                }
            }
            System.out.println();
        }

*/

//        findPath(destination);
//        System.out.println("After train to destination: [" + destination[0] + "," + destination[1] +"]");
//        findPath(new int[]{0,6});
//        System.out.println("After First train to destination: [0,6]");
//        findPath(new int[]{8,8});
//        System.out.println("After First train to destination: [8,8]");

        findPath(new int[]{8,8});
        findPath(new int[]{4,4});
        findPath(new int[]{4,8});
        findPath(new int[]{4,0});
        findPath(new int[]{6,0});
        findPath(new int[]{8,0});
    }

    public static void initializeOutputData()
    {
        for(int i = 0; i < 9; i++)
        {
            Arrays.fill(output[i],new outputNodeValue(-1,"*"));
        }
    }

    public static void findPath(int dest[])
    {
        int xcurr = dest[0];
        int ycurr = dest[1];

        int xprev = dest[0];
        int yprev = dest[1];

        List path = new ArrayList();
        List<String> turn_outs = new ArrayList<String>();

        path.add(xcurr+"."+ycurr);

        outputNodeValue curCount = output[xcurr][ycurr];

        if("X" != curCount.direction)
        {
            System.out.println("Destination [" + dest[0] + "," + dest[1] + "], is not valid!");
            return;
        }

        List surroundingCoOrds = null;

        while(xcurr > 0 || ycurr > 0)
        {
            surroundingCoOrds = surroudingCoOrdinates(xcurr,ycurr);

            for(int i = 0; i < 4; i++)
            {
                int xDest = ((int[])surroundingCoOrds.get(0))[i];
                int yDest = ((int[])surroundingCoOrds.get(1))[i];

                if (xDest >= 0 && xDest < 9 && yDest >= 0 && yDest < 9)
                {
                    if (data[xDest][yDest] != "*")
                    {
                        outputNodeValue check = output[xDest][yDest];
                        if(check.count == curCount.count-1)
                        {
                            path.add(xDest+"."+yDest);
                            checkAndAddTurnOut(curCount.direction, turn_outs,new int[]{xprev,yprev},new int[]{xcurr,ycurr},new int[]{xDest,yDest});
                            xprev = xcurr;
                            yprev = ycurr;
                            xcurr = xDest;
                            ycurr = yDest;
                            curCount = output[xcurr][ycurr];
                            break;
                        }
                    }
                }
            }
        }

        Collections.reverse(path);
        Collections.reverse(turn_outs);

        System.out.println("For Destination [" + dest[0] + "," + dest[1] + "], Path calculated is: " + path);
        System.out.println("Total number of turn outs required: " + turn_outs.size());
        System.out.println("Turn outs are: " + turn_outs);

        for(int i =0 ; i < 9; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                outputNodeValue cur = output[i][j];
                if(null != cur)
                {
                    System.out.print(cur.direction);
                    System.out.print("\t");
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    public static void checkAndAddTurnOut(String direction, List<String> turn_outs, int[] xyPrev, int[] xyCurr, int[] xyDest)
    {
        if(direction != Direction.FORWARD.toString() && direction != Direction.RIGHT.toString() && direction != Direction.LEFT.toString())
            return;

        String required_direction = "";
        String present_direction = output[xyCurr[0]][xyCurr[1]].direction;

        if(xyPrev[0] == xyDest[0] || xyPrev[1] == xyDest[1])
        {
            required_direction = Direction.FORWARD.toString();
        }
        else if(xyDest[0] != xyPrev[0] && xyDest[1] != xyPrev[1])
        {
            if(xyCurr[0] == xyPrev[0])
            {
                if(xyDest[1] < xyPrev[1])
                {
                    if(xyDest[0] < xyPrev[0])
                    {
                        required_direction = Direction.LEFT.toString();
                    }
                    else if( xyPrev[0] < xyDest[0])
                    {
                        required_direction = Direction.RIGHT.toString();
                    }
                }
                else if(xyDest[1] > xyPrev[1])
                {
                    if(xyDest[0] < xyPrev[0])
                    {
                        required_direction = Direction.RIGHT.toString();
                    }
                    else if(xyPrev[0] < xyDest[0])
                    {
                        required_direction = Direction.LEFT.toString();
                    }
                }
            }
            else
            {
                if(xyDest[0] < xyPrev[0])
                {
                    if(xyDest[1] < xyPrev[1]) {
                        required_direction = Direction.RIGHT.toString();
                    }
                    else if( xyPrev[1] < xyDest[1])
                    {
                        required_direction = Direction.LEFT.toString();
                    }
                }
                else if(xyPrev[0] < xyDest[0])
                {
                    if(xyDest[1] < xyPrev[1])
                    {
                        required_direction = Direction.LEFT.toString();
                    }
                    else if(xyPrev[1] < xyDest[1])
                    {
                        required_direction = Direction.RIGHT.toString();
                    }
                }
            }
        }

        if(present_direction != required_direction)
        {
            turn_outs.add("At:["+xyCurr[0]+"," + xyCurr[1]+ "], "+ "from: "+ present_direction+"->"+required_direction);
            output[xyCurr[0]][xyCurr[1]].direction = required_direction;
        }
    }

    public static List surroudingCoOrdinates(int x, int y)
    {
        List list = new ArrayList();
        int[] xcoordinates = new int[]{x - 1, x + 1, x, x};
        int[] ycoordinates = new int[]{y, y, y - 1, y + 1};

        list.add(xcoordinates);
        list.add(ycoordinates);

        return list;
    }
}
